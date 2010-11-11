package procesamiento;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
/**
 * Comando que convierte una imagen color a escala de grises
 * @author oscar
 *
 */
public class ConvertEscalaGrises extends AbstractImageCommand {

	/**
	 * Constructor
	 * @param image
	 */
	public ConvertEscalaGrises(PlanarImage image) {
		super(image);
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#execute()
	 */
	public PlanarImage execute() {
		if (getImage() != null) {
			double[][] matrix = { { 1./2 ,1./4, 1./4, 0 } };
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(getImage());
			pb.add(matrix);
			PlanarImage image = JAI.create("bandcombine", pb, null);
			return image;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#getCommandName()
	 */
	public String getCommandName() {
		return "Convertir a escala de grises";
	}

	/*
	 * (non-Javadoc)
	 * @see procesamiento.ImageComand#postExecute()
	 */
	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
