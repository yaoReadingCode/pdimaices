package aplicarFiltros.configuracion.modelmapper;

import java.awt.Color;
import java.util.List;

import aplicarFiltros.configuracion.exception.ValidationException;

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
			data[index][COLOR_INDEX] = (c.getColorRgb() != null) ? new Color(c.getColorRgb()): Color.gray;
		}
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see aplicarFiltros.configuracion.TableMapper#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object value, int row, int col) throws ValidationException{
		Clase clase = getDataModel().get(row); 
		switch (col) {
		case NOMBRE_INDEX:
			clase.setNombre((String)value);
			break;
		case DESCRIPCION_INDEX:
			clase.setDescripcion((String)value);
			break;
		case AGRUPADOR_INDEX:
			clase.setAgrupador((String)value);
			break;
		case ORDEN_INDEX:
			clase.setOrdenEvaluacion((Integer)value);
			break;
		case COLOR_INDEX:
			clase.setColorRgb(((Color)value).getRGB());
			break;
		default:
			break;
		}
		validate(clase);
	}

	public void addNewRow() {
		Clase nuevo = new Clase();
		getDataModel().add(nuevo);
	}

	public void saveRow(int row) throws ValidationException {
		Clase clase = getDataModel().get(row);
		validate(clase);
		ObjectDao.getInstance().save(clase);
	}

	public Clase deleteRow(int row) {

		Clase clase = super.deleteRow(row);
		if(clase != null && clase.getId() != null){
			ObjectDao.getInstance().delete(clase);
		}
		return clase;
	}

	public void validate(Clase clase) throws ValidationException {
		if (clase.getNombre() == null || clase.getNombre().trim().equals(""))
			throw new ValidationException(NOMBRE_INDEX, "Debe ingresar un nombre");
		if (clase.getDescripcion() == null || clase.getDescripcion().trim().equals(""))
			throw new ValidationException(DESCRIPCION_INDEX, "Debe ingresar una descripción");
		if (clase.getOrdenEvaluacion() == null)
			throw new ValidationException(ORDEN_INDEX, "Debe el orden de evaluación");
	}
}
