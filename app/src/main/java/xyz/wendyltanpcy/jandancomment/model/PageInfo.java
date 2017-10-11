package xyz.wendyltanpcy.jandancomment.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Wendy on 2017/10/11.
 */

public class PageInfo extends DataSupport {
    private int latestPageNum;

    public void setLatestPageNum(int latestPageNum) {
        this.latestPageNum = latestPageNum;
    }

    public int getLatestPageNum() {
        return latestPageNum;
    }

}
