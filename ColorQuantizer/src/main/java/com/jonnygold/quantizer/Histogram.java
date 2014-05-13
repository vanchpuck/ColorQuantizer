package com.jonnygold.quantizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Histogram implements IsHistogram {

//	private List<RGBColor> colors = new ArrayList<RGBColor>();
	
	private Map<RGBColor, Integer> data = new HashMap<RGBColor, Integer>();
	
	@Override
	public Collection<RGBColor> getColors() {
		return data.keySet();
	}

	@Override
	public int getCount(RGBColor color) {
		return data.get(color);
	}

	@Override
	public Map<RGBColor, Integer> getData() {
		return new HashMap<RGBColor, Integer>(data);
	}

	@Override
	public void addColor(RGBColor color) {
		if(data.containsKey(color)){
			data.put(color, data.get(color)+1);
		} else{
			data.put(color, 1);
		}
	}

	@Override
	public boolean containsColor(RGBColor color) {
		return data.containsKey(color);
	}

}
