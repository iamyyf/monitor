package cn.chinaunicom.monitor.beans;

/**
 * Created by yfYang on 2017/10/18.
 */

public class ReportEntity {
    public String reportContent;
    public String center;
    public String centerId;
    public long sendTime;

    //这里是业务需求添加的字段，不是服务器返回的字段
    public int isChecked;   //该报告是否已经查看
}
