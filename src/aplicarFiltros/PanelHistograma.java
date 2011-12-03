package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import objeto.Histograma;
import objeto.HistogramaContainer;
import objeto.ObjetoUtil;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class PanelHistograma extends JPanel {

	private static final long serialVersionUID = 1L;
	private DecimalFormat formater = new DecimalFormat("0.00");
	private static final String NO_DATA_MESSAGE = "No hay datos";  //  @jve:decl-index=0:
	private static final int CHART_HEIGHT = 150;
	private static final int CHART_WIDTH = 500;
	private JScrollPane histogramaScrollPane = null;
	private JPanel panel = null;
	private JPanel panelDatos = null;
	private JPanel panelCorrelacion = null;
	private JLabel labelCorrelacion = null;
	private JLabel valueCorrelacion = null;
	private JPanel panelDistancia = null;
	private JLabel labelDistancia = null;
	private JLabel valueDistancia = null;
	private JPanel panelSumatoriaH1 = null;
	private JPanel panelSumatoriaH2 = null;
	private JLabel labelSumatoriaH1 = null;
	private JLabel labelSumatoriaH2 = null;
	private JLabel valueSumatoriaH1 = null;
	private JLabel valueSumatoriaH2 = null;

	
	private DefaultCategoryDataset dataset = new DefaultCategoryDataset();  //  @jve:decl-index=0:
	
	private Double correlacion = null;  //  @jve:decl-index=0:
	
	private String nombreSerie1 = "";  //  @jve:decl-index=0:
	
	private String nombreSerie2 = "";
	
	private Double distanciaBhattacharya = null;
	
	private Double sumatoriaH1 = null;  //  @jve:decl-index=0:
	
	private Double sumatoriaH2 = null;  //  @jve:decl-index=0:
	
	/**
	 * Contenedor de histogramas RGB 1 
	 */
	private HistogramaContainer histograma1;  //  @jve:decl-index=0:
	
	/**
	 * Contenedor de histogramas RGB 2 
	 */
	private HistogramaContainer histograma2;
	
	private String tipoHistograma = null;
	
	/**
	 * This is the default constructor
	 */
	public PanelHistograma(HistogramaContainer histograma1, HistogramaContainer histograma2, String nombreSerie1, String nombreSerie2, String tipoHistograma) {
		super();
		setHistograma1(histograma1);
		setHistograma2(histograma2);
		setNombreSerie1(nombreSerie1);
		setNombreSerie2(nombreSerie2);
		setTipoHistograma(tipoHistograma);
		initialize();
		fillDatasets(histograma1, histograma2);
		graficar();
	}
	/**
	 * Crea los datasets con los datos del histograma
	 * @param histograma2
	 */
	private void fillDatasets(HistogramaContainer histograma1, HistogramaContainer histograma2) {
		fillDataset(dataset,histograma1, histograma2,getTipoHistograma());
	}

	private void fillDataset(DefaultCategoryDataset dataset, HistogramaContainer container1, HistogramaContainer container2, String typeHistograma) {
		Histograma histograma1 = container1.getHistograma(typeHistograma);
		if(histograma1 != null){
			double[] values1 = histograma1.getValores();
			double sumH1 = 0;
			for(int i = 0; i < values1.length; i++){
				dataset.addValue(values1[i], getNombreSerie1(), Double.valueOf(i));
				sumH1 += values1[i];
			}
			sumatoriaH1 = sumH1;
		}
		
		Histograma histograma2 = container2.getHistograma(typeHistograma);
		if (histograma2 != null){
			double[] values2 = histograma2.getValores();
			double sumH2 = 0;
			for(int i = 0; i < values2.length; i++){
				dataset.addValue(values2[i], getNombreSerie2(), Double.valueOf(i));
				sumH2 += values2[i];
			}
			sumatoriaH2 = sumH2;
		}
		
		if (histograma1 != null && histograma2 != null){
			correlacion = ObjetoUtil.coeficienteCorrelacion(histograma1.getValores(), histograma2.getValores());
			distanciaBhattacharya = ObjetoUtil.distanciaBhattacharya(histograma1.getValores(), histograma2.getValores());
		}
	}
	
	private void createChart(JPanel panelContainer, DefaultCategoryDataset dataSet){
		JFreeChart chart = ChartFactory.createLineChart("", "", "Valor", dataSet, PlotOrientation.VERTICAL, true,  true, false); 
	    Plot plot = chart.getPlot();
		plot.setNoDataMessage(NO_DATA_MESSAGE);
	    ChartPanel panel = new ChartPanel(chart);
	    panel.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT));
	    panelContainer.add(panel);
	    panelContainer.add(getPanelDatos(),BorderLayout.EAST);

	}
	/**
	 * Grafica los histogramas
	 */
	public void graficar(){
		createChart(getPanel(), dataset);
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 1.0;
		this.setSize(CHART_WIDTH, CHART_HEIGHT);
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.add(getHistogramaScrollPane(), gridBagConstraints);
	}

	/**
	 * This method initializes histogramaScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getHistogramaScrollPane() {
		if (histogramaScrollPane == null) {
			histogramaScrollPane = new JScrollPane();
			histogramaScrollPane.setViewportView(getPanel());
		}
		return histogramaScrollPane;
	}

	/**
	 * This method initializes panel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setBorder(BorderFactory.createTitledBorder(null, getTipoHistograma(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), Color.BLACK));
		}
		return panel;
	}
	
	/**
	 * This method initializes panelDatos	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPanelDatos() {
		if (panelDatos == null) {
			panelDatos = new JPanel();
			panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));
			panelDatos.add(getPanelCorrelacion());
			panelDatos.add(getPanelDistancia());
			//panelDatos.add(getPanelSumatoriaH1());
			//panelDatos.add(getPanelSumatoriaH2());
		}
		return panelDatos;
	}

	/**
	 * This method initializes panelCorrelacion	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPanelCorrelacion() {
		if (panelCorrelacion == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			panelCorrelacion = new JPanel();
			panelCorrelacion.setLayout(flowLayout);
			panelCorrelacion.add(getLabelCorrelacion());
			panelCorrelacion.add(getValueCorrelacion());
		}
		return panelCorrelacion;
	}
	
	public JLabel getLabelCorrelacion() {
		if (labelCorrelacion == null){
			labelCorrelacion = new JLabel();
			labelCorrelacion.setText("Correlación:");
			labelCorrelacion.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return labelCorrelacion;
	}
	public JLabel getValueCorrelacion() {
		if (valueCorrelacion == null){
			valueCorrelacion = new JLabel();
			if (correlacion != null){
				valueCorrelacion.setText(formater.format(correlacion));
			}
		}
		return valueCorrelacion;
	}

	/**
	 * This method initializes panelCorrelacion	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPanelDistancia() {
		if (panelDistancia == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(FlowLayout.LEFT);
			panelDistancia = new JPanel();
			panelDistancia.setLayout(flowLayout1);
			panelDistancia.add(getLabelDistancia());
			panelDistancia.add(getValueDistancia());
		}
		return panelDistancia;
	}

	
	public JLabel getLabelDistancia() {
		if (labelDistancia == null){
			labelDistancia = new JLabel();
			labelDistancia.setText("Distancia:");
			labelDistancia.setToolTipText("Distancia de Bhattacharya");
			labelDistancia.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return labelDistancia;
	}
	public JLabel getValueDistancia() {
		if (valueDistancia == null){
			valueDistancia = new JLabel();
			if (distanciaBhattacharya != null){
				valueDistancia.setText(formater.format(distanciaBhattacharya));
			}
		}
		return valueDistancia;
	}

	/**
	 * This method initializes panelSumatoriaH1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getPanelSumatoriaH1() {
		if (panelSumatoriaH1 == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(FlowLayout.LEFT);
			panelSumatoriaH1 = new JPanel();
			panelSumatoriaH1.setLayout(flowLayout1);
			panelSumatoriaH1.add(getLabelSumatoriaH1());
			panelSumatoriaH1.add(getValueSumatoriaH1());
		}
		return panelSumatoriaH1;
	}

	/**
	 * This method initializes panelSumatoriaH2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getPanelSumatoriaH2() {
		if (panelSumatoriaH2 == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(FlowLayout.LEFT);
			panelSumatoriaH2 = new JPanel();
			panelSumatoriaH2.setLayout(flowLayout1);
			panelSumatoriaH2.add(getLabelSumatoriaH2());
			panelSumatoriaH2.add(getValueSumatoriaH2());
		}
		return panelSumatoriaH2;
	}
	
	public JLabel getLabelSumatoriaH1() {
		if (labelSumatoriaH1 == null){
			labelSumatoriaH1 = new JLabel();
			labelSumatoriaH1.setText("Sumatoria H1:");
			labelSumatoriaH1.setToolTipText("Sumatoria " + getNombreSerie1());
			labelSumatoriaH1.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return labelSumatoriaH1;
	}
	public JLabel getValueSumatoriaH1() {
		if (valueSumatoriaH1 == null){
			valueSumatoriaH1 = new JLabel();
			if (sumatoriaH1 != null){
				valueSumatoriaH1.setText(formater.format(sumatoriaH1));
			}
		}
		return valueSumatoriaH1;
	}

	public JLabel getLabelSumatoriaH2() {
		if (labelSumatoriaH2 == null){
			labelSumatoriaH2 = new JLabel();
			labelSumatoriaH2.setText("Sumatoria H2:");
			labelSumatoriaH2.setToolTipText("Sumatoria " + getNombreSerie2());
			labelSumatoriaH2.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return labelSumatoriaH2;
	}
	public JLabel getValueSumatoriaH2() {
		if (valueSumatoriaH2 == null){
			valueSumatoriaH2 = new JLabel();
			if (sumatoriaH2 != null){
				valueSumatoriaH2.setText(formater.format(sumatoriaH2));
			}
		}
		return valueSumatoriaH2;
	}
	
	public HistogramaContainer getHistograma1() {
		return histograma1;
	}
	public void setHistograma1(HistogramaContainer histograma1) {
		this.histograma1 = histograma1;
	}
	public HistogramaContainer getHistograma2() {
		return histograma2;
	}
	public void setHistograma2(HistogramaContainer histograma2) {
		this.histograma2 = histograma2;
	}
	public String getNombreSerie1() {
		return nombreSerie1;
	}
	public void setNombreSerie1(String nombreSerie1) {
		this.nombreSerie1 = nombreSerie1;
	}
	public String getNombreSerie2() {
		return nombreSerie2;
	}
	public void setNombreSerie2(String nombreSerie2) {
		this.nombreSerie2 = nombreSerie2;
	}
	
	public String getTipoHistograma() {
		return tipoHistograma;
	}
	public void setTipoHistograma(String tipoHistograma) {
		this.tipoHistograma = tipoHistograma;
	}
	
	public Double getCorrelacion() {
		return correlacion;
	}
	public void setCorrelacion(Double correlacion) {
		this.correlacion = correlacion;
	}
	
	public Double getDistanciaBhattacharya() {
		return distanciaBhattacharya;
	}
	public void setDistanciaBhattacharya(Double distanciaBhattacharya) {
		this.distanciaBhattacharya = distanciaBhattacharya;
	}
}  //  @jve:decl-index=0:visual-constraint="2,-53"
