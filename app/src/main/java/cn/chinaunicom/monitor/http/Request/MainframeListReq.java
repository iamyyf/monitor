package cn.chinaunicom.monitor.http.Request;

/**
 * Created by yfyang on 2017/8/11.
 */

public class MainframeListReq extends BaseReq {
    public String appHierarchyId; //类别Id
    public String appClassId;     //应用大类
    public String appName;        //应用名
    public String centerId;       //中心标志
    public String envirId;        //环境标志
}