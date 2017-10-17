package cn.chinaunicom.monitor.beans;

import java.util.List;
import java.util.Map;

/**
 * Created by yfYang on 2017/8/30.
 */

public class AlarmDetailEntity {
    public long sendTime;
    //public String messageContent;
    public Map<String, Object> formatMessage;
    public List<String> keyOrder;
    public String jsonMap;
    public String jsonList;
}