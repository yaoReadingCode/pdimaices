package procesamiento;

import java.awt.Color;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import objeto.Objeto;

public class ProcesingTask extends SwingWorker<Object, Object> {
	private PlanarImage result;
	private PlanarImage originalImage;
	private HSVRange hsvRange;
	private boolean done = false;
	private ProgressMonitor progressMonitor;

	public ProcesingTask(PlanarImage originalImage, HSVRange hsvRange) {
		super();
		this.originalImage = originalImage;
		this.hsvRange = hsvRange;
	}

	public PlanarImage getResult() {
		try {
			while (!done)
				synchronized (this) {
					wait();
				}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public PlanarImage getOriginalImage() {
		return originalImage;
	}

	public void setOriginalImage(PlanarImage originalImage) {
		this.originalImage = originalImage;
	}

	public HSVRange getHsvRange() {
		return hsvRange;
	}

	public void setHsvRange(HSVRange hsvRange) {
		this.hsvRange = hsvRange;
	}

	 
	public List<Objeto> doInBackground() {
		try {
			int progress = 0;
			done = false;

			Binarizar ef = new Binarizar(getOriginalImage(),getHsvRange(), null);
			TiledImage output1 = (TiledImage) ef.execute();
			progress += 25;
			setProgress(progress);

			/*
			Binarizar bin = new Binarizar(output1, Color.black, Color.white);
			PlanarImage binaryImage = bin.execute();*/
			progress += 25;
			setProgress(progress);

			// Realiza la resta de la imagen actual y su erosion
			DetectarContornoGrueso dc2 = new DetectarContornoGrueso(output1);
			PlanarImage contornoImage = dc2.execute();
			progress += 25;
			setProgress(progress);

			DetectarContornoViejo dc = new DetectarContornoViejo(contornoImage, getOriginalImage(), new Color(100, 100, 100), Color.RED);
			result = dc.execute();			
			progress += 25;
			setProgress(progress);
			done = true;
		

			synchronized (this) {
				this.notify();
			}

			ef.postExecute();
			//bin.postExecute();
			// o.postExecute();
			// c.postExecute();
			dc.postExecute();
			return dc.getObjetos();
		} catch (Exception e) {
			done = true;
			e.printStackTrace();
			this.notify();
		}
		return null;
	}

	 
	public void done() {
		done = true;
		progressMonitor.setProgress(0);

	}
}
