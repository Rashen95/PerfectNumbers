public class PrimeNumberCheckerThread extends Thread {
    private final long start;
    private final long end;
    private final long checkedNumber;

    public PrimeNumberCheckerThread(long start, long end, long checkedNumber) {
        this.start = start;
        this.end = end;
        this.checkedNumber = checkedNumber;
    }

    @Override
    public void run() {
        isPrimeNumber();
    }

    public void isPrimeNumber() {
        for (long i = this.start; i < this.end; i++) {
            if (!Main.primeNumberFlag.get()) {
                break;
            }
            if (checkedNumber % i == 0) {
                Main.primeNumberFlag.set(false);
            }
        }
    }
}