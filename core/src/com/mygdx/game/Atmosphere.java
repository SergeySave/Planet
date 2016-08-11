package com.mygdx.game;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Atmosphere {
	private ShaderProgram atmosphereShader;
	private Mesh quad;
	
	private int cameraDir, u_near, u_far;

	public Atmosphere() {
	}

	public void setup(FileHandle vertex, FileHandle fragment) {
		float[] verts = new float[20];
		int i = 0;
		verts[i++] = -1.f; // x1
		verts[i++] = -1.f; // y1
		verts[i++] =  0.f; // u1
		verts[i++] =  0.f; // v1
		verts[i++] = 0;
		
		verts[i++] =  -1.f; // x2
		verts[i++] = 1.f; // y2
		verts[i++] =  0.f; // u2
		verts[i++] =  1.f; // v2
		verts[i++] = 1;
		
		verts[i++] = 1.f; // x4
		verts[i++] =  -1.f; // y4
		verts[i++] =  1.f; // u4
		verts[i++] =  0.f; // v4
		verts[i++] = 2;
		
		verts[i++] =  1.f; // x3
		verts[i++] =  1.f; // y2
		verts[i++] =  1.f; // u3
		verts[i++] =  1.f; // v3
		verts[i++] = 3;
		quad = new Mesh(true, 4, 0
				, new VertexAttribute(Usage.Position, 2, "a_position")
				, new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0")
				, new VertexAttribute(Usage.Generic, 1, "vertexNum"));
		quad.setVertices(verts);

		atmosphereShader = new ShaderProgram(vertex, fragment);
		if (!atmosphereShader.isCompiled())
			throw new GdxRuntimeException(atmosphereShader.getLog());
		cameraDir = atmosphereShader.getUniformLocation("cameraDir[0]");
		u_near = atmosphereShader.getUniformLocation("u_near");
		u_far = atmosphereShader.getUniformLocation("u_far");
	}

	public void dispose() {
		quad.dispose();
		atmosphereShader.dispose();
	}

	public void render(Camera cam, FrameBuffer buff) {
		buff.getColorBufferTexture().bind(); {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			atmosphereShader.begin();{
				System.out.println(Arrays.deepToString(cam.frustum.planePoints));
				float[] values = new float[3*4];
				Vector3 v;
				v = cam.frustum.planePoints[4].cpy().sub(cam.position).nor();
				values[0] = v.x;
				values[1] = v.y;
				values[2] = v.z;
				v = cam.frustum.planePoints[5].cpy().sub(cam.position).nor();
				values[3] = v.x;
				values[4] = v.y;
				values[5] = v.z;
				v = cam.frustum.planePoints[6].cpy().sub(cam.position).nor();
				values[6] = v.x;
				values[7] = v.y;
				values[8] = v.z;
				v = cam.frustum.planePoints[7].cpy().sub(cam.position).nor();
				values[9] = v.x;
				values[10] = v.y;
				values[11] = v.z;
				atmosphereShader.setUniform3fv(cameraDir, values, 0, values.length);
				//atmosphereShader.setUniformf("medFace", cam.direction);
				quad.render(atmosphereShader, GL20.GL_TRIANGLE_STRIP, 0, 4);
			}
			atmosphereShader.end();
		}
	}
}
