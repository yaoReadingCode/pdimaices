package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import objeto.Clase;
import objeto.ClaseObjeto;
import objeto.Objeto;
import objeto.Rasgo;
import objeto.RasgoObjeto;

public class Test {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		GridBagLayout gbl = new GridBagLayout();

		JPanel container = new JPanel();
		container.setLayout(gbl);
		frame.getContentPane().add(new JScrollPane(container),
				BorderLayout.CENTER);

		// Add other gridbag constraints here

		String path = "image/";
		File directorio = new File(path);
		String[] ficheros = directorio.list();
		for (int i = 0; i < ficheros.length; i++) {
			if (ficheros[i].endsWith(".tif")) {
				Objeto o = new Objeto();
				o.setPathImage(path + ficheros[i]);
				o.setName(ficheros[i]);
				List<RasgoObjeto> rasgos = new ArrayList<RasgoObjeto>();
				rasgos.add(new RasgoObjeto(new Rasgo("AREA"), 1.0));
				ClaseObjeto clase = new ClaseObjeto(new Clase("INDETERMINADO"));
				o.addClase(clase);
				o.setRasgos(rasgos);
				// ObjetoPanel jp = new ObjetoPanel(o);
				PreviewObjetoPanel panel = new PreviewObjetoPanel(o);
				// JButton panel = new JButton("Boton");

				// Place a component at cell location (1,1)
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridy = i / 3;// GridBagConstraints.RELATIVE;
				gbc.gridx = i % 3;// GridBagConstraints.RELATIVE;
				gbc.gridheight = 1;
				gbc.gridwidth = 1;
				gbc.fill = GridBagConstraints.BOTH;

				// Associate the gridbag constraints with the component
				gbl.setConstraints(panel, gbc);

				// Add the component to the container
				container.add(panel);

			}
		}

		// Show the frame
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
}
