package com.mygdx.game.quadtree;

import java.util.List;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.buffer.IBOGenerator;
import com.mygdx.game.buffer.VBOGenerator;

public class QuadTreeSphere implements RenderableProvider {
	
	//ALL QuadTrees Data
	public static final byte FLAG_UP = 		0b0001;
	public static final byte FLAG_DOWN = 	0b0010;
	public static final byte FLAG_RIGHT = 	0b0100;
	public static final byte FLAG_LEFT = 	0b1000;
	
	//Per QuadTreeData
	public final Material mat = new Material();
	private Node xp, xm, yp, ym, zp, zm;
	private short[][] indicies = new short[0b10000][];
	private int maxVerticies = 0;
	private int maxIndicies = 0;
	public IDeltaLengthGenerator generator;
	private Shader shader;
	private Environment env;
	private FileHandle vert, frag;
	public Config config;
	//private Camera cam;
	
	

	public QuadTreeSphere(Camera cam, Environment e, FileHandle vert, FileHandle frag, Config config, IDeltaLengthGenerator gen) {
		//this.cam = cam;
		this.env = e;
		this.config = config;
		
		this.vert = vert;
		this.frag = frag;
		
		generator = gen;
		
		for (int i = 0; i<indicies.length; i++) {
			indicies[i] = IBOGenerator.generateIndiciesArray((byte)i, config.splitAmt);
			maxIndicies = Math.max(maxIndicies, indicies[i].length);
		}
		
		maxVerticies = VBOGenerator.getMaxVerticies(config.splitAmt);

		//lvl = 0, no parent, x = 0, y = 0, size = 1, set side # correctly
		xp = new Node(0, null, 0, 0, config.FULL_SIZE, 0, this);
		zp = new Node(0, null, 0, 0, config.FULL_SIZE, 1, this);
		xm = new Node(0, null, 0, 0, config.FULL_SIZE, 2, this);
		zm = new Node(0, null, 0, 0, config.FULL_SIZE, 3, this);
		yp = new Node(0, null, 0, 0, config.FULL_SIZE, 4, this);
		ym = new Node(0, null, 0, 0, config.FULL_SIZE, 5, this);

		xp.above.add(yp);
		xp.left.add(zp);
		xp.right.add(zm);
		xp.below.add(ym);

		zp.above.add(yp);
		zp.left.add(xm);
		zp.right.add(xp);
		zp.below.add(ym);

		xm.above.add(yp);
		xm.left.add(zm);
		xm.right.add(zp);
		xm.below.add(ym);

		zm.above.add(yp);
		zm.left.add(xp);
		zm.right.add(xm);
		zm.below.add(ym);

		yp.above.add(xm);
		yp.left.add(zp);
		yp.right.add(zm);
		yp.below.add(xp);

		ym.above.add(xp);
		ym.left.add(zp);
		ym.right.add(zm);
		ym.below.add(xm);
		
		//mat.set(IntAttribute.createCullFace(GL20.GL_NONE));
	}

	/**
	 * @return the xp
	 */
	public Node getXp() {
		return xp;
	}

	/**
	 * @return the xm
	 */
	public Node getXm() {
		return xm;
	}

	/**
	 * @return the yp
	 */
	public Node getYp() {
		return yp;
	}

	/**
	 * @return the ym
	 */
	public Node getYm() {
		return ym;
	}

	/**
	 * @return the zp
	 */
	public Node getZp() {
		return zp;
	}

	/**
	 * @return the zm
	 */
	public Node getZm() {
		return zm;
	}

	public List<Node> getAllNodes() {
		List<Node> n = xp.getAllNodes();
		n.addAll(xm.getAllNodes());
		n.addAll(yp.getAllNodes());
		n.addAll(ym.getAllNodes());
		n.addAll(zp.getAllNodes());
		n.addAll(zm.getAllNodes());
		return n;
	}

