import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class SharedCounter {
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
        Thread[] threads = new Thread[T];

        //start timer for measurements
        long startTime = System.currentTimeMillis();

        //initialize all threads
        for (int i = 0; i < T; i++) {
            threads[i] = new Thread(new SharedCounterSearch(N));
            threads[i].start();
        }

        //wait for all threads to finish through Semaphore
        try {
            SharedCounterSearch.finished.acquire(T);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for threads to finish");
            System.exit(1);
        }

        //wait for all threads to finish
        //for (Thread thread : threads) {
        //    try {
        //        thread.join();
        //    } catch (InterruptedException e) {
        //        e.printStackTrace();
        //    }
        //}

        //stop timer and calculate elapsed time
        long finishTime = System.currentTimeMillis();
        long timeElapsed = finishTime - startTime;

        //print the results
        System.out.println("All primes found between 1 and " + N + " with " + T + " Threads" + ":");
        System.out.println(SharedCounterSearch.primes);
        System.out.println("Total time elapsed: " + timeElapsed + " milliseconds");
        System.out.println("Total number of primes found: " + SharedCounterSearch.primes.size());
    }
}

class SharedCounterSearch implements Runnable {

    private final int maximum; // It is N, boundary of numbers to be evaluated
    private static final ReentrantLock lock = new ReentrantLock();
    static final ArrayList<Integer> primes = new ArrayList<>();
    // Shared counter used by all threads
    private static int counter;
    // A monitor to protect the counter
    private static final Object monitor = new Object();
    // A semaphore to signal when all threads are done
    static final Semaphore finished = new Semaphore(0);

    public static void add(int i) {
        lock.lock();
        try {
            primes.add(i);
        } finally {
            lock.unlock();
        }
    }

    public SharedCounterSearch(int maximum) {

        this.maximum = maximum;
    }

    public void run() {
        int i;

        while (true) {
            // Get the next number to test
            synchronized (SharedCounterSearch.monitor) {
                i = SharedCounterSearch.counter++;
                if (i > maximum) {
                    break;
                }
            }
            // Test if n is prime and add it to the list if it is
            if (isPrime(i)) {
                synchronized (SharedCounterSearch.primes) {
                    SharedCounterSearch.add(i);
                }
            }
        }
        // Signal that this thread is done (semaphore is notified that thread is done)
        SharedCounterSearch.finished.release();

    }

    //check if a number is prime with complexity O(sqrt(n))
    public static boolean isPrime(int i) {
        if (i <= 1) {
            return false;
        } else if (i <= 3) {
            return true;
        } else if (i % 2 == 0 || i % 3 == 0) {
            return false;
        } else {
            int j = 5;
            while (j * j <= i) {
                if (i % j == 0 || i % (j + 2) == 0) {
                    return false;
                }
                j += 6;
            }
            return true;
        }
    }
}
