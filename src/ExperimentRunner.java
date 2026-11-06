import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Random;

public class ExperimentRunner {

    private static final String ALPHABET = "ACGT";
    private static final int TRIALS = 5;
    private static final long SEED = 42L;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        ValidationSuite.runAllChecks();

        StringBuilder csv = new StringBuilder();
        csv.append("Category,Algorithm,Scenario,InputSize,AvgTimeNs,AvgTimeMs,OperationCount\n");

        warmupJvm();

        runDpExperiments(csv);
        runStringMatchingExperiments(csv);
        runKasaiExperiments(csv);

        saveCsv(csv.toString(), "results/tables/benchmark_summary.csv");
    }

    private static void warmupJvm() {
        Random rng = new Random(SEED);
        String s1 = randomString(rng, 200);
        String s2 = randomString(rng, 200);
        for (int i = 0; i < 3; i++) {
            AlignmentMetrics.computeLcsLength(s1, s2);
            AlignmentMetrics.computeLevenshtein(s1, s2);
            PatternSearchers.findNaive(s1, "ACGT");
            PatternSearchers.findKmp(s1, "ACGT");
            PatternSearchers.findRabinKarp(s1, "ACGT");
            int[] sa = SuffixLcpBuilder.generateSuffixArray(s1);
            SuffixLcpBuilder.buildKasaiLcp(s1, sa);
        }
    }

    private static void runDpExperiments(StringBuilder csv) {
        int[] sizes = {100, 500, 1000};
        System.out.println("=== Part A: Dynamic Programming (LCS & Edit Distance) ===");
        System.out.printf("%-15s | %-8s | %-14s | %-12s | %-15s%n",
                "Algorithm", "Size (n)", "Avg Time (ns)", "Avg Time (ms)", "Cell Updates");
        System.out.println("---------------------------------------------------------------------------");

        for (int n : sizes) {
            Random rng = new Random(SEED + n);
            String strA = randomString(rng, n);
            String strB = randomString(rng, n);

            // LCS
            long totalLcsTime = 0;
            long lcsOps = 0;
            for (int t = 0; t < TRIALS; t++) {
                long start = System.nanoTime();
                int res = AlignmentMetrics.computeLcsLength(strA, strB);
                long end = System.nanoTime();
                if (res < 0) System.out.print("");
                totalLcsTime += (end - start);
                lcsOps = AlignmentMetrics.lastCellUpdates;
            }
            long avgLcsNs = totalLcsTime / TRIALS;
            double avgLcsMs = avgLcsNs / 1_000_000.0;
            System.out.printf("%-15s | %-8d | %-14d | %-12.4f | %-15d%n",
                    "LCS", n, avgLcsNs, avgLcsMs, lcsOps);
            csv.append(String.format("DP,LCS,RandomPairs,%d,%d,%.4f,%d%n", n, avgLcsNs, avgLcsMs, lcsOps));

            // Edit Distance
            long totalEdTime = 0;
            long edOps = 0;
            for (int t = 0; t < TRIALS; t++) {
                long start = System.nanoTime();
                int res = AlignmentMetrics.computeLevenshtein(strA, strB);
                long end = System.nanoTime();
                if (res < 0) System.out.print("");
                totalEdTime += (end - start);
                edOps = AlignmentMetrics.lastCellUpdates;
            }
            long avgEdNs = totalEdTime / TRIALS;
            double avgEdMs = avgEdNs / 1_000_000.0;
            System.out.printf("%-15s | %-8d | %-14d | %-12.4f | %-15d%n",
                    "Edit Distance", n, avgEdNs, avgEdMs, edOps);
            csv.append(String.format("DP,EditDistance,RandomPairs,%d,%d,%.4f,%d%n", n, avgEdNs, avgEdMs, edOps));
        }
        System.out.println();
    }

    private static void runStringMatchingExperiments(StringBuilder csv) {
        int[] textLengths = {10000, 50000, 100000};
        int patternLength = 12;

        System.out.println("=== Part B: String Matching (Naive, KMP, Rabin-Karp) ===");
        System.out.printf("%-12s | %-10s | %-8s | %-14s | %-12s | %-15s%n",
                "Algorithm", "Scenario", "Text (n)", "Avg Time (ns)", "Avg Time (ms)", "Char Compares");
        System.out.println("----------------------------------------------------------------------------------------");

        for (int n : textLengths) {
            Random rng = new Random(SEED + n);
            String text = randomString(rng, n);

            String presentPattern = text.substring(n / 2, n / 2 + patternLength);
            benchmarkMatchers(text, presentPattern, "Present", n, csv);

            String absentPattern = presentPattern.substring(0, patternLength - 1) + "Z";
            benchmarkMatchers(text, absentPattern, "Absent", n, csv);
        }
        System.out.println();
    }

    private static void benchmarkMatchers(String text, String pattern, String scenario, int n, StringBuilder csv) {
        String[] names = {"Naive", "KMP", "Rabin-Karp"};
        for (int alg = 0; alg < 3; alg++) {
            long totalTime = 0;
            long compares = 0;
            for (int t = 0; t < TRIALS; t++) {
                long start = System.nanoTime();
                int count;
                if (alg == 0) {
                    count = PatternSearchers.findNaive(text, pattern).size();
                } else if (alg == 1) {
                    count = PatternSearchers.findKmp(text, pattern).size();
                } else {
                    count = PatternSearchers.findRabinKarp(text, pattern).size();
                }
                long end = System.nanoTime();
                if (count < 0) System.out.print("");
                totalTime += (end - start);
                compares = PatternSearchers.charComparisons;
            }
            long avgNs = totalTime / TRIALS;
            double avgMs = avgNs / 1_000_000.0;
            System.out.printf("%-12s | %-10s | %-8d | %-14d | %-12.4f | %-15d%n",
                    names[alg], scenario, n, avgNs, avgMs, compares);
            csv.append(String.format("StringMatching,%s,%s,%d,%d,%.4f,%d%n",
                    names[alg], scenario, n, avgNs, avgMs, compares));
        }
    }

    private static void runKasaiExperiments(StringBuilder csv) {
        int[] sizes = {1000, 5000, 10000};
        System.out.println("=== Part C: Kasai's LCP Algorithm ===");
        System.out.printf("%-15s | %-8s | %-14s | %-12s | %-15s%n",
                "Algorithm", "Size (n)", "Avg Time (ns)", "Avg Time (ms)", "Char Checks");
        System.out.println("---------------------------------------------------------------------------");

        for (int n : sizes) {
            Random rng = new Random(SEED + n);
            String text = randomString(rng, n);
            int[] sa = SuffixLcpBuilder.generateSuffixArray(text);

            long totalTime = 0;
            long checks = 0;
            for (int t = 0; t < TRIALS; t++) {
                long start = System.nanoTime();
                int[] lcp = SuffixLcpBuilder.buildKasaiLcp(text, sa);
                long end = System.nanoTime();
                if (lcp.length < 0) System.out.print("");
                totalTime += (end - start);
                checks = SuffixLcpBuilder.lcpCharChecks;
            }
            long avgNs = totalTime / TRIALS;
            double avgMs = avgNs / 1_000_000.0;
            System.out.printf("%-15s | %-8d | %-14d | %-12.4f | %-15d%n",
                    "Kasai LCP", n, avgNs, avgMs, checks);
            csv.append(String.format("AdvancedString,KasaiLCP,RandomDNA,%d,%d,%.4f,%d%n",
                    n, avgNs, avgMs, checks));
        }
        System.out.println();
    }

    private static String randomString(Random rng, int length) {
        char[] buf = new char[length];
        int alphaLen = ALPHABET.length();
        for (int i = 0; i < length; i++) {
            buf[i] = ALPHABET.charAt(rng.nextInt(alphaLen));
        }
        return new String(buf);
    }

    private static void saveCsv(String content, String filePath) {
        try {
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                out.print(content);
            }
            System.out.println("Saved experimental tables to: " + filePath);
        } catch (IOException e) {
            System.err.println("Could not write CSV file: " + e.getMessage());
        }
    }
}