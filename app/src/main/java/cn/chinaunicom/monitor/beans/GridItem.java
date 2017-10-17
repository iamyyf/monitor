package cn.chinaunicom.monitor.beans;

import java.util.List;

/**
 * Created by yfYang on 2017/9/22.
 */

public class GridItem {
    public String monitorItemName;
    public String icon;
    public String title;
    public String controllerClassName; //IOS
    public List<CellEntity> cells;
}
