package v2;

public class MyVector {

	private double x;
	private double y;

	//Constructores
	public MyVector() {
		x = y = 0.0;
	}

	public MyVector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	////////////// getters & setters ////////////////////////
	public double getX() {return this.x;}
	public double getY() {return this.y;}
	public void setX(int i) {this.x = i;}
	public void setY(int i) {this.y = i;}

	
	public void decelerar(double decel_factor){
		double multi = 1-decel_factor;
		//System.out.println("multi "+multi);
		//this.x = x * multi;
		//this.y = y * multi;
		this.x *= multi;
		this.y *= multi;
	}

	public void readjustModule(double newModule){
		// newModule == maximo
		double diff = newModule / getCurrentModule();

		if (diff == 0) return;

		this.x = diff * this.x;
		this.y = diff * this.y;
	}

	public double getCurrentModule(){
		//System.out.println("modulo: "+Math.sqrt(x*x + y*y));
		return Math.sqrt(x*x + y*y);
	}

	// product of two vectors .....
	public MyVector MultiplicaVectores(MyVector v1) {
		MyVector v2 = new MyVector(this.x * v1.x,  this.y * v1.y);
		return v2;
	}

	//producto vector * entero
	public MyVector MultiplicaVectores(double num) {
		MyVector v2 = new MyVector(this.x * num, this.y * num);
		return v2;
	}

	// Sum of two vectors ....
	public MyVector SumaVectores(MyVector v1) {
		MyVector v2 = new MyVector(this.x + v1.x, this.y + v1.y);
		return v2;
	}

	
	// Convert vector to a string ...
	public String toString() {
		return "Vector(" + this.x + ", " + this.y + ")";
	}

}
