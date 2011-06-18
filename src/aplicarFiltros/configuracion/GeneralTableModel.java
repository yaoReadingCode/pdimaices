package aplicarFiltros.configuracion;

import javax.swing.table.AbstractTableModel;

import aplicarFiltros.configuracion.exception.ValidationException;
import aplicarFiltros.configuracion.modelmapper.ModelMapper;

public class GeneralTableModel extends AbstractTableModel {
	protected AdminPanel adminPanel;
    protected String[] columnNames;
    protected Object[][] data;
    protected ModelMapper mapper;
    
	public GeneralTableModel(ModelMapper mapper) {
		super();
		this.adminPanel = adminPanel;
		this.mapper = mapper;
		if(mapper != null){
			columnNames = mapper.getColumnNames();
			data = mapper.getData();
		}
	}

	public int getColumnCount() {
        return columnNames.length;
    }
    
    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col){
    	if (data.length > row){
            data[row][col] = value;
            if (mapper != null){
            	try {
    				mapper.setValueAt(value, row, col);
    				getAdminPanel().cleanErrorMessage();
    			} catch (ValidationException e) {
    				getAdminPanel().showErrorMessage(e, row);
    			}
            }
    	}
    }
    
    
    public void addRow(){
    	if (mapper != null){
    		mapper.addNewRow();
    		this.data = mapper.getData();
    	}
    }
    
    public void saveRow(int row) throws ValidationException{
    	if (mapper != null){
    		mapper.saveRow(row);
    	}
    }

    public void deleteRow(int row){
    	if (mapper != null){
    		mapper.deleteRow(row);
    		this.data = mapper.getData();
    	}
    }

	public AdminPanel getAdminPanel() {
		return adminPanel;
	}

	public void setAdminPanel(AdminPanel adminPanel) {
		this.adminPanel = adminPanel;
	}

}
