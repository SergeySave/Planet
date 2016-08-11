package com.mygdx.game;

import java.util.Random;

import kurt.spencer.OpenSimplexNoise;

public class ModifiedSimplex {

	private OpenSimplexNoise[] octaves;
	private double[] frequency;
	private double[] amplitude;

	public ModifiedSimplex() {
		this(new Random().nextLong());
	}

	public ModifiedSimplex(long seed) {
		int nOctaves = 9;
		double persistance = 0.6;

		octaves = new OpenSimplexNoise[nOctaves];
		frequency = new double[nOctaves];
		amplitude = new double[nOctaves];

		Random rand = new Random(seed);

		for (int i = 0; i<nOctaves; i++) {
			octaves[i] = new OpenSimplexNoise(rand.nextLong());
			frequency[i] = Math.pow(2, i);
			amplitude[i] = Math.pow(persistance, i);
		}

		frequency[8] = 1.2;
		amplitude[8] = -0.1;
	}

	public double noise(double x, double y, double z) {
		double val = 0;

		for (int i = 0; i<octaves.length; i++) {
			double f = frequency[i];
			double a = amplitude[i];
			if (i == 8) {
				double v = 1-Math.abs(octaves[i].eval(x*f, y*f, z*f)) + 0.5;
				//System.out.println(v);
					val += v*v*v * a;
			} else {
				val += octaves[i].eval(x*f, y*f, z*f) * a;
			}
		}

		return val;
	}
}
