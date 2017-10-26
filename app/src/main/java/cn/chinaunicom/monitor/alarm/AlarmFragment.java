package cn.chinaunicom.monitor.alarm;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jauker.widget.BadgeView;
import com.zaaach.toprightmenu.MenuItem;
import com.zaaach.toprightmenu.TopRightMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.MainActivity;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.beans.AlarmCategoryEntity;
import cn.chinaunicom.monitor.beans.CenterEntity;
import cn.chinaunicom.monitor.callback.TopRightPointCallBack;
import cn.chinaunicom.monitor.sqlite.AlarmDatabaseHelper;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;
import cn.chinaunicom.monitor.viewholders.AlarmCategoryViewHolder;
import me.leolin.shortcutbadger.ShortcutBadger;

public class AlarmFragment extends Fragment implements TopRightPointCallBack {

    private AlarmCategoryEntity curCategoryEntity = null;
    private AlarmDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private TopRightMenu menu;
    private List<MenuItem> menuItems = new ArrayList<>();
    //key:centerName, value:centerId
    private Map<String, String> uncheckCenterMap = new HashMap<>();
    private BadgeView imgRightBtnBadgeView;

    public static AlarmFragment instance = null;

    @Bind(R.id.txtViewTitle)
    public TextView title;

    @Bind(R.id.imgBtnRight)
    ImageButton imgBtnRight;

    @Bind(R.id.alarmCategoryList)
    ListView alarmCategoryList;

    @OnClick(R.id.imgBtnRight)
    void popMenu() {
        Cursor uncheckCenterCursor = db.query("CENTER", Config.CENTER_COLUMN,
                null, null, null, null, null);
        while (uncheckCenterCursor.moveToNext()) {
            if (uncheckCenterCursor.getInt(3) == 1) //有未读消息
                uncheckCenterMap.put(uncheckCenterCursor.getString(1), uncheckCenterCursor.getString(2));
        }
        initTopRigtMenu();
        initMenuItem(ChinaUnicomApplication.alarmCenterList, uncheckCenterMap);
        menu.showAsDropDown(imgBtnRight, 0, 0);
        uncheckCenterCursor.close();
    }

    public AlarmFragment() {
        // Required empty public constructor
    }

