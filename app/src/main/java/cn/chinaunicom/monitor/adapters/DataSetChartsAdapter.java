package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.asynctask.DataSetLineChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.FourPieChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.LineChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.MqBarChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.OnePieChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.SixPieChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.ThreePieChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.TwoPieChartAsyncTask;
import cn.chinaunicom.monitor.beans.DataSetChartsEntity;
import cn.chinaunicom.monitor.http.Request.ChartReq;
import cn.chinaunicom.monitor.http.Request.DataSetChartsReq;
import cn.chinaunicom.monitor.utils.Const;
import cn.chinaunicom.monitor.viewholders.BarChartViewHolder;
import cn.chinaunicom.monitor.viewholders.BaseViewHolder;
import cn.chinaunicom.monitor.viewholders.FourPieChartViewHolder;
import cn.chinaunicom.monitor.viewholders.LineChartViewHolder;
import cn.chinaunicom.monitor.viewholders.OnePieChartViewHolder;
import cn.chinaunicom.monitor.viewholders.SixPieChartViewHolder;
import cn.chinaunicom.monitor.viewholders.ThreePieChartViewHolder;
import cn.chinaunicom.monitor.viewholders.TopicViewHolder;
import cn.chinaunicom.monitor.viewholders.TwoPieChartViewHolder;

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
        String chartType = Const.OUTGOING;
        switch (position) {
            case 0: chartType = Const.OUTGOING;break;
            case 1: chartType = Const.INCOMING;break;
            case 2: chartType = Const.DISK;break;
            case 3: chartType = Const.MEMORY;break;
            case 4: chartType = Const.CPU;
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
