package org.pixielib.content;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Event {

    private static final ObjectMapper mapper = new ObjectMapper();
    ObjectNode node;
    Map<String, String> metadata;

    public Event(String rep) throws IOException {
        node = (ObjectNode) mapper.readTree(rep);
        parseMeta();
    }

    private void parseMeta() {
        metadata = new HashMap<String, String>();

        ArrayNode meta = (ArrayNode) node.get("METADATA");
        for (int i = 0; i < meta.size(); ++i) {
            ObjectNode o = (ObjectNode) meta.get(i);
            String name = o.get("METADATA_NAME").getTextValue();
            String value = o.get("METADATA_VALUE").getTextValue();
            metadata.put(name, value);
        }
    }

    public String getObjectId() {
        return getMeta("ObjectId");
    }

    public String getMeta(String field) {
        return metadata.get(field);
    }
}
