package com.jonnygold.quantizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1184400540702413368L;

	public ImagePanel(Color color){
		super();
		setBackground(color);
		
		setPreferredSize(new Dimension(100,100));
	}
	
	public ImagePanel(LayoutManager layout, Color color){
		super(layout);
		setBackground(color);
		setPreferredSize(new Dimension(100,100));
	}
	
}
