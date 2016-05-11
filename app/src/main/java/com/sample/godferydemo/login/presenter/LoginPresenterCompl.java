package com.sample.godferydemo.login.presenter;

import android.app.Activity;

import com.sample.godferydemo.login.model.IUser;
import com.sample.godferydemo.login.model.UserModel;
import com.sample.godferydemo.login.view.ILoginView;

/**
 * Created by chen on 2016/3/12.
 */
public class LoginPresenterCompl implements ILoginPresenter{

    ILoginView iLoginView;
    IUser iUser;

    public LoginPresenterCompl(Activity activity, ILoginView iLoginView){
        this.iLoginView = iLoginView;
        iUser = new UserModel(activity,"mvp","mvp");
    }

    @Override
    public void clear() {
        iLoginView.onClearText();
    }

    @Override
    public void doLogin(String name, String passwd) {
        boolean isLoginSuccess = true;
        int code = iUser.checkUserValidity(name, passwd);
        if(code != 0) isLoginSuccess = false;
        boolean result = isLoginSuccess;
        iLoginView.onLoginResult(result,code);
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        iLoginView.setProgressBarVisibility(visibility);
    }
}
