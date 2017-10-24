package cn.chinaunicom.monitor.beans;

import java.util.List;
import java.util.Map;

/**
 * Created by yfYang on 2017/8/28.
 */

public class AlarmCategory {
    public List<AlarmCategoryCenterEntity> warningMsgRecords; //warningMsgRecords
    public int warningMsgCount;                         //warningMsgCount

//    public List<AlarmCategoryCenterEntity> records; //warningMsgRecords
//    public int recordCount;                         //warningMsgCount
    public int badge;

    public List<Long> reportIds;
    public int reportIdCount;
}
