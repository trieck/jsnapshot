package org.pixielib.content;

import org.pixielib.util.Timer;

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
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void snapshot(String infile, String outfile) throws IOException {
        SnapshotTree tree = null;

        try {
            tree = new SnapshotTree();
            tree.snapshot(infile, outfile);
        } finally {
            if (tree != null)
                tree.close();
        }
    }
}
