@echo off
set JAVA_OPTS=-Djava.io.tmpdir=d:\temp -Dexec.args="%1 %2"
set JAVA_OPTS=%JAVA_OPTS% -Dexec.mainClass="org.pixielib.content.Snapshotter"
set MAVEN_OPTS=-Xmx64m -Xms64m

mvn --quiet %JAVA_OPTS% exec:java  
