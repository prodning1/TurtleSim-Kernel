package com.prodning.turtlesim.kernel.test.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.prodning.turtlesim.kernel.combat.CombatSettings;
import com.prodning.turtlesim.kernel.combat.CombatSimulation;
import com.prodning.turtlesim.kernel.combat.Fleet;
import com.prodning.turtlesim.kernel.combat.data.FleetCombatUnit;
import com.prodning.turtlesim.kernel.combat.data.SimulationResult;
import com.prodning.turtlesim.kernel.combat.data.TechLevels;
import com.prodning.turtlesim.kernel.combat.data.FleetCombatUnit.CombatGroup;
import com.prodning.turtlesim.kernel.parse.EntityFileParser;

public class FleetCombatTestGUI {
	public static JTextField attackingFleetTextField = new JTextField(30);
	public static JTextField defendingFleetTextField = new JTextField(30);
	
	public static JTextArea resultsTextArea = new JTextArea();
	public static JScrollPane resultsScrollPane = new JScrollPane(resultsTextArea);
	
	public static JButton simulateButton = new JButton("Simulate");
	
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		
		jf.setLayout(new GridBagLayout());
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		jf.add(new JLabel("Attacking Fleet ID"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		jf.add(attackingFleetTextField, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		jf.add(new JLabel("Defending Fleet ID"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 1;
		jf.add(defendingFleetTextField, c);
		
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		jf.add(resultsScrollPane, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		jf.add(resultsScrollPane, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		jf.add(simulateButton, c);
		
		simulateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					resultsTextArea.setText(getResults(attackingFleetTextField.getText(), defendingFleetTextField.getText()));
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		jf.pack();
		jf.setVisible(true);
	}

	public static String getResults(String fleet1s, String fleet2s) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		CombatSettings.setDefenseToDebris(false);
		CombatSettings.setShipDebrisRatio(0.3);
		CombatSettings.setDefenseDebrisRatio(0.3);
		
		System.out.print("Parsing fleets...");
		Fleet fleet1 = EntityFileParser.getFleetById(fleet1s);
		Fleet fleet2 = EntityFileParser.getFleetById(fleet2s);
		System.out.println("done\n");
		
		if(fleet1 == null || fleet2 == null)
			System.exit(1);
		
		FleetCombatUnit fcu1 = new FleetCombatUnit(fleet1);
		fcu1.setCombatGroup(CombatGroup.ATTACKING);
		FleetCombatUnit fcu2 = new FleetCombatUnit(fleet2);
		fcu2.setCombatGroup(CombatGroup.DEFENDING);
		
		ArrayList<FleetCombatUnit> fleets = new ArrayList<FleetCombatUnit>();
		
		fleets.add(fcu1);
		fleets.add(fcu2);
		
		fcu1.setTechLevels(new TechLevels());
		fcu2.setTechLevels(new TechLevels());
		
		String resultString = "";
		
		resultString +="Attacking fleet: " + Fleet.compositionIdToName(fleet1.getFleetComposition()).toString()+"\n";
		resultString +="Defending fleet: " + Fleet.compositionIdToName(fleet2.getFleetComposition()).toString()+"\n";
		resultString +="\n";

		resultString += "Simulating combat...";
		SimulationResult result = CombatSimulation.SimulateFleetCombat(fleets, 1);
		resultString +="done\n\n";
		
		resultString +="\n";
		resultString +="Attacker wins:   " + result.getAttackerWinChance()*100 + "%"+"\n";
		resultString +="Defender wins:   " + result.getDefenderWinChance()*100 + "%"+"\n";
		resultString +="Draw:            " + result.getDrawChance()*100 + "%"+"\n";
		resultString +="In ~" + result.getRounds() + " rounds"+"\n";
//		resultString +=);
//		resultString +="Losses attacker: " + result.getAttackerLosses());
//		resultString +="Losses defender: " + result.getDefenderLosses());
		resultString +="\n";
		resultString +="Debris field:    " + result.getDebrisField().toString()+"\n";
//		resultString +=);
//		resultString +="Attacking fleet: " + result.getAttackerFleetComposition());
//		resultString +="Defending fleet: " + result.getDefenderFleetComposition());
		
		return resultString;
	}
}