	public List<Node> getLeaves() {
		List<Node> n = xp.getLeaves();
		n.addAll(xm.getLeaves());
		n.addAll(yp.getLeaves());
		n.addAll(ym.getLeaves());
		n.addAll(zp.getLeaves());
		n.addAll(zm.getLeaves());
		return n;
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for (Node n : new Node[]{xp, xm, zp, zm, yp, ym}) { //
			getRenderables(n, renderables, pool);
		}
	}

	private void getRenderables(Node node, Array<Renderable> renderables, Pool<Renderable> pool) {
		if (node == null) {
			return;
		}
		if (node.isLeaf()) {
			Renderable r = convertToRenderable(node, pool.obtain());
			if (r != null) {
				renderables.add(r);
			} else {
				pool.free(r);
			}
		} else {
			getRenderables(node.getNodeUpLeft(), renderables, pool);
			getRenderables(node.getNodeUpRight(), renderables, pool);
			getRenderables(node.getNodeDownLeft(), renderables, pool);
			getRenderables(node.getNodeDownRight(), renderables, pool);
		}
	}

	private Renderable convertToRenderable(Node n, Renderable r) {
		Mesh m = n.getMesh(indicies, maxVerticies, maxIndicies);
		if (config.wireframe) {
			r.meshPart.set("NodeMeshPart", m, 0, m.getNumVertices(), GL20.GL_LINE_STRIP);
		} else {
			r.meshPart.set("NodeMeshPart", m, 0, m.getNumVertices(), GL20.GL_TRIANGLE_STRIP);
		}
		r.environment = env;
		r.material = mat;
		r.worldTransform.idt().scl(config.outsideScale).translate(n.getOffset());
		
		if (shader == null) {
			shader = new DefaultShader(r, new DefaultShader.Config(vert.readString(), frag.readString()));
			shader.init();
		}
		r.shader = shader;
		
		//r.worldTransform.set(getMatrixForNode(n));
		return r;
	}
	
	public Matrix4 getMatrixForNode(Node n, boolean actual) {
		Matrix4 mat = new Matrix4();
		mat.scl(config.insideScale);
		if (actual) {
			mat.scl(config.outsideScale);
		}
		if (n.getSide() == 1) {
			mat.rotate(Vector3.Y, -90);
		} else if (n.getSide() == 2) {
			mat.rotate(Vector3.Y, 180);
		} else if (n.getSide() == 3) {
			mat.rotate(Vector3.Y, 90);
		} else if (n.getSide() == 4) {
			mat.rotate(Vector3.Z, 90);
		} else if (n.getSide() == 5) {
			mat.rotate(Vector3.Z, -90);
		}
		return mat;
	}

	public void dispose() {
		xp.dispose(true);
		xm.dispose(true);
		zp.dispose(true);
		zm.dispose(true);
		yp.dispose(true);
		ym.dispose(true);
	}
	
	public void update(Camera cam) {
		Vector3 v3 = cam.position.cpy();
		Vector3 face = cam.direction.cpy();

		List<Node> check = getLeaves();

		for (Node n : check) {
			if (!n.isLeaf()) {
				continue;
			}
			int lod = n.getPreferredLOD(v3, face);
			if (lod > n.getLevel()) {
				n.split();
			} else if (lod < n.getLevel()) {
				Node p = n.getParent();
				if (p != null) {
					int plod = p.getPreferredLOD(v3, face);
					if (plod <= lod) {
						p.combine();
					}
				}
			}
		}
	}
	
	public static class Config {
		public double ampScaleModel;
		public double[] thresholds;
		public int splitAmt;
		public double FULL_SIZE;
		public boolean wireframe;
		public float insideScale;
		public float outsideScale;
		public boolean moveVerticies;
		public Config(double ampScaleModel, double[] thresholds, int splitAmt, double fULL_SIZE, boolean wireframe, float insideScale, float outsideScale,boolean moveVerticies) {
			this.ampScaleModel = ampScaleModel;
			this.thresholds = thresholds;
			this.splitAmt = splitAmt;
			FULL_SIZE = fULL_SIZE;
			this.wireframe = wireframe;
			this.insideScale = insideScale;
			this.outsideScale = outsideScale;
			this.moveVerticies = moveVerticies;
		}
		
	}
}
