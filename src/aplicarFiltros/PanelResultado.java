/*
 * Created by JFormDesigner on Mon Jan 31 22:55:17 ART 2011
 */

package aplicarFiltros;

import java.awt.*;
import java.awt.event.*;
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

import objeto.RubroCalidad;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import procesamiento.Norma;
import procesamiento.Rebaja;
import procesamiento.Standar;
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
	
	private Map<String,Float> datasetCountPorc = new HashMap<String, Float>(); //  @jve:decl-index=0:
	
	private Map<RubroCalidad,Agrupador> datasetPixelModel = new HashMap<RubroCalidad, Agrupador>();  //  @jve:decl-index=0:
	
	private long totalPixeles = 0;  //  @jve:decl-index=0:
	
	private Clasificador clasificador;  //  @jve:decl-index=0:
	
	private Double pesoHectolitrico = 0.0;  //  @jve:decl-index=0:
	
	private Double humedad = 0.0;
	
	
	public Double getPesoHectolitrico() {
		return pesoHectolitrico;
	}
	public void setPesoHectolitrico(Double pesoHectolitrico) {
		this.pesoHectolitrico = pesoHectolitrico;
	}
	public Double getHumedad() {
		return humedad;
	}
	public void setHumedad(Double humedad) {
		this.humedad = humedad;
	}
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
		totalPixeles = 0;
		datasetPixelModel.clear();
		datasetCount.clear();
		datasetPixel.clear();
	}
		
	public void addValuePixel(RubroCalidad rubro, Double cantidadPixeles, Double cantidadObjetos){
		Agrupador cantidaParcial = null;
		if (datasetPixelModel.containsKey(rubro)){
			cantidaParcial = datasetPixelModel.get(rubro);
			Double area = cantidaParcial.getArea() + cantidadPixeles;
			cantidaParcial.setArea(area);
			Double cantidad = cantidaParcial.getCantidad() + cantidadObjetos;
			cantidaParcial.setCantidad(cantidad);
		}
		else{
			cantidaParcial = new Agrupador();
			cantidaParcial.setNombre(rubro.getDescripcion());
			cantidaParcial.setArea(cantidadPixeles);
			cantidaParcial.setCantidad(cantidadObjetos);
		}
		datasetPixelModel.put(rubro, cantidaParcial);
		totalPixeles += cantidadPixeles;
	}
	
	public void actualizarDataSetCount(){
		Standar stan = new Standar(getClasificador());
		DefaultTableModel model = (DefaultTableModel) tableRasgos2.getModel();
		DecimalFormat formater = new DecimalFormat("0.00");
		DecimalFormat formaterInteger = new DecimalFormat("0");

		for(RubroCalidad agrupador: datasetPixelModel.keySet()){
			Agrupador agrupadorClase = datasetPixelModel.get(agrupador);
			Double porcentaje = 0.0;
			if (totalPixeles != 0)
				porcentaje = (agrupadorClase.getArea() * 100f)/ totalPixeles;
			agrupadorClase.setPorcentaje(porcentaje);
			model.addRow(new Object[]{agrupador.getDescripcion(), formaterInteger.format(agrupadorClase.getCantidad()), formater.format(porcentaje)+"%"});
			//datasetCountPorc.put(agrupador, porcentaje.floatValue());
			//Grafico
			datasetCount.setValue( agrupador.getDescripcion(),agrupadorClase.getArea());
		}
		
		int clasificadosIncorrectamente = getClasificador().getClasificadosIncorrectamente().size();
		int cantidadObjetos = getClasificador().countObject() - 1; // Cantidad de objetos menos el objeto referencia
		long clasificadosCorrectamente = cantidadObjetos - clasificadosIncorrectamente;
		Double porcentajeCorrectamente = 0.0;
		Double porcentajeIncorrectamente = 0.0;
		if (cantidadObjetos != 0){
			porcentajeCorrectamente = (clasificadosCorrectamente * 100)/ (double) cantidadObjetos;
			porcentajeIncorrectamente = (clasificadosIncorrectamente * 100)/ (double) cantidadObjetos;
		}
		
		model.addRow(new Object[]{"Clasificados Correctamente", clasificadosCorrectamente, formater.format(porcentajeCorrectamente)+"%"});
		model.addRow(new Object[]{"Clasificados Incorrectamente", clasificadosIncorrectamente, formater.format(porcentajeIncorrectamente)+"%"});
		
		RubroCalidad humedad = getClasificador().getRubroCalidad(Clasificador.RUBRO_CALIDAD_HUMEDAD);
		RubroCalidad pesoHectolitrico = getClasificador().getRubroCalidad(Clasificador.RUBRO_CALIDAD_PESO_HECTOLITRICO);
		datasetPixelModel.put(pesoHectolitrico,new Agrupador(pesoHectolitrico.getDescripcion(), 0.0, 0.0,this.pesoHectolitrico, false));
		datasetPixelModel.put(humedad,new Agrupador(humedad.getDescripcion(), 0.0, 0.0,this.humedad, false));

		Rebaja rebaja = stan.getNorma(datasetPixelModel);
		Norma norma = rebaja.getNorma();
		this.resultado.setText(norma.getName());
		this.Descuento.setText(rebaja.getDescuento()+"%");
	}

	public void actualizarDataSetPixel(){
		for(RubroCalidad agrupador: datasetPixelModel.keySet()){
			Agrupador agrupadorClase = datasetPixelModel.get(agrupador);
			if (agrupadorClase.isGraficar())
				//Grafico
				datasetPixel.setValue( agrupador.getDescripcion(),agrupadorClase.getArea());
		}
	}
	
	public void graficar(){
		   
	  //Grafico de cantidad de Pixeles
		JFreeChart chartPixel = ChartFactory.createPieChart("Clasificación", datasetPixel, true,  true,
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
		
	public PanelResultado(Clasificador clasificador) {
		this.clasificador = clasificador;
		initComponents();
		
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Sebastian Colavita
		DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
		panel2 = new JPanel();
		panel1 = new JPanel();
		scrollPaneRasgos2 = new JScrollPane();
		tableRasgos2 = new JTable();
		button1 = new JButton();
		separator1 = compFactory.createSeparator("Clasificaci\u00f3n");
		label1 = new JLabel();
		resultado = new JLabel();
		label2 = new JLabel();
		Descuento = new JLabel();
		panel3 = new JPanel();
		panelGraficoPixel = new JPanel();

		//======== this ========

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

		setLayout(null);

		//======== panel2 ========
		{
			panel2.setBackground(Color.blue);
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
							"Clasificaciones", "Cantidad", "Porcentaje Area"
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
				scrollPaneRasgos2.setBounds(10, 60, 605, 155);

				//---- button1 ----
				button1.setIcon(new ImageIcon("\\\\img\\\\maiz_mon810_al.jpg"));
				panel1.add(button1);
				button1.setBounds(620, 60, 225, 155);
				panel1.add(separator1);
				separator1.setBounds(10, 5, 835, separator1.getPreferredSize().height);

				//---- label1 ----
				label1.setText("Resultado:");
				label1.setFont(new Font("Times New Roman", Font.BOLD, 13));
				panel1.add(label1);
				label1.setBounds(10, 35, 67, label1.getPreferredSize().height);

				//---- resultado ----
				resultado.setText("Grado A");
				resultado.setFont(new Font("Times New Roman", Font.BOLD, 13));
				resultado.setForeground(Color.blue);
				panel1.add(resultado);
				resultado.setBounds(79, 35, 171, 19);

				//---- label2 ----
				label2.setText(" Descuento:");
				label2.setFont(new Font("Times New Roman", Font.BOLD, 13));
				panel1.add(label2);
				label2.setBounds(250, 35, 72, 19);

				//---- Descuento ----
				Descuento.setText("10%");
				Descuento.setFont(new Font("Times New Roman", Font.BOLD, 13));
				Descuento.setForeground(Color.blue);
				panel1.add(Descuento);
				Descuento.setBounds(320, 35, 60, Descuento.getPreferredSize().height);

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
			panel1.setBounds(15, 10, 856, 225);

			//======== panel3 ========
			{
				panel3.setBorder(new TitledBorder("Gr\u00e1ficos"));
				panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));

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
			panel3.setBounds(13, 245, 857, 355);

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
		panel2.setBounds(10, 5, 883, 610);

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
	// Generated using JFormDesigner Evaluation license - Sebastian Colavita
	private JPanel panel2;
	private JPanel panel1;
	private JScrollPane scrollPaneRasgos2;
	private JTable tableRasgos2;
	private JButton button1;
	private JComponent separator1;
	private JLabel label1;
	private JLabel resultado;
	private JLabel label2;
	private JLabel Descuento;
	private JPanel panel3;
	private JPanel panelGraficoPixel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	//private JPanel panelGraficoPixel = new JPanel();
	
}
