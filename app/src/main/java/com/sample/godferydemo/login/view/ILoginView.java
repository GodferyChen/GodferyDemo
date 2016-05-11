package com.sample.godferydemo.login.view;

/**
 * Created by chen on 2016/3/12.
 */
public interface ILoginView {

    void onClearText();

    void onLoginResult(Boolean result, int code);

    void setProgressBarVisibility(int visibility);

}
