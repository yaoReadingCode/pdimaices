package procesamiento;

import javax.media.jai.PlanarImage;
/**
 * Interfaz para ejecutar comandos sobre una imagen
 * @author oscar
 *
 */
public interface ImageComand {
	/**
	 * Método que ejecuta una operación sobre la imagen
	 * @return Imagen resultante del procesamiento
	 */
	public PlanarImage execute();
	
	/**
	 * Deshace los cambios realizados sobre la imagen en el método execute
	 * @return Imagen antes de realizar el procesamiento
	 */
	public PlanarImage undo();
	
	/**
	 * Retorna el nombre del comando ejecutado
	 * @return
	 */
	public String getCommandName();
	
	/**
	 * Operaciones a realizar después de ejecutar el comando
	 */
	public void postExecute();
	
	/**
	 * Retorna un texto que da información el procesamiento realizado sobre la imagen
	 * @return
	 */
	public String getInfo();
	
}
