package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.asynctask.MainframeDetailChartAsyncTask;
import cn.chinaunicom.monitor.http.Request.MainframeDetailChartReq;
import cn.chinaunicom.monitor.viewholders.LineChartViewHolder;

/**
 * Created by yfYang on 2017/8/18.
 */

public class MainframeDetailChartAdapter extends BaseAdapter {

    private LineChartViewHolder lineChartViewHolder;
    private MainframeDetailChartReq req = new MainframeDetailChartReq();
    private Context context;

    public MainframeDetailChartAdapter(Context context, String lineParam1) {
        this.context = context;
        req.userToken = ChinaUnicomApplication.token;
        req.param1 = lineParam1;
        req.param2 = "10";
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGroup) {
        if (null != converView) {
            lineChartViewHolder = (LineChartViewHolder) converView.getTag();
        } else {
            converView = View.inflate(context, R.layout.list_item_line_chart, null);
            lineChartViewHolder = new LineChartViewHolder(converView);
            converView.setTag(lineChartViewHolder);
        }
        startAsyncTask(new MainframeDetailChartAsyncTask(context, lineChartViewHolder, req));
        return converView;
    }

    private void startAsyncTask(AsyncTask task) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])null);
    }
}
