package cn.chinaunicom.monitor.http.Request;

import java.util.List;

/**
 * Created by yfYang on 2017/10/19.
 */

public class ReportsReq extends BaseReq {
    public String userName;
    public List<Long> reportIds;
}
