package com.prodning.turtlesim.kernel.combat.data;


public class TechLevels {
	private int weaponsTech;
	private int armorTech;
	private int shieldTech;
	
	public TechLevels() {
		this(0,0,0);
	}
	
	public TechLevels(int weaponsTech, int armorTech, int shieldTech) {
		this.weaponsTech = weaponsTech;
		this.armorTech = armorTech;
		this.shieldTech = shieldTech;
	}
	public int getWeaponsTech() {
		return weaponsTech;
	}
	public int getArmorTech() {
		return armorTech;
	}
	public int getShieldTech() {
		return shieldTech;
	}
	
	public TechLevels clone() {
		return new TechLevels(weaponsTech, armorTech, shieldTech);
	}
}