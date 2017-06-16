package com.a45g.athena.connectivitymonitor;

public class ConnectivityOutput {
    private long mId;
    private String mTimestamp;
    private String mInterface;
    private String mEvent;
    private String mDetails;

    public ConnectivityOutput(long id, String timestamp, String iface, String event, String details){
        mId = id;
        mTimestamp = timestamp;
        mInterface = iface;
        mEvent = event;
        mDetails = details;
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

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        this.mDetails = details;
    }

    public String getInterface() {
        return mInterface;
    }

    public void setInterface(String iface) {
        this.mInterface = iface;
    }

    public String getEvent() {
        return mEvent;
    }

    public void setEvent(String event) {
        this.mEvent = event;
    }
}
