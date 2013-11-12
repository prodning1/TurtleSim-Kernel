//Copyright 2013 Philip Rodning

package com.prodning.turtlesim.kernel.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.prodning.turtlesim.kernel.combat.CombatEntity.CombatEntityType;
import com.prodning.turtlesim.kernel.combat.data.FleetCombatUnit;
import com.prodning.turtlesim.kernel.combat.data.MacroCombatResult;
import com.prodning.turtlesim.kernel.combat.data.MicroCombatResult;
import com.prodning.turtlesim.kernel.combat.data.SimulationResult;
import com.prodning.turtlesim.kernel.combat.data.TechLevels;
import com.prodning.turtlesim.kernel.combat.data.FleetCombatUnit.CombatGroup;
import com.prodning.turtlesim.kernel.combat.data.MacroCombatResult.ResultType;

public class CombatSimulation {
	static int verbosity = 2;
	
	public static SimulationResult SimulateFleetCombat(List<FleetCombatUnit> fleetCombatUnits, int numberOfSimulations) {
		SimulationResult result = new SimulationResult();
		
		for(int i = 0; i < numberOfSimulations; i++) {
			ArrayList<FleetCombatUnit> newFleetCombatUnits = new ArrayList<FleetCombatUnit>();
			
			for(FleetCombatUnit fcu : fleetCombatUnits) {
				newFleetCombatUnits.add(fcu.deepClone());
			}
			
			MacroCombatResult mcr = fleetCombat(newFleetCombatUnits);
			
			result.addCombatResult(mcr);
		}
		
		return result;
	}
	
