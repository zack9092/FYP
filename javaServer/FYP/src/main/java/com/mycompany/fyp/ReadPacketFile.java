/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fyp;

import java.io.EOFException; 
import java.util.concurrent.TimeoutException; 
import org.pcap4j.core.NotOpenException; 
import org.pcap4j.core.PcapHandle; 
import org.pcap4j.core.PcapHandle.TimestampPrecision; 
import org.pcap4j.core.PcapNativeException; 
import org.pcap4j.core.Pcaps; 
import org.pcap4j.packet.Packet; 
 
@SuppressWarnings("javadoc") 
public class ReadPacketFile { 
 
  private static final int COUNT = 5; 
 
  private static final String PCAP_FILE_KEY 
    = ReadPacketFile.class.getName() + ".pcapFile"; 
  private static final String PCAP_FILE 
    = System.getProperty(PCAP_FILE_KEY, "C:/Users/User/Desktop/testing.pcap"); 
 
  public static void main(String[] args) throws PcapNativeException, NotOpenException { 
    PcapHandle handle; 
    try { 
      handle = Pcaps.openOffline(PCAP_FILE, TimestampPrecision.NANO); 
    } catch (PcapNativeException e) { 
      handle = Pcaps.openOffline(PCAP_FILE); 
    } 
 
    for (int i = 0; i < COUNT; i++) { 
      try { 
        Packet packet = handle.getNextPacketEx(); 
        System.out.println(handle.getTimestamp()); 
        System.out.println(packet); 
      } catch (TimeoutException e) { 
      } catch (EOFException e) { 
        System.out.println("EOF"); 
        break; 
      } 
    } 
 
    handle.close(); 
  } 
 
}
