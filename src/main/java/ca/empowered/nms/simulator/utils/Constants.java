package ca.empowered.nms.simulator.utils;

public class Constants {

	public static enum STATE { DOWN, UP, UNKNOWN, OTHER  };
	public static enum RELATIONSHIP { HOSTS, HOSTEDBY, CONNECTEDVIA, COMPOSEDOF, PARTOF, CONNECTS, CONNECTEDTO, CAUSEDBY, CAUSES  };
	public static enum DIRECTION { NODE1TONNODE2, NODE2TONNODE1, NONE, BOTH };
	
}
