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

}