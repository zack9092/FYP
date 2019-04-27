/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fyp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 *
 * @author User
 */
public class DecisionTree {
	public static String[] routerMac = {"0023CD00C51B","00E02C312194","00E02C310F37"};// ordered
	public static String[] openarea = {"c9a","c9b"};// ordered   
public static void main(String[] args){	
    String a = "";
    HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead 
    try {
        HttpGet request = new HttpGet("http://it27fyp2019.appspot.com/getDeviceArray?key=IT27");
        request.addHeader("content-type", "application");
        HttpResponse response = httpClient.execute(request);
        String json_string = EntityUtils.toString(response.getEntity());
        //System.out.println(json_string); 
        a = json_string;
        //handle response here...
    }catch (Exception ex) {
        //handle exception here
        System.out.println("GET ERROR");
    }
  
	final int NUM_OF_ATTRIBUTE  = 1;
//     a = "{\"allPackets\":[{\"14CC20C04DA2\":[{\"timeStamp\":1551190456277,\"RSSI\":-80,\"responsibleRouter\":\"AABBCCDDEEFF\"},"
//    		+ "{\"timeStamp\":1551190456232,\"RSSI\":-92,\"responsibleRouter\":\"AABBCCDDEEFF\"}]"
//    		+ ",\"64098070359B\":[{\"timeStamp\":1551190430912,\"RSSI\":-82,\"responsibleRouter\":\"AABBCCDDEEFF\"}]}"
//    		+ ",{\"14CC20C04DA2\":[{\"timeStamp\":1551190456277,\"RSSI\":-70,\"responsibleRouter\":\"AABBCCDDEEFF\"}]}]}";
    HashMap<String, JSONArray> deviceMap = new HashMap<>();
	JSONArray tmpArray;
	JSONArray putArray;
//		Break down the JSON
    	JSONObject allPackets = new JSONObject(a);
    	JSONArray allPacketsArray = (JSONArray)allPackets.get("allPackets");
    	for(int i=0 ; i<allPacketsArray.length(); i++){
//    		System.out.println(allPacketsArray.length());
    		JSONObject allMac = (JSONObject)allPacketsArray.get(i);
    		Iterator<String> keys = allMac.keys();
    		for(int j=0 ; j<allMac.length(); j++){
//    			System.out.println(allMac.length());
//    	Group packets with their MAC   
    			String str_Name=keys.next();
//    			System.out.println(str_Name);
    			if(!deviceMap.containsKey(str_Name)) {
    				deviceMap.put(str_Name, new JSONArray());
    			}
    			tmpArray = deviceMap.get(str_Name);
    			putArray = (JSONArray)allMac.get(str_Name);
//    			System.out.println(putArray.length());
    			for(int k=0 ; k<putArray.length(); k++) {
    				tmpArray.put(putArray.get(k));
    			}	
    		}
    	}
    
//    	System.out.println(deviceMap);

//    	Filter out useless timestamp from same routerMac
    	Set<String> deviceMapKeys = deviceMap.keySet();
    	ArrayList<ArrayList<JSONObject>> newDeviceList = new ArrayList<>();
    	for(String s :deviceMapKeys) {
    		ArrayList<JSONObject> newEachDevice = new ArrayList<>();
    		//try only one MAC address
    		if(!s.equalsIgnoreCase("E8E8B75847A9")) {
    			//System.out.println(s);
    			continue;
    		}
        	for(String rMac : routerMac) {  			
    			JSONArray ss = deviceMap.get(s);
    			long newestTimeStamp = 0;
    			int newestIndex = -1;
    			JSONObject obj;
    			for(int j=0;j<ss.length();j++) {
    				obj = ss.getJSONObject(j);
    				if(newestTimeStamp < Long.parseLong(obj.get("timeStamp")+"") && obj.getString("responsibleRouter").equalsIgnoreCase(rMac)) { // add code to do more filter
    				newestIndex = j;
    				}
    			}
    			if(newestIndex!=-1) {
    				obj = ss.getJSONObject(newestIndex);
    				newEachDevice.add(obj);
    			}
    		}
    		newDeviceList.add(newEachDevice);
    	}
   	
//    	format all records to the prediction array format
    	JSONObject locations;
    	locations = decisionTree(newDeviceList);
    	System.out.println(locations);
		// Upload to nodejs
		try {
            HttpPost request = new HttpPost("http://it27fyp2019.appspot.com/deviceLocation");
            StringEntity params =new StringEntity("Details="+locations.toString());
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            //handle response here...
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            System.out.println(content);
        }catch (Exception ex) {
            //handle exception here
            System.out.println("POST LOCATION ERROR");
        }
}

public static JSONObject decisionTree(ArrayList<ArrayList<JSONObject>> dataset) {
	JSONObject locations = new JSONObject();
	int[] sittingArea = {0,0};// sofa , orange
	for(ArrayList<JSONObject> data :dataset) {
		int[] rssiArray = {100,100,100};
		for(JSONObject da : data) {
			for(int i = 0 ; i < routerMac.length ; i++) {
				if(da.getString("responsibleRouter").equals(routerMac[i])) {
					rssiArray[i] = da.getInt("RSSI");					
					break;
				}				
			}
		}
		// Do prediction on one data
		System.out.println(rssiArray[0]+" "+rssiArray[1]+" "+rssiArray[2]);
		if(rssiArray[0]<-35 && rssiArray[0]>-64 && rssiArray[1]<-27 && rssiArray[1]>-46 && rssiArray[2]<-49 && rssiArray[2]>-69){//Check for sofa
			System.out.println("Sofa");
			sittingArea[0]++;
		}else
		if(rssiArray[0]<-51 && rssiArray[0]>-68 && ((rssiArray[1]<-60 && rssiArray[1]>-79) || rssiArray[1] == 100) && rssiArray[2]<-46 && rssiArray[2]>-59){
			System.out.println("orange");
			sittingArea[1]++;
		}else {
			System.out.println("other");
		}
	}
	for(int i = 0 ; i < openarea.length ; i++) {
		locations.put(openarea[i], sittingArea[i]);
	}
	return locations;
}
}
