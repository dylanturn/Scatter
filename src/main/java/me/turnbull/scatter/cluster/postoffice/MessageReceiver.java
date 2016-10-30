package me.turnbull.scatter.cluster.postoffice;

import me.turnbull.scatter.cluster.Messenger;
import me.turnbull.scatter.cluster.header.KeepAlive;
import org.jgroups.Message;
import org.jgroups.Receiver;

/**
 * @author  Dylan Turnbull
 */

public class MessageReceiver implements Receiver {

    private Messenger messenger;

    public MessageReceiver(Messenger messenger){
        this.messenger = messenger;
    }

    @Override
    public void receive(Message message) {
        if(message.getHeader(new KeepAlive().getMagicId())!=null){
            messenger.processKeepAlive(message.getSrc(),message.getHeader(new KeepAlive().getMagicId()));
        }
    }
}
