package com.jonnygold.quantizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Histogram implements IsHistogram {
	
	public static class Bar {
		
		private RGBColor color;
		
		private int count;
		
		public Bar(RGBColor color, int count) {
			this.color = color;
			this.count = count;
		}
		
		public RGBColor getColor() {
			return color;
		}
		
		public int getCount() {
			return count;
		}
		
		public void setColor(RGBColor color) {
			this.color = color;
		}
		
		public void setCount(int count) {
			if(count < 0){
				throw new IllegalArgumentException("Colors count should be greater than 0");
			}
			this.count = count;
		}
				
	}
	
	public static class Builder{
		
		private Map<RGBColor, Bar> builderData = new HashMap<RGBColor, Bar>();
		
		public Builder addColor(RGBColor color){
			if(builderData.containsKey(color)) {
				builderData.get(color).setCount(builderData.get(color).getCount()+1);
			} else {
				builderData.put(color, new Bar(color, 1));
			}
			return this;
		}
		
		public Builder addColor(RGBColor color, int count){
			if(builderData.containsKey(color)) {
				builderData.get(color).setCount(builderData.get(color).getCount()+count);
			} else {
				builderData.put(color, new Bar(color, count));
			}
			return this;
		}
		
		public Histogram build(){
			return new Histogram(new HashMap<>(builderData));
		}
		
		public Histogram build(int bound) {
			Map<RGBColor, Bar> data = new HashMap<RGBColor, Bar>(builderData);
			
			RGBColor color = null;
			for(Iterator<RGBColor> iter = data.keySet().iterator(); iter.hasNext(); ){
				color = iter.next();
				if(data.get(color).getCount() <= bound){
					iter.remove();;
				}
			}
			return new Histogram(data);
		}
		
		public Histogram build(double bound) {
			Map<RGBColor, Bar> data = new HashMap<RGBColor, Bar>(builderData);
			
			int sum = 0;
			for(RGBColor color : data.keySet()){
				sum+=data.get(color).getCount();
			}
			RGBColor color = null;			
			for(Iterator<RGBColor> iter = data.keySet().iterator(); iter.hasNext(); ){
				color = iter.next();
				if((double)data.get(color).getCount()/(double)sum <= bound){
					iter.remove();;
				}
			}
			return new Histogram(data);
		}
	}
	
	
	private Map<RGBColor, Bar> data;
	
	
	private Histogram(Map<RGBColor, Bar> builderData){
		data = builderData;
	}
	
	@Override
	public Collection<RGBColor> getColors() {
		return data.keySet();
	}
	
	@Override
	public Map<RGBColor, Integer> getData() {
		Map<RGBColor, Integer> result = new HashMap<>(data.size());
		for(Map.Entry<RGBColor, Bar> entry : data.entrySet()){
			result.put(entry.getKey(), entry.getValue().getCount());
		}
		return result;
	}
	
	@Override
	public int getCount(RGBColor color) {
		return data.get(color).getCount();
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
