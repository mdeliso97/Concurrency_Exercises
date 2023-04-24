import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    static class Consumer extends Thread {
        int id;
        int N;
        int lastItem;
        static Semaphore mutex = new Semaphore(1); // mutual exclusion for accessing the LinkedList
        ConcurrentLinkedQueue<Integer> queue;
        AtomicIntegerArray consumerArray;

        public Consumer(int id, ConcurrentLinkedQueue<Integer> queue, AtomicIntegerArray consumerArray, int N) {
            this.id = id;
            this.queue = queue;
            this.N = N;
            this.consumerArray = consumerArray;
        }

        public void run() {
            while (consumerArray.get(id) < N) {
                try {
                    mutex.acquire();
                    if (queue.size() != 0) {
                        // lastItem = queue.remove(queue.size() - 1);
                        consumerArray.incrementAndGet(id);
                        if (consumerArray.get(id) % 100 == 0) {
                            System.out.println("Consumer " + id + " consumed element " + lastItem + " and consumed in total " + consumerArray.get(id));
                        }
                    }
                    mutex.release();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class Producer extends Thread {
        int id;
        int N;
        ConcurrentLinkedQueue<Integer> queue;
        AtomicIntegerArray producerArray;

        public Producer(int id, ConcurrentLinkedQueue<Integer> queue, AtomicIntegerArray producerArray,int N) {
            this.id = id;
            this.queue = queue;
            this.N = N;
            this.producerArray = producerArray;
        }

        public void run() {
            while (producerArray.get(id) < N) {
                queue.add(id);
                producerArray.incrementAndGet(id);
                if (producerArray.get(id) % 100 == 0) {
                    System.out.println("Producer " + id + " produced element " + id + " and produced in total " + producerArray.get(id));
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

        ConcurrentLinkedQueue<Integer> queue = Concurrent.initializeConcurrentLinkedQueue();

        AtomicIntegerArray consumerArray = Concurrent.consumerCounter(T);
        AtomicIntegerArray producerArray = Concurrent.producerCounter(T);

        Producer[] producer = new Producer[T];
        Consumer[] consumer = new Consumer[T];
        for (int i = 0; i < T; i++) {
            producer[i] = new Producer(i, queue, producerArray, N);
            consumer[i] = new Consumer(i, queue, consumerArray, N);
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
    }
}

