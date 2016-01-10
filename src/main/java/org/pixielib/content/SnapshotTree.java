package org.pixielib.content;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SnapshotTree {

    private EventStore store;

    public SnapshotTree() throws IOException {
        store = new EventStore();
        store.open();
    }

    public void close() throws IOException {
        store.close();
    }

    public void snapshot(String infile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(infile));

        String line;
        while ((line = reader.readLine()) != null) {
            process(new Event(line));
        }
    }

    private void process(Event event) throws IOException {
        String name = event.get("EVENT_NAME").getTextValue();
        if (name.matches("Created")) {
            insert(event);
        }
    }

    private void insert(Event event) throws IOException {
        if (store.find(event)) {
            store.update(event);
        } else {
            insert(event, getParentId(event));
        }
    }

    private void insert(Event event, String parentId) throws IOException {
        store.insert(event);
        addChild(parentId, event);
    }

    private void addChild(String parentId, Event event) throws IOException {
        String objectId = event.getObjectId();
        if (objectId.equals(parentId))
            return;  // self

        String rootId = event.getRootId();

        Event parent = new Event();
        if (!store.find(parentId, parent)) {
            if (!rootId.equals(parentId))
                parent.setParentId(rootId);
            parent.setRootId(rootId);
            parent.setObjectId(parentId);
            parent.addChild(objectId);
            store.insert(parent);
            if (!rootId.equals(parentId))
                addChild(rootId, parent);
        } else {
            parent.addChild(objectId);
            store.update(parent);
        }
    }

    private String getParentId(Event event) {
        String parentId = event.getParentId();
        if (parentId.length() == 0)
            parentId = event.getRootId();

        return parentId;
    }
}
