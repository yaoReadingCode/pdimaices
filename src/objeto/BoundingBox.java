package objeto;

public class BoundingBox {
	private double minX = 0;
	private double maxX = 0;
	private double minY = 0;
	private double maxY = 0;
	
	public BoundingBox(double minX, double minY, double maxX, double maxY) {
		super();
		this.maxX = maxX;
		this.maxY = maxY;
		this.minX = minX;
		this.minY = minY;
	}
	/**
	 * Crea el Bounding Box entre dos pixeles
	 * @param p1
	 * @param p2
	 */
	public BoundingBox(Pixel p1, Pixel p2) {
		super();
		this.maxX = Math.max(p1.getXDouble(), p2.getXDouble());
		this.maxY = Math.max(p1.getYDouble(), p2.getYDouble());
		this.minX = Math.min(p1.getXDouble(), p2.getXDouble());
		this.minY = Math.min(p1.getYDouble(), p2.getYDouble());
	}
	public double getMinX() {
		return minX;
	}
	public void setMinX(double minX) {
		this.minX = minX;
	}
	public double getMaxX() {
		return maxX;
	}
	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}
	public double getMinY() {
		return minY;
	}
	public void setMinY(double minY) {
		this.minY = minY;
	}
	public double getMaxY() {
		return maxY;
	}
	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public double width(){
		return Math.abs(this.maxX - this.minX);
	}
	public double height(){
		return Math.abs(this.maxY - this.minY);
	}
	public void trasladar(Pixel punto) {
		this.maxX += punto.getX();
		this.maxY += punto.getY();
		this.minX += punto.getX();
		this.minY += punto.getY();
	}

	/**
	 * Retorna un nuevo pixel con x e y relativos al Bounding Box
	 * @param p
	 * @return
	 */
	public Pixel getPixelRelativo(Pixel p){
		Pixel nuevo = p.clonar();
		nuevo.setXDouble(p.getX() - this.minX);
		nuevo.setYDouble(p.getY() - this.minY);
		return nuevo;
	}

	/**
	 * Retorna un nuevo pixel con x e y relativos al Bounding Box
	 * @param p
	 * @return
	 */
	public Pixel getPixelOriginal(Pixel p){
		Pixel nuevo = p.clonar();
		nuevo.setXDouble(p.getX() + this.minX);
		nuevo.setYDouble(p.getY() + this.minY);
		return nuevo;
	}
	
	/**
	 * Devuelve si un pixel esta contenido en el rectangulo
	 * @param p
	 * @return
	 */
	public boolean isPertenece(Pixel p){
		if (p.getX() >= this.minX && p.getX() <= this.maxX &&
			p.getY() >= this.minY && p.getX() <= this.maxY){
			return true;
		}
		return false;
	}

}
