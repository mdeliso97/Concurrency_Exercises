import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MatrixMultiplication {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int N = Integer.parseInt(args[0]);
        int[][] A = generateRandomMatrix(N);
        int[][] B = generateRandomMatrix(N);
        int[][] C = new int[N][N];

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Future<Integer> future = executor.submit(new MatrixMultiplicationTask(A, B, i, j));
                C[i][j] = future.get();
            }
        }

        executor.shutdown();

        System.out.println("Matrix A:");
        printMatrix(A);
        System.out.println("Matrix B:");
        printMatrix(B);
        System.out.println("Matrix C:");
        printMatrix(C);
    }

    private static int[][] generateRandomMatrix(int N) {
        int[][] matrix = new int[N][N];
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = rand.nextInt(10);
            }
        }
        return matrix;
    }

    private static void printMatrix(int[][] matrix) {
        int N = matrix.length;
        for (int[] ints : matrix) {
            for (int j = 0; j < N; j++) {
                System.out.print(ints[j] + " ");
            }
            System.out.println();
        }
    }
}

class MatrixMultiplicationTask implements Callable<Integer> {
    private int[][] A;
    private int[][] B;
    private int row;
    private int col;

    public MatrixMultiplicationTask(int[][] A, int[][] B, int row, int col) {
        this.A = A;
        this.B = B;
        this.row = row;
        this.col = col;
    }

    public Integer call() {
        int N = A.length;
        int sum = 0;
        for (int i = 0; i < N; i++) {
            sum += A[row][i] * B[i][col];
        }
        return sum;
    }
}