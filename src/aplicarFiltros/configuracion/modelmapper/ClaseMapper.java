package aplicarFiltros.configuracion.modelmapper;

import java.util.List;

import objeto.Clase;
import dataAcces.ObjectDao;

public class ClaseMapper extends ModelMapper<Clase> {
	
	
	private final String[] columnNames = {"Nombre","Descripción","Agrupador","Orden","Color"};

	private static final int NOMBRE_INDEX = 0;
	private static final int DESCRIPCION_INDEX = 1;
	private static final int AGRUPADOR_INDEX = 2;
	private static final int ORDEN_INDEX = 3;
	private static final int COLOR_INDEX = 4;

	public ClaseMapper() {
		super();
		this.dataModel = ObjectDao.getInstance().qryAll(Clase.class.getName());
	}
	
	public ClaseMapper(List<Clase> clases) {
		super(clases);
	}
	
	/*
	 * (non-Javadoc)
	 * @see aplicarFiltros.configuracion.TableMapper#getColumnNames()
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/*
	 * (non-Javadoc)
	 * @see aplicarFiltros.configuracion.TableMapper#getData()
	 */
	public Object[][] getData() {
		Object[][] data = new Object[getDataModel().size()][5];
		for(int index = 0; index < getDataModel().size(); index++){
			Clase c = getDataModel().get(index);
			data[index][NOMBRE_INDEX] = c.getNombre();
			data[index][DESCRIPCION_INDEX] = c.getDescripcion();
			data[index][AGRUPADOR_INDEX] = c.getAgrupador();
			data[index][ORDEN_INDEX] = c.getOrdenEvaluacion();
			data[index][COLOR_INDEX] = c.getColorRgb();
		}
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see aplicarFiltros.configuracion.TableMapper#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object value, int row, int col) {
		switch (col) {
		case NOMBRE_INDEX:
			getDataModel().get(row).setNombre((String)value);
			break;
		case DESCRIPCION_INDEX:
			getDataModel().get(row).setDescripcion((String)value);
			break;
		case AGRUPADOR_INDEX:
			getDataModel().get(row).setAgrupador((String)value);
			break;
		case ORDEN_INDEX:
			getDataModel().get(row).setOrdenEvaluacion((Integer)value);
			break;
		case COLOR_INDEX:
			getDataModel().get(row).setColorRgb((Integer)value);
			break;
		default:
			break;
		}
		
	}

	public void addNewRow() {
		Clase nuevo = new Clase();
		getDataModel().add(nuevo);
	}

	public void saveRow(int row) {
		Object obj = getDataModel().get(row);
		ObjectDao.getInstance().save(obj);
	}

	public Clase deleteRow(int row) {

		Clase clase = super.deleteRow(row);
		if(clase != null && clase.getId() != null){
			ObjectDao.getInstance().delete(clase);
		}
		return clase;
	}
}
