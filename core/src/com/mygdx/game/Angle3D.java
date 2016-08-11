package com.mygdx.game;

public class Angle3D {
	public double lat, lon;
	
	
	private static final double epsilon = 1e-4;
	
	public boolean closeEnough(Angle3D other) {
		return (int)(lat/epsilon) == (int)(other.lat/epsilon) && (int)(lon/epsilon) == (int)(other.lon/epsilon);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Angle3D)) {
			return false;
		}
		Angle3D other = (Angle3D)obj;
		return other.lat == lat && other.lon == lon;
	}
	
	@Override
	public String toString() {
		if (lon == -0) {
			lon = 0;
		}
		return "[Angle3D lat=" + Math.toDegrees(lat) + " lon=" + Math.toDegrees(lon) + "]";
	}
}
