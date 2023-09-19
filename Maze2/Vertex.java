import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import java.awt.Color;
import tester.*;

// Represents a vertex in a graph or a cell in a maze
public class Vertex {
  int x;
  int y;
  boolean visitedByUser;
  boolean visitedByComputer;
  boolean isCorrect;
  static final int CELL_SIZE = 32;
  static final int UP = 0;
  static final int DOWN = 2;
  static final int RIGHT = 1;
  static final int LEFT = 3;

  // Constructor
  Vertex(int x, int y) {
    this.x = x;
    this.y = y;
    this.visitedByUser = false;
    this.visitedByComputer = false;
    this.isCorrect = false;
  }

  // Sets the vertex as visited (by user)
  void visitByUser() {
    this.visitedByUser = true;
  }

  // Sets the vertex as visited (by computer)
  void visitByComputer() {
    this.visitedByComputer = true;
  }

  // Sets the vertex as part of a correct solution
  void markAsCorrect() {
    this.isCorrect = true;
  }

  // Resets the vertex to an untouched state
  void resetVertex() {
    this.isCorrect = false;
    this.visitedByComputer = false;
    this.visitedByUser = false;
  }

  // Returns a new Vertex that is the given distance away from this one
  Vertex addVertex(int dx, int dy) {
    return new Vertex(this.x + dx, this.y + dy);
  }

  // Returns true if the Vertex is within the given bounds
  boolean isInBounds(int width, int height) {
    return (this.x > 0 || this.y > 0 || this.x < width || this.y < height);
  }

  // Returns the direction that the given Vertex is in relative to this one
  int findDirection(Vertex other) {
    if (this.x - other.x < 0) {
      return RIGHT;
    }
    else if (this.x - other.x > 0) {
      return LEFT;
    }
    else if (this.y - other.y < 0) {
      return DOWN;
    }
    else {
      return UP;
    }
  }

  // Draws a Vertex onto the given WorldScene
  void drawVertex(WorldScene g2d, int cellSize, int width, int height, boolean playerOn) {
    RectangleImage baseImage = new RectangleImage(cellSize, cellSize, "solid",
        determineCellColor(width, height, playerOn));
    RectangleImage outlineImage = new RectangleImage(cellSize, cellSize, "outline", Color.black);

    g2d.placeImageXY(baseImage, (this.x * cellSize) + (cellSize / 2),
        (this.y * cellSize) + (cellSize / 2));
    g2d.placeImageXY(outlineImage, (this.x * cellSize) + (cellSize / 2),
        (this.y * cellSize) + (cellSize / 2));
  }

  // Returns the correct cell color based on the Vertex's position
  Color determineCellColor(int width, int height, boolean playerOn) {
    if (this.isCorrect) {
      return new Color(30, 150, 30);
    }
    else if (this.visitedByUser) {
      return new Color(25, 120, 25);
    }
    else if (playerOn) {
      return new Color(25, 200, 25);
    }
    else if (this.visitedByComputer) {
      return new Color(150, 0, 250);
    }
    else if (this.x == 0 && this.y == 0) {
      return new Color(20, 130, 100);
    }
    else if (this.x == width - 1 && this.y == height - 1) {
      return new Color(100, 20, 255);
    }
    else {
      return new Color(190, 190, 190);
    }
  }

  @Override
  // overrides the hashCode method to make it work with the equals method
  public int hashCode() {
    return (this.x * 1000) + this.y;
  }

  @Override
  // overrides the equals method
  public boolean equals(Object other) {
    if (other instanceof Vertex) {
      Vertex that = (Vertex) other;
      return this.x == that.x && this.y == that.y;
    }
    else {
      return false;
    }
  }
}

class TestVertex {
  Vertex v1, v2, v3, v4, vertex1, vertex2, vertex3;
  WorldScene background;
  WorldScene expectedScene;

  void initData() {
    v1 = new Vertex(0, 0);
    v2 = new Vertex(1, 0);
    v3 = new Vertex(1, 1);
    v4 = new Vertex(0, 1);

    vertex1 = new Vertex(0, 0);
    vertex2 = new Vertex(1, 1);
    vertex3 = new Vertex(2, 2);

    this.background = new WorldScene(4 * Vertex.CELL_SIZE, 4 * Vertex.CELL_SIZE);
    this.expectedScene = new WorldScene(4 * Vertex.CELL_SIZE, 4 * Vertex.CELL_SIZE);
  }

