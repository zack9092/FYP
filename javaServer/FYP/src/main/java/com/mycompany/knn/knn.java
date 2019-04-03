/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.knn;

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
public class knn {
    public static final String[] locLibrary= {"","","","","","","C4A","C5A","C5B",""};

    
public static void main(String[] args){	
    String a = "";
    HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead 
    try {
        HttpGet request = new HttpGet("http://localhost:3000/getDeviceArray");
        request.addHeader("content-type", "application");
        HttpResponse response = httpClient.execute(request);
        String json_string = EntityUtils.toString(response.getEntity());
        System.out.println(json_string); 
        a = json_string;
        //handle response here...
    }catch (Exception ex) {
        //handle exception here
        System.out.println("GET ERROR");
    }
  
	final int NUM_OF_ATTRIBUTE  = 1;
//    String a = "{\"allPackets\":[{\"14CC20C04DA2\":[{\"timeStamp\":1551190456277,\"RSSI\":-80,\"responsibleRouter\":\"AABBCCDDEEFF\"},"
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
    
    	System.out.println(deviceMap);

//    	Filter out useless timestamp from same USB
    	Set<String> deviceMapKeys = deviceMap.keySet();
    	HashMap<String, JSONObject> newDeviceMap = new HashMap<>();
    	for(String s :deviceMapKeys) {
    		//try only one MAC address
    		if(!s.equalsIgnoreCase("E8E8B75847A9")) {
    			System.out.println(s);
    			continue;
    		}
    		JSONArray ss = deviceMap.get(s);
    		long newestTimeStamp = 0;
    		int newestIndex = 0;
    		JSONObject obj;
    		for(int j=0;j<ss.length();j++) {
    			obj = ss.getJSONObject(j);
    			if(newestTimeStamp < Long.parseLong(obj.get("timeStamp")+"")) { // add code to do more filter
    				newestIndex = j;
    			}
    		}
    		obj = ss.getJSONObject(newestIndex);
    		newDeviceMap.put(s, obj);
    	}
    
    	System.out.println(newDeviceMap);
    	
//    	format all records to the prediction array format
    	
    
    
        String basePath = new File("").getAbsolutePath();
		System.out.println();
		//knn(basePath+"/knnData"+"/apl_train.txt",basePath+"/knnData"+"/apl_test.txt",1,2);
		
//	    Creating the array for prediction
		List<TestRecord> predictList = new ArrayList<>();	
		
//		Initialize the array

	    for(String s :deviceMapKeys) {
    		//try only one MAC address
    		if(!s.equalsIgnoreCase("E8E8B75847A9")) {
    			continue;
    		}
	    	double[] da = new double[NUM_OF_ATTRIBUTE];
	    	da[0] = 00;
			da[0] = newDeviceMap.get(s).getDouble("RSSI");
			predictList.add(new TestRecord(da,-1));	
	    }

		TestRecord[] predictArray = new TestRecord[ predictList.size() ];
		predictList.toArray( predictArray );
		JSONObject locations;
		locations = knn(basePath+"/knnData"+"/apl_train.txt",predictArray,1,2);
		System.out.println(locations);
		// Upload to nodejs
		try {
            HttpPost request = new HttpPost("http://localhost:3000/deviceLocation");
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
	
	public static JSONObject knn(String trainingFile, TestRecord[] testingSet, int K, int metricType){
		//get the current time
		final long startTime = System.currentTimeMillis();
		JSONObject locations = new JSONObject();
		locations.put("C4A", 0);
		locations.put("C5A", 0);
              locations.put("C5B", 0);
		
		// make sure the input arguments are legal
		if(K <= 0){
			System.out.println("K should be larger than 0!");
			return locations;
		}
		
		// metricType should be within [0,2];
		if(metricType > 2 || metricType <0){
			System.out.println("metricType is not within the range [0,2]. Please try again later");
			return locations;
		}
		
		
		
		try {
			//read trainingSet and testingSet
			TrainRecord[] trainingSet =  FileManager.readTrainFile(trainingFile);
			//TestRecord[] testingSet =  FileManager.readTestFile(testFile);
			
			//determine the type of metric according to metricType
			Metric metric;
			if(metricType == 0)
				metric = new CosineSimilarity();
			else if(metricType == 1)
				metric = new L1Distance();
			else if (metricType == 2)
				metric = new EuclideanDistance();
			else{
				System.out.println("The entered metric_type is wrong!");
				return locations;
			}
			
			//test those TestRecords one by one
			int numOfTestingRecord = testingSet.length;
			for(int i = 0; i < numOfTestingRecord; i ++){
				TrainRecord[] neighbors = findKNearestNeighbors(trainingSet, testingSet[i], K, metric);
				int classLabel = classify(neighbors);
				System.out.println("Predicting "+testingSet[i].attributes[0]+" as class label "+classLabel);
				testingSet[i].predictedLabel = classLabel; //assign the predicted label to TestRecord
				System.out.println(classLabel);
				int num = (int)locations.get(locLibrary[classLabel]);
				num++;
				locations.put(locLibrary[classLabel], num);
				
			}
			
			//calculate the accuracy
			int correctPrediction = 0;
			for(int j = 0; j < numOfTestingRecord; j ++){
				if(testingSet[j].predictedLabel == testingSet[j].classLabel)
					correctPrediction ++;
			}
			
			//Output a file containing predicted labels for TestRecords
			String predictPath = FileManager.outputFile(testingSet, trainingFile);
			System.out.println("The prediction file is stored in "+predictPath);
			System.out.println("The accuracy is "+((double)correctPrediction / numOfTestingRecord)*100+"%");
			
			//print the total execution time
			final long endTime = System.currentTimeMillis();
			System.out.println("Total excution time: "+(endTime - startTime) / (double)1000 +" seconds.");
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		return locations;
	}
	
	// Find K nearest neighbors of testRecord within trainingSet 
	static TrainRecord[] findKNearestNeighbors(TrainRecord[] trainingSet, TestRecord testRecord,int K, Metric metric){
		int NumOfTrainingSet = trainingSet.length;
		assert K <= NumOfTrainingSet : "K is lager than the length of trainingSet!";
		
		//Update KNN: take the case when testRecord has multiple neighbors with the same distance into consideration
		//Solution: Update the size of container holding the neighbors
		TrainRecord[] neighbors = new TrainRecord[K];
		
		//initialization, put the first K trainRecords into the above arrayList
		int index;
		for(index = 0; index < K; index++){
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);
			neighbors[index] = trainingSet[index];
		}
		
		//go through the remaining records in the trainingSet to find K nearest neighbors
		for(index = K; index < NumOfTrainingSet; index ++){
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);
			
			//get the index of the neighbor with the largest distance to testRecord
			int maxIndex = 0;
			for(int i = 1; i < K; i ++){
				if(neighbors[i].distance > neighbors[maxIndex].distance)
					maxIndex = i;
			}
			
			//add the current trainingSet[index] into neighbors if applicable
			if(neighbors[maxIndex].distance > trainingSet[index].distance)
				neighbors[maxIndex] = trainingSet[index];
		}
		
		return neighbors;
	}
	
	// Get the class label by using neighbors
	static int classify(TrainRecord[] neighbors){
		//construct a HashMap to store <classLabel, weight>
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		int num = neighbors.length;
		
		for(int index = 0;index < num; index ++){
			TrainRecord temp = neighbors[index];
			int key = temp.classLabel;
		
			//if this classLabel does not exist in the HashMap, put <key, 1/(temp.distance)> into the HashMap
			if(!map.containsKey(key))
				map.put(key, 1 / temp.distance);
			
			//else, update the HashMap by adding the weight associating with that key
			else{
				double value = map.get(key);
				value += 1 / temp.distance;
				map.put(key, value);
			}
		}	
		
		//Find the most likely label
		double maxSimilarity = 0;
		int returnLabel = -1;
		Set<Integer> labelSet = map.keySet();
		Iterator<Integer> it = labelSet.iterator();
		
		//go through the HashMap by using keys 
		//and find the key with the highest weights 
		while(it.hasNext()){
			int label = it.next();
			double value = map.get(label);
			if(value > maxSimilarity){
				maxSimilarity = value;
				returnLabel = label;
			}
		}
		
		return returnLabel;
	}
	
	static String extractGroupName(String filePath){
		StringBuilder groupName = new StringBuilder();
		for(int i = 0; i < filePath.length(); i ++){
			if(filePath.charAt(i) != '_')
				groupName.append(filePath.charAt(i));
			else
				break;
		}
		
		return groupName.toString();
	} 
}
