package aplicarFiltros.configuracion.modelmapper;

import java.util.ArrayList;
import java.util.List;

import objeto.Rasgo;

import aplicarFiltros.configuracion.exception.ValidationException;

public abstract class ModelMapper<T> {
	protected List<T> dataModel = new ArrayList<T>();
	
	public ModelMapper() {
		super();
	}
	
	public ModelMapper(List<T> dataModel) {
		super();
		this.dataModel = dataModel;
	}

	public void setDataModel(List<T> data) {
		this.dataModel = data;
	}

	public List<T> getDataModel() {
		return dataModel;
	}

	public abstract String[] getColumnNames();  
	
	public abstract Object[][] getData();
	
	public abstract void setValueAt(Object value, int row, int col) throws ValidationException;
	
	public abstract void addNewRow();
	
	public abstract void saveRow(int row) throws ValidationException;

	public abstract void validate(T object) throws ValidationException;

	/**
	 * Hook method
	 * @param row
	 */
	public T deleteRow(int row){
		if (row < getDataModel().size()){
			T obj = getDataModel().get(row); 
			getDataModel().remove(row);
			return obj;
		}
		return null;
	}
}
