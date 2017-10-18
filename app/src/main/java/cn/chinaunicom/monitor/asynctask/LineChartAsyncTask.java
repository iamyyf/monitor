package cn.chinaunicom.monitor.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.chart.LineChartStyle;
import cn.chinaunicom.monitor.chart.charthelper.LineChartHelper;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.ChartReq;
import cn.chinaunicom.monitor.http.Response.ChartResp;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.LineChartViewHolder;

/**
 * Created by yfyang on 2017/8/10.
 */

public class LineChartAsyncTask extends AsyncTask<Void, Void, ChartResp> {

    private LineChartViewHolder viewHolder;
    private ChartReq lineChartReq;
    private LineChartHelper lineChartHelper;
    private Context context;

    public LineChartAsyncTask(Context context, LineChartViewHolder viewHolder, ChartReq req) {
        this.viewHolder = viewHolder;
        this.lineChartReq = req;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        lineChartReq.userToken = ChinaUnicomApplication.token;
    }

    @Override
    protected ChartResp doInBackground(Void... params) {
        ChartResp resp = new Http.Builder().create().getLineChart(lineChartReq);
        return resp;
    }

    @Override
    protected void onPostExecute(ChartResp resp) {
        super.onPostExecute(resp);
        if (Utils.isRequestSuccess(resp)) {
            lineChartHelper = LineChartHelper
                    .getLineChartHelper(viewHolder.lineChart, new LineChartStyle());
            if (null != resp.data && !Utils.isListEmpty(resp.data.records)) {
                viewHolder.chartTitle.setText(resp.data.records.get(0).title);
                lineChartHelper.setData(resp.data);
            }
        } else {
            //Toasty.error(context, Config.TOAST_REQUEST_FAILED, Toast.LENGTH_SHORT).show();
        }
        viewHolder.loading.setVisibility(View.GONE);
        viewHolder.chartTitle.setVisibility(View.VISIBLE);
        viewHolder.lineChart.setVisibility(View.VISIBLE);
    }
}
