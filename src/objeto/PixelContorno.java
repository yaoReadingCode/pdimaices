package objeto;

public class PixelContorno {
	private int direccion;
	private Pixel pixel;
	public int getDireccion() {
		return direccion;
	}
	public void setDireccion(int direccion) {
		this.direccion = direccion;
	}
	public Pixel getPixel() {
		return pixel;
	}
	public void setPixel(Pixel pixel) {
		this.pixel = pixel;
	}
	public PixelContorno(int direccion, Pixel pixel) {
		super();
		this.direccion = direccion;
		this.pixel = pixel;
	}
	 
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof PixelContorno))
			return false;
		PixelContorno p = (PixelContorno) obj;
		if (getPixel() != null)
			return getPixel().equals(p.pixel);
		return false;
	}
	 
	public String toString() {
		if (getPixel() != null)
			return getPixel().toString();
		return "";
	}
	
}
