package aplicarFiltros.configuracion.modelmapper;

import java.util.List;

import objeto.Rasgo;
import aplicarFiltros.configuracion.exception.ValidationException;
import dataAcces.ObjectDao;

public class RasgoMapper extends ModelMapper<Rasgo> {
	
	private final String[] columnNames = {"Nombre","Descripción","Evaluador Rasgo"};

	private static final int NOMBRE_INDEX = 0;
	private static final int DESCRIPCION_INDEX = 1;
	private static final int EVALUADOR_CLASE_INDEX = 2;
	
	public RasgoMapper() {
		super();
		this.dataModel = ObjectDao.getInstance().qryAll(Rasgo.class.getName());
	}
	
	public RasgoMapper(List<Rasgo> rasgos) {
		super(rasgos);
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
		Object[][] data = new Object[getDataModel().size()][3];
		for(int index = 0; index < getDataModel().size(); index++){
			Rasgo r = getDataModel().get(index);
			data[index][NOMBRE_INDEX] = r.getNombre();
			data[index][DESCRIPCION_INDEX] = r.getDescripcion();
			data[index][EVALUADOR_CLASE_INDEX] = r.getNombreEvaluadorRasgo();
		}
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see aplicarFiltros.configuracion.TableMapper#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object value, int row, int col) throws ValidationException {
		Rasgo rasgo = getDataModel().get(row);
		switch (col) {
		case NOMBRE_INDEX:
			rasgo.setNombre((String)value);
			break;
		case DESCRIPCION_INDEX:
			rasgo.setDescripcion((String)value);
			break;
		case EVALUADOR_CLASE_INDEX:
			rasgo.setNombreEvaluadorRasgo((String)value);
			break;
		default:
			break;
		}
		validateRasgo(rasgo);
		
	}

	public void addNewRow() {
		Rasgo nuevo = new Rasgo();
		getDataModel().add(nuevo);
	}
	
	public void validateRasgo(Rasgo rasgo) throws ValidationException{
		if (rasgo.getNombre() == null || rasgo.getNombre().trim().equals(""))
			throw new ValidationException(NOMBRE_INDEX, "Debe ingresar un nombre");
		if (rasgo.getDescripcion() == null || rasgo.getDescripcion().trim().equals(""))
			throw new ValidationException(DESCRIPCION_INDEX, "Debe ingresar una descripción");
		if (rasgo.getNombreEvaluadorRasgo() == null || rasgo.getNombreEvaluadorRasgo().trim().equals(""))
			throw new ValidationException(EVALUADOR_CLASE_INDEX, "Debe ingresar un evaluador para el rasgo");
	}

	public void saveRow(int row) throws ValidationException{
		Rasgo rasgo = getDataModel().get(row);
		validateRasgo(rasgo);
		ObjectDao.getInstance().save(rasgo);
	}

	public Rasgo deleteRow(int row) {

		Rasgo rasgo = super.deleteRow(row);
		if(rasgo != null && rasgo.getId() != null){
			ObjectDao.getInstance().delete(rasgo);
		}
		return rasgo;
	}

}
