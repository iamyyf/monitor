package cn.chinaunicom.monitor.utils;

import cn.chinaunicom.monitor.R;

/**
 * Created by yfyang on 2017/8/2.
 */

public class Config {
    //网络相关
    //public static final String HTTP_SERVER_URL  = "http://120.52.49.69:9701";       //Server地址
    //public static final String HTTP_SERVER_URL  = "http://172.16.4.48:8080";        //lpy
    public static final String HTTP_SERVER_URL  = "http://tiangongplatform.cn:8081";  //新的server地址
    public static final int HTTP_TIMEOUT          = 10000;                            //超时时间
    public static final String REQ_SUCCESS_MSG    ="OK";                              //请求成功msg
    public static final int REQ_SUCCESS_CODE      = 0;                                //请求成功code
    public static int LOAD_TOAST_POS              = 0;                                //loading的位置,在Application类中初始化
    public static int POP_UP_DIALOG_HEIGHT        = 0;                                //主机页面弹出对话框的高度
    public static int POP_UP_DIALOG_WIDTH         = 0;                                //主机页面弹出对话框的宽度

    //Toast提示
    public static final String TOAST_REQUEST_FAILED    = "请求失败";                  //请求失败提示文本
    public static final String TOAST_LOGIN_FAILED      = "登录失败";                  //登录失败提示
    public static final String TOAST_LOGIN_TIP         = "用户名密码不能为空";         //用户名密码验证提示

    public static final int[] GRID_VIEW_ITEM_LOGO = {
            R.mipmap.ic_mq,
            R.mipmap.ic_drds,
            R.mipmap.ic_kvstore,
            R.mipmap.ic_es,
            R.mipmap.ic_data_set,
            R.mipmap.ic_data_set,
            R.mipmap.ic_data_set,
            R.mipmap.ic_mainframe_logo,
            R.mipmap.ic_mainframe_logo,
            R.mipmap.ic_data_set,
            R.mipmap.ic_data_set
    };

    //图形类别
    public static final String LINE_CHART           = "LineChartTableViewCell";       //折线图
    public static final String PIE_CHART            = "BrokerStatusTableViewCell";    //饼图
    public static final String BAR_CHART            = "BarChartTableViewCell";        //柱状图
    public static final String MAINFRAME_DETAIL     = "主机折线图";

    //数据归集请求参数
    public static final String DATA_SET_IP_REQ      = "压力测试";
    public static final String OUTGOING             = "outgoing";
    public static final String CPU                  = "cpu";
    public static final String MEMORY               = "memory";
    public static final String DISK                 = "disk";
    public static final String INCOMING             = "incoming";

    //Sqlite
    public final static int DB_VERSION = 1;                                     //db的版本
    public static String DB_NAME;                                               //db的名称,在登录后，MainActivity中初始化
    public final static String[] ALARM_CATEGORY_COLUMN                          //告警分类表的列名
            = {"id", "category", "uncheck_num", "latest_content", "latest_send_time", "column", "center_id"};

    public final static String[] ALARM_COLUMN                                   //告警详情表的列名
            = {"id", "category", "column", "send_time", "json_list", "json_map", "center_id"};

    public final static String[] CENTER_COLUMN                                  //中心
            = {"id", "center_name", "center_id", "is_uncheck"};

    public final static String[] REPORT_COLUMN
            ={"id", "center_name", "center_id", "report_content", "send_time", "is_uncheck"};

    public final static String[] REPORT_CENTER_COLUMN
            ={"id", "center_name", "center_id", "is_uncheck"};

    //极光推送
    public final static String CHECK_REPORT = "晨检报告";


    //通用
    public final static int TOP_RIGHT_MENU_HEIGHT = 1000;
}
