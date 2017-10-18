package cn.chinaunicom.monitor.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.chart.BarChartStyle;
import cn.chinaunicom.monitor.chart.charthelper.BarChartHelper;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.ChartReq;
import cn.chinaunicom.monitor.http.Response.ChartResp;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.BarChartViewHolder;

/**
 * Created by yfyang on 2017/8/10.
 */

public class MqBarChartAsyncTask extends AsyncTask<Void, Void, ChartResp>{
    private ChartReq barChartReq;
    private BarChartHelper barChartHelper;
    private BarChartViewHolder viewHolder;
    private Context context;

    public MqBarChartAsyncTask(Context context, BarChartViewHolder viewHolder, ChartReq req) {
        this.viewHolder = viewHolder;
        this.context = context;
        this.barChartReq = req;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //barChartReq.param1 = Config.MQ_BAR_CHART_REQ_PARAM_1;
        //barChartReq.param2 = Config.MQ_BAR_CHART_REQ_PARAM_2;
        barChartReq.userToken = ChinaUnicomApplication.token;
    }

    @Override
    protected ChartResp doInBackground(Void... voids) {
        ChartResp resp =  new Http.Builder().create().getBarChart(barChartReq);
        return resp;
    }

    @Override
    protected void onPostExecute(ChartResp resp) {
        super.onPostExecute(resp);
        if (null != resp) {
            barChartHelper = BarChartHelper
                    .getBarChartHelper(viewHolder.barChart, new BarChartStyle());
            if (null != resp.data && !Utils.isListEmpty(resp.data.records)) {
                viewHolder.chartTitle.setText(resp.data.records.get(0).title);
                barChartHelper.setData(resp.data);
            }
        } else {
            //Toasty.error(context, Config.TOAST_REQUEST_FAILED, Toast.LENGTH_SHORT).show();
        }

        viewHolder.loading.setVisibility(View.GONE);
        viewHolder.chartTitle.setVisibility(View.VISIBLE);
        viewHolder.barChart.setVisibility(View.VISIBLE);
    }
}
