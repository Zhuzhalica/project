package org.example.filter_four.service;

import com.google.gson.Gson;
import io.micrometer.core.instrument.MeterRegistry;
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
import org.example.filter_four.data.model.ProcessedImage;
import org.example.filter_four.metric.RequestMetric;
import org.example.filter_four.repository.ProcessedImageInfoRepository;
import org.example.filter_sdk.Filter;
import org.example.filter_sdk.FilterFinalMessage;
import org.example.filter_sdk.FilterMessage;
import org.example.filter_sdk.ImageRepository;
import org.example.filter_sdk.ProcessedImageId;
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
public class OldPhotoFilterKafkaService {

  private final KafkaTemplate<String, String> template;
  private final ProcessedImageInfoRepository processedImageInfo;
  private final ImageRepository imageRepository;
  private final String wipTopic;
  private final String doneTopic;
  private final Gson jsonConverter = new Gson();
  private final MeterRegistry registry;

  /**
   * Constructor.
   *
   * @param template           kafka template
   * @param processedImageInfo repository processed image info
   * @param wipTopic           kafka wipTopic name for work with filters
   */
  public OldPhotoFilterKafkaService(
      KafkaTemplate<String, String> template,
      ProcessedImageInfoRepository processedImageInfo,
      ImageRepository imageRepository,
      @Value("${kafka.wip-topic}") String wipTopic,
      @Value("${kafka.done-topic}") String doneTopic, MeterRegistry registry) {
    this.template = template;
    this.processedImageInfo = processedImageInfo;
    this.imageRepository = imageRepository;
    this.wipTopic = wipTopic;
    this.doneTopic = doneTopic;
    this.registry = registry;
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
  public void read(@Payload ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
    var requestMetric = new RequestMetric(registry);
    try {
      requestMetric.beforeAction();
      ProcessRequest(record, acknowledgment);
      requestMetric.afterAction();
    } catch (Exception e) {
      requestMetric.excpetion();
    }

  }

  private void ProcessRequest(ConsumerRecord<String, String> record, Acknowledgment acknowledgment)
      throws Exception {
    var message = jsonConverter.fromJson(record.value(), FilterMessage.class);

    if (message.getFilters().get(0) != Filter.OldPhoto) {
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
    var processedImage = oldPhotoImage(image, shortContentType);

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

  private byte[] oldPhotoImage(byte[] image, String format)
      throws IOException, InterruptedException {
    var inputStream = new ByteArrayInputStream(image);
    var bufferedImage = ImageIO.read(inputStream);
    var width = bufferedImage.getWidth();
    var height = bufferedImage.getHeight();
    var oldPhotoImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    var numThreads = Runtime.getRuntime().availableProcessors();
    var executor = Executors.newFixedThreadPool(numThreads);

    for (int y = 0; y < height; y++) {
      var finalY = y;
      executor.submit(() -> {
        for (int x = 0; x < width; x++) {
          var color = new Color(bufferedImage.getRGB(x, finalY));

          var red = (int) (0.393 * color.getRed() + 0.769 * color.getGreen()
              + 0.189 * color.getBlue());
          var green = (int) (0.349 * color.getRed() + 0.686 * color.getGreen()
              + 0.168 * color.getBlue());
          var blue = (int) (0.272 * color.getRed() + 0.534 * color.getGreen()
              + 0.131 * color.getBlue());

          red = Math.min(255, red);
          green = Math.min(255, green);
          blue = Math.min(255, blue);

          oldPhotoImage.setRGB(x, finalY, new Color(red, green, blue).getRGB());
        }
      });
    }

    executor.shutdown();
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(oldPhotoImage, format, outputStream);
    return outputStream.toByteArray();
  }
}
