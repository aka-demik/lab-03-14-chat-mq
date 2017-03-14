package mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Consumer implements Runnable {

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


            MessageConsumer consumer = session.createConsumer(topic);
            while (true) {
                Message message = consumer.receive();
                if (message != null && (message instanceof TextMessage)) {
                    Destination destination;
                    TextMessage textMessage = (TextMessage) message;

                    if ((destination = message.getJMSReplyTo()) != null) {
                        session.createProducer(destination).send(message);
                    }
                    if ("exit".equals(textMessage.getText()))
                        break;
                    System.out.println((textMessage).getText());
                }
            }
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
