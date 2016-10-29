package me.turnbull.scatter.webinterface;

import me.turnbull.scatter.cluster.Messenger;

import static spark.Spark.get;
import static spark.Spark.port;

/**
 * @author  Dylan Turnbull
 */

public class InteractionRouter {

    Messenger messenger;

    public InteractionRouter(Messenger messenger){
        this.messenger = messenger;
        port(5000);
        get("/time", (request, response) -> getTime());
        get("/cluster", (request, response) -> getCluster());
        get("/membership", (request, response) -> getMembership());
        get("/statistics", (request, response) -> getStatistics());
    }

    private String getTime(){
        return String.valueOf(System.currentTimeMillis());
    }

    private String getCluster(){
        try {
            String cluster = String.format("<Cluster name=\"%s\" respondingNode=\"%s\" coordinator=\"%s\" reponseToken=\"XXX\" />\n",
                    messenger.getClusterName(),
                    messenger.getName(),
                    messenger.getClusterCoordinator());
            return cluster;
        } catch(Exception error){
            System.out.println(error.getStackTrace());
        }
        return "Call Failed!";
    }

    private String getMembership(){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append
            stringBuilder.append(String.format("<Cluster name=\"%s\" respondingNode=\"%s\" coordinator=\"%s\" reponseToken=\"XXX\">\n",
                    messenger.getClusterName(),
                    messenger.getName(),
                    messenger.getClusterCoordinator()));
            stringBuilder.append(String.format("<Members size=\"%s\" >\n",messenger.getMemberCount()));
            for(String member : messenger.getMemberList()) {
                stringBuilder.append(String.format("<Member name=\"%s\" />\n",member));
            }
            stringBuilder.append("<Members />\n");
            stringBuilder.append("<Cluster />\n");
            return stringBuilder.toString();
        } catch(Exception error){
            System.out.println(error.getStackTrace());
        }
        return "Call Failed!";
    }

    private String getStatistics(){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("<Cluster name=\"%s\" respondingNode=\"%s\" coordinator=\"%s\" reponseToken=\"XXX\">\n",
                    messenger.getClusterName(),
                    messenger.getName(),
                    messenger.getClusterCoordinator()));
            stringBuilder.append("<Statistics>\n");

            stringBuilder.append(String.format("<Statistic name=\"ReceivedBytes\" value=\"%s\" />\n",messenger.getReceivedBytes()));
            stringBuilder.append(String.format("<Statistic name=\"ReceivedMessages\" value=\"%s\" />\n",messenger.getReceivedMessages()));
            stringBuilder.append(String.format("<Statistic name=\"SentBytes\" value=\"%s\" />\n",messenger.getSentBytes()));
            stringBuilder.append(String.format("<Statistic name=\"SentMessages\" value=\"%s\" />\n",messenger.getSentMessages()));

            stringBuilder.append("<Statistics />\n");
            stringBuilder.append("<Cluster />\n");
            return stringBuilder.toString();
        } catch(Exception error){
            System.out.println(error.getStackTrace());
        }
        return "Call Failed!";
    }
}
