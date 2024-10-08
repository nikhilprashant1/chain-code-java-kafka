package org.hyperledger.fabric.samples.assettransfer.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AssetsKafkaPublisher {
    @Value("${application.topic.asset.queue}")
    private String assetTopic;

    @Autowired
    private KafkaTemplate<String, String> assetsKafkaTemplate;


    public void publish( String message)  {
        log.info("Sending message to topic : " + assetTopic +" Data is " + message);
        assetsKafkaTemplate.send(assetTopic, message)
                .addCallback(
                        result -> log.info("Message sent successfully to topic: {} with offset: {}", assetTopic, result.getRecordMetadata().offset()),
                        ex -> log.error("Failed to send message to topic: {} due to: {}", assetTopic, ex.getMessage())
                );
    }
}