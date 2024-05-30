package org.example.project.kafka;

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.filter_sdk.FilterFinalMessage;
import org.example.project.data.models.enums.FilterStatus;
import org.example.project.repositories.FilterImageInfoRepository;
import org.example.project.repositories.ImageRepository;
import org.example.project.repositories.MetaImageInfoRepository;
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
public class FilterKafkaService {

  private final KafkaTemplate<String, String> template;
  private final FilterImageInfoRepository filterImageInfoRepository;
  private final MetaImageInfoRepository metaImageInfoRepository;
  private final ImageRepository imageRepository;
  private final String wipTopic;
  private final Gson jsonConverter = new Gson();

  /**
   * Constructor.
   *
   * @param template                  kafka template
   * @param filterImageInfoRepository repository filter info
   * @param wipTopic                  kafka topic name for work with filters
   */
  public FilterKafkaService(KafkaTemplate<String, String> template,
      FilterImageInfoRepository filterImageInfoRepository,
      MetaImageInfoRepository metaImageInfoRepository,
      ImageRepository imageRepository,
      @Value("${kafka.wip-topic}") String wipTopic) {
    this.template = template;
    this.filterImageInfoRepository = filterImageInfoRepository;
    this.metaImageInfoRepository = metaImageInfoRepository;
    this.imageRepository = imageRepository;
    this.wipTopic = wipTopic;
  }

  /**
   * Create message for brokers on filter image.
   *
   * @param message kafka message on filter image
   */
  public void write(String message) {
    template.send(wipTopic, message);
  }

  /**
   * Commit finish filters work.
   *
   * @param record         message
   * @param acknowledgment consume commit
   */
  @KafkaListener(topics = "${kafka.done-topic}",
      groupId = "${kafka.group-id}",
      concurrency = "${kafka.partitions}",
      properties = {
          ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG + "=false",
          ConsumerConfig.ISOLATION_LEVEL_CONFIG + "=read_committed",
          ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG
              + "=org.apache.kafka.clients.consumer.RoundRobinAssignor",
      })
  public void read(@Payload ConsumerRecord<String, String> record,
      Acknowledgment acknowledgment) throws Exception {
    var result = jsonConverter.fromJson(record.value(), FilterFinalMessage.class);

    var filterImageInfo = filterImageInfoRepository
        .findById(result.getRequestId())
        .orElse(null);

    if (filterImageInfo != null) {
      var originalMeta = metaImageInfoRepository
          .findById(filterImageInfo.getOriginalImageId())
          .orElse(null);

      if (originalMeta == null) {
        filterImageInfo.setStatus(FilterStatus.ERROR);
        filterImageInfoRepository.save(filterImageInfo);
        acknowledgment.acknowledge();
        return;
      }

      var meta = imageRepository.getImageMeta(result.getImageId());

      originalMeta.setId(result.getImageId());
      originalMeta.setSize(meta.size());
      metaImageInfoRepository.save(originalMeta);

      filterImageInfo.setStatus(FilterStatus.DONE);
      filterImageInfo.setFilterImageId(result.getImageId());

      filterImageInfoRepository.save(filterImageInfo);
    }

    acknowledgment.acknowledge();
  }
}
