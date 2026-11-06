public class AlignmentMetrics {

    public static long lastCellUpdates = 0;

    public static int computeLcsLength(String firstSeq, String secondSeq) {
        lastCellUpdates = 0;
        if (firstSeq == null || secondSeq == null) return 0;

        int rows = firstSeq.length();
        int cols = secondSeq.length();
        if (rows == 0 || cols == 0) return 0;

        int[][] grid = new int[rows + 1][cols + 1];

        for (int r = 1; r <= rows; r++) {
            char chA = firstSeq.charAt(r - 1);
            for (int c = 1; c <= cols; c++) {
                lastCellUpdates++;
                if (chA == secondSeq.charAt(c - 1)) {
                    grid[r][c] = grid[r - 1][c - 1] + 1;
                } else {
                    int fromTop = grid[r - 1][c];
                    int fromLeft = grid[r][c - 1];
                    grid[r][c] = (fromTop >= fromLeft) ? fromTop : fromLeft;
                }
            }
        }
        return grid[rows][cols];
    }

    public static int computeLevenshtein(String source, String target) {
        lastCellUpdates = 0;
        if (source == null || target == null) return 0;

        int srcLen = source.length();
        int tgtLen = target.length();

        int[][] costMatrix = new int[srcLen + 1][tgtLen + 1];

        for (int i = 0; i <= srcLen; i++) {
            costMatrix[i][0] = i;
        }
        for (int j = 0; j <= tgtLen; j++) {
            costMatrix[0][j] = j;
        }

        for (int i = 1; i <= srcLen; i++) {
            char sChar = source.charAt(i - 1);
            for (int j = 1; j <= tgtLen; j++) {
                lastCellUpdates++;
                char tChar = target.charAt(j - 1);
                if (sChar == tChar) {
                    costMatrix[i][j] = costMatrix[i - 1][j - 1];
                } else {
                    int del = costMatrix[i - 1][j];
                    int ins = costMatrix[i][j - 1];
                    int sub = costMatrix[i - 1][j - 1];
                    costMatrix[i][j] = 1 + Math.min(sub, Math.min(del, ins));
                }
            }
        }
        return costMatrix[srcLen][tgtLen];
    }
}