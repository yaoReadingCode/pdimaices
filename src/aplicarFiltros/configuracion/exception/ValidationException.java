package aplicarFiltros.configuracion.exception;
/**
 * Excepcion para manejar errores de validación de campos
 * @author oscar
 *
 */
public class ValidationException extends Exception {
	
	/**
	 * Identificador del campo
	 */
	private int fieldId = 0;

	public ValidationException(int fieldIndex, String message) {
		super(message);
		this.fieldId = fieldIndex;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}
	
}
