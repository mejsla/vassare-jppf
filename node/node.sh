#!/bin/sh

CLASSPATH=config:target/node-0.1.0-SNAPSHOT-shaded.jar

java -classpath $CLASSPATH -Dlog4j.configuration=log4j.properties org.jppf.node.NodeLauncher
