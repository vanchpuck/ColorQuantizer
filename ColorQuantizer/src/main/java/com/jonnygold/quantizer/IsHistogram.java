package com.jonnygold.quantizer;

import java.util.Collection;
import java.util.Map;

public interface IsHistogram {

	public Collection<RGBColor> getColors();
	
	public int getCount(RGBColor color);
	
	public Map<RGBColor, Integer> getData();
	
	public boolean containsColor(RGBColor color);
	
	public int size();
	
}
