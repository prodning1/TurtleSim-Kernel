package com.prodning.turtlesim.kernel.test;

import java.util.ArrayList;
import java.util.HashMap;

import com.prodning.turtlesim.kernel.combat.CombatEntity;
import com.prodning.turtlesim.kernel.combat.CombatSimulation;
import com.prodning.turtlesim.kernel.combat.Fleet;
import com.prodning.turtlesim.kernel.combat.CombatEntity.CombatEntityType;
import com.prodning.turtlesim.kernel.combat.data.FleetCombatUnit;
import com.prodning.turtlesim.kernel.combat.data.MacroCombatResult;
import com.prodning.turtlesim.kernel.combat.data.TechLevels;
import com.prodning.turtlesim.kernel.combat.data.FleetCombatUnit.CombatGroup;
import com.prodning.turtlesim.kernel.combat.data.MacroCombatResult.ResultType;

public class MacroCombatTest {
	public static void main(String[] args) {
		Fleet attackingFleet = new Fleet("ATTACKER");
		Fleet defendingFleet = new Fleet("DEFENDER");
		
		HashMap<String, Integer> cruiserRapidFire = new HashMap<String, Integer>();
		cruiserRapidFire.put("LIGHT_FIGHTER", 6);
		cruiserRapidFire.put("ESPIONAGE_PROBE", 5);
		cruiserRapidFire.put("SOLAR_SATELLITE", 5);
		cruiserRapidFire.put("ROCKET_LAUNCHER", 10);
		
		HashMap<String, Integer> lfRapidFire = new HashMap<String, Integer>();
		lfRapidFire.put("ESPIONAGE_PROBE", 5);
		lfRapidFire.put("SOLAR_SATELLITE", 5);
		
		int atkWins = 0;
		int defWins = 0;
		int draw = 0;
		
		for (int n = 0; n < 100; n++) {
			attackingFleet = new Fleet("ATTACKER");
			defendingFleet = new Fleet("DEFENDER");
			
			for (int i = 0; i < 5; i++)
				attackingFleet.addToFleet(new CombatEntity("CRUISER",
						CombatEntityType.SHIP, 27000, 50, 400,
						cruiserRapidFire));
//			for (int i = 0; i < 1110; i++)
//				attackingFleet.addToFleet(new CombatEntity("LIGHT_FIGHTER",
//						CombatEntityType.SHIP, 4000, 10, 50,
//						lfRapidFire));
			
			for (int i = 0; i < 50; i++)
				defendingFleet.addToFleet(new CombatEntity("LIGHT_FIGHTER",
						CombatEntityType.SHIP, 4000, 10, 50,
						lfRapidFire));
//			for (int i = 0; i < 500; i++)
//				defendingFleet.addToFleet(new CombatEntity("ROCKET_LAUNCHER",
//						CombatEntityType.DEFENSE, 2000, 20, 80,
//						null));

			FleetCombatUnit fcu1 = new FleetCombatUnit(attackingFleet);
			fcu1.setCombatGroup(CombatGroup.ATTACKING);
			FleetCombatUnit fcu2 = new FleetCombatUnit(defendingFleet);
			fcu2.setCombatGroup(CombatGroup.DEFENDING);
			
			ArrayList<FleetCombatUnit> fleets = new ArrayList<FleetCombatUnit>();
			
			fleets.add(fcu1);
			fleets.add(fcu2);
			
			fcu1.setTechLevels(new TechLevels());
			fcu2.setTechLevels(new TechLevels());

			MacroCombatResult result = CombatSimulation.fleetCombat(fleets);

//			System.out.println();
//			System.out.println("******** RESULT ********");
//			System.out.println(result.getResultType().toString() + " after "
//					+ result.getRounds() + " rounds.");
//			System.out.println("Total attacker lost ships: "
//					+ result.getAttackerLosses().size());
//			System.out.println("Total defender lost ships: "
//					+ result.getDefenderLosses().size());
			
			if(result.getResultType() == ResultType.ATTACKER_WIN)
				atkWins++;
			if(result.getResultType() == ResultType.DEFENDER_WIN)
				defWins++;
			if(result.getResultType() == ResultType.DRAW)
				draw++;
		}
		
		System.out.printf("Atk wins: %d\nDef wins: %d\nDraw: %d\n", atkWins, defWins, draw);
	}
}
