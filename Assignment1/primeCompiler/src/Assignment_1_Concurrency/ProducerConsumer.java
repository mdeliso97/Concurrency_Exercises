import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer {

    // initialize environment objects and variables
    private final int BUFFER_SIZE;
    private final Lock lock = new ReentrantLock();

    // conditions are helpful to put constraints on locks which are only given if conditions are met
    private final Condition prodCond = lock.newCondition();
    private final Condition consCond = lock.newCondition();
    private final int[] buffer;
    private int counter = 0;
    private int prodIndex = 0;
    private int consIndex = 0;
    // a semaphore used to signal when all threads are done
    public static final Semaphore finished = new Semaphore(0);

    public ProducerConsumer(int N) {
        BUFFER_SIZE = N;
        buffer = new int[BUFFER_SIZE];
    }
    // implements producer, if the buffer is not full, the thread will produce one element with his id
    public void Produce(int id) throws InterruptedException {
        lock.lock();
        try {
            while (counter == BUFFER_SIZE) {
                prodCond.await();
            }
            buffer[prodIndex] = id;
            prodIndex = (prodIndex + 1) % BUFFER_SIZE;
            counter++;
            System.out.println("Producer " + id + " has produced element with id " + id);
            consCond.signalAll();
        } finally {
            lock.unlock();
        }
    }
    // implements consumer, if the buffer is not empty, the thread will consume one and reduce the counter variable
    public void Consume(int id) throws InterruptedException {
        lock.lock();
        try {
            while (counter == 0) {
                consCond.await();
            }
            int item = buffer[consIndex];
            consIndex = (consIndex + 1) % BUFFER_SIZE;
            counter--;
            System.out.println("Consumer " + id + " consumed element with id " + item);
            prodCond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        //check that inputs are 2
        if (args.length != 2) {
            System.out.println("The arguments provided are not 2!");
            return;
        }

        //parse input arguments
        int T = Integer.parseInt(args[0]);
        int N = Integer.parseInt(args[1]);

        //check that inputs are positive
        if (T <= 0 || N <= 0) {
            System.out.println("The integers you entered are not positive, please try again");
            return;
        }

        //initialize threads
        Thread[] threads = new Thread[2*T];

        ProducerConsumer pc = new ProducerConsumer(N);

        //prints Environment
        System.out.println("The size of the bounded circular buffer is " + N + " with " + T + " Producer and " + T + " Consumer Threads:");

        //initialize threads as producers
        for (int i = 0; i < T; i++) {
            final int id = i;
            threads[i] = new Thread(new Thread(() -> {
                try {
                    while (true) {
                        pc.Produce(id);
                        Thread.sleep((long) (Math.random() * 1000));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
            threads[i].start();
        }

        //initialize threads as consumers
        for (int i = 0; i < T; i++) {
            final int id = i;
            threads[i] = new Thread(new Thread(() -> {
                try {
                    while (true) {
                        pc.Consume(id);
                        Thread.sleep((long) (Math.random() * 1000));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
            threads[i].start();
        }

        //wait for all threads to finish through Semaphore
        try {
            ProducerConsumer.finished.acquire(T);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for threads to finish");
            System.exit(1);
        }
    }


}
