package cn.chinaunicom.monitor.chart;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.utils.ColorTemplate;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

/**
 * Created by yfyang on 2017/8/7.
 */

public class LineChartStyle extends ChartStyle {
    public int[]   chartColors
            = {rgb("#2ecc71"), rgb("#f1c40f"),
                rgb("#e74c3c"), rgb("#3498db"), rgb("#a07ab6")};                   //图表颜色
    public boolean showLegend                = true;                               //是否显示图例
    public Legend.LegendOrientation orientation
            = Legend.LegendOrientation.HORIZONTAL;                                 //图例方向
    public Legend.LegendHorizontalAlignment horizontalAlignment
            = Legend.LegendHorizontalAlignment.LEFT;                               //图例水平位置
    public Legend.LegendVerticalAlignment verticalAlignment
            = Legend.LegendVerticalAlignment.BOTTOM;                               //图例垂直位置
    public float circleHoleRadius = 1.0f;                                          //折线上点的空心直径
    public float circleRadius = 1.5f;                                              //折线上点的直径


}
