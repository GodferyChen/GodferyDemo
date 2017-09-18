package a.b.c.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import a.b.c.R;
import okhttp3.Call;

public class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getCanonicalName();
    protected Toolbar mToolbar;
    protected ActionBar mActionBar;
    protected Handler mHandler = new Handler();
    protected Activity mActivity;
    @Nullable
    protected Call mCall;
    protected boolean isAttachedToWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_MODE_OVERLAY);
        mActivity = this;
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCall();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
    }

    protected void cancelCall() {
        if (mCall != null) mCall.cancel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initToolbar() {
        immersedByToolbar();
        initStatusBarByToolbar();
        initActionBarByToolbar();
    }

    private void immersedByToolbar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 19) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= 21) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    private int initStatusBarByToolbar() {
        Resources res = getResources();
        int statusBarHeight = res.getDimensionPixelSize(res.getIdentifier("status_bar_height",
                "dimen", "android"));
        View statusBarPlaceholder = findViewById(R.id.header_v_place_holder);
        if (statusBarPlaceholder != null)
            statusBarPlaceholder.getLayoutParams().height = statusBarHeight;
        return statusBarHeight;
    }

    private ActionBar initActionBarByToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar
                    .DISPLAY_SHOW_TITLE);
            mActionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        }
        return mActionBar;
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        int[] attrs = {R.attr.colorPrimary};
        TypedArray array = obtainStyledAttributes(attrs);
        int colorPrimary = array.getColor(0, 0xFF000000);
        array.recycle();
        if (Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(colorPrimary);
        super.onSupportActionModeStarted(mode);
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        if (Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(Color.TRANSPARENT);
        super.onSupportActionModeFinished(mode);

    }
}
