







import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import java.awt.Color;

// represents a Vertex in a graph
// represents a Cell in a maze
public class Vertex {
  int x;
  int y;
  boolean userVisited;
  boolean compVisited;
  boolean correct;
  static final int CELL_SIZE = 32;
  static final int NORTH = 0;
  static final int EAST = 1;
  static final int SOUTH = 2;
  static final int WEST = 3;

  // constructor
  Vertex(int x, int y) {
    this.x = x;
    this.y = y;
    this.userVisited = false;
    this.compVisited = false;
    this.correct = false;
  }

  @Override
  // overrides the hashCode method for equality purposes in HashMaps
  public int hashCode() {
    return (this.x * 1000) + this.y;
  }

  @Override
  // overrides the equals method for generic purposes
  public boolean equals(Object other) {
    if (other instanceof Vertex) {
      Vertex that = (Vertex) other;
      return this.x == that.x && this.y == that.y;
    } else {
      return false;
    }
  }

  // returns the direction that the given Vertex is in relative to this one
  int direction(Vertex other) {
    if (this.x - other.x < 0) {
      return EAST;
    } else if (this.x - other.x > 0) {
      return WEST;
    } else if (this.y - other.y < 0) {
      return SOUTH;
    } else {
      return NORTH;
    }
  }

  // draws a cell onto the given WorldScene
  void drawVertex(WorldScene ws, int cellSize, int width, int height, boolean playerOn) {
    ws.placeImageXY(new RectangleImage(cellSize, cellSize, "solid",
        this.cellColor(width, height, playerOn)),
        (this.x * cellSize) + (cellSize / 2), (this.y * cellSize) + (cellSize / 2));
    ws.placeImageXY(new RectangleImage(cellSize, cellSize, "outline", Color.black),
        (this.x * cellSize) + (cellSize / 2), (this.y * cellSize) + (cellSize / 2));
  }

  // returns the correct cell color based on the cell's position
  Color cellColor(int width, int height, boolean playerOn) {
    if (this.correct) {
      return new Color(62, 118, 204);
    } else if (this.userVisited) {
      return new Color(255, 107, 53);
    } else if (playerOn) {
      return new Color(255, 200, 10);
    } else if (this.compVisited) {
      return new Color(144, 184, 242);
    }  else if (this.x == 0 && this.y == 0) {
      return new Color(33, 127, 70);
    } else if (this.x == width - 1 && this.y == height - 1) {
      return new Color(108, 32, 128);
    } else {
      return new Color(192, 192, 192);
    }
  }

  // sets the vertex as visited (by user)
  void userVertex() {
    this.userVisited = true;
  }

  // sets the vertex as visited (by computer)
  void visitVertex() {
    this.compVisited = true;
  }

  // sets the vertex as part of a correct solution
  void correctVertex() {
    this.correct = true;
    //this.visitVertex();
  }

  // resets the vertex to an untouched state
  void resetVertex() {
    this.correct = false;
    this.compVisited = false;
    this.userVisited = false;
  }

  // adds the given ints to the Vertex's x and y coords
  Vertex addVertices(int dx, int dy) {
    return new Vertex(this.x + dx, this.y + dy);
  }

  // returns true if the Vertex is within the given bounds
  boolean inBounds(int width, int height) {
    return (this.x > 0 || this.y > 0 || this.x < width || this.y < height);
  }

}
