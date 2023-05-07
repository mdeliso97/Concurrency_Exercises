import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Concurrent.java implements the consumer-producer problem with a ConcurrentLinkedQueue instead of a LinkedList. Works
 * just as the previous one but a bit slower. To check the correctness, it is possible to remove comments on the print
 * statement which I commented so that it does not affect runtime.
 * <p>
 * Elapsed time: 1293 milliseconds
 */

public class Concurrent {

    public static ConcurrentLinkedQueue<Integer> initializeConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<>();
    }
    public static AtomicIntegerArray consumerCounter(int T) {
        return new AtomicIntegerArray(T);
    }
    public static AtomicIntegerArray producerCounter(int T) {
        return new AtomicIntegerArray(T);
    }

    static Semaphore mutex = new Semaphore(1); // grants mutual exclusion for accessing the LinkedList
    static class Consumer extends Thread {
        int id;
        int N;
        int lastItem;
        ConcurrentLinkedQueue<Integer> list;
        AtomicIntegerArray consumerArray;

        public Consumer(int id, ConcurrentLinkedQueue<Integer> list, AtomicIntegerArray consumerArray, int N) {
            this.id = id;
            this.list = list;
            this.N = N;
            this.consumerArray = consumerArray;
        }

        public void run() {
            while (consumerArray.get(id) < N) {
                try {
                    mutex.acquire();
                    if (!list.isEmpty()) {
                        lastItem = list.remove();
                        consumerArray.incrementAndGet(id);
                        // System.out.println("Consumer " + id + " consumed element " + lastItem + " and consumed in total " + consumerArray.get(id));
                    }
                    mutex.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class Producer extends Thread {
        int id;
        int N;
        ConcurrentLinkedQueue<Integer> list;
        AtomicIntegerArray producerArray;

        public Producer(int id, ConcurrentLinkedQueue<Integer> list, AtomicIntegerArray producerArray,int N) {
            this.id = id;
            this.list = list;
            this.N = N;
            this.producerArray = producerArray;
        }

        public void run() {
            while (producerArray.get(id) < N) {
                try {
                    mutex.acquire();
                    list.add(id);
                    producerArray.incrementAndGet(id);
                    // System.out.println("Producer " + id + " produced element " + id + " and produced in total " + producerArray.get(id));
                    mutex.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
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

        ConcurrentLinkedQueue<Integer> list = Concurrent.initializeConcurrentLinkedQueue();

        AtomicIntegerArray consumerArray = Concurrent.consumerCounter(T);
        AtomicIntegerArray producerArray = Concurrent.producerCounter(T);

        Producer[] producer = new Producer[T];
        Consumer[] consumer = new Consumer[T];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < T; i++) {
            producer[i] = new Producer(i, list, producerArray, N);
            consumer[i] = new Consumer(i, list, consumerArray, N);
            producer[i].start();
            consumer[i].start();
        }

        // Wait for threads completion
        for (int i = 0; i < T; i++) {
            try {
                consumer[i].join();
                producer[i].join();
            } catch (InterruptedException e) {
            }
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
    }
}

