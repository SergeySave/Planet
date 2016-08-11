package com.mygdx.game.quadtree;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Angle3D;

public class MathHelper {
	public static double dist2FromPointToLine(Vector3 l1, Vector3 l2, Vector3 p) {
		double t = -((l1.cpy().sub(p).dot(l2.cpy().sub(l1)))/(l2.cpy().sub(l1).len2()));
		
		if (t < 0) {
			return l1.cpy().sub(p).len2();
		} else if (t > 1) {
			return l2.cpy().sub(p).len2();
		}
		
		return l1.cpy().sub(p).add(l2.cpy().sub(l1).scl((float)t)).len2();
	}
	
	public static double dist2FromPointToRect(Vector3 point, Vector3 ul, Vector3 ur, Vector3 dl, Vector3 dr) {
		return Math.min(dist2FromPointToTri(point, ul, dl, ur), dist2FromPointToTri(point, dr, ur, dl));
	}
	
	public static double dist2FromPointToTri(Vector3 point, Vector3 mainPoint, Vector3 point1, Vector3 point2) {
		Vector3 normal = point1.cpy().sub(mainPoint).crs(point2.cpy().sub(mainPoint));
		
		double distAlongNormal = Math.abs(normal.x * (point.x - mainPoint.x) + normal.y * (point.y - mainPoint.y) + normal.z * (point.z - mainPoint.z)) / normal.len();
		
		Vector3 pointInPlane = point.cpy().sub(normal.cpy().setLength((float)distAlongNormal));
		
		double areaTri = Math.abs(point1.cpy().sub(mainPoint).len() * point2.cpy().sub(mainPoint).len()) / 2;
		double alpha = Math.abs(point1.cpy().sub(pointInPlane).len() * point2.cpy().sub(pointInPlane).len()) / (2 * areaTri);
		double beta = Math.abs(mainPoint.cpy().sub(pointInPlane).len() * point2.cpy().sub(pointInPlane).len()) / (2 * areaTri);
		double gamma = 1 - alpha - beta;
		
		if (alpha >= 0 && beta >= 0 && gamma >= 0 && alpha <= 1 && beta <= 1 && gamma <= 1) {
			return (point.len() - mainPoint.len())*(point.len() - mainPoint.len());
		} else {
			return Math.min(dist2FromPointToLine(mainPoint, point1, point), Math.min(dist2FromPointToLine(mainPoint, point2, point), dist2FromPointToLine(point2, point1, point)));
		}
	}

	public static Vector3 mapCubePosToSphere( Vector3 vPosition )
	{
		float x2 = vPosition.x * vPosition.x;
		float y2 = vPosition.y * vPosition.y;
		float z2 = vPosition.z * vPosition.z;

		vPosition.x = (float) (vPosition.x * Math.sqrt( 1.0f - ( y2 * 0.5f ) - ( z2 * 0.5f ) + ( (y2 * z2) / 3.0f ) ));
		vPosition.y = (float) (vPosition.y * Math.sqrt( 1.0f - ( z2 * 0.5f ) - ( x2 * 0.5f ) + ( (z2 * x2) / 3.0f ) ));
		vPosition.z = (float) (vPosition.z * Math.sqrt( 1.0f - ( x2 * 0.5f ) - ( y2 * 0.5f ) + ( (x2 * y2) / 3.0f ) ));

		return vPosition;
	}

	public static Angle3D getAngle(Vector3 v) {
		Angle3D angle = new Angle3D();

		angle.lat = Math.PI/2 - Math.acos(v.y / v.len());
		angle.lon = -Math.atan2(v.z, v.x);

		return angle;
	}

	public static Vector3 fromAngle(Angle3D a, float r) {

		return new Vector3(
				(float)(r * Math.sin(Math.PI/2 - a.lat) * Math.cos(-a.lon)),
				(float)(r * Math.cos(Math.PI/2 - a.lat)),
				(float)(r * Math.sin(Math.PI/2 - a.lat) * Math.sin(-a.lon)));
	}
}
