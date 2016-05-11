package a.b.c;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;

public class User implements Serializable {

    public final static String SP_VERSION_CODE = "VERSION_CODE";
    public final static String SP_USER_ID = "USER_ID";
    public final static String SP_SEX = "SEX";
    public final static String SP_LOGIN_TYPE = "LOGIN_TYPE";
    public final static String SP_NICKNAME = "NICKNAME";
    public final static String SP_USERNAME = "USERNAME";
    public final static String SP_AVATAR = "AVATAR";
    public final static String SP_HEIGHT = "HEIGHT";
    public final static String SP_WEIGHT = "WEIGHT";
    public final static String SP_BIRTHDAY = "BIRTHDAY";
    public final static String SP_EXPECT_DATE = "EXPECT_DATE";
    public final static String SP_MOBILE = "MOBILE";
    public final static String SP_EMAIL = "EMAIL";
    public final static String SP_WECHAT = "WECHAT";
    public final static String SP_WEIBO = "WEIBO";
    public final static String SP_QQ = "QQ";
    public final static String SP_MSGRECEIVEPUSH = "MSGRECEIVEPUSH";
    public final static String SP_DEVICE_NAME = "DEVICE_NAME";
    public final static String SP_DEVICE_MAC = "DEVICE_MAC";

    public int versionCode;// 版本码
    public int sex;// 用户性别:1男  2女
    public int loginType;// 登录方法类型 0默认，1为QQ，2为微信，3为微博
    public int height;// 用户身高
    public int weight;// 用户体重
    public long birthday;// 用户生日
    public long expectDate;// 预产期
    public String userId;// 用户id
    public String nickname;// 姓名
    public String username;// 用户名
    public String avatar;// 用户头像图片
    public String mobile;// 用户手机号
    public String email;//邮箱
    public String wechat;//微信
    public String weibo;//微博
    public String qq;//QQ
    public String deviceName;//QQ
    public String deviceMac;//QQ
    public boolean msgReceivePush;
    private Context context;

    public User(Context context) {
        this.context = context;
        get();
    }

    private SharedPreferences preferences() {
        return PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    private SharedPreferences.Editor editor() {
        return preferences().edit();
    }

    public void remove(String key) {
        editor().remove(key).apply();
    }

    public void clear() {
        editor().clear().apply();
        editor().putInt(SP_VERSION_CODE, this.versionCode).apply();
        this.sex = 0;
        this.loginType = 0;
        this.height = 160;
        this.weight = 50;
        this.userId = "";
        this.birthday = 0;
        this.expectDate = 0;
        this.avatar = "";
        this.nickname = "";
        this.username = "";
        this.mobile = "";
        this.email = "";
        this.wechat = "";
        this.weibo = "";
        this.qq = "";
        this.deviceName = "";
        this.deviceMac = "";
        this.msgReceivePush = true;
    }

    public void save() {
        editor().putInt(SP_VERSION_CODE, this.versionCode)
                .putInt(SP_LOGIN_TYPE, this.loginType)
                .putInt(SP_SEX, this.sex)
                .putInt(SP_HEIGHT, this.height)
                .putInt(SP_WEIGHT, this.weight)
                .putString(SP_USER_ID, this.userId)
                .putLong(SP_BIRTHDAY, this.birthday)
                .putLong(SP_EXPECT_DATE, this.expectDate)
                .putString(SP_AVATAR, this.avatar)
                .putString(SP_NICKNAME, this.nickname)
                .putString(SP_USERNAME, this.username)
                .putString(SP_MOBILE, this.mobile)
                .putString(SP_EMAIL, this.email)
                .putString(SP_WECHAT, this.wechat)
                .putString(SP_WEIBO, this.weibo)
                .putString(SP_QQ, this.qq)
                .putString(SP_DEVICE_NAME, this.deviceName)
                .putString(SP_DEVICE_MAC, this.deviceMac)
                .putBoolean(SP_MSGRECEIVEPUSH, this.msgReceivePush)
                .apply();
    }

    public void get() {

        SharedPreferences preferences = preferences();
        this.versionCode = preferences.getInt(SP_VERSION_CODE, 0);
        this.loginType = preferences.getInt(SP_LOGIN_TYPE, 0);
        this.sex = preferences.getInt(SP_SEX, 0);
        this.height = preferences.getInt(SP_HEIGHT, 160);
        this.weight = preferences.getInt(SP_WEIGHT, 50);
        this.userId = preferences.getString(SP_USER_ID, "");
        this.birthday = preferences.getLong(SP_BIRTHDAY, 0);
        this.expectDate = preferences.getLong(SP_EXPECT_DATE, 0);
        this.avatar = preferences.getString(SP_AVATAR, "");
        this.nickname = preferences.getString(SP_NICKNAME, "");
        this.username = preferences.getString(SP_USERNAME, "");
        this.mobile = preferences.getString(SP_MOBILE, "");
        this.email = preferences.getString(SP_EMAIL, "");
        this.wechat = preferences.getString(SP_WECHAT, "");
        this.weibo = preferences.getString(SP_WEIBO, "");
        this.qq = preferences.getString(SP_QQ, "");
        this.deviceName = preferences.getString(SP_DEVICE_NAME, "");
        this.deviceMac = preferences.getString(SP_DEVICE_MAC, "");
        this.msgReceivePush = preferences.getBoolean(SP_MSGRECEIVEPUSH, true);
    }

    public User clone(Context context) {
        User user = new User(context);
        user.versionCode = this.versionCode;
        user.loginType = this.loginType;
        user.sex = this.sex;
        user.height = this.height;
        user.weight = this.weight;
        user.userId = this.userId;
        user.birthday = this.birthday;
        user.expectDate = this.expectDate;
        user.avatar = this.avatar;
        user.nickname = this.nickname;
        user.username = this.username;
        user.mobile = this.mobile;
        user.email = this.email;
        user.wechat = this.wechat;
        user.weibo = this.weibo;
        user.qq = this.qq;
        user.deviceName = this.deviceName;
        user.deviceMac = this.deviceMac;
        user.msgReceivePush = this.msgReceivePush;
        return user;
    }

    public User copy(User user) {
        this.versionCode = user.versionCode;
        this.loginType = user.loginType;
        this.sex = user.sex;
        this.height = user.height;
        this.weight = user.weight;
        this.userId = user.userId;
        this.birthday = user.birthday;
        this.expectDate = user.expectDate;
        this.avatar = user.avatar;
        this.nickname = user.nickname;
        this.username = user.username;
        this.mobile = user.mobile;
        this.email = user.email;
        this.wechat = user.wechat;
        this.weibo = user.weibo;
        this.qq = user.qq;
        this.deviceName = user.deviceName;
        this.deviceMac = user.deviceMac;
        this.msgReceivePush = user.msgReceivePush;
        return this;
    }
}
