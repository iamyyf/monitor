package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.chinaunicom.monitor.R;


/**
 * Created by yfyang on 2017/8/8.
 */

public class GridViewHolder extends BaseViewHolder {
    public ImageView icon;
    public TextView itemName;

    public GridViewHolder(View view) {
        super(view);
        this.icon = (ImageView) view.findViewById(R.id.gridIcon);
        this.itemName = (TextView) view.findViewById(R.id.gridItemName);
    }
}
