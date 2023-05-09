public class CASConsensus implements IConsensus, Runnable {

    private volatile Object decision = null;

    public class CASConsensusThread implements Runnable {
        private CASConsensus casConsensus;

        public CASConsensusThread() {
            this.casConsensus = new CASConsensus();
        }


        public void run() {

        }
    }
    @Override
    public Object decide(Object v) {
        while (true) {
            Object curDecision = decision;
            if (curDecision != null) {
                System.out.println("The decision made is " + curDecision);
                return curDecision;
            }
            if (compareAndSetDecision(null, v)) {
                System.out.println("The decision made is " + v);
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
            System.out.println("The arguments provided are not 2!");
            return;
        }

        //parse input arguments
        int T = Integer.parseInt(args[0]);

        //check that inputs are positive
        if (T <= 0) {
            System.out.println("The integers you entered are not positive, please try again");
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
