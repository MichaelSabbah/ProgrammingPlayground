package playground.logic.helpers;

public class Location {
	private double x;
	private double y;

	public Location() {
		this.x = 0.0;
		this.y = 0.0;
	}

	public Location(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		Location other = (Location)obj;
		return this.getX() == other.getX() &&
				this.getY() == other.getY();
	}

	@Override
	public String toString() {
		return "Location [x=" + x + ", y=" + y + "]";
	}
}
