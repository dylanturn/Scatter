/*
 * Scatter - Distributed Processing Platform
 * Copyright (C) 2016 Dylan Turnbull [dylanturn@gmail.com]
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package me.turnbull.scatter.api;


import me.turnbull.scatter.api.partbuilder.LocalNodeTransform;
import me.turnbull.scatter.cluster.Messenger;
import me.turnbull.scatter.cluster.postoffice.MemberRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

/**
 * Project: Scatter
 * Date:    11/4/2016
 * Author:  Dylan Turnbull
 */

public class ScatterAPI {
    private static final Logger LOG = LogManager.getLogger(ScatterAPI.class);

    private int apiPort;
    private int apiIntervalMS;
    private String responseXML;
    private Messenger messenger;

    public ScatterAPI(Messenger messenger, int apiPort, int apiIntervalMS) {
        this.messenger = messenger;
        this.apiPort = apiPort;
        this.apiIntervalMS = apiIntervalMS;

        System.out.println("Starting API Server...");
        if(LOG.isInfoEnabled())
            LOG.info("Starting API Server...");

        startDataCollector();

        port(apiPort);
        before((request, response) -> response.type("application/xml"));
        after((request, response) -> { response.header("Access-Control-Allow-Origin", "*"); });
        get("/sdk", (request, response) -> getResponseXML(request));

        System.out.println("API Server Started!");

    }

    private void startDataCollector(){
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(
                ()->{ collectData(messenger, this); }, 0, apiIntervalMS, TimeUnit.MILLISECONDS);
    }

    public synchronized void setResponseXML(long observerId, String responseXML){
        if(LOG.isTraceEnabled())
            LOG.trace("Received API response XML from observer " + observerId);
        this.responseXML = responseXML;
    }

    private String getResponseXML(Request request){
        if(LOG.isDebugEnabled())
            LOG.debug("Responding to API request from " + request.ip());
        return responseXML;
    }

    private void collectData(Messenger messenger, ScatterAPI scatterAPI){
        long id = new Thread().currentThread().getId();
        String responseDate = "";//new Timestamp(System.currentTimeMillis()).getDate().toString();
        String responseToken = "token";

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        responseBuilder.append(String.format("<Cluster time=\"%s\" reponseToken=\"%s\">",responseDate,responseToken));
        responseBuilder.append(getClusterXML());
        responseBuilder.append("<LocalNode>");
        responseBuilder.append(new LocalNodeTransform(messenger).getLocalNodeXML());
        responseBuilder.append("</LocalNode>");
        responseBuilder.append(getMembershipXML());
        responseBuilder.append("</Cluster>");
        scatterAPI.setResponseXML(id, responseBuilder.toString());
    }

    private String getClusterXML(){
        return String.format( "<ClusterDetail name=\"DemoCluster\" primaryNode=\"bf759287238e-23557\" secondaryNode=\"bf759287238e-23557\" clusterIp=\"228.8.8.8\" clusterPort=\"45566\" version=\"1\" />",
                messenger.getClusterName(),
                messenger.getClusterCoordinator(),
                messenger.getClusterSecondary(),
                messenger.getClusterIP(),
                messenger.getClusterPort(),
                messenger.getClusterVersion());
    }

    private String getMembershipXML(){
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append(String.format("<Members size=\"%s\">",messenger.getMemberCount()));
        for(MemberRecord member : messenger.getMemberList()){
            xmlBuilder.append(String.format("<Member name=\"%s\" ipaddress=\"%s\" ping=\"%s\"/>",member.memberAddress.toString(),member.memberIpAddress,member.memberPing));
        }
        xmlBuilder.append("</Members>");
        return xmlBuilder.toString();
    }
}


