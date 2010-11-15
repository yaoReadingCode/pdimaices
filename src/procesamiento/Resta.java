package procesamiento;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

/**
 * Comando que realiza la operación de resta de dos imagenes binarias
 * @author oscar
 *
 */
public class Resta extends AbstractImageCommand {
	/**
	 * Imagen 1
	 */
	private PlanarImage image1;
	
	/**
	 * Imagen 2
	 */
	private PlanarImage image2;

	/**
	 * 
	 * @param image Imagen original
	 * @param image1 Operando 1 de la resta
	 * @param image2 Operando 2 de la resta
	 */
	public Resta(PlanarImage image, PlanarImage image1, PlanarImage image2) {
		super(image);
		this.image1 = image1;
		this.image2 = image2;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#execute()
	 */
	public PlanarImage execute() {
		if (getImage1() != null && getImage2() != null) {
			RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(getImage1());
			pb.addSource(getImage2());
			return JAI.create("subtract", pb,hints);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Resta";
	}

	public PlanarImage getImage1() {
		return image1;
	}

	public void setImage1(PlanarImage image1) {
		this.image1 = image1;
	}

	public PlanarImage getImage2() {
		return image2;
	}

	public void setImage2(PlanarImage image2) {
		this.image2 = image2;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		// TODO Auto-generated method stub
		
	}

}
