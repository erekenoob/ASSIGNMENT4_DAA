import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValidationSuite {

    public static void main(String[] args) {
        runAllChecks();
    }

    public static void runAllChecks() {
        System.out.println("=== Running Correctness & Edge-Case Tests ===");

        // 1 LCS tests
        check(AlignmentMetrics.computeLcsLength("AGGTAB", "GXTXAYB") == 4, "LCS standard case");
        check(AlignmentMetrics.computeLcsLength("ALGORITHMS", "ALGORITHMS") == 10, "LCS identical strings");
        check(AlignmentMetrics.computeLcsLength("AAAA", "BBBB") == 0, "LCS completely different strings");
        check(AlignmentMetrics.computeLcsLength("", "NONEMPTY") == 0, "LCS with empty string");

        // 2 Edit Distance tests
        check(AlignmentMetrics.computeLevenshtein("KITTEN", "SITTING") == 3, "Edit Distance substitution/insertion");
        check(AlignmentMetrics.computeLevenshtein("SATURDAY", "SUNDAY") == 3, "Edit Distance deletion/substitution");
        check(AlignmentMetrics.computeLevenshtein("EXACT", "EXACT") == 0, "Edit Distance identical strings");
        check(AlignmentMetrics.computeLevenshtein("", "TARGET") == 6, "Edit Distance first empty");
        check(AlignmentMetrics.computeLevenshtein("SOURCE", "") == 6, "Edit Distance second empty");

        // 3 String Matching tests (verifying Naive == KMP == Rabin-Karp)
        verifySearch("ABABDABACDABABCABAB", "ABAB", Arrays.asList(0, 10, 15), "Pattern at start, middle, end & multiple");
        verifySearch("HELLOWORLD", "HELLO", Collections.singletonList(0), "Pattern at beginning");
        verifySearch("HELLOWORLD", "OWO", Collections.singletonList(4), "Pattern in middle");
        verifySearch("HELLOWORLD", "WORLD", Collections.singletonList(5), "Pattern at end");
        verifySearch("AAAAA", "AA", Arrays.asList(0, 1, 2, 3), "Overlapping multiple occurrences");
        verifySearch("ABCDEFGHIJ", "XYZ", Collections.emptyList(), "Pattern absent");
        verifySearch("SHORT", "LONGERPATTERN", Collections.emptyList(), "Pattern longer than text");
        verifySearch("ANYTEXT", "", Collections.emptyList(), "Empty pattern handling");

        // 4 Kasai's LCP test on manually verified string "banana"
        // Suffixes sorted
        // 5 "a"
        // 3 "ana"
        // 1 "anana"
        // 0 "banana"
        // 4 "na"
        // 2 "nana"
        String sample = "banana";
        int[] knownSa = {5, 3, 1, 0, 4, 2};
        int[] expectedLcp = {1, 3, 0, 0, 2, 0};
        int[] builtSa = SuffixLcpBuilder.generateSuffixArray(sample);
        int[] actualLcp = SuffixLcpBuilder.buildKasaiLcp(sample, knownSa);

        check(Arrays.equals(builtSa, knownSa), "Suffix Array matches manual array for 'banana'");
        check(Arrays.equals(actualLcp, expectedLcp), "Kasai LCP matches manual array [1, 3, 0, 0, 2, 0] for 'banana'");

        System.out.println("ALL TESTS PASSED SUCCESSFULLY!\n");
    }

    private static void verifySearch(String text, String pat, List<Integer> expected, String label) {
        List<Integer> resNaive = PatternSearchers.findNaive(text, pat);
        List<Integer> resKmp = PatternSearchers.findKmp(text, pat);
        List<Integer> resRk = PatternSearchers.findRabinKarp(text, pat);

        boolean same = resNaive.equals(resKmp) && resKmp.equals(resRk) && resNaive.equals(expected);
        if (!same) {
            throw new AssertionError("Mismatch in test [" + label + "]: Naive=" + resNaive
                    + ", KMP=" + resKmp + ", RK=" + resRk + ", Expected=" + expected);
        }
        System.out.println("[PASS] String Matching: " + label + " -> " + resNaive);
    }

    private static void check(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError("Test failed: " + label);
        }
        System.out.println("[PASS] " + label);
    }
}