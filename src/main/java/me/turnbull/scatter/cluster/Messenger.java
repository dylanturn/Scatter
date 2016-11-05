package me.turnbull.scatter.cluster;

import me.turnbull.scatter.cluster.header.KeepAlive;
import me.turnbull.scatter.cluster.postoffice.MemberRecord;
import me.turnbull.scatter.cluster.postoffice.MessageReceiver;
import org.jgroups.*;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.protocols.UDP;
import org.jgroups.stack.Configurator;
import org.jgroups.stack.IpAddress;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Streamable;
import org.jgroups.util.Util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author  Dylan Turnbull
 */

public class Messenger {

    JChannel clusterChannel;
    String clusterName;
    String clusterIP;
    int clusterPort;
    String clusterVersion = "1";

    String protocolStack = "udp.xml";

    Map<Address,MemberRecord> memberRecords = new ConcurrentHashMap<>();

    public Messenger(String clusterName, String clusterIP, int clusterPort) {
        try{
            this.clusterName = clusterName;
            this.clusterIP = clusterIP;
            this.clusterPort = clusterPort;
            clusterChannel = new JChannel(protocolStack);
            ProtocolStack stack = clusterChannel.getProtocolStack();

            if(clusterIP.length() > 1)
                stack.findProtocol(UDP.class).setValue("mcast_addr", new IpAddress(clusterIP));

            if(clusterPort > 0)
                stack.findProtocol(UDP.class).setValue("mcast_port", clusterPort);

            ClassConfigurator.add(new KeepAlive().getMagicId(), KeepAlive.class);
            clusterChannel.stats(true);
            clusterChannel.setReceiver(new MessageReceiver(this));
            clusterChannel.connect(clusterName);
            System.out.println("Cluster Started: " + clusterChannel.getName());
            startKeepAlive();
        }catch (Exception error) {
            System.out.println("Error Caught!");
            System.out.println(error.getMessage());
            System.exit(100);
        }
    }

    public String getName(){
        return clusterChannel.getName();
    }
    public String getClusterName(){
        return clusterChannel.getClusterName();
    }
    public String getClusterCoordinator(){
        return clusterChannel.getView().getCoord().toString();
    }
    public String getClusterSecondary(){ return null; }
    public int getMemberCount(){
        return clusterChannel.view().getMembers().size();
    }
    public Collection<MemberRecord> getMemberList(){ return memberRecords.values(); }

    public String getClusterIP(){ return clusterIP; }
    public String getClusterPort(){ return String.valueOf(clusterPort); }
    public String getClusterVersion(){ return clusterVersion; }

    public long getReceivedBytes(){
        return clusterChannel.getReceivedBytes();
    }
    public long getReceivedMessages(){
        return clusterChannel.getReceivedMessages();
    }
    public long getSentBytes(){
        return clusterChannel.getSentBytes();
    }
    public long getSentMessages(){
        return clusterChannel.getSentMessages();
    }

    private void startKeepAlive(){
        Thread keepAliveThread = new Thread(new Runnable(){
            @Override
            public void run(){
                int errors = 0;
                while(clusterChannel.isConnected()) {

                    for(Address address : memberRecords.keySet()){
                        if(!clusterChannel.getView().containsMember(address)){
                            memberRecords.remove(address);
                        }
                    }

                    Message keepAliveMSG = new Message(null);
                    PhysicalAddress physicalAddress = (PhysicalAddress)
                        clusterChannel.down(
                            new Event(
                                Event.GET_PHYSICAL_ADDRESS, clusterChannel.getAddress()
                            )
                    );

                    keepAliveMSG.putHeader(new KeepAlive().getMagicId(), new KeepAlive(physicalAddress.printIpAddress()));
                    try {
                        clusterChannel.send(keepAliveMSG);
                    } catch(Exception error) {
                        errors++;
                        System.out.println(error.getMessage());
                    } finally {
                        if(errors==2){
                            System.exit(101);
                        }
                    }
                    Util.sleep(500);
                }
            }
        });
        keepAliveThread.start();
    }
    public void processKeepAlive(Address memberAddress, KeepAlive keepAlive){
        long pingTime = System.currentTimeMillis()-keepAlive.getSystemTime();
        memberRecords.put(memberAddress,new MemberRecord(memberAddress, keepAlive.getIpAddress(),pingTime));
    }

    public void send(Message message){
        try {
            clusterChannel.send(message);
        } catch (Exception error){
            System.out.println(error.getMessage());
            System.exit(102);
        }
    }


}
