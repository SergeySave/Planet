package com.mygdx.game.quadtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Container2;
import com.mygdx.game.buffer.VBOGenerator;

public class Node {

	private boolean isLeaf, isDirty;
	private Node nodeUpLeft, nodeUpRight, nodeDownLeft, nodeDownRight, parent;
	protected Set<Node> above, left, below, right;
	private int level, side;
	private double x, y, s;
	private Mesh mesh;
	private QuadTreeSphere qt;
	private Vector3 offset;
	private double[] radDeltas;

	public Node(int lvl, Node parent, double x, double y, double s, int side, QuadTreeSphere qtc) {
		isLeaf = true;
		qt = qtc;
		isDirty = true;
		this.x = x;
		this.y = y;
		this.s = s;
		this.side = side;
		level = lvl;
		this.parent = parent;
		above = new HashSet<Node>();
		below = new HashSet<Node>();
		right = new HashSet<Node>();
		left = new HashSet<Node>();
	}

	private MapReturn getMappings() {
		MapReturn mr = new MapReturn();

		//Positive X Axis
		mr.xMapUp = (x,y)->x; mr.xMapDown = mr.xMapUp;// mr.xMapRight = mr.xMapUp; mr.xMapLeft = mr.xMapUp;
		mr.yMapUp = (x,y)->y;/* mr.yMapDown = mr.yMapUp;*/ mr.yMapRight = mr.yMapUp; mr.yMapLeft = mr.yMapUp;
		mr.revUp = (n)->n.below;
		mr.revDown = (n)->n.above;
		mr.revLeft = (n)->n.right;
		mr.revRight = (n)->n.left;
		mr.axisMapUp = (n)->n.getXPosition();
		mr.axisMapRight = (n)->n.getYPosition();
		mr.axisMapDown = (n)->n.getXPosition();
		mr.axisMapLeft = (n)->n.getYPosition();

		if (y==0) {
			mr.xMapUp = (x,y)->x;
			mr.yMapUp = (x,y)->qt.config.FULL_SIZE;
		} else if (y+s == qt.config.FULL_SIZE) {
			mr.xMapDown = (x,y)->x;
			//mr.yMapDown = (x,y)->0d;
		}
		if (x==0) {
			//mr.xMapLeft = (x,y)->QuadTreeCube.FULL_SIZE;
			mr.yMapLeft = (x,y)->y;
		} else if (x+s == qt.config.FULL_SIZE) {
			//mr.xMapRight = (x,y)->0d;
			mr.yMapRight = (x,y)->y;
		}

		if (side == 1) { 	//Positive Z Axis
			if (y==0) {
				mr.revUp = (n)->n.left;
				mr.xMapUp = (x,y)->x;
				//mr.yMapUp = (x,y)->x;
				mr.axisMapUp = (n)->n.getYPosition();
			}
			if (y+s==qt.config.FULL_SIZE) {
				mr.revDown = (n)->n.left;
				mr.xMapDown = (x,y)->qt.config.FULL_SIZE-x-s/2;
				//mr.yMapDown = (x,y)->QuadTreeCube.FULL_SIZE-x;
				mr.axisMapDown = (n)->n.getYPosition();
			}
		} else if (side == 2) { 	//Negative X Axis
			if (y==0) {
				mr.revUp = (n)->n.above;
				mr.xMapUp = (x,y)->qt.config.FULL_SIZE-x-s/2;
				//mr.yMapUp = (x,y)->0d;
			}
			if (y+s==qt.config.FULL_SIZE) {
				mr.revDown = (n)->n.below;
				mr.xMapDown = (x,y)->qt.config.FULL_SIZE-x-s/2;
				//mr.yMapDown = (x,y)->0d;
			}
		} else if (side == 3) { 	//Negative Z Axis
			if (y==0) {
				mr.revUp = (n)->n.right;
				mr.xMapUp = (x,y)->qt.config.FULL_SIZE-x-s/2;
				//mr.yMapUp = (x,y)->qt.FULL_SIZE-x-s/2;
				mr.axisMapUp = (n)->n.getYPosition();
			}
			if (y+s==qt.config.FULL_SIZE) {
				mr.revDown = (n)->n.right;
				mr.xMapDown = (x,y)->x;
				//mr.yMapDown = (x,y)->x;
				mr.axisMapDown = (n)->n.getYPosition();
			}
		} else if (side == 4) { 	//Positive Y Axis
			if (y==0) {
				mr.revUp = (n)->n.above;
				mr.xMapUp = (x,y)->qt.config.FULL_SIZE-x-s/2;
				//mr.yMapUp = (x,y)->0d;
			}
			if (x==0) {
				mr.revLeft = (n)->n.above;
				//mr.xMapLeft = (x,y)->y;
				mr.yMapLeft = (x,y)->y;
				mr.axisMapLeft = (n)->n.getXPosition();
			}
			if (x+s==qt.config.FULL_SIZE) {
				mr.revRight = (n)->n.above;
				//mr.xMapRight = (x,y)->QuadTreeCube.FULL_SIZE-y;
				mr.yMapRight = (x,y)->qt.config.FULL_SIZE-y-s/2;
				mr.axisMapRight = (n)->n.getXPosition();
			}
		} else if (side == 5) { 	//Negative Y Axis
			if (y+s==qt.config.FULL_SIZE) {
				mr.revDown = (n)->n.below;
				mr.xMapDown = (x,y)->qt.config.FULL_SIZE-x-s/2;
				//mr.yMapDown = (x,y)->QuadTreeCube.FULL_SIZE;
			}
			if (x==0) {
				mr.revLeft = (n)->n.below;
				//mr.xMapLeft = (x,y)->QuadTreeCube.FULL_SIZE-y;
				mr.yMapLeft = (x,y)->qt.config.FULL_SIZE-y-s/2;
				mr.axisMapLeft = (n)->n.getXPosition();
			}
			if (x+s==qt.config.FULL_SIZE) {
				mr.revRight = (n)->n.below;
				//mr.xMapRight = (x,y)->y;
				mr.yMapRight = (x,y)->y;
				mr.axisMapRight = (n)->n.getXPosition();
			}
		}

		return mr;
	}

