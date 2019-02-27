package com.mycompany.fyp;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;


public class Device {
    private ArrayList<SignalInfo> packets = new ArrayList<SignalInfo>();
    public final int TIME_GAP = 5000; // milsec
    private String sourceMac = null;
    
    public void removeOutdatedPacket(){
        long newest = 0;
        long tmpTimeStamp;
        for(SignalInfo p : packets){
            tmpTimeStamp = p.getTimeStamp();
            if(tmpTimeStamp > newest){
                newest = tmpTimeStamp;
            }
        }
        for(int i = 0 ; i < packets.size() ;){
            tmpTimeStamp = packets.get(i).getTimeStamp();
            if(tmpTimeStamp < newest - TIME_GAP){
                packets.remove(i);
                continue;
            }
            i++;
        }        
    }
    
    public JSONArray createJSONArray(){
        JSONArray ja = new JSONArray();
        for(SignalInfo p : packets){
            ja.put(p.getJSON());
        }
        return ja;
    }
    
    
    public void addPacket(SignalInfo p){
        packets.add(p);
        if(sourceMac == null){
            sourceMac = p.getSourceMac();
        }
    }

    public String getSourceMac() {
        return sourceMac;
    }
}