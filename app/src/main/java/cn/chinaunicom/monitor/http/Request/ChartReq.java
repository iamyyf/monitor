package cn.chinaunicom.monitor.http.Request;

import java.util.List;

/**
 * Created by yfyang on 2017/8/3.
 */

public class ChartReq extends BaseReq {
    public String groupName;           //主机群组名
    public String center;              //中心
    public int dataNum;                //点的数量
    public List<String> itemNames;     //请求监控项名称
    //public String param1;
    //public String param2;
}
