package dev.jaymin.sfgjms.listener;

import dev.jaymin.sfgjms.config.JMSConfig;
import dev.jaymin.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HelloMessageListener {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JMSConfig.MY_QUEUE)
    public void listen(@Payload HelloWorldMessage helloWorldMessage, @Headers MessageHeaders headers, Message message) {
        System.out.println("I got the message");
        System.out.println(helloWorldMessage);
    }
    @JmsListener(destination = JMSConfig.MY_SEND_REV_QUEUE)
    public void listenSendAndRec(@Payload HelloWorldMessage helloWorldMessage, @Headers MessageHeaders headers, Message message) throws JMSException {
        System.out.println(helloWorldMessage);
        HelloWorldMessage replayMessage = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("World!")
                .build();
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), replayMessage);
    }

}
