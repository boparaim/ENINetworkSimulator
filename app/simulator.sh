#!/bin/bash
# utility script to start the simulator

# script directory
script=$(readlink -f "$0")
mypath=$(dirname "$script")
javapath=java
mainclass=ca.empowered.nms.simulator.Main
classpath="$mypath/../conf/:$mypath/../libs/*"
jvmopts="-Dlog4j.debug -Dsun.java2d.opengl=True -Dsun.java2d.directx=True -Dorg.graphstream.ui.renderer=org.graphstream.ui.j2dviewer.J2DGraphRenderer"

$javapath $jvmopts -cp $classpath $mainclass