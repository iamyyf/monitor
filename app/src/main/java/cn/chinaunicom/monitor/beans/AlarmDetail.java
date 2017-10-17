package cn.chinaunicom.monitor.beans;

import java.util.List;

/**
 * 告警详情
 * Created by yfYang on 2017/8/30.
 */

public class AlarmDetail {
    public List<AlarmDetailEntity> records;
    public int recordCount;
    public String item;
    public String centerId;
    public String centerName;
}
