package com.prodning.turtlesim.kernel.test;

import java.util.HashMap;

import com.prodning.turtlesim.kernel.combat.CombatEntity;
import com.prodning.turtlesim.kernel.combat.CombatEntity.CombatEntityType;
import com.prodning.turtlesim.kernel.combat.data.MicroCombatResult;
import com.prodning.turtlesim.kernel.combat.data.TechLevels;

public class MicroCombatTest {
	public static void main(String[] args) {
		HashMap<String, Integer> cruiserRapidFire = new HashMap<String, Integer>();
		cruiserRapidFire.put("LIGHT_FIGHTER", 6);
		cruiserRapidFire.put("ESPIONAGE_PROBE", 5);
		cruiserRapidFire.put("SOLAR_SATELLITE", 5);
		
		HashMap<String, Integer> lfRapidFire = new HashMap<String, Integer>();
		lfRapidFire.put("ESPIONAGE_PROBE", 5);
		lfRapidFire.put("SOLAR_SATELLITE", 5);
		
		CombatEntity attackerEntity = new CombatEntity("CRUISER", CombatEntityType.SHIP, 27000, 50, 400, cruiserRapidFire);
		CombatEntity defenderEntity = new CombatEntity("LIGHT_FIGHTER", CombatEntityType.SHIP, 4000, 10, 50, lfRapidFire);
		
		attackerEntity.setTechLevels(new TechLevels());
		defenderEntity.setTechLevels(new TechLevels());
		attackerEntity.restoreAll();
		attackerEntity.calculateEffectiveValues();
		defenderEntity.restoreAll();
		defenderEntity.calculateEffectiveValues();
		
		MicroCombatResult result;
		do {
			result = defenderEntity.receiveAttack(attackerEntity);
			
			defenderEntity.restoreAll();
		} while (result.getRapidFireSuccess());
	}
}
