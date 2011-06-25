package aplicarFiltros.configuracion;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ColorCellEditor extends AbstractCellEditor implements TableCellEditor {
	private SelectColorPanel colorPanel = new SelectColorPanel();
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value != null){
			colorPanel.setSelectedColor((Color)value);
		}
		else{
			colorPanel.setSelectedColor(SelectColorPanel.DEFAULT_COLOR);
		}
		colorPanel.setVisible(true);
		return colorPanel;
	}

	public Object getCellEditorValue() {
		return colorPanel.getSelectedColor();
	}



}
