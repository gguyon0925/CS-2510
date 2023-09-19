import javalib.impworld.WorldScene;
import java.util.ArrayList;
import tester.*;

// Represents a player in the game
public class Player {
  Vertex currentPosition;
  ArrayList<Edge> visitedEdges;
  Utils utils;

  // Initializes a new Player with a starting position
  Player(Vertex startPosition) {
    this.currentPosition = startPosition;
    this.utils = new Utils();
    this.visitedEdges = new ArrayList<>();
  }

  // Moves the player's currentPosition based on the input dx/dy,
  // and records the player's move in the ArrayList visitedEdges
  // Returns the index of the visited edge in the given ArrayList<Edge> arr, or -1 if no edge found
  int movePlayer(int dx, int dy, ArrayList<Edge> arr, int width, int height) {
    Vertex destination = this.currentPosition.addVertex(dx, dy);
    if (!destination.isInBounds(width, height)) {
      return -1;
    }

    int index = 0;
    for (Edge edge : arr) {
      if (connectsVertices(edge, this.currentPosition, destination)) {
        this.currentPosition = destination;
        visitedEdges.add(edge);
        return index;
      }
      index++;
    }
    return -1;
  }

  // helper method only used for the move method, helps readability of code and makes it more modular
  private boolean connectsVertices(Edge edge, Vertex currentPosition, Vertex destination) {
    return edge.includesVertex(currentPosition) && edge.includesVertex(destination);
  }

  // Draws the player on the given WorldScene
  void drawPlayer(WorldScene ws, int cellSize, int width, int height) {
    this.currentPosition.drawVertex(ws, cellSize, width, height, true);
  }

  // Returns true if the player is at the same position as the given vertex
  boolean isAtPosition(Vertex v) {
    return this.currentPosition.equals(v);
  }
}

class TestPlayer {
  Player player;
  Vertex startPosition;
  Vertex destination;
  Edge edge;
  ArrayList<Edge> arr;

  void initData() {
    startPosition = new Vertex(0, 0);
    destination = new Vertex(1, 0);
    edge = new Edge(startPosition, destination, 1);
    arr = new ArrayList<>();
    arr.add(edge);
    player = new Player(startPosition);
  }

  // Test the movePlayer method
  void testMovePlayer(Tester t) {
    initData();

    int dx = 1;
    int dy = 0;
    int width = 10;
    int height = 10;
    int index = player.movePlayer(dx, dy, arr, width, height);

    t.checkExpect(index, 0);
    t.checkExpect(player.currentPosition, destination);
    t.checkExpect(player.visitedEdges.get(0), edge);
  }

  // Test the drawPlayer method
  void testDrawPlayer(Tester t) {
    initData();

    int cellSize = 10;
    int width = 10;
    int height = 10;
    WorldScene ws = new WorldScene(width, height);
    player.drawPlayer(ws, cellSize, width, height);

    t.checkExpect(ws.width, width);
    t.checkExpect(ws.height, height);
  }

  // Test the movePlayer method when destination is out of bounds
  void testMovePlayerOutOfBounds(Tester t) {
    initData();

    int dx = 10;
    int dy = 10;
    int width = 10;
    int height = 10;
    int index = player.movePlayer(dx, dy, arr, width, height);

    t.checkExpect(index, -1);
    t.checkExpect(player.currentPosition, startPosition);
    t.checkExpect(player.visitedEdges.size(), 0);
  }

  // Test the isAtPosition method
  void testIsAtPosition(Tester t) {
    initData();

    t.checkExpect(player.isAtPosition(startPosition), true);
    t.checkExpect(player.isAtPosition(destination), false);

  }
}