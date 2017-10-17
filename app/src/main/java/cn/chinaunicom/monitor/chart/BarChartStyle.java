package cn.chinaunicom.monitor.chart;

import com.github.mikephil.charting.utils.ColorTemplate;

/**
 * Created by yfyang on 2017/8/3.
 */

public class BarChartStyle extends ChartStyle {
    public float barWidth                 = 20.0f;                                //柱状图柱子的宽度
    public int[] chartColors              = ColorTemplate.MATERIAL_COLORS;       //图表颜色
    public boolean showLegend             = false;                               //是否显示图例
}
