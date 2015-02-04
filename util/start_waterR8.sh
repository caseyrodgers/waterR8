#!/bin/bash

MAVEN_HOME=~/maven
export MAVEN_HOME

JAVA_HOME=/usr/java/latest
export JAVA_HOME

while true;do
  $MAVEN_HOME/bin/mvn exec:java -o &>> /tmp/waterR8.log
done;