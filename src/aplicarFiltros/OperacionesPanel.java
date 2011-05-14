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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import org.hibernate.exception.JDBCConnectionException;

import com.sun.media.jai.widget.DisplayJAI;

import procesamiento.Binarizar;
import procesamiento.Closing;
import procesamiento.ConvertEscalaGrises;
import procesamiento.DetectarContorno;
import procesamiento.DetectarContornoGrueso;
import procesamiento.DetectarObjetos;
import procesamiento.Dilate;
import procesamiento.EliminarFondo;
import procesamiento.Erode;
import procesamiento.GradientMagnitud;
import procesamiento.HSVRange;
import procesamiento.IImageProcessing;
import procesamiento.ImageComand;
import procesamiento.Invert;
import procesamiento.ObtenerRangoColorObjeto;
import procesamiento.Opening;
import procesamiento.Skeleton;
import procesamiento.SobelFilter;
import procesamiento.clasificacion.Configuracion;

/**
 * @author User #3
 */
public class OperacionesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IImageProcessing imageHolder;
	
	public void dibujar(){
		//window1.setVisible(true);
		//window1.setEnabled(false);
	}
	public OperacionesPanel() {
		initComponents();
		/*
		this.textFieldHMax.setText("340");
		this.textFieldHMin.setText("60");
		this.textFieldSMin.setText("8");*/
	}

	private void textFieldIntegerKeyTyped(KeyEvent e) {
		if (!(e.getKeyChar() >= '0' && e.getKeyChar() <= '9'))
			e.setKeyChar('\b');
	}
	
	private void executeCommand(final ImageComand command){
		
		 Runnable runnable = new Runnable() {
	            public void run() {
	    			Visualizador.iniciarProgreso();
	    			
	            	getImageHolder().setImage(command.execute());
	            	
	            	Visualizador.terminar();
	        		command.postExecute();
	        		getImageHolder().addExecutedCommand(command, command.getInfo());
	            }
	        };
	     Thread thread = new Thread(runnable);
	     thread.start();
	    /*
		getImageHolder().setImage(command.execute());
		command.postExecute();
		getImageHolder().addExecutedCommand(command, command.getInfo());
		*/
	}

	/**
	 * Obtiene el rango HSV del fondo
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
	 * @return
	 */
	private HSVRange getHSVRangeObjeto() {
		HSVRange range = new HSVRange();
		return range;
	}
	
	/**
	 * Setea el rango HSV del objeto
	 * @return
	 */
	private void setHSVRangeObjeto(HSVRange range) {
	}

	private void buttonBinarizarActionPerformed(ActionEvent e) {
		//if (getImage() != null && getImageHolder() != null) {
			Binarizar ef = new Binarizar(getImage(), getHSVRange(), getHSVRangeObjeto());
			executeCommand(ef);
		//}
	}

	public PlanarImage getImage() {
		return getImageHolder().getImage();
	}

	public IImageProcessing getImageHolder() {
		return imageHolder;
	}

	public void setImageHolder(IImageProcessing imageHolder) {
		this.imageHolder = imageHolder;
		if(imageHolder != null){
			Configuracion configuracion = imageHolder.getClasificador().getConfiguracion();
			if(configuracion != null){
				if (configuracion.getFondoHMin() != null)
					textFieldHMin.setText(configuracion.getFondoHMin().toString());
				if (configuracion.getFondoHMax() != null)
					textFieldHMax.setText(configuracion.getFondoHMax().toString());
				if (configuracion.getFondoSMin() != null)
					textFieldSMin.setText(configuracion.getFondoSMin().toString());
				if (configuracion.getFondoSMax() != null)
					textFieldSMax.setText(configuracion.getFondoSMax().toString());
				if (configuracion.getFondoVMin() != null)
					textFieldVMin.setText(configuracion.getFondoVMin().toString());
				if (configuracion.getFondoVMax() != null)
					textFieldVMax.setText(configuracion.getFondoVMax().toString());

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
		//Se inicia el Progressbar
		//Visualizador.iniciarProgreso();
		try{
			DetectarObjetos o = new DetectarObjetos(getImage(), getImageHolder().getOriginalImage(), getHSVRange(),getImageHolder().getClasificador());
			executeCommand(o);
		}
		catch (JDBCConnectionException ex) {
			JOptionPane.showMessageDialog(this, "Error de conexión a la base de datos.","Error",JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al clasificar.","Error",JOptionPane.ERROR_MESSAGE);
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
		DetectarContorno dc = new DetectarContorno(getImageHolder().getImage(), getImageHolder().getOriginalImage(), new Color(100,100,100), Color.RED );
		executeCommand(dc);
	}

	private void buttonToGraySscaleActionPerformed(ActionEvent e) {
		ConvertEscalaGrises dc = new ConvertEscalaGrises(getImageHolder().getImage());
		executeCommand(dc);
	}


	private void buttonDetectarContorno2ActionPerformed(ActionEvent e) {
		DetectarContornoGrueso dc = new DetectarContornoGrueso(getImageHolder().getImage());
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
		EliminarFondo dc = new EliminarFondo(getImageHolder().getImage(),getHSVRange(),getHSVRangeObjeto());
		executeCommand(dc);
	}

	private void buttonSkeletonActionPerformed(ActionEvent e) {
		Skeleton dc = new Skeleton(getImageHolder().getImage());
		executeCommand(dc);
	}	
	private void buttonRangoObjetoActionPerformed(ActionEvent e) {
		ObtenerRangoColorObjeto command = new ObtenerRangoColorObjeto(getImageHolder().getImage(),getHSVRange());
		getImageHolder().setImage(command.execute());
		setHSVRangeObjeto(command.getRangoObjeto());
		command.postExecute();
		getImageHolder().addExecutedCommand(command, command.getInfo());
	}

	private void buttonGuardarClasificacionActionPerformed(ActionEvent e) {
		
		try{
			getImageHolder().getClasificador().guardarClasificacion();
		    JOptionPane.showMessageDialog(this, "Clasificación guardada.");	
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Ocurrió un error al guardar la clasificación.","Error",JOptionPane.ERROR_MESSAGE);
		}

	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
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
		panelS2 = new JPanel();
		labelVSMin = new JLabel();
		textFieldVMin = new JTextField();
		labelVMax = new JLabel();
		textFieldVMax = new JTextField();
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

		//======== this ========

		// JFormDesigner evaluation mark
//		setBorder(new javax.swing.border.CompoundBorder(
//			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
//				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
//				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
//				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		setLayout(null);

		//======== tabbedPane1 ========
		{
			tabbedPane1.setBackground(UIManager.getColor("Button.background"));

			//======== panel1 ========
			{
				panel1.setLayout(null);

				//======== panel3 ========
				{
					panel3.setBorder(new BevelBorder(BevelBorder.RAISED, new Color(0, 0, 153), null, null, new Color(0, 0, 102)));
					panel3.setLayout(null);

					//---- buttonGuardarClasificacion ----
					buttonGuardarClasificacion.setText("Guardar clasificacion");
					buttonGuardarClasificacion.setFont(new Font("Tahoma", Font.PLAIN, 12));
					buttonGuardarClasificacion.setForeground(new Color(0, 0, 102));
					buttonGuardarClasificacion.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							buttonGuardarClasificacionActionPerformed(e);
						}
					});
					panel3.add(buttonGuardarClasificacion);
					buttonGuardarClasificacion.setBounds(35, 85, 190, buttonGuardarClasificacion.getPreferredSize().height);

					//---- buttonContorno ----
					buttonContorno.setText("Clasificar");
					buttonContorno.setBackground(UIManager.getColor("Button.background"));
					buttonContorno.setFont(new Font("Tahoma", Font.PLAIN, 12));
					buttonContorno.setForeground(new Color(0, 0, 102));
					buttonContorno.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							buttonContornoActionPerformed(e);
						}
					});
					panel3.add(buttonContorno);
					buttonContorno.setBounds(35, 55, 190, buttonContorno.getPreferredSize().height);

					//---- button1 ----
					button1.setIcon(new ImageIcon("img\\c9.jpg"));
					panel3.add(button1);
					button1.setBounds(5, 115, 255, 40);

					//---- button2 ----
					button2.setIcon(new ImageIcon("img\\c9.jpg"));
					panel3.add(button2);
					button2.setBounds(5, 5, 255, 40);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for(int i = 0; i < panel3.getComponentCount(); i++) {
							Rectangle bounds = panel3.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
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
			tabbedPane1.addTab("Principal", panel1);


			//======== panel2 ========
			{
				panel2.setLayout(null);

				//======== fondoPanel ========
				{
					fondoPanel.setLayout(null);

					//======== panelH ========
					{
						panelH.setBorder(new TitledBorder(null, "H", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
						panelH.setLayout(null);

						//---- labelHMin ----
						labelHMin.setText("Min:");
						labelHMin.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelH.add(labelHMin);
						labelHMin.setBounds(10, 25, 35, labelHMin.getPreferredSize().height);

						//---- textFieldHMin ----
						textFieldHMin.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelH.add(textFieldHMin);
						textFieldHMin.setBounds(50, 20, 65, textFieldHMin.getPreferredSize().height);

						//---- labelHMax ----
						labelHMax.setText("Max:");
						labelHMax.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelH.add(labelHMax);
						labelHMax.setBounds(150, 25, 35, 14);

						//---- textFieldHMax ----
						textFieldHMax.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelH.add(textFieldHMax);
						textFieldHMax.setBounds(190, 20, 65, 20);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for(int i = 0; i < panelH.getComponentCount(); i++) {
								Rectangle bounds = panelH.getComponent(i).getBounds();
								preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
							}
							Insets insets = panelH.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							panelH.setMinimumSize(preferredSize);
							panelH.setPreferredSize(preferredSize);
						}
					}
					fondoPanel.add(panelH);
					panelH.setBounds(10, 15, 290, 55);

					//======== panelS ========
					{
						panelS.setBorder(new TitledBorder(null, "S", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
						panelS.setLayout(null);

						//---- labelSMin ----
						labelSMin.setText("Min:");
						labelSMin.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS.add(labelSMin);
						labelSMin.setBounds(10, 25, 35, labelSMin.getPreferredSize().height);

						//---- textFieldSMin ----
						textFieldSMin.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS.add(textFieldSMin);
						textFieldSMin.setBounds(50, 20, 65, textFieldSMin.getPreferredSize().height);

						//---- labelSMax ----
						labelSMax.setText("Max:");
						labelSMax.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS.add(labelSMax);
						labelSMax.setBounds(150, 25, 35, 14);

						//---- textFieldSMax ----
						textFieldSMax.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS.add(textFieldSMax);
						textFieldSMax.setBounds(190, 20, 65, 20);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for(int i = 0; i < panelS.getComponentCount(); i++) {
								Rectangle bounds = panelS.getComponent(i).getBounds();
								preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
							}
							Insets insets = panelS.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							panelS.setMinimumSize(preferredSize);
							panelS.setPreferredSize(preferredSize);
						}
					}
					fondoPanel.add(panelS);
					panelS.setBounds(10, 70, 290, 55);

					//======== panelS2 ========
					{
						panelS2.setBorder(new TitledBorder(null, "V", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
						panelS2.setLayout(null);

						//---- labelVSMin ----
						labelVSMin.setText("Min:");
						labelVSMin.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS2.add(labelVSMin);
						labelVSMin.setBounds(10, 25, 35, labelVSMin.getPreferredSize().height);

						//---- textFieldVMin ----
						textFieldVMin.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS2.add(textFieldVMin);
						textFieldVMin.setBounds(50, 20, 65, textFieldVMin.getPreferredSize().height);

						//---- labelVMax ----
						labelVMax.setText("Max:");
						labelVMax.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS2.add(labelVMax);
						labelVMax.setBounds(150, 25, 35, 14);

						//---- textFieldVMax ----
						textFieldVMax.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS2.add(textFieldVMax);
						textFieldVMax.setBounds(190, 20, 65, 20);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for(int i = 0; i < panelS2.getComponentCount(); i++) {
								Rectangle bounds = panelS2.getComponent(i).getBounds();
								preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
							}
							Insets insets = panelS2.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							panelS2.setMinimumSize(preferredSize);
							panelS2.setPreferredSize(preferredSize);
						}
					}
					fondoPanel.add(panelS2);
					panelS2.setBounds(10, 120, 290, 55);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for(int i = 0; i < fondoPanel.getComponentCount(); i++) {
							Rectangle bounds = fondoPanel.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
						}
						Insets insets = fondoPanel.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						fondoPanel.setMinimumSize(preferredSize);
						fondoPanel.setPreferredSize(preferredSize);
					}
				}
				panel2.add(fondoPanel);
				fondoPanel.setBounds(10, 5, 310, 180);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel2.getComponentCount(); i++) {
						Rectangle bounds = panel2.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel2.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel2.setMinimumSize(preferredSize);
					panel2.setPreferredSize(preferredSize);
				}
			}
			//======== panel7 ========
			{
				panel7.setLayout(null);

				//---- buttonBinarizar ----
				buttonBinarizar.setText("Binarizar");
				buttonBinarizar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonBinarizarActionPerformed(e);
					}
				});
				panel7.add(buttonBinarizar);
				buttonBinarizar.setBounds(30, 30, 270, buttonBinarizar.getPreferredSize().height);

				//---- buttonGradientMagnitude ----
				buttonGradientMagnitude.setText("GradientMagnitude");
				buttonGradientMagnitude.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonGradientMagnitudActionPerformed(e);
					}
				});
				panel7.add(buttonGradientMagnitude);
				buttonGradientMagnitude.setBounds(30, 55, 270, buttonGradientMagnitude.getPreferredSize().height);

				//---- buttonOpening ----
				buttonOpening.setText("Opening");
				buttonOpening.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonOpeningActionPerformed(e);
					}
				});
				panel7.add(buttonOpening);
				buttonOpening.setBounds(30, 80, 270, 23);

				//---- buttonClosing ----
				buttonClosing.setText("Closing");
				buttonClosing.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonClosingActionPerformed(e);
					}
				});
				panel7.add(buttonClosing);
				buttonClosing.setBounds(30, 105, 270, 23);

				//---- buttonErosion ----
				buttonErosion.setText("Erosi\u00f3n");
				buttonErosion.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonErosionActionPerformed(e);
					}
				});
				panel7.add(buttonErosion);
				buttonErosion.setBounds(30, 130, 270, buttonErosion.getPreferredSize().height);

				//---- buttonDilatacion ----
				buttonDilatacion.setText("Dilataci\u00f3n");
				buttonDilatacion.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonDilatacionActionPerformed(e);
					}
				});
				panel7.add(buttonDilatacion);
				buttonDilatacion.setBounds(30, 155, 270, 23);

				//---- buttonContornoActual ----
				buttonContornoActual.setText("Contorno Imagen Actual");
				buttonContornoActual.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonContornoActualActionPerformed(e);
					}
				});
				panel7.add(buttonContornoActual);
				buttonContornoActual.setBounds(30, 180, 270, 25);

				//---- buttonToGraySscale ----
				buttonToGraySscale.setText("Convertir a escala de grises");
				buttonToGraySscale.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonToGraySscaleActionPerformed(e);
					}
				});
				panel7.add(buttonToGraySscale);
				buttonToGraySscale.setBounds(30, 205, 270, 25);

				//---- buttonDetectarContorno2 ----
				buttonDetectarContorno2.setText("Detectar contorno grueso");
				buttonDetectarContorno2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonDetectarContorno2ActionPerformed(e);
					}
				});
				panel7.add(buttonDetectarContorno2);
				buttonDetectarContorno2.setBounds(30, 230, 270, 25);

				//---- buttonInvert ----
				buttonInvert.setText("Invertir");
				buttonInvert.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonInvertActionPerformed(e);
					}
				});
				panel7.add(buttonInvert);
				buttonInvert.setBounds(30, 255, 270, buttonInvert.getPreferredSize().height);

				//---- buttonSobel ----
				buttonSobel.setText("Sobel");
				buttonSobel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonSobelActionPerformed(e);
					}
				});
				panel7.add(buttonSobel);
				buttonSobel.setBounds(30, 280, 270, buttonSobel.getPreferredSize().height);

				//---- buttonEliminarFondo ----
				buttonEliminarFondo.setText("Eliminar Fondo");
				buttonEliminarFondo.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonEliminarFondoActionPerformed(e);
					}
				});
				panel7.add(buttonEliminarFondo);
				buttonEliminarFondo.setBounds(30, 305, 270, buttonEliminarFondo.getPreferredSize().height);

				//---- buttonSkeleton ----
				buttonSkeleton.setText("Esqueleto");
				buttonSkeleton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonSkeletonActionPerformed(e);
					}
				});
				panel7.add(buttonSkeleton);
				buttonSkeleton.setBounds(30, 330, 270, buttonSkeleton.getPreferredSize().height);

				//---- buttonRangoObjeto ----
				buttonRangoObjeto.setText("Rango objeto");
				buttonRangoObjeto.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonRangoObjetoActionPerformed(e);
					}
				});
				panel7.add(buttonRangoObjeto);
				buttonRangoObjeto.setBounds(30, 355, 270, buttonRangoObjeto.getPreferredSize().height);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel7.getComponentCount(); i++) {
						Rectangle bounds = panel7.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel7.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel7.setMinimumSize(preferredSize);
					panel7.setPreferredSize(preferredSize);
				}
			}
			tabbedPane1.addTab("Opciones", panel7);

		}
		add(tabbedPane1);
		tabbedPane1.setBounds(25, 25, 340, 345);

		tabbedPane1.addTab("Fondo", null, panel2, null);
		{ // compute preferred size
			Dimension preferredSize = new Dimension();
			for(int i = 0; i < getComponentCount(); i++) {
				Rectangle bounds = getComponent(i).getBounds();
				preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
				preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
			}
			Insets insets = getInsets();
			preferredSize.width += insets.right;
			preferredSize.height += insets.bottom;
			setMinimumSize(preferredSize);
			setPreferredSize(preferredSize);
		}
		// //GEN-END:initComponents
		
		fondoPanel.setBorder(new TitledBorder(null, "Fondo", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
			panel2.add(getButtonSeleccionarFondo(), null);
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
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
	private JPanel panelS2;
	private JLabel labelVSMin;
	private JTextField textFieldVMin;
	private JLabel labelVMax;
	private JTextField textFieldVMax;
	private JPanel panel7;
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

	/**
	 * This method initializes buttonSeleccionarFondo	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonSeleccionarFondo() {
		if (buttonSeleccionarFondo == null) {
			buttonSeleccionarFondo = new JButton();
			buttonSeleccionarFondo.setBounds(new Rectangle(15, 196, 303, 26));
			buttonSeleccionarFondo.setText("Seleccionar fondo desde imagen");
			buttonSeleccionarFondo.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					buttonSeleccionarFondoActionPerformed(e);
				}
			});
		}
		return buttonSeleccionarFondo;
	}
	private void buttonSeleccionarFondoActionPerformed(ActionEvent e) {
		//Raster raster = getImageHolder().getSelectedRectangle();
		//ColorModel colorModel = PlanarImage.createColorModel(raster.getSampleModel());
		//TiledImage image = new TiledImage(0, 0, raster.getWidth(), raster.getHeight(), 0, 0,raster.getSampleModel(), colorModel);
		// Set the data of the tiled image to be the raster.
		BufferedImage image = getImageHolder().getSelectedRectangle();
		//image.setData(raster);
		
		JFrame frame = new JFrame();
		frame.setTitle("Seleccion");
		// Get the JFrame's ContentPane.
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		// Create an instance of DisplayJAI.
		DisplayJAI dj = new DisplayJAI(image);
		// Add to the JFrame's ContentPane an instance of JScrollPane containing the
		// DisplayJAI instance.
		contentPane.add(new JScrollPane(dj),BorderLayout.CENTER);
		// Add a text label with the image information.
		// Set the closing operation so the application is finished.
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		//frame.setSize(image.getWidth()+20,image.getHeight()+20); // adjust the frame size.
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
	
}
