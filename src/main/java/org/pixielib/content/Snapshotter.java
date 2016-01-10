package org.pixielib.content;

import org.pixielib.util.Timer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Snapshotter {

    public Snapshotter() {

    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.printf("usage: %s infile outfile\n", Snapshotter.class.getSimpleName());
            System.exit(1);
        }

        Timer timer = new Timer();

        try {
            Snapshotter snapshotter = new Snapshotter();
            snapshotter.snapshot(args[0], args[1]);

            System.out.printf("    elapsed time %s\n", timer);

        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    public void snapshot(String infile, String outfile) throws IOException {

        EventStore store = new EventStore();
        store.open();

        BufferedReader reader = new BufferedReader(new FileReader(infile));

        String line;
        while ((line = reader.readLine()) != null) {
            store.insert(new Event(line));
        }
    }
}
