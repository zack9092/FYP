package com.mycompany.fyp;

import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject; 
import org.pcap4j.core.NotOpenException; 
import org.pcap4j.core.PcapNativeException; 


public class Main {

    public static void main(String[] args) throws PcapNativeException, NotOpenException {
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
            
            //final File folder = new File("/home/you/Desktop");
            while(true){
            ArrayList<SignalInfo> signalInfos = new ArrayList<SignalInfo>();
            ArrayList<Device> devices = new ArrayList<Device>();
                        
            //read and store all packet data from files
            //for (final File fileEntry : folder.listFiles()) {
            //System.out.println(fileEntry.getName());
            //SOME CODE HERE for reading in 802.11 stuffs
            //SOME CODE HERE for putting packets into packet list
             SignalInfo p = new SignalInfo("source","destination",100,1235);
             SignalInfo p2 = new SignalInfo("source2","destination2",100,1235);
             SignalInfo p3 = new SignalInfo("source","destination3",100,1235);
             SignalInfo p4 = new SignalInfo("source","destination4",100,1235); 
             signalInfos.add(p);signalInfos.add(p2);signalInfos.add(p3);signalInfos.add(p4);
            //}
            
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
                HttpPost request = new HttpPost("http://localhost:3000/devicesPosition");
                StringEntity params =new StringEntity("details="+toNodejs.toString());
                request.addHeader("content-type", "application/x-www-form-urlencoded");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                //handle response here...
            }catch (Exception ex) {
                //handle exception here
                System.out.println("POST ERROR");
            }
            break;
     }  
    
}
}

