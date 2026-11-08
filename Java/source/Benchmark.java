
import java.util.*;

public class Benchmark {

    static volatile Object blackhole;


    interface Matcher {
        Map<String, Object> run(Map<String, Object> input);
    }

    static class Algorithm {
        String name;
        Matcher matcher;

        Algorithm(String name, Matcher matcher) {
            this.name = name;
            this.matcher = matcher;
        }
    }

    static final Algorithm[] ALGORITHMS = {
            new Algorithm("Naive", Algorithms::naive),
            new Algorithm("KMP", Algorithms::kmp),
            new Algorithm("RK", Algorithms::rk)
    };

    static final int WARMUP_RUNS = 30;
    static final int MEASURED_RUNS = 5;
    static final int REPETITIONS = 100;

    static String randomString(Random random, int length) {
        StringBuilder result = new StringBuilder(length);

        // Printable ASCII characters: 32..126
        for (int i = 0; i < length; i++) {
            result.append((char) (32 + random.nextInt(95)));
        }

        return result.toString();
    }

    static Map<String, Object> makeInput(String text, String pattern) {
        Map<String, Object> input = new HashMap<>();
        input.put("text", text);
        input.put("pattern", pattern);
        return input;
    }

    static double median(double[] values) {
        double[] copy = values.clone();
        Arrays.sort(copy);

        int middle = copy.length / 2;

        if (copy.length % 2 == 0) {
            return (copy[middle - 1] + copy[middle]) / 2.0;
        }

        return copy[middle];
    }

    static void benchmarkCase(
            String type,
            int n,
            int m,
            String text,
            String pattern) {

        Map<String, Object> input = makeInput(text, pattern);

        System.out.println();
        System.out.println("Case: " + type
                + ", n=" + n + ", m=" + m);

        // 1. Check that all algorithms return identical results.
        Map<String, Object> expected = ALGORITHMS[0].matcher.run(input);

        for (int i = 1; i < ALGORITHMS.length; i++) {
            Map<String, Object> actual = ALGORITHMS[i].matcher.run(input);

            if (!expected.equals(actual)) {
                throw new IllegalStateException(
                        "Different results: "
                                + ALGORITHMS[0].name + " vs "
                                + ALGORITHMS[i].name);
            }
        }

        System.out.println("Correctness check: PASS");

        // 2. Warm up the JVM.
        for (Algorithm algorithm : ALGORITHMS) {
            for (int i = 0; i < WARMUP_RUNS; i++) {
                blackhole = algorithm.matcher.run(input);
            }
        }

        // 3. Measure each algorithm.
        for (Algorithm algorithm : ALGORITHMS) {

            double[] measurements = new double[MEASURED_RUNS];

            for (int run = 0; run < MEASURED_RUNS; run++) {

                Object lastResult = null;

                long start = System.nanoTime();

                for (int repetition = 0;
                     repetition < REPETITIONS;
                     repetition++) {

                    lastResult = algorithm.matcher.run(input);
                }

                long end = System.nanoTime();
                blackhole = lastResult;


                // Average time per algorithm call, in milliseconds.
                measurements[run] =
                        (end - start)
                                / (double) REPETITIONS
                                / 1_000_000.0;
            }

            System.out.printf(
                    Locale.US,
                    "%s: median = %.6f ms/op | runs = ",
                    algorithm.name,
                    median(measurements));

            for (int i = 0; i < measurements.length; i++) {
                System.out.printf(
                        Locale.US,
                        "%.6f%s",
                        measurements[i],
                        i + 1 == measurements.length ? "" : ", ");
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        System.out.println("=== String Matching Benchmark ===");
        System.out.println("Java version: "
                + System.getProperty("java.version"));
        System.out.println("OS: "
                + System.getProperty("os.name"));
        System.out.println("Architecture: "
                + System.getProperty("os.arch"));
        System.out.println("Available processors: "
                + Runtime.getRuntime().availableProcessors());

        System.out.println("Warm-up calls per algorithm: "
                + WARMUP_RUNS);
        System.out.println("Measured batches: " + MEASURED_RUNS);
        System.out.println("Calls per measured batch: "
                + REPETITIONS);

        int[][] sizes = {
                {500, 50},
                {1500, 100},
                {3000, 200}
        };

        Random random = new Random(42);

        for (int[] size : sizes) {
            int n = size[0];
            int m = size[1];

            // Random input.
            String randomText = randomString(random, n);
            String randomPattern = randomString(random, m);

            benchmarkCase(
                    "Random",
                    n,
                    m,
                    randomText,
                    randomPattern
            );

            // Repetitive input:
            // Text:    aaaaaaaaaaaaa...
            // Pattern: aaaaaaaaa...ab
            //
            // This makes Naive perform many comparisons
            // before discovering a mismatch.
            String repetitiveText = "a".repeat(n);
            String repetitivePattern =
                    "a".repeat(m - 1) + "b";

            benchmarkCase(
                    "Repetitive",
                    n,
                    m,
                    repetitiveText,
                    repetitivePattern
            );
        }

        System.out.println();
        System.out.println("=== Benchmark completed ===");
    }
}
