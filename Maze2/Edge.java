import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tester.*;

class Edge {
  Vertex start;
  Vertex end;
  int weight;
  Utils utils;
  boolean playerVisited;
  boolean computerVisited;
  boolean isCorrect;

  // Constructor
  Edge(Vertex start, Vertex end, int weight) {
    this.start = start;
    this.end = end;
    this.weight = weight;
    utils = new Utils();
    this.playerVisited = false;
    this.computerVisited = false;
    this.isCorrect = false;
  }

  // Compares the weights of two edges
  int compareWeights(Edge other) {
    return Integer.compare(this.weight, other.weight);
  }

  // Returns true if the edge will cause a cycle
  boolean causesCycle(HashMap<Vertex, Vertex> reps) {
    return this.utils.hasCycle(reps, this.start, this.end);
  }

  // Adds a vertex to the list of vertices if it is not already there
  void addUniqueVertices(ArrayList<Vertex> vertices) {
    this.utils.addWithoutDuplicates(vertices, this.start);
    this.utils.addWithoutDuplicates(vertices, this.end);
  }

  // Draws the edge on the given world scene at the given cell size
  void drawEdge(WorldScene ws, int cellSize) {
    int direction = this.start.findDirection(this.end);
    Color color = getColor();

    int x, y;
    switch (direction) {
    case Vertex.UP:
      x = (this.start.x * cellSize) + (cellSize / 2) + 1;
      y = (this.start.y * cellSize);
      drawRectangle(ws, cellSize - 1, 2, color, x, y);
      break;
    case Vertex.DOWN:
      x = (this.start.x * cellSize) + (cellSize / 2) + 1;
      y = (this.start.y * cellSize) + cellSize;
      drawRectangle(ws, cellSize - 1, 2, color, x, y);
      break;
    case Vertex.LEFT:
      x = (this.start.x * cellSize);
      y = (this.start.y * cellSize) + (cellSize / 2) + 1;
      drawRectangle(ws, 2, cellSize - 1, color, x, y);
      break;
    default: // Vertex.RIGHT
      x = (this.start.x * cellSize) + cellSize;
      y = (this.start.y * cellSize) + (cellSize / 2) + 1;
      drawRectangle(ws, 2, cellSize - 1, color, x, y);
    }
  }

  // Returns the color of the edge based on its state, only used in drawEdge
  private Color getColor() {
    if (this.isCorrect) {
      return new Color(62, 118, 204);
    }
    else if (this.playerVisited) {
      return new Color(255, 107, 53);
    }
    else if (this.computerVisited) {
      return new Color(144, 184, 242);
    }
    else {
      return new Color(192, 192, 192);
    }
  }

  // Draws a rectangle on the given world scene at the given coordinates, only used in drawEdge
  private void drawRectangle(WorldScene ws, int width, int height, Color color, int x, int y) {
    ws.placeImageXY(new RectangleImage(width, height, "solid", color), x, y);
  }

  // Returns true if the given vertex is one of the vertices of this edge
  boolean includesVertex(Vertex v) {
    return this.start.equals(v) || this.end.equals(v);
  }

  // Returns the other vertex of this edge
  Vertex oppositeVertex(Vertex v) {
    if (!this.includesVertex(v)) {
      return v;
    }
    else {
      if (v.equals(this.end)) {
        return this.start;
      }
      else {
        return this.start;
      }
    }
  }

  // If the player has visited this edge, mark the vertices as visited
  void playerVisitedEdge(ArrayList<Vertex> vertices) {
    this.playerVisited = true;
    for (Vertex v : vertices) {
      if (v.equals(this.end) || v.equals(this.start)) {
        v.visitByUser();
      }
    }
  }

  // If the computer has visited this edge, mark the vertices as visited
  void computerVisitedEdge(ArrayList<Vertex> vertices) {
    this.computerVisited = true;
    for (Vertex v : vertices) {
      if (v.equals(this.start) || v.equals(this.end)) {
        v.visitByComputer();
      }
    }
  }

  // Marks the edge as correct and the vertices as correct
  void markEdgeCorrect(ArrayList<Vertex> vertices) {
    this.isCorrect = true;
    for (Vertex v : vertices) {
      if (v.equals(this.start) || v.equals(this.end)) {
        v.markAsCorrect();
      }
    }
  }

  // Resets the edge and the vertices it contains to their default state
  void resetEdge(ArrayList<Vertex> vertices) {
    this.isCorrect = false;
    this.computerVisited = false;
    this.playerVisited = false;
    for (Vertex v : vertices) {
      if (v.equals(this.start) || v.equals(this.end)) {
        v.resetVertex();
      }
    }
  }

