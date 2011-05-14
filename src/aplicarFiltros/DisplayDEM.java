package aplicarFiltros;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

import procesamiento.RgbHsv;

import com.sun.media.jai.widget.DisplayJAI;

public class DisplayDEM extends DisplayJAI implements MouseMotionListener {
	protected StringBuffer pixelInfo = new StringBuffer(50); // Pixel information (formatted as a
	// StringBuffer).
	protected double[] dpixel; // Pixel information as an array of doubles.
	protected RandomIter readIterator; // a RandomIter that allow us to get
	// the data of a single pixel.
	protected PlanarImage surrogateImage; // The surrogate byte image.
	protected int width, height; // Dimensions of the image
	protected double minValue, maxValue; // Range of the image values.
	
	public static final int NORMAL_MODE = 0;
	
	public static final int SELECTION_MODE = 1;
	
	private int modo = NORMAL_MODE;
	
	private boolean mousePressed = false;
	
	private int xClickOrigen;

	private int yClickOrigen;

	private int xClickDestino;

	private int yClickDestino;
	
	private Rectangle selectionRectangle = null;

	final static float dash1[] = {5.0f};
	
	final static BasicStroke dashed = new BasicStroke(1.0f, 
            BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_MITER,
            5.0f, dash1, 0.0f);

	/**
	 * The constructor of the class, which creates the data structures and
	 * surrogate image.
	 */
	public DisplayDEM(RenderedImage imageFront, RenderedImage imageBack) {
		display(imageFront, imageBack);
		pixelInfo = new StringBuffer(50);
		addMouseMotionListener(this); // Registers the mouse motion listener.
		addMouseListener(dragAndDrowListener());
	}

	public DisplayDEM() {
		super();
		pixelInfo = new StringBuffer(50);
		addMouseMotionListener(this); // Registers the mouse motion listener.
		addMouseListener(dragAndDrowListener());
	}

	private MouseListener dragAndDrowListener(){
		MouseListener listener = new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				mousePressed = true;
				xClickOrigen = evt.getX();
				yClickOrigen = evt.getY();
				xClickDestino = evt.getX();
				yClickDestino = evt.getY();
				
				if (modo == SELECTION_MODE){
					modo = NORMAL_MODE;
					selectionRectangle = null;
					repaint();
				}
					
			}

			public void mouseReleased(MouseEvent evt) {
				mousePressed = false;
				xClickDestino = evt.getX();
				yClickDestino = evt.getY();
			}

		};
		return listener;
	}
	
	public void display(RenderedImage imageFront, RenderedImage imageBack) {

		
		readIterator = RandomIterFactory.create(imageBack, null);
		// Get some facts about the image
		width = imageBack.getWidth();
		height = imageBack.getHeight();
		dpixel = new double[imageBack.getSampleModel().getNumBands()];
		surrogateImage = PlanarImage.wrapRenderedImage(imageBack);
		set(imageFront);
	}

	// This method is here just to satisfy the MouseMotionListener interface
	public void mouseDragged(MouseEvent evt) {
		this.setModo(SELECTION_MODE);
		if (mousePressed){
			xClickDestino = evt.getX();
			yClickDestino = evt.getY();
			repaint();
		}

	}
	
	public void mousePressed(MouseEvent evt) {
		mousePressed = true;
		xClickOrigen = evt.getX();
		yClickOrigen = evt.getY();
		xClickDestino = evt.getX();
		yClickDestino = evt.getY();
	}

	public void mouseReleased(MouseEvent evt) {
		mousePressed = false;
		xClickDestino = evt.getX();
		yClickDestino = evt.getY();
	}

	// This method will be called when the mouse is moved over the image.
	public void mouseMoved(MouseEvent me) {
		if (surrogateImage != null) {
			pixelInfo.setLength(0); // Clear the StringBuffer
			int x = me.getX(); // Get the mouse coordinates.
			int y = me.getY();
			if ((x >= width) || (y >= height)) // Avoid exceptions, consider
			// only
			{ // pixels within image bounds.
				pixelInfo.append("No data!");
				return;
			}
			pixelInfo.append("(INFO) " + x + "," + y + ": ");
			readIterator.getPixel(x, y, dpixel); // Read the original pixel
			// value.
			float hsv[] = RgbHsv.RGBtoHSV((int) dpixel[0], (int) dpixel[1],
					(int) dpixel[2]);
			pixelInfo.append("H: " + Math.round(hsv[0]) + " - S: "
					+ Math.round(hsv[1]) + " - V: " + Math.round(hsv[2])+ " | "); // Append
			pixelInfo.append("R: " + dpixel[0] + " - G: "
					+ dpixel[1] + " - B: " + dpixel[2]); // Append

			// to
			// the
			// StringBuffer
			// .
		}
	} // end of method mouseMoved

	
	// Allows other classes to access the pixel info string.
	public String getPixelInfo() {
		if (surrogateImage != null)
			return pixelInfo.toString();
		return null;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		if (modo == SELECTION_MODE){
			((Graphics2D) g).setStroke(dashed);
			((Graphics2D) g).setColor(Color.WHITE);
			selectionRectangle = new Rectangle(xClickOrigen,yClickOrigen,xClickDestino - xClickOrigen,yClickDestino - yClickOrigen);
			((Graphics2D) g).draw(selectionRectangle);
		}
	}

	public int getModo() {
		return modo;
	}

	public void setModo(int modo) {
		this.modo = modo;
	}

	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}
	
	public BufferedImage getSelectedRectangle() {
		if (getSelectionRectangle() != null){
			return surrogateImage.getAsBufferedImage(getSelectionRectangle(),null);
		}
		return null;
	}

	
}