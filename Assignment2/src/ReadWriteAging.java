
public class ReadWriteAging {
    private int readers;
    private int writers;
    private int counter;
    private boolean isWriter;
    // implements aging for writers to avoid starvation
    private int writeWaitTime;
    private int readWaitTime;
    private int lastWriterId;

    public synchronized void lockRead(int id) throws InterruptedException {
        while (isWriter || (readers > 0 && writeWaitTime < readers) || (id != lastWriterId && writeWaitTime > 0)) {
            if (readers > 0 && writeWaitTime < readers) {
                System.out.println("I'm reader " + id + " and I've been greedy, I'll let writer go first");
            }
            wait();
        }
        System.out.println("I'm reader " + id + " and I'm reading counter = " + counter);
        readers++;
    }

    public synchronized void unlockRead() {
        readers--;
        writeWaitTime += writers;
        readWaitTime = 0;
        notifyAll();
    }

    public synchronized void lockWrite(int id) throws InterruptedException {
        while (isWriter || readers > 0 || (writers > 0 && readWaitTime < writers) || (id != lastWriterId && readWaitTime > 0)) {
            if (writers > 0 && writeWaitTime < writers) {
                System.out.println("I'm writer " + id + " and I've been greedy, I'll let reader go first");
            }
            wait();
        }
        isWriter = true;
        writers++;
    }

    public synchronized void unlockWrite(int id) {
        counter = id;
        System.out.println("I'm writer " + id + " and I wrote counter = " + id);
        isWriter = false;
        lastWriterId = id;
        readWaitTime += readers;
        writeWaitTime = 0;
        writers--;
        notifyAll();
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
                    lock.lockWrite(id);
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
                    lock.lockRead(id);
                    // perform read operation
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlockRead();
                }
            });
            readerThread.start();
        }
    }
}
