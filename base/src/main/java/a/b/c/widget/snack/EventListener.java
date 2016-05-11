package a.b.c.widget.snack;

/**
 * Interface used to notify of all {@link Snackbar} display events. Useful if you want
 * to move other views while the SnackBar is on screen.
 */
public interface EventListener {
    /**
     * Called when a {@link Snackbar} is about to enter the screen
     *
     * @param snackBar the {@link Snackbar} that's being shown
     */
    public void onShow(Snackbar snackBar);

    /**
     * Called when a {@link Snackbar} is about to enter the screen while
     * a {@link Snackbar} is about to exit the screen by replacement.
     *
     * @param snackBar the {@link Snackbar} that's being shown
     */
    public void onShowByReplace(Snackbar snackBar);

    /**
     * Called when a {@link Snackbar} is fully shown
     *
     * @param snackBar the {@link Snackbar} that's being shown
     */
    public void onShown(Snackbar snackBar);

    /**
     * Called when a {@link Snackbar} is about to exit the screen
     *
     * @param snackBar the {@link Snackbar} that's being dismissed
     */
    public void onDismiss(Snackbar snackBar);

    /**
     * Called when a {@link Snackbar} is about to exit the screen
     * when a new {@link Snackbar} is about to enter the screen.
     *
     * @param snackBar the {@link Snackbar} that's being dismissed
     */
    public void onDismissByReplace(Snackbar snackBar);

    /**
     * Called when a {@link Snackbar} had just been dismissed
     *
     * @param snackBar the {@link Snackbar} that's being dismissed
     */
    public void onDismissed(Snackbar snackBar);
}
