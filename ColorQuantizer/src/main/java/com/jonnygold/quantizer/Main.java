package com.jonnygold.quantizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.swing.JFrame;

public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8903441595017639323L;

	public Main() throws IOException{
		super("Quantizer");
		
		setSize(new Dimension(400, 400));		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setLayout(new FlowLayout(FlowLayout.CENTER));
		
		RGBCube c = getCube();
		
		Collection<RGBColor> colors = c.quantize(2);
		for(RGBColor color : colors){
			add(new ImagePanel(new FlowLayout(FlowLayout.CENTER), new Color(color.getRed(), color.getGreen(), color.getBlue())));
		}
		
//		add(new ImagePanel(new FlowLayout(FlowLayout.CENTER), Color.GREEN));
//		add(new ImagePanel(new FlowLayout(FlowLayout.CENTER), Color.BLUE));
		
		setVisible(true);
	}
	
	private RGBCube getCube() throws IOException{
		BufferedImage img = ImageIO.read(new File("/home/izolotov/Downloads/Calendar/belirussia.jpg"));
		
		Histogram.Builder b = new Histogram.Builder();
		
		int[] p = new int[3];
		
		for(int y=0; y<img.getHeight(); y++){
			for(int x=0; x<img.getWidth(); x++){
				p = img.getRaster().getPixel(y, x, p);
				b.addColor(new RGBColor(p[0], p[1], p[2]));
			}
		}
		
		return new RGBCube(b.build());
	}
	
	public static void main(String[] args) throws IOException {
		new Main();
	}
	
}
