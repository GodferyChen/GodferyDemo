package a.b.c.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import a.b.c.R;

public abstract class AbsRefreshFragment extends AbsContentFragment implements SwipeRefreshLayout
        .OnRefreshListener {

    protected
    @Nullable
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean initRefresh = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {

        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipeRefreshLayout);
        int[] attrs = {R.attr.colorPrimary};
        TypedArray array = mActivity.obtainStyledAttributes(attrs);
        int colorPrimary = array.getColor(0, 0xFF000000);
        array.recycle();
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeColors(colorPrimary);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (initRefresh && mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                }
            });
        }
    }

    protected void unInitRefresh() {
        initRefresh = false;
    }

    protected void refreshComplete() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
