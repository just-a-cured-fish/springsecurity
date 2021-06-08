package com.yxh.springsecurity.bean;

import lombok.Data;

@Data
public class Node {
    private String path;
    private String name;
    private String type;
    private String len;
    private String owner;
    private String modifytime;
    public void setLen(long len){
            this.len=convertFileSize(len);
    }
    public   String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

}
