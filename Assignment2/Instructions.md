Here's a possible implementation of a simple read-write lock in Java using synchronized methods and the wait/notify mechanism:

    public class ReadWriteLock {
    private int readers;
    private boolean isWriter;
    
        public synchronized void lockRead() throws InterruptedException {
            while (isWriter) {
                wait();
            }
            readers++;
        }
        
        public synchronized void unlockRead() {
            readers--;
            notifyAll();
        }
        
        public synchronized void lockWrite() throws InterruptedException {
            while (isWriter || readers > 0) {
                wait();
            }
            isWriter = true;
        }
        
        public synchronized void unlockWrite() {
            isWriter = false;
            notifyAll();
        }
    }

The ReadWriteLock class has two instance variables: readers, which keeps track of the number of readers currently executing in the critical section, and isWriter, which indicates whether a writer is currently executing in the critical section.

The lockRead() method acquires a read lock by waiting in a loop while there is a writer executing in the critical section. Once the writer has exited and the lock is available, readers is incremented to reflect that a new reader has entered the critical section.

The unlockRead() method releases a read lock by decrementing readers and notifying all waiting threads that the lock is available.

The lockWrite() method acquires a write lock by waiting in a loop while there is either a writer executing or one or more readers executing in the critical section. Once the lock is available, isWriter is set to true to indicate that a writer is executing in the critical section.

The unlockWrite() method releases a write lock by setting isWriter to false and notifying all waiting threads that the lock is available.

Note that this implementation does not provide any guarantees regarding fairness or reentrancy. Additionally, it's worth noting that this implementation could suffer from writer starvation if there are many readers constantly entering and exiting the critical section.