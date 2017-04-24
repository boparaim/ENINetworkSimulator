rem utility script to start the simulator

rem script directory
SET mypath=%~dp0
SET javapath=java
SET mainclass=ca.empowered.nms.simulator.Main
SET classpath=%mypath%..\conf\;%mypath%..\libs\*
SET jvmopts=-Dlog4j.debug -Dsun.java2d.opengl=True -Dsun.java2d.directx=True -Dorg.graphstream.ui.renderer=org.graphstream.ui.j2dviewer.J2DGraphRenderer

%javapath% %jvmopts% -cp %classpath% %mainclass%