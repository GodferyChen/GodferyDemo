package a.b.c.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import a.b.c.R;
import a.b.c.fragment.EmptyFragment;
import a.b.c.fragment.LoadingFragment;
import a.b.c.fragment.ReloadFragment;
import a.b.c.impl.Callback;

public abstract class AbsContainerActivity extends BaseActivity implements Callback {

    private boolean isDestroy;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDestroy = false;
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    @CheckResult
    @Override
    public boolean isDestroy() {
        return isDestroy;
    }

    @Override
    public void showEmpty(int ivResId, @NonNull String tips) {
        if (!isDestroy()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, EmptyFragment.newInstance(ivResId, tips),
                            EmptyFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showEmpty(@LayoutRes int layoutResId) {
        if (!isDestroy()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, EmptyFragment.newInstance(layoutResId),
							EmptyFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showLoading() {
        if (!isDestroy()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, LoadingFragment.newInstance(),
							LoadingFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showLoading(@LayoutRes int layoutResId) {
        if (!isDestroy()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, LoadingFragment.newInstance(layoutResId),
							LoadingFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void hideLoading() {
        if (!isDestroy()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, new Fragment(), "null")
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showReload() {
        if (!isDestroy()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, ReloadFragment.newInstance(), ReloadFragment
							.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showReload(@DrawableRes int ivResId, @NonNull String tips, @NonNull String action) {
        if (!isDestroy()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, ReloadFragment.newInstance(ivResId, tips,
							action), ReloadFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showReload(@LayoutRes int layoutResId) {
        if (!isDestroy()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, ReloadFragment.newInstance(layoutResId), ReloadFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showContent(@NonNull Fragment fragment) {
        if (!isDestroy()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }
}