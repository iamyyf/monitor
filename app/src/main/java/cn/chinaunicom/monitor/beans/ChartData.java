package cn.chinaunicom.monitor.beans;

import java.util.List;

/**
 * Created by yfyang on 2017/8/3.
 */

public class ChartData {
    public int timeSkip;
    public List<ChartPointDetail> records;
    public long rightXValue;
    public long leftXValue;
    public int maxValue;
    public int recordCount;
    public String maxYClock;
}
