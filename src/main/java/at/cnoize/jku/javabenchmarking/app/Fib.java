package at.cnoize.jku.javabenchmarking.app;

public class Fib {

    public static void main(String[] args) {
        System.out.println(calculateFib(50));
    }

    private static long calculateFib(int i) {
        return fib(i);
    }

    public static long fib(int n) {
        if (0 <= n && n <= 1) {
            return n;
        } else {
            return fib(n - 2) + fib(n - 1);
        }
    }
}
