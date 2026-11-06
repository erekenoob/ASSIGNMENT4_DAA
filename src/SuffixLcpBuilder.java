import java.util.Arrays;

public class SuffixLcpBuilder {

    public static long lcpCharChecks = 0;


    public static int[] generateSuffixArray(String text) {
        int n = text.length();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, (a, b) -> {
            int idx1 = a, idx2 = b;
            while (idx1 < n && idx2 < n) {
                char c1 = text.charAt(idx1);
                char c2 = text.charAt(idx2);
                if (c1 != c2) return Character.compare(c1, c2);
                idx1++;
                idx2++;
            }
            return Integer.compare(n - a, n - b);
        });

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = order[i];
        }
        return sa;
    }
}