package ace.charitan.donation.internal.service;

import ace.charitan.common.dto.TestKafkaMessageDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import static org.springframework.kafka.support.KafkaHeaders.REPLY_TOPIC;

@Component
class KafkaMessageProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;


//    public void sendMessage(String topic, String message) {
//        System.out.println("Project microservice sent message: " + message);
//        kafkaTemplate.send(topic, message);
//    }

    public void sendDonationNotification(Donation donation) {
        TestKafkaMessageDto dto = new TestKafkaMessageDto(donation.getFirstName(), donation.getMessage());
        kafkaTemplate.send("donation-notification", dto);
    }

    public TestKafkaMessageDto testRequestResponse() throws ExecutionException, InterruptedException {
        TestKafkaMessageDto dto = new TestKafkaMessageDto("John Request", "Requesting a response from notification");
        ProducerRecord<String, Object> record = new ProducerRecord<>("john-request", dto);
        record.headers().add(REPLY_TOPIC, REPLY_TOPIC.getBytes());
        RequestReplyFuture<String, Object, Object> future = replyingKafkaTemplate.sendAndReceive(record);

        Object response = future.get().value();

        System.out.println("Object" + response);

        return (TestKafkaMessageDto) response ;
    }

}