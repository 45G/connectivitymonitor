package com.a45g.athena.connectivitymonitor;


public class OutputData {
    private String mValue;
    private String mTime;

    public OutputData(String value, String time) {
        super();
        this.mValue = value;
        this.mTime = time;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        this.mValue = value;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }
}
