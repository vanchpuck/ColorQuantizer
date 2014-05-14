package com.jonnygold.quantizer;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class RGBCubeTest {

	private RGBCube cube;
	
	@Before
	public void init(){
		IsHistogram histogram = new Histogram.Builder().
				addColor(new RGBColor(20, 40, 0)).
				addColor(new RGBColor(20, 40, 0)).
				addColor(new RGBColor(20, 40, 0)).
				addColor(new RGBColor(40, 20, 0)).
				addColor(new RGBColor(40, 20, 0)).
				addColor(new RGBColor(5, 60, 0)).
				addColor(new RGBColor(5, 60, 0)).
				addColor(new RGBColor(5, 60, 0)).
				addColor(new RGBColor(5, 60, 0)).
				addColor(new RGBColor(50, 80, 0)).
				addColor(new RGBColor(50, 80, 0)).
				addColor(new RGBColor(60, 30, 0)).
				addColor(new RGBColor(80, 50, 0)).
				addColor(new RGBColor(80, 50, 0)).
				build();
		cube = new RGBCube(histogram);
	}
	
	@Test
	public void test() {
		Collection<RGBColor> colors = cube.quantize(2);
		for(RGBColor c : colors){
			System.out.println(c);
		}
	}

}
