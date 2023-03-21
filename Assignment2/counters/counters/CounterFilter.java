package counters;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class CounterFilter {

	static long counter;
	static FilterLock lock;

	static class FilterLock {
		AtomicIntegerArray level;
		AtomicIntegerArray victim;
		int n;

		public FilterLock(int n) {
			this.n = n;
			level = new AtomicIntegerArray(n);
			victim = new AtomicIntegerArray(n);
		}

		public void lock(int i) {
			for (int l = 1; l < n; l++) {
				level.set(i, l);
				victim.set(l, i);
				for (int k = 0; k < n; k++) {
					if (k == i) continue;
					while (level.get(k) >= l && victim.get(l) == i);
				}
			}
		}

		public void unlock(int i) {
			level.set(i, 0);
		}
	}

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
					lock.lock(id);
					counter++;
					lock.unlock(id);
				}
			} else {
				for (long l = 0; l < n; l++) {
					lock.lock(id);
					counter--;
					lock.unlock(id);
				}
			}
		}
	}

	public static void main(String[] args) {
		int t = (args.length >= 1 ? Integer.parseInt(args[0]) : 4);
		long n = (args.length >= 2 ? Long.parseLong(args[1]) : 10000000);

		lock = new FilterLock(t);

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
