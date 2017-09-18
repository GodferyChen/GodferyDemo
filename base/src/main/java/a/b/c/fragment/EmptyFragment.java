package a.b.c.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import a.b.c.R;

public class EmptyFragment extends BaseFragment {

    public final static String ARG_IMAGE = "ARG_IMAGE";
    public final static String ARG_TIPS = "ARG_TIPS";
    public final static String ARG_LAYOUT_RES_ID = "ARG_LAYOUT_RES_ID";

    public static EmptyFragment newInstance(@DrawableRes int ivResId, @NonNull String tips) {

        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE, ivResId);
        args.putString(ARG_TIPS, tips);
        EmptyFragment fragment = new EmptyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EmptyFragment newInstance(@LayoutRes int layoutResId) {

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        EmptyFragment fragment = new EmptyFragment();
        fragment.setArguments(args);
        return fragment;
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
            mRootView = inflater.inflate(R.layout.layout_empty, container, false);
            ImageView ivEmpty = (ImageView) mRootView.findViewById(R.id.iv_empty);
            TextView tvEmptyTips = (TextView) mRootView.findViewById(R.id.tv_empty_tips);
            int ivResId = args.getInt(ARG_IMAGE, 0);
            String tips = args.getString(ARG_TIPS);
            ivEmpty.setImageResource(ivResId);
            tvEmptyTips.setText(tips);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
