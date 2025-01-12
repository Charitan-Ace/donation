package ace.charitan.donation.internal.service;

import ace.charitan.common.dto.TestKafkaMessageDto;
import ace.charitan.common.dto.donation.SendDonationNotificationDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdRequestDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdResponseDto;
import ace.charitan.donation.internal.dto.InternalDonationDto;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.springframework.kafka.support.KafkaHeaders.REPLY_TOPIC;

@Component
class KafkaMessageProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    private Object sendAndReceive(
            DonationProducerTopic topic, Serializable data) {
        try {

            ProducerRecord<String, Object> record = new ProducerRecord<>(topic.getTopic(), data);
            RequestReplyFuture<String, Object, Object> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
            // SendResult<String, Object> sendResult = replyFuture.getSendFuture().get(10,
            // TimeUnit.SECONDS);
            ConsumerRecord<String, Object> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
            return consumerRecord.value();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public TestKafkaMessageDto testRequestResponse() throws ExecutionException, InterruptedException {
        TestKafkaMessageDto dto = new TestKafkaMessageDto("John Request", "Requesting a response from notification");
        ProducerRecord<String, Object> record = new ProducerRecord<>("john-request", dto);
        record.headers().add(REPLY_TOPIC, REPLY_TOPIC.getBytes());
        RequestReplyFuture<String, Object, Object> future = replyingKafkaTemplate.sendAndReceive(record);

        Object response = future.get().value();

        System.out.println("Object" + response);

        return (TestKafkaMessageDto) response;
    }

    public void sendDonationNotification(InternalDonationDto dto) {
        SendDonationNotificationDto newDto = new SendDonationNotificationDto(dto.getId(), dto.getAmount(),
                dto.getMessage(), "skibidi", "toilet", LocalDate.now());
        kafkaTemplate.send("donation-notification", newDto);
        kafkaTemplate.send("donation-email", newDto);
    }

    GetProjectByCharityIdResponseDto sendAndReceive(GetProjectByCharityIdRequestDto requestDto) {
        GetProjectByCharityIdResponseDto responseDto = (GetProjectByCharityIdResponseDto) sendAndReceive(
                DonationProducerTopic.PROJECT_GET_ALL_PROJECTS_BY_CHARITY_ID, requestDto);
        System.out.println(responseDto);
        return responseDto;
    }

}