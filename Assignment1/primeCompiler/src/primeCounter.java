import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class primeCounter {
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

        //calculate the range for each thread
        int threadRange = (N - 1) / T;

        //start timer for measurements
        long startTime = System.currentTimeMillis();

        //split the range and assign to each thread
        for (int i = 0; i < T; i++) {
            int start = i * threadRange + 1;
            int end = start + threadRange;
            threads[i] = new Thread(new PrimeSearch(start, end));
            threads[i].start();
        }

        //wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //stop timer and calculate elapsed time
        long finishTime = System.currentTimeMillis();
        long timeElapsed = finishTime - startTime;

        //print the results
        System.out.println("All primes found between 1 and " + N + " with " + T + " Threads" + ":");
        System.out.println(PrimeSearch.primes);
        System.out.println("Total time elapsed: " + timeElapsed + " milliseconds");
        System.out.println("Total number of primes found: " + PrimeSearch.primes.size());
    }
}

class PrimeSearch implements Runnable {
    private final int start;
    private final int end;
    private static final ReentrantLock lock = new ReentrantLock();

    // A list of all found prime numbers
    static ArrayList<Integer> primes = new ArrayList<>();

    public static void add(int i) {
        lock.lock();
        try {
            primes.add(i);
        } finally {
            lock.unlock();
        }
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

    public PrimeSearch(int start, int end) {
        this.start = start;
        this.end = end;
    }
    public void run() {

        for (int i = start; i <= end; i++) {
            if (isPrime(i)) {
                PrimeSearch.add(i);
            }
        }
    }
}