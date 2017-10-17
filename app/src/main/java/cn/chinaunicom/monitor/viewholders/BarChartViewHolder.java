package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.chart.BarChartStyle;

/**
 * Created by yfyang on 2017/8/7.
 */

public class BarChartViewHolder extends BaseViewHolder {

    public BarChart barChart;
    public TextView chartTitle;
    public FrameLayout loading;

    public BarChartViewHolder(View view) {
        super(view);
        this.barChart = (BarChart) view.findViewById(R.id.barChart);
        this.barChart.setNoDataText(new BarChartStyle().chartNoDataText);
        this.chartTitle = (TextView) view.findViewById(R.id.barChartTitle);
        this.loading = (FrameLayout) view.findViewById(R.id.loadingIndicator);
    }
}
