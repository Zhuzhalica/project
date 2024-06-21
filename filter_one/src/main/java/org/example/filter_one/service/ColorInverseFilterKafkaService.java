package org.example.filter_one.service;

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
import org.example.filter_one.data.model.ProcessedImage;
import org.example.filter_one.metric.RequestMetric;
import org.example.filter_one.repository.ProcessedImageInfoRepository;
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
public class ColorInverseFilterKafkaService {

  private final KafkaTemplate<String, String> template;
  private final ProcessedImageInfoRepository processedImageInfo;
  private final ImageRepository imageRepository;
  private final MeterRegistry registry;
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
  public ColorInverseFilterKafkaService(KafkaTemplate<String, String> template,
      ProcessedImageInfoRepository processedImageInfo, ImageRepository imageRepository,
      MeterRegistry registry, @Value("${kafka.wip-topic}") String wipTopic,
      @Value("${kafka.done-topic}") String doneTopic) {
    this.template = template;
    this.processedImageInfo = processedImageInfo;
    this.imageRepository = imageRepository;
    this.registry = registry;
    this.wipTopic = wipTopic;
    this.doneTopic = doneTopic;
  }

  /**
   * Process image with filter.
   *
   * @param record         message
   * @param acknowledgment consume commit
   */
  @KafkaListener(topics = "${kafka.wip-topic}", groupId = "${kafka.group-id}", concurrency = "${kafka.wip-partitions}", properties = {
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
    if (message.getFilters().get(0) != Filter.ColorInverse) {
      acknowledgment.acknowledge();
      return;
    }

    var processedInfo = processedImageInfo.findById(
        new ProcessedImageId(message.getImageId(), message.getRequestId())).orElse(null);

    if (processedInfo != null) {
      acknowledgment.acknowledge();
      return;
    }

    var isLastFilter = message.getFilters().size() == 1;

    var image = imageRepository.downloadImage(message.getImageId());
    var metaInfo = imageRepository.getImageMeta(message.getImageId());
    var shortContentType = metaInfo.contentType().split("/")[1];
    var processedImage = ColorInverseImage(image, shortContentType);

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

  private byte[] ColorInverseImage(byte[] image, String format)
      throws IOException, InterruptedException {
    var inputStream = new ByteArrayInputStream(image);
    var bufferedImage = ImageIO.read(inputStream);
    var width = bufferedImage.getWidth();
    var height = bufferedImage.getHeight();
    BufferedImage invertedImage = new BufferedImage(bufferedImage.getWidth(),
        bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);

    var numThreads = Runtime.getRuntime().availableProcessors();
    var executor = Executors.newFixedThreadPool(numThreads);

    for (int y = 0; y < height; y++) {
      int finalY = y;
      executor.submit(() -> {
        for (int x = 0; x < width; x++) {
          var color = new Color(bufferedImage.getRGB(x, finalY));
          var red = 255 - color.getRed();
          var green = 255 - color.getGreen();
          var blue = 255 - color.getBlue();
          var invertedColor = new Color(red, green, blue);
          invertedImage.setRGB(x, finalY, invertedColor.getRGB());
        }
      });
    }

    executor.shutdown();
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(invertedImage, format, outputStream);
    return outputStream.toByteArray();
  }
}
