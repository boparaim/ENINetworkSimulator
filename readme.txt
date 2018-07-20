npm install -g @angular/cli
# not required anymore; user npm install/search @types/package
npm install -g typings
ng new ENINetworkSimulatorFE
cd ENINetworkSimulatorFE/
ng serve --open
npm install --save sockjs-client
npm install --save stompjs
npm install --save vis
npm install --save jquery
npm install -D @types/jquery
npm install --save pekeuplaod

https://angular.io/guide/quickstart

cd ENINetworkSimulator
gradle --warning-mode=all clean build
sometimes need to run project -> rt-click -> gradle -> refresh gradle project
/drives/c/Users/mboparai/Downloads/apache-tomcat-9.0.8/bin/catalina.sh stop
/drives/c/Users/mboparai/Downloads/apache-tomcat-9.0.8/bin/catalina.sh start

install erlang (https://www.rabbitmq.com/install-windows-manual.html, https://www.rabbitmq.com/configure.html)
install rabbitmq server
in sbin/rabbitmq-env.bat
	set RABBITMQ_NODE_IP_ADDRESS=127.0.0.1
	set RABBITMQ_NODE_PORT=5672
	set RABBITMQ_NODENAME=rabbit@localhost
	set RABBITMQ_SERVICENAME=RabbitMQ
/drives/c/Users/mboparai/Downloads/rabbitmq_server-3.7.5/sbin/rabbitmq-server.bat




to start development:
start mariadb 10		/drives/c/Users/mboparai/Downloads/mariadb-10.2.6-winx64/bin/mysqld.exe
start rabbitmq 3.7		in local console: first run: export ERLANG_HOME="c:\Program Files\erlang9.3", then -
						/drives/c/Users/mboparai/Downloads/rabbitmq_server-3.7.5/sbin/rabbitmq-server.bat
start tomcat 9			/drives/c/Users/mboparai/Downloads/apache-tomcat-9.0.8/bin/catalina.sh run
cd to ENINetworkSimulator
gradle build to build Java Spring (if tomcat is running, this will automatically trigger reload)

cd to ENINetworkSimulatorFE
ng serve --open to start angular http server
and another shell tab for angular cli commands




OR FOR Backend simply start the docker vm, which has mariadb, rabbitmq and tomcat containers running
and then access tomcat from PC @ http://192.168.78.128:8095/		(192.168.78.128 is docker vm IP)