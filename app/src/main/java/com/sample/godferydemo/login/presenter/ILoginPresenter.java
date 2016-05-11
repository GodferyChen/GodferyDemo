package com.sample.godferydemo.login.presenter;

/**
 * Created by chen on 2016/3/12.
 */
public interface ILoginPresenter {

    void clear();

    void doLogin(String name, String passwd);

    void setProgressBarVisibility(int visibility);

}
