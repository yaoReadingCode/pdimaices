/*
 * Created by JFormDesigner on Mon Jan 31 22:55:17 ART 2011
 */

package aplicarFiltros;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import procesamiento.clasificacion.Clasificador;

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
	
	private Map<String,Integer> datasetCountModel = new HashMap<String, Integer>();  //  @jve:decl-index=0:
	private Map<String,Long> datasetPixelModel = new HashMap<String, Long>();
	
	private Integer totalObjetos = 0;  //  @jve:decl-index=0:
	
	private Clasificador clasificador;
	
	public JFrame getContenedor() {
		return contenedor;
	}
	public void setContenedor(JFrame contenedor) {
		this.contenedor = contenedor;
	}

	public Clasificador getClasificador() {
		return clasificador;
	}
	public void setClasificador(Clasificador clasificador) {
		this.clasificador = clasificador;
	}

	private JFrame contenedor;

	public void initDataSetModels(){
		totalObjetos = 0;
		datasetCountModel.clear();
		datasetPixelModel.clear();
		datasetCount.clear();
		datasetPixel.clear();
	}
	public void addValueCount(String nombre, int cantidad){
		Integer count = cantidad;
		if (datasetCountModel.containsKey(nombre)){
			Integer cantidaParcial = datasetCountModel.get(nombre);
			count += cantidaParcial;
		}
		totalObjetos += cantidad;
		datasetCountModel.put(nombre, count);

		
	}
	
	public void addValuePixel(String nombre, Long cantidadPixeles){
		Long count = cantidadPixeles;
		if (datasetPixelModel.containsKey(nombre)){
			Long cantidaParcial = datasetPixelModel.get(nombre);
			count += cantidaParcial;
		}
		datasetPixelModel.put(nombre, count);
	}
	
	public void actualizarDataSetCount(){
		DefaultTableModel model = (DefaultTableModel) tableRasgos2.getModel();
		DecimalFormat formater = new DecimalFormat("0.00");

		for(String agrupador: datasetCountModel.keySet()){
			Integer cantidad = datasetCountModel.get(agrupador);
			Double porcentaje = 0.0;
			if (totalObjetos != 0)
				porcentaje = (cantidad * 100)/ (double) totalObjetos;
			model.addRow(new Object[]{agrupador, cantidad, formater.format(porcentaje)+"%"});
			//Grafico
			datasetCount.setValue( agrupador,cantidad);
		}
		
		int clasificadosIncorrectamente = getClasificador().getClasificadosIncorrectamente().size();
		int clasificadosCorrectamente = totalObjetos - clasificadosIncorrectamente;
		Double porcentajeCorrectamente = 0.0;
		Double porcentajeIncorrectamente = 0.0;
		if (totalObjetos != 0){
			porcentajeCorrectamente = (clasificadosCorrectamente * 100)/ (double) totalObjetos;
			porcentajeIncorrectamente = (clasificadosIncorrectamente * 100)/ (double) totalObjetos;
		}
		model.addRow(new Object[]{"Clasificados Correctamente", clasificadosCorrectamente, formater.format(porcentajeCorrectamente)+"%"});
		model.addRow(new Object[]{"Clasificados Incorrectamente", clasificadosIncorrectamente, formater.format(porcentajeIncorrectamente)+"%"});
	}

	public void actualizarDataSetPixel(){
		for(String agrupador: datasetPixelModel.keySet()){
			Long cantidad = datasetPixelModel.get(agrupador);
			//Grafico
			datasetPixel.setValue( agrupador,cantidad);
		}
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
	    //panel.setPreferredSize(new java.awt.Dimension(500, 250));
	    panel.setSize(new java.awt.Dimension(500, 250));
	    this.setPanelGrafico(content);
	    panelGrafico.setPreferredSize(new java.awt.Dimension(500, 180));
	    
	    
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
	    //panelPixel.setPreferredSize(new java.awt.Dimension(500, 250));
	    panelGraficoPixel.setPreferredSize(new java.awt.Dimension(500, 180));
	   
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
	
	
	public PanelResultado(Clasificador clasificador) {
		this.clasificador = clasificador;
		initComponents();
		
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - oscar giorgetti
		DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
		panel2 = new JPanel();
		panel1 = new JPanel();
		scrollPaneRasgos2 = new JScrollPane();
		tableRasgos2 = new JTable();
		button1 = new JButton();
		separator1 = compFactory.createSeparator("Clasificaci\u00f3n");
		panel3 = new JPanel();
		panelGrafico = new JPanel();
		separator2 = new JSeparator();
		panelGraficoPixel = new JPanel();

		//======== this ========

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

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
			panel1.setBounds(5, 5, 670, 180);

			//======== panel3 ========
			{
				panel3.setBorder(new TitledBorder("Gr\u00e1ficos"));
				panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));

				//======== panelGrafico ========
				{
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
				panel3.add(separator2);

				//======== panelGraficoPixel ========
				{
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
				panel3.add(panelGraficoPixel);
			}
			panel2.add(panel3);
			panel3.setBounds(5, 190, 670, 350);

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
		panel2.setBounds(5, 5, 680, 550);

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
	// Generated using JFormDesigner Evaluation license - oscar giorgetti
	private JPanel panel2;
	private JPanel panel1;
	private JScrollPane scrollPaneRasgos2;
	private JTable tableRasgos2;
	private JButton button1;
	private JComponent separator1;
	private JPanel panel3;
	private JPanel panelGrafico;
	private JSeparator separator2;
	private JPanel panelGraficoPixel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	private JPanel panel = new JPanel();
	//private JPanel panelGraficoPixel = new JPanel();
	
	private void agregarGrafico(){
		//======== panel3 ========
			
			panel.setLayout(null);
			//panel.add(separator2);
			//separator2.setBounds(10, 5, 650, separator2.getPreferredSize().height);

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
