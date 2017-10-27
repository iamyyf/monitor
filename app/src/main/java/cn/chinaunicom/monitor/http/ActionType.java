package cn.chinaunicom.monitor.http;

/**
 * Created by yfyang on 2017/8/3.
 */

public enum  ActionType {

    Login("/openapi/mobile/login"),                             //登录
    Chart("/openapi/zabbix/appdata"),                           //图形:折线图和柱状图
    MainframeDetailChart("/openapi/zabbix/listtimevalues"),     //图形:折线图和柱状图
    CenterList("/openapi/hsf/dropcentervalue"),                 //中心标志列表
    EnvirAndCateList("/openapi/hsf/dropvalues"),                //环境和归属列表
    MainframeList("/openapi/hsf/ipSearchByApp"),                //主机列表
    DeployCateAndAppbelong("/openapi/hsf/dropappvalue"),        //部署情况中应用归属和应用大类
    DeployList("/openapi/hsf/searchAllApp"),                    //部署情况列表
    MainframeDetail("/openapi/zabbix/itemsbyhost"),             //主机详情
    //AlarmCategory("/openapi/pushmsg/unchecklastdata"),          //告警分类
    AlarmCategory("/openapi/notification/getcontents"),          //告警分类
    PostJPushAlias("/openapi/pushmsg/getandroidtoken"),         //向服务器推送JPush需要的别名 Alias
    UnCheckAlarmDetail("/openapi/pushmsg/uncheckdata"),         //获得未查看的告警信息，请求之后才会加到历史告警里面
    GridInCenter("/openapi/zabbix/reqparams"),                  //下载所有图的参数，以及每个中心的gridview
    Logout("/openapi/androidlogout"),                           //登出
    DataSetIp("/openapi/zabbix/hosts"),                         //数据归集IP
    DataSetCharts("/openapi/zabbix/hostdata"),                  //数据归集折线图
    Reports("/openapi/androidgetreports"),                      //晨检报告

    //测试接口
    ConnectHost("/openapi/connecthost"),                        //链接主机
    ExcuteCommand("/openapi/excutecommand"),                    //执行命令
    HostIPs("/openapi/gethostconfig");                          //服务器ip

    public final String actionUrl;

    ActionType(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}
