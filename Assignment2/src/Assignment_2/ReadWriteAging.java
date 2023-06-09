import java.util.concurrent.Semaphore;

public class ReadWriteAging {
    private int readers = 0;
    private int counter;
    private boolean isWriter;
    private boolean isYourTurnWriter = false;
    // implements aging for writers to avoid starvation
    private int writeWaitTime;
    private int allDone = 0;

    private static final Semaphore finished = new Semaphore(0);

    public synchronized void lockRead(int id, int writers) throws InterruptedException {
        while (isWriter || (readers > 0 && writeWaitTime >= 10 && allDone != writers) || isYourTurnWriter) {
            if (readers > 0 && writeWaitTime >= 10) {
                System.out.println("I'm reader " + id + " and I've been greedy, I'll let writer go first");
                isYourTurnWriter = true;
            }
            wait();
        }
        System.out.println("I'm reader " + id + " and I'm reading counter = " + counter);
        readers++;
    }

    public synchronized void unlockRead() {
        readers--;
        writeWaitTime++;

        notifyAll();
        ReadWriteAging.finished.release();
    }

    public synchronized void lockWrite() throws InterruptedException {
        while (isWriter || readers > 0) {
            wait();
        }
        isWriter = true;
    }

    public synchronized void unlockWrite(int id) {
        counter = id;
        System.out.println("I'm writer " + id + " and I wrote counter = " + id);
        isWriter = false;
        writeWaitTime = 0;
        isYourTurnWriter = false;
        notifyAll();
        ReadWriteAging.finished.release();
        allDone++;
    }

    public static void main(String[] args) {
        //check that inputs are 2
        if (args.length != 2) {
            System.out.println("The arguments provided are not 2!");
            return;
        }

        int t_Writer = Integer.parseInt(args[0]);
        int t_Reader = Integer.parseInt(args[1]);

        //check that inputs are positive
        if (t_Reader <= 0 || t_Writer <= 0) {
            System.out.println("The integers you entered are not positive, please try again");
            return;
        }

        ReadWriteAging lock = new ReadWriteAging();
        System.out.println("Start with " + t_Reader + " readers and " + t_Writer + " writers");

        // initialize writer threads
        for (int i = 0; i < t_Writer; i++) {
            final int id = i;
            Thread writerThread = new Thread(() -> {
                try {
                    lock.lockWrite();
                    // perform write operation
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlockWrite(id);
                }
            });
            writerThread.start();
        }

        // initialize reader threads
        for (int i = 0; i < t_Reader; i++) {
            final int id = i;
            Thread readerThread = new Thread(() -> {
                try {
                    lock.lockRead(id, t_Writer);
                    // perform read operation
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlockRead();
                }
            });
            readerThread.start();
        }

        //wait for all threads to finish through Semaphore
        try {
            ReadWriteAging.finished.acquire(t_Writer + t_Reader);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for threads to finish");
            System.exit(1);
        }
    }
}
