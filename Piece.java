import java.util.List;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

public class Piece {
    private Shape pieceShape;
    private int[][] coords;
    private int[][][] coordsTable;
    private List<Shape> shapeList = new ArrayList<>();

    public Piece() {
        coords = new int[4][2];
        setShape(Shape.NoShape);
    }

    public void setShape(Shape shape) {
        coordsTable = new int[][][] {
                { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } }, // NoShape
                { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, // ZShape
                { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }, // SShape
                { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, // LineShape
                { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }, // TShape
                { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, // SquareShape
                { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 1, -1 } }, // LShape
                { { -1, -1 }, { -1, 0 }, { 0, 0 }, { 1, 0 } }, // MirroredLShape
        };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }

        pieceShape = shape;
    }

    private void setX(int index, int x) {
        coords[index][0] = x;
    }

    private void setY(int index, int y) {
        coords[index][1] = y;
    }

    public int x(int index) {
        return coords[index][0];
    }

    public int y(int index) {
        return coords[index][1];
    }

    public Shape getShape() {
        return pieceShape;
    }

    public void setRandomShape() {
        if (shapeList.size() < 6) {
            List<Shape> newShapes = new ArrayList<>();
            for (Shape shape : Shape.values()) {
                if (shape != Shape.NoShape) {
                    newShapes.add(shape);
                }
            }
            do {
                Collections.shuffle(newShapes, new SecureRandom()); // Use SecureRandom
            } while (shapeList.size() > 0 && shapeList.get(shapeList.size() - 1) == newShapes.get(0));
            shapeList.addAll(newShapes);
        }
        Shape shape = shapeList.remove(0);
        setShape(shape);
    }

    public int minX() {
        int m = coords[0][0];

        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][0]);
        }

        return m;
    }

    public int minY() {
        int m = coords[0][1];

        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }

        return m;
    }

    public Piece rotateRight() {
        if (pieceShape == Shape.SquareShape) {
            return this;
        }

        Piece result = new Piece();
        result.pieceShape = pieceShape;
        result.coordsTable = this.coordsTable; // copy coordsTable
        result.shapeList = new ArrayList<>(this.shapeList); // copy shapeList

        for (int i = 0; i < 4; i++) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }

        return result;
    }

    public Piece rotateLeft() {
        if (pieceShape == Shape.SquareShape) {
            return this;
        }

        Piece result = new Piece();
        result.pieceShape = pieceShape;
        result.coordsTable = this.coordsTable; // copy coordsTable
        result.shapeList = new ArrayList<>(this.shapeList); // copy shapeList

        for (int i = 0; i < 4; i++) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }

        return result;
    }
}
