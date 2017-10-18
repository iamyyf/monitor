package cn.chinaunicom.monitor.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.beans.ChartData;
import cn.chinaunicom.monitor.chart.LineChartStyle;
import cn.chinaunicom.monitor.chart.charthelper.LineChartHelper;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.DataSetChartsReq;
import cn.chinaunicom.monitor.http.Response.DataSetChartsResp;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.LineChartViewHolder;

/**
 * Created by yfyang on 2017/10/11.
 */

public class DataSetLineChartAsyncTask extends AsyncTask<Void, Void, DataSetChartsResp> {

    private LineChartViewHolder viewHolder;
    private DataSetChartsReq lineChartReq;
    private LineChartHelper lineChartHelper;
    private Context context;
    private String chartType;

    public DataSetLineChartAsyncTask(Context context, LineChartViewHolder viewHolder, DataSetChartsReq req, String chartType) {
        this.viewHolder = viewHolder;
        this.lineChartReq = req;
        this.context = context;
        this.chartType = chartType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        lineChartReq.userToken = ChinaUnicomApplication.token;
    }

    @Override
    protected DataSetChartsResp doInBackground(Void... params) {
        DataSetChartsResp resp = new Http.Builder().create().getDataSetCharts(lineChartReq);
        return resp;
    }

    @Override
    protected void onPostExecute(DataSetChartsResp resp) {
        super.onPostExecute(resp);
        if (Utils.isRequestSuccess(resp)) {
            lineChartHelper = LineChartHelper
                    .getLineChartHelper(viewHolder.lineChart, new LineChartStyle());
            if (!Utils.isListEmpty(resp.data.records)) {
                ChartData data = resp.data.records.get(0).outgoing;
                if (chartType.equals(Config.OUTGOING)) {
                    data = resp.data.records.get(0).outgoing;
                } else if (chartType.equals(Config.INCOMING)) {
                    data = resp.data.records.get(0).incoming;
                } else if (chartType.equals(Config.DISK)) {
                    data = resp.data.records.get(0).disk;
                } else if (chartType.equals(Config.CPU)) {
                    data = resp.data.records.get(0).cpu;
                } else if (chartType.equals(Config.MEMORY)) {
                    data = resp.data.records.get(0).memory;
                }
                if (!Utils.isListEmpty(data.records)) {
                    viewHolder.chartTitle.setText(data.records.get(0).title);
                    lineChartHelper.setData(data);
                }
            }
        } else {
            //Toasty.error(context, Config.TOAST_REQUEST_FAILED, Toast.LENGTH_SHORT).show();
        }
        viewHolder.loading.setVisibility(View.GONE);
        viewHolder.chartTitle.setVisibility(View.VISIBLE);
        viewHolder.lineChart.setVisibility(View.VISIBLE);
    }
}
