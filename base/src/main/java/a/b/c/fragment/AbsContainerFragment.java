package a.b.c.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import a.b.c.R;
import a.b.c.impl.Callback;

public abstract class AbsContainerFragment extends BaseFragment implements Callback {

    private boolean isDestroy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDestroy = false;
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return mRootView;
    }

    @Override
    public void showEmpty(int ivResId, @NonNull String tips) {
        if (!isDestroy()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, EmptyFragment.newInstance(ivResId, tips),
							EmptyFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showEmpty(@LayoutRes int layoutResId) {
        if (!isDestroy()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, EmptyFragment.newInstance(layoutResId),
							EmptyFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showLoading() {
        if (!isDestroy()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, LoadingFragment.newInstance(),
							LoadingFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showLoading(@LayoutRes int layoutResId) {
        if (!isDestroy()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, LoadingFragment.newInstance(layoutResId),
							LoadingFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void hideLoading() {
        if (!isDestroy()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, new Fragment(), "null")
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showReload() {
        if (!isDestroy()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, ReloadFragment.newInstance(), ReloadFragment
							.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showReload(@DrawableRes int ivResId, @NonNull String tips, @NonNull String action) {
        if (!isDestroy()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, ReloadFragment.newInstance(ivResId, tips,
							action), ReloadFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showReload(@LayoutRes int layoutResId) {
        if (!isDestroy()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerTopLayer, ReloadFragment.newInstance(layoutResId), ReloadFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void showContent(@NonNull Fragment fragment) {
        if (!isDestroy()) {
            Log.d(TAG, "showContent() called with: " + "fragment = [" + fragment + "]");
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @CheckResult
    @Override
    public boolean isDestroy() {
        return isDestroy;
    }
}
