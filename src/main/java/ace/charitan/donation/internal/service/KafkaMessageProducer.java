package ace.charitan.donation.internal.service;

import ace.charitan.common.dto.TestKafkaMessageDto;
import ace.charitan.common.dto.donation.SendDonationNotificationDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

import static org.springframework.kafka.support.KafkaHeaders.REPLY_TOPIC;

@Component
class KafkaMessageProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;


    public TestKafkaMessageDto testRequestResponse() throws ExecutionException, InterruptedException {
        TestKafkaMessageDto dto = new TestKafkaMessageDto("John Request", "Requesting a response from notification");
        ProducerRecord<String, Object> record = new ProducerRecord<>("john-request", dto);
        record.headers().add(REPLY_TOPIC, REPLY_TOPIC.getBytes());
        RequestReplyFuture<String, Object, Object> future = replyingKafkaTemplate.sendAndReceive(record);

        Object response = future.get().value();

        System.out.println("Object" + response);

        return (TestKafkaMessageDto) response ;
    }

    public void sendDonationNotification(InternalDonationDto dto) {
        SendDonationNotificationDto newDto = new SendDonationNotificationDto(1L, 10.00, "abc", "xyz", "bucky", LocalDate.now());
        kafkaTemplate.send("donation-notification",newDto);
        kafkaTemplate.send("donation-email", newDto);
    }

}