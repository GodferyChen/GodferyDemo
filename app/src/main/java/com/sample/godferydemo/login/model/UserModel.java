package com.sample.godferydemo.login.model;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.sample.godferydemo.R;

import org.json.JSONObject;

import a.b.c.manager.LogManager;
import a.b.c.manager.OkHttpManager;
import a.b.c.manager.UrlManager;
import okhttp3.Call;

/**
 * Created by chen on 2016/3/12.
 */
public class UserModel implements IUser {

    String name;
    String passwd;
    Activity mActivity;

    public UserModel(Activity activity, String name, String passwd) {
        this.name = name;
        this.passwd = passwd;
        this.mActivity = activity;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPasswd() {
        return passwd;
    }

    @Override
    public int checkUserValidity(String name, String passwd) {
//        if (name == null || passwd == null && !name.equals(getName()) || !passwd.equals(getPasswd())) {
//            return -1;
//        }
        if (TextUtils.isEmpty(name)) {
            return -1;
        }
        if (TextUtils.isEmpty(passwd)) {
            return -1;
        }
        Call mCall = UrlManager.okHttp(mActivity)
                .path(UrlManager.PATH_LOGIN)
                .action("checkUserLogin")
                .put("username", name)
                .put("password", passwd)
                .post(new OkHttpManager.Callback() {
                    @Override
                    public void failure(OkHttpManager.Result result) throws Exception {
                        if (result.hasNetwork()) {
                            LogManager.tS(mActivity, R.string.http_request_failure);
                        } else {
                            LogManager.tS(mActivity, R.string.http_not_network);
                        }
                    }

                    @Override
                    public void success(OkHttpManager.Result result) throws Exception {
                        JSONObject object = result.jsonObject(false);
                        if (object != null) {
                            int errorCode = object.optInt("error_code", Integer.MIN_VALUE);
                            if (errorCode == 0) {
                                LogManager.tS(mActivity, "登录成功");
                            } else if (errorCode == 11002) {
                                LogManager.tS(mActivity, "用户不存在");
                            } else if (errorCode == 11007) {
                                LogManager.tS(mActivity, "密码错误");
                            } else if (errorCode == 11018) {
                                LogManager.tS(mActivity, "用户名字不能为空");
                            } else {
                                LogManager.tS(mActivity, R.string.http_unknown);
                            }
                        } else {
                            LogManager.tS(mActivity, R.string.http_unknown);
                        }
                    }
                });
        return 0;
    }

}
