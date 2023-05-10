import java.util.Random;
import java.util.concurrent.Semaphore;

public class CASConsensus extends Thread implements IConsensus {

    private volatile Object decision = null;
    static Semaphore mutex = new Semaphore(1);
    Random random = new Random();

    public void run() {
        int rand = random.nextInt(5) + 1;
        Object result;
        try {
            mutex.acquire();
            result = decide(rand);
            mutex.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Thread " + Thread.currentThread().getId() + " decided on " + result);
    }

    public Object getDecision() {

        return decision;
    }

    @Override
    public Object decide(Object v) throws InterruptedException {
        while (true) {
            Object curDecision = getDecision();
            if (curDecision != null) {
                //System.out.println("The decision made is " + curDecision);
                return curDecision;
            }
            if (compareAndSetDecision(null, v)) {
                //System.out.println("The decision made is " + v);
                return v;
            }
        }
    }

    private boolean compareAndSetDecision(Object expect, Object update) {
        synchronized (this) {
            if (decision == expect) {
                decision = update;
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) {

        //check that inputs is 1
        if (args.length != 1) {
            System.out.println("The argument provided is not 1!");
            return;
        }

        //parse input arguments
        int T = Integer.parseInt(args[0]);

        //check that inputs are positive
        if (T <= 0) {
            System.out.println("The integer you entered is not positive, please try again");
            return;
        }

        Thread[] threads = new Thread[T];

        for (int i = 0; i < T; i++) {
            threads[i] = new Thread(new CASConsensus());
            threads[i].start();
        }

        // Wait for threads completion
        for (int i = 0; i < T; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
            }
        }
    }
}
