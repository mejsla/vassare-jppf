@echo off

setlocal

set CLASSPATH=config;target/client-0.1.0-SNAPSHOT-shaded.jar

java -classpath %CLASSPATH% -Dlog4j.configuration=log4j.properties se.mejsla.vassare.jppf.client.TemplateApplicationRunner

endlocal
