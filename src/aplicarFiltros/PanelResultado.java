/*
 * Created by JFormDesigner on Mon Jan 31 22:55:17 ART 2011
 */

package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.jgoodies.forms.factories.DefaultComponentFactory;

/**
 * @author seba cola
 */
public class PanelResultado extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultPieDataset datasetCount = new DefaultPieDataset();
	private DefaultPieDataset datasetPixel = new DefaultPieDataset();
	public JFrame getContenedor() {
		return contenedor;
	}
	public void setContenedor(JFrame contenedor) {
		this.contenedor = contenedor;
	}

	private JFrame contenedor;
	public void addValueCount(String nombre, int cantidad, float porcentaje){
		DefaultTableModel model = (DefaultTableModel) tableRasgos2.getModel();
		model.addRow(new Object[]{nombre, cantidad, (porcentaje+"%")});
		//Grafico
		datasetCount.setValue( nombre,cantidad);
	}
	
	public void addValuePixel(String nombre, long cantidadPixeles){
		//Grafico
		datasetPixel.setValue( nombre,cantidadPixeles);
	}
	
	
	public void graficar(){
		//Grafico de cantidad de semillas
		JFreeChart chart = ChartFactory.createPieChart("Clasificación Cantidad de Semillas", datasetCount, true,  true,
	            false); 
		chart.setBackgroundPaint(Color.ORANGE);
	    PiePlot plot = (PiePlot)chart.getPlot();
	    //Color de las etiquetas
	    plot.setLabelBackgroundPaint(Color.ORANGE);
	    //Color de el fondo del gráfico
	    plot.setBackgroundPaint(Color.WHITE);
	    plot.setNoDataMessage("No hay data");
		
	    
	    ChartPanel panel = new ChartPanel(chart);
	    final JPanel content = new JPanel(new BorderLayout());
	    content.add(panel);
	    panel.setPreferredSize(new java.awt.Dimension(500, 180));
	    this.setPanelGrafico(content);
	    
	    
	  //Grafico de cantidad de Pixeles
		JFreeChart chartPixel = ChartFactory.createPieChart("Clasificación por Volumen", datasetPixel, true,  true,
	            false); 
		chartPixel.setBackgroundPaint(Color.ORANGE);
	    PiePlot plotPixel = (PiePlot)chartPixel.getPlot();
	    //Color de las etiquetas
	    plotPixel.setLabelBackgroundPaint(Color.ORANGE);
	    //Color de el fondo del gráfico
	    plotPixel.setBackgroundPaint(Color.WHITE);
	    plotPixel.setNoDataMessage("No hay data");
		
	    
	    ChartPanel panelPixel = new ChartPanel(chartPixel);
	    final JPanel contentPixel = new JPanel(new BorderLayout());
	    contentPixel.add(panelPixel);
	    panelPixel.setPreferredSize(new java.awt.Dimension(500, 180));
	   
	    this.panelGraficoPixel.setLayout(new BorderLayout());
		this.panelGraficoPixel.add(contentPixel);
		contentPixel.setVisible(true);
		this.panelGraficoPixel.setVisible(true);
	}
	public void setPanelGrafico(Component arg0){
		this.panelGrafico.setLayout(new BorderLayout());
		this.panelGrafico.add(arg0);
		arg0.setVisible(true);
		this.panelGrafico.setVisible(true);
	}
	
	
	public PanelResultado() {
		initComponents();
		
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
		DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
		panel2 = new JPanel();
		panel1 = new JPanel();
		scrollPaneRasgos2 = new JScrollPane();
		tableRasgos2 = new JTable();
		button1 = new JButton();
		separator1 = compFactory.createSeparator("Clasificaci\u00f3n");
		panel3 = new JPanel();
		separator2 = compFactory.createSeparator("Grafico");
		panelGrafico = new JPanel();

		//======== this ========

		// JFormDesigner evaluation mark
/*		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});
*/
		setLayout(null);

		//======== panel2 ========
		{
			panel2.setBackground(new Color(51, 0, 255));
			panel2.setLayout(null);

			//======== panel1 ========
			{
				panel1.setBorder(new BevelBorder(BevelBorder.RAISED));
				panel1.setLayout(null);

				//======== scrollPaneRasgos2 ========
				{

					//---- tableRasgos2 ----
					tableRasgos2.setModel(new DefaultTableModel(
						new Object[][] {
						},
						new String[] {
							"Clasificaciones", "Cantidad de Objetos", "Porcentaje"
						}
					) {
						Class<?>[] columnTypes = new Class<?>[] {
							String.class, String.class, Object.class
						};
						@Override
						public Class<?> getColumnClass(int columnIndex) {
							return columnTypes[columnIndex];
						}
					});
					tableRasgos2.setPreferredScrollableViewportSize(new Dimension(200, 100));
					tableRasgos2.setBackground(UIManager.getColor("RadioButton.light"));
					tableRasgos2.setCellSelectionEnabled(true);
					scrollPaneRasgos2.setViewportView(tableRasgos2);
				}
				panel1.add(scrollPaneRasgos2);
				scrollPaneRasgos2.setBounds(10, 25, 375, 145);

				//---- button1 ----
				button1.setIcon(new ImageIcon("\\\\img\\\\maiz_mon810_al.jpg"));
				panel1.add(button1);
				button1.setBounds(390, 25, 270, 145);
				panel1.add(separator1);
				separator1.setBounds(10, 5, 650, separator1.getPreferredSize().height);

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
			panel2.add(panel1);
			panel1.setBounds(5, 10, 670, 180);

			//======== panel3 ========
			{
				panel3.setLayout(null);
				panel3.add(separator2);
				separator2.setBounds(10, 5, 650, separator2.getPreferredSize().height);

				//======== panelGrafico ========
				{
					panelGrafico.setBorder(new BevelBorder(BevelBorder.LOWERED));
					panelGrafico.setLayout(null);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for(int i = 0; i < panelGrafico.getComponentCount(); i++) {
							Rectangle bounds = panelGrafico.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
						}
						Insets insets = panelGrafico.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panelGrafico.setMinimumSize(preferredSize);
						panelGrafico.setPreferredSize(preferredSize);
					}
				}
				panel3.add(panelGrafico);
				panelGrafico.setBounds(10, 25, 650, 180);

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
			panel2.add(panel3);
			panel3.setBounds(5, 200, 670, 225);
			this.agregarGrafico();

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
		add(panel2);
		panel2.setBounds(5, 5, 680, 685);

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
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
		
		button1.setIcon(new ImageIcon("img\\maiz_mon810_al.jpg"));
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Oscar Giorgetti
	private JPanel panel2;
	private JPanel panel1;
	private JScrollPane scrollPaneRasgos2;
	private JTable tableRasgos2;
	private JButton button1;
	private JComponent separator1;
	private JPanel panel3;
	private JComponent separator2;
	private JPanel panelGrafico;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	private JPanel panel = new JPanel();
	private JPanel panelGraficoPixel = new JPanel();
	
	private void agregarGrafico(){
		//======== panel3 ========
			
			panel.setLayout(null);
			panel.add(separator2);
			separator2.setBounds(10, 5, 650, separator2.getPreferredSize().height);

			//======== panelGrafico ========
			{
				panelGraficoPixel.setBorder(new BevelBorder(BevelBorder.LOWERED));
				panelGraficoPixel.setLayout(null);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panelGraficoPixel.getComponentCount(); i++) {
						Rectangle bounds = panelGraficoPixel.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panelGraficoPixel.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panelGraficoPixel.setMinimumSize(preferredSize);
					panelGraficoPixel.setPreferredSize(preferredSize);
				}
			}
			panel.add(panelGraficoPixel);
			panelGraficoPixel.setBounds(10, 25, 650, 180);

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for(int i = 0; i < panel.getComponentCount(); i++) {
					Rectangle bounds = panel.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				Insets insets = panel.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				panel.setMinimumSize(preferredSize);
				panel.setPreferredSize(preferredSize);
			}

		panel2.add(panel);
		panel.setBounds(5, 435, 670, 225);
	}
}
