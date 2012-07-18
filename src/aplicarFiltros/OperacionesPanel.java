/*
 * Created by JFormDesigner on Tue Sep 22 21:27:21 GMT 2009
 */

package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.text.DecimalFormat;

import javax.media.jai.PlanarImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

import org.hibernate.exception.JDBCConnectionException;

import procesamiento.Binarizar;
import procesamiento.Closing;
import procesamiento.ConvertEscalaGrises;
import procesamiento.DetectarContorno;
import procesamiento.DetectarContornoGrueso;
import procesamiento.DetectarObjetos;
import procesamiento.Dilate;
import procesamiento.EliminarFondo;
import procesamiento.EliminarFondoHistograma;
import procesamiento.Erode;
import procesamiento.GaussianFilter;
import procesamiento.GradientMagnitud;
import procesamiento.HSVRange;
import procesamiento.IImageProcessing;
import procesamiento.ImageComand;
import procesamiento.Invert;
import procesamiento.MedianFilter;
import procesamiento.ObtenerRangoColorObjeto;
import procesamiento.Opening;
import procesamiento.RealzarImagen;
import procesamiento.RgbHsv;
import procesamiento.Skeleton;
import procesamiento.SobelFilter;
import procesamiento.clasificacion.Configuracion;

import com.sun.media.jai.widget.DisplayJAI;

import dataAcces.ObjectDao;

/**
 * @author User #3
 */
