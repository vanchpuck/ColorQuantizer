package com.jonnygold.quantizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public class Quantizer {

	private static final Comparator<RGBColor> RED_COMPARATOR = new Comparator<RGBColor>() {
		@Override
		public int compare(RGBColor color1, RGBColor color2) {
			return Integer.compare(color1.getRed(), color2.getRed());
		}
	};
	
	private static final Comparator<RGBColor> GREEN_COMPARATOR = new Comparator<RGBColor>() {
		@Override
		public int compare(RGBColor color1, RGBColor color2) {
			return Integer.compare(color1.getGreen(), color2.getGreen());
		}
	};
	
	private static final Comparator<RGBColor> BLUE_COMPARATOR = new Comparator<RGBColor>() {
		@Override
		public int compare(RGBColor color1, RGBColor color2) {
			return Integer.compare(color1.getBlue(), color2.getBlue());
		}
	};
	
	private static enum Axis {
		RED {
			@Override
			public int getQuantizationBound() {
				return 60;
			}

			@Override
			public Comparator<RGBColor> getComparator() {
				return RED_COMPARATOR;
			}

		},
		GREEN {
			@Override
			public int getQuantizationBound() {
				return 60;
			}

			@Override
			public Comparator<RGBColor> getComparator() {
				return GREEN_COMPARATOR;
			}
		},
		BLUE {
			@Override
			public int getQuantizationBound() {
				return 90;
			}

			@Override
			public Comparator<RGBColor> getComparator() {
				return BLUE_COMPARATOR;
			}
		};
		
		public abstract int getQuantizationBound();
		
		public abstract Comparator<RGBColor> getComparator();
	}
	
	private static class CubeAxis {
		
		private Axis axis;
		private int length;
		
		public CubeAxis(Axis axis, int length){
			this.axis = axis;
			this.length = length;
		}
		
		public Comparator<RGBColor> getComparator() {
			return axis.getComparator();
		}
		
		public boolean isQuantized() {
			return (length > axis.getQuantizationBound());
		}
	}
	
	private class RGBCube {
		
		private RGBColor[] data;
		private int from;
		private int to;
		private CubeAxis cubeAxis;
		
		public RGBCube(RGBColor[] colors, int from, int to) {
			this.data = colors;
			this.from = from;
			this.to = to;
			this.cubeAxis = getCubeAxis();
			sortData();
		}
		
		public RGBColor getCentroid() {
			double colorsCount = getColorsCount();
			double rCenter = 0, gCenter = 0, bCenter = 0;
			for(int i=from; i<to; i++){
				rCenter += histogramData.get(data[i])/colorsCount*data[i].getRed();
				gCenter += histogramData.get(data[i])/colorsCount*data[i].getGreen();
				bCenter += histogramData.get(data[i])/colorsCount*data[i].getBlue();
			}
			return new RGBColor((int)rCenter, (int)gCenter, (int)bCenter);
		}
		
		public boolean isQuantized() {
			return cubeAxis.isQuantized();
		}
		
		public int findSplitIdx() {
			int splitIdx = -1;
			switch (cubeAxis.axis) {
			case RED:
				splitIdx = findRedSplitIdx();
				break;
			case GREEN:
				splitIdx = findGreenSplitIdx();
				break;
			default:
				splitIdx = findBlueSplitIdx();
				break;
			}
			if(splitIdx == -1){
				return findMedianIdx();
			}
			return splitIdx;
		}
		
		private int findRedSplitIdx(){
			int maxGulf = data[from+1].getRed() - data[from].getRed();
			int currGulf = 0;
			int idx = from+1;
			for(int i=from+2; i<to; i++){
				currGulf = data[i].getRed() - data[i-1].getRed();
				if(currGulf > maxGulf){
					maxGulf = currGulf;
					idx = i;
				}
			}
			if(!isDeepEnough(maxGulf)){
				return -1;
			}
			return idx;
		}
		
		private int findGreenSplitIdx(){
			int maxGulf = data[from+1].getGreen() - data[from].getGreen();
			int currGulf = 0;
			int idx = from+1;
			for(int i=from+2; i<to; i++){
				currGulf = data[i].getGreen() - data[i-1].getGreen();
				if(currGulf > maxGulf){
					maxGulf = currGulf;
					idx = i;
				}
			}
			if(!isDeepEnough(maxGulf)){
				return -1;
			}
			return idx;
		}
		
		private int findBlueSplitIdx(){
			int maxGulf = data[from+1].getBlue() - data[from].getBlue();
			int currGulf = 0;
			int idx = from+1;
			for(int i=from+2; i<to; i++){
				currGulf = data[i].getBlue() - data[i-1].getBlue();
				if(currGulf > maxGulf){
					maxGulf = currGulf;
					idx = i;
				}
			}
			if(!isDeepEnough(maxGulf)){
				return -1;
			}
			return idx;
		}
		
		private boolean isDeepEnough(int gulf){
			return (double)gulf/(double)cubeAxis.length > 0.1;
		}
		
		private int findMedianIdx(){
			int totalSum = 0;
			int medianIdx = from;
			
			for(int i=from; i<to; i++){
				totalSum+=histogramData.get(data[i]);
			}
			for(int i=from, sum=0; i<to && sum<(totalSum>>1); i++){
				sum+=histogramData.get(data[i]);
				medianIdx = i;
			}
			if(medianIdx == to){
				return medianIdx;
			} 
			return medianIdx+1;
		}
		
		private void sortData(){
			Arrays.sort(data, from, to, cubeAxis.getComparator());
		}
		
		public int getColorsCount(){
			int counter = 0;
			for(int i=from; i<to; i++){
				counter += histogramData.get(data[i]);
			}
			return counter;
		}
		
		private CubeAxis getCubeAxis(){
			int currR, currG, currB;
			int minR, maxR, minG, maxG, minB, maxB;
			minR = maxR = data[from].getRed();
			minG = maxG = data[from].getGreen();
			minB = maxB = data[from].getBlue();
			for(int i=from+1; i<to; i++) {
				currR = data[i].getRed();
				currG = data[i].getGreen();
				currB = data[i].getBlue();
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
			int rDim = maxR - minR;
			int gDim = maxG - minG;
			int bDim = maxB - minB;
			if(rDim >= gDim && rDim >= bDim) {
				return new CubeAxis(Axis.RED, rDim);
			}
			else if(gDim >= rDim && rDim >= bDim) {
				return new CubeAxis(Axis.GREEN, gDim);
			}
			else {
				return new CubeAxis(Axis.BLUE, bDim);
			}
		}
		
	}
	
	private Map<RGBColor, Integer> histogramData;
//	
//	private RGBColor[] colors;
	
	public IsHistogram quantize(IsHistogram histogram, int level){
		
		histogramData = histogram.getData();
		
		return quantize(new Histogram.Builder(), getColorsArray(histogramData), 0, histogramData.size(), 0, level).build();
				
	}
	
	private Histogram.Builder quantize(Histogram.Builder builder, RGBColor[] colors, int from, int to, int currLevel, int maxLevel){
		if(from == to){
			return builder;
		}
		
		RGBCube cube = new RGBCube(colors, from, to);
		
		if(currLevel >= maxLevel || to-from==1 || !cube.isQuantized()){
			builder.addColor(cube.getCentroid(), cube.getColorsCount());
			return builder;
		}
		
		int splitIdx = cube.findSplitIdx();
		
		quantize(builder, colors, from, splitIdx, currLevel+1, maxLevel);
		quantize(builder, colors, splitIdx, to, currLevel+1, maxLevel);
		
		return builder;
	}
	
	private RGBColor[] getColorsArray(Map<RGBColor, Integer> data) {
		RGBColor[] array = new RGBColor[data.size()];
		int idx = 0;
		for(RGBColor color : data.keySet()){
			array[idx] = color;
			idx++;
		}
		return array;
	}
	
}
