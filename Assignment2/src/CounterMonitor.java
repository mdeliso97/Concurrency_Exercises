public class CounterMonitor {

    static long counter;
    static Object lock = new Object();

    static class CounterThread implements Runnable {
        int id;
        long n;

        public CounterThread(int id, long n) {
            this.id = id;
            this.n = n;
        }

        @Override
        public void run() {
            if (id % 2 == 0) {
                for (long l = 0; l < n; l++) {
                    synchronized(lock) {
                        counter++;
                    }
                }
            } else {
                for (long l = 0; l < n; l++) {
                    synchronized(lock) {
                        counter--;
                    }
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

        int t = Integer.parseInt(args[0]);
        long n = Integer.parseInt(args[1]);

        //check that inputs are positive
        if (t <= 0 || n <= 0) {
            System.out.println("The integers you entered are not positive, please try again");
            return;
        }

        System.out.println("Start with " + t + " threads");
        // Create threads
        Thread[] threads = new Thread[t];
        for (int i = 0; i < t; i++) {
            threads[i] = new Thread(new CounterThread(i, n));
        }
        long time = System.currentTimeMillis();
        // Start threads
        for (int i = 0; i < t; i++) {
            threads[i].start();
        }
        // Wait for threads completion
        for (int i = 0; i < t; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Finished with total of " + counter + " in " + time + " ms");
    }

}
