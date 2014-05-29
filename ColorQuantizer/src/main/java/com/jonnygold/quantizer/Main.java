package com.jonnygold.quantizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.swing.JFrame;

public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8903441595017639323L;

	private static Comparator<Map.Entry<RGBColor, Integer>> compar = new Comparator<Map.Entry<RGBColor,Integer>>() {

		@Override
		public int compare(Entry<RGBColor, Integer> o1, Entry<RGBColor, Integer> o2) {
			return -Integer.compare(o1.getValue(), o2.getValue());
		}
		
	};
	
	public Main() throws IOException{
		super("Quantizer");
		
		setSize(new Dimension(400, 400));		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setLayout(new FlowLayout(FlowLayout.CENTER));
		
		Quantizer q = new Quantizer();
		
		
		IsHistogram hist = q.quantize(getHist(), 3);
		
		TreeSet<Map.Entry<RGBColor, Integer>> set = new TreeSet<Map.Entry<RGBColor, Integer>>(compar);
		set.addAll( hist.getData().entrySet());
		
		int idx = 0;
		for(Map.Entry<RGBColor, Integer> bar : set){
			if(idx > 5){
				break;
			}
			add(new ImagePanel(new FlowLayout(FlowLayout.CENTER), new Color(bar.getKey().getRed(), bar.getKey().getGreen(), bar.getKey().getBlue())));
			idx++;
		}
		
//		add(new ImagePanel(new FlowLayout(FlowLayout.CENTER), Color.GREEN));
//		add(new ImagePanel(new FlowLayout(FlowLayout.CENTER), Color.BLUE));
		
		setVisible(true);
	}
	
	private IsHistogram getHist() throws IOException{
//		BufferedImage img = ImageIO.read(new File("/home/izolotov/Downloads/На календарь/kaz.jpg"));
//		BufferedImage img = ImageIO.read(new File("/home/izolotov/Downloads/Calendar/ukrane.jpg"));
		BufferedImage img = ImageIO.read(new File("/home/izolotov/Desktop/index.jpg"));
		
		Histogram.Builder b = new Histogram.Builder();
		
		int[] p = new int[3];
		
		for(int x=0; x<img.getWidth(); x++){
			for(int y=0; y<img.getHeight(); y++){
				p = img.getRaster().getPixel(x, y, p);
				b.addColor(new RGBColor(p[0], p[1], p[2]));
			}
		}
		return b.build(0.0001);
	}
	
	public static void main(String[] args) throws IOException {
		new Main();
	}
	
}
