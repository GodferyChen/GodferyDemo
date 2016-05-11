package com.sample.godferydemo.login.model;

/**
 * Created by chen on 2016/3/12.
 */
public interface IUser {

    String getName();

    String getPasswd();

    int checkUserValidity(String name, String passwd);

}
