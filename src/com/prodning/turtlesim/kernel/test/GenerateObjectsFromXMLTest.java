package com.prodning.turtlesim.kernel.test;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.prodning.turtlesim.kernel.combat.CombatSimulation;
import com.prodning.turtlesim.kernel.combat.Fleet;
import com.prodning.turtlesim.kernel.combat.data.FleetCombatUnit;
import com.prodning.turtlesim.kernel.combat.data.MacroCombatResult;
import com.prodning.turtlesim.kernel.combat.data.TechLevels;
import com.prodning.turtlesim.kernel.combat.data.FleetCombatUnit.CombatGroup;
import com.prodning.turtlesim.kernel.parse.EntityFileParser;

public class GenerateObjectsFromXMLTest {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		Fleet fleet1 = EntityFileParser.getFleetById("F0001");
		Fleet fleet2 = EntityFileParser.getFleetById("F0002");
		
		System.out.println(Fleet.compositionIdToName(fleet1.getFleetComposition()).toString());
		System.out.println(Fleet.compositionIdToName(fleet2.getFleetComposition()).toString());
		
		FleetCombatUnit fcu1 = new FleetCombatUnit(fleet1);
		fcu1.setCombatGroup(CombatGroup.ATTACKING);
		FleetCombatUnit fcu2 = new FleetCombatUnit(fleet2);
		fcu2.setCombatGroup(CombatGroup.DEFENDING);
		
		ArrayList<FleetCombatUnit> fleets = new ArrayList<FleetCombatUnit>();
		
		fleets.add(fcu1);
		fleets.add(fcu2);
		
		fcu1.setTechLevels(new TechLevels());
		fcu2.setTechLevels(new TechLevels());

		MacroCombatResult result = CombatSimulation.fleetCombat(fleets);

		System.out.println();
		System.out.println("******** RESULT ********");
		System.out.println(result.getResultType().toString() + " after "
				+ result.getRounds() + " rounds.");
//		System.out.println("Total attacker lost ships: "
//				+ result.getAttackerLosses().size());
//		System.out.println("Total defender lost ships: "
//				+ result.getDefenderLosses().size());
		
		System.out.println(Fleet.compositionIdToName(fleet1.getFleetComposition()).toString());
		System.out.println(Fleet.compositionIdToName(fleet2.getFleetComposition()).toString());
	}
}