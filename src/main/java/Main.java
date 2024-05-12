import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    // Атомики для многопоточных вычислений
    public static volatile AtomicBoolean primeNumberFlag = new AtomicBoolean(true);
    public static volatile AtomicLong counter = new AtomicLong(0);

    // Коллекции для хранения найденных вторых множителей и потоков для вычислений
    private static final Map<Integer, Long> foundedSecondFactors = new TreeMap<>();
    private static final List<PrimeNumberCheckerThread> primeNumberCheckerThreadList = new ArrayList<>();
    private static final List<PerfectNumberCheckerThread> perfectNumberCheckerThreadList = new ArrayList<>();

    // Остальные необходимые переменные
    private static long secondFactor;
    private static long checkedNumber;
    private static long numbersPerProcessors;
    private static long start;
    private static long end;
    private static int processors;
    private static int p;
    private static final DateTimeFormatter dTF = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        System.out.println("Данная программа выводит первые 7 совершенных чисел");

        // Нахождение всех простых чисел для второго множителя при заданном p по формуле Эйлера
        for (p = 2; p <= 20; p++) {
            secondFactor = (long) Math.pow(2, p) - 1;
            processors = Runtime.getRuntime().availableProcessors();
            start = 2;

            if (secondFactor < processors) {
                calculatePrimeNumbersWith1Core();

                primeNumberFlag.set(true);
            } else {
                numbersPerProcessors = secondFactor / 2 / processors;
                end = start + numbersPerProcessors;

                calculatePrimeNumbersWithMultiCore();

                primeNumberCheckerThreadList.clear();
                primeNumberFlag.set(true);
            }
        }

        // Нахождение всех совершенных чисел при найденных простых вторых множителях
        for (int p : foundedSecondFactors.keySet()) {
            checkedNumber = (long) Math.pow(2, p - 1) * foundedSecondFactors.get(p);
            start = 1;

            if (checkedNumber < processors) {
                calculatePerfectNumbersWith1Core();

                counter.set(0);
            } else {
                numbersPerProcessors = checkedNumber / 2 / processors;
                end = start + numbersPerProcessors;

                calculatePerfectNumbersWithMultiCore();

                perfectNumberCheckerThreadList.clear();
                counter.set(0);
            }
        }

        System.out.println("Работа программы завершена");
    }

    private static void calculatePrimeNumbersWith1Core() {
        PrimeNumberCheckerThread p1 = new PrimeNumberCheckerThread(start, secondFactor / 2 + 1, secondFactor);
        p1.start();

        try {
            p1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (primeNumberFlag.get()) {
            foundedSecondFactors.put(p, secondFactor);
        }
    }

    private static void calculatePrimeNumbersWithMultiCore() {
        for (int i = 1; i <= processors; i++) {
            if (i != processors) {
                primeNumberCheckerThreadList.add(new PrimeNumberCheckerThread(start, end, secondFactor));
            } else {
                primeNumberCheckerThreadList.add(new PrimeNumberCheckerThread(start, secondFactor / 2 + 1, secondFactor));
            }

            start = end;
            end = start + numbersPerProcessors;
        }

        for (PrimeNumberCheckerThread p1 : primeNumberCheckerThreadList) {
            p1.start();
        }

        for (PrimeNumberCheckerThread p1 : primeNumberCheckerThreadList) {
            try {
                p1.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (primeNumberFlag.get()) {
            foundedSecondFactors.put(p, secondFactor);
        }
    }

    private static void calculatePerfectNumbersWith1Core() {
        PerfectNumberCheckerThread p1 = new PerfectNumberCheckerThread(1, checkedNumber, checkedNumber);
        p1.start();

        try {
            p1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (counter.get() == checkedNumber) {
            System.out.printf("[%s] %s является совершенным числом\n", LocalDateTime.now().format(dTF), checkedNumber);
        }
    }

    private static void calculatePerfectNumbersWithMultiCore() {
        for (int i = 1; i <= processors; i++) {
            if (i != processors) {
                perfectNumberCheckerThreadList.add(new PerfectNumberCheckerThread(start, end, checkedNumber));
            } else {
                perfectNumberCheckerThreadList.add(new PerfectNumberCheckerThread(start, checkedNumber / 2 + 1, checkedNumber));
            }

            start = end;
            end = start + numbersPerProcessors;
        }

        for (PerfectNumberCheckerThread p1 : perfectNumberCheckerThreadList) {
            p1.start();
        }

        for (PerfectNumberCheckerThread p1 : perfectNumberCheckerThreadList) {
            try {
                p1.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (counter.get() == checkedNumber) {
            System.out.printf("[%s] %s является совершенным числом\n", LocalDateTime.now().format(dTF), checkedNumber);
        }
    }
}