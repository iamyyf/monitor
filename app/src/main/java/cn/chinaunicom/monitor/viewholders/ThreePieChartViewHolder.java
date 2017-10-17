package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.chart.PieChartStyle;

/**
 * Created by yfyang on 2017/8/7.
 */

public class ThreePieChartViewHolder extends BaseViewHolder {
    private PieChart pieChart_1;
    private PieChart pieChart_2;
    private PieChart pieChart_3;

    private TextView title_1;
    private TextView title_2;
    private TextView title_3;

    public LinearLayout firstRowChart;
    public LinearLayout firstRowTitle;
    public FrameLayout loading;

    public List<PieChart> pieCharts = new ArrayList<>();
    public List<TextView> chartTitles = new ArrayList<>();

    public ThreePieChartViewHolder(View view) {
        super(view);
        this.pieChart_1 = (PieChart)view.findViewById(R.id.pieChart_1);
        this.pieChart_2 = (PieChart)view.findViewById(R.id.pieChart_2);
        this.pieChart_3 = (PieChart)view.findViewById(R.id.pieChart_3);

        this.title_1 = (TextView) view.findViewById(R.id.chart1Title);
        this.title_2 = (TextView) view.findViewById(R.id.chart2Title);
        this.title_3 = (TextView) view.findViewById(R.id.chart3Title);

        this.firstRowChart = (LinearLayout) view.findViewById(R.id.firstRowChart);
        this.firstRowTitle = (LinearLayout) view.findViewById(R.id.firstRowTitle);

        this.loading = (FrameLayout) view.findViewById(R.id.loadingIndicator);

        pieCharts.add(this.pieChart_1);
        pieCharts.add(this.pieChart_2);
        pieCharts.add(this.pieChart_3);

        chartTitles.add(this.title_1);
        chartTitles.add(this.title_2);
        chartTitles.add(this.title_3);

        for (int num = 0; num < pieCharts.size(); num++) {
            pieCharts.get(num).setNoDataText(new PieChartStyle().chartNoDataText);
        }
    }
}
