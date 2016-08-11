package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.quadtree.QuadTreeSphere;

public class MyGdxGame extends ApplicationAdapter {
	public PerspectiveCamera cam;
	public RenderContext renderContext;
	public Environment environment;
	public Model axes, sphere;
	public ModelInstance ai, sky;
	public Shader skyShader;
	public ModelBatch batch;
	public SpriteBatch sprite;
	private FrameBuffer frameBuffer;
	private Texture texture;
	float planetRadius = 2;
	float minDistFromPlanet = 1;

	boolean needsLODUpdate = true;

	int u_projViewTrans, u_worldTrans;

	private QuadTreeSphere planet, water;
	Atmosphere atm;

	float[][][] heightMap;

	private BitmapFont font;

	private float getDist(float[][][] radDelta, int w, int i, int j) {
		float val = radDelta[w][i][j];
		if (val < 0) {
			return 0;
		}
		return val;
	}

	@Override
	public void create () {
		texture = new Texture(Gdx.files.internal("outputImage.png"));

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Helvetica.ttf"));
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.size = 40;
		param.color = Color.WHITE;
		font = generator.generateFont(param);
		param = null;
		generator.dispose();

		sprite = new SpriteBatch();

		//GLProfiler.enable();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.05f, 0.05f, 0.05f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 08f, -1f, -0.8f, -0.2f));
		//environment.set(new DepthTestAttribute(depthFunc));

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(1000f, 0f, 0f);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 2000f; 
		cam.update();
		
		Simplex simplex = new Simplex(4, 0.4, 256);

		planet = new QuadTreeSphere(cam, environment, Gdx.files.internal("modDef.vertex.glsl"), Gdx.files.internal("modDef.fragment.glsl"),
				new QuadTreeSphere.Config(0.4f, new double[] {Double.POSITIVE_INFINITY, 500, 450, 400, 350, 250, 200, 100, 50}, 4, 1, false, 6, 900/6, true),
				simplex);
		/*
		water = new QuadTreeSphere(cam, environment, Gdx.files.internal("water.vertex.glsl"), Gdx.files.internal("water.fragment.glsl"),
				new QuadTreeSphere.Config(0.4f, new double[] {Double.POSITIVE_INFINITY, 500, 450, 400, 350, 250, 200, 100, 50}, 4, 1, false, 6, 900/6, false),
				(v)->0);*/

		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		//atm = new Atmosphere();
		//atm.setup(Gdx.files.internal("test.vertex.glsl"), Gdx.files.internal("test.fragment.glsl"));

		batch = new ModelBatch();

		ModelBuilder modelBuilder = new ModelBuilder();
		int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

		modelBuilder.begin();{
			MeshPartBuilder xMAxis = modelBuilder.part("xMAxis", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(0.25f, 0, 0, 1)));
			xMAxis.box(-2500, 0, 0, 5000, 1f, 1f);
			MeshPartBuilder xPAxis = modelBuilder.part("xPAxis", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(1, 0, 0, 1)));
			xPAxis.box(2500, 0, 0, 5000, 1f, 1f);
			MeshPartBuilder yMAxis = modelBuilder.part("yMAxis", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(0, 0.25f, 0, 1)));
			yMAxis.box(0, -2500, 0, 1f, 5000, 1f);
			MeshPartBuilder yPAxis = modelBuilder.part("yPAxis", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(0, 1, 0, 1)));
			yPAxis.box(0, 2500, 0, 1f, 5000, 1f);
			MeshPartBuilder zMAxis = modelBuilder.part("zMAxis", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(0, 0, 0.25f, 1)));
			zMAxis.box(0, 0, -2500, 1f, 1f, 5000);
			MeshPartBuilder zPAxis = modelBuilder.part("zPAxis", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(0, 0, 1, 1)));
			zPAxis.box(0, 0, 2500, 1f, 1f, 5000);
		}axes = modelBuilder.end();

		ai = new ModelInstance(axes);
		
		sphere = modelBuilder.createSphere(1000*2, 1000*2, 1000*2, 90, 90, new Material(), VertexAttributes.Usage.Position);
		
		sky = new ModelInstance(sphere);
		
		skyShader = new DefaultShader(sky.getRenderable(new Renderable()), new DefaultShader.Config(Gdx.files.internal("skyGround.vertex.glsl").readString(), Gdx.files.internal("skyGround.fragment.glsl").readString()));

		Gdx.input.setInputProcessor(new InputProcessor() {
			public boolean keyDown(int keycode) {return false;}
			public boolean keyUp(int keycode) {return false;}
			public boolean keyTyped(char character) {return false;}
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}
			public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
			public boolean mouseMoved(int screenX, int screenY) {return false;}
			public boolean scrolled(int amount) {
				moveOut(amount);
				return false;
			}
		});

		System.out.println("Done");
	}

	private void moveOut(float units) {
		cam.position.setLength(cam.position.len() + units);
		cam.update();
		//cam.translate(cam.position.cpy()..setLength(units));
	}

	@Override
	public void render () {

		final float rotateSpeed = 1;//cam.position.len()/1000;

		float dY = (Gdx.input.isKeyPressed(Keys.W) ? -1 : 0) + (Gdx.input.isKeyPressed(Keys.S) ? 1 : 0);
		float dX = (Gdx.input.isKeyPressed(Keys.D) ? -1 : 0) + (Gdx.input.isKeyPressed(Keys.A) ? 1 : 0);

		float dR = (Gdx.input.isKeyPressed(Keys.Q) ? -1 : 0) + (Gdx.input.isKeyPressed(Keys.E) ? 1 : 0);

		float dU = ((Gdx.input.isKeyPressed(Keys.UP) ? -1 : 0) + (Gdx.input.isKeyPressed(Keys.DOWN) ? 1 : 0))*4;
		float dL = ((Gdx.input.isKeyPressed(Keys.RIGHT) ? -1 : 0) + (Gdx.input.isKeyPressed(Keys.LEFT) ? 1 : 0))*4;

		Vector3 tmpV1 = cam.position.cpy().crs(cam.up);
		cam.rotateAround(new Vector3(), tmpV1.nor(), dY * -rotateSpeed);
		cam.rotateAround(new Vector3(), cam.up, dX * -rotateSpeed);

		cam.rotate(cam.direction.cpy().rotate(cam.up, 90), dU * rotateSpeed);
		cam.rotate(cam.position, dL * rotateSpeed);
		cam.rotate(cam.direction, dR * rotateSpeed);

		//Angle3D a1 = getAngle(cam.position);
		//float height = getHeightAt(a1)+minDistFromPlanet;
		//System.out.println(height + " c: " + cam.position.len());

		//if (cam.position.len2() < height*height) {
		//	cam.position.setLength(height);
		//}

		//Gdx.gl.glEnable(GL20.GL_BLEND);
		//Gdx.gl.glBlendColor(1, 1, 1, 1);
		//Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		cam.update();

		//frameBuffer.begin(); {
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			texture.bind();
			batch.begin(cam);

			batch.getRenderContext().setDepthMask(true);
			batch.getRenderContext().setDepthTest(GL20.GL_LESS, cam.near, cam.far);
			batch.getRenderContext().setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			//batch.render(water);
			//batch.end();
			
			//batch.begin(cam);

			//batch.getRenderContext().setDepthMask(true);
			//batch.getRenderContext().setDepthTest(GL20.GL_LESS, cam.near, cam.far);
			//batch.getRenderContext().setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			batch.render(planet);
			//batch.render(sky, skyShader);
			batch.end();

		//}frameBuffer.end();

		//atm.render(cam, frameBuffer);

		//System.out.println("Draw Calls: " + GLProfiler.drawCalls);
		//System.out.println("Shade Switch: " + GLProfiler.shaderSwitches);
		//GLProfiler.reset();

		//Gdx.gl.glDisable(GL20.GL_BLEND);

		planet.update(cam);
		//water.update(cam);

		sprite.begin();
		font.draw(sprite, "Leaves: " + planet.getLeaves().size(), 10, 50);
		sprite.end();
	}

	@Override
	public void dispose () {
		//shader.dispose();
		axes.dispose();
		planet.dispose();
		//water.dispose();
		frameBuffer.dispose();
		//atm.dispose();
		sphere.dispose();
	}

	@Override
	public void resume () {
	}

	@Override
	public void resize (int width, int height) {
		if (frameBuffer != null) {
			frameBuffer.dispose();
			frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
		}
	}

	@Override
	public void pause () {
	}

	public Vector3 MapCubeToSphere( Vector3 vPosition )
	{
		float x2 = vPosition.x * vPosition.x;
		float y2 = vPosition.y * vPosition.y;
		float z2 = vPosition.z * vPosition.z;

		vPosition.x = (float) (vPosition.x * Math.sqrt( 1.0f - ( y2 * 0.5f ) - ( z2 * 0.5f ) + ( (y2 * z2) / 3.0f ) ));
		vPosition.y = (float) (vPosition.y * Math.sqrt( 1.0f - ( z2 * 0.5f ) - ( x2 * 0.5f ) + ( (z2 * x2) / 3.0f ) ));
		vPosition.z = (float) (vPosition.z * Math.sqrt( 1.0f - ( x2 * 0.5f ) - ( y2 * 0.5f ) + ( (x2 * y2) / 3.0f ) ));

		return vPosition;
	}

	public Angle3D getAngle(Vector3 v) {
		Angle3D angle = new Angle3D();

		angle.lat = Math.PI/2 - Math.acos(v.y / v.len());
		angle.lon = -Math.atan2(v.z, v.x);

		return angle;
	}

	public Vector3 fromAngle(Angle3D a, float r) {

		return new Vector3(
				(float)(r * Math.sin(Math.PI/2 - a.lat) * Math.cos(-a.lon)),
				(float)(r * Math.cos(Math.PI/2 - a.lat)),
				(float)(r * Math.sin(Math.PI/2 - a.lat) * Math.sin(-a.lon)));
	}

	public float getHeightAt(Angle3D angle) {
		Vector3 v = fromAngle(angle, 1);

		float max = Math.max(Math.abs(v.x),
				Math.max(Math.abs(v.y),
						Math.abs(v.z)));

		v.x /= max;
		v.y /= max;
		v.z /= max;

		int side = -1;
		float i = -1;
		float j = -1;
		if (v.z == -1) {
			side  = 0;
			i = v.x/2 + 0.5f;
			j = v.y/2 + 0.5f;
		} else if (v.z == 1) {
			side = 1;
			i = v.x/2 + 0.5f;
			j = v.y/2 + 0.5f;
		} else if (v.x == -1) {
			side = 2;
			i = v.z/2 + 0.5f;
			j = v.y/2 + 0.5f;
		} else if (v.x == 1) {
			side = 3;
			i = v.z/2 + 0.5f;
			j = v.y/2 + 0.5f;
		} else if (v.y == -1) {
			side = 4;
			i = v.z/2 + 0.5f;
			j = v.x/2 + 0.5f;
		} else if (v.y == 1) {
			side = 5;
			i = v.z/2 + 0.5f;
			j = v.x/2 + 0.5f;
		}

		i *= heightMap[side].length-1;
		j *= heightMap[side][(int)i].length-1;

		int i1 = (int)i;
		int i2 = i1 + 1;
		int j1 = (int)j;
		int j2 = j1 + 1;

		float iDecimal = i - i1;
		float iD2 = 1-iDecimal;
		float jDecimal = j - j1;
		float jD2 = 1-jDecimal;

		double sqrt2 = Math.sqrt(2);

		float hi1j1 = getDist(heightMap, side, i1, j1);
		float hi2j1 = getDist(heightMap, side, i2, j1);
		float hi1j2 = getDist(heightMap, side, i1, j2);
		float hi2j2 = getDist(heightMap, side, i2, j2);

		float i1j1 = (float) (sqrt2-Math.sqrt(iDecimal*iDecimal + jDecimal*jDecimal));
		float i2j1 = (float) (sqrt2-Math.sqrt(iD2*iD2 + jDecimal*jDecimal));
		float i1j2 = (float) (sqrt2-Math.sqrt(iDecimal*iDecimal + jD2*jD2));
		float i2j2 = (float) (sqrt2-Math.sqrt(iD2*iD2 + jD2*jD2));

		float total = i1j1 + i2j1 + i1j2 + i2j2;

		float height = hi1j1*i1j1/total + hi2j1*i2j1/total + hi1j2*i1j2/total + hi2j2*i2j2/total;

		return height;
	}
}
