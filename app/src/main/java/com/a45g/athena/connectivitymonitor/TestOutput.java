package com.a45g.athena.connectivitymonitor;

public class TestOutput {
    private long mId;
    private String mTimestamp;
    private String mType;
    private String mValue;

    public TestOutput(long id, String timestamp, String type, String value) {
        mId = id;
        mTimestamp = timestamp;
        mType = type;
        mValue = value;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        this.mTimestamp = timestamp;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        this.mValue = value;
    }
}
