package a.b.c.widget.snack;

/**
 * This adapter class provides empty implementations of the methods from {@link EventListener}.
 * If you are only interested in a subset of the interface methods you can extend this class an override only the methods you need.
 */
public abstract class EventListenerAdapter implements EventListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onShow(Snackbar snackBar) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onShowByReplace(Snackbar snackBar) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onShown(Snackbar snackBar) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDismiss(Snackbar snackBar) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDismissByReplace(Snackbar snackBar) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDismissed(Snackbar snackBar) {

    }
}
