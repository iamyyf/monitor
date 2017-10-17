package cn.chinaunicom.monitor.viewholders;


import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.wang.avi.AVLoadingIndicatorView;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.chart.BarChartStyle;
import cn.chinaunicom.monitor.chart.LineChartStyle;

/**
 * Created by yfyang on 2017/8/7.
 */

public class LineChartViewHolder extends BaseViewHolder {
    public LineChart lineChart;
    public TextView chartTitle;
    public FrameLayout loading;

    public LineChartViewHolder(View view) {
        super(view);
        this.lineChart = (LineChart) view.findViewById(R.id.lineChart);
        this.lineChart.setNoDataText(new LineChartStyle().chartNoDataText);

        this.chartTitle = (TextView) view.findViewById(R.id.lineChartTitle);
        this.loading = (FrameLayout) view.findViewById(R.id.loadingIndicator);
    }
}
