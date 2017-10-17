package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.TextView;

import cn.chinaunicom.monitor.R;

/**
 * Created by yfYang on 2017/8/15.
 */

public class AppCategoryViewHolder extends BaseViewHolder {

    public TextView appCategory;

    public AppCategoryViewHolder(View view) {
        super(view);
        appCategory = (TextView) view.findViewById(R.id.appCategory);

    }
}
