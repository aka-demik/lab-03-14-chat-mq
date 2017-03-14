package mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Publisher implements Runnable {

    @Override
    public void run() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
//                "tcp://10.240.17.94:61616");
                "tcp://localhost:61616");

        try {
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("chat");
            MessageProducer producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            TemporaryQueue ackQueue = session.createTemporaryQueue();
            session.createConsumer(ackQueue).setMessageListener(message -> {
                if (message != null && (message instanceof TextMessage)) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.err.println("ACK: " + textMessage.getText());
                    } catch (JMSException e) {
                        System.err.println("BAD ACK: " + e.getMessage());
                    }
                }
            });

            new BufferedReader(new InputStreamReader(System.in)).lines().forEach(s -> {

                try {
                    TextMessage message = session.createTextMessage(s);
                    message.setJMSReplyTo(ackQueue);
                    producer.send(message);
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });

            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
