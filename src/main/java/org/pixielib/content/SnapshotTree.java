package org.pixielib.content;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SnapshotTree {

    private static final String SNAP_EVENT =
            "Click|DoubleClick|GotFocus|LostFocus|SelectedIndexChanged|UserModified|CellValueChanged";

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
        } else if (name.matches("Destroyed")) {
            destroy(event);
        } else if (name.matches("Reparented")) {
            reparent(event);
        } else if (name.matches(SNAP_EVENT)) {
            snapshot(event);
        } else {
            update(event);
        }
    }

    private void snapshot(Event event) throws IOException {
        update(event);
    }

    private void reparent(Event event) {

    }

    private void destroy(Event event) throws IOException {
        store.destroy(event);
        parentRemove(event.getParentId(), event.getObjectId());
    }

    private void parentRemove(String parentId, String objectId) throws IOException {
        Event p = new Event();
        if (store.find(parentId, p)) {
            if (p.removeChild(objectId))
                store.update(p);
        }
    }

    private void insert(Event event) throws IOException {
        if (store.find(event)) {
            store.update(event);
        } else {
            insert(event, getParentId(event));
        }
    }

    private void update(Event event) throws IOException {
        Event u = new Event();
        if (store.find(event.getObjectId(), u)) {
            store.update(u);
        } else {
            store.insert(event);
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
