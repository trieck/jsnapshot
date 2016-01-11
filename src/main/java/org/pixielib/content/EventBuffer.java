package org.pixielib.content;

import com.google.flatbuffers.FlatBufferBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.nio.ByteBuffer;

public class EventBuffer {

    private FlatBufferBuilder builder = new FlatBufferBuilder();

    public EventBuffer() {
    }

    private EventBuffer(Event event) {
        construct(event);
    }

    private EventBuffer(ByteBuffer buffer) {
        set(buffer);
    }

    public static EventBuffer makeBuffer(Event event) {
        return new EventBuffer(event);
    }

    public static EventBuffer makeBuffer(ByteBuffer buffer) {
        return new EventBuffer(buffer);
    }

    public ByteBuffer getBuffer() {
        return ByteBuffer.wrap(builder.sizedByteArray());
    }

    private void construct(Event event) {

        int name = builder.createString(event.getString("EVENT_NAME"));
        long sequence = event.getLong("EVENT_SEQUENCE_NUMBER");
        long initialSequence = event.getLong("InitialSequenceNumber");
        int source = builder.createString(event.getString("EVENT_SOURCE"));
        int opid = builder.createString(event.getString("OPERATION_ID"));
        long procid = event.getLong("PROCESS_ID");
        int sessionId = builder.createString(event.getString("SESSION_ID"));
        int timeStamp = builder.createString(event.getString("TIME_STAMP"));
        int timeZone = builder.createString(event.getString("TIME_ZONE_NAME"));
        int userid = builder.createString(event.getString("USER_ID"));

        ArrayNode meta = event.getMetadata();
        int[] metaoffsets = new int[meta.size()];

        for (int i = 0; i < meta.size(); ++i) {
            ObjectNode o = (ObjectNode) meta.get(i);
            int mname = builder.createString(o.get("METADATA_NAME").getTextValue());
            int mvalue = 0;
            JsonNode val = o.get("METADATA_VALUE");
            if (val != null && !val.isNull())
                mvalue = builder.createString(val.getTextValue());

            metaoffsets[i] = FBMeta.createFBMeta(builder, mname, mvalue);
        }

        int metavector = FBEvent.createMetadataVector(builder, metaoffsets);

        int childrenvector = 0;
        if (event.hasChildren()) {
            ArrayNode children = event.getChildren();
            int[] childrenOffsets = new int[children.size()];

            for (int i = 0; i < children.size(); ++i) {
                String child = children.get(i).getTextValue();
                childrenOffsets[i] = builder.createString(child);
            }

            childrenvector = FBEvent.createTreeChildrenVector(builder, childrenOffsets);
        }

        int root = FBEvent.createFBEvent(builder, name, sequence, initialSequence, source, opid, procid, sessionId,
                timeStamp, timeZone, userid, childrenvector, metavector);

        FBEvent.finishFBEventBuffer(builder, root);
    }

    FBEvent getEvent() {
        return FBEvent.getRootAsFBEvent(builder.dataBuffer());
    }

    public void set(ByteBuffer buffer) {
        builder.init(buffer);
    }
}
