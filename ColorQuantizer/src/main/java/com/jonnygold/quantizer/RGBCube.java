package com.jonnygold.quantizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RGBCube {
		
	private static class Quantizer {
		
		private static final int RED_QUANTIZE_BOUND = 60;
		private static final int GREEN_QUANTIZE_BOUND = 60;
		private static final int BLUE_QUANTIZE_BOUND = 90;
		
		private final class CubeDimension {
			private int rDim, gDim, bDim, maxDim;
			
			public CubeDimension(int rDim, int gDim, int bDim){
				this.rDim = rDim;
				this.gDim = gDim;
				this.bDim = bDim;
				
				if(rDim >= gDim && rDim >= bDim)
					maxDim = rDim;
				else if(gDim >= rDim && rDim >= bDim) 
					maxDim = gDim;
				else 
					maxDim = bDim;
			}
			
			public int getRedDim() {
				return rDim;
			}
			
			public int getGreenDim() {
				return gDim;
			}
			
			public int getBlueDim() {
				return bDim;
			}
			
			public int getMaxDim() {
				return maxDim;
			}
			
			public Comparator<IsRGBColor> getComparator(){
				if(maxDim == rDim) {
					return RED_COMPARATOR;
				} else if(maxDim == gDim) {
					return GREEN_COMPARATOR;
				} else {
					return BLUE_COMPARATOR;
				}
			}
			
			public boolean isQuantized(){
				if(maxDim == rDim && maxDim > RED_QUANTIZE_BOUND) {
					return true;
				} else if(maxDim == gDim  && maxDim > GREEN_QUANTIZE_BOUND) {
					return true;
				} else if(maxDim == bDim  && maxDim > BLUE_QUANTIZE_BOUND){
					return true;
				}
				return false;
			}
		}
				
		private static final Comparator<IsRGBColor> RED_COMPARATOR = new Comparator<IsRGBColor>() {
			@Override
			public int compare(IsRGBColor color1, IsRGBColor color2) {
				return Integer.compare(color1.getRed(), color2.getRed());
			}
		};
		
		private static final Comparator<IsRGBColor> GREEN_COMPARATOR = new Comparator<IsRGBColor>() {
			@Override
			public int compare(IsRGBColor color1, IsRGBColor color2) {
				return Integer.compare(color1.getGreen(), color2.getGreen());
			}
		};
		
		private static final Comparator<IsRGBColor> BLUE_COMPARATOR = new Comparator<IsRGBColor>() {
			@Override
			public int compare(IsRGBColor color1, IsRGBColor color2) {
				return Integer.compare(color1.getBlue(), color2.getBlue());
			}
		};
		
		Map<RGBColor, Integer> data;
		
		private Set<IsRGBColor> mainColors = new HashSet<IsRGBColor>();
		
		Quantizer(IsHistogram histogram){
			data = histogram.getData();
		}
		
		public Collection<IsRGBColor> quantize(int level){
			IsRGBColor[] colors = getColorsArray();
			quantize(colors, 0, colors.length, 0, level);
			return mainColors;
		}
		
		private void quantize(IsRGBColor[] colors, int from, int to, int currLevel, int maxLevel){
			if(from == to){
				//throw new IllegalArgumentException("Невозможно квантовать куб нулевой размерности.");
				return;
			}
			if(currLevel >= maxLevel || to-from==1){
				mainColors.add(getCenter(colors, from, to));
				return;
			}
			CubeDimension dim = getCubeDimension(colors, from, to);
			if(!dim.isQuantized()){
				mainColors.add(getCenter(colors, from, to));
				return;
			}
			Arrays.sort(colors, from, to, dim.getComparator());
			int medianIdx = findMedianIdx(colors, from, to);
			quantize(colors, from, medianIdx, currLevel+1, maxLevel);
			quantize(colors, medianIdx, to, currLevel+1, maxLevel);
		}
		
		private int findSplitIdx(IsRGBColor[] colors, int from, int to){
			
		}
		
		private int findMedianIdx(IsRGBColor[] colors, int from, int to){
			int totalSum = 0;
			int medianIdx = from;
			
			for(int i=from; i<to; i++){
				totalSum+=data.get(colors[i]);
			}
			for(int i=from, sum=0; i<to && sum<(totalSum>>1); i++){
				sum+=data.get(colors[i]);
				medianIdx = i;
			}
			if(medianIdx == to){
				return medianIdx;
			} 
			return medianIdx+1;
		}
		
		private CubeDimension getCubeDimension(IsRGBColor[] colors, int from, int to){
			int currR, currG, currB;
			int minR, maxR, minG, maxG, minB, maxB;
			minR = maxR = colors[from].getRed();
			minG = maxG = colors[from].getGreen();
			minB = maxB = colors[from].getBlue();
			for(int i=from+1; i<to; i++) {
				currR = colors[i].getRed();
				currG = colors[i].getGreen();
				currB = colors[i].getBlue();
				if(currR < minR){
					minR = currR;
				} else if(currR > maxR) {
					maxR = currR;
				}
				if(currG < minG){
					minG = currG;
				} else if(currG > maxG) {
					maxG = currG;
				}
				if(currB < minB){
					minB = currB;
				} else if(currB > maxB) {
					maxB = currB;
				}
			}
			return new CubeDimension(maxR - minR, maxG - minG, maxB - minB);
		}

		private IsRGBColor getCenter(IsRGBColor[] colors, int from, int to){
			double colorsCount = getColorsCount(colors, from, to);
			double rCenter = 0, gCenter = 0, bCenter = 0;
			for(int i=from; i<to; i++){
				rCenter += data.get(colors[i])/colorsCount*colors[i].getRed();
				gCenter += data.get(colors[i])/colorsCount*colors[i].getGreen();
				bCenter += data.get(colors[i])/colorsCount*colors[i].getBlue();
			}
			return new RGBColor((int)rCenter, (int)gCenter, (int)bCenter);
		}
		
		private int getColorsCount(IsRGBColor[] colors, int from, int to){
			int counter = 0;
			for(int i=from; i<to; i++){
				counter += data.get(colors[i]);
			}
			return counter;
		}
		
		private IsRGBColor[] getColorsArray() {
			IsRGBColor[] array = new RGBColor[data.size()];
			int idx = 0;
			for(IsRGBColor color : data.keySet()){
				array[idx] = color;
				idx++;
			}
			return array;
		}
		
	}
	
	private IsHistogram histogram;
	
	public RGBCube(IsHistogram histogram) {
		this.histogram = histogram;
	}
	
	public Collection<IsRGBColor> quantize(int level){
		return new Quantizer(histogram).quantize(level);
	}
		
}
