package com.mygdx.game;

import com.badlogic.gdx.math.Vector3;

public class VectorHelper {
	public static Vector3 extendVector(Vector3 vector, float distance) {
		return vector.setLength(distance + vector.len());
	}
}
