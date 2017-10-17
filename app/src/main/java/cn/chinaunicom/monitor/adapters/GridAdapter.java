package cn.chinaunicom.monitor.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.GridInCenter;
import cn.chinaunicom.monitor.beans.GridItem;
import cn.chinaunicom.monitor.utils.Const;
import cn.chinaunicom.monitor.viewholders.GridViewHolder;

/**
 * Created by yfyang on 2017/8/10.
 */

public class GridAdapter extends BaseAdapter {

    private Context context;
    private List<GridItem> gridData;
    public GridAdapter(Context context, List<GridItem> gridData) {
        this.context = context;
        this.gridData = gridData;
    }

    @Override
    public int getCount() {
        return gridData.size();
    }

    @Override
    public Object getItem(int position) {
        return gridData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewHolder viewHolder;
        if (null != convertView) {
            viewHolder = (GridViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.grid_item, null);
            viewHolder = new GridViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder.itemName.setText(gridData.get(position).monitorItemName);
        viewHolder.icon.setImageResource(Const.GRID_VIEW_ITEM_LOGO[position%5]);

        return convertView;
    }
}
