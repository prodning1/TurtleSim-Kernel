package com.prodning.turtlesim.kernel.combat;

import java.util.HashMap;
import java.util.Random;

import com.prodning.turtlesim.kernel.combat.data.MicroCombatResult;
import com.prodning.turtlesim.kernel.combat.data.TechLevels;


public class CombatEntity implements Cloneable {
	public static enum CombatEntityType {
		DEFENSE,
		SHIP;
	}
	
	/**
	 * The resource name of the object, eg SMALL_CARGO
	 */
	private String id = "";
	private String fleetName = "";
	private int uniqueID = -1;
	
	private CombatEntityType type;
	
	private double structuralIntegrity = -1;
	private double baseShield = -1;
	private double baseWeapon = -1;
	
	private double initialHull = -1;
	private double initialShield = -1;
	
	private double hull = -1;
	private double shield = -1;
	private double effectiveWeapon = -1;
	
	private TechLevels techLevels;
	
	
	/**
	 * hashmap to determine the rapid fire values against the various units
	 */
	private HashMap<String, Integer> rapidFireMap;
	
	public CombatEntity(String id, CombatEntityType type, double structuralIntegrity,
			double baseShield, double baseWeapon,
			HashMap<String, Integer> rapidFireMap) {
		this.id = id;
		this.type = type;
		this.structuralIntegrity = structuralIntegrity;
		this.baseShield = baseShield;
		this.baseWeapon = baseWeapon;
		this.rapidFireMap = rapidFireMap;
	}

	private CombatEntity(String id, String fleetName, int uniqueID,
			CombatEntityType type, double structuralIntegrity,
			double baseShield, double baseWeapon, double initialHull,
			double initialShield, double hull, double shield,
			double effectiveWeapon, HashMap<String, Integer> rapidFireMap) {
		this.id = id;
		this.fleetName = fleetName;
		this.uniqueID = uniqueID;
		this.type = type;
		this.structuralIntegrity = structuralIntegrity;
		this.baseShield = baseShield;
		this.baseWeapon = baseWeapon;
		this.initialHull = initialHull;
		this.initialShield = initialShield;
		this.hull = hull;
		this.shield = shield;
		this.effectiveWeapon = effectiveWeapon;
		this.rapidFireMap = rapidFireMap;
	}

	public MicroCombatResult receiveAttack(CombatEntity attacker) {
		MicroCombatResult result = new MicroCombatResult();
		
		Random rand = new Random();
		
		if (hull > 0) {
			// Check for bounce effect (attacking weapon must be greater than 1%
			// of defender's shield)
			double shieldBeforeHit = shield;
			if (attacker.getWeapon() >= shield * 0.01) {
				// hit shields
				shield -= attacker.getWeapon();

				// if shields were destroyed, push remaining damage to hull
				if (shield < 0) {
					hull += shield;
					shield = 0;
				}
				
				result.setShieldDamage(shieldBeforeHit - shield);
			}

			// if hull is less than 70% of initial hull, chance to destroy ship
			double initialHull = ((structuralIntegrity * 0.1) * (1 + 0.1 * techLevels.getArmorTech()));
			if (hull < initialHull*0.7 && hull > 0) {
				double explodeChance = 1 - (hull / initialHull);
				if (explodeChance > rand.nextDouble()) {
					hull = 0;
				}
			}
		}
		
		if (attacker.getRapidFireMap() != null) {
			Integer rapidfireValue = attacker.getRapidFireMap().get(this.id);
			if (rapidfireValue == null)
				rapidfireValue = 0;

			double rapidfireChance = (rapidfireValue - 1.0) / rapidfireValue;

			if (rapidfireChance > rand.nextDouble())
				result.setRapidfireSuccess(true);
			else
				result.setRapidfireSuccess(false);
		} else
			result.setRapidfireSuccess(false);
		
		return result;
	}
	
	public void calculateEffectiveValues() {
		effectiveWeapon = (baseWeapon*(1 + 0.1*techLevels.getWeaponsTech()));
		initialHull = ((structuralIntegrity*0.1)*(1 + 0.1*techLevels.getArmorTech()));
		initialShield = (baseShield*(1 + 0.1*techLevels.getShieldTech()));
	}
	
	public void restoreArmor() {
		hull = initialHull;
	}
	
	public void restoreShield() {
		shield = initialShield;
	}
	
	public void restoreAll() {
		restoreArmor();
		restoreShield();
	}
	
	public double getHullPercentage() {
		return hull/initialHull;
	}

	public double getBaseHull() {
		return structuralIntegrity;
	} public double getBaseShield() {
		return baseShield;
	} public double getWeapon() {
		return baseWeapon;
	} public double getHull() {
		return hull;
	} public double getShield() {
		return shield;
	} public double getEffectiveWeapon() {
		return effectiveWeapon;
	} public int getUniqueID() {
		return uniqueID;
	} public void setUniqueID(int uniqueID) {
		this.uniqueID = uniqueID;
	} public String getEntityID() {
		return id;
	} public CombatEntityType getType() {
		return type;
	} public HashMap<String, Integer> getRapidFireMap() {
		return rapidFireMap;
	} public TechLevels getTechLevels() {
		return techLevels;
	} public void setTechLevels(TechLevels techLevels) {
		this.techLevels = techLevels;
	}

	@Override
	public String toString() {
		return "CombatEntity [name=" + id + ", type=" + type + ", baseHull=" + structuralIntegrity + ", baseShield=" + baseShield + ", baseWeapon=" + baseWeapon + ", hull=" + hull + ", shield=" + shield + ", effectiveWeapon=" + effectiveWeapon + ", rapidFireMap=" + rapidFireMap + "]";
	}

	@Override
	public CombatEntity clone() {
		return new CombatEntity(new String(id), new String(fleetName), uniqueID,
				type, structuralIntegrity,
				baseShield, baseWeapon, initialHull,
				initialShield, hull, shield,
				effectiveWeapon, rapidFireMap);
	}
	
	public void setFleetName(String fleetName) {
		this.fleetName = fleetName;
	} public String getFleetName() {
		return fleetName;
	}
}