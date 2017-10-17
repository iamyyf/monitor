package cn.chinaunicom.monitor.beans;

import cn.chinaunicom.monitor.http.Response.ChartResp;

/**
 * Created by yfYang on 2017/10/11.
 */

public class DataSetChartsEntity {
    public String host;
    public ChartData outgoing;
    public ChartData incoming;
    public ChartData memory;
    public ChartData disk;
    public ChartData cpu;
}
