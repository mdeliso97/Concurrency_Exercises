import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.CountDownLatch;

/**
 * CyclicBarrierClass.java provides an implementation of the consumer-producer problem. It uses a very similar mechanism
 * as for the previous implementations, but instead uses CyclicBarrier for producers and CountDownLatch for consumers.
 * From the runtime measured, it is pretty clear that this implementation is the slowest, also clear is the motivation
 * for it, since for both structures, all threads need to wait that all other threads joined the barrier or latch before
 * being able to continue, leading to massive waiting time. But as saw in the lecture, this kind of structures can be very
 * useful in certain cases where the outcome of a calculation is dependent to another or even for granting Fairness,
 * which here is respected. As for the previous cases, to check the correctness, it is possible to remove comments on
 * the print statement which I commented so that it does not affect runtime.
 * <p>
 * Elapsed time: 70608 milliseconds
 */

public class CyclicBarrierClass {

    public static ConcurrentLinkedQueue<Integer> initializeConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<>();
    }
    public static AtomicIntegerArray consumerCounter(int T) {
        return new AtomicIntegerArray(T);
    }
    public static AtomicIntegerArray producerCounter(int T) {
        return new AtomicIntegerArray(T);
    }
    public static CyclicBarrier cyclicBarrier(int T) {
        return new CyclicBarrier(T);
    }
    public static CountDownLatch countDownLatch(int T) {
        return new CountDownLatch(T);
    }

    static Semaphore mutex = new Semaphore(1); // grants mutual exclusion for accessing the LinkedList
    static class Consumer extends Thread {
        int id;
        int N;
        int lastItem;
        ConcurrentLinkedQueue<Integer> list;
        AtomicIntegerArray consumerArray;
        CountDownLatch latch;

        public Consumer(int id, ConcurrentLinkedQueue<Integer> list, AtomicIntegerArray consumerArray, int N, CountDownLatch latch) {
            this.id = id;
            this.list = list;
            this.N = N;
            this.consumerArray = consumerArray;
            this.latch = latch;
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
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    latch.await();
                    // System.out.println("All threads have completed, continuing with execution");
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
        CyclicBarrier cyclicBarrier;

        public Producer(int id, ConcurrentLinkedQueue<Integer> list, AtomicIntegerArray producerArray,int N, CyclicBarrier cyclicBarrier) {
            this.id = id;
            this.list = list;
            this.N = N;
            this.producerArray = producerArray;
            this.cyclicBarrier = cyclicBarrier;
        }

        public void run() {
            while (producerArray.get(id) < N) {
                try {
                    mutex.acquire();
                    list.add(id);
                    producerArray.incrementAndGet(id);
                    // System.out.println("Producer " + id + " produced element " + id + " and produced in total " + producerArray.get(id));
                    mutex.release();
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
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

        ConcurrentLinkedQueue<Integer> list = CyclicBarrierClass.initializeConcurrentLinkedQueue();

        AtomicIntegerArray consumerArray = CyclicBarrierClass.consumerCounter(T);
        AtomicIntegerArray producerArray = CyclicBarrierClass.producerCounter(T);
        CyclicBarrier cyclicBarrier = CyclicBarrierClass.cyclicBarrier(T);
        CountDownLatch latch = CyclicBarrierClass.countDownLatch(T);

        Producer[] producer = new Producer[T];
        Consumer[] consumer = new Consumer[T];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < T; i++) {
            producer[i] = new Producer(i, list, producerArray, N, cyclicBarrier);
            consumer[i] = new Consumer(i, list, consumerArray, N, latch);
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

