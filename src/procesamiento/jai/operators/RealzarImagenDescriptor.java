package procesamiento.jai.operators;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.awt.image.renderable.RenderedImageFactory;

import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationRegistry;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.registry.RenderableRegistryMode;
import javax.media.jai.registry.RenderedRegistryMode;

import com.sun.media.jai.codec.TIFFEncodeParam;

import procesamiento.ImageUtil;


public class RealzarImagenDescriptor extends OperationDescriptorImpl implements RenderedImageFactory{

    /**
     * The resource strings that provide the general documentation
     * and specify the parameter list for this operation.
     */
    private static final String[][] resources = {
        {"GlobalName",  "RealzarImagen"},
        {"LocalName",   "RealzarImagen"},
        {"Vendor",      "com.pdimaices"},
        {"Description", ""},
        {"DocURL",      ""},
        {"Version",     "1.0.0"},
    };

    /** The parameter class list for this operation. */
    private static final Class[] paramClasses = {};

    /** The parameter name list for this operation. */
    private static final String[] paramNames = {};

    /** The parameter default value list for this operation. */
    private static final Object[] paramDefaults = {};

	private static boolean registered = false;

    /** Constructor. */
    public RealzarImagenDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    /** Returns <code>true</code> since renderable operation is supported. */
    public boolean isRenderableSupported() {
        return true;
    }

    /**
     * Validates the input parameters.
     *
     * <p> In addition to the standard checks performed by the
     * superclass method, this method checks that "low" and "high"
     * have length at least 1 and that each "low" value is less than
     * or equal to the corresponding "high" value.
     */
    protected boolean validateParameters(ParameterBlock args,
                                         StringBuffer msg) {
        if (!super.validateParameters(args, msg)) {
            return false;
        }

        return true;
    }


    /**
     * Clamps the pixel values of an image to a specified range.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all
     * supplied arguments except <code>hints</code> and invokes
     * {@link JAI#create(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderedOp
     *
     * @param source0 <code>RenderedImage</code> source 0.
     * @param low The lower boundary for each band.
     * May be <code>null</code>.
     * @param high The upper boundary for each band.
     * May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use.
     * May be <code>null</code>.
     * @return The <code>RenderedOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderedOp create(RenderedImage source0,
                                    RenderingHints hints)  {
        ParameterBlockJAI pb =
            new ParameterBlockJAI("RealzarImagen",
                                  RenderedRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);
        return JAI.create("RealzarImagen", pb, hints);
    }

    /**
     * Clamps the pixel values of an image to a specified range.
     *
     * <p>Creates a <code>ParameterBlockJAI</code> from all
     * supplied arguments except <code>hints</code> and invokes
     * {@link JAI#createRenderable(String,ParameterBlock,RenderingHints)}.
     *
     * @see JAI
     * @see ParameterBlockJAI
     * @see RenderableOp
     *
     * @param source0 <code>RenderableImage</code> source 0.
     * @param low The lower boundary for each band.
     * May be <code>null</code>.
     * @param high The upper boundary for each band.
     * May be <code>null</code>.
     * @param hints The <code>RenderingHints</code> to use.
     * May be <code>null</code>.
     * @return The <code>RenderableOp</code> destination.
     * @throws IllegalArgumentException if <code>source0</code> is <code>null</code>.
     */
    public static RenderableOp createRenderable(RenderableImage source0,
                                                RenderingHints hints)  {
        ParameterBlockJAI pb =
            new ParameterBlockJAI("RealzarImagen",
                                  RenderableRegistryMode.MODE_NAME);

        pb.setSource("source0", source0);
        return JAI.createRenderable("RealzarImagen", pb, hints);
    }

    public static void register() {
		if (!registered) {
			OperationRegistry or = JAI.getDefaultInstance().getOperationRegistry();

			RealzarImagenDescriptor d = new RealzarImagenDescriptor();
			//or.registerOperationDescriptor(d, operationName);
			//or.registerRIF(operationName, productName, d);
			or.registerDescriptor(d);
			or.registerFactory("rendered", "RealzarImagen", "com.pdimaices" , d);
			registered = true;
		}
	}
	public static void main(String[] args) throws Exception {
		RealzarImagenDescriptor.register();
		PlanarImage im0 = ImageUtil.loadImage("img/SDC11276.JPG", ImageUtil.tileWidth, ImageUtil.tileHeight);//(PlanarImage) JAI.create("fileload", "img/maices_01.jpg");
		//im0 = ImageUtil.createTiledImage(im0, ImageUtil.tileWidth, ImageUtil.tileHeight);
		JAI.getDefaultInstance().getTileScheduler().setParallelism(2);
		ParameterBlock pb2 = new ParameterBlock();
		pb2.addSource(im0);
		PlanarImage im1 = JAI.create("RealzarImagen", pb2);
		
		//Point[] tileIndices =  im1.getTileIndices(null);
		//JAI.getDefaultInstance().getTileScheduler().scheduleTiles((OpImage)im1, tileIndices);

		
		TIFFEncodeParam param = new TIFFEncodeParam();
		param.setTileSize(ImageUtil.tileWidth, ImageUtil.tileHeight);
		param.setWriteTiled(true);
		
		ParameterBlock pb = new ParameterBlock();
        pb.addSource(im1);
        pb.add("realzarImagen.tif");
        pb.add("tiff");
        pb.add(param);
        RenderedOp r = JAI.create("filestore",pb);
        r.dispose();
	}

	@Override
	public RenderedImage create(ParameterBlock paramBlock, RenderingHints hints) {
		RenderedImage ri = paramBlock.getRenderedSource(0);
		return new RealzarImageOp(ri);
	}
	
}