    public static AlarmFragment getInstance() {
        instance = new AlarmFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, view);
        //这里初始化的顺序不能变更！
        initDB();
        initTopRigtMenu();
        initView();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ChinaUnicomApplication.alarmCategoryEntities.clear();
        instance = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //关闭数据库资源
        //db.close();
    }

    private void initDB() {
        dbHelper = new AlarmDatabaseHelper(getContext(), Config.DB_NAME, null, Config.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    private void initView() {
        ShortcutBadger.applyCount(getContext(), 0); //for 1.1.4+
        imgRightBtnBadgeView = new BadgeView(getActivity());
        imgRightBtnBadgeView.setTargetView(imgBtnRight);
        imgRightBtnBadgeView.getBackground().setAlpha(0);
        imgRightBtnBadgeView.setTextColor(Color.rgb(205, 175, 149));
        imgRightBtnBadgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT );

        Cursor uncheckCenterCursor = db.query("CENTER", Config.CENTER_COLUMN,
                null, null, null, null, null);
        while (uncheckCenterCursor.moveToNext()) {
            if (uncheckCenterCursor.getInt(3) == 1) {
                uncheckCenterMap.put(uncheckCenterCursor.getString(1), uncheckCenterCursor.getString(2));
                imgRightBtnBadgeView.setText("●");
            }
        }
        uncheckCenterCursor.close();

        //首次显示将当前选择的中心设为List中的第一个
        if (null == ChinaUnicomApplication.alarmCurCenter &&
                !Utils.isListEmpty(ChinaUnicomApplication.alarmCenterList)) {
            String itemId = ChinaUnicomApplication.alarmCenterList.get(0).itemId;
            String title = ChinaUnicomApplication.alarmCenterList.get(0).title;
            ChinaUnicomApplication.alarmCurCenter = new CenterEntity(itemId, title);
        }
        //如果用户第一次登录，在选择告警页面时，此时如果中心的列表还没下载完，告警分类的列表就应该不能显示
        if (null != ChinaUnicomApplication.alarmCurCenter &&
                !Utils.isStringEmpty(ChinaUnicomApplication.alarmCurCenter.itemId)) {
            initAlarmCategoryList(ChinaUnicomApplication.alarmCurCenter.itemId);
            getAlarmCategoryEntities(ChinaUnicomApplication.alarmCurCenter.itemId);
        }
        initTitleBar();
        initListView();
    }

    private void initTopRigtMenu() {
        updateAlarmCenterList();
        menu = new TopRightMenu(getActivity());
        menu.setHeight(Config.POP_UP_DIALOG_HEIGHT)
                .setWidth(Config.POP_UP_DIALOG_WIDTH)
                .showIcon(true)
                .dimBackground(true)
                .needAnimationStyle(true)
                .setOnMenuItemClickListener(
                        new TopRightMenu.OnMenuItemClickListener() {
                            @Override
                            public void onMenuItemClick(int position) {
                                actionWhenClickMenuItem(position);
                            }
                        });
    }

    //点击菜单后重置Title，重置currentCenter变量
    private void actionWhenClickMenuItem(int position) {
        //这里的逻辑都在OnClick监听里面，如果TopRight为空，也就不可能点击，所以不用判空
        if (null != ChinaUnicomApplication.alarmCurCenter) {
            ChinaUnicomApplication.alarmCurCenter.title = ChinaUnicomApplication.alarmCenterList.get(position).title;
            ChinaUnicomApplication.alarmCurCenter.itemId = ChinaUnicomApplication.alarmCenterList.get(position).itemId;
        } else {
            ChinaUnicomApplication.alarmCurCenter =
                    new CenterEntity(ChinaUnicomApplication.alarmCenterList.get(position).itemId,
                    ChinaUnicomApplication.alarmCenterList.get(position).title);
        }

        initAlarmCategoryList(ChinaUnicomApplication.alarmCurCenter.itemId);
        getAlarmCategoryEntities(ChinaUnicomApplication.alarmCurCenter.itemId);
        Utils.showSuccessToast(getContext(), "已切换到 " + ChinaUnicomApplication.alarmCurCenter.title);
        title.setText(ChinaUnicomApplication.alarmCurCenter.title + "-告警");
    }

    //删除弹出菜单对话框里面中心前的红点与否
    private void removeCenterRedPoint() {
        Cursor centerCursor = db.query("ALARM_CATEGORY", Config.ALARM_CATEGORY_COLUMN,
                "center_id=?", new String[]{ChinaUnicomApplication.alarmCurCenter.itemId}, null, null, null);

        if (!centerCursor.moveToNext()) {
            db.delete("CENTER", "center_name=? and center_id=?",
                    new String[]{ChinaUnicomApplication.alarmCurCenter.title,
                            ChinaUnicomApplication.alarmCurCenter.itemId});
            uncheckCenterMap.remove(ChinaUnicomApplication.alarmCurCenter.title);
            updateAlarmCenterList();
            if (!Utils.isListEmpty(ChinaUnicomApplication.alarmCenterList)) {
                actionWhenClickMenuItem(0);
            } else {
                ChinaUnicomApplication.alarmCurCenter = null;
                ChinaUnicomApplication.alarmCategoryEntities.clear();
                ChinaUnicomApplication.alarmCategoryAdapter.notifyDataSetChanged();
                title.setText("暂无告警记录");
            }
            updateTopRightPoint();
        } else {
            //把游标移动到第一条记录之前
            centerCursor.moveToPrevious();
            while (centerCursor.moveToNext()) {
                //如果该中心还有未读消息，就保留红点
                if (centerCursor.getInt(2) != 0)
                    break;
                if (centerCursor.isLast()) {
                    ContentValues values = new ContentValues();
                    values.put("is_uncheck", 0);
                    db.update("CENTER", values, "center_name=? and center_id=?",
                            new String[]{ChinaUnicomApplication.alarmCurCenter.title,
                                    ChinaUnicomApplication.alarmCurCenter.itemId});
                    uncheckCenterMap.remove(ChinaUnicomApplication.alarmCurCenter.title);
                    updateTopRightPoint();
                }
            }
        }

        centerCursor.close();
    }

    private void updateTopRightPoint() {
        if (!uncheckCenterMap.isEmpty()) {
            imgRightBtnBadgeView.setText("●");
        } else {
            imgRightBtnBadgeView.setBadgeCount(0);
        }
    }

    //初始化menu的item
    private void initMenuItem(List<CenterEntity> data, Map<String, String> uncheckCenterMap) {
        menuItems.clear();
        //如果未查看的中心Map中有该中心，则显示红点，否则不显示
        for (int i = 0; i < data.size(); i++) {
            if (uncheckCenterMap.containsKey(data.get(i).title)) {
                menuItems.add(new MenuItem(R.mipmap.ic_red_point, data.get(i).title));
            } else {
                menuItems.add(new MenuItem(R.mipmap.ic_blank, data.get(i).title));
            }
        }
        menu.addMenuList(menuItems);
    }

    //这里进行全局告警类别列表更新的目的在于，如果首次打开App且没有更新的告警来，要读取以前是否有未读的消息进行显示
    private void getAlarmCategoryEntities(String currentCenterId) {
        Cursor cursor = db.query("ALARM_CATEGORY", Config.ALARM_CATEGORY_COLUMN, "center_id=?",
                new String[]{currentCenterId}, null, null,null);
        //每次加载新的UI要将全局保存的告警类别信息进行清空，否则会重复显示
        ChinaUnicomApplication.alarmCategoryEntities.clear();

        while (cursor.moveToNext()) {
            AlarmCategoryEntity e = new AlarmCategoryEntity();
            //括号里的序号对应SqliteDataBaseHelper中表的列
            e.title = cursor.getString(1);
            e.value = cursor.getInt(2);
            e.mesContent = cursor.getString(3);
            e.sendTime = cursor.getLong(4);
            e.column = cursor.getString(5);
            ChinaUnicomApplication.alarmCategoryEntities.add(e);
        }
        cursor.close();
        ChinaUnicomApplication.alarmCategoryAdapter.notifyDataSetChanged();
    }

    private void updateAlarmCenterList() {
        Cursor centerCursor = db.query("CENTER", Config.CENTER_COLUMN, null, null, null, null, null);
        ChinaUnicomApplication.alarmCenterList.clear();
        while (centerCursor.moveToNext()) {
            CenterEntity e = new CenterEntity();
            e.title = centerCursor.getString(1);
            e.itemId = centerCursor.getString(2);
            e.isUncheck = centerCursor.getInt(3);
            ChinaUnicomApplication.alarmCenterList.add(e);
        }
    }

    private void initListView() {
        alarmCategoryList.setAdapter(ChinaUnicomApplication.alarmCategoryAdapter);
        alarmCategoryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                curCategoryEntity = ChinaUnicomApplication.alarmCategoryEntities.get(position);
                new AlertDialog
                        .Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("是否要删除关于" + curCategoryEntity.title + "的告警记录？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ChinaUnicomApplication.alarmCategoryEntities.remove(position);
                                db.delete("ALARM_CATEGORY", "category=? and center_id=?",
                                        new String[]{curCategoryEntity.title, ChinaUnicomApplication.alarmCurCenter.itemId});
                                db.delete("ALARM", "category=? and center_id=?",
                                        new String[]{curCategoryEntity.title, ChinaUnicomApplication.alarmCurCenter.itemId});

                                //更新Tab告警的红点
                                MainActivity.instance.updateAlarmBadge();
                                //移除中心列表里相应中心的红点提示
                                removeCenterRedPoint();
                                ChinaUnicomApplication.alarmCategoryAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
                return true;
            }
        });

        alarmCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                curCategoryEntity = ChinaUnicomApplication.alarmCategoryEntities.get(position);
                Intent intent = new Intent(getContext(), AlarmDetailActivity.class);
                intent.putExtra("ALARM_CATEGORY", curCategoryEntity.column);
                intent.putExtra("ALARM_CATEGORY_TITLE", curCategoryEntity.title);
                intent.putExtra("CENTER_NAME", ChinaUnicomApplication.alarmCurCenter.title);
                intent.putExtra("CENTER_ID", ChinaUnicomApplication.alarmCurCenter.itemId);
                ((AlarmCategoryViewHolder)view.getTag()).badgeView.setBadgeCount(0);

                //这里是处理Badge的逻辑，如果带红点的item已读，则将UnCheck中的该项清除
                ContentValues values = new ContentValues();
                values.put("uncheck_num", 0);
                curCategoryEntity.value = 0;
                ChinaUnicomApplication.badgeMap.put(curCategoryEntity.title, 0);
                ShortcutBadger.applyCount(ChinaUnicomApplication.getApplication(), 0);
                db.update("ALARM_CATEGORY", values,"category=? and center_id=?",
                        new String[]{curCategoryEntity.title, ChinaUnicomApplication.alarmCurCenter.itemId});
                //更新Tab告警的红点
                MainActivity.instance.updateAlarmBadge();
                //移除中心列表里相应中心的红点提示
                removeCenterRedPoint();
                startActivity(intent);
            }
        });
    }

    private void initAlarmCategoryList(String currentCenterId) {
        //1.将未读的categroy存入map，为badge做准备
        Cursor cursor = db.query("ALARM_CATEGORY",new String[]{"category", "uncheck_num"},
                "center_id=?", new String[]{currentCenterId}, null, null, null);
        ChinaUnicomApplication.badgeMap.clear();
        while (cursor.moveToNext()) {
            ChinaUnicomApplication.badgeMap.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imgBtnRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_menu, null));
        } else {
            imgBtnRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_menu));
        }
        if (!Utils.isListEmpty(ChinaUnicomApplication.alarmCenterList))
            title.setText(ChinaUnicomApplication.alarmCurCenter.title + "-告警");
        else
            title.setText("暂无任何告警");
    }

    @Override
    public void showPoint() {
        Cursor uncheckCenterCursor = db.query("CENTER", Config.CENTER_COLUMN,
                null, null, null, null, null);
        while (uncheckCenterCursor.moveToNext()) {
            if (uncheckCenterCursor.getInt(3) == 1) {
                uncheckCenterMap.put(uncheckCenterCursor.getString(1), uncheckCenterCursor.getString(2));
            }
        }
        uncheckCenterCursor.close();
        imgRightBtnBadgeView.setText("●");
    }

    //告警分类列表的ViewHolder
    class ViewHolder {
        @Bind(R.id.alarmLogo)
        ImageView alarmLogo;

        @Bind(R.id.alarmCategoryTitle)
        TextView alarmCategoryTitle;

        @Bind(R.id.latestAlarm)
        TextView latestAlarm;

        @Bind(R.id.alarmSendTime)
        TextView alarmSendTime;

        BadgeView badgeView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            badgeView = new BadgeView(getActivity());
            badgeView.setTargetView(alarmLogo);
            badgeView.setBadgeCount(0);
        }
    }

}
