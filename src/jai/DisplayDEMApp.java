package jai;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import procesamiento.Binarizar;
import procesamiento.HSVRange;

public class DisplayDEMApp extends JFrame implements MouseMotionListener {
	private DisplayDEM dd; // An instance of the DisplayDEM component.
	private JLabel label; // Label to display information about the image.

	public DisplayDEMApp(PlanarImage imageFront, PlanarImage imageBack) {
		setTitle("Move the mouse over the image !");
		getContentPane().setLayout(new BorderLayout());
		dd = new DisplayDEM(imageFront, imageBack); // Create the component.
		getContentPane().add(new JScrollPane(dd), BorderLayout.CENTER);
		label = new JLabel("---"); // Create the label.
		getContentPane().add(label, BorderLayout.SOUTH);
		dd.addMouseMotionListener(this); // Register mouse events.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		// setSize(400, 200);
		setVisible(true);
	}

	// This method is here just to satisfy the MouseMotionListener interface.
	public void mouseDragged(MouseEvent e) {
	}

	// This method will be executed when the mouse is moved over the
	// application.
	public void mouseMoved(MouseEvent e) {
		label.setText(dd.getPixelInfo()); // Update the label with the
		// DisplayDEM instance info.
	}

	public static void main(String[] args) {
		PlanarImage imageBack = JAI.create("fileload", args[0]);
		HSVRange range = new HSVRange();		
		range.setHMin(90f);
		range.setHMax(350f);
		range.setSMin(3f);
		Binarizar e = new Binarizar(imageBack ,range);
		PlanarImage imageFront = e.execute();
		new DisplayDEMApp(imageFront, imageBack);
		//JAI.create("filestore", imageFront, "imagenSinfondo.tif", "TIFF");
	}
}