public class OperacionesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IImageProcessing imageHolder;
	
	private static DecimalFormat rangeFormater = new DecimalFormat("0");  //  @jve:decl-index=0:
	
	private static boolean panelOperacionesEnabled = false;
	
	//  @jve:decl-index=0:

	public void dibujar() {
		// window1.setVisible(true);
		// window1.setEnabled(false);
	}

	public OperacionesPanel() {
		initComponents();
		/*
		 * this.textFieldHMax.setText("340"); this.textFieldHMin.setText("60");
		 * this.textFieldSMin.setText("8");
		 */
	}

	private void textFieldIntegerKeyTyped(KeyEvent e) {
		if (!(e.getKeyChar() >= '0' && e.getKeyChar() <= '9'))
			e.setKeyChar('\b');
	}
	
	private void executeCommand(final ImageComand command) {
		final Window window = this.getImageHolder().getMainWindow();
		Runnable runnable = new Runnable() {
			public void run() {
				long startTime = System.currentTimeMillis();
				Visualizador.iniciarProgreso(window);

				getImageHolder().setImage(command.execute());

				Visualizador.terminar();
				long endTime = System.currentTimeMillis();
				getImageHolder().addExecutedCommand(command, command.getInfo(), endTime - startTime);
				command.postExecute();
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
		/*
		 * getImageHolder().setImage(command.execute()); command.postExecute();
		 * getImageHolder().addExecutedCommand(command, command.getInfo());
		 */
	}

	/**
	 * Obtiene el rango HSV del fondo
	 * 
	 * @return
	 */
	private HSVRange getHSVRange() {
		HSVRange range = new HSVRange();
		if (textFieldHMin.getText() != null
				&& !textFieldHMin.getText().trim().equals(""))
			range.setHMin(Float.parseFloat(textFieldHMin.getText()));
		if (textFieldHMax.getText() != null
				&& !textFieldHMax.getText().trim().equals(""))
			range.setHMax(Float.parseFloat(textFieldHMax.getText()));
		if (textFieldSMin.getText() != null
				&& !textFieldSMin.getText().trim().equals(""))
			range.setSMin(Float.parseFloat(textFieldSMin.getText()));
		if (textFieldSMax.getText() != null
				&& !textFieldSMax.getText().trim().equals(""))
			range.setSMax(Float.parseFloat(textFieldSMax.getText()));
		if (textFieldVMin.getText() != null
				&& !textFieldVMin.getText().trim().equals(""))
			range.setVMin(Float.parseFloat(textFieldVMin.getText()));
		if (textFieldVMax.getText() != null
				&& !textFieldVMax.getText().trim().equals(""))
			range.setVMax(Float.parseFloat(textFieldVMax.getText()));
		return range;
	}

	/**
	 * Obtiene el rango HSV del objeto
	 * 
	 * @return
	 */
	private HSVRange getHSVRangeObjeto() {
		HSVRange range = new HSVRange();
		return range;
	}

	/**
	 * Setea el rango HSV del fondo
	 * 
	 * @return
	 */
	private void setHSVRange(HSVRange range) {
		if (range != null) {
			textFieldHMin.setText(null);
			textFieldHMax.setText(null);
			textFieldSMin.setText(null);
			textFieldSMax.setText(null);
			textFieldVMin.setText(null);
			textFieldVMax.setText(null);
			if (range.getHMin() != null)
				textFieldHMin.setText(rangeFormater.format(range.getHMin()));
			if (range.getHMax() != null)
				textFieldHMax.setText(rangeFormater.format(range.getHMax()));
			if (range.getSMin() != null)
				textFieldSMin.setText(rangeFormater.format(range.getSMin()));
			if (range.getSMax() != null)
				textFieldSMax.setText(rangeFormater.format(range.getSMax()));
			if (range.getVMin() != null)
				textFieldVMin.setText(rangeFormater.format(range.getVMin()));
			if (range.getVMax() != null)
				textFieldVMax.setText(rangeFormater.format(range.getVMax()));
		}
	}

	/**
	 * Setea el diametro del objeto de referencia
	 * 
	 * @return
	 */
	private void setDiametro(Double diametro) {
		textFieldDiametro.setValue(diametro);
		/**
		if (diametro != null) {
			textFieldDiametro.setText(diametroFormater.format(diametro));
		}*/
	}

	private void buttonBinarizarActionPerformed(ActionEvent e) {
		// if (getImage() != null && getImageHolder() != null) {
		Binarizar ef = new Binarizar(getImage(), getHSVRange(),
				getHSVRangeObjeto());
		executeCommand(ef);
		// }
	}

	public PlanarImage getImage() {
		return getImageHolder().getImage();
	}

	public IImageProcessing getImageHolder() {
		return imageHolder;
	}

	public void setImageHolder(IImageProcessing imageHolder) {
		this.imageHolder = imageHolder;
		if (imageHolder != null) {
			Configuracion configuracion = imageHolder.getClasificador().getConfiguracion();
			if (configuracion != null) {
				setHSVRange(configuracion.getHSVRange());
				setDiametro(configuracion.getDiametroObjetoReferencia());
			}
		}
	}

	private void buttonGradientMagnitudActionPerformed(ActionEvent e) {
		GradientMagnitud b = new GradientMagnitud(getImageHolder().getImage());
		executeCommand(b);
	}

	private void buttonOpeningActionPerformed(ActionEvent e) {
		Opening o = new Opening(getImageHolder().getImage());
		executeCommand(o);
	}

	private void buttonContornoActionPerformed(ActionEvent e) {
		// Se inicia el Progressbar
		// Visualizador.iniciarProgreso();
		try {
			DetectarObjetos o = new DetectarObjetos(getImage(),
					getImageHolder().getImage(), getHSVRange(),
					getImageHolder().getClasificador());
			executeCommand(o);
		} catch (JDBCConnectionException ex) {
			JOptionPane.showMessageDialog(this,
					"Error de conexión a la base de datos.", "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Ocurrió un error inesperado al clasificar.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void buttonClosingActionPerformed(ActionEvent e) {
		Closing o = new Closing(getImageHolder().getImage());
		executeCommand(o);
	}

	private void buttonErosionActionPerformed(ActionEvent e) {
		Erode er = new Erode(getImageHolder().getImage());
		executeCommand(er);
	}

	private void buttonDilatacionActionPerformed(ActionEvent e) {
		Dilate er = new Dilate(getImageHolder().getImage());
		executeCommand(er);
	}

	private void buttonContornoActualActionPerformed(ActionEvent e) {
		DetectarContorno dc = new DetectarContorno(getImageHolder().getImage(),
				getImageHolder().getOriginalImage(), new Color(100, 100, 100),
				Color.RED);
		executeCommand(dc);
	}

	private void buttonToGraySscaleActionPerformed(ActionEvent e) {
		ConvertEscalaGrises dc = new ConvertEscalaGrises(getImageHolder()
				.getImage());
		executeCommand(dc);
	}

	private void buttonDetectarContorno2ActionPerformed(ActionEvent e) {
		DetectarContornoGrueso dc = new DetectarContornoGrueso(getImageHolder()
				.getImage());
		executeCommand(dc);
	}

	private void buttonInvertActionPerformed(ActionEvent e) {
		Invert dc = new Invert(getImageHolder().getImage());
		executeCommand(dc);
	}

	private void buttonSobelActionPerformed(ActionEvent e) {
		SobelFilter dc = new SobelFilter(getImageHolder().getImage());
		executeCommand(dc);
	}

	private void buttonEliminarFondoActionPerformed(ActionEvent e) {
		EliminarFondo dc = new EliminarFondo(getImageHolder().getImage(),
				getHSVRange(), getHSVRangeObjeto());
		executeCommand(dc);
	}

	private void buttonSkeletonActionPerformed(ActionEvent e) {
		Skeleton dc = new Skeleton(getImageHolder().getImage());
		executeCommand(dc);
	}

	private void buttonRangoObjetoActionPerformed(ActionEvent e) {
		ObtenerRangoColorObjeto command = new ObtenerRangoColorObjeto(
				getImageHolder().getImage(), getHSVRange());
		executeCommand(command);
	}

	private void buttonMedianFilterActionPerformed(ActionEvent e) {
		MedianFilter mf = new MedianFilter(getImageHolder().getImage());
		executeCommand(mf);
	}
	  
	private void buttonGaussianFilterActionPerformed(ActionEvent e) {
		GaussianFilter gf = new GaussianFilter(getImageHolder().getImage(), 5f);
		executeCommand(gf);
	}
	
	private void buttonEliminarFondoHistogramaActionPerformed(
			ActionEvent e) {
		EliminarFondoHistograma ef = new EliminarFondoHistograma(getImageHolder().getImage());
		executeCommand(ef);
		
	}
	
	private void buttonRealzarImagenPerformed(
			ActionEvent e) {
		RealzarImagen ri = new RealzarImagen(getImageHolder().getImage());
		executeCommand(ri);
		
	}

	private void buttonGuardarClasificacionActionPerformed(ActionEvent e) {

		try {
			getImageHolder().getClasificador().guardarClasificacion();
			JOptionPane.showMessageDialog(this, "Clasificación guardada.");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Ocurrió un error al guardar la clasificación.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - seba cola
		tabbedPane1 = new JTabbedPane();
		panel1 = new JPanel();
		panel3 = new JPanel();
		buttonGuardarClasificacion = new JButton();
		buttonContorno = new JButton();
		button1 = new JButton();
		button2 = new JButton();
		panel2 = new JPanel();
		fondoPanel = new JPanel();
		panelH = new JPanel();
		labelHMin = new JLabel();
		textFieldHMin = new JTextField();
		labelHMax = new JLabel();
		textFieldHMax = new JTextField();
		panelS = new JPanel();
		labelSMin = new JLabel();
		textFieldSMin = new JTextField();
		labelSMax = new JLabel();
		textFieldSMax = new JTextField();
		panelV = new JPanel();
		labelVSMin = new JLabel();
		textFieldVMin = new JTextField();
		labelVMax = new JLabel();
		textFieldVMax = new JTextField();
		scrollPanel7 = new JScrollPane();
		panel7 = new JPanel();
		buttonBinarizar = new JButton();
		buttonGradientMagnitude = new JButton();
		buttonOpening = new JButton();
		buttonClosing = new JButton();
		buttonErosion = new JButton();
		buttonDilatacion = new JButton();
		buttonContornoActual = new JButton();
		buttonToGraySscale = new JButton();
		buttonDetectarContorno2 = new JButton();
		buttonInvert = new JButton();
		buttonSobel = new JButton();
		buttonEliminarFondo = new JButton();
		buttonSkeleton = new JButton();
		buttonRangoObjeto = new JButton();
		buttonMedianFilter = new JButton();
		buttonGaussianFilter = new JButton();
		buttonEliminarFondoHistograma = new JButton();
		buttonRealzarImagen = new JButton();

		// ======== this ========

		// JFormDesigner evaluation mark
		// setBorder(new javax.swing.border.CompoundBorder(
		// new javax.swing.border.TitledBorder(new
		// javax.swing.border.EmptyBorder(0, 0, 0, 0),
		// "", javax.swing.border.TitledBorder.CENTER,
		// javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog",
		// java.awt.Font.BOLD, 12),
		// java.awt.Color.red), getBorder())); addPropertyChangeListener(new
		// java.beans.PropertyChangeListener(){public void
		// propertyChange(java.beans.PropertyChangeEvent
		// e){if("border".equals(e.getPropertyName()))throw new
		// RuntimeException();}});

		setLayout(null);

		// ======== tabbedPane1 ========
		{
			tabbedPane1.setBackground(UIManager.getColor("Button.background"));

			// ======== panel1 ========
			{
				panel1.setLayout(null);

				// ======== panel3 ========
				{
					panel3.setBorder(new BevelBorder(BevelBorder.RAISED,
							new Color(0, 0, 153), null, null, new Color(0, 0,
									102)));
					panel3.setLayout(null);

					// ---- buttonGuardarClasificacion ----
					buttonGuardarClasificacion.setText("Guardar clasificacion");
					buttonGuardarClasificacion.setFont(new Font("Tahoma",
							Font.PLAIN, 12));
					buttonGuardarClasificacion.setForeground(new Color(0, 0,
							102));
					buttonGuardarClasificacion
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									buttonGuardarClasificacionActionPerformed(e);
								}
							});
					panel3.add(buttonGuardarClasificacion);
					buttonGuardarClasificacion
							.setBounds(35, 85, 190, buttonGuardarClasificacion
									.getPreferredSize().height);

					// ---- buttonContorno ----
					buttonContorno.setText("Clasificar");
					buttonContorno.setBackground(UIManager
							.getColor("Button.background"));
					buttonContorno.setFont(new Font("Tahoma", Font.PLAIN, 12));
					buttonContorno.setForeground(new Color(0, 0, 102));
					buttonContorno.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							buttonContornoActionPerformed(e);
						}
					});
					panel3.add(buttonContorno);
					buttonContorno.setBounds(35, 55, 190, buttonContorno
							.getPreferredSize().height);

					// ---- button1 ----
					button1.setIcon(new ImageIcon("img\\c9.jpg"));
					panel3.add(button1);
					button1.setBounds(5, 115, 255, 40);

					// ---- button2 ----
					button2.setIcon(new ImageIcon("img\\c9.jpg"));
					panel3.add(button2);
					button2.setBounds(5, 5, 255, 40);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < panel3.getComponentCount(); i++) {
							Rectangle bounds = panel3.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = panel3.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel3.setMinimumSize(preferredSize);
						panel3.setPreferredSize(preferredSize);
					}
				}
				panel1.add(panel3);
				panel3.setBounds(30, 10, 265, 160);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for (int i = 0; i < panel1.getComponentCount(); i++) {
						Rectangle bounds = panel1.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width,
								preferredSize.width);
						preferredSize.height = Math.max(bounds.y
								+ bounds.height, preferredSize.height);
					}
					Insets insets = panel1.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel1.setMinimumSize(preferredSize);
					panel1.setPreferredSize(preferredSize);
				}
			}
			tabbedPane1.addTab("Principal", panel1);

			// ======== panel2 ========
			{
				panel2.setLayout(null);

				// ======== fondoPanel ========
				{
					fondoPanel.setLayout(null);

					// ======== panelH ========
					{
						panelH.setBorder(BorderFactory.createTitledBorder(null,
								"H", TitledBorder.LEADING, TitledBorder.TOP,
								new Font("Dialog", Font.BOLD, 12),
								SystemColor.textHighlight));
						panelH.setLayout(null);

						// ---- labelHMin ----
						labelHMin.setText("Min:");
						labelHMin.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelH.add(labelHMin);
						labelHMin.setBounds(10, 25, 35, labelHMin
								.getPreferredSize().height);

						// ---- textFieldHMin ----
						textFieldHMin.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelH.add(textFieldHMin);
						textFieldHMin.setBounds(50, 20, 65, textFieldHMin
								.getPreferredSize().height);

						// ---- labelHMax ----
						labelHMax.setText("Max:");
						labelHMax.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelH.add(labelHMax);
						labelHMax.setBounds(129, 16, 35, 14);

						// ---- textFieldHMax ----
						textFieldHMax.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelH.add(textFieldHMax);
						textFieldHMax.setBounds(172, 12, 65, 20);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for (int i = 0; i < panelH.getComponentCount(); i++) {
								Rectangle bounds = panelH.getComponent(i)
										.getBounds();
								preferredSize.width = Math.max(bounds.x
										+ bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y
										+ bounds.height, preferredSize.height);
							}
							Insets insets = panelH.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							panelH.setMinimumSize(preferredSize);
							panelH.setPreferredSize(preferredSize);
						}
					}
					fondoPanel.add(panelH);
					panelH.setBounds(11, 15, 245, 37);

					// ======== panelS ========
					{
						panelS.setLayout(null);

						// ---- labelSMin ----
						labelSMin.setText("Min:");
						labelSMin.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS.add(labelSMin);
						labelSMin.setBounds(10, 25, 35, labelSMin
								.getPreferredSize().height);

						// ---- textFieldSMin ----
						textFieldSMin.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS.add(textFieldSMin);
						textFieldSMin.setBounds(50, 20, 65, textFieldSMin
								.getPreferredSize().height);

						// ---- labelSMax ----
						labelSMax.setText("Max:");
						labelSMax.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS.add(labelSMax);
						labelSMax.setBounds(129, 17, 35, 14);

						// ---- textFieldSMax ----
						textFieldSMax.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS.add(textFieldSMax);
						textFieldSMax.setBounds(172, 13, 65, 20);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for (int i = 0; i < panelS.getComponentCount(); i++) {
								Rectangle bounds = panelS.getComponent(i)
										.getBounds();
								preferredSize.width = Math.max(bounds.x
										+ bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y
										+ bounds.height, preferredSize.height);
							}
							Insets insets = panelS.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							panelS.setMinimumSize(preferredSize);
							panelS.setPreferredSize(preferredSize);
						}
					}
					fondoPanel.add(panelS);
					panelS.setBounds(11, 54, 245, 37);

					// ======== panelS2 ========
					{
						panelV.setBorder(BorderFactory.createTitledBorder(
								null, "V", TitledBorder.LEADING,
								TitledBorder.TOP, new Font("Dialog", Font.BOLD,
										12), SystemColor.textHighlight));
						panelV.setLayout(null);

						// ---- labelVSMin ----
						labelVSMin.setText("Min:");
						labelVSMin.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelV.add(labelVSMin);
						labelVSMin.setBounds(10, 25, 35, labelVSMin
								.getPreferredSize().height);

						// ---- textFieldVMin ----
						textFieldVMin.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelV.add(textFieldVMin);
						textFieldVMin.setBounds(50, 20, 65, textFieldVMin
								.getPreferredSize().height);

			fondoPanel.add(getButtonReestablecerFondo(), null);
						panelS.setBorder(BorderFactory.createTitledBorder(null,
								"S", TitledBorder.LEADING, TitledBorder.TOP,
								new Font("Dialog", Font.BOLD, 12),
								SystemColor.textHighlight));
						// ---- labelVMax ----
						labelVMax.setText("Max:");
						labelVMax.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelV.add(labelVMax);
						labelVMax.setBounds(129, 19, 35, 14);

						// ---- textFieldVMax ----
						textFieldVMax.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelV.add(textFieldVMax);
						textFieldVMax.setBounds(173, 14, 65, 20);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for (int i = 0; i < panelV.getComponentCount(); i++) {
								Rectangle bounds = panelV.getComponent(i)
										.getBounds();
								preferredSize.width = Math.max(bounds.x
										+ bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y
										+ bounds.height, preferredSize.height);
							}
							Insets insets = panelV.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							panelV.setMinimumSize(preferredSize);
							panelV.setPreferredSize(preferredSize);
						}
					}
					fondoPanel.add(panelV);
					panelV.setBounds(11, 94, 245, 38);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < fondoPanel.getComponentCount(); i++) {
							Rectangle bounds = fondoPanel.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = fondoPanel.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						fondoPanel.setMinimumSize(preferredSize);
						fondoPanel.setPreferredSize(preferredSize);
					}
				}
				panel2.add(fondoPanel);
				fondoPanel.setBounds(3, 5, 397, 167);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for (int i = 0; i < panel2.getComponentCount(); i++) {
						Rectangle bounds = panel2.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width,
								preferredSize.width);
						preferredSize.height = Math.max(bounds.y
								+ bounds.height, preferredSize.height);
					}
					Insets insets = panel2.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel2.setMinimumSize(preferredSize);
					panel2.setPreferredSize(preferredSize);
				}
			}
			// ======== panel7 ========
			{
				panel7.setLayout(new BorderLayout());

				// ---- buttonBinarizar ----
				buttonBinarizar.setText("Binarizar");
				buttonBinarizar.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonBinarizar.setPreferredSize(new Dimension(270, 30));
				buttonBinarizar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonBinarizarActionPerformed(e);
					}
				});
				// ---- buttonGradientMagnitude ----
				buttonGradientMagnitude.setText("GradientMagnitude");
				buttonGradientMagnitude.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonGradientMagnitude.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonGradientMagnitudActionPerformed(e);
					}
				});
				// ---- buttonOpening ----
				buttonOpening.setText("Opening");
				buttonOpening.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonOpening.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonOpeningActionPerformed(e);
					}
				});
				// ---- buttonClosing ----
				buttonClosing.setText("Closing");
				buttonClosing.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonClosing.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonClosingActionPerformed(e);
					}
				});
				// ---- buttonErosion ----
				buttonErosion.setText("Erosi\u00f3n");
				buttonErosion.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonErosion.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonErosionActionPerformed(e);
					}
				});
				// ---- buttonDilatacion ----
				buttonDilatacion.setText("Dilataci\u00f3n");
				buttonDilatacion.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonDilatacion.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonDilatacionActionPerformed(e);
					}
				});
				// ---- buttonContornoActual ----
				buttonContornoActual.setText("Contorno Imagen Actual");
				buttonContornoActual.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonContornoActual.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonContornoActualActionPerformed(e);
					}
				});
				// ---- buttonToGraySscale ----
				buttonToGraySscale.setText("Convertir a escala de grises");
				buttonToGraySscale.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonToGraySscale.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonToGraySscaleActionPerformed(e);
					}
				});
				// ---- buttonDetectarContorno2 ----
				buttonDetectarContorno2.setText("Detectar contorno grueso");
				buttonDetectarContorno2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonDetectarContorno2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonDetectarContorno2ActionPerformed(e);
					}
				});
				// ---- buttonInvert ----
				buttonInvert.setText("Invertir");
				buttonInvert.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonInvert.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonInvertActionPerformed(e);
					}
				});

				// ---- buttonSobel ----
				buttonSobel.setText("Sobel");
				buttonSobel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonSobel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonSobelActionPerformed(e);
					}
				});
				// ---- buttonEliminarFondo ----
				buttonEliminarFondo.setText("Eliminar Fondo");
				buttonEliminarFondo.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonEliminarFondo.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonEliminarFondoActionPerformed(e);
					}
				});
				// ---- buttonSkeleton ----
				buttonSkeleton.setText("Esqueleto");
				buttonSkeleton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonSkeleton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonSkeletonActionPerformed(e);
					}
				});

				// ---- buttonRangoObjeto ----
				buttonRangoObjeto.setText("Rango objeto");
				buttonRangoObjeto.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonRangoObjeto.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonRangoObjetoActionPerformed(e);
					}
				});
				
				// ---- buttonMedianFilter ----
				buttonMedianFilter.setText("Filtro Mediana");
				buttonMedianFilter.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonMedianFilter.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonMedianFilterActionPerformed(e);
					}

					
				});
				// ---- buttonGussianFilter ----
				buttonGaussianFilter.setText("Filtro Gausiano");
				buttonGaussianFilter.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonGaussianFilter.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonGaussianFilterActionPerformed(e);
					}
				});

				// ---- buttonEliminarFondoHistograma ----
				buttonEliminarFondoHistograma.setText("Eliminar Fondo Histograma");
				buttonEliminarFondoHistograma.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonEliminarFondoHistograma.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonEliminarFondoHistogramaActionPerformed(e);
					}

				});
				
				// ---- buttonRealzarImagen ----
				buttonRealzarImagen.setText("Realzar Imagen");
				buttonRealzarImagen.setAlignmentX(JComponent.CENTER_ALIGNMENT);
				buttonRealzarImagen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonRealzarImagenPerformed(e);
					}

				});
				
				{ // compute preferred size
					/*
					Dimension preferredSize = new Dimension();
					for (int i = 0; i < panel7.getComponentCount(); i++) {
						Rectangle bounds = panel7.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width,
								preferredSize.width);
						preferredSize.height = Math.max(bounds.y
								+ bounds.height, preferredSize.height);
					}
					Insets insets = panel7.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel7.setMinimumSize(preferredSize);
					panel7.setPreferredSize(preferredSize);*/
					Box box = Box.createVerticalBox();
					
					box.add(buttonBinarizar, null);
					box.add(buttonGradientMagnitude, null);
					box.add(buttonOpening, null);
					box.add(buttonClosing, null);
					box.add(buttonErosion, null);
					box.add(buttonDilatacion, null);
					box.add(buttonContornoActual, null);
					box.add(buttonToGraySscale, null);
					box.add(buttonDetectarContorno2, null);
					box.add(buttonInvert, null);
					box.add(buttonSobel, null);
					box.add(buttonEliminarFondo, null);
					box.add(buttonSkeleton, null);
					box.add(buttonRangoObjeto, null);
					box.add(buttonMedianFilter, null);
					box.add(buttonGaussianFilter, null);
					box.add(buttonEliminarFondoHistograma, null);
					box.add(buttonRealzarImagen, null);
					panel7.add(box,BorderLayout.CENTER);
				}
			}
		}
		// scroll panel 7
		scrollPanel7.setViewportView(panel7);
		
		add(tabbedPane1);
		tabbedPane1.setBounds(13, 5, 409, 442);

		tabbedPane1.setVisible(true);
		{ // compute preferred size
			Dimension preferredSize = new Dimension();
			for (int i = 0; i < getComponentCount(); i++) {
				Rectangle bounds = getComponent(i).getBounds();
				preferredSize.width = Math.max(bounds.x + bounds.width,
						preferredSize.width);
				preferredSize.height = Math.max(bounds.y + bounds.height,
						preferredSize.height);
			}
			Insets insets = getInsets();
			preferredSize.width += insets.right;
			preferredSize.height += insets.bottom;
			setMinimumSize(preferredSize);
			setPreferredSize(preferredSize);
		}
		// //GEN-END:initComponents

		fondoPanel.setBorder(BorderFactory.createTitledBorder(null, "Fondo",
				TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog",
						Font.BOLD, 12), SystemColor.textHighlight));
		this.setSize(new Dimension(436, 452));
		panel7.setPreferredSize(this.getPreferredSize());
		tabbedPane1.addTab("Configuración", null, panel2, null);
		if (panelOperacionesEnabled)
			tabbedPane1.addTab("Operaciones", null, scrollPanel7, null);
		panel2.add(getObjetoPanel1(), null);
		fondoPanel.add(getButtonSeleccionarFondo(), null);
			fondoPanel.add(getButtonEliminarFondo2(), null);
			fondoPanel.add(getButtonGuardarFondo(), null);
			scrollPanel7.setVisible(true);
		labelSMin.setBounds(new Rectangle(10, 17, 35, 14));
		textFieldHMin.setBounds(new Rectangle(50, 12, 65, 20));
		textFieldSMin.setBounds(new Rectangle(50, 14, 65, 20));
		labelHMin.setBounds(new Rectangle(10, 16, 35, 14));
		labelVSMin.setBounds(new Rectangle(10, 18, 35, 14));
		textFieldVMin.setBounds(new Rectangle(51, 14, 65, 20));
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - seba cola
	private JTabbedPane tabbedPane1;
	private JPanel panel1;
	private JPanel panel3;
	private JButton buttonGuardarClasificacion;
	private JButton buttonContorno;
	private JButton button1;
	private JButton button2;
	private JPanel panel2;
	private JPanel fondoPanel;
	private JPanel panelH;
	private JLabel labelHMin;
	private JTextField textFieldHMin;
	private JLabel labelHMax;
	private JTextField textFieldHMax;
	private JPanel panelS;
	private JLabel labelSMin;
	private JTextField textFieldSMin;
	private JLabel labelSMax;
	private JTextField textFieldSMax;
	private JPanel panelV;
	private JLabel labelVSMin;
	private JTextField textFieldVMin;
	private JLabel labelVMax;
	private JTextField textFieldVMax;
	private JPanel panel7;
	private JScrollPane scrollPanel7;
	private JButton buttonBinarizar;
	private JButton buttonGradientMagnitude;
	private JButton buttonOpening;
	private JButton buttonClosing;
	private JButton buttonErosion;
	private JButton buttonDilatacion;
	private JButton buttonContornoActual;
	private JButton buttonToGraySscale;
	private JButton buttonDetectarContorno2;
	private JButton buttonInvert;
	private JButton buttonSobel;
	private JButton buttonEliminarFondo;
	private JButton buttonSkeleton;
	private JButton buttonRangoObjeto;
	private JButton buttonSeleccionarFondo = null;
	private JButton buttonMedianFilter;
	private JButton buttonGaussianFilter;
	private JButton buttonEliminarFondoHistograma;
	private JButton buttonRealzarImagen;
	private JPanel objetoPanel1 = null;
	private JPanel panelH2 = null;
	private JLabel labelDiametro = null;
	private JFormattedTextField textFieldDiametro = null;
	private JButton buttonEliminarFondo2 = null;
	private JButton buttonGuardarFondo = null;
	private JButton buttonReestablecerFondo = null;
	private JLabel labelMilimetros = null;
	private JButton buttonGuardarDiametro = null;
	/**
	 * This method initializes buttonSeleccionarFondo
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getButtonSeleccionarFondo() {
		if (buttonSeleccionarFondo == null) {
			buttonSeleccionarFondo = new JButton();
			buttonSeleccionarFondo.setText("Seleccionar desde imagen");
			buttonSeleccionarFondo.setBounds(new Rectangle(12, 140, 244, 21));
			buttonSeleccionarFondo.setActionCommand("");
			buttonSeleccionarFondo
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							buttonSeleccionarFondoActionPerformed(e);
						}
					});
		}
		return buttonSeleccionarFondo;
	}

	private void showSelectedImage(BufferedImage image) {
		if (image != null) {
			JFrame frame = new JFrame();
			frame.setTitle("Seleccion");
			// Get the JFrame's ContentPane.
			Container contentPane = frame.getContentPane();
			contentPane.setLayout(new BorderLayout());
			// Create an instance of DisplayJAI.
			DisplayJAI dj = new DisplayJAI(image);
			// Add to the JFrame's ContentPane an instance of JScrollPane
			// containing the
			// DisplayJAI instance.
			contentPane.add(new JScrollPane(dj), BorderLayout.CENTER);
			// Add a text label with the image information.
			// Set the closing operation so the application is finished.
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	}

	private void buttonSeleccionarFondoActionPerformed(ActionEvent e) {
		BufferedImage image = getImageHolder().getSelectedRectangle();
		if (image != null){
			showSelectedImage(image);

			Raster raster = image.getData();
			HSVRange range = RgbHsv.createHsvRange(raster, null);
			setHSVRange(range);
		}
	}

	/**
	 * This method initializes objetoPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getObjetoPanel1() {
		if (objetoPanel1 == null) {
			objetoPanel1 = new JPanel();
			objetoPanel1.setLayout(null);
			objetoPanel1.setBounds(new Rectangle(3, 172, 397, 61));
			objetoPanel1.setMinimumSize(new Dimension());
			objetoPanel1.setPreferredSize(new Dimension());
			objetoPanel1.setBorder(BorderFactory.createTitledBorder(null, "Objeto de Referencia", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 12), SystemColor.textHighlight));
			objetoPanel1.add(getPanelH2(), getPanelH2().getName());
			objetoPanel1.add(getButtonGuardarDiametro(), null);
		}
		return objetoPanel1;
	}

	/**
	 * This method initializes panelH2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanelH2() {
		if (panelH2 == null) {
			labelMilimetros = new JLabel();
			labelMilimetros.setBounds(new Rectangle(145, 16, 36, 14));
			labelMilimetros.setText("mm.");
			labelMilimetros.setFont(new Font("Tahoma", Font.BOLD, 11));
			labelDiametro = new JLabel();
			labelDiametro.setFont(new Font("Tahoma", Font.BOLD, 11));
			labelDiametro.setBounds(new Rectangle(10, 16, 61, 14));
			labelDiametro.setToolTipText("Diámetro en milímetros");
			labelDiametro.setText("Diámetro:");
			panelH2 = new JPanel();
			panelH2.setLayout(null);
			panelH2.setBounds(new Rectangle(11, 17, 244, 35));
			panelH2.setMinimumSize(new Dimension());
			panelH2.setPreferredSize(new Dimension());
			panelH2.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			panelH2.add(labelDiametro, labelDiametro.getName());
			panelH2.add(getTextFieldDiametro(), getTextFieldDiametro().getName());
			panelH2.add(labelMilimetros, null);
		}
		return panelH2;
	}

	/**
	 * This method initializes textFieldDiametro
	 * 
	 * @return javax.swing.JTextField
	 */
	private JFormattedTextField getTextFieldDiametro() {
		if (textFieldDiametro == null) {
			NumberFormatter formater = new NumberFormatter(new DecimalFormat("#.000"));
			textFieldDiametro = new JFormattedTextField(formater);
			textFieldDiametro.setBounds(new Rectangle(78, 11, 65, 20));
		}
		return textFieldDiametro;
	}

	/**
	 * This method initializes buttonEliminarFondo2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonEliminarFondo2() {
		if (buttonEliminarFondo2 == null) {
			buttonEliminarFondo2 = new JButton();
			buttonEliminarFondo2.setText("Eliminar Fondo");
			buttonEliminarFondo2.setBounds(new Rectangle(268, 26, 117, 21));
			buttonEliminarFondo2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					buttonEliminarFondoActionPerformed(e);
				}
			});
		}
		return buttonEliminarFondo2;
	}

	/**
	 * This method initializes buttonGuardarFondo	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonGuardarFondo() {
		if (buttonGuardarFondo == null) {
			buttonGuardarFondo = new JButton();
			buttonGuardarFondo.setText("Guardar");
			buttonGuardarFondo.setBounds(new Rectangle(268, 55, 117, 21));
			buttonGuardarFondo.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					buttonGuardarFondoActionPerformed(e);
				}
			});
		}
		return buttonGuardarFondo;
	}

	protected void buttonGuardarFondoActionPerformed(ActionEvent e) {
		Configuracion configuracion = imageHolder.getClasificador().getConfiguracion();
		if (configuracion != null) {
			configuracion.setHSVRange(getHSVRange());
			ObjectDao.getInstance().save(configuracion);
		}
	}

	/**
	 * This method initializes buttonReestablecerFondo	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonReestablecerFondo() {
		if (buttonReestablecerFondo == null) {
			buttonReestablecerFondo = new JButton();
			buttonReestablecerFondo.setBounds(new Rectangle(269, 84, 115, 21));
			buttonReestablecerFondo.setText("Reestablecer");
			buttonReestablecerFondo.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					buttonReestablecerActionPerformed(e);
				}
			});
		}
		return buttonReestablecerFondo;
	}

	protected void buttonReestablecerActionPerformed(ActionEvent e) {
		Configuracion configuracion = imageHolder.getClasificador().getConfiguracion();
		if (configuracion != null) {
			setHSVRange(configuracion.getHSVRange());
		}
	}

	/**
	 * This method initializes buttonGuardarDiametro	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonGuardarDiametro() {
		if (buttonGuardarDiametro == null) {
			buttonGuardarDiametro = new JButton();
			buttonGuardarDiametro.setBounds(new Rectangle(269, 19, 117, 21));
			buttonGuardarDiametro.setText("Guardar");
			buttonGuardarDiametro.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					guardarDiametroActionPerformed();
				}
			});
		}
		return buttonGuardarDiametro;
	}

	private void guardarDiametroActionPerformed() {
		Configuracion configuracion = imageHolder.getClasificador().getConfiguracion();
		if (configuracion != null) {
			try{
				Double diametro = Double.valueOf(textFieldDiametro.getValue().toString());//Double.valueOf(textFieldDiametro.getText());
				configuracion.setDiametroObjetoReferencia(diametro);
				ObjectDao.getInstance().save(configuracion);
				textFieldDiametro.setBackground(Color.white);
				textFieldDiametro.setToolTipText(null);
			}
			catch (NullPointerException e) {
				textFieldDiametro.setBackground(Color.red);
				textFieldDiametro.setToolTipText("Valor requerido");
			}
			catch (NumberFormatException e) {
				textFieldDiametro.setBackground(Color.red);
				textFieldDiametro.setToolTipText("Valor inválido");
			}
			catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						"Ocurrió un error inesperado al realizar la operación.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}

} 