export jar with only source and resources - application.properties

cd to following path
c:\ENINetworkSimulator>java -cp .;ENINetworkSimulator2.jar;libs\* ca.empowered.nms.simulator.Main

[root@localhost ENINetworkSimulator]# java -cp .:ENINetworkSimulator2.jar:libs/* ca.empowered.nms.simulator.Main



To create new release -
1. Create new zip file
2. Only include the changed files and new libraries
3. Include everything if is a major release


TODOs
1. allow user to specify the format of the notification (json)
2. allow user to zoom in on a node in GUI
3. change title of the GUI from graphstream
4. hower over to see connected node names