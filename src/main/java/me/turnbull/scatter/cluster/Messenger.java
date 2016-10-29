package me.turnbull.scatter.cluster;

import me.turnbull.scatter.cluster.header.KeepAlive;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.util.Util;

import java.util.ArrayList;

/**
 * @author  Dylan Turnbull
 */

public class Messenger {

    JChannel clusterChannel;
    String clusterName = "DemoCluster";
    String protocolStack = "udp.xml";

    public Messenger() {
        try{
            clusterChannel = new JChannel(protocolStack);
            ClassConfigurator.add(new KeepAlive().getMagicId(), KeepAlive.class);
            clusterChannel.stats(true);
            clusterChannel.connect(clusterName);
            System.out.println("Cluster Started: " + clusterChannel.getName());
            startKeepAlive();
        }catch (Exception error) {
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
    public int getMemberCount(){
        return clusterChannel.view().getMembers().size();
    }
    public ArrayList<String> getMemberList(){
        ArrayList<String> memberList = new ArrayList<>();
        for(Address memberAddress : clusterChannel.getView().getMembers()){
            memberList.add(memberAddress.toString());
        }
        return memberList;
    }
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
                while(true) {
                    Message keepAliveMSG = new Message(null);
                    keepAliveMSG.putHeader(new KeepAlive().getMagicId(), new KeepAlive());
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
    public void send(Message message){
        try {
            clusterChannel.send(message);
        } catch (Exception error){
            System.out.println(error.getMessage());
            System.exit(102);
        }
    }
}
