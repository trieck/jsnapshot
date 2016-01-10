@echo off
mvn --quiet -Djava.io.tmpdir=${OS_TMP_DIR} exec:java -Dexec.mainClass="org.pixielib.content.Snapshotter" -Dexec.args="%1 %2"
