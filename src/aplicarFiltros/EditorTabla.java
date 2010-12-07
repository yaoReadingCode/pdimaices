package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class EditorTabla extends AbstractCellEditor implements TableCellEditor {
	JPanel panel = new JPanel();
	JTextField campo;
	JButton boton;

	public EditorTabla(JTextField campo, JButton boton) {
		panel.setLayout(new BorderLayout(1, 0));
		this.campo = campo;
		this.boton = boton;
		panel.add(campo, BorderLayout.CENTER);
		panel.add(boton, BorderLayout.EAST);
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		boton.setActionCommand("" + column);
		campo.setText(value != null ? value.toString() : "");
		return panel;
	}

	public void setValor(Object valor) {
		campo.setText(valor != null ? valor.toString() : "");
		//campo.setValor(valor);
		stopCellEditing();
	}

	public Object getValor() {
		return campo.getText();
	}

	public Object getCellEditorValue() {
		return campo.getText();
	}

	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	public boolean stopCellEditing() {
		/*
		 * JTable tabla = (JTable) panel.getParent(); int col =
		 * tabla.getEditingColumn(); try { if
		 * (VentanaAgregarClase.esValorValido((Object)campo.getText(), col) ==
		 * null) { return false; } } catch (Exception ex) { return false; }
		 */
		fireEditingStopped();
		return true;
	}

	public void cancelCellEditing() {
		fireEditingCanceled();
	}

}