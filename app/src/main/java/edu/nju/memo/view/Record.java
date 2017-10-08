package edu.nju.memo.view;

/**
 * Created by Guo on 2017/10/6.
 */

public class Record {
    private int imageId;

    private String date;

    public Record(int imageId, String date) {
        this.imageId = imageId;
        this.date = date;
    }

    public int getImageId() {
        return imageId;
    }

    public String getDate() {
        return date;
    }
}
