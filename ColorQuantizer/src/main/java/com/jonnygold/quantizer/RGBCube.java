package com.jonnygold.quantizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public class RGBCube {
		
	private static class Quantizer {
		
		private Comparator<RGBColor> rComparator = new Comparator<RGBColor>() {
			@Override
			public int compare(RGBColor color1, RGBColor color2) {
				return Integer.compare(color1.getRed(), color2.getRed());
			}
		};
		
		private Comparator<RGBColor> gComparator = new Comparator<RGBColor>() {
			@Override
			public int compare(RGBColor color1, RGBColor color2) {
				return Integer.compare(color1.getGreen(), color2.getGreen());
			}
		};
		
		private Comparator<RGBColor> bComparator = new Comparator<RGBColor>() {
			@Override
			public int compare(RGBColor color1, RGBColor color2) {
				return Integer.compare(color1.getBlue(), color2.getBlue());
			}
		};
		
		Map<RGBColor, Integer> data;
		
		private Collection<RGBColor> mainColors = new ArrayList<RGBColor>();
		
		Quantizer(IsHistogram histogram){
			data = histogram.getData();
		}
		
		public Collection<RGBColor> quantize(int level){
			RGBColor[] colors = getColorsArray();
			quantize(colors, 0, colors.length, 0, level);
			return mainColors;
		}
		
		private void quantize(RGBColor[] colors, int from, int to, int currLevel, int maxLevel){
			if(from == to){
				//throw new IllegalArgumentException("Невозможно квантовать куб нулевой размерности.");
				return;
			}
			if(currLevel >= maxLevel || to-from==1){
				mainColors.add(getCenter(colors, from, to));
				return;
			}
			Arrays.sort(colors, from, to, getComparator(colors, from, to));
			int medianIdx = findMedianIdx(colors, from, to);
			quantize(colors, from, medianIdx, currLevel+1, maxLevel);
			quantize(colors, medianIdx, to, currLevel+1, maxLevel);
		}
		
		private int findMedianIdx(RGBColor[] colors, int from, int to){
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
		
		private int[] getCubeBorders(RGBColor[] colors, int from, int to){
			int[] borders = new int[6];
			int currR, currG, currB;
			borders[0] = borders[1] = colors[from].getRed();
			borders[2] = borders[3] = colors[from].getGreen();
			borders[4] = borders[5] = colors[from].getBlue();
			for(int i=from+1; i<to; i++) {
				currR = colors[i].getRed();
				currG = colors[i].getGreen();
				currB = colors[i].getBlue();
				if(currR < borders[0]){
					borders[0] = currR;
				} else if(currR > borders[1]) {
					borders[1] = currR;
				}
				if(currG < borders[2]){
					borders[2] = currG;
				} else if(currG > borders[3]) {
					borders[3] = currG;
				}
				if(currB < borders[4]){
					borders[4] = currB;
				} else if(currB > borders[5]) {
					borders[5] = currB;
				}
			}
			return borders;
		}
		
		private Comparator<RGBColor> getComparator(RGBColor[] colors, int from, int to){
			int[] borders = getCubeBorders(colors, from, to);
			int rLen = borders[1] - borders[0];
			int gLen = borders[3] - borders[2];
			int bLen = borders[5] - borders[4];
			if(rLen >= gLen && rLen >= bLen)
				return rComparator;
			else if(gLen >= rLen && rLen >= bLen) 
				return gComparator;
			else 
				return bComparator;
		}
		
		private RGBColor getCenter(RGBColor[] colors, int from, int to){
			double colorsCount = getColorsCount(colors, from, to);
			double rCenter = 0, gCenter = 0, bCenter = 0;
			for(int i=from; i<to; i++){
				int c = data.get(colors[i]);
				int r = colors[i].getGreen();
				double k = data.get(colors[i])/colorsCount;
				rCenter += data.get(colors[i])/colorsCount*colors[i].getRed();
				gCenter += data.get(colors[i])/colorsCount*colors[i].getGreen();
				bCenter += data.get(colors[i])/colorsCount*colors[i].getBlue();
			}
			return new RGBColor((int)rCenter, (int)gCenter, (int)bCenter);
		}
		
		private int getColorsCount(RGBColor[] colors, int from, int to){
			int counter = 0;
			for(int i=from; i<to; i++){
				counter += data.get(colors[i]);
			}
			return counter;
		}
		
		private RGBColor[] getColorsArray() {
			RGBColor[] array = new RGBColor[data.size()];
			int idx = 0;
			for(RGBColor color : data.keySet()){
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
	
	public Collection<RGBColor> quantize(int level){
		return new Quantizer(histogram).quantize(level);
	}
		
}
