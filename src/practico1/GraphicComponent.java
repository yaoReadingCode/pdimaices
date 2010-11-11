package practico1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class GraphicComponent extends JPanel {
	private Image imgInv;
	private Graphics graphInv;
	private Dimension dimInv;
	private int height = 128;
	private int width = 256;
	private int nivelesGris = 256;

	 
	public void paint(Graphics graphic) {
		//Dimension dim = getSize();
		this.invalidate();
		int width = getWidth();
		int height = getHeight();
		if ((graphInv == null || (width != dimInv.width) || (height != dimInv.height))) {
			dimInv = new Dimension(width,height);
			//setSize(width, height);
			imgInv = createImage(width, height);
			graphInv = imgInv.getGraphics();
		}
	
		int paso = 0;
		int anchoFranja = width / getNivelesGris();
		for (int i = 0; i < width; i++) {
			if (i != 0 && anchoFranja != 0 && i % anchoFranja == 0)
				paso++;
			int c = 0;
			if (paso != 0)
				c = (paso +1) * anchoFranja;
			c = 256 * c / width;
			if (c > 255)
				c= 255;
			for (int j = 0; j < getHeight(); j++) {
				graphInv.setColor(new Color(c, c, c));
				graphInv.drawLine(i, j, i, j);
			}
		}
		//graphic.setColor(Color.WHITE);
		//graphic.fillRect(0,0, 1024, height);
		graphic.drawImage(imgInv, 0, 0, this);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		setSize(width, height);
	}

	public int getNivelesGris() {
		return nivelesGris;
	}

	public void setNivelesGris(int nivelesGris) {
		this.nivelesGris = nivelesGris;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		setSize(width, height);
	}
	
}
