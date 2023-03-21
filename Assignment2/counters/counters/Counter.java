package counters;

public class Counter {

	static volatile long counter;

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
					counter++;
				}
			} else {
				for (long l = 0; l < n; l++) {
					counter--;
				}
			}
		}
	}

	public static void main(String[] args) {
		int t = (args.length >= 1 ? Integer.parseInt(args[0]) : 4);
		long n = (args.length >= 2 ? Long.parseLong(args[1]) : 10000000);

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
