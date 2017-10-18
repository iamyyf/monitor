package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.asynctask.DataSetLineChartAsyncTask;
import cn.chinaunicom.monitor.http.Request.DataSetChartsReq;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.viewholders.LineChartViewHolder;
import cn.chinaunicom.monitor.viewholders.TopicViewHolder;

/**
 * Created by yfyang on 2017/10/11.
 */

public class DataSetChartsAdapter extends BaseAdapter {

    private Context context;
    private LineChartViewHolder lineChartViewHolder;
    private DataSetChartsReq req = new DataSetChartsReq();

    public DataSetChartsAdapter(Context context,String ip) {
        this.context = context;
        req.userToken = ChinaUnicomApplication.token;
        req.dataNum = 5;
        req.hosts = new ArrayList<>();
        req.hosts.add(ip);
    }

    @Override
    public int getCount() {
        return 5;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        String chartType = Config.OUTGOING;
        switch (position) {
            case 0: chartType = Config.OUTGOING;break;
            case 1: chartType = Config.INCOMING;break;
            case 2: chartType = Config.DISK;break;
            case 3: chartType = Config.MEMORY;break;
            case 4: chartType = Config.CPU;
        }

        //这里没复用ViewHolder，防止数据不一致
        convertView = View.inflate(context, R.layout.list_item_line_chart, null);
        lineChartViewHolder = new TopicViewHolder(convertView);
        convertView.setTag(lineChartViewHolder);
        startAsyncTask(new DataSetLineChartAsyncTask(context, lineChartViewHolder, req, chartType));

        return convertView;
    }

    private void startAsyncTask(AsyncTask task) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])null);
    }
}
