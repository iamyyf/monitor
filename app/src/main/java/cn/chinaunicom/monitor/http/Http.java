package cn.chinaunicom.monitor.http;

import com.google.gson.Gson;

import cn.chinaunicom.monitor.http.Request.AlarmCategoryReq;
import cn.chinaunicom.monitor.http.Request.BaseReq;
import cn.chinaunicom.monitor.http.Request.DataSetChartsReq;
import cn.chinaunicom.monitor.http.Request.DataSetIpReq;
import cn.chinaunicom.monitor.http.Request.GridViewReq;
import cn.chinaunicom.monitor.http.Request.LogoutReq;
import cn.chinaunicom.monitor.http.Request.MainframeDetailChartReq;
import cn.chinaunicom.monitor.http.Request.ReportsReq;
import cn.chinaunicom.monitor.http.Request.UnCheckAlarmDetailReq;
import cn.chinaunicom.monitor.http.Request.ChartReq;
import cn.chinaunicom.monitor.http.Request.DeployListReq;
import cn.chinaunicom.monitor.http.Request.JPushAliasReq;
import cn.chinaunicom.monitor.http.Request.LoginReq;
import cn.chinaunicom.monitor.http.Request.MainframeDetailReq;
import cn.chinaunicom.monitor.http.Request.MainframeListReq;
import cn.chinaunicom.monitor.http.Response.DataSetChartsResp;
import cn.chinaunicom.monitor.http.Response.DataSetIpResp;
import cn.chinaunicom.monitor.http.Response.GridViewResp;
import cn.chinaunicom.monitor.http.Response.LogoutResp;
import cn.chinaunicom.monitor.http.Response.ReportsResp;
import cn.chinaunicom.monitor.http.Response.UnCheckAlarmCategoryResp;
import cn.chinaunicom.monitor.http.Response.UnCheckAlarmDetailResp;
import cn.chinaunicom.monitor.http.Response.CateAndAppbelongResp;
import cn.chinaunicom.monitor.http.Response.CenterFlagResp;
import cn.chinaunicom.monitor.http.Response.ChartResp;
import cn.chinaunicom.monitor.http.Response.DeployListResp;
import cn.chinaunicom.monitor.http.Response.EnviAndCateResp;
import cn.chinaunicom.monitor.http.Response.JPushAliasResp;
import cn.chinaunicom.monitor.http.Response.LoginResp;
import cn.chinaunicom.monitor.http.Response.MainframeDetailResp;
import cn.chinaunicom.monitor.http.Response.MainframeListResp;
import cn.chinaunicom.monitor.http.Response.PieChartResp;
import cn.chinaunicom.monitor.utils.Config;

/**
 * Created by yfyang on 2017/8/3.
 */

public class Http extends AbstractHttpClient{

    protected Http(String serverUrl, String sessionId) {
        super(serverUrl, sessionId);
    }

    public LoginResp login(LoginReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.Login.actionUrl, postString, LoginResp.class);
    }

    public ChartResp getBarChart(ChartReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.Chart.actionUrl, postString, ChartResp.class);
    }

    public ChartResp getLineChart(ChartReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.Chart.actionUrl, postString, ChartResp.class);
    }

    public ChartResp getMainframeDetailChart(MainframeDetailChartReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.MainframeDetailChart.actionUrl, postString, ChartResp.class);
    }

    public PieChartResp getPieCharts(ChartReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.Chart.actionUrl, postString, PieChartResp.class);
    }

    public CenterFlagResp getCenterList(BaseReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.CenterList.actionUrl, postString, CenterFlagResp.class);
    }

    public EnviAndCateResp getEnvirAndCateList(BaseReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.EnvirAndCateList.actionUrl, postString, EnviAndCateResp.class);
    }

    public MainframeListResp getMainframeList(MainframeListReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.MainframeList.actionUrl, postString, MainframeListResp.class);
    }

    public CateAndAppbelongResp getCateAndAppbelongs(BaseReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.DeployCateAndAppbelong.actionUrl, postString, CateAndAppbelongResp.class);
    }

    public DeployListResp getDepolyList(DeployListReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.DeployList.actionUrl, postString, DeployListResp.class);
    }

    public MainframeDetailResp getMainframeDetail(MainframeDetailReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.MainframeDetail.actionUrl, postString, MainframeDetailResp.class);
    }

    public UnCheckAlarmCategoryResp getUnCheckAlarmCategory(AlarmCategoryReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.AlarmCategory.actionUrl, postString, UnCheckAlarmCategoryResp.class);
    }

    public UnCheckAlarmDetailResp getUnCheckAlarmDetail(UnCheckAlarmDetailReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.UnCheckAlarmDetail.actionUrl, postString, UnCheckAlarmDetailResp.class);
    }

    public JPushAliasResp postAliasToServer(JPushAliasReq req) {
        String postString = new Gson().toJson(req);
        return  syncOkHttpPost(ActionType.PostJPushAlias.actionUrl, postString, JPushAliasResp.class);
    }

    public GridViewResp getGridInCenter(GridViewReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.GridInCenter.actionUrl, postString, GridViewResp.class);
    }

    public LogoutResp logout(LogoutReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.Logout.actionUrl, postString, LogoutResp.class);
    }

    public DataSetIpResp getDataSetIp(DataSetIpReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.DataSetIp.actionUrl, postString, DataSetIpResp.class);
    }

    public DataSetChartsResp getDataSetCharts(DataSetChartsReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.DataSetCharts.actionUrl, postString, DataSetChartsResp.class);
    }

    public ReportsResp getReports(ReportsReq req) {
        String postString = new Gson().toJson(req);
        return syncOkHttpPost(ActionType.Reports.actionUrl, postString, ReportsResp.class);
    }


    public static class Builder {
        public Http create() {
            return new Http(Config.HTTP_SERVER_URL, "");
        }

        public Http create(String sessionId) {
            return new Http(Config.HTTP_SERVER_URL, sessionId);
        }

        public Http create(String serverUrl, String sessionId) {
            return new Http(serverUrl, sessionId);
        }
    }
}
