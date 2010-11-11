package jai.histogram;

/*
 * Part of the Java Image Processing Cookbook, please see
 * http://www.lac.inpe.br/~rafael.santos/JIPCookbook/index.jsp
 * for information on usage and distribution.
 * Rafael Santos (rafael.santos@lac.inpe.br)
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.media.jai.Histogram;
import javax.swing.JComponent;

/**
 * This class displays a histogram (instance of Histogram) as a component. Only
 * the first histogram band is considered for plotting. The component has a
 * tooltip which displays the bin index and bin count for the bin under the
 * mouse cursor.
 */
public class DisplayHistogramComponent extends JComponent implements
		MouseMotionListener {
	// The histogram and its title.
	private Histogram histogram;
	private String title;
	private int band;
	// Some data and hints for the histogram plot.
	private int[] counts;
	private int maxCount;
	private int indexMultiplier = 1;
	private int skipIndexes = 8;
	// The components' dimensions.
	private int width, height = 250;
	// Some constants for this component. Some may be changed through set
	// methods.
	private int verticalTicks = 10;
	private Insets border = new Insets(40, 70, 40, 30);
	private int binWidth = 3;
	private Font fontSmall = new Font("monospaced", 0, 10);
	private Font fontLarge = new Font("default", Font.ITALIC, 20);
	private Color backgroundColor = Color.BLACK;
	private Color barColor;
	private Color marksColor;

	/**
	 * The constructor for this class, which will set its fields' values and get
	 * some information about the histogram.
	 * 
	 * @param histogram
	 *            the histogram to be plotted.
	 * @param band
	 *            which band of the histogram will be plotted.
	 * @param title
	 *            the title of the plot.
	 */
	public DisplayHistogramComponent(Histogram histogram, int band, String title) {
		this.histogram = histogram;
		this.title = title;
		this.band = band;
		// Use default colors.
		backgroundColor = Color.BLACK;
		barColor = new Color(255, 255, 200);
		marksColor = new Color(100, 180, 255);
		// Calculate the components dimensions.
		width = histogram.getNumBins(band) * binWidth;
		// Get the histogram data.
		counts = histogram.getBins(band);
		// Get the max and min counts.
		maxCount = Integer.MIN_VALUE;
		for (int c = 0; c < counts.length; c++)
			maxCount = Math.max(maxCount, counts[c]);
		addMouseMotionListener(this);
	}

	/**
	 * Override the default bin width (for plotting)
	 */
	public void setBinWidth(int newWidth) {
		binWidth = newWidth;
		width = histogram.getNumBins(band) * binWidth;
	}

	/**
	 * Override the default height for the plot.
	 * 
	 * @param h
	 *            the new height.
	 */
	public void setHeight(int h) {
		height = h;
	}

	/**
	 * Override the index multiplying factor (for bins with width != 1)
	 */
	public void setIndexMultiplier(int i) {
		indexMultiplier = i;
	}

	/**
	 * Override the index skipping factor (determines how many labels will be
	 * printed on the index axis).
	 */
	public void setSkipIndexes(int i) {
		skipIndexes = i;
	}

	/**
	 * Set the maximum value (used to scale the histogram y-axis). The default
	 * value is defined in the constructor and can be overriden with this
	 * method.
	 */
	public void setMaxCount(int m) {
		maxCount = m;
	}

	/**
	 * Returns the max count for this histogram.
	 */
	public int getMaxCount() {
		return maxCount;
	}

	/**
	 * This method informs the maximum size of this component, which will be the
	 * same as the preferred size.
	 */
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	/**
	 * This method informs the minimum size of this component, which will be the
	 * same as the preferred size.
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * This method informs the preferred size of this component, which will be
	 * constant.
	 */
	public Dimension getPreferredSize() {
		return new Dimension(width + border.left + border.right, height
				+ border.top + border.bottom);
	}

	/**
	 * Sets the components' background color.
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Sets the components' bars color.
	 */
	public void setBarColor(Color barColor) {
		this.barColor = barColor;
	}

	/**
	 * Sets the components' marks color.
	 */
	public void setMarksColor(Color marksColor) {
		this.marksColor = marksColor;
	}

	/**
	 * This method will paint the component.
	 */
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// Draw the background.
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0, getSize().width, getSize().height);
		// Draw some marks.
		g2d.setColor(marksColor);
		g2d.drawRect(border.left, border.top, width, height);
		// Draw the histogram bars.
		g2d.setColor(barColor);
		for (int bin = 0; bin < histogram.getNumBins(band); bin++) {
			int x = border.left + bin * binWidth;
			double barStarts = border.top + height * (maxCount - counts[bin])
					/ (1. * maxCount);
			double barEnds = Math.ceil(height * counts[bin] / (1. * maxCount));
			g2d.drawRect(x, (int) barStarts, binWidth, (int) barEnds);
		}
		// Draw the values on the horizontal axis. We will plot only 1/8th of
		// them.
		g2d.setColor(marksColor);
		g2d.setFont(fontSmall);
		FontMetrics metrics = g2d.getFontMetrics();
		int halfFontHeight = metrics.getHeight() / 2;
		for (int bin = 0; bin <= histogram.getNumBins(band); bin++) {
			if (bin % skipIndexes == 0) {
				String label = "" + (indexMultiplier * bin);
				int textHeight = metrics.stringWidth(label); // remember it will
																// be rotated!
				int x = border.left + bin * binWidth + binWidth / 2;
				g2d.translate(border.left + bin * binWidth + halfFontHeight,
						border.top + height + textHeight + 2);
				g2d.rotate(-Math.PI / 2);
				g2d.drawString(label, 0, 0);
				g2d.rotate(Math.PI / 2);
				g2d.translate(-(border.left + bin * binWidth + halfFontHeight),
						-(border.top + height + textHeight + 2));
			}
		}
		// Draw the values on the vertical axis. Let's draw only some of them.
		double step = (int) (maxCount / verticalTicks);
		for (int l = 0; l <= verticalTicks; l++) // last will be done separately
		{
			String label;
			if (l == verticalTicks)
				label = String.format("%7d", maxCount);
			else
				label = String.format("%7d", (int) (l * step));
			int textWidth = metrics.stringWidth(label);
			g2d.drawString(label, border.left - 2 - textWidth, border.top
					+ height - l * (height / verticalTicks));
		}
		// Draw the title.
		g2d.setColor(barColor);
		g2d.setFont(fontLarge);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		metrics = g2d.getFontMetrics();
		int textWidth = metrics.stringWidth(title);
		g2d.drawString(title,
				(border.left + width + border.right - textWidth) / 2, 28);
	}

	/**
	 * This method does not do anything, it is here to keep the
	 * MouseMotionListener interface happy.
	 */
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 * This method will be called when the mouse is moved over the component. It
	 * will set the tooltip text on the component to show the histogram data.
	 */
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		// Don't show anything out of the plot region.
		if ((x > border.left) && (x < border.left + width) && (y > border.top)
				&& (y < border.top + height)) {
			// Convert the X to an index on the histogram.
			x = (x - border.left) / binWidth;
			y = counts[x];
			setToolTipText((indexMultiplier * x) + ": " + y);
		} else {
			setToolTipText(null);
		}
	}

}
