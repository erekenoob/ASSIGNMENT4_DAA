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


        System.out.println("ALL TESTS PASSED SUCCESSFULLY!\n");
    }



    private static void check(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError("Test failed: " + label);
        }
        System.out.println("[PASS] " + label);
    }
}