	public void split() {
		if (!isLeaf) {
			return;
		}
		nodeUpLeft = new Node(level + 1, this, x, y, s/2, side, qt);
		nodeUpRight = new Node(level + 1, this, x + s/2, y, s/2, side, qt);
		nodeDownLeft = new Node(level + 1, this, x, y + s/2, s/2, side, qt);
		nodeDownRight = new Node(level + 1, this, x + s/2, y + s/2, s/2, side, qt);

		//Set all easily known nodes
		nodeUpLeft.below.add(nodeDownLeft);
		nodeUpLeft.right.add(nodeUpRight);
		nodeUpRight.below.add(nodeDownRight);
		nodeUpRight.left.add(nodeUpLeft);
		nodeDownRight.above.add(nodeUpRight);
		nodeDownRight.left.add(nodeDownLeft);
		nodeDownLeft.above.add(nodeUpLeft);
		nodeDownLeft.right.add(nodeDownRight);

		MapReturn mr = getMappings();

		getAdjacent().forEach((n2)->n2.setDirty());

		if (above.size() == 1) {
			Node n = above.iterator().next();
			if (mr.revUp.apply(n).size() > 1) {
				n.split();
			}
		}
		if (below.size() == 1) {
			Node n = below.iterator().next();
			if (mr.revDown.apply(n).size() > 1) {
				n.split();
			}
		}
		if (right.size() == 1) {
			Node n = right.iterator().next();
			if (mr.revRight.apply(n).size() > 1) {
				n.split();
			}
		}
		if (left.size() == 1) {
			Node n = left.iterator().next();
			if (mr.revLeft.apply(n).size() > 1) {
				n.split();
			}
		}

		for (Node n : above) {
			mr.revUp.apply(n).remove(this);
			double ulX = mr.xMapUp.apply(nodeUpLeft.getXPosition(),nodeUpLeft.getYPosition());
			double urX = mr.xMapUp.apply(nodeUpRight.getXPosition(),nodeUpRight.getYPosition());
			double nX = mr.axisMapUp.apply(n);
			if (nX <= ulX && nX + 1/n.getInvSize() >ulX) {
				mr.revUp.apply(n).add(nodeUpLeft);
				nodeUpLeft.above.add(n);
			}
			if (nX <= urX && nX + 1/n.getInvSize() >urX) {
				mr.revUp.apply(n).add(nodeUpRight);
				nodeUpRight.above.add(n);
			}
		}
		above.clear();
		for (Node n : below) {
			mr.revDown.apply(n).remove(this);
			double dlX = mr.xMapDown.apply(nodeDownLeft.getXPosition(),nodeDownLeft.getYPosition());
			double drX = mr.xMapDown.apply(nodeDownRight.getXPosition(),nodeDownRight.getYPosition());
			double nX = mr.axisMapDown.apply(n);
			if (nX <= dlX && nX + 1/n.getInvSize() >dlX) {
				mr.revDown.apply(n).add(nodeDownLeft);
				nodeDownLeft.below.add(n);
			}
			if (nX <= drX && nX + 1/n.getInvSize() >drX) {
				mr.revDown.apply(n).add(nodeDownRight);
				nodeDownRight.below.add(n);
			}
		}
		below.clear();
		for (Node n : right) {
			mr.revRight.apply(n).remove(this);
			double urY = mr.yMapRight.apply(nodeUpRight.getXPosition(),nodeUpRight.getYPosition());
			double drY = mr.yMapRight.apply(nodeDownRight.getXPosition(),nodeDownRight.getYPosition());
			double nY = mr.axisMapRight.apply(n);
			if (nY <= urY && nY + 1/n.getInvSize() >urY) {
				mr.revRight.apply(n).add(nodeUpRight);
				nodeUpRight.right.add(n);
			}
			if (nY <= drY && nY + 1/n.getInvSize() >drY) {
				mr.revRight.apply(n).add(nodeDownRight);
				nodeDownRight.right.add(n);
			}
		}
		right.clear();
		for (Node n : left) {
			mr.revLeft.apply(n).remove(this);
			double ulY = mr.yMapLeft.apply(nodeUpRight.getXPosition(),nodeUpRight.getYPosition());
			double dlY = mr.yMapLeft.apply(nodeDownRight.getXPosition(),nodeDownRight.getYPosition());
			double nY = mr.axisMapLeft.apply(n);
			if (nY <= ulY && nY + 1/n.getInvSize() >ulY) {
				mr.revLeft.apply(n).add(nodeUpLeft);
				nodeUpLeft.left.add(n);
			}
			if (nY <= dlY && nY + 1/n.getInvSize() >dlY) {
				mr.revLeft.apply(n).add(nodeDownLeft);
				nodeDownLeft.left.add(n);
			}
		}
		left.clear();

		isLeaf = false;
		dispose(false);
	}

