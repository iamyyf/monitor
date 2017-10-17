package cn.chinaunicom.monitor.beans;

import java.util.List;

/**
 * Created by yfyang on 2017/8/11.
 */

public class DeployCategoryEntity {
    public String itemId;
    public String title;
    public List<AppbelongEntity> belongs;

    public DeployCategoryEntity(String itemId, String title) {
        this.itemId = itemId;
        this.title = title;
    }
}
