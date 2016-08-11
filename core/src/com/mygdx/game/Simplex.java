package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.quadtree.IDeltaLengthGenerator;

import kurt.spencer.OpenSimplexNoise;

public class Simplex implements IDeltaLengthGenerator{
	
	private OpenSimplexNoise[] octaves;
	private double[] frequency;
	private double[] amplitude;
	
	public Simplex(int nOctaves, double persistance) {
		this(nOctaves, persistance, new Random().nextLong());
	}
	
	public Simplex(int nOctaves, double persistance, long seed) {
		octaves = new OpenSimplexNoise[nOctaves];
		frequency = new double[nOctaves];
		amplitude = new double[nOctaves];
		
		Random rand = new Random(seed);
		
		for (int i = 0; i<nOctaves; i++) {
			octaves[i] = new OpenSimplexNoise(rand.nextLong());
			frequency[i] = Math.pow(2, i);
			amplitude[i] = Math.pow(persistance, i);
		}
	}
	
	public Simplex(float[] frequencies, float[] amplitudes, long seed) {
		int nOctaves = Math.min(frequencies.length, amplitudes.length);
		octaves = new OpenSimplexNoise[nOctaves];
		frequency = new double[nOctaves];
		amplitude = new double[nOctaves];
				
		Random rand = new Random(seed);
		
		for (int i = 0; i<nOctaves; i++) {
			octaves[i] = new OpenSimplexNoise(rand.nextLong());
			frequency[i] = frequencies[i];
			amplitude[i] = amplitudes[i];
		}
		
	}
	
	public double noise(double x, double y) {
		double val = 0;
		
		for (int i = 0; i<octaves.length; i++) {
			double f = frequency[i];
			double a = amplitude[i];
			val += octaves[i].eval(x*f, y*f) * a;
		}
		
		return val;
	}
	
	public double noise(double x, double y, double z) {
		double val = 0;
		
		for (int i = 0; i<octaves.length; i++) {
			double f = frequency[i];
			double a = amplitude[i];
			val += octaves[i].eval(x*f, y*f, z*f) * a;
		}
		
		return val;
	}
	
	public double noise(double x, double y, double z, double w) {
		double val = 0;
		
		for (int i = 0; i<octaves.length; i++) {
			double f = frequency[i];
			double a = amplitude[i];
			val += octaves[i].eval(x*f, y*f, z*f, w*f) * a;
		}
		
		return val;
	}

	@Override
	public double getDeltaLength(Vector3 v3) {
		return noise(v3.x, v3.y, v3.z);
	}
}
