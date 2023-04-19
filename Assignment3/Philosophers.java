/**
 * The class implements the dining philosopher problem. Essentially there are two boolean arrays which keeps track of the
 * forks picked up and the philosophers which are currently eating. TO ensure that no deadlocks occur, I implemented a
 * so-called "resource hierarchy solution" which consists on allowing a philosopher to pick up both forks if both are
 * available at the same time, otherwise the philosopher will put down any fork it picked up and start thinking again.
 */
public class Philosophers {
    private static final int NUM_PHILOSOPHERS = 5; // number of philosophers
    private static final int NUM_FORKS = 5; // number of forks
    private static final int NUM_MEALS = 10; // number of meals each philosopher eats

    private static boolean[] forks = new boolean[NUM_FORKS]; // array of forks, initially all available
    private static boolean[] isEating = new boolean[NUM_PHILOSOPHERS]; // array of philosophers' eating state, initially all false

    private static class Philosopher implements Runnable {
        private int id; // philosopher's id
        private int numMeals; // number of meals this philosopher has eaten

        public Philosopher(int id) {
            this.id = id;
        }

        // method for philosopher to think
        private void think() {
            try {
                Thread.sleep((long) (Math.random() * 2000)); // philosopher thinks for random time between 0-1 seconds
                System.out.println("Philosopher " + id + " is thinking...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // method for philosopher to eat
        private void eat() {
            try {
                Thread.sleep((long) (Math.random() * 2000)); // philosopher eats for random time between 0-1 seconds
                System.out.println("Philosopher " + id + " is eating...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (numMeals < NUM_MEALS) { // philosopher eats NUM_MEALS times
                // philosopher thinks for a while
                think();

                // philosopher picks up left fork
                synchronized (forks) {
                    while (forks[id]) { // check if left fork is available
                        try {
                            forks.wait(); // wait for left fork to be available
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    forks[id] = true; // pick up left fork
                    System.out.println("Philosopher " + id + " picks left fork");
                }

                // philosopher picks up right fork
                synchronized (forks) {
                    while (forks[(id + 1) % NUM_PHILOSOPHERS]) { // check if right fork is available
                        try {
                            forks.wait(); // wait for right fork to be available
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    forks[(id + 1) % NUM_PHILOSOPHERS] = true; // pick up right fork
                    System.out.println("Philosopher " + id + " picks right fork");
                }

                // philosopher eats for a while
                synchronized (isEating) {
                    isEating[id] = true; // set eating state to true
                }
                eat();

                // philosopher puts down right fork
                synchronized (forks) {
                    forks[(id + 1) % NUM_PHILOSOPHERS] = false; // put down right fork
                    System.out.println("Philosopher " + id + " has put down right fork");
                    forks.notifyAll(); // notify all philosophers that right fork is available
                }

                // philosopher puts down left fork
                synchronized (forks) {
                    forks[id] = false; // put down left fork
                    System.out.println("Philosopher " + id + " has put down left fork");
                    forks.notifyAll(); // notify all philosophers that left fork is available
                }

                // philosopher finishes eating
                synchronized (isEating) {
                    isEating[id] = false; // set eating state to false
                    System.out.println("Philosopher " + id + " finished eating");
                    numMeals++; // increment number of meals eaten
                }
            }
        }
    }
    public static void main(String[] args) {
        // create threads for each philosopher
        Thread[] philosophers = new Thread[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Thread(new Philosopher(i));
        }

        // start all philosopher threads
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i].start();
        }

        // wait for all philosopher threads to complete
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            try {
                philosophers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

