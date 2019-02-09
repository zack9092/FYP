package com.mycompany.fyp;

import org.json.JSONObject;

public class SignalInfo {
    private String sourceMac;
    private String destinationMac;
    private int RSSI;
    private int timeStamp;
    
    public SignalInfo(String sourceMac,String destinationMac,int RSSI,int timeStamp){
        this.sourceMac = sourceMac;
        this.destinationMac = destinationMac;
        this.RSSI = RSSI;
        this.timeStamp = timeStamp;
    }
    
    public JSONObject getJSON(){
        JSONObject obj = new JSONObject();
        obj.put("destinationMac", getDestinationMac());
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

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }
}
