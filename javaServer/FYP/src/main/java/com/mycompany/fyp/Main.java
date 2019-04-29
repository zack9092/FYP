package com.mycompany.fyp;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject; 
import org.pcap4j.core.NotOpenException; 
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException; 
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import java.io.EOFException; 
import java.util.Arrays;
import java.util.concurrent.TimeoutException; 

public class Main {

     
private static int COUNT = 1000; 
private static final long UPLOAD_RATE_SECONDS = 15; // how many seconds until consider a old file
private static final String PCAP_FILE_KEY 
    = ReadPacketFile.class.getName() + ".pcapFile"; 
private static String PCAP_FILE 
    = System.getProperty(PCAP_FILE_KEY, "C:/Users/User/Desktop/testing.pcap"); 
private static String receiverMac = "00E02C310F37";  
//mac of the wireless adapter["00E02C310F37","00E02C312195","00E02F60EA64"]

private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}
    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        final int SLEEP_TIME = 5; //seconds
        int mode = 1; // 1 for reading pcap , -1 for check booking
        /*
        //TESTING
        SignalInfo p = new SignalInfo("source","destination",100,1235);
        Device d= new Device();
        d.addPacket(p);
        
        System.out.println(p.getJSON());
        
        //Start loading pcap file
    final int COUNT = 2; 
    final String PCAP_FILE_KEY 
        = ReadPacketFile.class.getName() + ".pcapFile"; 
    final String PCAP_FILE 
        = System.getProperty(PCAP_FILE_KEY, "C:/Users/User/Desktop/radiotap.pcap");  

    PcapHandle handle; 
    try { 
      handle = Pcaps.openOffline(PCAP_FILE, TimestampPrecision.NANO); 
    } catch (PcapNativeException e) { 
      handle = Pcaps.openOffline(PCAP_FILE); 
    } 
 
    for (int i = 0; i < COUNT; i++) { 
      try { 
        Packet packet = handle.getNextPacketEx(); 
        // format packet data into SignalInfo here
        System.out.println(handle.getTimestamp()); 
        String tmp = packet.getPayload().toString();
        System.out.println(tmp); 
      } catch (TimeoutException e) { 
      } catch (EOFException e) { 
        System.out.println("EOF"); 
        break; 
      } 
    } 
    handle.close(); 
            //END TESTING
*/


            String basePath = new File("").getAbsolutePath();
            final File folder = new File(basePath+"/radiotap");
            try{              
            while(true){
            if(mode == 1) {
            mode *= -1;
        	System.out.println("Doing packet mode");
            Thread.sleep(SLEEP_TIME * 1000);    
            ArrayList<SignalInfo> signalInfos = new ArrayList<>();
            ArrayList<Device> devices = new ArrayList<>();
                        
            //read and store all packet data from files
            File[] fileEntry = folder.listFiles();
            for (int i=0 ; i < fileEntry.length ; i++) {
                PcapHandle handle; 
                String fileName = fileEntry[i].getName();
                if(fileName.contains(".pcap-")){               	
                    PCAP_FILE = System.getProperty(PCAP_FILE_KEY, basePath+"/radiotap/"+fileName); 
                    //Check if the file is old and delete it
                	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                	long currentSeconds = timestamp.getTime()/1000;
                	long fileSceonds = Long.parseLong(PCAP_FILE.substring(PCAP_FILE.indexOf("-")+1));
                    System.out.println(PCAP_FILE);
                    long timeDifference = (currentSeconds - fileSceonds);
                    System.out.println("The time difference between the pcap file and now is: "+timeDifference);
                    if(timeDifference > UPLOAD_RATE_SECONDS) {
                    	System.out.println("Bad file");
                		if(fileEntry[i].delete()){
                			System.out.println(fileEntry[i].getName() + " is deleted!");
                		}else{
                			System.out.println("Delete operation is failed.");
                		}
                    	continue;
                    }
                    //start processing packets
                    try { 
                      handle = Pcaps.openOffline(PCAP_FILE, PcapHandle.TimestampPrecision.NANO); 
                    } catch (PcapNativeException e) { 
                      handle = Pcaps.openOffline(PCAP_FILE); 
                    } 
                    for (int j = 0; j < COUNT; j++) { 
                      try { 
                        Packet packet = handle.getNextPacketEx();
                        System.out.println(handle.getTimestamp().getTime()); 
                        byte[] bytePayload = packet.getPayload().getRawData();
//                      Maybe checking if packet is of a certain format (expect DS 00)
//                        if((bytePayload[1] & 1)!= 1){
//                            System.out.println("DS flag is not set, wrong packet");
//                            continue;
//                        }
                        String sourceMac = bytesToHex(Arrays.copyOfRange(bytePayload,10,16));
                        System.out.println("This packet have a source Mac of :" + sourceMac);
                        System.out.println("This packet have a receiver Mac of :" + receiverMac);
                        int rssi = 100;
                        long timeStamp = handle.getTimestamp().getTime();
                        byte[] byteHeader = packet.getHeader().getRawData();
                        int radiotapHeaderLength = byteHeader[2];
                        System.out.println("This packet have a length of :" + radiotapHeaderLength);
                        rssi = byteHeader[14];//30 for TPlink , 14 for CNTEPE
                        System.out.println("This packet have a RSSI of :" + rssi);
                        // Mapping packet info into signalInfo object
                        SignalInfo p = new SignalInfo(sourceMac,receiverMac,rssi,timeStamp);
                        signalInfos.add(p);
                      } catch (TimeoutException e) { 
                      } catch (EOFException e) { 
                        System.out.println("EOF"); 
                        break; 
                      } catch (Exception e) {
                    	  
                      }
                    } 

                    
                handle.close(); 
                }
            }

            //group packets to their device
            Boolean newGroup = true; // flag for new group of packets
            String deviceMac = "";
            SignalInfo s;
            while(signalInfos.size()>0){
                Device device = new Device(); 
                newGroup = true;
                ArrayList<Integer> removeList = new ArrayList<Integer>();
                for (int i = 0 ; i < signalInfos.size() ; ) {
                    s = signalInfos.get(i);
                    if(newGroup){
                        deviceMac = s.getSourceMac();
                        newGroup = false;
                    }
                    if(deviceMac.equals(s.getSourceMac())){
                        device.addPacket(s); // group all packets from same device
                        signalInfos.remove(s);// remember packet to be removed 
                        continue;
                    }
                    i++;
                }
                device.removeOutdatedPacket();
                devices.add(device); // add device into the device list
                System.out.println(device.createJSONArray());
                System.out.println(signalInfos.size());
            }
            // Finished crafting the device list that requires indoor positioning
            
            // ADD CODE HERE for passing all data (in json) to the nodejs server
            JSONObject toNodejs = new JSONObject();
                for(Device d : devices){
                    toNodejs.put(d.getSourceMac(),d.createJSONArray());
                }
            System.out.println(toNodejs);
            
            HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead 
            try {
                HttpPost request = new HttpPost("http://it27fyp2019.appspot.com/devicesPacket");
                StringEntity params =new StringEntity("details="+toNodejs.toString());
                request.addHeader("content-type", "application/x-www-form-urlencoded");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                //handle response here...
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
                System.out.println(content);
            }catch (Exception ex) {
                //handle exception here
                System.out.println("POST DEVICE LOCATION ERROR");
            }
            }        	
            if(mode == -1) {
            	mode *= -1;
            	System.out.println("Doing checking mode");
            	Thread.sleep(SLEEP_TIME * 1000); 
            	HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead 
                try {
                    HttpGet request = new HttpGet("http://it27fyp2019.appspot.com/checkBooking");
                    request.addHeader("content-type", "application");
                    HttpResponse response = httpClient.execute(request);
                    //handle response here...
//                    HttpEntity entity = response.getEntity();
//                    String content = EntityUtils.toString(entity);
//                    System.out.println(content);
                }catch (Exception ex) {
                    //handle exception here
                    System.out.println("CHECK BOOKING ERROR");
                }           	
            }
        }
        }catch (Exception ex) {
            //handle exception here
            ex.printStackTrace();
        }
    }
}


