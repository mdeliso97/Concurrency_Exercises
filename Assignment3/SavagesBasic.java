import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

/**
 * The program creates a shared portions variable to keep track of the number of portions in the pot, and uses three
 * Semaphore objects: mutex, empty, and full. THe pot is extended through an AtomicIntegerArray which holds 1 if the
 * slot has food in it, 0 otherwise.
 * <p>
 * The mutex semaphore is used to ensure mutual exclusion when accessing the pot, so that only one thread can
 * access it at a time.
 * <p>
 * The empty semaphore is used to signal the cook that the pot is empty and needs to be refilled. Each savage that
 * finds the pot empty releases the empty semaphore and waits for the full semaphore to be released by the cook
 * when the pot is refilled.
 * <p>
 * The full semaphore is used to signal the savages that the pot has been refilled and is now full. Each time the
 * cook refills the pot, it releases the full semaphore, which allows the waiting savages to resume eating.
 * <p>
 * The Savage class represents a thread that simulates a savage. Each savage loops indefinitely and tries to eat
 * from the pot. If the pot is empty, the current savage releases the empty semaphore and waits for the full semaphore
 * to be released by the cook. If the pot is not empty, the savage takes a portion from the pot, prints a message to the
 * console, and sleeps for a while to simulate eating time.
 * <p>
 * The Cook class represents a thread that simulates the cook. The cook loops indefinitely and waits for the empty
 * semaphore to know if the pot needs to be refilled.
 * <p>
 */

public class SavagesBasic {
    static Semaphore mutex = new Semaphore(1); // mutual exclusion for accessing the pot
    static Semaphore empty = new Semaphore(0); // counts the number of times the pot is empty
    static Semaphore full = new Semaphore(0); // counts the number of times the pot is full

    public static AtomicIntegerArray eatCounter(int savages) {
        return new AtomicIntegerArray(savages);
    }

    public static AtomicIntegerArray initializePot(int potLength) {
        return new AtomicIntegerArray(potLength);
    }

    private static int portions = 0; // number of portions in the pot

    static class Savage extends Thread {
        public static final Semaphore finished0 = new Semaphore(0);
        int id;
        AtomicIntegerArray pot;
        AtomicIntegerArray portionsEaten;

        public Savage(int id, AtomicIntegerArray portionsEaten, AtomicIntegerArray pot) {
            this.id = id;
            this.pot = pot;
            this.portionsEaten = portionsEaten;
        }

        public void run() {
            while (portionsEaten.get(id) < 1) {
                try {
                    mutex.acquire(); // acquire mutual exclusion
                    if (portions != 0) {
                        for (int i = 0; i < pot.length(); i++) {
                            if (pot.get(i) != 0) {
                                pot.getAndSet(i, 0);
                                portionsEaten.getAndIncrement(id);
                                portions--;
                                System.out.println("Savage " + id + " ate a portion, " + portions + " portions left");
                                break;
                            }
                        }
                    } else {
                        System.out.println("Savage " + id + " Notifies cooker");
                        empty.release(); // notify the cook that the pot is empty
                        full.acquire(); // wait for the cook to refill the pot
                        System.out.println("Current situation on portions eaten per Savage: " + portionsEaten);
                        portions = pot.length();
                    }
                    mutex.release(); // release mutual exclusion
                    Thread.sleep(1000); // simulate eating time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Cooker extends Thread {
        AtomicIntegerArray pot;
        AtomicIntegerArray portionsEaten;
        public static final Semaphore finished1 = new Semaphore(0);

        public Cooker(AtomicIntegerArray pot, AtomicIntegerArray portionsEaten) {
            this.pot = pot;
            this.portionsEaten = portionsEaten;
        }

        public void run() {
            while (true) {
                try {
                    empty.acquire(); // wait for the pot to be empty
                    for (int i = 0; i < pot.length(); i++)// refill the pot
                        pot.getAndIncrement(i);

                    System.out.println("Cooker refilled the pot");
                    full.release(); // notify the savages that the pot is full
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Savage.finished0.acquire(pot.length());
                    System.out.println("All savages are done eating: " + portionsEaten);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        int Savages = Integer.parseInt(args[0]);
        int PotLength = Integer.parseInt(args[1]);

        //check that inputs are positive
        if (Savages <= 0 || PotLength <= 0) {
            System.out.println("The integers you entered are not positive, please try again");
            return;
        }

        AtomicIntegerArray Pot = SavagesBasic.initializePot(PotLength);

        AtomicIntegerArray portionsEaten = SavagesBasic.eatCounter(Savages);

        Savage[] savages = new Savage[Savages];
        for (int i = 0; i < Savages; i++) {
            savages[i] = new Savage(i, portionsEaten, Pot);
            savages[i].start();
        }
        Cooker cooker = new Cooker(Pot, portionsEaten);
        cooker.start();

        // Wait for threads completion
        try {
            Savage.finished0.acquire(Savages);
            Cooker.finished1.acquire();
        } catch (InterruptedException e) {
        }
    }

}