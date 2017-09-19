package a.b.c.impl;

import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

public interface Callback {

    void showEmpty(@DrawableRes int ivResId, @NonNull String tips);

    void showEmpty(@LayoutRes int layoutResId);

    void showLoading();

    void showLoading(@LayoutRes int layoutResId);

    void hideLoading();

    void showReload();

    void showReload(@DrawableRes int ivResId, @NonNull String tips, @NonNull String action);

    void showReload(@LayoutRes int layoutResId);

    void reload();

    void showContent(@NonNull Fragment fragment);

    @CheckResult
    boolean isDestroy();
}
