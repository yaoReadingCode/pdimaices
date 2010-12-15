package aplicarFiltros;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class TestBooleanIcon {

	public static void main(String[] args) {
		Object[][] data = { { "True icon", Boolean.TRUE },
				{ "False icon", Boolean.FALSE } };
		String[] columnNames = { "Type", "Icon" };
		TableModel model = new DefaultTableModel(data, columnNames) {
			public Class getColumnClass(int columnIndex) {
				return columnIndex == 1 ? Boolean.class : Object.class;
			}
		};
		JTable table = new JTable(model);
		Icon icon = new BooleanIcon();
		JCheckBox check = (JCheckBox) table.getDefaultRenderer(Boolean.class);
		check.setIcon(icon);
		DefaultCellEditor editor = (DefaultCellEditor) table
				.getDefaultEditor(Boolean.class);
		((JCheckBox) editor.getComponent()).setIcon(icon);
		
		table.setRowHeight(30);

		final JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new JScrollPane(table));
		f.pack();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				f.setLocationRelativeTo(null);
				f.setVisible(true);
			}
		});
	}
}