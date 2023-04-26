import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Rules:
 * <p>
 * - You can use volatile variables, synchronized constructs and AtomicIntegerArray
 * objects.
 * - You should not rely on the fairness implementation provided by the Java classes (e.g., using a fair semaphore).
 * </p>
 * <p>
 * - You are not allowed to use atomic classes (e.g., AtomicInteger) or synchronizers (e.g., CountDownLatch, Semaphore
 * and Phaser).
 * </p>
 * <p>
 * - You are not allowed to use condition variables, i.e., the wait()/ notify() and
 * await()/ signal() methods.
 * </p>
 * <p>
 * - You are not allowed to use the Thread.interrupt() and Thread.sleep()
 * </p>
 *
 * Notes: The current implementation does not work properly, I really struggled to find out a solution to the
 * SavagesFair with all of these constraints, with all implementation I tried, I always had mutual exclusion problems,
 * the savages kept eating from the pot even if it was empty or accessed synchronized methods simultaneously for some
 * reasons, probably a different data-structure with queues might have helped. Maybe with some extra time I could have
 * figured out a better way to implement it.
 */


public class SavagesFair {

    public static AtomicIntegerArray eatCounter(int savages) {
        return new AtomicIntegerArray(savages);
    }

    public static AtomicIntegerArray initializePot(int potLength) {
        return new AtomicIntegerArray(potLength);
    }

    private static volatile int portions = 0; // number of portions in the pot
    private static volatile int count = 0;
    private static boolean cooking = false;

    public synchronized static void incrementPortions() {
        portions++;
    }

    public synchronized static void decrementPortions() {
        portions--;
    }
    public synchronized static int getPortions(int length, AtomicIntegerArray pot) {
        for (int i = 0; i < length; i++) {
            if (pot.get(i) == 0)
                count++;
        }
        return count;
    }

    static class Savage extends Thread {
        int id;
        AtomicIntegerArray pot;
        AtomicIntegerArray portionsEaten;
        ReentrantLock[] locks;

        public Savage(int id, AtomicIntegerArray portionsEaten, AtomicIntegerArray pot, ReentrantLock[] locks) {
            this.id = id;
            this.pot = pot;
            this.portionsEaten = portionsEaten;
            this.locks = locks;
        }
        public int get(int i) {
            locks[i].lock();
            try {
                return pot.get(i);
            } finally {
                locks[i].unlock();
            }
        }
        public void getAndSet(int i) {
            locks[i].lock();
            try {
                if (get(i) != 0) {
                    decrementPortions();
                    pot.getAndSet(i, 0);
                    portionsEaten.getAndIncrement(id);
                    System.out.println("Savage " + id + " ate a portion, " + portions + " portions left");
                    System.out.println(pot);
                }
            } finally {
                locks[i].unlock();
            }
        }
        public void Eat() throws InterruptedException {
            if (portions > 0 && !cooking) {
                for (int i = 0; i < pot.length(); i++) {
                    getAndSet(i);
                }
            }
        }

        public void run() {
            while (true) {
                try {
                    Eat();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class Cooker extends Thread {
        AtomicIntegerArray pot;
        AtomicIntegerArray portionsEaten;
        public Cooker(AtomicIntegerArray pot, AtomicIntegerArray portionsEaten) {
            this.pot = pot;
            this.portionsEaten = portionsEaten;
        }

        public void run() {
            while (true) {
                cooking = true;
                if (getPortions(pot.length(), pot) == 0) {

                    for (int i = 0; i < pot.length(); i++)// refill the pot
                    {
                        pot.getAndIncrement(i);
                        incrementPortions();
                    }
                    System.out.println("Cooker refilled the pot");
                }
                cooking = false;
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
        int Savages = Integer.parseInt(args[0]);
        int PotLength = Integer.parseInt(args[1]);

        //check that inputs are positive
        if (Savages <= 0 || PotLength <= 0) {
            System.out.println("The integers you entered are not positive, please try again");
            return;
        }

        AtomicIntegerArray Pot = SavagesFair.initializePot(PotLength);

        AtomicIntegerArray portionsEaten = SavagesFair.eatCounter(Savages);

        ReentrantLock[] locks = new ReentrantLock[PotLength];
        for (int i = 0; i < PotLength; i++) {
            locks[i] = new ReentrantLock();
        }

        SavagesFair.Savage[] savages = new SavagesFair.Savage[Savages];
        for (int i = 0; i < Savages; i++) {
            savages[i] = new SavagesFair.Savage(i, portionsEaten, Pot, locks);
            savages[i].start();
        }
        SavagesFair.Cooker cooker = new SavagesFair.Cooker(Pot, portionsEaten);
        cooker.start();

        // Wait for threads completion
        for (int i = 0; i < Savages; i++) {
            try {
                savages[i].join();
                cooker.join();
            } catch (InterruptedException e) {
            }
        }
    }
}
