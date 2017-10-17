package cn.chinaunicom.monitor.utils;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.List;

import cn.chinaunicom.monitor.http.Response.BaseResp;
import es.dmoral.toasty.Toasty;

/**
 * Created by yfyang on 2017/8/9.
 */

public class Utils {
    public static boolean isStringEmpty(String str) {
        if (null == str || str.length() == 0 || str.equals(""))
            return true;
        return false;
    }

    public static boolean isRequestSuccess(BaseResp resp) {
        if (null != resp && resp.msg.equals(Const.REQ_SUCCESS_MSG) && resp.code == Const.REQ_SUCCESS_CODE)
            return true;
        return false;
    }

    public static void showErrorToast(Context context, String content) {
        Toasty.error(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void showSuccessToast(Context context, String content) {
        Toasty.success(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void showInfoToast(Context context, String content) {
        Toasty.info(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void showWarningToast(Context context, String content) {
        Toasty.warning(context, content, Toast.LENGTH_SHORT).show();
    }

    public static boolean isListEmpty(List list) {
        if (null == list || list.size() == 0)
            return true;
        return false;
    }

    public static void initLoadToast(LoadToast loadToast) {
        loadToast.setProgressColor(Color.WHITE);
        loadToast.setTranslationY(Const.LOAD_TOAST_POS);
        loadToast.setBackgroundColor(Color.rgb(244,34,6));
    }
}
