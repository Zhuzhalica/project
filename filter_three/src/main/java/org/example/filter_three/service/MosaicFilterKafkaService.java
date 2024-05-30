package org.example.filter_three.service;

import com.google.gson.Gson;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.filter_sdk.Filter;
import org.example.filter_sdk.FilterFinalMessage;
import org.example.filter_sdk.FilterMessage;
import org.example.filter_sdk.ImageRepository;
import org.example.filter_sdk.ProcessedImageId;
import org.example.filter_three.data.model.ProcessedImage;
import org.example.filter_three.repository.ProcessedImageInfoRepository;
import org.example.filter_three.settings.FilterSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Service for kafka filter image.
 */
@Service
public class MosaicFilterKafkaService {

  private final KafkaTemplate<String, String> template;
  private final ProcessedImageInfoRepository processedImageInfo;
  private final ImageRepository imageRepository;
  private final FilterSettings settings;
  private final String wipTopic;
  private final String doneTopic;
  private final Gson jsonConverter = new Gson();

  /**
   * Constructor.
   *
   * @param template           kafka template
   * @param processedImageInfo repository processed image info
   * @param wipTopic           kafka wipTopic name for work with filters
   */
  public MosaicFilterKafkaService(
      KafkaTemplate<String, String> template,
      ProcessedImageInfoRepository processedImageInfo,
      ImageRepository imageRepository, FilterSettings settings,
      @Value("${kafka.wip-topic}") String wipTopic,
      @Value("${kafka.done-topic}") String doneTopic) {
    this.template = template;
    this.processedImageInfo = processedImageInfo;
    this.imageRepository = imageRepository;
    this.settings = settings;
    this.wipTopic = wipTopic;
    this.doneTopic = doneTopic;
  }

  /**
   * Process image with filter.
   *
   * @param record         message
   * @param acknowledgment consume commit
   */
  @KafkaListener(
      topics = "${kafka.wip-topic}",
      groupId = "${kafka.group-id}",
      concurrency = "${kafka.wip-partitions}",
      properties = {
          ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG + "=false",
          ConsumerConfig.ISOLATION_LEVEL_CONFIG + "=read_committed",
          ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG
              + "=org.apache.kafka.clients.consumer.RoundRobinAssignor"})
  public void read(@Payload ConsumerRecord<String, String> record, Acknowledgment acknowledgment)
      throws Exception {
    var message = jsonConverter.fromJson(record.value(), FilterMessage.class);

    if (message.getFilters().get(0) != Filter.Mosaic) {
      acknowledgment.acknowledge();
      return;
    }

    var processedInfo = processedImageInfo
        .findById(new ProcessedImageId(message.getImageId(), message.getRequestId()))
        .orElse(null);

    if (processedInfo != null) {
      acknowledgment.acknowledge();
      return;
    }

    var isLastFilter = message.getFilters().size() == 1;

    var image = imageRepository.downloadImage(message.getImageId());
    var metaInfo = imageRepository.getImageMeta(message.getImageId());
    var shortContentType = metaInfo.contentType().split("/")[1];
    var processedImage = mosaicImage(image, shortContentType);

    var newImageId = imageRepository.loadImage(processedImage, metaInfo, !isLastFilter);
    processedImageInfo.save(new ProcessedImage(message.getImageId(), message.getRequestId()));

    if (isLastFilter) {
      var doneMessage = new FilterFinalMessage(newImageId, message.getRequestId());
      template.send(doneTopic, jsonConverter.toJson(doneMessage));
    } else {
      message.setImageId(newImageId);
      message.getFilters().remove(0);
      template.send(wipTopic, jsonConverter.toJson(message));
    }

    acknowledgment.acknowledge();
  }

  private byte[] mosaicImage(byte[] image, String format) throws IOException, InterruptedException {
    var inputStream = new ByteArrayInputStream(image);
    var bufferedImage = ImageIO.read(inputStream);
    var width = bufferedImage.getWidth();
    var height = bufferedImage.getHeight();
    var blockSize = settings.getBlockSize();
    var mosaicImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    var numThreads = Runtime.getRuntime().availableProcessors();
    var executor = Executors.newFixedThreadPool(numThreads);

    for (var y = 0; y < height; y += blockSize) {
      int finalY = y;
      executor.submit(() -> {
        for (var x = 0; x < width; x += blockSize) {
          int r = 0, g = 0, b = 0, count = 0;

          for (var ky = 0; ky < blockSize && finalY + ky < height; ky++) {
            for (var kx = 0; kx < blockSize && x + kx < width; kx++) {
              var color = new Color(bufferedImage.getRGB(x + kx, finalY + ky));
              r += color.getRed();
              g += color.getGreen();
              b += color.getBlue();
              count++;
            }
          }

          r /= count;
          g /= count;
          b /= count;
          var averageColor = new Color(r, g, b);

          for (var ky = 0; ky < blockSize && finalY + ky < height; ky++) {
            for (var kx = 0; kx < blockSize && x + kx < width; kx++) {
              mosaicImage.setRGB(x + kx, finalY + ky, averageColor.getRGB());
            }
          }
        }
      });
    }

    executor.shutdown();
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(mosaicImage, format, outputStream);
    return outputStream.toByteArray();
  }
}
