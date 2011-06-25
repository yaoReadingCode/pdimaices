package aplicarFiltros.configuracion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SelectColorPanel extends JPanel{
	public static Color DEFAULT_COLOR;
	private Color selectedColor = DEFAULT_COLOR;

	public SelectColorPanel() {
		super();
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(100, 20));
	}

	private JComponent getPanel(){
		return this;
	}
	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
		this.setBackground(selectedColor);
	}

	public static void main(String[] args) {
		SelectColorPanel panel = new SelectColorPanel();
		JFrame frame = new JFrame();
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.pack();
	}

	public void setVisible(boolean flag) {
		super.setVisible(flag);
		if (flag){
			Color color = JColorChooser.showDialog(getPanel(), "Color de fondo",selectedColor);
			if (color != null) {
				setSelectedColor(color);
			}
		}
	}
	
	
}
