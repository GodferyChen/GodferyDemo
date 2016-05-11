package a.b.c.widget.snack;

public enum SnackbarType {

    /**
     * SnackBar with a single line
     */
    SINGLE_LINE(48, 48, 1),
    /**
     * SnackBar with two lines
     */
    MULTI_LINE(48, 80, 2);

    private int minHeight;
    private int maxHeight;
    private int maxLines;

    SnackbarType(int minHeight, int maxHeight, int maxLines) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.maxLines = maxLines;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMaxLines() {
        return maxLines;
    }
}
