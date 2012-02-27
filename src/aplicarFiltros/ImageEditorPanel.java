/*
 * Created by JFormDesigner on Fri Jun 26 21:28:07 GMT 2009
 */

package aplicarFiltros;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TileCache;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import procesamiento.IImageProcessing;
import procesamiento.ImageComand;
import procesamiento.ImageUtil;
import procesamiento.clasificacion.Clasificador;
import aplicarFiltros.configuracion.AdminPanel;
import aplicarFiltros.configuracion.GeneralTableModel;
import aplicarFiltros.configuracion.modelmapper.ClaseMapper;
import aplicarFiltros.configuracion.modelmapper.RasgoMapper;

import components.ImageFileView;
import components.ImageFilter;
import components.ImagePreview;

/**
 * @author User #3
 */
public class ImageEditorPanel extends JPanel implements IImageProcessing,
		MouseMotionListener {

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static private String newline = "\n";
	private JFileChooser fc;
	private PlanarImage inputImage = null;
	private PlanarImage modifiedImage = null;
	private DisplayDEM dd;
	private List<ImageComand> executedCommands = new ArrayList<ImageComand>();
	private OperacionesPanel panelEliminarFondo;
	
	private Clasificador clasificador;
	
	private static SimpleDateFormat formater=new SimpleDateFormat("(HH:mm:ss) ");
	
	

	public ImageEditorPanel() {
		try{
			clasificador = new Clasificador();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		initComponents();
	}


	public void updateImage() {
		if (modifiedImage != null)
			dd.display(modifiedImage, inputImage);
		// labelImage.setIcon(new ImageIcon(modifiedImage));
	}

	private void abrirMenuItemActionPerformed(ActionEvent e) {
		// Set up the file chooser.
		if (fc == null) {
			fc = new JFileChooser();

			// Add a custom file filter and disable the default
			// (Accept All) file filter.
			fc.addChoosableFileFilter(new ImageFilter());
			fc.setAcceptAllFileFilterUsed(false);

			// Add custom icons for file types.
			fc.setFileView(new ImageFileView());

			// Add the preview pane.
			fc.setAccessory(new ImagePreview(fc));
		}

		// Show it.
		int returnVal = fc.showDialog(ImageEditorPanel.this, "Cargar imagen");

		// Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			logPantalla("Attaching file: " + file.getName() + "." );


			try {
				inputImage = ImageUtil.loadImage(file.getAbsolutePath(), ImageUtil.tileWidth, ImageUtil.tileHeight);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			//inputImage = new TiledImage(inputImage, false);
			inputImage = ImageUtil.reformatImage(inputImage, new Dimension(ImageUtil.tileWidth, ImageUtil.tileHeight));
			setModifiedImage(inputImage);
			updateImage();
			// labelImage.invalidate();
			// labelImage.repaint();
		} else {
			logPantalla("Carga de imagen cancelada por el usuario.");
		}
		log.setCaretPosition(log.getDocument().getLength());

		// Reset the file chooser for the next time it's shown.
		fc.setSelectedFile(null);

	}

	private void guardarMenuItemActionPerformed(ActionEvent e) {
		// Set up the file chooser.
		if (fc == null) {
			fc = new JFileChooser();

			// Add a custom file filter and disable the default
			// (Accept All) file filter.
			fc.addChoosableFileFilter(new ImageFilter());
			fc.setAcceptAllFileFilterUsed(false);

			// Add custom icons for file types.
			fc.setFileView(new ImageFileView());

			// Add the preview pane.
			fc.setAccessory(new ImagePreview(fc));
		}

		// Show it.
		int returnVal = fc.showDialog(ImageEditorPanel.this, "Guardar imagen");

		// Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			int indexType = path.lastIndexOf(".");
			String newPath = path.substring(0, indexType) + ".tif";
			if (modifiedImage != null) {

				JAI.create("filestore", modifiedImage, newPath, "TIFF");
			} else if (inputImage != null) {
				JAI.create("filestore", inputImage, newPath, "TIFF");
			}
		}
		fc.setSelectedFile(null);
	}

	private void menuItemDeshacerActionPerformed(ActionEvent e) {
		int commandSize = getExecutedCommands().size();
		if (commandSize > 0) {
			ImageComand command = getExecutedCommands().get(commandSize - 1);
			getExecutedCommands().remove(commandSize - 1);
			PlanarImage image = command.undo();
			setModifiedImage(image);
			updateImage();
			logPantalla("Se deshizo el commando: " + command.getCommandName());
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		menuBar1 = new JMenuBar();
		archvoMenu = new JMenu();
		abrirMenuItem = new JMenuItem();
		guardarMenuItem = new JMenuItem();
		menuEdicion = new JMenu();
		menuItemDeshacer = new JMenuItem();
		panel2 = new JPanel();
		panel1 = new JPanel();
		panel4 = new JPanel();
		scrollPane1 = new JScrollPane();
		log = new JTextArea();
		inferior = new JTextArea();

		//======== this ========
		setBackground(new Color(204, 204, 204));
		setLayout(new BorderLayout());

		//======== menuBar1 ========
		{

			//======== archvoMenu ========
			{
				archvoMenu.setText("Archivo");

				//---- abrirMenuItem ----
				abrirMenuItem.setText("Abrir imagen");
				abrirMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						abrirMenuItemActionPerformed(e);
					}
				});
				archvoMenu.add(abrirMenuItem);

				//---- guardarMenuItem ----
				guardarMenuItem.setText("Guardar");
				guardarMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						guardarMenuItemActionPerformed(e);
					}
				});
				archvoMenu.add(guardarMenuItem);
			}
			menuBar1.add(archvoMenu);

			//======== menuEdicion ========
			{
				menuEdicion.setText("Edici\u00f3n");

				//---- menuItemDeshacer ----
				menuItemDeshacer.setText("Deshacer");
				menuItemDeshacer.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						menuItemDeshacerActionPerformed(e);
					}
				});
				menuEdicion.add(menuItemDeshacer);
			}
			menuBar1.add(menuEdicion);
			menuBar1.add(getConfiguracionMenu());
		}
		add(menuBar1, BorderLayout.NORTH);

		//======== panel2 ========
		{
			panel2.setLayout(new TableLayout(new double[][] {
				{TableLayout.PREFERRED},
				{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));
			((TableLayout)panel2.getLayout()).setHGap(5);
			((TableLayout)panel2.getLayout()).setVGap(5);
		}
		add(panel2, BorderLayout.EAST);

		//======== panel1 ========
		{
			panel1.setLayout(null);

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for(int i = 0; i < panel1.getComponentCount(); i++) {
					Rectangle bounds = panel1.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				Insets insets = panel1.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				panel1.setMinimumSize(preferredSize);
				panel1.setPreferredSize(preferredSize);
			}
		}
		add(panel1, BorderLayout.WEST);

		//======== panel4 ========
		{
			panel4.setLayout(new BorderLayout());

			//======== scrollPane1 ========
			{

				//---- log ----
				log.setRows(4);
				log.setEditable(false);
				scrollPane1.setViewportView(log);
			}
			panel4.add(scrollPane1, BorderLayout.CENTER);

			//---- inferior ----
			inferior.setRows(2);
			inferior.setEditable(false);
			inferior.setBackground(SystemColor.control);
			panel4.add(inferior, BorderLayout.SOUTH);
		}
		add(panel4, BorderLayout.SOUTH);
		// //GEN-END:initComponents

		// labelImage.setPreferredSize(new Dimension(300, 300));
		
		panelEliminarFondo = new OperacionesPanel();
		panelEliminarFondo.setImageHolder(this);
		panel2.add(panelEliminarFondo, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
		dd = new DisplayDEM(); // Create the component.
		dd.setPreferredSize(new Dimension(300, 300));
		add(new JScrollPane(dd), BorderLayout.CENTER);
		dd.addMouseMotionListener(this); // Register mouse events.
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JMenuBar menuBar1;
	private JMenu archvoMenu;
	private JMenuItem abrirMenuItem;
	private JMenuItem guardarMenuItem;
	private JMenu menuEdicion;
	private JMenuItem menuItemDeshacer;
	private JPanel panel2;
	private JPanel panel1;
	private JPanel panel4;
	private JScrollPane scrollPane1;
	private static JTextArea log;
	private JTextArea inferior;
	// JFormDesigner - End of variables declaration //GEN-END:variables
	private JMenu configuracionMenu = null;
	private JMenuItem adminRasgosMenuItem = null;
	private JMenuItem adminClasesMenuItem = null;

	/**
	 * This method initializes configuracionMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getConfiguracionMenu() {
		if (configuracionMenu == null) {
			configuracionMenu = new JMenu();
			configuracionMenu.setText("Configuración");
			configuracionMenu.add(getAdminRasgosMenuItem());
			configuracionMenu.add(getAdminClasesMenuItem());
		}
		return configuracionMenu;
	}
	
	/**
	 * This method initializes adminRasgosMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAdminRasgosMenuItem() {
		if (adminRasgosMenuItem == null) {
			adminRasgosMenuItem = new JMenuItem();
			adminRasgosMenuItem.setText("Administrar Rasgos");
			adminRasgosMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					adminRasgosActionPerformed(e);
				}
			});
		}
		return adminRasgosMenuItem;
	}


	protected void adminRasgosActionPerformed(ActionEvent e) {
		GeneralTableModel model = new GeneralTableModel(new RasgoMapper());
		createFrame(new AdminPanel(model, "Rasgos"), "Administrar Rasgos");
	}

	protected void adminClasesActionPerformed(ActionEvent e) {
		GeneralTableModel model = new GeneralTableModel(new ClaseMapper());
		createFrame(new AdminPanel(model, "Clases"), "Administrar Clases");
	}

	private JFrame createFrame(JPanel panel, String title){
		JFrame frame = new JFrame(title);
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		return frame;
	}

	/**
	 * This method initializes adminClasesMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAdminClasesMenuItem() {
		if (adminClasesMenuItem == null) {
			adminClasesMenuItem = new JMenuItem();
			adminClasesMenuItem.setText("Administrar Clases");
			adminClasesMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					adminClasesActionPerformed(e);
				}
			});
		}
		return adminClasesMenuItem;
	}


	public static void main(String[] args) {
		TileCache cache = JAI.createTileCache(1l * 1024 * 1024);
		JAI.setDefaultTileSize(new Dimension(ImageUtil.tileWidth, ImageUtil.tileHeight));
		
		ImageLayout tileLayout = new ImageLayout();
		tileLayout.setTileWidth(ImageUtil.tileWidth);
		tileLayout.setTileHeight(ImageUtil.tileHeight);

		HashMap map = new HashMap();
		map.put(JAI.KEY_IMAGE_LAYOUT, tileLayout);
		map.put(JAI.KEY_TILE_CACHE, cache);
		map.put(JAI.KEY_CACHED_TILE_RECYCLING_ENABLED, true);
		RenderingHints tileHints = new RenderingHints(map);
		
		JAI.getDefaultInstance().setRenderingHints(tileHints);

		// Create and set up the window.
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		JFrame frame = new JFrame("Clasicador de granos");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ImageEditorPanel panel = new ImageEditorPanel();
		// Add content to the window.
		frame.add(panel);
		//panel.setFrameContenedor(frame);

		// Display the window.
		frame.pack();
		frame.setSize(1000, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		//new BrowserJava("http://www.google.com");
	}

	public PlanarImage getModifiedImage() {
		return modifiedImage;
	}

	public void setModifiedImage(PlanarImage modifiedImage) {
		this.modifiedImage = modifiedImage;
	}

	public PlanarImage getImage() {
		return getModifiedImage();
	}

	public void setImage(PlanarImage image) {
		setModifiedImage(image);
		updateImage();
	}

	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseMoved(MouseEvent arg0) {
		String info = dd.getPixelInfo();
		if (info != null)
			if (!"No data!".equals(info))
				inferior.setText(dd.getPixelInfo() + newline);

	}

	public List<ImageComand> getExecutedCommands() {
		return executedCommands;
	}

	public void setExecutedCommands(List<ImageComand> executedCommands) {
		this.executedCommands = executedCommands;
	}

	public void addExecutedCommand(ImageComand comand, String info) {
		if (comand != null) {
			getExecutedCommands().add(comand);
			
			logPantalla("Se ejecutó el commando: " + comand.getCommandName()
					);
			if (info != null && !info.trim().equals(""))
				logPantalla("\n" + info );
		}

	}

	public PlanarImage getOriginalImage() {
		return inputImage;
	}
	
	public static void logPantalla(String texto){
		Date fecha = new Date(System.currentTimeMillis());
		log.append(formater.format(fecha)+  texto + newline);
	}


	public Clasificador getClasificador() {
		return clasificador;
	}


	public void setClasificador(Clasificador clasificador) {
		this.clasificador = clasificador;
	}

	public BufferedImage getSelectedRectangle() {
		return dd.getSelectedRectangle();
	}

}
