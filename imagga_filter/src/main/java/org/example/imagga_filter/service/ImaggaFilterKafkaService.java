package org.example.imagga_filter.service;

import com.google.gson.Gson;
import io.micrometer.core.instrument.MeterRegistry;
import io.minio.StatObjectResponse;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.filter_sdk.Filter;
import org.example.filter_sdk.FilterFinalMessage;
import org.example.filter_sdk.FilterMessage;
import org.example.filter_sdk.ImageRepository;
import org.example.filter_sdk.ProcessedImageId;
import org.example.imagga_filter.ImmagaClient;
import org.example.imagga_filter.data.model.ProcessedImage;
import org.example.imagga_filter.metric.RequestMetric;
import org.example.imagga_filter.repository.ProcessedImageInfoRepository;
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
public class ImaggaFilterKafkaService {

  private final KafkaTemplate<String, String> template;
  private final ProcessedImageInfoRepository processedImageInfo;
  private final ImageRepository imageRepository;
  private final ImmagaClient immagaClient;
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
  public ImaggaFilterKafkaService(
      KafkaTemplate<String, String> template,
      ProcessedImageInfoRepository processedImageInfo,
      ImageRepository imageRepository,
      ImmagaClient immagaClient,
      @Value("${kafka.wip-topic}") String wipTopic,
      @Value("${kafka.done-topic}") String doneTopic, MeterRegistry registry) {
    this.template = template;
    this.processedImageInfo = processedImageInfo;
    this.imageRepository = imageRepository;
    this.immagaClient = immagaClient;
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

    if (message.getFilters().get(0) != Filter.Tags) {
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
    var processedImage = addImageTags(image, metaInfo, shortContentType);

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

  private byte[] addImageTags(byte[] image, StatObjectResponse metaInfo, String format)
      throws IOException {
    var topTags = new String[3];
    var imageUrl = immagaClient.uploadImage(image, metaInfo);
    topTags = immagaClient.getTopTags(imageUrl);

    var inputStream = new ByteArrayInputStream(image);
    var bufferedImage = ImageIO.read(inputStream);
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();

    var taggedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    var graphics = taggedImage.createGraphics();
    graphics.drawImage(bufferedImage, 0, 0, null);

    graphics.setFont(new Font("Arial", Font.BOLD, 20));
    graphics.setColor(Color.GREEN);

    var period = height / 4;
    var y = height - period;
    var x = width < 10 ? 0 : 10;
    for (var tag : topTags) {
      graphics.drawString(tag, x, y);
      y -= period;
    }

    graphics.dispose();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ImageIO.write(taggedImage, format, outputStream);
    return outputStream.toByteArray();
  }
}
