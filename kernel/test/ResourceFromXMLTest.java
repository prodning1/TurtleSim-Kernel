package com.prodning.turtlesim.kernel.test;

import com.prodning.turtlesim.kernel.data.Resource;
import com.prodning.turtlesim.kernel.parse.EntityFileParser;

public class ResourceFromXMLTest {

	public static void main(String[] args) {
		Resource r = EntityFileParser.getResourceById("1004");
		
		System.out.println(r.getMetal());
		System.out.println(r.getCrystal());
		System.out.println(r.getDeuterium());
	}
}
