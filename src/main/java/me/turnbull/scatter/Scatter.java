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

package me.turnbull.scatter;

import me.turnbull.scatter.api.ScatterAPI;
import me.turnbull.scatter.cluster.Messenger;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * Project: Scatter
 * Date:    11/4/2016
 * Author:  Dylan Turnbull
 */

public class Scatter {

    private final static String CLUSTER_NAME = System.getenv("CLUSTER_NAME");
    private final static String CLUSTER_ADDR = System.getenv("CLUSTER_ADDR");
    private final static int CLUSTER_PORT = Integer.parseInt(System.getenv("CLUSTER_PORT"));
    private final static int API_PORT = Integer.parseInt(System.getenv("API_PORT"));

    private static Messenger messenger;
    private static ScatterAPI scatterAPI;

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("******************************************");
        System.out.println("*    Scatter - Distributed Processing    *");
        System.out.println("******************************************");
        System.out.println(String.format("Build Timestamp: %s", getCompileTimeStamp().toString()));
        System.out.println(String.format("Cluster Name:    %s", CLUSTER_NAME));
        System.out.println(String.format("Cluster Address: %s", CLUSTER_ADDR));
        System.out.println(String.format("Cluster Port:    %s", CLUSTER_PORT));
        System.out.println(String.format("API Port:        %s", API_PORT));

        messenger = new Messenger(CLUSTER_NAME, CLUSTER_ADDR, CLUSTER_PORT);
        scatterAPI = new ScatterAPI(messenger,API_PORT,500);

        System.out.println("Press Ctl+C to Quit...");
        while(true){
            Thread.sleep(5000);
        }
    }

    /**
     * get date a class was compiled by looking at the corresponding class file in the jar.
     * @author Zig
     */
    public static Date getCompileTimeStamp( ) throws IOException {
        Class<?> cls = Scatter.class;
        ClassLoader loader = cls.getClassLoader();
        String filename = cls.getName().replace('.', '/') + ".class";
        // get the corresponding class file as a Resource.
        URL resource=( loader!=null ) ?
                loader.getResource( filename ) :
                ClassLoader.getSystemResource( filename );
        URLConnection connection = resource.openConnection();
        // Note, we are using Connection.getLastModified not File.lastModifed.
        // This will then work both or members of jars or standalone class files.
        long time = connection.getLastModified();
        return( time != 0L ) ? new Date( time ) : null;
    }
}
