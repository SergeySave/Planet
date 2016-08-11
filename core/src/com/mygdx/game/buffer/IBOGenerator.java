package com.mygdx.game.buffer;

public class IBOGenerator {
	public static short[] generateIndiciesArray(byte type, int splitAmt) {
		if (type == 0b0000) { //Normal
			int totalIndicies = splitAmt * (2*(splitAmt + 1)+2) - 2;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerRow = splitAmt+1;
			int vertexBottom = vertexTop + verticiesPerRow;
			for (int i = 0; i < splitAmt; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				index = fillRowTriangles(splitAmt, index, indicies, (short)vertexTop, (short)vertexBottom);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-1);
				}
			}
			return indicies;
		} else if (type == 0b0001) { //Smaller up
			int totalIndicies = (4*splitAmt + 1) + 2 + (splitAmt-1) * (2*(splitAmt + 1)+2) - 2 + (splitAmt > 1 ? 1 : 0);
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerTopRow = 2*splitAmt+1;
			int verticiesPerRow = splitAmt+1;
			int vertexBottom = vertexTop + verticiesPerTopRow;
			index = fillRowUpTriangles(splitAmt, index, indicies, (short)vertexTop, (short)vertexBottom);
			if (splitAmt > 1) {
				indicies[index++] = (short) (vertexBottom - 1);
				indicies[index++] = (short) (vertexBottom - 1);
			}
			vertexTop = vertexBottom;
			vertexBottom += verticiesPerRow;
			for (int i = 1; i < splitAmt; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				index = fillRowTriangles(splitAmt, index, indicies, (short)vertexTop, (short)vertexBottom);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-1);
				}
			}
			return indicies;
		} else if (type == 0b0010) { //Smaller down
			int totalIndicies = (4*splitAmt + 1) + 3 + (splitAmt-1) * (2*(splitAmt + 1)+2) - 2;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerRow = splitAmt+1;
			int vertexBottom = vertexTop + verticiesPerRow;
			for (int i = 0; i < splitAmt-1; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				index = fillRowTriangles(splitAmt, index, indicies, (short)vertexTop, (short)vertexBottom);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-1);
				}
			}
			if (splitAmt > 1) {
				indicies[index++] = (short) vertexTop;
			}
			fillRowDownTriangles(splitAmt, index, indicies, (short)vertexTop, (short)vertexBottom);
			return indicies;
		} else if (type == 0b0011) { //Smaller up and down
			if (splitAmt == 1) {
				return new short[]{0,3,1,4,2,5};
			}
			int totalIndicies = (4*splitAmt + 1) + 3 + (4*splitAmt + 1) + 3 + (splitAmt-2) * (2*(splitAmt + 1)+2) - 2;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerTopRow = 2*splitAmt+1;
			int verticiesPerRow = splitAmt+1;
			int vertexBottom = vertexTop + verticiesPerTopRow;
			index = fillRowUpTriangles(splitAmt, index, indicies, (short)vertexTop, (short)vertexBottom);
			indicies[index++] = (short) (vertexBottom - 1);
			indicies[index++] = (short) (vertexBottom - 1);
			vertexTop = vertexBottom;
			vertexBottom += verticiesPerRow;
			for (int i = 1; i < splitAmt-1; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				index = fillRowTriangles(splitAmt, index, indicies, (short)vertexTop, (short)vertexBottom);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-1);
				}
			}
			indicies[index++] = (short) vertexTop;
			fillRowDownTriangles(splitAmt, index, indicies, (short)vertexTop, (short)vertexBottom);
			return indicies;
		} else if (type == 0b0100) { //Smaller right
			int totalIndicies = 4*(splitAmt+1) + (splitAmt-1) * (2*(splitAmt+1)+2) - 2;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerRow = splitAmt+2;
			int vertexBottom = vertexTop + verticiesPerRow;
			for (int i = 0; i < splitAmt; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				index = fillRowTriangles(splitAmt-1, index, indicies, (short)vertexTop, (short)vertexBottom);
				indicies[index++] = (short) (vertexBottom-2);
				indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexBottom-2+verticiesPerRow);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-2);
				}
			}
			return indicies;
		} else if (type == 0b0110) { //Smaller right and down
			int totalIndicies = 2*(splitAmt-1) + 4*(splitAmt) + (splitAmt) * (2*(splitAmt)+2) - 2;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerRow = splitAmt+2;
			int verticiesBottomRow = 2*splitAmt + 1;
			int vertexBottom = vertexTop + verticiesPerRow;
			for (int i = 0; i < splitAmt-1; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				index = fillRowTriangles(splitAmt-1, index, indicies, (short)vertexTop, (short)vertexBottom);
				indicies[index++] = (short) (vertexBottom-2);
				indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexBottom-2+verticiesPerRow);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-2);
				}
			}
			if (splitAmt > 1) {
				indicies[index++] = (short) (vertexTop);
			}
			index = fillRowDownTriangles(splitAmt-1, index, indicies, (short)(vertexTop), (short)(vertexBottom));
			vertexTop = vertexBottom;
			vertexBottom += verticiesBottomRow;
			indicies[index++] = (short) (vertexTop-2);
			indicies[index++] = (short) (vertexBottom - 2);
			indicies[index++] = (short) (vertexTop-1);
			indicies[index++] = (short) (vertexBottom - 1);
			return indicies;
		} else if (type == 0b0101) { //Smaller Up and Right
			int totalIndicies = 4*(splitAmt+1) + (4*splitAmt + 1) + 2 + (splitAmt-1) * (2*(splitAmt)+2) - 2-5;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerTopRow = 2*splitAmt+2;
			int verticiesPerRow = splitAmt+2;
			int vertexBottom = vertexTop + verticiesPerTopRow;
			index = fillRowUpTriangles(splitAmt-1, index, indicies, (short)vertexTop, (short)vertexBottom);
			indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);
			indicies[index++] = (short) (vertexBottom-3);
			indicies[index++] = (short) (vertexBottom-2+verticiesPerRow);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexBottom-3);
			indicies[index++] = (short) (vertexBottom-2);
			if (splitAmt > 1) {
				indicies[index++] = (short) (vertexBottom-2);
			}
			vertexTop = vertexBottom;
			vertexBottom += verticiesPerRow;
			for (int i = 1; i < splitAmt; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				index = fillRowTriangles(splitAmt-1, index, indicies, (short)vertexTop, (short)vertexBottom);
				indicies[index++] = (short) (vertexBottom-2);
				indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexBottom-2+verticiesPerRow);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-2);
				}
			}
			return indicies;
		} else if (type == 0b0111) { //Smaller Up, Right, and Down
			if (splitAmt == 1) {
				return new short[] {0,4,1,5,2,5,3,6};
			}
			int totalIndicies = 6*(splitAmt) - 4 + (splitAmt) * (2*(splitAmt + 1)+2);
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerTopRow = 2*(splitAmt+1);
			int verticiesPerRow = splitAmt+2;
			int verticiesBottomRow = 2*splitAmt + 1;
			int vertexBottom = vertexTop + verticiesPerTopRow;
			index = fillRowUpTriangles(splitAmt-1, index, indicies, (short)vertexTop, (short)vertexBottom);
			indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);
			indicies[index++] = (short) (vertexBottom-3);
			indicies[index++] = (short) (vertexBottom-2+verticiesPerRow);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexBottom-3);
			indicies[index++] = (short) (vertexBottom-2);
			indicies[index++] = (short) (vertexBottom-2);
			vertexTop = vertexBottom;
			vertexBottom += verticiesPerRow;
			for (int i = 1; i < splitAmt-1; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				index = fillRowTriangles(splitAmt-1, index, indicies, (short)vertexTop, (short)vertexBottom);
				indicies[index++] = (short) (vertexBottom-2);
				indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexBottom-2+verticiesPerRow);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-2);
				}
			}
			indicies[index++] = (short) vertexTop;
			index = fillRowDownTriangles(splitAmt-1, index, indicies, (short)(vertexTop), (short)(vertexBottom));
			vertexTop = vertexBottom;
			vertexBottom += verticiesBottomRow;
			indicies[index++] = (short) (vertexTop-2);
			indicies[index++] = (short) (vertexBottom - 2);
			indicies[index++] = (short) (vertexTop-1);
			indicies[index++] = (short) (vertexBottom - 1);
			return indicies;
		} else if (type == 0b1000) { //Smaller left
			int totalIndicies = 4*(splitAmt+1) + (splitAmt-1) * (2*(splitAmt+1)+2) - 2;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerRow = splitAmt+2;
			int vertexBottom = vertexTop + verticiesPerRow-1;
			for (int i = 0; i < splitAmt; i++) {
				if (i>0) {
					indicies[index++] = (short) (vertexTop);
				}

				indicies[index++] = (short) (vertexTop);
				indicies[index++] = (short) (vertexBottom);
				indicies[index++] = (short) (vertexTop+1);
				indicies[index++] = (short) (vertexBottom+1);
				index = fillRowTriangles(splitAmt-1, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+2));

				vertexTop = vertexBottom+1;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-1);
				}
			}
			return indicies;
		} else if (type == 0b1010) { //Smaller Down and Left
			int totalIndicies = 2*(splitAmt-1) + 4*(splitAmt) + 2 + (splitAmt) * (2*(splitAmt)+2) - 2;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerRow = splitAmt+2;
			int vertexBottom = vertexTop + verticiesPerRow;
			for (int i = 0; i < splitAmt-1; i++) {
				if (i>0) {
					indicies[index++] = (short) (vertexTop);
				}

				indicies[index++] = (short) (vertexTop);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexTop+1);
				indicies[index++] = (short) (vertexBottom);
				index = fillRowTriangles(splitAmt-1, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+1));

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-2);
				}
			}
			if (splitAmt > 1) {
				indicies[index++] = (short) (vertexTop);
			}
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexBottom - 1);
			indicies[index++] = (short) (vertexTop + 1);
			indicies[index++] = (short) (vertexBottom);
			indicies[index++] = (short) (vertexTop + 1);
			indicies[index++] = (short) (vertexBottom + 1);
			fillRowDownTriangles(splitAmt-1, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+2));
			return indicies;
		} else if (type == 0b1001) { //Smaller up and left
			int totalIndicies = 4*(splitAmt+1) + (4*splitAmt + 1) + 2 + (splitAmt-1) * (2*(splitAmt)+2) - 2-5-3 + (splitAmt > 1 ? 1 : 0);
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerTopRow = 2*splitAmt+2;
			int verticiesPerRow = splitAmt+2;
			int vertexBottom = vertexTop + verticiesPerTopRow;
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexTop+1);
			indicies[index++] = (short) (vertexBottom);
			if (splitAmt > 1) {
				index = fillRowUpTriangles(splitAmt-1, index, indicies, (short)(vertexTop+2), (short)(vertexBottom+1));
				indicies[index++] = (short) (vertexBottom-2);
			}
			indicies[index++] = (short) (vertexBottom-2);
			vertexTop = vertexBottom;
			vertexBottom += verticiesPerRow;
			for (int i = 1; i < splitAmt; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				indicies[index++] = (short) (vertexTop);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexTop+1);
				indicies[index++] = (short) (vertexBottom);
				index = fillRowTriangles(splitAmt-1, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+1));

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-2);
				}
			}
			return indicies;
		} else if (type == 0b1100) { //Smaller left and right
			int totalIndicies = 6*(splitAmt+1) + (splitAmt-1) * (2*(splitAmt+1)+2) - 4;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerRow = splitAmt+3;
			int vertexBottom = vertexTop + verticiesPerRow;
			if (splitAmt == 1) {
				return new short[]{0,2,1,3,4,5};
			}
			for (int i = 0; i < splitAmt; i++) {
				if (i>0) {
					indicies[index++] = (short) (vertexTop);
				}

				indicies[index++] = (short) (vertexTop);
				indicies[index++] = (short) (vertexBottom-2);
				indicies[index++] = (short) (vertexTop+1);
				indicies[index++] = (short) (vertexBottom);
				index = fillRowTriangles(splitAmt-2, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+1));
				indicies[index++] = (short) (vertexBottom-3);
				indicies[index++] = (short) (vertexBottom-4+verticiesPerRow);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-3);
				}
			}
			return indicies;
		} else if (type == 0b1011) { //Smaller Up, Down, and Left
			if (splitAmt == 1) {
				return new short[] {0,3,1,4,2,5,6};
			}
			int totalIndicies = 6*(splitAmt) - 5 + (splitAmt) * (2*(splitAmt + 1)+2+1);
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerTopRow = 2*(splitAmt+1);
			int verticiesPerRow = splitAmt+2;
			int vertexBottom = vertexTop + verticiesPerTopRow;
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexTop+1);
			indicies[index++] = (short) (vertexBottom);
			index = fillRowUpTriangles(splitAmt-1, index, indicies, (short)(vertexTop+2), (short)(vertexBottom+1));
			indicies[index++] = (short) (vertexBottom-2);
			indicies[index++] = (short) (vertexBottom-2);
			vertexTop = vertexBottom;
			vertexBottom += verticiesPerRow;
			for (int i = 1; i < splitAmt-1; i++) {
				if (i>0) {
					indicies[index++] = (short) vertexTop;
				}

				indicies[index++] = (short) (vertexTop);
				indicies[index++] = (short) (vertexBottom);
				indicies[index++] = (short) (vertexTop+1);
				indicies[index++] = (short) (vertexBottom+1);
				index = fillRowTriangles(splitAmt-1, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+2));

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-2);
				}
			}
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexBottom - 1);
			indicies[index++] = (short) (vertexTop + 1);
			indicies[index++] = (short) (vertexBottom);
			indicies[index++] = (short) (vertexTop + 1);
			indicies[index++] = (short) (vertexBottom + 1);
			fillRowDownTriangles(splitAmt-1, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+2));
			return indicies;
		} else if (type == 0b1101) { // Smaller Up, Right, and Left
			if (splitAmt == 1) {
				return new short[] {0,3,1,5,2,5,4,6};
			}
			int totalIndicies = 8*(splitAmt) + (splitAmt-1) * (2*(splitAmt+1)+2);
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerTopRow = 2*splitAmt+3;
			int verticiesPerRow = splitAmt+3;
			int vertexBottom = vertexTop + verticiesPerTopRow;
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexBottom-2);
			indicies[index++] = (short) (vertexTop+1);
			indicies[index++] = (short) (vertexBottom);
			index = fillRowUpTriangles(splitAmt-2, index, indicies, (short)(vertexTop+2), (short)(vertexBottom+1));
			indicies[index++] = (short) (vertexBottom-4+verticiesPerRow);
			indicies[index++] = (short) (vertexBottom-4);
			indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexBottom-4);
			indicies[index++] = (short) (vertexBottom-3);
			indicies[index++] = (short) (vertexBottom-3);
			vertexTop = vertexBottom;
			vertexBottom += verticiesPerRow;
			for (int i = 1; i < splitAmt; i++) {
				if (i>0) {
					indicies[index++] = (short) (vertexTop);
				}

				indicies[index++] = (short) (vertexTop);
				indicies[index++] = (short) (vertexBottom-2);
				indicies[index++] = (short) (vertexTop+1);
				indicies[index++] = (short) (vertexBottom);
				index = fillRowTriangles(splitAmt-2, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+1));
				indicies[index++] = (short) (vertexBottom-3);
				indicies[index++] = (short) (vertexBottom-4+verticiesPerRow);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-3);
				}
			}
			return indicies;
		} else if (type == 0b1110) { // Smaller Down, Right, and Left
			if (splitAmt == 1) {
				return new short[] {0,2,1,3,4,3,5,6};
			}
			int totalIndicies = 8*(splitAmt) + (splitAmt-1) * (2*(splitAmt+1)+2);
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerRow = splitAmt+3;
			int verticiesBottomRow = 2*splitAmt+1;
			int vertexBottom = vertexTop + verticiesPerRow;
			for (int i = 0; i < splitAmt-1; i++) {
				if (i>0) {
					indicies[index++] = (short) (vertexTop);
				}

				indicies[index++] = (short) (vertexTop);
				indicies[index++] = (short) (vertexBottom-2);
				indicies[index++] = (short) (vertexTop+1);
				indicies[index++] = (short) (vertexBottom);
				index = fillRowTriangles(splitAmt-2, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+1));
				indicies[index++] = (short) (vertexBottom-3);
				indicies[index++] = (short) (vertexBottom-4+verticiesPerRow);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-3);
				}
			}
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexBottom - 2);
			indicies[index++] = (short) (vertexTop + 1);
			indicies[index++] = (short) (vertexBottom);
			indicies[index++] = (short) (vertexTop + 1);
			indicies[index++] = (short) (vertexBottom + 1);
			index = fillRowDownTriangles(splitAmt-2, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+2));
			vertexTop = vertexBottom;
			vertexBottom += verticiesBottomRow;
			indicies[index++] = (short) (vertexTop-3);
			indicies[index++] = (short) (vertexBottom - 2);
			indicies[index++] = (short) (vertexTop-1);
			indicies[index++] = (short) (vertexBottom - 1);
			return indicies;
		} else if (type == 0b1111) { //Smaller Up, Down, Right, and Left
			if (splitAmt == 1) {
				return generateIndiciesArray((byte) 0b0000, 2);
			}
			int totalIndicies = 10*(splitAmt) + (splitAmt-1) * (2*(splitAmt+1)+2) - 2;
			short[] indicies = new short[totalIndicies];
			int index = 0;
			int vertexTop = 0;
			int verticiesPerTopRow = 2*splitAmt+3;
			int verticiesPerRow = splitAmt+3;
			int verticiesBottomRow = 2*splitAmt+1;
			int vertexBottom = vertexTop + verticiesPerTopRow;
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexBottom-2);
			indicies[index++] = (short) (vertexTop+1);
			indicies[index++] = (short) (vertexBottom);
			index = fillRowUpTriangles(splitAmt-2, index, indicies, (short)(vertexTop+2), (short)(vertexBottom+1));
			indicies[index++] = (short) (vertexBottom-4+verticiesPerRow);
			indicies[index++] = (short) (vertexBottom-4);
			indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexBottom-1);
			indicies[index++] = (short) (vertexBottom-4);
			indicies[index++] = (short) (vertexBottom-3);
			indicies[index++] = (short) (vertexBottom-3);
			vertexTop = vertexBottom;
			vertexBottom += verticiesPerRow;
			for (int i = 1; i < splitAmt-1; i++) {
				if (i>0) {
					indicies[index++] = (short) (vertexTop);
				}

				indicies[index++] = (short) (vertexTop);
				indicies[index++] = (short) (vertexBottom-2);
				indicies[index++] = (short) (vertexTop+1);
				indicies[index++] = (short) (vertexBottom);
				index = fillRowTriangles(splitAmt-2, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+1));
				indicies[index++] = (short) (vertexBottom-3);
				indicies[index++] = (short) (vertexBottom-4+verticiesPerRow);
				indicies[index++] = (short) (vertexBottom-1);
				indicies[index++] = (short) (vertexBottom-3+verticiesPerRow);

				vertexTop = vertexBottom;
				vertexBottom += verticiesPerRow;

				if (i<splitAmt-1) {
					indicies[index++] = (short) (vertexBottom-3);
				}
			}
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexTop);
			indicies[index++] = (short) (vertexBottom - 2);
			indicies[index++] = (short) (vertexTop + 1);
			indicies[index++] = (short) (vertexBottom);
			indicies[index++] = (short) (vertexTop + 1);
			indicies[index++] = (short) (vertexBottom + 1);
			index = fillRowDownTriangles(splitAmt-2, index, indicies, (short)(vertexTop+1), (short)(vertexBottom+2));
			vertexTop = vertexBottom;
			vertexBottom += verticiesBottomRow;
			indicies[index++] = (short) (vertexTop-3);
			indicies[index++] = (short) (vertexBottom - 2);
			indicies[index++] = (short) (vertexTop-1);
			indicies[index++] = (short) (vertexBottom - 1);
			return indicies;
		}

		return null;
	}

	/*
	 * Inclusive -> Exclusive
	 */
	private static int fillRowTriangles(int num, int startIndex, short[] indicies, short topStartVertex, short bottomStartVertex) {
		int index = startIndex;
		indicies[index++] = topStartVertex++;
		indicies[index++] = bottomStartVertex++;
		for (int i = 0; i < num; i++) {
			indicies[index++] = topStartVertex++;
			indicies[index++] = bottomStartVertex++;
		}
		return index;
	}

	/*
	 * Inclusive -> Exclusive
	 */
	private static int fillRowUpTriangles(int num, int startIndex, short[] indicies, short topStartVertex, short bottomStartVertex) {
		int index = startIndex;
		indicies[index++] = topStartVertex++;
		for (int i = 0; i < num; i++) {
			indicies[index++] = bottomStartVertex++;
			indicies[index++] = topStartVertex++;
			indicies[index++] = bottomStartVertex;
			indicies[index++] = topStartVertex++;
		}
		return index;
	}

	/*
	 * Inclusive -> Exclusive
	 */
	private static int fillRowDownTriangles(int num, int startIndex, short[] indicies, short topStartVertex, short bottomStartVertex) {
		int index = startIndex;
		indicies[index++] = topStartVertex++;
		indicies[index++] = bottomStartVertex++;
		for (int i = 0; i < num; i++) {
			indicies[index++] = topStartVertex;
			indicies[index++] = bottomStartVertex++;
			indicies[index++] = topStartVertex++;
			indicies[index++] = bottomStartVertex++;
		}
		return index;
	}
}
