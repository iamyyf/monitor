package cn.chinaunicom.monitor.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.chart.PieChartStyle;
import cn.chinaunicom.monitor.chart.charthelper.PieChartsHelper;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.ChartReq;
import cn.chinaunicom.monitor.http.Response.PieChartResp;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.ThreePieChartViewHolder;

/**
 * Created by yfyang on 2017/8/10.
 */

public class ThreePieChartAsyncTask extends AsyncTask<Void, Void, PieChartResp> {
    private ThreePieChartViewHolder viewHolder;
    private ChartReq pieChartReq;
    private PieChartsHelper pieChartsHelper;
    private Context context;
    private String param1;

    public ThreePieChartAsyncTask(Context context, ThreePieChartViewHolder viewHolder, ChartReq req) {
        this.viewHolder = viewHolder;
        this.context = context;
        this.pieChartReq = req;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pieChartReq.userToken = ChinaUnicomApplication.token;
    }

    @Override
    protected PieChartResp doInBackground(Void... params) {
        PieChartResp resp = new Http.Builder().create().getPieCharts(pieChartReq);
        return resp;
    }

    @Override
    protected void onPostExecute(PieChartResp resp) {
        super.onPostExecute(resp);
        if (null != resp) {
            pieChartsHelper =
                    PieChartsHelper.getPieChartsHelper(viewHolder.pieCharts, new PieChartStyle());
            //对每个图分别设置数据
            for (int chartNum = 0; chartNum < resp.data.records.size()/*chartNum < viewHolder.pieCharts.size()*/; chartNum++) {
                if (null != resp.data && !Utils.isListEmpty(resp.data.records)) {
                    pieChartsHelper.setData(resp, viewHolder.pieCharts.get(chartNum), chartNum);
                    viewHolder.chartTitles.get(chartNum).setText(resp.data.records.get(chartNum).title);
                }
            }
            viewHolder.firstRowTitle.setVisibility(View.VISIBLE);
        } else {
            //Toasty.error(context, Const.TOAST_REQUEST_FAILED, Toast.LENGTH_SHORT).show();
        }
        viewHolder.firstRowChart.setVisibility(View.VISIBLE);
        viewHolder.loading.setVisibility(View.GONE);
    }
}
