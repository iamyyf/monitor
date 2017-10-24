package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.chinaunicom.monitor.R;


/**
 * Created by yfyang on 2017/10/20.
 */

public class ReportGridViewHolder extends BaseViewHolder {
    public ImageView icon;
    public TextView itemName;
    public TextView isChecked;

    public ReportGridViewHolder(View view) {
        super(view);
        this.icon = (ImageView) view.findViewById(R.id.gridExcelIcon);
        this.itemName = (TextView) view.findViewById(R.id.gridReportName);
        this.isChecked = (TextView) view.findViewById(R.id.isChecked);
    }
}
