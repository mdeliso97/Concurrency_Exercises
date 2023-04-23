import java.util.LinkedList;

public class Unsafe {

    public static LinkedList<Integer> initializeLinkedList(int linkedListLength) {
        return new LinkedList<Integer>();
    }

    static class Consumer extends Thread {
        int id;
        AtomicIntegerArray queue;

        public consumer(int id, AtomicIntegerArray portionsEaten, AtomicIntegerArray pot) {
            this.id = id;
            this.pot = pot;
            this.portionsEaten = portionsEaten;
        }

        public void run() {
            while (true) {
                try {
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
                        System.out.println("Current situation on portions eaten per Savage: " + portionsEaten);
                        portions = pot.length();
                    }
                    Thread.sleep(1000); // simulate eating time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Producer extends Thread {
        int id;
        AtomicIntegerArray queue;

        public producer(int id, AtomicIntegerArray portionsEaten, AtomicIntegerArray pot) {
            this.id = id;
            this.list = pot;
            this.portionsEaten = portionsEaten;
        }

        public void run() {
            while (true) {
                try {
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
                        System.out.println("Current situation on portions eaten per Savage: " + portionsEaten);
                        portions = pot.length();
                    }
                    Thread.sleep(1000); // simulate eating time
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
        int T = Integer.parseInt(args[0]);
        int QueueLength = Integer.parseInt(args[1]);

        //check that inputs are positive
        if (T <= 0 || QueueLength <= 0) {
            System.out.println("The integers you entered are not positive, please try again");
            return;
        }

        AtomicIntegerArray Pot = Unsafe.initializeQueue(QueueLength);

        AtomicIntegerArray portionsEaten = SavagesBasic.eatCounter(Savages);

        Savage[] savages = new Savage[Savages];
        for (int i = 0; i < Savages; i++) {
            savages[i] = new Savage(i, portionsEaten, Pot);
            savages[i].start();
        }
        Cooker cooker = new Cooker(Pot, portionsEaten);
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