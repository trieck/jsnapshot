package org.pixielib.content;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private static final String OBJECT_ID = "ObjectId";
    private static final String PARENT_ID = "ParentObjectId";
    private static final String ROOT_WINDOW_OBJECT_ID = "RootWindowObjectId";
    private static final String METADATA_NAME = "METADATA_NAME";
    private static final String METADATA_VALUE = "METADATA_VALUE";
    private static final String METADATA = "METADATA";
    private static final String TREE_CHILDREN = "TreeChildren";

    private static final ObjectMapper mapper = new ObjectMapper();
    private ObjectNode node;
    private Map<String, String> metadata;

    public Event() {
        node = mapper.createObjectNode();
        metadata = new HashMap<String, String>();
        clear();
    }

    public Event(String rep) throws IOException {
        node = (ObjectNode) mapper.readTree(rep);
        parseMeta();
    }

    private void parseMeta() {
        metadata = new HashMap<String, String>();

        ArrayNode meta = (ArrayNode) node.get(METADATA);
        for (int i = 0; i < meta.size(); ++i) {
            ObjectNode o = (ObjectNode) meta.get(i);
            String name = o.get(METADATA_NAME).getTextValue();
            String value = o.get(METADATA_VALUE).getTextValue();
            metadata.put(name, value);
        }
    }

    public String getObjectId() {
        return getMeta(OBJECT_ID);
    }

    public void setObjectId(String objectId) {
        putMeta(OBJECT_ID, objectId);
    }

    private void putMeta(String name, String value) {
        removeMeta(name);
        appendMeta(name, value);
        metadata.put(name, value);
    }

    private void appendMeta(String name, String value) {
        ObjectNode o = mapper.createObjectNode();
        o.put(METADATA_NAME, name);
        o.put(METADATA_VALUE, value);

        ArrayNode array = (ArrayNode) node.get(METADATA);
        array.add(o);
    }

    private void removeMeta(String name) {
        ArrayNode array = (ArrayNode) node.get(METADATA);
        for (int i = 0; i < array.size(); ++i) {
            ObjectNode o = (ObjectNode) array.get(i);
            if (o.get(METADATA_NAME).equals(name)) {
                array.remove(i);
                break;
            }
        }
    }

    public String getMeta(String field) {
        String value = metadata.get(field);
        if (value == null)
            return "";

        return value;
    }

    public JsonNode get(String field) {
        return node.get(field);
    }

    public String getParentId() {
        return getMeta(PARENT_ID);
    }

    public void setParentId(String parentId) {
        putMeta(PARENT_ID, parentId);
    }

    public String getRootId() {
        return getMeta(ROOT_WINDOW_OBJECT_ID);
    }

    public void setRootId(String rootId) {
        putMeta(ROOT_WINDOW_OBJECT_ID, rootId);
    }

    public void addChild(String objectId) {
        ArrayNode children = (ArrayNode) node.get(TREE_CHILDREN);
        if (children == null) {
            children = mapper.createArrayNode();
            node.put(TREE_CHILDREN, children);
        }

        if (!hasChild(objectId)) {
            children.add(objectId);
        }
    }

    public boolean hasChild(String objectId) {
        ArrayNode children = (ArrayNode) node.get(TREE_CHILDREN);
        if (children == null)
            return false;

        for (int i = 0; i < children.size(); ++i) {
            JsonNode child = children.get(i);
            if (child.getTextValue().equals(objectId))
                return true;
        }

        return false;
    }

    public String getString(String fieldname) {
        JsonNode n = node.get(fieldname);
        if (n == null)
            return "";

        return n.getTextValue();
    }

    public long getLong(String fieldname) {
        JsonNode n = node.get(fieldname);
        if (n == null)
            return 0L;

        return n.getLongValue();
    }

    public ArrayNode getMetadata() {
        return (ArrayNode) get(METADATA);
    }

    public ArrayNode getChildren() {
        return (ArrayNode) get(TREE_CHILDREN);
    }

    public boolean hasChildren() {
        return getChildren() != null;
    }

    public void set(EventBuffer buffer) {

        clear();

        FBEvent event = buffer.getEvent();

        node.put("EVENT_NAME", event.name());
        node.put("EVENT_SEQUENCE_NUMBER", event.sequence());
        node.put("EVENT_SOURCE", event.source());
        node.put("OPERATION_ID", event.operationId());
        node.put("PROCESS_ID", event.processId());
        node.put("SESSION_ID", event.sessionId());
        node.put("TIME_STAMP", event.timeStamp());
        node.put("TIME_ZONE_NAME", event.timeZoneName());
        node.put("USER_ID", event.userId());

        int length = event.treeChildrenLength();
        for (int i = 0; i < length; ++i) {
            addChild(event.treeChildren(i));
        }

        if (event.initialSequence() > 0) {
            node.put("InitialSequenceNumber", event.initialSequence());
        }

        length = event.metadataLength();
        for (int i = 0; i < length; ++i) {
            FBMeta meta = event.metadata(i);
            putMeta(meta.name(), meta.value());
        }
    }

    private void clear() {
        node.removeAll();
        node.put(METADATA, mapper.createArrayNode());
        metadata.clear();
    }
}

