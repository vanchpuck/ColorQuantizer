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
		
		Quantizer q = new Quantizer();
		
		
		Collection<RGBColor> colors = q.quantize(getHist(), 3);
		for(IsRGBColor color : colors){
			add(new ImagePanel(new FlowLayout(FlowLayout.CENTER), new Color(color.getRed(), color.getGreen(), color.getBlue())));
		}
		
//		add(new ImagePanel(new FlowLayout(FlowLayout.CENTER), Color.GREEN));
//		add(new ImagePanel(new FlowLayout(FlowLayout.CENTER), Color.BLUE));
		
		setVisible(true);
	}
	
	private IsHistogram getHist() throws IOException{
//		BufferedImage img = ImageIO.read(new File("/home/izolotov/Downloads/На календарь/kaz.jpg"));
//		BufferedImage img = ImageIO.read(new File("/home/izolotov/Downloads/Calendar/ukrane.jpg"));
		BufferedImage img = ImageIO.read(new File("/home/izolotov/Desktop/rus.jpg"));
		
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
