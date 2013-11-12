package com.prodning.turtlesim.kernel.combat.data;

import java.util.List;

public class MacroCombatResult {
	public static enum ResultType {
		ATTACKER_WIN,
		DEFENDER_WIN,
		DRAW;
	}
	
	private List<FleetCombatUnit> fleetCombatUnits;
	private ResultType resultType;
	private int rounds;
	
	public List<FleetCombatUnit> getFleetCombatUnits() {
		return fleetCombatUnits;
	} public void setFleetCombatUnits(List<FleetCombatUnit> fleetCombatUnits) {
		this.fleetCombatUnits = fleetCombatUnits;
	} public ResultType getResultType() {
		return resultType;
	} public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	} public int getRounds() {
		return rounds;
	} public void setRounds(int rounds) {
		this.rounds = rounds;
	}
}
