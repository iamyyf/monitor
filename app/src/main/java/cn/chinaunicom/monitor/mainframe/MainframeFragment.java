package cn.chinaunicom.monitor.mainframe;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zaaach.toprightmenu.MenuItem;
import com.zaaach.toprightmenu.TopRightMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chinaunicom.monitor.ChinaUnicomApplication;
import cn.chinaunicom.monitor.R;
import cn.chinaunicom.monitor.adapters.GridAdapter;
import cn.chinaunicom.monitor.beans.CenterEntity;
import cn.chinaunicom.monitor.http.Http;
import cn.chinaunicom.monitor.http.Request.BaseReq;
import cn.chinaunicom.monitor.http.Request.GridViewReq;
import cn.chinaunicom.monitor.http.Response.CenterFlagResp;
import cn.chinaunicom.monitor.http.Response.GridViewResp;
import cn.chinaunicom.monitor.utils.Config;
import cn.chinaunicom.monitor.utils.Utils;
import es.dmoral.toasty.Toasty;

/**
 * @author yfYang
 */
public class MainframeFragment extends Fragment {

    private TopRightMenu menu;
    private List<MenuItem> menuItems = new ArrayList<>();
    private GridAdapter gridAdapter;

    @Bind(R.id.gridView)
    GridView gridView;

    @Bind(R.id.imgBtnRight)
    ImageButton imgBtnRight;

    @Bind(R.id.imgBtnLeft)
    ImageButton imgBtnLeft;

    @Bind(R.id.txtViewTitle)
    TextView title;

