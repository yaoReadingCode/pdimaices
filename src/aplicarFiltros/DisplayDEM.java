package aplicarFiltros;

import jai.histogram.DisplayHistogramApp;
import jai.histogram.DisplayHistogramComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import procesamiento.RgbHsv;

import com.sun.media.jai.widget.DisplayJAI;

public class DisplayDEM extends DisplayJAI implements MouseMotionListener {
	protected StringBuffer pixelInfo = new StringBuffer(50); // Pixel information (formatted as a
	// StringBuffer).
	protected double[] dpixel; // Pixel information as an array of doubles.
	protected RandomIter readIterator; // a RandomIter that allow us to get
	// the data of a single pixel.
	protected PlanarImage surrogateImage; // The surrogate byte image.
	protected PlanarImage frontImage; // The front byte image.
	protected Histogram histogramaImagen = null;
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
		addMouseListener(getMouseListener());
	}

	public DisplayDEM() {
		super();
		pixelInfo = new StringBuffer(50);
		addMouseMotionListener(this); // Registers the mouse motion listener.
		addMouseListener(getMouseListener());
	}

	private MouseListener getMouseListener(){
		MouseListener listener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 2){
					showPanelHistograma();
				}
			}

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
	
	private void showPanelHistograma() {
		if (histogramaImagen == null){
			calculateHistogram();
		}
		JFrame frame = new JFrame("Histograms");
		Container cp = frame.getContentPane();
		JScrollPane scrollpane = new JScrollPane();
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));
		double [] mean = histogramaImagen.getMean();
		double [] varianza = histogramaImagen.getStandardDeviation();
		if (histogramaImagen.getNumBands() == 3){
			DisplayHistogramComponent cRed = new DisplayHistogramComponent(histogramaImagen, 0,"Red");
			DisplayHistogramComponent cGreen = new DisplayHistogramComponent(histogramaImagen, 1,"Green");
			DisplayHistogramComponent cBlue = new DisplayHistogramComponent(histogramaImagen, 2,"Blue");
			cRed.setBarColor(Color.RED);
			cRed.setMarksColor(Color.WHITE);
			cGreen.setBarColor(Color.GREEN);
			cGreen.setMarksColor(Color.WHITE);
			cBlue.setBarColor(Color.BLUE);
			cBlue.setMarksColor(Color.WHITE);
			// Use the same scale on the y-axis.
			int max = Math.max(cRed.getMaxCount(), Math.max(cGreen.getMaxCount(),
					cBlue.getMaxCount()));
			cRed.setMaxCount(max);
			cGreen.setMaxCount(max);
			cBlue.setMaxCount(max);
			panel.add(cRed);
			panel.add(cGreen);
			panel.add(cBlue);
		}
		else{
			DisplayHistogramComponent cGray = new DisplayHistogramComponent(histogramaImagen, 0,"");
			cGray.setBarColor(Color.GRAY);
			cGray.setMarksColor(Color.WHITE);
			panel.add(cGray);
		}
		scrollpane.setViewportView(panel);
		cp.add(scrollpane);
		// Set the closing operation so the application is finished.
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack(); // Adjust the frame size using preferred dimensions.
		frame.setVisible(true); // Show the frame.
	}
	
	private void calculateHistogram(){
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(frontImage);
		pb.add(null); // The ROI.
		pb.add(1); // Samplings.
		pb.add(1);
		pb.add(new int[] { 256 }); // Num. bins.
		pb.add(new double[] { 0 }); // Min value to be considered.
		pb.add(new double[] { 256 }); // Max value to be considered.
		// Creates the histogram.
		PlanarImage temp = JAI.create("histogram", pb);
		histogramaImagen = (Histogram) temp.getProperty("histogram");
	}
	public void display(RenderedImage imageFront, RenderedImage imageBack) {
		readIterator = RandomIterFactory.create(imageBack, null);
		// Get some facts about the image
		width = imageBack.getWidth();
		height = imageBack.getHeight();
		dpixel = new double[imageBack.getSampleModel().getNumBands()];
		surrogateImage = PlanarImage.wrapRenderedImage(imageBack);
		frontImage = PlanarImage.wrapRenderedImage(imageFront);
		set(imageFront);
		histogramaImagen = null;
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