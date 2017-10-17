package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.chinaunicom.monitor.R;

/**
 * Created by yfYang on 2017/8/16.
 */

public class MainframeDetailViewHolder extends BaseViewHolder {

    public TextView monitorItem;
    public TextView monitorTime;
    public TextView monitorData;
    public Button chartBtn;

    public MainframeDetailViewHolder(View view) {
        super(view);

        monitorItem = (TextView) view.findViewById(R.id.monitorItem);
        monitorTime = (TextView) view.findViewById(R.id.monitorTime);
        monitorData = (TextView) view.findViewById(R.id.monitorData);
        //chartBtn = (Button) view.findViewById(R.id.chartBtn);
    }
}
