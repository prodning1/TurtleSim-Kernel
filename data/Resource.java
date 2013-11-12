package com.prodning.turtlesim.kernel.data;

public class Resource {
	private int metal;
	private int crystal;
	private int deuterium;
	
	public Resource(int metal, int crystal, int deuterium) {
		this.metal = metal;
		this.crystal = crystal;
		this.deuterium = deuterium;
	}
	
	public Resource() {
		this(0,0,0);
	}
	
	/**
	 * @return the metal equivalent with ratio 1:1.5:3
	 */
	public int getMetalEquivalent() {
		return metal + (int) (crystal*1.5) + (int) (deuterium*2.5);
	}
	
	public int getTotal() {
		return metal + crystal + deuterium;
	}
	
	public int getMetal() {
		return metal;
	} public void setMetal(int metal) {
		this.metal = metal;
	} public int getCrystal() {
		return crystal;
	} public void setCrystal(int crystal) {
		this.crystal = crystal;
	} public int getDeuterium() {
		return deuterium;
	} public void setDeuterium(int deuterium) {
		this.deuterium = deuterium;
	}
	
	public Resource add(Resource r) {
		return new Resource(metal + r.getMetal(), crystal + r.getCrystal(), deuterium + r.getDeuterium());
	}
	
	public void addThis(Resource r) {
		metal += r.getMetal();
		crystal += r.getCrystal();
		deuterium += r.getDeuterium();
	}
	
	public Resource scalar(double d) {
		return new Resource((int)(metal*d),
							(int)(crystal*d),
							(int)(deuterium*d));
	}
	
	public Resource clone() {
		return new Resource(metal, crystal, deuterium);
	}
	
	@Override
	public String toString() {
		return String.format("(Metal: %d, Crystal: %d, Deuterium: %d)",metal,crystal,deuterium);
	}
}
