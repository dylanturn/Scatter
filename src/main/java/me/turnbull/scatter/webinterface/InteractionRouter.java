package me.turnbull.scatter.webinterface;

import me.turnbull.scatter.cluster.Messenger;
import me.turnbull.scatter.cluster.postoffice.MemberRecord;
import static spark.Spark.*;

/**
 * @author  Dylan Turnbull
 */

public class InteractionRouter {

    Messenger messenger;

    public InteractionRouter(Messenger messenger){
        this.messenger = messenger;
        port(5000);

        before((request, response) -> response.type("application/xml"));

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

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
            String cluster = String.format("<Cluster name=\"%s\" respondingNode=\"%s\" primaryCoord=\"%s\" secondaryCoord=\"%s\" reponseToken=\"XXX\" />",
                    messenger.getClusterName(),
                    messenger.getName(),
                    messenger.getClusterCoordinator(),
                    messenger.getClusterCoordinator());
            return cluster;
        } catch(Exception error){
            System.out.println("getCluster: " + error.getStackTrace());
        }
        return "Call Failed!";
    }

    private String getMembership(){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("<Cluster name=\"%s\" respondingNode=\"%s\" primaryCoord=\"%s\" secondaryCoord=\"%s\" reponseToken=\"XXX\">",
                    messenger.getClusterName(),
                    messenger.getName(),
                    messenger.getClusterCoordinator(),
                    messenger.getClusterCoordinator()));
            stringBuilder.append(String.format("<Members size=\"%s\" >",messenger.getMemberCount()));
            for(MemberRecord member : messenger.getMemberList()) {
                stringBuilder.append(String.format("<Member name=\"%s\" ipaddress=\"%s\" ping=\"%s\"/>",member.memberAddress.toString(),member.memberIpAddress,member.memberPing));
            }
            stringBuilder.append("</Members>");
            stringBuilder.append("</Cluster>");
            return stringBuilder.toString();
        } catch(Exception error){
            System.out.println("getMembership: " + error.getStackTrace());
        }
        return "Call Failed!";
    }

    private String getStatistics(){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("<Cluster name=\"%s\" respondingNode=\"%s\" primaryCoord=\"%s\" secondaryCoord=\"%s\" reponseToken=\"XXX\">",
                    messenger.getClusterName(),
                    messenger.getName(),
                    messenger.getClusterCoordinator(),
                    messenger.getClusterCoordinator()));
            stringBuilder.append("<Statistics>");

            stringBuilder.append(String.format("<Statistic name=\"ReceivedBytes\" value=\"%s\" />\n",messenger.getReceivedBytes()));
            stringBuilder.append(String.format("<Statistic name=\"ReceivedMessages\" value=\"%s\" />\n",messenger.getReceivedMessages()));
            stringBuilder.append(String.format("<Statistic name=\"SentBytes\" value=\"%s\" />\n",messenger.getSentBytes()));
            stringBuilder.append(String.format("<Statistic name=\"SentMessages\" value=\"%s\" />\n",messenger.getSentMessages()));

            stringBuilder.append("</Statistics>");
            stringBuilder.append("</Cluster>");
            return stringBuilder.toString();
        } catch(Exception error){
            System.out.println(error.getStackTrace());
        }
        return "Call Failed!";
    }
}
