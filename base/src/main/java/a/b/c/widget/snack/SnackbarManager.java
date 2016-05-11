package a.b.c.widget.snack;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

/**
 * A handler for multiple {@link Snackbar}s
 */
public class SnackbarManager {

    private static final String TAG = SnackbarManager.class.getSimpleName();
    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    private static WeakReference<Snackbar> snackbarReference;

    private SnackbarManager() {
    }

    /**
     * Displays a {@link Snackbar} in the current {@link Activity}, dismissing
     * the current SnackBar being displayed, if any. Note that the Activity will be obtained from
     * the SnackBar's {@link android.content.Context}. If the SnackBar was created with
     * {@link Activity#getApplicationContext()} then you must explicitly pass the target
     * Activity using {@link #show(Snackbar, Activity)}
     *
     * @param snackBar instance of {@link Snackbar} to display
     */
    public static void show(@NonNull Snackbar snackBar) {
        try {
            show(snackBar, (Activity) snackBar.getContext());
        } catch (ClassCastException e) {
            Log.e(TAG, "Couldn't get Activity from the SnackBar's Context. Try calling " +
                    "#show(SnackBar, Activity) instead", e);
        }
    }

    /**
     * Displays a {@link Snackbar} in the current {@link Activity}, dismissing
     * the current SnackBar being displayed, if any
     *
     * @param snackBar instance of {@link Snackbar} to display
     * @param activity target {@link Activity} to display the SnackBar
     */
    public static void show(@NonNull final Snackbar snackBar, @NonNull final Activity activity) {
        MAIN_THREAD.post(new Runnable() {
            @Override
            public void run() {
                Snackbar currentSnackBar = getCurrentSnackBar();
                if (currentSnackBar != null) {
                    if (currentSnackBar.isShowing() && !currentSnackBar.isDimissing()) {
                        currentSnackBar.dismissAnimation(false);
                        currentSnackBar.dismissByReplace();
                        snackbarReference = new WeakReference<>(snackBar);
                        snackBar.showAnimation(false);
                        snackBar.showByReplace(activity);
                        return;
                    }
                    currentSnackBar.dismiss();
                }
                snackbarReference = new WeakReference<>(snackBar);
                snackBar.show(activity);
            }
        });
    }

    /**
     * Displays a {@link Snackbar} in the specified {@link ViewGroup}, dismissing
     * the current SnackBar being displayed, if any
     *
     * @param snackBar instance of {@link Snackbar} to display
     * @param parent   parent {@link ViewGroup} to display the SnackBar
     */
    public static void show(@NonNull Snackbar snackBar, @NonNull ViewGroup parent) {
        show(snackBar, parent, Snackbar.shouldUsePhoneLayout(snackBar.getContext()));
    }

    /**
     * Displays a {@link Snackbar} in the specified {@link ViewGroup}, dismissing
     * the current SnackBar being displayed, if any
     *
     * @param snackBar       instance of {@link Snackbar} to display
     * @param parent         parent {@link ViewGroup} to display the SnackBar
     * @param usePhoneLayout true: use phone layout, false: use tablet layout
     */
    public static void show(@NonNull final Snackbar snackBar, @NonNull final ViewGroup parent,
                            final boolean usePhoneLayout) {
        MAIN_THREAD.post(new Runnable() {
            @Override
            public void run() {
                Snackbar currentSnackBar = getCurrentSnackBar();
                if (currentSnackBar != null) {
                    if (currentSnackBar.isShowing() && !currentSnackBar.isDimissing()) {
                        currentSnackBar.dismissAnimation(false);
                        currentSnackBar.dismissByReplace();
                        snackbarReference = new WeakReference<>(snackBar);
                        snackBar.showAnimation(false);
                        snackBar.showByReplace(parent, usePhoneLayout);
                        return;
                    }
                    currentSnackBar.dismiss();
                }
                snackbarReference = new WeakReference<>(snackBar);
                snackBar.show(parent, usePhoneLayout);
            }
        });
    }

    /**
     * Dismisses the {@link Snackbar} shown by this manager.
     */
    public static void dismiss() {
        final Snackbar currentSnackBar = getCurrentSnackBar();
        if (currentSnackBar != null) {
            MAIN_THREAD.post(new Runnable() {
                @Override
                public void run() {
                    currentSnackBar.dismiss();
                }
            });
        }
    }

    /**
     * Return the current SnackBar
     */
    public static Snackbar getCurrentSnackBar() {
        if (snackbarReference != null) {
            return snackbarReference.get();
        }
        return null;
    }
}
