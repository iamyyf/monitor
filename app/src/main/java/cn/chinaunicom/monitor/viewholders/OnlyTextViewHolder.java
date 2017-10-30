package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.chinaunicom.monitor.R;

/**
 * Created by yfYang on 2017/10/30.
 */

public class OnlyTextViewHolder extends BaseViewHolder {

    public TextView text;

    public OnlyTextViewHolder(View view) {
        super(view);
        text = (TextView) view.findViewById(R.id.spinnerItem);
    }
}
