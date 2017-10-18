package cn.chinaunicom.monitor.chart.charthelper;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.chart.PieChartStyle;
import cn.chinaunicom.monitor.http.Response.PieChartResp;

/**
 * Created by yfyang on 2017/8/7.
 */

public class PieChartsHelper {
    private PieChartStyle style;
    private List<PieChart> pieChartList;

    public static PieChartsHelper getPieChartsHelper(List<PieChart> pieChartList, PieChartStyle style) {
        if (null == pieChartList || pieChartList.size() == 0)
            return null;
        style = (null == style ? new PieChartStyle():style);

        return new PieChartsHelper(pieChartList, style);
    }

    private PieChartsHelper(List<PieChart> pieChartList, PieChartStyle style) {
        this.style = style;
        this.pieChartList = new ArrayList<>(pieChartList);
        //分别设置每个饼图
        for (int chartNum = 0; chartNum < pieChartList.size(); chartNum++)
            setChartStyle(pieChartList.get(chartNum));
    }

    public void setChartStyle(PieChart chart) {
        chart.setNoDataText(style.chartNoDataText);
        chart.setNoDataTextColor(R.color.colorBlack);
        chart.setTransparentCircleRadius(style.transparentCircleRadius);
        chart.setRotationAngle(style.rotationAngle);
        chart.setRotationEnabled(style.rotationEnabled);
        chart.setHoleRadius(style.holeRadius);
        chart.setCenterTextSize(style.centerTextSize);
        chart.setMaxAngle(style.maxAngle);
        chart.getLegend().setEnabled(style.showLegend);
        chart.getDescription().setEnabled(style.showDiscription);
    }


    public void setData(PieChartResp dataSource, PieChart chart, int chartNum) {
        //List<String> xVals = new ArrayList<>();
        List<PieEntry> yVals = new ArrayList<>();
        yVals.add(new PieEntry(50.0f));

        if (null != dataSource && null != dataSource.data && null != dataSource.data.records) {
            String value = dataSource.data.records.get(chartNum).value;
            chart.setCenterText(value);
            yVals.add(new PieEntry(Float.parseFloat(value)));
        }
        PieDataSet pieDataSet = new PieDataSet(yVals,"");
        PieData data = new PieData(pieDataSet);
        pieDataSet.setColors(style.sliceColor);
        pieDataSet.setDrawValues(style.showDrawValue);                             //图形上的字是否显示
        chart.setData(data);
        chart.invalidate();
    }
}
