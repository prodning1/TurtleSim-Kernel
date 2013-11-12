package com.prodning.turtlesim.kernel.combat;

import java.util.ArrayList;
import java.util.HashMap;

import com.prodning.turtlesim.kernel.parse.EntityFileParser;

/**
 * 
 */
public class Fleet extends ArrayList<CombatEntity> {
	private int numberOfShips = 0;
	private String id;
	
	
	public Fleet(String id) {
		this.id = id;
	}
	
	public int addToFleet(CombatEntity c) {
		c.setUniqueID(numberOfShips);
		
		this.add(c);
		
		return numberOfShips++;
	}
	
	public HashMap<String, Integer> getFleetComposition() {
		HashMap<String, Integer> fleetComposition = new HashMap<String, Integer>();
		
		for(CombatEntity entity : this) {
			Integer n = fleetComposition.get(entity.getEntityID());
			if(n == null)
				n = 0;
			n++;
			fleetComposition.put(entity.getEntityID(), n);
		}
		
		return fleetComposition;
	}
	
	public String toString() {
		String result = "Fleet: " + id + "\nTotal ships: " + Integer.toString(this.size()) + "\n";
		for(CombatEntity entity : this) {
			result = result + String.format("%4d %20s ", entity.getUniqueID(), EntityFileParser.getNameById(entity.getEntityID())) + entity.getHull() + "\t" + String.format("%1$,.2f",entity.getHullPercentage()*100) + "\t" + entity.getShield() + "\t" + entity.getEffectiveWeapon() + "\n";
		}
		
		return result;
	}

	public String getId() {
		return id;
	}

	public Fleet deepClone() {
		String newId;
		
		if(id.endsWith("C"))
			newId = new String(id);
		else
			newId = id.concat("C");
			
		
		Fleet result = new Fleet(newId);
		
		for(CombatEntity c : this) {
			result.add(c.clone());
		}
		
		return result;
	}
	
	public static HashMap<String,Integer> compositionIdToName(HashMap<String,Integer> composition) {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		
		for(String s : composition.keySet()) {
			result.put(EntityFileParser.getNameById(s), composition.get(s));
		}
		
		return result;
	}
}
