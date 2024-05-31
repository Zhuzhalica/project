package org.example.imagga_filter.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Bean components for apache kafka.
 */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@EnableKafka
@RequiredArgsConstructor
public class KafkaInitializer {

  private final KafkaProperties properties;

  /**
   * Create kafka topics.
   *
   * @param doneTopic  finish filter topic name
   * @param partitions kafka partitions
   * @param replicas   kafka replicas
   * @return created topics
   */
  @Bean
  public NewTopics topic(
      @Value("${kafka.wip-topic}") String wipTopic,
      @Value("${kafka.done-topic}") String doneTopic,
      @Value("${kafka.wip-partitions}") int wipPartitions,
      @Value("${kafka.done-partitions}") int donePartitions,
      @Value("${kafka.replicas}") short replicas) {
    return new NewTopics(new NewTopic(wipTopic, wipPartitions, replicas),
        new NewTopic(doneTopic, donePartitions, replicas));
  }

  /**
   * Create kafak template.
   *
   * @return kafka template
   */
  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    var props = properties.buildProducerProperties(null);

    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class);
    props.put(ProducerConfig.LINGER_MS_CONFIG, 0);

    props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
    props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 30000);

    props.put(ProducerConfig.ACKS_CONFIG, "all");

    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
  }
}
