package cn.chinaunicom.monitor.chart;

import android.graphics.Color;

/**
 * Created by yfyang on 2017/8/7.
 */

public class PieChartStyle extends ChartStyle {
    public boolean showLegend                       = false;                        //显示图例
    public boolean rotationEnabled                  = false;                        //是否可以旋转
    public float transparentCircleRadius            = 90.0f;                        //半透明圆的半径
    public float rotationAngle                      = 165.5f;                       //旋转角度
    public float holeRadius                         = 40.0f;                        //内部圆洞半径
    public float centerTextSize                     = 16.0f;                        //文字字号
    public float maxAngle                           = 210.0f;                       //饼图最大显示角度
    public boolean showDrawValue                    = false;                        //饼图上是否显示数据
    public int[] sliceColor
            = {Color.rgb(46,204,113), Color.rgb(231,76,60)};                        //饼图的颜色
}