	public void combine() {
		if (isLeaf) {
			return;
		}

		MapReturn mr = getMappings();

		left.addAll(nodeUpLeft.left);
		for (Node n : nodeUpLeft.left) {
			mr.revLeft.apply(n).remove(nodeUpLeft);
			mr.revLeft.apply(n).add(this);
		}
		left.addAll(nodeDownLeft.left);
		for (Node n : nodeDownLeft.left) {
			mr.revLeft.apply(n).remove(nodeDownLeft);
			mr.revLeft.apply(n).add(this);
		}

		right.addAll(nodeUpRight.right);
		for (Node n : nodeUpRight.right) {
			mr.revRight.apply(n).remove(nodeUpRight);
			mr.revRight.apply(n).add(this);
		}
		right.addAll(nodeDownRight.right);
		for (Node n : nodeDownRight.right) {
			mr.revRight.apply(n).remove(nodeDownRight);
			mr.revRight.apply(n).add(this);
		}

		above.addAll(nodeUpLeft.above);
		for (Node n : nodeUpLeft.above) {
			mr.revUp.apply(n).remove(nodeUpLeft);
			mr.revUp.apply(n).add(this);
		}
		above.addAll(nodeUpRight.above);
		for (Node n : nodeUpRight.above) {
			mr.revUp.apply(n).remove(nodeUpRight);
			mr.revUp.apply(n).add(this);
		}

		below.addAll(nodeDownLeft.below);
		for (Node n : nodeDownLeft.below) {
			mr.revDown.apply(n).remove(nodeDownLeft);
			mr.revDown.apply(n).add(this);
		}
		below.addAll(nodeDownRight.below);
		for (Node n : nodeDownRight.below) {
			mr.revDown.apply(n).remove(nodeDownRight);
			mr.revDown.apply(n).add(this);
		}

		getAdjacent().forEach((n2)->n2.setDirty());

		nodeUpLeft.dispose(true);
		nodeUpRight.dispose(true);
		nodeDownLeft.dispose(true);
		nodeDownRight.dispose(true);
		nodeUpLeft = null;
		nodeUpRight = null;
		nodeDownLeft = null;
		nodeDownRight = null;
		isLeaf = true;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public int getLevel() {
		return level;
	}

	public Node getNodeDownLeft() {
		return nodeDownLeft;
	}

	public Node getNodeDownRight() {
		return nodeDownRight;
	}

	public Node getNodeUpLeft() {
		return nodeUpLeft;
	}

	public Node getNodeUpRight() {
		return nodeUpRight;
	}

	public double getXPosition() {
		return x;
	}

	public double getYPosition() {
		return y;
	}

	public double getInvSize() {
		return 1/s;
	}

	public double getSize() {
		return s;
	}

	public Node getParent() {
		return parent;
	}

	public boolean contains(double x, double y) {
		return x >= getXPosition() && y >= getYPosition() && x <= getXPosition() + 1/getInvSize() && y <= getYPosition() + 1/getInvSize();
	}

	public Node getSmallestBoundingNode(double x, double y) {
		if (isLeaf) {
			if (contains(x, y)) {
				return this;
			}
			return null;
		} else {
			if (nodeUpLeft.contains(x, y)) {
				return nodeUpLeft.getSmallestBoundingNode(x, y);
			} else if (nodeUpRight.contains(x, y)) {
				return nodeUpRight.getSmallestBoundingNode(x, y);
			} else if (nodeDownLeft.contains(x, y)) {
				return nodeDownLeft.getSmallestBoundingNode(x, y);
			} else if (nodeDownRight.contains(x, y)) {
				return nodeDownRight.getSmallestBoundingNode(x, y);
			}
		}
		return null;
	}

	public List<Node> getAllNodes() {
		if (isLeaf) {
			return new ArrayList<Node>(Arrays.asList(new Node[] {this}));
		} else {
			List<Node> nodes = nodeUpLeft.getAllNodes();
			nodes.addAll(nodeUpRight.getAllNodes());
			nodes.addAll(nodeDownLeft.getAllNodes());
			nodes.addAll(nodeDownRight.getAllNodes());
			nodes.add(this);
			return nodes;
		}
	}

	public int getPreferredLOD(Vector3 v, Vector3 facing) {
		Vector3[] corners = getCorners(true);

		double dist2 = MathHelper.dist2FromPointToRect(v, corners[0], corners[1], corners[2], corners[3]);

		//Vector3 normal = corners[2].cpy().sub(corners[0]).crs(corners[1].cpy().sub(corners[0])).nor();

		//double dot = normal.dot(facing);

		int lod = 0;
		//if (dot < 0) {
			for (int i = 0; i<qt.config.thresholds.length; i++) {
				if (qt.config.thresholds[i]*qt.config.thresholds[i] < dist2) {
					break;
				}
				lod = i;
			}

		//}
		if (isLeaf) {
			return Math.max(lod, getMinAdj()-1);
		} else {
			return Math.max(Math.max(Math.max(
					nodeUpLeft.getPreferredLOD(v, facing),
					nodeUpRight.getPreferredLOD(v, facing)),Math.max(
							nodeDownLeft.getPreferredLOD(v, facing),
							nodeDownRight.getPreferredLOD(v, facing))), lod);
		}
	}

	public List<Node> getLeaves() {
		if (isLeaf) {
			return new ArrayList<Node>(Arrays.asList(new Node[] {this}));
		} else {
			List<Node> nodes = nodeUpLeft.getLeaves();
			nodes.addAll(nodeUpRight.getLeaves());
			nodes.addAll(nodeDownLeft.getLeaves());
			nodes.addAll(nodeDownRight.getLeaves());
			return nodes;
		}
	}

	public short getType() {
		short type = 0b0000;
		type |= (above.size() <=1 ? 0 : QuadTreeSphere.FLAG_UP);
		type |= (below.size() <=1 ? 0 : QuadTreeSphere.FLAG_DOWN);
		type |= (left.size()  <=1 ? 0 : QuadTreeSphere.FLAG_LEFT);
		type |= (right.size() <=1 ? 0 : QuadTreeSphere.FLAG_RIGHT);
		return type;
	}

	public int getMinAdj() {
		int maxLevel = Integer.MIN_VALUE;
		for (Node n : above) {
			maxLevel = Math.max(maxLevel, n.level);
		}
		for (Node n : below) {
			maxLevel = Math.max(maxLevel, n.level);
		}
		for (Node n : right) {
			maxLevel = Math.max(maxLevel, n.level);
		}
		for (Node n : left) {
			maxLevel = Math.max(maxLevel, n.level);
		}
		return maxLevel;
	}

	public List<Node> getAdjacent() {
		List<Node> a = new ArrayList<Node>();
		a.addAll(above);
		a.addAll(below);
		a.addAll(left);
		a.addAll(right);
		return a;
	}

	public int getSide() {
		return side;
	}

	public void setDirty() {
		this.isDirty = true;
	}

	public void dispose(boolean subNodes) {
		if (mesh != null) {
			mesh.dispose();
			mesh = null;
		}
		if (subNodes) {
			if (nodeUpLeft != null) {
				nodeUpLeft.dispose(true);
			}
			if (nodeDownLeft != null) {
				nodeDownLeft.dispose(true);
			}
			if (nodeUpRight != null) {
				nodeUpRight.dispose(true);
			}
			if (nodeDownRight != null) {
				nodeDownRight.dispose(true);
			}
		}
		isDirty = true;
	}

	public Mesh getMesh(short[][] indicies, int maxVerticies, int maxIndicies) {
		if (mesh == null) {
			mesh = new Mesh(false, false, maxVerticies, maxIndicies, new VertexAttributes(
					new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
					new VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_normal"),
					new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_radDelta"),
					new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")));
		}
		if (isDirty) {
			short type = getType();

			Container2<float[], Vector3> cont = VBOGenerator.generateVertexData(type, x, y, s, qt.config.splitAmt, qt.getMatrixForNode(this, false), qt.generator, qt.config.ampScaleModel, qt.config.moveVerticies);

			float[] vertData = cont.one;
			offset = cont.two;

			mesh.setVertices(vertData, 0, indicies[type].length*(3+3+1+2)); //count = numIndicies * numComponentsPerVertex
			mesh.setIndices(indicies[type]);
			isDirty = false;
		}
		return mesh;
	}

	public Vector3 getOffset() {
		return offset;
	}

	/**
	 * 0 = top left
	 * 1 = bottom left
	 * 2 = top right
	 * 3 = bottom right
	 */
	private Vector3[] getCorners(boolean transform) {
		Vector3[] v = new Vector3[4];

		v[0] = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y)+1), (float)(-2*(x)+1)));
		v[1] = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y)+1), (float)(-2*(x+s)+1)));
		v[2] = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y+s)+1), (float)(-2*(x)+1)));
		v[3] = MathHelper.mapCubePosToSphere(new Vector3(1f, (float)(-2*(y+s)+1), (float)(-2*(x+s)+1)));

		if (radDeltas == null) {
			radDeltas = new double[] {qt.generator.getDeltaLength(v[0]) * qt.config.ampScaleModel,
					qt.generator.getDeltaLength(v[1]) * qt.config.ampScaleModel,
					qt.generator.getDeltaLength(v[2]) * qt.config.ampScaleModel,
					qt.generator.getDeltaLength(v[3]) * qt.config.ampScaleModel};
		}

		v[0].setLength((float) (v[0].len() + radDeltas[0]));
		v[1].setLength((float) (v[1].len() + radDeltas[1]));
		v[2].setLength((float) (v[2].len() + radDeltas[2]));
		v[3].setLength((float) (v[3].len() + radDeltas[3]));

		if (transform) {
			Matrix4 mat = qt.getMatrixForNode(this, true);

			v[0].mul(mat);
			v[1].mul(mat);
			v[2].mul(mat);
			v[3].mul(mat);
		}

		return v;
	}

	private class MapReturn {
		public Function<Node, Set<Node>> revUp, revDown, revRight, revLeft;
		public BiFunction<Double, Double, Double> xMapUp, yMapUp, xMapDown/*, yMapDown, xMapRight*/, yMapRight/*, xMapLeft*/, yMapLeft;
		public Function<Node, Double> axisMapUp, axisMapDown, axisMapRight, axisMapLeft;
	}
}
