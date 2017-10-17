package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.asynctask.FourPieChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.LineChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.MqBarChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.OnePieChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.SixPieChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.ThreePieChartAsyncTask;
import cn.chinaunicom.monitor.asynctask.TwoPieChartAsyncTask;
import cn.chinaunicom.monitor.http.Request.ChartReq;
import cn.chinaunicom.monitor.utils.Const;
import cn.chinaunicom.monitor.utils.Logger;
import cn.chinaunicom.monitor.viewholders.BarChartViewHolder;
import cn.chinaunicom.monitor.viewholders.BaseViewHolder;
import cn.chinaunicom.monitor.viewholders.FourPieChartViewHolder;
import cn.chinaunicom.monitor.viewholders.InTPSViewHolder;
import cn.chinaunicom.monitor.viewholders.LineChartViewHolder;
import cn.chinaunicom.monitor.viewholders.OnePieChartViewHolder;
import cn.chinaunicom.monitor.viewholders.OutTPSViewHolder;
import cn.chinaunicom.monitor.viewholders.SixPieChartViewHolder;
import cn.chinaunicom.monitor.viewholders.ThreePieChartViewHolder;
import cn.chinaunicom.monitor.viewholders.TopicViewHolder;
import cn.chinaunicom.monitor.viewholders.TwoPieChartViewHolder;

/**
 * Created by yfyang on 2017/8/10.
 */

public class ChartsAdapter extends BaseAdapter {

    private Context context;
    private BaseViewHolder pieChartViewHolder;
    private LineChartViewHolder lineChartViewHolder;
    private BarChartViewHolder barChartViewHolder;

    public ChartsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
            return ChinaUnicomApplication.curChartCells.size();
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

        String chartType = ChinaUnicomApplication.curChartCells.get(position).cellClassName;
        ChartReq chartReq = ChinaUnicomApplication.curChartCells.get(position).reqParams;

        if (chartType.equals(Const.BAR_CHART)) {
            if (null != convertView && convertView.getTag() instanceof BarChartViewHolder) {
                barChartViewHolder = (BarChartViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.list_item_bar_chart, null);
                barChartViewHolder = new BarChartViewHolder(convertView);
                convertView.setTag(barChartViewHolder);
                startAsyncTask(new MqBarChartAsyncTask(context, barChartViewHolder, chartReq));
            }
        } else if (chartType.equals(Const.PIE_CHART)) {
            if (chartReq.itemNames.size() == 1) {
                convertView = View.inflate(context, R.layout.list_item_pie_charts_1, null);
                pieChartViewHolder = new OnePieChartViewHolder(convertView);
                convertView.setTag(pieChartViewHolder);
                startAsyncTask(new OnePieChartAsyncTask(context, (OnePieChartViewHolder)pieChartViewHolder, chartReq));
            } else if (chartReq.itemNames.size() == 2) {
                convertView = View.inflate(context, R.layout.list_item_pie_charts_2, null);
                pieChartViewHolder = new TwoPieChartViewHolder(convertView);
                convertView.setTag(pieChartViewHolder);
                startAsyncTask(new TwoPieChartAsyncTask(context, (TwoPieChartViewHolder)pieChartViewHolder, chartReq));
            } else if (chartReq.itemNames.size() == 3) {
                convertView = View.inflate(context, R.layout.list_item_pie_charts_3, null);
                pieChartViewHolder = new ThreePieChartViewHolder(convertView);
                convertView.setTag(pieChartViewHolder);
                startAsyncTask(new ThreePieChartAsyncTask(context, (ThreePieChartViewHolder)pieChartViewHolder, chartReq));
            } else if (chartReq.itemNames.size() == 4) {
                convertView = View.inflate(context, R.layout.list_item_pie_charts_4, null);
                pieChartViewHolder = new FourPieChartViewHolder(convertView);
                convertView.setTag(pieChartViewHolder);
                startAsyncTask(new FourPieChartAsyncTask(context, (FourPieChartViewHolder)pieChartViewHolder, chartReq));
            } else {
                convertView = View.inflate(context, R.layout.list_item_pie_charts_6, null);
                pieChartViewHolder = new SixPieChartViewHolder(convertView);
                convertView.setTag(pieChartViewHolder);
                startAsyncTask(new SixPieChartAsyncTask(context, (SixPieChartViewHolder)pieChartViewHolder, chartReq));
            }
        } else if (chartType.equals(Const.LINE_CHART)) {
            //这里没复用ViewHolder，防止数据不一致
            convertView = View.inflate(context, R.layout.list_item_line_chart, null);
            lineChartViewHolder = new TopicViewHolder(convertView);
            convertView.setTag(lineChartViewHolder);
            startAsyncTask(new LineChartAsyncTask(context, lineChartViewHolder, chartReq));
        }

        return convertView;
    }

    private void startAsyncTask(AsyncTask task) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])null);
    }
}
