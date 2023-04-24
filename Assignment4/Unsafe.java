import java.util.LinkedList;
import java.util.function.Consumer;

public class Unsafe {

    public static LinkedList<Integer> initializeLinkedList() {
        return new LinkedList<Integer>();
    }

    static class Consumer extends Thread {
        int id;
        int N;
        int lastItem;
        int consumed;
        LinkedList<Integer> list;

        public Consumer(int id, LinkedList<Integer> list, int N) {
            this.id = id;
            this.list = list;
            this.N = N;
        }

        public void run() {
            while (consumed < N) {
                if (list.size() != 0) {
                    lastItem = list.remove(list.size() - 1);
                    System.out.println("Consumer " + id + " consumed element" + lastItem + " from list");
                    consumed++;

                }
            }
        }
    }


    static class Producer extends Thread {
        int id;
        int produced = 0;
        int N;
        LinkedList<Integer> list;

        public Producer(int id, LinkedList<Integer> list, int N) {
            this.id = id;
            this.list = list;
            this.N = N;
        }

        public void run() {
            while (produced < N) {
                if (list.size() > 0) {
                    list.add(id);
                    System.out.println("Thread " + id + " produced element " + id);
                    produced++;
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
            producer[i] = new Producer(i, list, N);
            consumer[i] = new Consumer(i, list, N);
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
    }
}

