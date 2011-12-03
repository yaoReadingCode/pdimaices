package aplicarFiltros;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import objeto.Histograma;
import objeto.HistogramaContainer;
import objeto.Objeto;

public class PanelHistogramaContainer extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final int PANEL_WIDTH = 700;
	public static final int PANEL_HEIGHT = 420;
	private JScrollPane panelScrollPane = null;
	private JPanel histogramasPanel = null;

	private String nombreSerie1 = "";  //  @jve:decl-index=0:
	
	private String nombreSerie2 = "";
	
	/**
	 * Contenedor de histogramas RGB 1 
	 */
	private HistogramaContainer histograma1;  //  @jve:decl-index=0:
	
	/**
	 * Contenedor de histogramas RGB 2 
	 */
	private HistogramaContainer histograma2;  //  @jve:decl-index=0:
	
	public PanelHistogramaContainer(HistogramaContainer histograma1, HistogramaContainer histograma2, String nombreSerie1, String nombreSerie2) {
		super();
		this.nombreSerie1 = nombreSerie1;
		this.nombreSerie2 = nombreSerie2;
		this.histograma1 = histograma1;
		this.histograma2 = histograma2;
		initialize();
	}

	private void createPanelsHistograma() {
		for(Histograma histo: getHistograma1().getHistogramas()){
			PanelHistograma panel = new PanelHistograma(getHistograma1(),getHistograma2(),getNombreSerie1(),getNombreSerie2(),histo.getTipo());
			getHistogramasPanel().add(panel);
		}
	}

	/**
	 * This method initializes panelScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getPanelScrollPane() {
		if (panelScrollPane == null) {
			panelScrollPane = new JScrollPane();
			panelScrollPane.setViewportView(getHistogramasPanel());
		}
		return panelScrollPane;
	}

	/**
	 * This method initializes histogramasPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getHistogramasPanel() {
		if (histogramasPanel == null) {
			histogramasPanel = new JPanel();
			histogramasPanel.setLayout(new BoxLayout(histogramasPanel, BoxLayout.Y_AXIS));
			createPanelsHistograma();
		}
		return histogramasPanel;
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
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setLayout(new GridBagLayout());
		this.add(getPanelScrollPane(), gridBagConstraints);
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

	public static void showPanel(PanelHistogramaContainer panel){
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setSize(PANEL_WIDTH, PANEL_HEIGHT);
		frame.setTitle("Histrogramas");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setResizable(true);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		Histograma histograma1 = new Histograma();
		histograma1.setTipo(Histograma.HISTOGRAMA_R);
		double[] values1 = {0,1,2,5,3,1,0};
		histograma1.setValores(values1);
		Objeto objeto = new Objeto();
		objeto.getHistogramas().add(histograma1);

		Histograma histograma2 = new Histograma();
		histograma2.setTipo(Histograma.HISTOGRAMA_R);
		double[] values2 = {5,4,3,1,2,3,3};
		histograma2.setValores(values2);
		Objeto objeto2 = new Objeto();
		objeto2.getHistogramas().add(histograma2);
		
		Histograma histogramaG = new Histograma();
		histogramaG.setTipo(Histograma.HISTOGRAMA_G);
		histogramaG.setValores(values2);
		objeto.getHistogramas().add(histogramaG);

		Histograma histogramaG2 = new Histograma();
		histogramaG2.setTipo(Histograma.HISTOGRAMA_G);
		histogramaG2.setValores(values2);
		objeto2.getHistogramas().add(histogramaG2);

		PanelHistogramaContainer panel = new PanelHistogramaContainer(objeto,objeto2,"Objeto 1", "Objeto 2");
		showPanel(panel);
	}
	
}
