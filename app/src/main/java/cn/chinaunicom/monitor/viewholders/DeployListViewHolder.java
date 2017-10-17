package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.TextView;

import cn.chinaunicom.monitor.R;

/**
 * Created by yfyang on 2017/8/14.
 */

public class DeployListViewHolder extends BaseViewHolder {
    public TextView category;       //类别
    public TextView appBelongs;    //应用分类
    public TextView appName;        //应用名称
    public TextView backup;         //应用信息备注

    public DeployListViewHolder(View view) {
        super(view);
//        category = (TextView) view.findViewById(R.id.category);
//        appBelongs = (TextView) view.findViewById(R.id.appBelongs);
        appName = (TextView) view.findViewById(R.id.appName);
        backup = (TextView) view.findViewById(R.id.appBackup);
    }
}
