package cn.chinaunicom.monitor.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.chart.LineChartStyle;
import cn.chinaunicom.monitor.chart.charthelper.LineChartHelper;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.ChartReq;
import cn.chinaunicom.monitor.http.Request.MainframeDetailChartReq;
import cn.chinaunicom.monitor.http.Response.ChartResp;
import cn.chinaunicom.monitor.utils.Const;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.LineChartViewHolder;
import es.dmoral.toasty.Toasty;

/**
 * Created by yfyang on 2017/9/25.
 */

public class MainframeDetailChartAsyncTask extends AsyncTask<Void, Void, ChartResp> {

    private LineChartViewHolder viewHolder;
    private MainframeDetailChartReq lineChartReq;
    private LineChartHelper lineChartHelper;
    private Context context;

    public MainframeDetailChartAsyncTask(Context context, LineChartViewHolder viewHolder, MainframeDetailChartReq req) {
        this.viewHolder = viewHolder;
        this.lineChartReq = req;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ChartResp doInBackground(Void... params) {
        ChartResp resp = new Http.Builder().create().getMainframeDetailChart(lineChartReq);
        return resp;
    }

    @Override
    protected void onPostExecute(ChartResp resp) {
        super.onPostExecute(resp);
        if (null != resp) {
            lineChartHelper = LineChartHelper
                    .getLineChartHelper(viewHolder.lineChart, new LineChartStyle());
            if (null != resp.data && !Utils.isListEmpty(resp.data.records)) {
                viewHolder.chartTitle.setText(resp.data.records.get(0).title);
                lineChartHelper.setData(resp.data);
            }
        } else {
            //Toasty.error(context, Const.TOAST_REQUEST_FAILED, Toast.LENGTH_SHORT).show();
        }
        viewHolder.loading.setVisibility(View.GONE);
        viewHolder.chartTitle.setVisibility(View.VISIBLE);
        viewHolder.lineChart.setVisibility(View.VISIBLE);
    }
}
