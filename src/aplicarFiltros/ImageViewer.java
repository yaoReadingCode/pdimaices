package aplicarFiltros;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImageViewer extends JPanel {
	private Image img;

	public Image getImg() {
		return img;
	}

	public void setImg(Image img) {
		this.img = img;
	}

	public ImageViewer() {
		super();
	}

	 
	public void paint(Graphics graphic) {
		graphic.drawImage(createImage(getWidth(), getHeight()),0,0, this);
		if (getImg() != null){
			this.setSize(getImg().getWidth(this),getImg().getHeight(this));
			this.invalidate();
			graphic.drawImage(getImg(), 0, 0, this);
		}

	}

}
