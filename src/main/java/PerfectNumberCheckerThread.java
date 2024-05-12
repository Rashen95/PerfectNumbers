public class PerfectNumberCheckerThread extends Thread {
    private final long start;
    private final long end;
    private final long checkedNumber;

    public PerfectNumberCheckerThread(long start, long end, long checkedNumber) {
        this.start = start;
        this.end = end;
        this.checkedNumber = checkedNumber;
    }

    @Override
    public void run() {
        isPerfectNumber();
    }

    public void isPerfectNumber() {
        for (long i = this.start; i < this.end; i++) {
            if (Main.counter.get() > checkedNumber) {
                break;
            }
            if (checkedNumber % i == 0) {
                Main.counter.addAndGet(i);
            }
        }
    }
}