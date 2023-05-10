import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class CASConsensus implements IConsensus {

    private final AtomicReference<Integer> decision = new AtomicReference<>(null);
    private final int numThreads;
    private final Random random = new Random();
    private final int[] values;

    public CASConsensus(int numThreads) {
        this.numThreads = numThreads;
        this.values = new int[numThreads];
    }

    public synchronized Integer decide(Integer value) {
        while (true) {
            Integer currentDecision = decision.get();
            if (currentDecision != null) {
                return currentDecision;
            }

            int numAgree = 1;
            values[0] = value;

            // Propose values from other threads
            for (int i = 0; i < numThreads; i++) {
                if (i == ThreadId.get()) continue;
                int proposedValue = proposeValue(i);
                values[numAgree] = proposedValue;
                numAgree++;
            }

            // Check if consensus is reached
            if (numAgree == numThreads) {
                Integer agreedValue = findMajority(values);
                if (decision.compareAndSet(null, agreedValue)) {
                    return agreedValue;
                }
            }
        }
    }

    private int proposeValue(int threadID) {
        // Simulate a delay
        try {
            Thread.sleep(random.nextInt(5) + 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Propose a value
        return values[threadID];
    }

    private int findMajority(int[] values) {
        int majorityCount = 0;
        int majorityValue = 0;
        for (int value : values) {
            int count = 0;
            for (int otherValue : values) {
                if (value == otherValue) count++;
            }
            if (count > majorityCount) {
                majorityCount = count;
                majorityValue = value;
            }
        }
        return majorityValue;
    }

    public static void main(String[] args) {

        // Check that input is positive
        if (args.length != 1) {
            System.out.println("Usage: java CASConsensus <numThreads>");
            return;
        }
        int numThreads = Integer.parseInt(args[0]);
        if (numThreads <= 0) {
            System.out.println("The integer you entered is not positive, please try again");
            return;
        }

        // Create threads and start them
        ThreadId.reset();
        CASConsensus consensus = new CASConsensus(numThreads);
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new ConsensusThread(consensus));
            threads[i].start();
        }

        // Wait for threads to complete
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Print final decision
        System.out.println("Consensus reached: " + consensus.decision.get());
    }

    @Override
    public Object decide(Object v) throws InterruptedException {
        return null;
    }
}

class ThreadId {
    private static int nextID = 0;
    private static ThreadLocal<Integer> threadId = ThreadLocal.withInitial(() -> nextID++);

    public static int get() {
        return threadId.get();
    }

    public static void reset() {
        nextID = 0;
    }
}

