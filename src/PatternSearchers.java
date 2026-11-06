import java.util.ArrayList;
import java.util.List;

public class PatternSearchers {

    public static long charComparisons = 0;
    public static long hashCollisions = 0;

    public static List<Integer> findNaive(String corpus, String needle) {
        charComparisons = 0;
        List<Integer> hits = new ArrayList<>();
        if (corpus == null || needle == null || needle.isEmpty()) return hits;

        int n = corpus.length();
        int m = needle.length();
        if (m > n) return hits;

        int limit = n - m;
        for (int shift = 0; shift <= limit; shift++) {
            int offset = 0;
            while (offset < m) {
                charComparisons++;
                if (corpus.charAt(shift + offset) != needle.charAt(offset)) {
                    break;
                }
                offset++;
            }
            if (offset == m) {
                hits.add(shift);
            }
        }
        return hits;
    }

    public static List<Integer> findKmp(String corpus, String needle) {
        charComparisons = 0;
        List<Integer> hits = new ArrayList<>();
        if (corpus == null || needle == null || needle.isEmpty()) return hits;

        int n = corpus.length();
        int m = needle.length();
        if (m > n) return hits;

        int[] borderTable = buildBorderArray(needle);
        int textIdx = 0;
        int patIdx = 0;

        while (textIdx < n) {
            charComparisons++;
            if (corpus.charAt(textIdx) == needle.charAt(patIdx)) {
                textIdx++;
                patIdx++;
                if (patIdx == m) {
                    hits.add(textIdx - m);
                    patIdx = borderTable[patIdx - 1];
                }
            } else {
                if (patIdx != 0) {
                    patIdx = borderTable[patIdx - 1];
                } else {
                    textIdx++;
                }
            }
        }
        return hits;
    }

    public static int[] buildBorderArray(String needle) {
        int m = needle.length();
        int[] border = new int[m];
        int prevLen = 0;
        int idx = 1;

        while (idx < m) {
            charComparisons++;
            if (needle.charAt(idx) == needle.charAt(prevLen)) {
                prevLen++;
                border[idx] = prevLen;
                idx++;
            } else {
                if (prevLen != 0) {
                    prevLen = border[prevLen - 1];
                } else {
                    border[idx] = 0;
                    idx++;
                }
            }
        }
        return border;
    }
}