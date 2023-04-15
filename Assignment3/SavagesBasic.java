import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * You can use volatile variables, synchronized constructs and AtomicIntegerArray
 * objects.
 * You should not rely on the fairness implementation provided by the Java classes (e.g.,
 * using a fair semaphore).
 * You are not allowed to use atomic classes (e.g., AtomicInteger) or synchronizers (e.g.,
 * CountDownLatch, Semaphore and Phaser).
 * You are not allowed to use condition variables, i.e., the wait()/ notify() and
 * await()/ signal() methods.
 * You are not allowed to use the Thread.interrupt() and Thread.sleep().
 */
public class SavagesBasic {

    private static final ReentrantLock lock = new ReentrantLock();
    private static volatile boolean CanCook = true;
    private static volatile boolean CanEat = false;
    private static volatile int counter = 0;

    public static AtomicIntegerArray eatCounter(int savages) {
        return new AtomicIntegerArray(savages);
    }

    public static AtomicIntegerArray initializePot(int potLength) {
        return new AtomicIntegerArray(potLength);
    }

    public static AtomicIntegerArray numberSavages(int savages) {
        return new AtomicIntegerArray(savages);
    }

    public static synchronized void TakePortion(AtomicIntegerArray Pot, int id, AtomicIntegerArray eatCounter) {
        boolean foundMeal = false;
        while (!foundMeal && CanEat && !CanCook) {
            for (int j = 0; j < Pot.length(); j++) {
                if (Pot.get(j) != 0) {
                    Pot.getAndSet(j, 0);
                    eatCounter.incrementAndGet(id);
                    System.out.println("I am Savage " + id + ": I took meal " + j + "!");
                    counter++;
                    if (Pot.length() - 1 == j && !CanCook) {
                        synchronized (lock) {
                            System.out.println("I am Savage " + id + ": Meals are finished! Please cook more cooker!");
                            CanCook = true;
                            CanEat = false;
                        }
                    }
                    foundMeal = true;
                    break;
                }
            }
        }
    }

    public static void RefillPot(AtomicIntegerArray Pot, int Savages) {
        while (CanCook) {
            if (CanCook && !CanEat) {
                System.out.println("Cooker: Gotcha!");
                for (int i = 0; i < Pot.length(); i++) {
                    if (Pot.get(i) == 0) {
                        Pot.getAndIncrement(i);
                    }
                }
                CanCook = false;
                CanEat = true;
                System.out.println("Meals are ready!");
            }
        }
        while (!CanCook && counter != Savages) {
        }
        if (CanCook && counter != Savages) {
            RefillPot(Pot, Savages);
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

        //initialize threads
        Thread[] threads = new Thread[Savages + 1];

        AtomicIntegerArray Pot = SavagesBasic.initializePot(PotLength);

        AtomicIntegerArray numberSavages = SavagesBasic.numberSavages(Savages);

        AtomicIntegerArray eatCounter = SavagesBasic.eatCounter(Savages);

        //prints Environment
        System.out.println("The size of the bounded Pot is " + Pot.length() + " with " + Savages + " Savages and 1 Cooker Threads:");

        //initialize threads as Savages
        for (int i = 0; i < Savages; i++) {
            int finalI = i;
            numberSavages.getAndIncrement(i);
            threads[i] = new Thread(() -> TakePortion(Pot, finalI, eatCounter));
        }

        //initialize cooker
        threads[Savages] = new Thread(() -> RefillPot(Pot, Savages));

        // Start threads
        for (int i = 0; i < Savages + 1; i++) {
            threads[i].start();
        }

        // Wait for threads completion
        for (int i = 0; i < Savages + 1; i++) {
            try {
                threads[i].join();
                numberSavages.getAndDecrement(i);
            } catch (InterruptedException e) {
            }
        }

    }
}

