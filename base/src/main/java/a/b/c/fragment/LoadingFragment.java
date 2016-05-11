package a.b.c.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import a.b.c.R;

public class LoadingFragment extends BaseFragment {

	public final static String ARG_IMAGE = "ARG_IMAGE";
	public final static String ARG_TIPS = "ARG_TIPS";
	public final static String ARG_LAYOUT_RES_ID = "ARG_LAYOUT_RES_ID";

	public static LoadingFragment newInstance() {

		Bundle args = new Bundle();

		LoadingFragment fragment = new LoadingFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public static LoadingFragment newInstance(@LayoutRes int layoutResId) {

		Bundle args = new Bundle();
		args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
		LoadingFragment fragment = new LoadingFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		Bundle args = getArguments();
		int layoutResId = args.getInt(ARG_LAYOUT_RES_ID, 0);
		if ((layoutResId >>> 24) >= 2) {
			mRootView = inflater.inflate(layoutResId, container, false);
		} else {
			mRootView = inflater.inflate(R.layout.layout_loading, container, false);
		}

		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
