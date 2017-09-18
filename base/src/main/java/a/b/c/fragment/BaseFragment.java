package a.b.c.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import okhttp3.Call;

public class BaseFragment extends Fragment {

    protected String TAG = this.getClass().getCanonicalName();
    protected Handler mHandler = new Handler();
    protected Activity mActivity;
    protected View mRootView;
    @Nullable
    protected Call mCall;
    protected boolean isAttachedToWindow;

    @CallSuper
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        return mRootView;
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCall();
    }

    protected void cancelCall() {
        if (mCall != null) mCall.cancel();
    }

    public void onAttachedToWindow() {
        isAttachedToWindow = true;
    }

    public void onDetachedFromWindow() {
        isAttachedToWindow = false;
    }
}
