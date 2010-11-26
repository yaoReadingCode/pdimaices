package procesamiento;

import javax.media.jai.PlanarImage;

import procesamiento.clasificacion.Clasificador;

/**
 * Clase abstracta que define el comportamiento común para implementar comandos
 * sobre una imagen
 * @author oscar
 *
 */
public abstract class AbstractImageCommand implements ImageComand {
	/**
	 * Imagen a procesar
	 */
	private PlanarImage image;
	
	private Clasificador clasificador;

	/**
	 * Constructor
	 * @param image Imagen a procesar
	 */
	public AbstractImageCommand(PlanarImage image) {
		super();
		setImage(image);
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#undo()
	 */
	public PlanarImage undo() {
		return getImage();
	}

	public PlanarImage getImage() {
		return image;
	}

	public void setImage(PlanarImage image) {
		this.image = image;
	}

	public Clasificador getClasificador() {
		return clasificador;
	}

	public void setClasificador(Clasificador clasificador) {
		this.clasificador = clasificador;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getInfo()
	 */
	public String getInfo() {
		return "";
	}

}
