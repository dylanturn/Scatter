package me.turnbull.scatter.cluster.header;

import org.jgroups.Header;
import org.jgroups.Global;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.function.Supplier;

/**
 * Created by dturnbu on 10/28/2016.
 */
public class KeepAlive extends Header {

    public boolean isReply;
    public long synEpoch; // Not actually a syn/ack. I just couldn't think of a better name.
    public long ackEpoch;

    public KeepAlive(){}

    @Override
    public short getMagicId() {
        return 3000;
    }

    @Override
    public int size() {
        return Global.BYTE_SIZE + Global.LONG_SIZE + Global.LONG_SIZE;
    }

    @Override
    public void writeTo(DataOutput dataOutput) throws Exception {
        dataOutput.writeBoolean(isReply);
        dataOutput.writeLong(synEpoch);
        dataOutput.writeLong(ackEpoch);
    }

    @Override
    public void readFrom(DataInput dataInput) throws Exception {
        isReply = dataInput.readBoolean();
        synEpoch = dataInput.readLong();
        ackEpoch = dataInput.readLong();
    }

    @Override
    public Supplier<? extends Header> create() {
        return () -> new KeepAlive();
    }
}
