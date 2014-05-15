package com.jonnygold.quantizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Histogram implements IsHistogram {
		
	public static class Builder{
		
		private Map<RGBColor, Integer> builderData = new HashMap<RGBColor, Integer>();
		
		public Builder addColor(RGBColor color){
			if(builderData.containsKey(color)) {
				builderData.put(color, builderData.get(color)+1 );
			} else {
				builderData.put(color, 1);
			}
			return this;
		}
		
		public Histogram build(){
			Map<RGBColor, Integer> newMap = new HashMap<RGBColor, Integer>();
			int sum = 0;
			for(RGBColor color : builderData.keySet()){
				sum+=builderData.get(color);
			}
			for(RGBColor color : builderData.keySet()){
				if(builderData.get(color) > 1340){
					newMap.put(color, builderData.get(color));
				}
			}
			return new Histogram(new HashMap<>(newMap));
		}
	}
	
	
	private Map<RGBColor, Integer> data = new HashMap<RGBColor, Integer>();
	
	
	private Histogram(Map<RGBColor, Integer> builderData){
		data = builderData;
	}
	
	@Override
	public Collection<RGBColor> getColors() {
		return data.keySet();
	}
	
	@Override
	public Map<RGBColor, Integer> getData() {
		return new HashMap<RGBColor, Integer>(data);
	}
	
	@Override
	public int getCount(RGBColor color) {
		return data.get(color);
	}

	@Override
	public boolean containsColor(RGBColor color) {
		return data.containsKey(color);
	}

	@Override
	public int size() {
		return data.size();
	}
	
	@Override
	public String toString() {
		return getData().toString();
	}
}
