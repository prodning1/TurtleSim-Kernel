package com.prodning.turtlesim.kernel.combat.data;

import java.util.HashMap;

import com.prodning.turtlesim.kernel.combat.CombatEntity;
import com.prodning.turtlesim.kernel.combat.CombatSettings;
import com.prodning.turtlesim.kernel.combat.Fleet;
import com.prodning.turtlesim.kernel.combat.CombatEntity.CombatEntityType;
import com.prodning.turtlesim.kernel.combat.data.MacroCombatResult.ResultType;
import com.prodning.turtlesim.kernel.data.Resource;
import com.prodning.turtlesim.kernel.parse.EntityFileParser;

public class SimulationResult {
	private int numberOfSimulations = 0;

	private HashMap<String, FCUSimulationResult> fcuSimResults = new HashMap<String, FCUSimulationResult>();

	private Resource debrisFieldSum = new Resource();
	private double attackerWins = 0;
	private double defenderWins = 0;
	private double draws = 0;
	private double roundsSum = 0;

	public void addCombatResult(MacroCombatResult macroCombatResult) {
		numberOfSimulations++;
		for (FleetCombatUnit fcu : macroCombatResult.getFleetCombatUnits()) {

			FCUSimulationResult fcuSimResult = new FCUSimulationResult();

			Resource debrisFieldThisCombat = new Resource();

			// Get attacker and defender resource losses
			for (CombatEntity entity : fcu.getLosses()) {
				fcuSimResult.getTotalLosses().addThis(
						EntityFileParser.getResourceById(entity.getEntityID()));
			}

			// Get attacker and defender debris field contributions
			for (CombatEntity entity : fcu.getLosses()) {
				if (entity.getType() == CombatEntityType.SHIP || CombatSettings.getDefenseToDebris() == true) {
					Resource debrisResource = new Resource(0,0,0);
					
					if (entity.getType() == CombatEntityType.SHIP)
						debrisResource = EntityFileParser.getResourceById(entity.getEntityID()).scalar(CombatSettings.getShipDebrisRatio());
					else if (entity.getType() == CombatEntityType.DEFENSE)
						debrisResource = EntityFileParser.getResourceById(entity.getEntityID()).scalar(CombatSettings.getDefenseDebrisRatio());
					else {
						// TODO error checking
						System.out.println("Error in debris field calculation");
						debrisResource = new Resource(-100000000, -100000000, -100000000);
					}

					// no deut in debris field
					debrisResource.setDeuterium(0);

					debrisFieldSum.addThis(debrisResource);
				}
			}

			HashMap<String, Integer> newComposition = Fleet.compositionIdToName(fcu.getFleet().getFleetComposition());
			HashMap<String, Integer> fleetCompositionSum;
			if(fcuSimResults.containsKey(fcu.getId()))
				fleetCompositionSum = fcuSimResults.get(fcu.getId()).getFleetCompositionSum();
			else
				fleetCompositionSum = new HashMap<String, Integer>();

			for (String s : newComposition.keySet()) {
				if (fleetCompositionSum.containsKey(s))
					fleetCompositionSum.put(s, fleetCompositionSum.get(s)
							+ newComposition.get(s));
				else
					fleetCompositionSum.put(s, newComposition.get(s));
			}
		}

		attackerWins += (macroCombatResult.getResultType() == ResultType.ATTACKER_WIN ? 1 : 0);
		defenderWins += (macroCombatResult.getResultType() == ResultType.DEFENDER_WIN ? 1 : 0);
		draws += (macroCombatResult.getResultType() == ResultType.DRAW ? 1 : 0);

		roundsSum += macroCombatResult.getRounds();
	}

	public int getNumberOfSimulations() {
		return numberOfSimulations;
	}

	public Resource getDebrisField() {
		return new Resource(debrisFieldSum.getMetal()
				/ numberOfSimulations, debrisFieldSum.getCrystal()
				/ numberOfSimulations, debrisFieldSum.getDeuterium()
				/ numberOfSimulations);
	}

	public double getAttackerWinChance() {
		return (double) attackerWins / numberOfSimulations;
	}

	public double getDefenderWinChance() {
		return (double) defenderWins / numberOfSimulations;
	}

	public double getDrawChance() {
		return (double) draws / numberOfSimulations;
	}

	public double getRounds() {
		return (double) roundsSum / numberOfSimulations;
	}
}
