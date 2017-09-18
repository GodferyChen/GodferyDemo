package a.b.c.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import a.b.c.R;
import a.b.c.impl.Callback;

public class ReloadFragment extends BaseFragment implements View.OnClickListener {

    public final static String ARG_IMAGE = "ARG_IMAGE";
    public final static String ARG_TIPS = "ARG_TIPS";
    public final static String ARG_ACTION = "ARG_ACTION";
    public final static String ARG_LAYOUT_RES_ID = "ARG_LAYOUT_RES_ID";

    private Callback mCallback;

    public static ReloadFragment newInstance() {
        return newInstance(R.mipmap.default_wifi, "数据加载失败，点击下面按钮重新加载", "重新加载");
    }

    public static ReloadFragment newInstance(@DrawableRes int ivResId, @NonNull String tips,
                                             @NonNull String action) {
        ReloadFragment fragment = new ReloadFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE, ivResId);
        args.putString(ARG_TIPS, tips);
        args.putString(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    public static ReloadFragment newInstance(@LayoutRes int layoutResId) {

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        ReloadFragment fragment = new ReloadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        }
    }

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

        Bundle args = getArguments();
        int layoutResId = args.getInt(ARG_LAYOUT_RES_ID, 0);
        if ((layoutResId >>> 24) >= 2) {
            mRootView = inflater.inflate(layoutResId, container, false);
        } else {
            mRootView = inflater.inflate(R.layout.layout_reload, container, false);
            ImageView ivReload = (ImageView) mRootView.findViewById(R.id.iv_reload);
            TextView tvReloadTips = (TextView) mRootView.findViewById(R.id.tv_reload_tips);
            TextView tvReload = (TextView) mRootView.findViewById(R.id.tv_reload);
            int ivResId = args.getInt(ARG_IMAGE, 0);
            String tips = args.getString(ARG_TIPS);
            String action = args.getString(ARG_ACTION);
            ivReload.setImageResource(ivResId);
            tvReloadTips.setText(tips);
            tvReload.setText(action);
            tvReload.setOnClickListener(this);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (mCallback != null && !mCallback.isDestroy()) {
            mCallback.reload();
        }
    }
}
