package com.sample.godferydemo.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.sample.godferydemo.R;
import com.sample.godferydemo.login.presenter.ILoginPresenter;
import com.sample.godferydemo.login.presenter.LoginPresenterCompl;
import com.sample.godferydemo.login.view.ILoginView;

import a.b.c.activity.BaseActivity;
import a.b.c.manager.LogManager;

/**
 * Created by chen on 2016/3/12.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, ILoginView {

    private EditText editUser, editPasswd;
    private Button btnLogin, btnClear;
    private ProgressBar progressBar;
    ILoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUser = (EditText) findViewById(R.id.et_username);
        editPasswd = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnClear = (Button) findViewById(R.id.btn_clear);
        progressBar = (ProgressBar) findViewById(R.id.progress_login);

        btnLogin.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        loginPresenter = new LoginPresenterCompl(mActivity,this);
        loginPresenter.setProgressBarVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                loginPresenter.setProgressBarVisibility(View.VISIBLE);
                btnClear.setEnabled(false);
                btnLogin.setEnabled(false);
                loginPresenter.doLogin(editUser.getText().toString(),editPasswd.getText().toString());
                break;
            case R.id.btn_clear:
                loginPresenter.clear();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClearText() {
        editUser.setText("");
        editPasswd.setText("");
    }

    @Override
    public void onLoginResult(Boolean result, int code) {
        loginPresenter.setProgressBarVisibility(View.INVISIBLE);
        btnLogin.setEnabled(true);
        btnClear.setEnabled(true);
        if(result){
            LogManager.tS(this,"Login Success");
        }else {
            LogManager.tS(this,"Login Fail, code = " + code);
        }
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }
}
