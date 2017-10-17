package cn.chinaunicom.monitor.chart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.utils.ColorTemplate;

/**
 * Created by yfyang on 2017/8/3.
 */

public class ChartStyle {
    public XAxis.XAxisPosition xAxisPosition = XAxis.XAxisPosition.BOTTOM;          //设置X轴的位置
    public boolean showDrawGridBackground    = true;                                //设置背景网格是否显示
    public boolean canPinchZoom              = false;                               //设置是否可以放大

    public boolean showDiscription           = false;                               //设置是否显示图表描述
    public boolean scaleEnabled              = true;                                //设置是否可以放大

    public float  minLeftVal                 = 0.0f;                                //左侧Y轴最小值
    public float  minRightVal                = 0.0f;                                //右侧Y轴最小值
    public String chartNoDataText            = "未请求到数据";                       //图表无数据的显示
}
