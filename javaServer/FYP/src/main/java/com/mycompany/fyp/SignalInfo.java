package com.mycompany.fyp;

import org.json.JSONObject;

public class SignalInfo {
    private String sourceMac;
    private String destinationMac;
    private int RSSI;
    private long timeStamp;
    
    public SignalInfo(String sourceMac,String destinationMac,int RSSI,long timeStamp){
        this.sourceMac = sourceMac;
        this.destinationMac = destinationMac;
        this.RSSI = RSSI;
        this.timeStamp = timeStamp;
    }
    
    public JSONObject getJSON(){
        JSONObject obj = new JSONObject();
        obj.put("responsibleRouter", getDestinationMac());
        obj.put("RSSI", getRSSI());
        return obj;
    }    

    public String getSourceMac() {
        return sourceMac;
    }

    public void setSourceMac(String sourceMac) {
        this.sourceMac = sourceMac;
    }

    public String getDestinationMac() {
        return destinationMac;
    }

    public void setDestinationMac(String destinationMac) {
        this.destinationMac = destinationMac;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
