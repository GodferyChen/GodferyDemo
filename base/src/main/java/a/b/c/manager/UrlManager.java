package a.b.c.manager;

import android.content.Context;
import android.content.pm.PackageInfo;

import a.b.c.User;

public class UrlManager {
    public final static String TAG = "FIT753NESS";
    private final static int MOBILE_TYPE = 1;
    private final static String MOBILE_IMEI = "";       //推送码
    private final static String MOBILE_MARKET = "hanyou";       //应用市场

    public final static String SCHEME = "http";
    public final static String HOST = "fetalheart.uwellnesshk.com";
    public final static String PORT = "80";
    public final static String DOMAIN = SCHEME + "://" + HOST + ":" + PORT;

    public final static String PATH_LOGIN = "api/userapp/login.jsp";
    public final static String PATH_HEART_MONTH = "/api/userapp/fetalhealth.jsp";
    public final static String PATH_TOKEN = "/api/userapp/qiniu.jsp";

    public static OkHttpManager okHttp(Context context) {
        OkHttpManager manager = OkHttpManager.newInstance(context)
                .domain(DOMAIN)
                .tag(TAG)
                .put("userid", new User(context).userId)
                .put("jpush_registrationid", MOBILE_IMEI)
                .put("mobile_type", MOBILE_TYPE)
                .put("mobile_market", MOBILE_MARKET)
                .put("language", context.getResources().getConfiguration().locale.getLanguage().equals("zh")?0:1);
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            manager.put("mobile_version", "v" + packageInfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return manager;
    }

    public static void initJpushArg(Context ctx) {
//        MOBILE_IMEI = JPushInterface.getRegistrationID(ctx);
    }

}