  void testDrawVertex(Tester t) {
    initData();

    // Test vertex1
    vertex1.drawVertex(background, Vertex.CELL_SIZE, 4, 4, false);
    RectangleImage baseImage1 = new RectangleImage(Vertex.CELL_SIZE, Vertex.CELL_SIZE, "solid",
        vertex1.determineCellColor(4, 4, false));
    RectangleImage outlineImage1 = new RectangleImage(Vertex.CELL_SIZE, Vertex.CELL_SIZE, "outline",
        Color.black);
    expectedScene.placeImageXY(baseImage1, (vertex1.x * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2),
        (vertex1.y * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2));
    expectedScene.placeImageXY(outlineImage1,
        (vertex1.x * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2),
        (vertex1.y * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2));
    t.checkExpect(background, expectedScene);

    // Test vertex2
    initData();
    vertex2.drawVertex(background, Vertex.CELL_SIZE, 4, 4, false);
    RectangleImage baseImage2 = new RectangleImage(Vertex.CELL_SIZE, Vertex.CELL_SIZE, "solid",
        vertex2.determineCellColor(4, 4, false));
    RectangleImage outlineImage2 = new RectangleImage(Vertex.CELL_SIZE, Vertex.CELL_SIZE, "outline",
        Color.black);
    expectedScene.placeImageXY(baseImage2, (vertex2.x * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2),
        (vertex2.y * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2));
    expectedScene.placeImageXY(outlineImage2,
        (vertex2.x * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2),
        (vertex2.y * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2));
    t.checkExpect(background, expectedScene);

    // Test vertex3
    initData();
    vertex3.drawVertex(background, Vertex.CELL_SIZE, 4, 4, false);
    RectangleImage baseImage3 = new RectangleImage(Vertex.CELL_SIZE, Vertex.CELL_SIZE, "solid",
        vertex3.determineCellColor(4, 4, false));
    RectangleImage outlineImage3 = new RectangleImage(Vertex.CELL_SIZE, Vertex.CELL_SIZE, "outline",
        Color.black);
    expectedScene.placeImageXY(baseImage3, (vertex3.x * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2),
        (vertex3.y * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2));
    expectedScene.placeImageXY(outlineImage3,
        (vertex3.x * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2),
        (vertex3.y * Vertex.CELL_SIZE) + (Vertex.CELL_SIZE / 2));
    t.checkExpect(background, expectedScene);
  }

  // test the determineCellColor method
  void testDetermineCellColor(Tester t) {
    initData();
    t.checkExpect(vertex1.determineCellColor(20, 20, true), new Color(25, 200, 25));
    t.checkExpect(vertex1.determineCellColor(20, 20, false), new Color(20, 130, 100));
    vertex1.visitedByComputer = true;
    t.checkExpect(vertex1.determineCellColor(20, 20, false), new Color(150, 0, 250));
    vertex1.visitedByUser = true;
    t.checkExpect(vertex1.determineCellColor(20, 20, true), new Color(25, 120, 25));
    vertex1.markAsCorrect();
    t.checkExpect(vertex1.determineCellColor(20, 20, false), new Color(30, 150, 30));
    t.checkExpect(vertex2.determineCellColor(20, 20, false), new Color(190, 190, 190));
    t.checkExpect(vertex3.determineCellColor(20, 20, false), new Color(190, 190, 190));
  }

  // Test the visitByUser method
  void testVisitByUser(Tester t) {
    initData();
    t.checkExpect(v1.visitedByUser, false);
    v1.visitByUser();
    t.checkExpect(v1.visitedByUser, true);

  }

  // Test the visitByComputer method
  void testVisitByComputer(Tester t) {
    initData();
    t.checkExpect(v1.visitedByComputer, false);
    v1.visitByComputer();
    t.checkExpect(v1.visitedByComputer, true);

  }

  // Test the markAsCorrect method
  void testMarkAsCorrect(Tester t) {
    initData();
    t.checkExpect(v1.isCorrect, false);
    v1.markAsCorrect();
    t.checkExpect(v1.isCorrect, true);

  }

  // Test the resetVertex method
  void testResetVertex(Tester t) {
    initData();
    v1.visitByUser();
    v1.visitByComputer();
    v1.markAsCorrect();
    v1.resetVertex();
    t.checkExpect(v1.visitedByUser, false);
    t.checkExpect(v1.visitedByComputer, false);
    t.checkExpect(v1.isCorrect, false);
  }

  // Test the addVertex method
  void testAddVertex(Tester t) {
    initData();
    t.checkExpect(v1.addVertex(1, 1), new Vertex(1, 1));
    t.checkExpect(v2.addVertex(-1, 1), new Vertex(0, 1));
    t.checkExpect(v3.addVertex(1, -1), new Vertex(2, 0));
    t.checkExpect(v4.addVertex(-1, -1), new Vertex(-1, 0));
  }

  // Test the isInBounds method
  void testIsInBounds(Tester t) {
    initData();
    t.checkExpect(v1.isInBounds(2, 2), true);
    t.checkExpect(v1.isInBounds(0, 0), false);
    t.checkExpect(v4.isInBounds(1, 1), true);
    t.checkExpect(v4.isInBounds(0, 0), true);
    t.checkExpect(v3.isInBounds(0, 1), true);
    t.checkExpect(v2.isInBounds(1, 0), true);
  }

  // Test the findDirection method
  void testFindDirection(Tester t) {
    initData();
    t.checkExpect(v1.findDirection(v2), Vertex.RIGHT);
    t.checkExpect(v2.findDirection(v3), Vertex.DOWN);
    t.checkExpect(v1.findDirection(v3), Vertex.RIGHT);
    t.checkExpect(v1.findDirection(v4), Vertex.DOWN);
    t.checkExpect(v2.findDirection(v1), Vertex.LEFT);
  }

  // Test the hashCode method
  void testHashCode(Tester t) {
    initData();
    t.checkExpect(v1.hashCode(), (0 * 1000) + 0);
    t.checkExpect(v2.hashCode(), (1 * 1000) + 0);
    t.checkExpect(v3.hashCode(), (1 * 1000) + 1);
    t.checkExpect(v4.hashCode(), (0 * 1000) + 1);
  }

  // Test the equals method
  void testEquals(Tester t) {
    initData();
    t.checkExpect(v1.equals(v2), false);
    t.checkExpect(v1.equals(new Vertex(0, 0)), true);
    t.checkExpect(v1.equals(v3), false);
    t.checkExpect(v1.equals(v4), false);
    t.checkExpect(v2.equals(v3), false);
    t.checkExpect(v2.equals(v4), false);
    t.checkExpect(v4.equals(v4), true);

  }
}
