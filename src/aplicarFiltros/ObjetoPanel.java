/*
 * Created by JFormDesigner on Sun Dec 05 20:47:07 GMT-03:00 2010
 */

package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import objeto.Objeto;
import objeto.ObjetoUtil;
import procesamiento.clasificacion.Clasificador;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * @author User #3
 */
public class ObjetoPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Objeto objeto = null;  //  @jve:decl-index=0:
	private Clasificador clasificador = null;  //  @jve:decl-index=0:
	private ClasePanel contenedor = null;
 	public ObjetoPanel(Objeto objeto, int nroPanel, Clasificador clasificador, ClasePanel contenedor) {
		initComponents();
		this.contenedor = contenedor;
		
		this.objeto = objeto;
		this.clasificador = clasificador;
		PlanarImage image = JAI.create("fileload", objeto.getPathImage());
		DisplayJAI dd = new DisplayJAI(image);
		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setPreferredSize(new Dimension(ObjetoUtil.DEFAULT_IMAGE_WIDTH,ObjetoUtil.DEFAULT_IMAGE_HEIGHT));
		scrollPanel.setViewportView(dd);
		this.panelImagen.add(new DisplayJAI(image),BorderLayout.CENTER);
		
		this.labelNombre.setText(objeto.getName());
		this.labelNro.setText(Integer.toString(nroPanel));
		this.checkSelected.setSelected(this.objeto.isSelected());
	}
 	
	@Override
	public void repaint() {
		super.repaint();
		if (checkSelected != null && getObjeto() != null)
			checkSelected.setSelected(getObjeto().isSelected());
	}

	public Objeto getObjeto() {
		return objeto;
	}

	public void setObjeto(Objeto objeto) {
		this.objeto = objeto;
	}

	public Clasificador getClasificador() {
		return clasificador;
	}

	public void setClasificador(Clasificador clasificador) {
		this.clasificador = clasificador;
	}
	
	private void panelImagenMouseClicked(MouseEvent e) {
		this.contenedor.getContenedor().presentarResultados(objeto);
	}

	/**
	 * Cambia el valor selected del objeto
	 * @param e
	 */
	private void toggleSelectItem(ActionEvent e) {
		if (checkSelected.isSelected())
			this.contenedor.agregarSeleccionado(getObjeto());
		else
			this.contenedor.eliminarSeleccionado(getObjeto());
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Sebastian Colavita
		panelTop = new JPanel();
		panelLabel = new JPanel();
		panelCheckBox = new JPanel();
		labelNro = new JLabel();
		labelNombre = new JLabel();
		panelImagen = new JPanel();
		panelCenter = new JPanel();
		//comboBoxClase = new JComboBox();
		checkSelected = new JCheckBox();

		//======== this ========
		setBorder(new LineBorder(new Color(153, 204, 255)));

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		setLayout(new BorderLayout());

		//======== panel1 ========
		{
			panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.X_AXIS));
			panelLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
			panelCheckBox.setLayout(new FlowLayout(FlowLayout.RIGHT));
			panelTop.add(panelLabel);
			panelTop.add(panelCheckBox);

			//---- labelNro ----
			labelNro.setText("1");
			panelLabel.add(labelNro);

			//---- labelNombre ----
			labelNombre.setText("Nombre");
			labelNombre.setFont(labelNombre.getFont().deriveFont(labelNombre.getFont().getStyle() | Font.BOLD));
			panelLabel.add(labelNombre);
			
			checkSelected.setToolTipText("Seleccionar");
			checkSelected.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					toggleSelectItem(e);
				}
			});
			panelCheckBox.add(checkSelected);
		}
		add(panelTop, BorderLayout.NORTH);

		//======== panelImagen ========
		{
			panelImagen.setBorder(UIManager.getBorder("ProgressBar.border"));
			panelImagen.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					panelImagenMouseClicked(e);
				}
			});
			panelImagen.setLayout(new BoxLayout(panelImagen, BoxLayout.X_AXIS));
		}
		add(panelImagen, BorderLayout.CENTER);

		//======== panel2 ========
		{
			panelCenter.setLayout(new FlowLayout(FlowLayout.LEFT));
		}
		add(panelCenter, BorderLayout.SOUTH);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Sebastian Colavita
	private JPanel panelTop;
	private JPanel panelLabel;
	private JPanel panelCheckBox;
	private JLabel labelNro;
	private JLabel labelNombre;
	private JPanel panelImagen;
	private JPanel panelCenter;
	private JCheckBox checkSelected;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