	public static MacroCombatResult fleetCombat(List<FleetCombatUnit> fleetCombatUnits) {
		Random rand = new Random();
		
		ArrayList<CombatEntity> attackerLosses = new ArrayList<CombatEntity>();
		ArrayList<CombatEntity> defenderLosses = new ArrayList<CombatEntity>();
		
		//Before the fight, make sure all combat entities have full hull, shields, and correct weapon values
		for (FleetCombatUnit fcu : fleetCombatUnits) {
			for (CombatEntity entity : fcu.getFleet()) {
				entity.setTechLevels(fcu.getTechLevels());
				entity.calculateEffectiveValues();
				entity.restoreAll();
			}
		}
		
		int round = 0;
		
		ArrayList<FleetCombatUnit> attackers = new ArrayList<FleetCombatUnit>();
		ArrayList<FleetCombatUnit> defenders = new ArrayList<FleetCombatUnit>();
		
		for (FleetCombatUnit fcu : fleetCombatUnits) {
			if(fcu.getCombatGroup() == CombatGroup.ATTACKING)
				attackers.add(fcu);
			else if(fcu.getCombatGroup() == CombatGroup.DEFENDING)
				defenders.add(fcu);
			//else error checking
		}
		
		Boolean attackerDestroyed = true;
		Boolean defenderDestroyed = true;
		
		//Simulate
		do {
			//begin combat round
			
			//for combat report
			int attackerFires = 0;
			int attackerPower = 0;
			int attackerShieldTotal = 0;
			
			int defenderFires = 0;
			int defenderPower = 0;
			int defenderShieldTotal = 0;
			
			if(verbosity >= 2) {
				System.out.println("Attacking fleet: " + Fleet.compositionIdToName(fleetCombatUnits.get(0).getFleet().getFleetComposition()).toString());
				System.out.println("Defending fleet: " + Fleet.compositionIdToName(fleetCombatUnits.get(1).getFleet().getFleetComposition()).toString());
			}
			
			//at beginning of each round, restore shields of all remaining units
			for (FleetCombatUnit ft : fleetCombatUnits) {
				for (CombatEntity entity : ft.getFleet()) {
					if (entity.getHull() > 0)
						entity.restoreShield();
				}
			}
			
			if(verbosity >= 3) {
				System.out.println("Attacking fleet:\n" + fleetCombatUnits.get(0).getFleet().toString());
				System.out.println("Defending fleet:\n" + fleetCombatUnits.get(1).getFleet().toString());
			}
			
			for (FleetCombatUnit fcu : fleetCombatUnits) {
				for (CombatEntity attackingEntity : fcu.getFleet()) {
					Boolean rapidFireSuccess;

					if (attackingEntity.getType() == CombatEntityType.DEFENSE) {
						System.out.println("Ignoring defense object "
								+ attackingEntity.getEntityID()
								+ " in attacking fleet.");
					}

					do {
						CombatEntity defendingEntity;
						
						// Pick a random defender to attack
						if(fcu.getCombatGroup() == CombatGroup.ATTACKING)
							defendingEntity = chooseRandomEntityFromACS(defenders);
						else
							defendingEntity = chooseRandomEntityFromACS(attackers);
						
						// defending unit receives the attack
						MicroCombatResult mcr = defendingEntity.receiveAttack(attackingEntity);

						// check for successful rapid fire roll
						rapidFireSuccess = mcr.getRapidFireSuccess();

						if (fcu.getCombatGroup() == CombatGroup.ATTACKING) {
							attackerFires++;
							attackerPower += attackingEntity.getEffectiveWeapon();
							defenderShieldTotal += mcr.getShieldDamage();
						}
						if (fcu.getCombatGroup() == CombatGroup.DEFENDING) {
							defenderFires++;
							defenderPower += attackingEntity.getEffectiveWeapon();
							attackerShieldTotal += mcr.getShieldDamage();
						}
					} while (rapidFireSuccess);

					// end attacker volley
				}
			}
			
			// check for losses and move them to the appropriate lists
			for (FleetCombatUnit fcu : fleetCombatUnits) {
				Iterator<CombatEntity> iter = fcu.getFleet().iterator();
				while (iter.hasNext()) {
					CombatEntity c = iter.next();
					
					if (c.getHull() <= 0) {
						fcu.getLosses().add(c);
						iter.remove();
					}
				}
			}
			
			if (verbosity >= 2) {
				System.out.println();
				System.out.println("The attacking fleet fires a total of " + attackerFires + " times with the power of " + attackerPower + " upon the defender.");
				System.out.println("The defender's shields absorb " + defenderShieldTotal + " damage points");
				System.out.println();
				System.out.println("The defending fleet fires a total of " + defenderFires + " times with the power of " + defenderPower + " upon the attacker.");
				System.out.println("The attacker's shields absorb " + attackerShieldTotal + " damage points");
				System.out.println();
			}
			
			
			//both fleet unions are destroyed unless we find ships
			attackerDestroyed = true;
			defenderDestroyed = true;
			
			for (FleetCombatUnit fcu : fleetCombatUnits) {
				if(fcu.getFleet().size() > 0) {
					switch(fcu.getCombatGroup()) {
						case ATTACKING:
							attackerDestroyed = false;
							break;
						case DEFENDING:
							defenderDestroyed = false;
							break;
					}
				}
			}
		} while ((++round < 6) && !attackerDestroyed && !defenderDestroyed);
		
		MacroCombatResult mcr = new MacroCombatResult();
		
		mcr.setFleetCombatUnits(fleetCombatUnits);
		mcr.setRounds(round);
		
		//determine result type
		if((!attackerDestroyed) && (defenderDestroyed)) {
			mcr.setResultType(ResultType.ATTACKER_WIN);
		}
		if((attackerDestroyed) && (!defenderDestroyed)) {
			mcr.setResultType(ResultType.DEFENDER_WIN);
		}
		if(attackerDestroyed.equals(defenderDestroyed)) {
			//either both fleets still alive or both destroyed (unlikely but possible)
			mcr.setResultType(ResultType.DRAW);
		}
		
		if (verbosity >= 1) {
			System.out.println();
			System.out.println("******** FINAL ********");
			System.out.println();

			System.out.println("Attacking Fleet:");
			System.out.println(Fleet.compositionIdToName(fleetCombatUnits.get(0).getFleet().getFleetComposition()));
			System.out.println();

			System.out.println("Defending Fleet:");
			System.out.println(Fleet.compositionIdToName(fleetCombatUnits.get(1).getFleet().getFleetComposition()));
			System.out.println();
		}
		
		return mcr;
	}
	
	private static CombatEntity chooseRandomEntityFromACS(List<FleetCombatUnit> fleetUnion) {
		//TODO check this
		
		Random rand = new Random();
		
		//find total size of fleet union 
		int totalSize = 0;
		for(FleetCombatUnit fcu : fleetUnion)
			totalSize += fcu.getFleet().size();
		
		//pick index within total size
		int index = rand.nextInt(totalSize);
		
		//iterate through fleets until you find the one that has that index in it
		for(FleetCombatUnit fcu : fleetUnion) {
			if(index > fcu.getFleet().size())
				index -= fcu.getFleet().size();
			else
				return fcu.getFleet().get(index);
		}
		
		System.out.println("You shouldn't be here");
		return null;
	}
}