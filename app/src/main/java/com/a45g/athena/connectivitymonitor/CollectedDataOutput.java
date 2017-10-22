package com.a45g.athena.connectivitymonitor;

public class CollectedDataOutput {
    private long id;
    private String timestamp;
    private String rx_wlan;
    private String rx_lte;
    private String tx_wlan;
    private String tx_lte;
    private String rssi_wlan;
    private String rssi_lte;
    private String rtt_wlan;
    private String rtt_lte;

    public CollectedDataOutput (long id, String timestamp, String rx_wlan, String rx_lte,
                                String tx_wlan, String tx_lte, String rssi_wlan,
                                String rssi_lte, String rtt_wlan, String rtt_lte){
        this.id = id;
        this.timestamp = timestamp;
        this.rx_wlan = rx_wlan;
        this.rx_lte = rx_lte;
        this.tx_wlan = tx_wlan;
        this.tx_lte = tx_lte;
        this.rssi_wlan = rssi_wlan;
        this.rssi_lte = rssi_lte;
        this.rtt_wlan = rtt_wlan;
        this.rtt_lte = rtt_lte;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRx_wlan() {
        return rx_wlan;
    }

    public void setRx_wlan(String rx_wlan) {
        this.rx_wlan = rx_wlan;
    }

    public String getRx_lte() {
        return rx_lte;
    }

    public void setRx_lte(String rx_lte) {
        this.rx_lte = rx_lte;
    }

    public String getTx_wlan() {
        return tx_wlan;
    }

    public void setTx_wlan(String tx_wlan) {
        this.tx_wlan = tx_wlan;
    }

    public String getTx_lte() {
        return tx_lte;
    }

    public void setTx_lte(String tx_lte) {
        this.tx_lte = tx_lte;
    }

    public String getRssi_wlan() {
        return rssi_wlan;
    }

    public void setRssi_wlan(String rssi_wlan) {
        this.rssi_wlan = rssi_wlan;
    }

    public String getRssi_lte() {
        return rssi_lte;
    }

    public void setRssi_lte(String rssi_lte) {
        this.rssi_lte = rssi_lte;
    }

    public String getRtt_wlan() {
        return rtt_wlan;
    }

    public void setRtt_wlan(String rtt_wlan) {
        this.rtt_wlan = rtt_wlan;
    }

    public String getRtt_lte() {
        return rtt_lte;
    }

    public void setRtt_lte(String rtt_lte) {
        this.rtt_lte = rtt_lte;
    }

    public String getDetails(){
        String details = "\nWLAN: Rx="+rx_wlan+" Tx="+tx_wlan+
                " RSSI="+rssi_wlan+" RTT="+rtt_wlan+
                "\nLTE: Rx="+rx_lte+" Tx="+tx_lte+
                " RSSI="+rssi_lte+" RTT="+rtt_lte;

        return details;
    }
}
