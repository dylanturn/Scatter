#!/bin/bash

cd /root
git clone https://github.com/dylanturn/Scatter.git
mvn -f /root/Scatter/pom.xml clean install
java -jar /root/.m2/repository/me/turnbull/scatter/1.0/scatter-1.0-jar-with-dependencies.jar

echo "exited $0"