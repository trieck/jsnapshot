package org.pixielib.content;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SnapshotTree {

    private static final String SNAP_EVENT =
            "Click|DoubleClick|GotFocus|LostFocus|SelectedIndexChanged|UserModified|CellValueChanged";

    private static Comparator<EventBuffer> SEQUENCE_COMPARATOR = new Comparator<EventBuffer>() {
        @Override
        public int compare(EventBuffer left, EventBuffer right) {
            long leftSeq = left.getEvent().initialSequence();
            long rightSeq = right.getEvent().initialSequence();
            return leftSeq < rightSeq ? -1 : leftSeq > rightSeq ? 1 : 0;
        }
    };

    private EventStore store;

    public SnapshotTree() throws IOException {
        store = new EventStore();
        store.open();
    }

    public void close() throws IOException {
        store.close();
    }

    public void snapshot(String infile, String outfile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(infile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));

        String line;
        while ((line = reader.readLine()) != null) {
            Event event = new Event(line);
            process(event);
            writer.write(event.toString());
            writer.newLine();
        }

        reader.close();
        writer.close();
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

        SnapshotParser parser = new SnapshotParser();

        parse(parser, event);
        parser.writePhrases(event);
    }

    private void parse(SnapshotParser parser, Event event) throws IOException {

        EventBuffer root = new EventBuffer();
        if (store.find(event.getRootId(), root)) {
            parseNode(parser, root);
        }
    }

    private void parseNode(SnapshotParser parser, EventBuffer node) throws IOException {
        parser.parse(node);

        List<EventBuffer> children = sortedChildren(node);
        for (EventBuffer child : children) {
            parseNode(parser, child);
        }
    }

    private List<EventBuffer> sortedChildren(EventBuffer buffer) throws IOException {

        List<EventBuffer> output = new ArrayList<>();

        FBEvent event = buffer.getEvent();

        int length = event.treeChildrenLength();
        for (int i = 0; i < length; ++i) {
            String objectId = event.treeChildren(i);
            EventBuffer child = new EventBuffer();
            if (store.find(objectId, child)) {
                output.add(child);
            }
        }

        Collections.sort(output, SEQUENCE_COMPARATOR);

        return output;
    }

    private void reparent(Event event) throws IOException {

        Event u = new Event();
        String objectId = event.getObjectId();

        if (store.find(objectId, u)) {
            reparent(u, event);
            store.update(event);
        } else {
            insert(event);
        }
    }

    private void reparent(Event from, Event to) throws IOException {

        String oldParentId = from.getParentId();
        String newParentId = to.getParentId();

        if (oldParentId.length() > 0 && newParentId.length() > 0 && !oldParentId.equals(newParentId)) {
            parentRemove(oldParentId, to.getObjectId());
            addChild(newParentId, to);
        }
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
        Event m, u = new Event();
        if (store.find(event.getObjectId(), u)) {
            reparent(u, event);
            m = event.merge(u);
            store.update(m);
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
