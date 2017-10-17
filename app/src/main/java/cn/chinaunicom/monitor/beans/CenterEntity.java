package cn.chinaunicom.monitor.beans;

/**
 * Created by yfyang on 2017/8/10.
 */

public class CenterEntity {
    public String itemId;
    public String title;
    public int isUncheck; //1:有新消息 0：没有

    public CenterEntity(String itemId, String title){
        this.itemId = itemId;
        this.title = title;
    }

    public CenterEntity(){
    }
}
