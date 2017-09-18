package a.b.c.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import a.b.c.impl.Callback;

public abstract class AbsContentFragment extends BaseFragment {

    protected
    @Nullable
    Callback mCallback;

    @CallSuper
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        }
    }

    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragment = getParentFragment();
        if (fragment instanceof Callback) {
            mCallback = (Callback) fragment;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {

        showLoading();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCall != null) mCall.cancel();
    }

    @CallSuper
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showEmpty(@DrawableRes int ivResId, @NonNull String tips) {
        if (mCallback != null) {
            mCallback.showEmpty(ivResId, tips);
        }
    }

    protected void showEmpty(@LayoutRes int layoutResId) {
        if (mCallback != null) {
            mCallback.showEmpty(layoutResId);
        }
    }

    protected void showLoading() {
        if (mCallback != null) {
            mCallback.showLoading();
        }
    }

    protected void showLoading(@LayoutRes int layoutResId) {
        if (mCallback != null) {
            mCallback.showLoading(layoutResId);
        }
    }

    protected void hideLoading() {
        if (mCallback != null) {
            mCallback.hideLoading();
        }
    }

    protected void showReload() {
        if (mCallback != null) {
            mCallback.showReload();
        }
    }

    protected void showReload(@DrawableRes int ivResId, @NonNull String tips, @NonNull String action) {
        if (mCallback != null) {
            mCallback.showReload(ivResId, tips, action);
        }
    }

    protected void showReload(@LayoutRes int layoutResId) {
        if (mCallback != null) {
            mCallback.showReload(layoutResId);
        }
    }

    protected void showContent(@NonNull Fragment fragment) {
        if (mCallback != null) {
            mCallback.showContent(fragment);
        }
    }
}
