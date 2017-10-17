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
 * Created by yfYang on 2017/9/11.
 */

public class FourPieChartViewHolder extends BaseViewHolder {

    private PieChart pieChart_1;
    private PieChart pieChart_2;
    private PieChart pieChart_3;
    private PieChart pieChart_4;

    private TextView title_1;
    private TextView title_2;
    private TextView title_3;
    private TextView title_4;

    public LinearLayout firstRowChart;
    public LinearLayout firstRowTitle;
    public LinearLayout secondRowChart;
    public LinearLayout secondRowTitle;
    public FrameLayout loading;

    public List<PieChart> pieCharts = new ArrayList<>();
    public List<TextView> chartTitles = new ArrayList<>();

    public FourPieChartViewHolder(View view) {
        super(view);
        this.pieChart_1 = (PieChart)view.findViewById(R.id.pieChart_1);
        this.pieChart_2 = (PieChart)view.findViewById(R.id.pieChart_2);
        this.pieChart_3 = (PieChart)view.findViewById(R.id.pieChart_3);
        this.pieChart_4 = (PieChart)view.findViewById(R.id.pieChart_4);

        this.title_1 = (TextView) view.findViewById(R.id.chart1Title);
        this.title_2 = (TextView) view.findViewById(R.id.chart2Title);
        this.title_3 = (TextView) view.findViewById(R.id.chart3Title);
        this.title_4 = (TextView) view.findViewById(R.id.chart4Title);

        this.firstRowChart = (LinearLayout) view.findViewById(R.id.firstRowChart);
        this.firstRowTitle = (LinearLayout) view.findViewById(R.id.firstRowTitle);
        this.secondRowChart = (LinearLayout) view.findViewById(R.id.secondRowChart);
        this.secondRowTitle = (LinearLayout) view.findViewById(R.id.secondRowTitle);
        this.loading = (FrameLayout) view.findViewById(R.id.loadingIndicator);

        pieCharts.add(this.pieChart_1);
        pieCharts.add(this.pieChart_2);
        pieCharts.add(this.pieChart_3);
        pieCharts.add(this.pieChart_4);

        chartTitles.add(this.title_1);
        chartTitles.add(this.title_2);
        chartTitles.add(this.title_3);
        chartTitles.add(this.title_4);

        for (int num = 0; num < pieCharts.size(); num++) {
            pieCharts.get(num).setNoDataText(new PieChartStyle().chartNoDataText);
        }
    }
}
