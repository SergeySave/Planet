package com.mygdx.game.buffer;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Container2;
import com.mygdx.game.quadtree.IDeltaLengthGenerator;
import com.mygdx.game.quadtree.MathHelper;

public class VBOGenerator {

	private static final int numComponents = 3+3+1+2;

	public static int getMaxVerticies(int splitAmt) {
		return (numComponents)*((splitAmt)*(splitAmt) + 2*(splitAmt*2+1) + 2*splitAmt-1);
	}

	private static final float derivEpsilonTheta = (float) 1e-3;

	private static int addVertexDataToArray(Vector3 v, float[] vertData, int vn, IDeltaLengthGenerator generator, Vector3 offset, double ampScaleModel, double uC, double vC, boolean offsetVerticies) {
		Vector3 right = v.cpy().rotate(Vector3.Y, derivEpsilonTheta);
		Vector3 down = v.cpy().rotate(Vector3.Y.cpy().crs(v), derivEpsilonTheta);

		double noise1 = generator.getDeltaLength(v);
		double noise2 = generator.getDeltaLength(right);
		double noise3 = generator.getDeltaLength(down);

		float rDelta1 = (float) (noise1 * ampScaleModel);
		float rDelta2 = (float) (noise2 * ampScaleModel);
		float rDelta3 = (float) (noise3 * ampScaleModel);

		if (offsetVerticies) {
			v.setLength(v.len() + rDelta1);
			right.setLength(right.len() + rDelta2);
			down.setLength(down.len() + rDelta3);
		}

		Vector3 normal = (down.cpy().sub(v).crs(right.cpy().sub(v))).nor();

		v.sub(offset);

		vertData[vn++] = v.x; //X
		vertData[vn++] = v.y; //Y
		vertData[vn++] = v.z;			//Z

		vertData[vn++] = normal.x; //NX
		vertData[vn++] = normal.y; //NY
		vertData[vn++] = normal.z;			//NZ

		vertData[vn++] = rDelta1;

		vertData[vn++] = (float)uC;
		vertData[vn++] = (float)vC;

		//float[] colors = getColorForVal(rDelta1);

		//vertData[vn++] = colors[0];		//R
		//vertData[vn++] = colors[1];			//G
		//vertData[vn++] = colors[2];			//B
		//vertData[vn++] = colors[3];		//A
		return vn;
	}

	public static Container2<float[], Vector3> generateVertexData(short type, double x0, double y0, double s, int splitAmt, Matrix4 mat, IDeltaLengthGenerator generator, double ampScaleModel, boolean offsetVerticies) {
		Vector3 offset = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*1/2) + 1), (float)(-2*(x0+s*1/2) + 1))).mul(mat);
		float[] vertData = null;

		if (type == 0b0000) { //Normal
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt+1))]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				for (double dx = 0; dx<=splitAmt; dx++) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b0001) { //Up
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt) + splitAmt*2 + 1)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				for (double dx = 0; dx<=splitAmt; dx+=(dy == 0 ? 0.5 : 1)) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b0010) { //Down
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt) + splitAmt*2 + 1)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == splitAmt) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
			}
		} else if (type == 0b0011) { //Down and Up
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt-1) + 2*(splitAmt*2 + 1))]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == 0 || dy == splitAmt) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
			}
		} else if (type == 0b0100) { //Right
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt+1) + splitAmt)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				for (double dx = 0; dx<=splitAmt; dx++) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
				}
				if (dy != splitAmt) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0+s) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 1, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b0101) { //Right and Up
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt) + splitAmt*2+1 + splitAmt)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == 0) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
				if (dy != splitAmt) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0+s) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 1, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b0110) { //Right and Down
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt) + splitAmt*2+1 + splitAmt)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == splitAmt) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
				if (dy != splitAmt) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0+s) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 1, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b0111) { //Right, Down, and Up
			vertData = new float[(numComponents)*((splitAmt)*(splitAmt-1) + 2*(splitAmt*2+1) + 2*splitAmt-1)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == 0 || dy == splitAmt) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
				if (dy != splitAmt) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0+s) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 1, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b1000) { //Left
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt+1) + splitAmt)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				for (double dx = 0; dx<=splitAmt; dx++) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
				}
				if (dy != splitAmt) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 0, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b1001) { //Left and Up
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt) + splitAmt*2+1 + splitAmt)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == 0) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
				if (dy != splitAmt) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 0, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b1010) { //Left and Down
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt) + splitAmt*2+1 + splitAmt)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == splitAmt) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
				if (dy != splitAmt) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 0, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b1011) { //Left, Down, and Up
			vertData = new float[(numComponents)*((splitAmt)*(splitAmt) + 2*(splitAmt*2+1) + splitAmt-1)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == 0 || dy == splitAmt) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
				if (dy != splitAmt) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 0, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b1100) { // Left and Right
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt+1) + splitAmt*2)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				for (double dx = 0; dx<=splitAmt; dx++) {
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
				}
				if (dy != splitAmt) {
					{
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 0, dy/splitAmt, offsetVerticies);
					}
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0+s) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 1, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b1101) { // Left, Right, and Up
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt) + splitAmt*2+1 + 2*splitAmt)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == 0) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
				if (dy != splitAmt) {
					{
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 0, dy/splitAmt, offsetVerticies);
					}
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0+s) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 1, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b1110) { //Left, Right, and Down
			vertData = new float[(numComponents)*((splitAmt+1)*(splitAmt) + splitAmt*2+1 + 2*splitAmt)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == splitAmt) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
				if (dy != splitAmt) {
					{
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 0, dy/splitAmt, offsetVerticies);
					}
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0+s) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 1, dy/splitAmt, offsetVerticies);
				}
			}
		} else if (type == 0b1111) { //Left, Right, Down, and Up
			vertData = new float[(numComponents)*((splitAmt)*(splitAmt) + 2*(splitAmt*2+1) + 2*splitAmt-1)]; // Size = Components per vertex * num verticies needed
			int vn = 0;

			for (double dy = 0; dy<=splitAmt; dy++) {
				if (dy == 0 || dy == splitAmt) {
					for (double dx = 0; dx<=splitAmt; dx+=0.5) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				} else {
					for (double dx = 0; dx<=splitAmt; dx++) {
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*dy/splitAmt) + 1), (float)(-2*(x0+s*dx/splitAmt) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, dx/splitAmt, dy/splitAmt, offsetVerticies);
					}
				}
				if (dy != splitAmt) {
					{
						Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0) + 1))).mul(mat);
						vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 0, dy/splitAmt, offsetVerticies);
					}
					Vector3 v = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y0+s*(dy+0.5)/splitAmt) + 1), (float)(-2*(x0+s) + 1))).mul(mat);
					vn = addVertexDataToArray(v, vertData, vn, generator, offset, ampScaleModel, 1, dy/splitAmt, offsetVerticies);
				}
			}
		}

		Container2<float[], Vector3> container = new Container2<float[], Vector3>();

		container.one = vertData;
		container.two = offset;

		return container;
	}
}
