@echo off
mvn --quiet -Djava.io.tmpdir=d:\temp exec:java -Dexec.mainClass="org.pixielib.content.Snapshotter" -Dexec.args="%1 %2"