  @Override
  // overrides the hashCode method for generic purposes
  public int hashCode() {
    return (this.start.hashCode() * 100000) + this.end.hashCode();
  }

  @Override
  // overrides the equals method for generic purposes
  public boolean equals(Object other) {
    if (other instanceof Edge) {
      Edge that = (Edge) other;
      return ((this.start.equals(that.start) && this.end.equals(that.end))
        || (this.start.equals(that.end) && this.end.equals(that.start)));
    } else {
      return false;
    }
  }
}

class TestEdge {
  Vertex v1, v2, v3, v4;
  Edge e1, e2, e3, e4, e5;

  void initData() {
    v1 = new Vertex(0, 0);
    v2 = new Vertex(1, 0);
    v3 = new Vertex(1, 1);
    v4 = new Vertex(0, 1);

    e1 = new Edge(v1, v2, 1);
    e2 = new Edge(v2, v3, 2);
    e3 = new Edge(v1, v3, 3);
    e4 = new Edge(v3, v4, 4);
    e5 = new Edge(v1, v4, 5);
  }

  void testCompareWeights(Tester t) {
    initData();
      t.checkExpect(e1.compareWeights(e2), -1);
      t.checkExpect(e2.compareWeights(e1), 1);
      t.checkExpect(e1.compareWeights(e1), 0);
  }

  void testCausesCycle(Tester t) {
    initData();
      HashMap<Vertex, Vertex> reps = new HashMap<>();
      reps.put(v1, v1);
      reps.put(v2, v1);
      reps.put(v3, v3);
      reps.put(v4, v3);

      t.checkExpect(e1.causesCycle(reps), true);
      t.checkExpect(e2.causesCycle(reps), false);
      t.checkExpect(e3.causesCycle(reps), true);
  }

  void testAddUniqueVertices(Tester t) {
    initData();
    ArrayList<Vertex> vertices = new ArrayList<>();
    e1.addUniqueVertices(vertices);
    
    ArrayList<Vertex> expected1 = new ArrayList<>(List.of(v1, v2));
    t.checkExpect(vertices.size(), expected1.size());
    for (int i = 0; i < vertices.size(); i++) {
        t.checkExpect(vertices.get(i), expected1.get(i));
    }

    e2.addUniqueVertices(vertices);
    
    ArrayList<Vertex> expected2 = new ArrayList<>(List.of(v1, v2, v3));
    t.checkExpect(vertices.size(), expected2.size());
    for (int i = 0; i < vertices.size(); i++) {
        t.checkExpect(vertices.get(i), expected2.get(i));
    }

    e3.addUniqueVertices(vertices);
    
    ArrayList<Vertex> expected3 = new ArrayList<>(List.of(v1, v2, v3));
    t.checkExpect(vertices.size(), expected3.size());
    for (int i = 0; i < vertices.size(); i++) {
        t.checkExpect(vertices.get(i), expected3.get(i));
    }

    e5.addUniqueVertices(vertices);
    
    ArrayList<Vertex> expected4 = new ArrayList<>(List.of(v1, v2, v3, v4));
    t.checkExpect(vertices.size(), expected4.size());
    for (int i = 0; i < vertices.size(); i++) {
        t.checkExpect(vertices.get(i), expected4.get(i));
    }
}


  void testIncludesVertex(Tester t) {
    initData();
      t.checkExpect(e1.includesVertex(v1), true);
      t.checkExpect(e1.includesVertex(v2), true);
      t.checkExpect(e1.includesVertex(v3), false);
      t.checkExpect(e1.includesVertex(v4), false);
  }

  void testOppositeVertex(Tester t) {
    initData();
      t.checkExpect(e1.oppositeVertex(v1), v1);
      t.checkExpect(e1.oppositeVertex(v2), v1);
      t.checkExpect(e1.oppositeVertex(v3), v3);
      t.checkExpect(e1.oppositeVertex(v4), v4);
  }

  void testEquals(Tester t) {
    initData();
      Edge e1Copy = new Edge(v1, v2, 1);
      Edge e1Reversed = new Edge(v2, v1, 1);

      t.checkExpect(e1.equals(e1Copy), true);
      t.checkExpect(e1.equals(e1Reversed), true);
      t.checkExpect(e1.equals(e2), false);
  }

  void testHashCode(Tester t) {
    initData();
      Edge e1Copy = new Edge(v1, v2, 1);

      t.checkExpect(e1.hashCode(), e1Copy.hashCode());
      t.checkExpect(e1.hashCode(), 1000);
      t.checkExpect(e1.hashCode() == e2.hashCode(), false);
      t.checkExpect(e2.hashCode(), 100001001);
  }
}