package me.turnbull.scatter;

import me.turnbull.scatter.cluster.Messenger;
import me.turnbull.scatter.webinterface.InteractionRouter;

/**
 * @author  Dylan Turnbull
 */

public class Scatter {

    private static Messenger scatterMessenger;
    private static InteractionRouter scatterRouter;

    public static void main(String[] args) {
        scatterMessenger = new Messenger();
        scatterRouter = new InteractionRouter(scatterMessenger);
        System.out.println("Press Ctl+C to Quit...");
    }
}
