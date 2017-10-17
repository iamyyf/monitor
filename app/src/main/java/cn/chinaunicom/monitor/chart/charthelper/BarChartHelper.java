package cn.chinaunicom.monitor.chart.charthelper;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.ChartData;
import cn.chinaunicom.monitor.beans.ChartPoint;
import cn.chinaunicom.monitor.chart.BarChartStyle;
import cn.chinaunicom.monitor.chart.XAxisFormatter;
import cn.chinaunicom.monitor.http.Response.ChartResp;
import cn.chinaunicom.monitor.utils.Const;

/**
 * Created by yfyang on 2017/8/3.
 */

public class BarChartHelper implements ChartHelper {
    private BarChartStyle style;
    private  BarChart chart;
    public static BarChartHelper getBarChartHelper(BarChart chart, BarChartStyle style) {
        if (null == chart)
            return null;
        style = (null == style ? new BarChartStyle():style);
        return new BarChartHelper(chart, style);
    }

    private BarChartHelper(BarChart chart, BarChartStyle style) {
        this.chart = chart;
        this.style = style;
        setChartStyle();
    }

    @Override
    public void setChartStyle() {
        chart.getXAxis().setPosition(style.xAxisPosition);
        chart.setDrawGridBackground(style.showDrawGridBackground);
        chart.setPinchZoom(style.canPinchZoom);
        chart.getLegend().setEnabled(style.showLegend);
        chart.getDescription().setEnabled(style.showDiscription);
        chart.setScaleEnabled(style.scaleEnabled);
        chart.setNoDataText(style.chartNoDataText);
        chart.setNoDataTextColor(R.color.colorBlack);
        chart.getAxisLeft().setAxisMinimum(style.minLeftVal);
        chart.getAxisRight().setAxisMinimum(style.minRightVal);
    }

    @Override
    public void setData(ChartData dataSource) {
        List<BarEntry> points = new ArrayList<>();

        //获取图表数据的个数，一般柱状图就一个数据，折线图有多个数据，每条线对应一个数据
        //折线图要用双重循环获得数据，每一个records保存了一条线的数据
        int pointNum = dataSource.records.get(0).listCount;
        int timeSkip = dataSource.timeSkip;

        for (int item = 0; item < pointNum; item++) {
            //获取每一个点的数据
            ChartPoint point = dataSource.records.get(0).list.get(item);

            points.add(new BarEntry((float) item*timeSkip, Float.parseFloat(point.value)));
        }

        BarDataSet barDataSet;
        barDataSet = new BarDataSet(points, "图例标题");
        barDataSet.setColors(style.chartColors);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(barDataSet);
        BarData data = new BarData(dataSets);

        //X轴的显示看XAxisFormatter的实现
        chart.getXAxis().setValueFormatter(new XAxisFormatter(dataSource));
        chart.setData(data);
        chart.getBarData().setBarWidth(style.barWidth);
        chart.invalidate();
    }
}
