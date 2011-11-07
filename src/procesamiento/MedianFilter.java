package procesamiento;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.MedianFilterDescriptor;

public class MedianFilter extends AbstractImageCommand {

	public MedianFilter(PlanarImage image) {
		super(image);
	}

	@Override
	public PlanarImage execute() {
		RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
		ParameterBlock p = new ParameterBlock();
		p.addSource(getImage());
		p.add(MedianFilterDescriptor.MEDIAN_MASK_SQUARE);
		p.add(3);
		PlanarImage result = JAI.create("MedianFilter", p, hints);
		return result;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "MedianFilter";
	}

	@Override
	public void postExecute() {
		// TODO Auto-generated method stub

	}

}
