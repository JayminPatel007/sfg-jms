package dev.jaymin.sfgjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jaymin.sfgjms.config.JMSConfig;
import dev.jaymin.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        HelloWorldMessage message = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();

        jmsTemplate.convertAndSend(JMSConfig.MY_QUEUE, message);
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {
        HelloWorldMessage message = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Hello")
                .build();

        Message receivedMessage = jmsTemplate.sendAndReceive(JMSConfig.MY_SEND_REV_QUEUE, session -> {
            Message helloMessage = null;
            try {
                helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                helloMessage.setStringProperty("_type", "dev.jaymin.sfgjms.model.HelloWorldMessage");
                log.info("Sending hello ....");
                return helloMessage;
            } catch (JsonProcessingException e) {
                throw new JMSException(e.getMessage());
            }
        });

        System.out.println(receivedMessage.getBody(String.class));
    }

}
