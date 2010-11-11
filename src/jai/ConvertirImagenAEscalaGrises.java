package jai;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

public class ConvertirImagenAEscalaGrises {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PlanarImage pi = JAI.create("fileload", args[0]);
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		BufferedImage image = op.filter(pi.getAsBufferedImage(), null);
		JAI.create("filestore", image, "rgbpattern2.tif", "TIFF");
		/*
		cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		cmRo = pi.getColorModel();
		cm = RasterFactory.createComponentColorModel(pi.getSampleModel()
				.getDataType(), cs, cmRo.hasAlpha(), cmRo
				.isAlphaPremultiplied(), cmRo.getTransparency());
		pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(cm);
		pi = JAI.create("colorconvert", pb);*/
	}

}
