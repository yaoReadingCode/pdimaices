/*
 * Created by JFormDesigner on Tue Sep 22 21:27:21 GMT 2009
 */

package aplicarFiltros;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.media.jai.PlanarImage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.hibernate.exception.JDBCConnectionException;

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
	            	getImageHolder().setImage(command.execute());
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
		if (textFieldHMin2.getText() != null
				&& !textFieldHMin2.getText().trim().equals(""))
			range.setHMin(Float.parseFloat(textFieldHMin2.getText()));
		if (textFieldHMax2.getText() != null
				&& !textFieldHMax2.getText().trim().equals(""))
			range.setHMax(Float.parseFloat(textFieldHMax2.getText()));
		if (textFieldSMin2.getText() != null
				&& !textFieldSMin2.getText().trim().equals(""))
			range.setSMin(Float.parseFloat(textFieldSMin2.getText()));
		if (textFieldSMax2.getText() != null
				&& !textFieldSMax2.getText().trim().equals(""))
			range.setSMax(Float.parseFloat(textFieldSMax2.getText()));
		if (textFieldVMin2.getText() != null
				&& !textFieldVMin2.getText().trim().equals(""))
			range.setVMin(Float.parseFloat(textFieldVMin2.getText()));
		if (textFieldVMax2.getText() != null
				&& !textFieldVMax2.getText().trim().equals(""))
			range.setVMax(Float.parseFloat(textFieldVMax2.getText()));
		return range;
	}
	
	/**
	 * Setea el rango HSV del objeto
	 * @return
	 */
	private void setHSVRangeObjeto(HSVRange range) {
		if (range != null){
			textFieldHMin2.setText((range.getHMin()!= null) ? range.getHMin().toString() : null );
			textFieldHMax2.setText((range.getHMax()!= null) ? range.getHMax().toString() : null );
			textFieldSMin2.setText((range.getSMin()!= null) ? range.getSMin().toString() : null );
			textFieldSMax2.setText((range.getSMax()!= null) ? range.getSMax().toString() : null );
			textFieldVMin2.setText((range.getVMin()!= null) ? range.getVMin().toString() : null );
			textFieldVMax2.setText((range.getVMax()!= null) ? range.getVMax().toString() : null );
		}
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
		tabbedPane1 = new JTabbedPane();
		panel1 = new JPanel();
		buttonContorno = new JButton();
		buttonGuardarClasificacion = new JButton();
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
		objetoPanel = new JPanel();
		panelH2 = new JPanel();
		labelHMin2 = new JLabel();
		textFieldHMin2 = new JTextField();
		labelHMax2 = new JLabel();
		textFieldHMax2 = new JTextField();
		panelS3 = new JPanel();
		labelSMin2 = new JLabel();
		textFieldSMin2 = new JTextField();
		labelSMax2 = new JLabel();
		textFieldSMax2 = new JTextField();
		panelS4 = new JPanel();
		labelVSMin2 = new JLabel();
		textFieldVMin2 = new JTextField();
		labelVMax2 = new JLabel();
		textFieldVMax2 = new JTextField();
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
		setLayout(null);

		//======== tabbedPane1 ========
		{

			//======== panel1 ========
			{
				panel1.setLayout(null);

				//---- buttonContorno ----
				buttonContorno.setText("Clasificar");
				buttonContorno.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonContornoActionPerformed(e);
					}
				});
				panel1.add(buttonContorno);
				buttonContorno.setBounds(25, 75, 270, buttonContorno.getPreferredSize().height);

				//---- buttonGuardarClasificacion ----
				buttonGuardarClasificacion.setText("Guardar clasificacion");
				buttonGuardarClasificacion.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonGuardarClasificacionActionPerformed(e);
					}
				});
				panel1.add(buttonGuardarClasificacion);
				buttonGuardarClasificacion.setBounds(25, 120, 270, buttonGuardarClasificacion.getPreferredSize().height);

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
					panel1.setPreferredSize( preferredSize );
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
							panelH.setPreferredSize( preferredSize );
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
							panelS.setPreferredSize( preferredSize );
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
							panelS2.setPreferredSize( preferredSize );
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
						fondoPanel.setPreferredSize( preferredSize );
					}
				}
				panel2.add(fondoPanel);
				fondoPanel.setBounds(10, 5, 310, 180);

				//======== objetoPanel ========
				{
					objetoPanel.setLayout(null);

					//======== panelH2 ========
					{
						panelH2.setBorder(new TitledBorder(null, "H", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
						panelH2.setLayout(null);

						//---- labelHMin2 ----
						labelHMin2.setText("Min:");
						labelHMin2.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelH2.add(labelHMin2);
						labelHMin2.setBounds(10, 25, 35, labelHMin2.getPreferredSize().height);

						//---- textFieldHMin2 ----
						textFieldHMin2.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelH2.add(textFieldHMin2);
						textFieldHMin2.setBounds(50, 20, 65, textFieldHMin2.getPreferredSize().height);

						//---- labelHMax2 ----
						labelHMax2.setText("Max:");
						labelHMax2.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelH2.add(labelHMax2);
						labelHMax2.setBounds(150, 25, 35, 14);

						//---- textFieldHMax2 ----
						textFieldHMax2.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelH2.add(textFieldHMax2);
						textFieldHMax2.setBounds(190, 20, 65, 20);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for(int i = 0; i < panelH2.getComponentCount(); i++) {
								Rectangle bounds = panelH2.getComponent(i).getBounds();
								preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
							}
							Insets insets = panelH2.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							panelH2.setPreferredSize( preferredSize );
						}
					}
					objetoPanel.add(panelH2);
					panelH2.setBounds(10, 20, 290, 55);

					//======== panelS3 ========
					{
						panelS3.setBorder(new TitledBorder(null, "S", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
						panelS3.setLayout(null);

						//---- labelSMin2 ----
						labelSMin2.setText("Min:");
						labelSMin2.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS3.add(labelSMin2);
						labelSMin2.setBounds(10, 25, 35, labelSMin2.getPreferredSize().height);

						//---- textFieldSMin2 ----
						textFieldSMin2.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS3.add(textFieldSMin2);
						textFieldSMin2.setBounds(50, 20, 65, textFieldSMin2.getPreferredSize().height);

						//---- labelSMax2 ----
						labelSMax2.setText("Max:");
						labelSMax2.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS3.add(labelSMax2);
						labelSMax2.setBounds(150, 25, 35, 14);

						//---- textFieldSMax2 ----
						textFieldSMax2.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS3.add(textFieldSMax2);
						textFieldSMax2.setBounds(190, 20, 65, 20);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for(int i = 0; i < panelS3.getComponentCount(); i++) {
								Rectangle bounds = panelS3.getComponent(i).getBounds();
								preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
							}
							Insets insets = panelS3.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							panelS3.setPreferredSize( preferredSize );
						}
					}
					objetoPanel.add(panelS3);
					panelS3.setBounds(10, 75, 290, 55);

					//======== panelS4 ========
					{
						panelS4.setBorder(new TitledBorder(null, "V", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
						panelS4.setLayout(null);

						//---- labelVSMin2 ----
						labelVSMin2.setText("Min:");
						labelVSMin2.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS4.add(labelVSMin2);
						labelVSMin2.setBounds(10, 25, 35, labelVSMin2.getPreferredSize().height);

						//---- textFieldVMin2 ----
						textFieldVMin2.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS4.add(textFieldVMin2);
						textFieldVMin2.setBounds(50, 20, 65, textFieldVMin2.getPreferredSize().height);

						//---- labelVMax2 ----
						labelVMax2.setText("Max:");
						labelVMax2.setFont(new Font("Tahoma", Font.BOLD, 11));
						panelS4.add(labelVMax2);
						labelVMax2.setBounds(150, 25, 35, 14);

						//---- textFieldVMax2 ----
						textFieldVMax2.addKeyListener(new KeyAdapter() {
							@Override
							public void keyTyped(KeyEvent e) {
								textFieldIntegerKeyTyped(e);
							}
						});
						panelS4.add(textFieldVMax2);
						textFieldVMax2.setBounds(190, 20, 65, 20);

						{ // compute preferred size
							Dimension preferredSize = new Dimension();
							for(int i = 0; i < panelS4.getComponentCount(); i++) {
								Rectangle bounds = panelS4.getComponent(i).getBounds();
								preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
								preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
							}
							Insets insets = panelS4.getInsets();
							preferredSize.width += insets.right;
							preferredSize.height += insets.bottom;
							panelS4.setPreferredSize( preferredSize );
						}
					}
					objetoPanel.add(panelS4);
					panelS4.setBounds(10, 130, 290, 55);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for(int i = 0; i < objetoPanel.getComponentCount(); i++) {
							Rectangle bounds = objetoPanel.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
						}
						Insets insets = objetoPanel.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						objetoPanel.setPreferredSize( preferredSize );
					}
				}
				panel2.add(objetoPanel);
				objetoPanel.setBounds(10, 190, 310, 190);

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
					panel2.setPreferredSize( preferredSize );
				}
			}
			tabbedPane1.addTab("Fonfo", panel2);


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
					panel7.setPreferredSize( preferredSize );
				}
			}
			tabbedPane1.addTab("Opciones", panel7);

		}
		add(tabbedPane1);
		tabbedPane1.setBounds(20, 25, 340, 420);

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
			setPreferredSize( preferredSize );
		}
		// //GEN-END:initComponents
		
		fondoPanel.setBorder(new TitledBorder(null, "Fondo", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
		objetoPanel.setBorder(new TitledBorder(null, "Objeto", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JTabbedPane tabbedPane1;
	private JPanel panel1;
	private JButton buttonContorno;
	private JButton buttonGuardarClasificacion;
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
	private JPanel objetoPanel;
	private JPanel panelH2;
	private JLabel labelHMin2;
	private JTextField textFieldHMin2;
	private JLabel labelHMax2;
	private JTextField textFieldHMax2;
	private JPanel panelS3;
	private JLabel labelSMin2;
	private JTextField textFieldSMin2;
	private JLabel labelSMax2;
	private JTextField textFieldSMax2;
	private JPanel panelS4;
	private JLabel labelVSMin2;
	private JTextField textFieldVMin2;
	private JLabel labelVMax2;
	private JTextField textFieldVMax2;
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
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