    @OnClick(R.id.imgBtnLeft)
    void showDataSet() {
        Intent intent = new Intent(getActivity(), DataSetChartActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.deployCondition)
    void showDeployCondition() {
        Intent intent = new Intent(getContext(), DeployListActivity.class);
        if (null != ChinaUnicomApplication.mainframeCurCenter) {
            intent.putExtra("CUR_CENTER_ITEM_ID", ChinaUnicomApplication.mainframeCurCenter.itemId);
            intent.putExtra("CUR_CENTER_TITLE", ChinaUnicomApplication.mainframeCurCenter.title);
            startActivity(intent);
        } else {
            Utils.showErrorToast(getContext(), "请等待所属中心数据请求完毕后再查看!");
        }
    }

    @OnClick(R.id.mainframeCondition)
    void showMainframeCondition() {
        Intent intent = new Intent(getContext(), MainframeListActivity.class);
        if (null != ChinaUnicomApplication.mainframeCurCenter) {
            intent.putExtra("CUR_CENTER_ITEM_ID", ChinaUnicomApplication.mainframeCurCenter.itemId);
            intent.putExtra("CUR_CENTER_TITLE", ChinaUnicomApplication.mainframeCurCenter.title);
            startActivity(intent);
        } else {
            Utils.showErrorToast(getContext(), "请等待所属中心数据请求完毕后再查看!");
        }
    }

    @OnClick(R.id.imgBtnRight)
    void popMenu() {
        menu.showAsDropDown(imgBtnRight, 0, 0);
    }

    public MainframeFragment() {
        // Required empty public constructor
    }

    public static MainframeFragment getInstance() {
        return new MainframeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mainframe, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        initTitleBar();
        initTopRigtMenu();
        if (Utils.isListEmpty(ChinaUnicomApplication.gridInCenterList))
            startGridTask();
        else
            initMenuItem(ChinaUnicomApplication.mainframeCenterList);

    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imgBtnRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_menu, null));
            imgBtnLeft.setImageDrawable(getResources().getDrawable(R.mipmap.ic_top_dataset, null));
        } else {
            imgBtnRight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_menu));
            imgBtnLeft.setImageDrawable(getResources().getDrawable(R.mipmap.ic_top_dataset));
        }
    }

    private void initGridView() {
        gridAdapter = new GridAdapter(getContext(), ChinaUnicomApplication.mainframeCurGrid);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChinaUnicomApplication.curChartCells.clear();
                ChinaUnicomApplication.curChartCells
                        .addAll(ChinaUnicomApplication.mainframeCurGrid.get(position).cells);
                showActivity(ChartListActivity.class, position);
            }
        });
    }

    private void initTopRigtMenu() {
        menu = new TopRightMenu(getActivity());
        menu.setHeight(Config.POP_UP_DIALOG_HEIGHT)
                .setWidth(Config.POP_UP_DIALOG_WIDTH)
                .dimBackground(true)
                .needAnimationStyle(true)
                .setHeight(Config.TOP_RIGHT_MENU_HEIGHT)
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
        if (null != ChinaUnicomApplication.mainframeCurCenter) {
            ChinaUnicomApplication.mainframeCurCenter.title = ChinaUnicomApplication.mainframeCenterList.get(position).title;
            ChinaUnicomApplication.mainframeCurCenter.itemId = ChinaUnicomApplication.mainframeCenterList.get(position).itemId;
        } else {
            ChinaUnicomApplication.mainframeCurCenter = new CenterEntity(ChinaUnicomApplication.mainframeCenterList.get(position).itemId,
                    ChinaUnicomApplication.mainframeCenterList.get(position).title);
        }
        updateGridList();
        gridAdapter.notifyDataSetChanged();
        Utils.showSuccessToast(getContext(), "已切换到 " + ChinaUnicomApplication.mainframeCurCenter.title);
        title.setText(ChinaUnicomApplication.mainframeCurCenter.title);
    }

    //根据GridView点击的按钮跳转到相应的Activity
    private void showActivity(Class target, int itemPos) {
        startActivity(new Intent(getActivity(), target)
                .putExtra("Monitor_Item", ChinaUnicomApplication.mainframeCurGrid.get(itemPos).title));
    }

    private void startCenterTask() {
        CenterListAsyncTask task = new CenterListAsyncTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    private void startGridTask() {
        GridViewTask task = new GridViewTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    public class GridViewTask extends AsyncTask<Void, Void, GridViewResp> {
        private GridViewReq req;

        @Override
        protected void onPreExecute() {
            req = new GridViewReq();
            req.userToken = ChinaUnicomApplication.token;
            super.onPreExecute();
        }

        @Override
        protected GridViewResp doInBackground(Void... params) {
            GridViewResp resp = new Http.Builder().create().getGridInCenter(req);
            return resp;
        }

        @Override
        protected void onPostExecute(GridViewResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                if (!Utils.isListEmpty(resp.data.records)) {
                    ChinaUnicomApplication.gridInCenterList.clear();
                    ChinaUnicomApplication.gridInCenterList.addAll(resp.data.records);
                }
            } else {
            }
            startCenterTask();
        }
    }

    private class CenterListAsyncTask extends AsyncTask<Void, Void, CenterFlagResp> {

        private BaseReq req;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            req = new BaseReq();
            req.userToken = ChinaUnicomApplication.token;
        }

        @Override
        protected CenterFlagResp doInBackground(Void... params) {
            CenterFlagResp resp = new Http.Builder().create().getCenterList(req);
            return resp;
        }

        @Override
        protected void onPostExecute(CenterFlagResp resp) {
            super.onPostExecute(resp);
            if (Utils.isRequestSuccess(resp)) {
                ChinaUnicomApplication.mainframeCenterList.clear();
                ChinaUnicomApplication.mainframeCenterList.addAll(resp.data.records);
                initMenuItem(ChinaUnicomApplication.mainframeCenterList);
            } else
                Toasty.error(getContext(), Config.TOAST_REQUEST_FAILED, Toast.LENGTH_SHORT).show();
        }
    }

    //初始化menu的item
    private void initMenuItem(List<CenterEntity> data) {
        for (int i = 0; i < data.size(); i++)
            menuItems.add(new MenuItem(data.get(i).title));
        menu.addMenuList(menuItems);
        if (null == ChinaUnicomApplication.mainframeCurCenter) {
            ChinaUnicomApplication.mainframeCurCenter = new CenterEntity(data.get(0).itemId, data.get(0).title);
        }
        updateGridList();
        initGridView();
        title.setText(ChinaUnicomApplication.mainframeCurCenter.title);
    }

    private void updateGridList() {
        ChinaUnicomApplication.mainframeCurGrid.clear();
        for (int i = 0; i < ChinaUnicomApplication.gridInCenterList.size(); i++) {
            if (ChinaUnicomApplication.gridInCenterList.get(i).centerId.equals(ChinaUnicomApplication.mainframeCurCenter.itemId) &&
                    ChinaUnicomApplication.gridInCenterList.get(i).centerName.equals(ChinaUnicomApplication.mainframeCurCenter.title)) {
                ChinaUnicomApplication.mainframeCurGrid.addAll(ChinaUnicomApplication.gridInCenterList.get(i).monitorItems);
            }
        }
    }
}
