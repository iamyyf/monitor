package cn.chinaunicom.monitor.chart.charthelper;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.ChartData;
import cn.chinaunicom.monitor.beans.ChartPoint;
import cn.chinaunicom.monitor.chart.LineChartStyle;
import cn.chinaunicom.monitor.chart.XAxisFormatter;
import cn.chinaunicom.monitor.http.Response.ChartResp;

/**
 * Created by yfyang on 2017/8/7.
 */

public class LineChartHelper implements ChartHelper {

    private LineChartStyle style;
    private LineChart chart;

    public static LineChartHelper getLineChartHelper(LineChart chart, LineChartStyle style) {
        if (null == chart)
            return null;
        style = (null == style ? new LineChartStyle():style);

        return new LineChartHelper(chart, style);
    }

    private LineChartHelper(LineChart chart, LineChartStyle style) {
        this.style = style;
        this.chart = chart;
        setChartStyle();
    }
    @Override
    public void setChartStyle() {
        chart.getXAxis().setPosition(style.xAxisPosition);
        chart.setDrawGridBackground(style.showDrawGridBackground);
        chart.setPinchZoom(style.canPinchZoom);
        chart.getLegend().setEnabled(style.showLegend);
        chart.getLegend().setOrientation(style.orientation);
        chart.getLegend().setHorizontalAlignment(style.horizontalAlignment);
        chart.getLegend().setVerticalAlignment(style.verticalAlignment);
        chart.getDescription().setEnabled(style.showDiscription);
        chart.setScaleEnabled(style.scaleEnabled);
        chart.setNoDataText(style.chartNoDataText);
        chart.setNoDataTextColor(R.color.colorBlack);
        chart.getAxisLeft().setAxisMinimum(style.minLeftVal);
        chart.getAxisRight().setAxisMinimum(style.minRightVal);
    }

//    @Override
//    public void setData(ChartResp dataSource) {
//
//        //获取图表数据的个数，一般柱状图就一个数据，折线图有多个数据，每条线对应一个数据
//        //折线图要用双重循环获得数据，每一个records保存了一条线的数据
//        int lineNum = dataSource.data.records.size();
//        int timeSkip = dataSource.data.timeSkip;
//        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//        //num是折线的序号
//        for (int num = 0; num < lineNum; num++){
//            List<Entry> points = new ArrayList<>();
//            int pointNum = dataSource.data.records.get(num).listCount;
//
//            //item是每一个折线上点的序号
//            for (int item = 0; item < pointNum; item++) {
//                //获取每一个点的数据
//                ChartPoint point = dataSource.data.records.get(num).list.get(item);
//                points.add(new Entry((float) item * timeSkip, Float.parseFloat(point.value)));
//            }
//
//            LineDataSet lineDataSet = new LineDataSet(points, dataSource.data.records.get(num).title);
//            lineDataSet.setColors(style.chartColors[num%style.chartColors.length]);
//            lineDataSet.setCircleColor(style.chartColors[num%style.chartColors.length]);
//            lineDataSet.setCircleHoleRadius(style.circleHoleRadius);
//            lineDataSet.setCircleRadius(style.circleRadius);
//            dataSets.add(lineDataSet);
//        }
//        LineData data = new LineData(dataSets);
//
//        //X轴的显示看XAxisFormatter的实现
//        chart.getXAxis().setValueFormatter(new XAxisFormatter(dataSource));
//
//        chart.setData(data);
//        chart.invalidate();
//    }

    @Override
    public void setData(ChartData dataSource) {

        //获取图表数据的个数，一般柱状图就一个数据，折线图有多个数据，每条线对应一个数据
        //折线图要用双重循环获得数据，每一个records保存了一条线的数据
        int lineNum = dataSource.records.size();
        int timeSkip = dataSource.timeSkip;
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        //num是折线的序号
        for (int num = 0; num < lineNum; num++){
            List<Entry> points = new ArrayList<>();
            int pointNum = dataSource.records.get(num).listCount;

            //item是每一个折线上点的序号
            for (int item = 0; item < pointNum; item++) {
                //获取每一个点的数据
                ChartPoint point = dataSource.records.get(num).list.get(item);
                points.add(new Entry((float) item * timeSkip, Float.parseFloat(point.value)));
            }

            LineDataSet lineDataSet = new LineDataSet(points, dataSource.records.get(num).title);
            lineDataSet.setColors(style.chartColors[num%style.chartColors.length]);
            lineDataSet.setCircleColor(style.chartColors[num%style.chartColors.length]);
            lineDataSet.setCircleHoleRadius(style.circleHoleRadius);
            lineDataSet.setCircleRadius(style.circleRadius);
            dataSets.add(lineDataSet);
        }
        LineData data = new LineData(dataSets);

        //X轴的显示看XAxisFormatter的实现
        chart.getXAxis().setValueFormatter(new XAxisFormatter(dataSource));

        chart.setData(data);
        chart.invalidate();
    }
}
