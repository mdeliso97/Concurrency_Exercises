Assignment 2

Exercise 2.1

Consider the “Counter” program seen in the labs and provided on ILIAS with different
synchronization mechanisms used to protect a shared integer. As a reminder, the program
takes as arguments two integers, T and N, and forks T threads that modify a shared integer
as follows. Even threads (i.e., threads 0, 2, 4...) increment the integer N times while odd
threads (i.e., threads 1, 3, 5...) decrement it N times. The program prints the final value of
the integer (which should be 0 if T is even, or N otherwise).
Execute the program with N=10'000'000 and T={2,4,8,16} and all provided synchronization
mechanisms. Report execution times.


Exercise 2.2
1. Read-write lock:

    A read-write lock allows either a single writer or multiple readers to execute in a critical
    section. Provide an implementation of a read-write lock in Java. You can use synchronized
    methods and the wait/notify mechanism if you wish. The class should provide the 4
    methods lockRead(), unlockRead(), lockWrite(), and unlockWrite(). This
    implementation does not need to be FIFO, starvation-free, nor reentrant.
    HINT: you might want to keep track of the number of readers and writers.
2. Starvation-free read-write lock

   Try to make the read-write lock starvation free for writes (a writer cannot be blocked
   forever by readers continuously requesting and acquiring the lock).

3. FIFO and reentrant read-write lock (optional):

   Try to make the read-write lock FIFO and reentrant.
   
Exercise 2.3

   Are the following histories linearizable or sequentially consistent? Explain your answers and
   write the equivalent linearizable/sequential consistent histories where applicable.

1. Read/write register

    Concurrent threads A, B, C, register r:
    
        A: r.write(1)
        C: r.read()
        A: r:void
        A: r.write(2)
        C: r:2
        C: r.read()
        B: r.read()
        A: r:void
        C: r:1
        A: r.write(1)
        B: r:1
        A: r:void

    Concurrent threads A, B, C, register r.

        A: r.write(1)
        B: r.read()
        A: r:void
        A: r.write(2)
        A: r:void
        A: r.write(1)
        B: r:1
        C: r.read()
        A: r:void
        C: r:2

2. Stack:
   
    We have the following operations:
   - push(x) pushes element x on the stack, returns void;
   - pop() retrieves an element from the stack;
   - empty() returns true if stack is empty and false otherwise.
- Concurrent threads A, B, C, stack s:

      C: s.empty()
      A: s.push(10)
      B: s.pop()
      A: s:void
      A: s.push(20)
      B: s:10
      A: s:void
      C: s:true

- Concurrent threads A, B, C, stack s:

      A: s.push(10)
      B: s.push(10)
      A: s:void
      A: s.pop()
      B: s:void
      B: s.empty()
      A: s:10
      B: s:true
      A: s.pop()
      A: s:10

3. Queue:

   We have the following operations:

   - enq(x) inserts element x in the queue, returns void;
   - deq() retrieves an element from the queue.
- Concurrent threads A, B, C, queue q.

      A: q.enq(x)
      B: q.enq(y)
      A: q:void
      B: q:void
      A: q.deq()
      C: q.deq()
      A: q:y
      C: q:y