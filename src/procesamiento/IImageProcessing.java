package procesamiento;

import javax.media.jai.PlanarImage;

import procesamiento.clasificacion.Clasificador;

/**
 * Interfaz que define métodos para administrar comandos ejecutados sobre una imagen
 * @author oscar
 *
 */
public interface IImageProcessing {
	
	/**
	 * Obtiene la imagen original sin procesar
	 * @return
	 */
	public PlanarImage getOriginalImage();
	
	/**
	 * Obtiene la imagen actual
	 * @return
	 */
	public PlanarImage getImage();

	/**
	 * Setea la imagen actual
	 * @param image
	 */
	public void setImage(PlanarImage image);
	
	/**
	 * Agrega un comando ejecutado sobre la imagen
	 * @param comand Comando
	 * @param info Información del comando
	 */
	public void addExecutedCommand(ImageComand comand, String info);
	
	/**
	 * Obtiene el objeto Clsificador
	 * @return Clasificador
	 */
	public Clasificador getClasificador();
}
