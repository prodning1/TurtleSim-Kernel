package com.prodning.turtlesim.kernel.combat.data;

import java.util.HashMap;

import com.prodning.turtlesim.kernel.data.Resource;

public class FCUSimulationResult {
	private Resource totalLosses = new Resource();
	private HashMap<String, Integer> fleetCompositionSum = new HashMap<String, Integer>();
	
	public Resource getTotalLosses() {
		return totalLosses;
	} public void setTotalLosses(Resource totalLosses) {
		this.totalLosses = totalLosses;
	} public HashMap<String, Integer> getFleetCompositionSum() {
		return fleetCompositionSum;
	} public void setFleetCompositionSum(HashMap<String, Integer> fleetComposition) {
		this.fleetCompositionSum = fleetComposition;
	}
}