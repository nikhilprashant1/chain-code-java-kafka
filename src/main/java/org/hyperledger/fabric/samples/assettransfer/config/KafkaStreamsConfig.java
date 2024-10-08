package org.hyperledger.fabric.samples.assettransfer.config;



import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
@Slf4j
public class KafkaStreamsConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.streams.application-id}")
    private String applicationId;
    @Value(value = "${spring.kafka.streams.default.key.serde}")
    private String keySerde;
    @Value(value = "${spring.kafka.streams.default.value.serde}")
    private String valueSerde;
    @Value("${application.topic.asset.queue}")
    private String assetTopic;
    @Value("${application.topic.central-kafka-stream.queue}")
    private String centralKafkaTopic;


    @Bean(name = "defaultKafkaStreamsConfig")
    public KafkaStreamsConfiguration kafkaStreamsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapAddress);
        config.put("application.id", applicationId);
        config.put("default.key.serde", keySerde);
        config.put("default.value.serde", valueSerde);
        return new KafkaStreamsConfiguration(config);
    }

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder, KafkaTemplate<String, String> kafkaTemplate) {
        KStream<String, String> stream = streamsBuilder.stream(assetTopic);

        stream.foreach((key, value) -> {
            log.info("Processing message: Key = " + key + ", Value = " + value);
            kafkaTemplate.send(centralKafkaTopic, key, "Processed: " + value);
        });

        return stream;
    }
}