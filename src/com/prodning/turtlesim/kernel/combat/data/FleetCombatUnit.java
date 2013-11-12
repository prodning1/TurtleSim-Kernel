package com.prodning.turtlesim.kernel.combat.data;

import java.util.ArrayList;

import com.prodning.turtlesim.kernel.combat.CombatEntity;
import com.prodning.turtlesim.kernel.combat.Fleet;

public class FleetCombatUnit {
	public static enum CombatGroup {
		ATTACKING,
		DEFENDING;
	}
	private Fleet fleet;
	private ArrayList<CombatEntity> losses = new ArrayList<CombatEntity>();
	private String id;
	private CombatGroup combatGroup;
	private TechLevels techLevels;
	
	public FleetCombatUnit(Fleet fleet) {
		this.fleet = fleet;
		this.id = fleet.getId();
	}
	
	public CombatGroup getCombatGroup() {
		return combatGroup;
	} public void setCombatGroup(CombatGroup combatGroup) {
		this.combatGroup = combatGroup;
	} public TechLevels getTechLevels() {
		return techLevels;
	} public void setTechLevels(TechLevels techLevels) {
		this.techLevels = techLevels;
	} public Fleet getFleet() {
		return fleet;
	} public String getId() {
		return id;
	} public ArrayList<CombatEntity> getLosses() {
		return losses;
	}
	
	public FleetCombatUnit deepClone() {
		Fleet newFleet = fleet.deepClone();
		
		FleetCombatUnit result = new FleetCombatUnit(newFleet);
		
		result.setCombatGroup(combatGroup);
		result.setTechLevels(techLevels.clone());
		
		return result;
	}
}
