package cn.chinaunicom.monitor.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.chinaunicom.monitor.beans.ChartData;
import cn.chinaunicom.monitor.http.Response.ChartResp;

/**
 * X轴数值显示样式
 *
 * Created by yfyang on 2017/8/4.
 */

public class XAxisFormatter implements IAxisValueFormatter {

    private ChartData dataSource;

    public XAxisFormatter(ChartData dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        /*
        *这里返回的value是根据最早的时间点已经过了几分钟
        * 最早的时间点就是dataSource.data.leftXValue*dataSource.data.timeSkip单位是分钟
        * dataSource.data.leftXValue这个东西是 时间/间隔
        * PS：不知道咋想的这么设计接口，蛋疼
        */
        long timeMillionSec = dataSource.leftXValue * dataSource.timeSkip * 1000L;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date(timeMillionSec + (long)(value*1000)));
    }
}
