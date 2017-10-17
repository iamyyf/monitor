package cn.chinaunicom.monitor.viewholders;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.chart.BarChartStyle;
import cn.chinaunicom.monitor.chart.PieChartStyle;

/**
 * Created by yfyang on 2017/8/7.
 */

public class SixPieChartViewHolder extends BaseViewHolder {
    private PieChart pieChart_1;
    private PieChart pieChart_2;
    private PieChart pieChart_3;
    private PieChart pieChart_4;
    private PieChart pieChart_5;
    private PieChart pieChart_6;

    private TextView title_1;
    private TextView title_2;
    private TextView title_3;
    private TextView title_4;
    private TextView title_5;
    private TextView title_6;

    public LinearLayout firstRowChart;
    public LinearLayout firstRowTitle;
    public LinearLayout secondRowChart;
    public LinearLayout secondRowTitle;
    public FrameLayout loading;

    public List<PieChart> pieCharts = new ArrayList<>();
    public List<TextView> chartTitles = new ArrayList<>();

    public SixPieChartViewHolder(View view) {
        super(view);
        this.pieChart_1 = (PieChart)view.findViewById(R.id.pieChart_1);
        this.pieChart_2 = (PieChart)view.findViewById(R.id.pieChart_2);
        this.pieChart_3 = (PieChart)view.findViewById(R.id.pieChart_3);
        this.pieChart_4 = (PieChart)view.findViewById(R.id.pieChart_4);
        this.pieChart_5 = (PieChart)view.findViewById(R.id.pieChart_5);
        this.pieChart_6 = (PieChart)view.findViewById(R.id.pieChart_6);

        this.title_1 = (TextView) view.findViewById(R.id.chart1Title);
        this.title_2 = (TextView) view.findViewById(R.id.chart2Title);
        this.title_3 = (TextView) view.findViewById(R.id.chart3Title);
        this.title_4 = (TextView) view.findViewById(R.id.chart4Title);
        this.title_5 = (TextView) view.findViewById(R.id.chart5Title);
        this.title_6 = (TextView) view.findViewById(R.id.chart6Title);

        this.firstRowChart = (LinearLayout) view.findViewById(R.id.firstRowChart);
        this.firstRowTitle = (LinearLayout) view.findViewById(R.id.firstRowTitle);
        this.secondRowChart = (LinearLayout) view.findViewById(R.id.secondRowChart);
        this.secondRowTitle = (LinearLayout) view.findViewById(R.id.secondRowTitle);
        this.loading = (FrameLayout) view.findViewById(R.id.loadingIndicator);

        pieCharts.add(this.pieChart_1);
        pieCharts.add(this.pieChart_2);
        pieCharts.add(this.pieChart_3);
        pieCharts.add(this.pieChart_4);
        pieCharts.add(this.pieChart_5);
        pieCharts.add(this.pieChart_6);

        chartTitles.add(this.title_1);
        chartTitles.add(this.title_2);
        chartTitles.add(this.title_3);
        chartTitles.add(this.title_4);
        chartTitles.add(this.title_5);
        chartTitles.add(this.title_6);

        for (int num = 0; num < pieCharts.size(); num++) {
            pieCharts.get(num).setNoDataText(new PieChartStyle().chartNoDataText);
        }
    }
}
