import mq.Consumer;
import mq.Publisher;

public class Main {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new Consumer());
        thread1.start();
        Thread thread2 = new Thread(new Publisher());
        thread2.start();
    }
}
