package com.prodning.turtlesim.kernel.combat;

public class CombatSettings {
	private static Boolean defenseToDebris;
	private static double shipDebrisRatio;
	private static double defenseDebrisRatio;
	
	
	public static Boolean getDefenseToDebris() {
		return defenseToDebris;
	} public static void setDefenseToDebris(Boolean defenseToDebris) {
		CombatSettings.defenseToDebris = defenseToDebris;
	} public static double getShipDebrisRatio() {
		return shipDebrisRatio;
	} public static void setShipDebrisRatio(double shipDebrisRatio) {
		CombatSettings.shipDebrisRatio = shipDebrisRatio;
	} public static double getDefenseDebrisRatio() {
		return defenseDebrisRatio;
	} public static void setDefenseDebrisRatio(double defenseDebrisRatio) {
		CombatSettings.defenseDebrisRatio = defenseDebrisRatio;
	}
}
