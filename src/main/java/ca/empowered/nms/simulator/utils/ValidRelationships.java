package ca.empowered.nms.simulator.utils;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.node.Element;
import ca.empowered.nms.simulator.node.Relationship;

public class ValidRelationships {

	private static final Logger log = LogManager.getLogger(ValidRelationships.class.getName());
	private static ArrayList<String[]> map = new ArrayList<>();
	
	// TODO: read this from a conf file/json
	public ValidRelationships() {
		// TODO: change 4	
		//map.add( new String[] { "LoadBalancer", "LoadBalancer", "1", "1" } );	
		map.add( new String[] { "LoadBalancer", "Router", "1", "2" } );	
		map.add( new String[] { "Router", "Router", "1", "1" } );	
		map.add( new String[] { "Router", "Switch", "1", "2" } );	
		//map.add( new String[] { "Router", "Host", "1", "1" } );	
		map.add( new String[] { "Switch", "Host", "1", "2" } );
		map.add( new String[] { "Host", "Card", "1", "2" } );
		//map.add( new String[] { "Host", "WebApplication", "1", "2" } );	// host can host 3 webapps; webapp can only be hosted by 1 host	
		map.add( new String[] { "Card", "Interface", "1", "2" } );	
		map.add( new String[] { "Interface", "WebApplication", "1", "2" } );	
		map.add( new String[] { "NetworkConnection", "Interface", "1", "10" } );
		map.add( new String[] { "NetworkConnection", "Switch", "1", "1" } );	
		map.add( new String[] { "NetworkConnection", "Router", "1", "1" } );	
		map.add( new String[] { "NetworkConnection", "LoadBalancer", "1", "1" } );
		map.add( new String[] { "BGPSession", "Router", "1", "4" } );
	}
	
	public static boolean isValidRelationship(Element node1, Element node2) {
		boolean isValid = false;
		
		String class1 = node1.getClass().getSimpleName();
		String class2 = node2.getClass().getSimpleName();
		int class1Limit = 0;
		int class2Limit = 0;
		int class1Count = 0;
		int class2Count = 0;
		
		// is a relationship defined for these two types
		for ( String[] entry : map ) {

			 
			//log.debug("class1: " + class1 + " class2: " + class2 + " entry[0]: " + entry[0] + " entry[1]: " + entry[1]);
			if ((entry[0].equals(class1) && entry[1].equals(class2))) {
				 class1Limit = Integer.parseInt(entry[3]);
				 class2Limit = Integer.parseInt(entry[2]);
				 isValid = true;
			}
			if ((entry[0].equals(class2) && entry[1].equals(class1))) {
				 class1Limit = Integer.parseInt(entry[2]);
				 class2Limit = Integer.parseInt(entry[3]);
				 isValid = true;
			}
		}
		
		// validate we haven't passed the limit on any side
		if ( isValid ) {
			for (Relationship relationship : node1.relationships) {
				if ( node1.equals( relationship.getElement1() ) ) {
					if ( relationship.getElement2().getClass().getSimpleName().equals(class2) ) {
						class1Count++;
					}
				} else {
					if ( relationship.getElement1().getClass().getSimpleName().equals(class2) ) {
						class1Count++;
					}
				}
			}
			for (Relationship relationship : node2.relationships) {
				if ( node2.equals( relationship.getElement1() ) ) {
					if ( relationship.getElement2().getClass().getSimpleName().equals(class1) ) {
						class2Count++;
					}
				} else {
					if ( relationship.getElement1().getClass().getSimpleName().equals(class1) ) {
						class2Count++;
					}
				}
			}
			
			if (class1Count >= class1Limit) {
				isValid = false;
			}
			
			if (class2Count >= class2Limit) {
				isValid = false;
			}
			
			//log.debug("\nclass1: " + class1 + " class2: " + class2 + " class1 limit: " + class1Limit + " class2 limit: " + class2Limit + " class1 count: " + class1Count + " class2 count: " + class2Count);
		}
		
		
		return isValid;
	}
	
}
