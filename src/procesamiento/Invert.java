package procesamiento;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

public class Invert extends AbstractImageCommand {

	public Invert(PlanarImage image) {
		super(image);
	}

	public PlanarImage execute() {
		if (getImage() != null){
			return JAI.create("invert", getImage());
		}
		return null;
	}

	public String getCommandName() {
		return Invert.class.getCanonicalName();
	}

	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
