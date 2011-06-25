package aplicarFiltros.configuracion;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorRenderer extends DefaultTableCellRenderer {
	private static final Color DEFAULT_COLOR = Color.gray;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (value instanceof Color){
			this.setBackground((Color)value);
		}
		else
			this.setBackground(DEFAULT_COLOR);
		return this;
	}

}
