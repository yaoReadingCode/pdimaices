package procesamiento;

import javax.media.jai.PlanarImage;
/**
 * Comando que detecta el contorno grueso de los objetos de una imagen.<br>
 * Realiza la resta entre una imagen binaria (blanco y negro) y su erosión.
 * El resultado es una imagen con el contorno grueso de los objetos.
 * @author oscar
 *
 */
public class DetectarContornoGrueso extends AbstractImageCommand {

	public DetectarContornoGrueso(PlanarImage image) {
		super(image);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#execute()
	 */
	public PlanarImage execute() {
		if (getImage() != null) {
			PlanarImage original = getImage();
			Erode cErosion = new Erode(original);
			PlanarImage imageErode = cErosion.execute();
			Resta cResta = new Resta(original, original, imageErode);
			PlanarImage result = cResta.execute();
			return result;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Detectar Cotorno Grueso";
	}
	
	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
