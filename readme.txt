This application simulates a network map. You can generate network nodes and simulate events on these nodes.
It can be used for testing the effectiveness of NMSs.

Credits: 
1. https://github.com/apache/spark
2. https://github.com/graphstream/gs-core
3. https://github.com/spring-projects/spring-framework

License:
1. Spark is released under version 2.0 of the Apache License.
2. GraphStream is free software distributed under the terms of two licenses, the CeCILL-C license that fits European law, 
and the GNU Lesser General Public License. 
3. The Spring Framework is released under version 2.0 of the Apache License.

How to get started:
1. Download the zip file - https://github.com/boparaim/ENINetworkSimulator/tree/enidellperf/dist
2. Unzip the downloaded file
3. cd to unzipped directory
4. Run ./bin/simulator.[cmd|sh]

Configuration files:
1. Node map can be defined in conf/config.json
2. Application settings are in conf/application.properties
3. Application logs settings are in conf/log4j2.properties

You can control node states from web browser -
Visit http://127.0.0.1:4567/ENINetworkSimulator/get/help for more information.

TODOs:
1. Allow user to specify the format of the notification. Currently only supports JSON.
2. Export topology information for other tools.
3. Improve algorithm in NodeManager.relateObjects()
4. Correct the implementation for degraded events. Degrade should move from lower to higher rank nodes.

Build the project:
1. Set JAVA_HOME in gradlew
2. Run gradle build
Create the release zip:
1. Run ./gradlew task createReleaseZip