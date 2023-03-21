package counters;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class CounterBakery {

	static long counter;
	static BakeryLock lock;

	static class BakeryLock {
		AtomicIntegerArray flag;
		AtomicIntegerArray label;
		int n;

		public BakeryLock(int n) {
			this.n = n;
			flag = new AtomicIntegerArray(n);
			label = new AtomicIntegerArray(n);
		}

		public void lock(int i) {
			flag.set(i, 1);
			int max = 0;
			for (int k = 0; k < n; k++) {
				int l = label.get(k);
				if (l > max)
					max = l;
			}
			label.set(i, max + 1);
			for (int k = 0; k < n; k++) {
				if (k == i) continue;
				int lk, li;
				while (flag.get(k) != 0 && ((lk = label.get(k)) < (li = label.get(i)) || (lk == li && k < i)));
			}
		}

		public void unlock(int i) {
			flag.set(i, 0);
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

		lock = new BakeryLock(t);

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
