@echo off

setlocal

set CLASSPATH=config;target/server-0.1.0-SNAPSHOT-shaded.jar

java -classpath %CLASSPATH% -Dlog4j.configuration=log4j.properties org.jppf.server.DriverLauncher

endlocal
