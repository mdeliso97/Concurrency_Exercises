import java.util.LinkedList;
import java.util.function.Consumer;

public class Unsafe {

    public static LinkedList<Integer> initializeLinkedList() {
        return new LinkedList<Integer>();
    }

    static class Consumer extends Thread {
        int id;
        int N;
        LinkedList<Integer> list;

        public consumer(int id, LinkedList<Integer> list, int N) {
            this.id = id;
            this.list = list;
            this.N = N;
        }

        public void run() {
            while ( int i = 0; i < N){
                try {
                    if (list.size() != 0) {
                        list.pop();

                    }
                }
            } else{
                System.out.println("Savage " + id + " Notifies cooker");
                System.out.println("Current situation on portions eaten per Savage: " + portionsEaten);
                portions = pot.length();
            }
            Thread.sleep(1000); // simulate eating time
        } catch(
        InterruptedException e)

        {
            e.printStackTrace();
        }
    }


    static class Producer extends Thread {
        int id;
        LinkedList<Integer> list;

        public producer(int id, LinkedList<Integer> list) {
            this.id = id;
            this.list = list;
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
        int N = Integer.parseInt(args[1]);

        //check that inputs are positive
        if (T <= 0 || N <= 0) {
            System.out.println("The integers you entered are not positive, please try again");
            return;
        }

        LinkedList<Integer> list = Unsafe.initializeLinkedList();

        Producer[] producer = new Producer[T];
        Consumer[] consumer = new Consumer[T];
        for (int i = 0; i < T; i++) {
            producer[i] = new Producer.producer(i, list, N);
            consumer[i] = new Consumer();
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

//        Thread[] threads = new Thread[2 * T];
//        //initialize threads as producers
//        for (int i = 0; i < T; i++) {
//            final int id = i;
//            threads[i] = new Thread(new Thread(() -> {
//                try {
//                    while (true) {
//                        new Unsafe.Producer(id, list);
//                        Thread.sleep((long) (Math.random() * 1000));
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }));
//            threads[i].start();
//        }
//
//        //initialize threads as consumers
//        for (int i = 0; i < T; i++) {
//            final int id = i;
//            threads[i] = new Thread(new Thread(() -> {
//                try {
//                    while (true) {
//                        new Unsafe.Consumer(id);
//                        Thread.sleep((long) (Math.random() * 1000));
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }));
//            threads[i].start();
//        }
//
//        // Wait for threads completion
//        for (int i = 0; i < 2 * T; i++) {
//            try {
//                threads[i].join();
//            } catch (InterruptedException e) {
//            }
//        }
    }
}

