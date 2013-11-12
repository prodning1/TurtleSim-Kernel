package com.prodning.turtlesim.kernel.combat.data;

public class MicroCombatResult {
	private Boolean rapidfireSuccess;
	private double shieldDamage;
	
	

	public MicroCombatResult(Boolean rapidfireSuccess, Boolean targetDestroyed) {
		this.rapidfireSuccess = rapidfireSuccess;
	}
	
	public MicroCombatResult() {
		this.rapidfireSuccess = null;
	}

	public Boolean getRapidFireSuccess() {
		return rapidfireSuccess;
	} public void setRapidfireSuccess(Boolean rapidfireSuccess) {
		this.rapidfireSuccess = rapidfireSuccess;
	} public double getShieldDamage() {
		return shieldDamage;
	} public void setShieldDamage(double shieldDamage) {
		this.shieldDamage = shieldDamage;
	}
}