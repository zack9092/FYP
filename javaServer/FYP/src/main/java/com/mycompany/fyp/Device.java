package com.mycompany.fyp;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;


public class Device {
    private ArrayList<SignalInfo> packets = new ArrayList<SignalInfo>();
    public final int TIME_GAP = 5;
    
    public void removeOutdatedPacket(){
        int newest = 0;
        int tmpTimeStamp;
        for(SignalInfo p : packets){
            tmpTimeStamp = p.getTimeStamp();
            if(tmpTimeStamp > newest){
                newest = tmpTimeStamp;
            }
        }
        for(SignalInfo p : packets){
            tmpTimeStamp = p.getTimeStamp();
            if(tmpTimeStamp < newest - TIME_GAP){
                packets.remove(p);
            }
        }        
    }
    
    public JSONObject createJSON(){
        JSONObject obj = new JSONObject();
        JSONArray ja = new JSONArray();
        for(SignalInfo p : packets){
            ja.put(p.getJSON());
        }
        obj.put("packets",ja);
        return obj;
    }
    
    
    public void addPacket(SignalInfo p){
        packets.add(p);
    }
}