import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javalib.impworld.*;
import javalib.worldimages.*;
import java.util.Comparator;
import tester.*;

// Represents a Maze
class Maze extends World {
  // Fields used for maze
  int mazeWidth;
  int mazeHeight;
  int responsiveSize;
  int horizontal;
  int vertical;
  // Fields used for lists of edges and vertices
  ArrayList<Edge> allEdges;
  ArrayList<Vertex> allVertices;
  ArrayList<Edge> edgesInTree;
  ArrayList<Edge> breadthSearch;
  ArrayList<Edge> depthSearch;
  ArrayList<Edge> solutionPath;
  // Represents the player
  Player player;
  // Fields used for animating
  ArrayList<Edge> animateList;
  int drawing;
  int animationSpeed;
  boolean solutionDisp;
  // Utilities
  Utils utils;
  Random rand;
  // Constants used for drawing
  static final int WALLS = 0;
  static final int SEARCH = 1;
  static final int SOLUTIONLINE = 2;

  // Constructor for testing
  Maze(ArrayList<Edge> edges) {
    this.utils = new Utils();
    this.rand = new Random();
    this.mazeWidth = mazeWidth;
    this.mazeHeight = mazeHeight;
    this.responsiveSize = this.calculateCellSize();
    this.horizontal = 1;
    this.vertical = 1;
    this.allEdges = edges;
    this.allVertices = this.utils.collectVertices(this.allEdges);
    this.edgesInTree = new ArrayList<>();
    this.breadthSearch = new ArrayList<>();
    this.depthSearch = new ArrayList<>();
    this.solutionPath = new ArrayList<>();
    this.player = new Player(new Vertex(0, 0));
    this.animateList = new ArrayList<>();
    this.drawing = -1;
    this.animationSpeed = 0;
    this.solutionDisp = false;
  }

  // Constructor for running the game
  Maze(int mazeWidth, int mazeHeight) {
    this.utils = new Utils();
    this.rand = new Random();
    this.mazeWidth = mazeWidth;
    this.mazeHeight = mazeHeight;
    this.responsiveSize = this.calculateCellSize();
    this.horizontal = 1;
    this.vertical = 1;
    this.initializeMaze();
    this.player = new Player(new Vertex(0, 0));
    this.animateList = new ArrayList<>();
    this.drawing = WALLS;
    this.startComputerPath(false);
    this.animationSpeed = 0;
    this.solutionDisp = false;
  }

  // USE THIS TO RUN THE GAME
  /*    GAME DIRECTIONS
   * Use the arrow keys to move the player around the maze
   * Press b or d to start the breadth and depth search
   * Press r to reset the maze
   * press q to create a new maze 
   */
  public static void main(String[] argv) {
    Maze maze = new Maze(30, 15);
    maze.bigBang(maze.mazeWidth * maze.responsiveSize,
        (maze.mazeHeight * maze.responsiveSize) + 100, .01);
  }

  // Generates the final graph, with all breadth, depth, and solutions calculated
  void initializeMaze() {
    this.allEdges = this.createRandomWeightedGraph(this.mazeWidth, this.mazeHeight);
    this.allVertices = this.utils.collectVertices(this.allEdges);
    this.edgesInTree = this.kruskal(this.allEdges, this.allVertices);

    Vertex startVertex = new Vertex(0, 0);
    Vertex endVertex = new Vertex(this.mazeWidth - 1, this.mazeHeight - 1);

    this.breadthSearch = this.searchMaze(startVertex, endVertex, this.edgesInTree, true, false);
    this.depthSearch = this.searchMaze(startVertex, endVertex, this.edgesInTree, false, false);
    this.solutionPath = this.searchMaze(startVertex, endVertex, this.edgesInTree, false, true);
    this.solutionDisp = false;
  }

  // returns a minimum spanning tree using the Kruskal's algorithm
  ArrayList<Edge> kruskal(ArrayList<Edge> edges, ArrayList<Vertex> vertices) {
    // Sort the edges by weight
    this.utils.quicksort(edges, new EdgeComparator());

    HashMap<Vertex, Vertex> reps = new HashMap<>();
    for (Vertex v : vertices) {
      reps.put(v, v);
    }

    ArrayList<Edge> goodEdges = new ArrayList<>();
    Iterator<Edge> edgesIterator = edges.iterator();
    while (goodEdges.size() < vertices.size() - 1 && edgesIterator.hasNext()) {
      Edge edge = edgesIterator.next();
      if (!edge.causesCycle(reps)) {
        goodEdges.add(edge);
        edgesIterator.remove();
      }
    }

    if (goodEdges.size() < vertices.size() - 1) {
      throw new Error("Not enough edges for the amount of vertices");
    }

    return goodEdges;
  }

  // Generates a graph with edges that have random weights 
  ArrayList<Edge> createRandomWeightedGraph(int width, int height) {
    ArrayList<Edge> edges = new ArrayList<>();
    int area = width * height;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Vertex currentVertex = new Vertex(x, y);
        if (y > 0) {
          Edge north = new Edge(currentVertex, new Vertex(x, y - 1),
              rand.nextInt(area) * horizontal);
          edges.add(north);
        }
        if (y < height - 1) {
          Edge south = new Edge(currentVertex, new Vertex(x, y + 1),
              rand.nextInt(area) * horizontal);
          edges.add(south);
        }
        if (x > 0) {
          Edge west = new Edge(currentVertex, new Vertex(x - 1, y), rand.nextInt(area) * vertical);
          edges.add(west);
        }
        if (x < width - 1) {
          Edge east = new Edge(currentVertex, new Vertex(x + 1, y), rand.nextInt(area) * vertical);
          edges.add(east);
        }
      }
    }
    return edges;
  }

  // returns the current worldScene
  public WorldScene makeScene() {
    WorldScene ws = new WorldScene(this.mazeWidth * this.responsiveSize,
        (this.mazeHeight * this.responsiveSize) + 40);

    for (Vertex v : this.allVertices) {
      v.drawVertex(ws, this.responsiveSize, this.mazeWidth, this.mazeHeight, false);
    }

    if (this.animateList.isEmpty()) {
      handleEmptyAnimateList();
    }
    else {
      handleNonEmptyAnimateList();
    }

    if (this.animateList.isEmpty() && !solutionDisp) {
      this.player.drawPlayer(ws, this.responsiveSize, this.mazeWidth, this.mazeHeight);
    }

    for (Edge e : this.edgesInTree) {
      e.drawEdge(ws, this.responsiveSize);
    }

    ws.placeImageXY(
        new OverlayImage(new TextImage(getCurrentState(), 50, Color.blue),
            new RectangleImage(this.mazeWidth * this.responsiveSize, 90, "solid", Color.white)),
        (this.mazeWidth * this.responsiveSize) / 2, (this.mazeHeight * this.responsiveSize) + 50);
    return ws;
  }

  // helper only used in the makeScene method to handle empty animateList
  private void handleEmptyAnimateList() {
    if (this.drawing == Maze.SEARCH) {
      this.drawing = Maze.SOLUTIONLINE;
      this.startComputerPath(false);
    }
    else {
      this.drawing = -1;
    }
  }

  // helper only used in the makeScene method to handle non-empty animateList
  private void handleNonEmptyAnimateList() {
    if (this.drawing == Maze.SOLUTIONLINE || this.drawing == Maze.SEARCH) {
      if (this.animationSpeed % 2 == 0) {
        this.animationSpeed = 1;
        int index = this.edgesInTree.indexOf(this.animateList.remove(0));
        if (this.drawing == Maze.SOLUTIONLINE) {
          this.edgesInTree.get(index).markEdgeCorrect(this.allVertices);
        }
        else {
          this.edgesInTree.get(index).computerVisitedEdge(this.allVertices);
        }
      }
      else {
        this.animationSpeed += 1;
      }
    }
    else if (this.drawing == Maze.WALLS) {
      this.edgesInTree.add(0, this.animateList.remove(0));
    }
  }

  // helper to the makeScene method
  // returns the text for the status bar
  String getCurrentState() {
    if (!this.animateList.isEmpty()) {
      return getAnimateListStatus();
    }
    else if (this.player.isAtPosition(new Vertex(this.mazeWidth - 1, this.mazeHeight - 1))) {
      return handleMazeSolved();
    }
    else if (this.solutionDisp) {
      return scoreBreadthDepth();
    }
    else {
      return "Maze Is Ready To Be Solved!";
    }
  }

  // helper to animate maze walls, search algorithms, or solution
  private String getAnimateListStatus() {
    if (this.drawing == WALLS) {
      return "Making the maze, removing lines: " + this.animateList.size();
    }
    else if (this.drawing == SEARCH) {
      return "Solving...";
    }
    else {
      return "Solution:";
    }
  }

  // helper used to display the status when maze is solved
  private String handleMazeSolved() {
    String status = "Maze Solved!";
    if (!this.solutionDisp) {
      this.drawing = Maze.SOLUTIONLINE;
      this.startComputerPath(false);
    }
    return status;
  }

  // starts the animation for any part of the maze including the walls, search, and solution
  public void startComputerPath(boolean breadth) {
    this.animateList = new ArrayList<>();

    if (this.drawing == WALLS) {
      this.animateList.addAll(this.edgesInTree);
      this.edgesInTree.clear();
    }
    else if (this.drawing == Maze.SEARCH || this.drawing == Maze.SOLUTIONLINE) {
      ArrayList<Edge> localSol = new ArrayList<>();

      if (this.drawing == Maze.SOLUTIONLINE) {
        localSol.addAll(this.solutionPath);
      }
      else {
        resetEdgesInTree();
        this.player = new Player(new Vertex(0, 0));
        localSol.addAll(breadth ? this.breadthSearch : this.depthSearch);
      }

      this.animateList.addAll(localSol);
      this.solutionDisp = true;
    }
  }

  // only used in the startComputerPath method, meant to reset the edges in the tree
  private void resetEdgesInTree() {
    for (Edge e : this.edgesInTree) {
      e.resetEdge(this.allVertices);
    }
  }

  // performs an action based on a given pressed key
  public void onKeyEvent(String s) {
    s = s.toLowerCase();

    if (s.equals("q")) {
      resetEdgesAndPlayer();
      this.initializeMaze();
      this.drawing = Maze.WALLS;
      this.startComputerPath(false);
    }
    else if (this.animateList.isEmpty()) {
      handleNonAnimatingEvents(s);
    }
  }

  // helper only used in the onKeyEvent method to reset the edges and player if q is pressed
  // makes a whole new maze and resets the player
  private void resetEdgesAndPlayer() {
    for (Edge e : this.edgesInTree) {
      e.resetEdge(this.allVertices);
    }
    this.player = new Player(new Vertex(0, 0));
  }

  // helper only used in the onKeyEvent method to handle the events for 
  // breadth, depth, reset, and player movement, reseting here will reset the maze
  // without creating a new one
  private void handleNonAnimatingEvents(String s) {
    if (s.equals("b") || s.equals("d")) {
      this.drawing = Maze.SEARCH;
      this.startComputerPath(s.equals("b"));
    }
    else if (s.equals("r")) {
      resetEdgesAndPlayer();
      this.solutionDisp = false;
    }
    else if (!solutionDisp
        && (s.equals("up") || s.equals("left") || s.equals("down") || s.equals("right"))) {
      handlePlayerMovement(s);
    }
  }

  // helper only used in the handleNonAnimatingEvents method to handle player movement
  private void handlePlayerMovement(String s) {
    int index;
    int dx = 0, dy = 0;

    switch (s) {
    case "up":
      dy = -1;
      break;
    case "left":
      dx = -1;
      break;
    case "down":
      dy = 1;
      break;
    case "right":
      dx = 1;
      break;
    }

    index = player.movePlayer(dx, dy, this.edgesInTree, this.mazeWidth, this.mazeHeight);

    if (index > 0 && index < this.edgesInTree.size()) {
      this.edgesInTree.get(index).playerVisitedEdge(this.allVertices);
    }
  }

  // returns the correct cell size for the maze's width and height
  int calculateCellSize() {
    if (Vertex.CELL_SIZE * this.mazeWidth > 1000 || Vertex.CELL_SIZE * this.mazeHeight > 1000) {
      return Vertex.CELL_SIZE / 2;
    }
    else {
      return Vertex.CELL_SIZE;
    }
  }

  ArrayList<Edge> searchMaze(Vertex start, Vertex end, ArrayList<Edge> edges, boolean breadth,
      boolean solved) {
    IDeque<Vertex> vertices = breadth ? new Queue<>() : new Stack<>();
    HashMap<Vertex, Edge> cameFromEdge = new HashMap<>();
    vertices.addAtHead(start);
    HashSet<Vertex> visited = new HashSet<>();
    ArrayList<Edge> searchList = new ArrayList<>();

    while (!vertices.isEmpty()) {
      Vertex next = vertices.removeHead();

      if (visited.contains(next)) {
        continue;
      }

      if (next.equals(end)) {
        if (solved) {
          return this.utils.reverse(searchMazeHelp(start, cameFromEdge, next));
        }
        else {
          Edge lastEdge = searchList.stream().filter(e -> e.includesVertex(next)).findFirst()
              .orElse(null);

          if (lastEdge != null) {
            searchList.add(new Edge(lastEdge.oppositeVertex(next), next, 0));
          }
          return searchList;
        }
      }

      visited.add(next);
      ArrayList<Vertex> neighbors = this.utils.getNeighbors(next, edges);

      for (Vertex v : neighbors) {
        if (!visited.contains(v)) {
          vertices.addAtHead(v);
          Edge edge = new Edge(next, v, 0);
          cameFromEdge.put(v, edge);
          searchList.add(edge);
        }
      }
    }
    return searchList;
  }

  // helper only used in the searchMaze method to help with the solution path
  ArrayList<Edge> searchMazeHelp(Vertex start, HashMap<Vertex, Edge> cameFromEdge, Vertex v) {
    ArrayList<Edge> result = new ArrayList<>();
    while (!v.equals(start)) {
      Edge e = cameFromEdge.get(v);
      if (e != null) {
        result.add(e);
        v = e.oppositeVertex(v);
      }
    }
    return result;
  }

  // reports the score of breadth vs depth first search
  String scoreBreadthDepth() {
    return "Breadth and Depth first search complete";

  }

}

// represents a Deque used for Maze game
interface IDeque<T> {
  // returns the element at the head of the Deque
  T head();

  // returns and removes the element at the head of the Deque
  T removeHead();

  // adds the given element to the head of the Deque
  T addAtHead(T t);

  // returns true if the Deque is empty
  boolean isEmpty();
}

// an ArrayList representing a queue structure
class Queue<T> implements IDeque<T> {
  ArrayList<T> items;

  // Constructor
  Queue(ArrayList<T> items) {
    this.items = items;
  }

  // Empty Constructor
  Queue() {
    this.items = new ArrayList<T>();
  }

  // returns the first (front) item in a Queue
  public T head() {
    if (this.isEmpty()) {
      throw new NullPointerException("Queue is Empty");
    }
    else {
      return this.items.get(0);
    }
  }

  // returns the first (front) item in a Queue and removes it from the Queue
  public T removeHead() {
    if (this.isEmpty()) {
      throw new NullPointerException("Queue is Empty");
    }
    else {
      return this.items.remove(0);
    }
  }

  // adds the given item to the end of the queue
  public T addAtHead(T t) {
    this.items.add(0, t);
    return t;
  }

  // returns whether or not this Queue is empty
  public boolean isEmpty() {
    return this.items.size() == 0;
  }
}

// represents a Stack
class Stack<T> implements IDeque<T> {
  ArrayList<T> items;

  // constructor
  Stack(ArrayList<T> items) {
    this.items = items;
  }

  // empty constructor
  Stack() {
    this.items = new ArrayList<>();
  }

  // returns the first element, or throws an error if the stack is empty
  public T head() {
    if (this.isEmpty()) {
      throw new NullPointerException("No items in list.");
    }
    else {
      return items.get(0);
    }
  }

  // returns and removes the first element, or throws an error if the stack is empty
  public T removeHead() {
    if (this.isEmpty()) {
      throw new NullPointerException("No items in list.");
    }
    else {
      return items.remove(0);
    }
  }

  // adds the given element to the front of the list
  public T addAtHead(T t) {
    items.add(0, t);
    return t;
  }

  // determines if the stack is empty
  public boolean isEmpty() {
    return items.size() == 0;
  }
}

// represents a comparator for edges
class EdgeComparator implements Comparator<Edge> {
  // compares two edges
  public int compare(Edge e1, Edge e2) {
    return e1.compareWeights(e2);
  }
}

// Represents utility functions for ArrayLists and HashMaps
class Utils {
  // Concatenates two ArrayLists
  <T> ArrayList<T> concatenate(ArrayList<T> a1, ArrayList<T> a2) {
    for (T t : a2) {
      a1.add(t);
    }
    return a1;
  }

  // Sorts an ArrayList using a quicksort algorithm based on the given comparator
  <T> void quicksort(ArrayList<T> arr, Comparator<T> comp) {
    quicksort(arr, comp, 0, arr.size() - 1);
  }

  // Overloaded quicksort method with specified range
  <T> void quicksort(ArrayList<T> arr, Comparator<T> comp, int low, int high) {
    if (low < high) {
      int pivotIndex = partition(arr, comp, low, high);
      quicksort(arr, comp, low, pivotIndex - 1);
      quicksort(arr, comp, pivotIndex + 1, high);
    }
  }

  // Partitions the ArrayList based on a pivot value and comparator, and returns the pivot index
  <T> int partition(ArrayList<T> arr, Comparator<T> comp, int low, int high) {
    T pivot = arr.get(high);
    int i = low - 1;
    for (int j = low; j < high; j++) {
      if (comp.compare(arr.get(j), pivot) <= 0) {
        i++;
        swap(arr, i, j);
      }
    }
    swap(arr, i + 1, high);
    return i + 1;
  }

  // Swaps two elements in an ArrayList
  <T> void swap(ArrayList<T> arr, int i, int j) {
    T temp = arr.get(i);
    arr.set(i, arr.get(j));
    arr.set(j, temp);
  }

  // Adds an element to an ArrayList if the element does not already exist in the list
  <T> ArrayList<T> addWithoutDuplicates(ArrayList<T> arr, T t) {
    if (!arr.contains(t)) {
      arr.add(t);
    }
    return arr;
  }

  // Collects all of the Vertices from a list of Edges and adds them to an ArrayList
  ArrayList<Vertex> collectVertices(ArrayList<Edge> arr) {
    HashSet<Vertex> verticesSet = new HashSet<>();
    for (Edge e : arr) {
      e.addUniqueVertices(new ArrayList<>(verticesSet));
      verticesSet.add(e.start);
      verticesSet.add(e.end);
    }
    return new ArrayList<>(verticesSet);
  }

  // Returns whether or not the given items cause a cycle in a graph
  // if not, updates the HashMap accordingly
  <T> boolean hasCycle(HashMap<T, T> hash, T t1, T t2) {
    T rootOne = findRoot(hash, t1);
    T rootTwo = findRoot(hash, t2);
    if (rootOne.equals(rootTwo)) {
      // they are already connected
      return true;
    }
    else {
      // they are separate
      hash.replace(rootTwo, rootOne);
      return false;
    }
  }

  // Helper method for hasCycle; finds the root connector of the given item in a HashMap
  <T> T findRoot(HashMap<T, T> hash, T t) {
    if (!hash.containsKey(t)) {
      throw new NullPointerException("Invalid key");
    }
    while (!hash.get(t).equals(t)) {
      t = hash.get(t);
    }
    return t;
  }

  // Returns the neighbors of the given Vertex as directed by the given ArrayList of Edges
  ArrayList<Vertex> getNeighbors(Vertex v, ArrayList<Edge> edges) {
    ArrayList<Vertex> neighbors = new ArrayList<>();
    for (Edge e : edges) {
      if (e.includesVertex(v)) {
        neighbors.add(e.start.equals(v) ? e.end : e.start);
      }
    }
    return neighbors;
  }

  // Reverses an ArrayList
  <T> ArrayList<T> reverse(ArrayList<T> arr) {
    ArrayList<T> reversed = new ArrayList<>(arr.size());
    for (int i = arr.size() - 1; i >= 0; i--) {
      reversed.add(arr.get(i));
    }
    return reversed;
  }

  // Gets the values of a HashMap given the keys and returns them in an ArrayList
  <T, U> ArrayList<U> getValues(HashMap<T, U> hash, ArrayList<T> keys) {
    ArrayList<U> values = new ArrayList<>(keys.size());
    for (T t : keys) {
      U value = hash.get(t);
      if (value != null) {
        values.add(value);
      }
    }
    return values;
  }
}

// Represents a vertex in a graph or a cell in a maze
class Vertex {
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

// Represents a player in the game
class Player {
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
    }
    else {
      return false;
    }
  }
}

// Represents the tests for all the methods that create the maze
class MazeTest {
  // represents the maze
  Maze maze, mazeTest;

  // represents all the edges in the maze
  ArrayList<Edge> allEdges;
  // represents all the vertices in the maze
  ArrayList<Vertex> allVertices;

  // represents the queue and stack used in the maze
  Queue<Integer> queue;
  Stack<Integer> stack;

  // represents the edges and vertices used in the maze
  Edge edge1;
  Edge edge2;
  Vertex v1, v2, v3, v4, vertex1, vertex2, vertex3;
  Edge e1, e2, e3, e4, e5;

  // represents the maze utils used in the maze
  Utils utils;

  // represents the comparator used in the maze
  EdgeComparator comparator;
  Comparator<Edge> comp;

  // worldscenes used for testing 
  WorldScene background;
  WorldScene expectedScene;

  // data used to test the methods in the player class 
  Player player;
  Vertex startPosition;
  Vertex destination;
  Edge edge;
  ArrayList<Edge> arr;

  void initData() {
    // initialize all the data
    allEdges = new ArrayList<>();
    allEdges.add(new Edge(new Vertex(0, 0), new Vertex(1, 0), 1));
    allEdges.add(new Edge(new Vertex(1, 0), new Vertex(1, 1), 1));
    allEdges.add(new Edge(new Vertex(1, 1), new Vertex(0, 1), 1));
    allEdges.add(new Edge(new Vertex(0, 1), new Vertex(0, 0), 1));

    maze = new Maze(allEdges);
    mazeTest = new Maze(2, 2);

    queue = new Queue<>();
    stack = new Stack<>();
    edge1 = new Edge(new Vertex(0, 0), new Vertex(1, 1), 2);
    edge2 = new Edge(new Vertex(0, 0), new Vertex(1, 1), 3);
    comparator = new EdgeComparator();

    utils = new Utils();
    comp = new EdgeComparator();

    v1 = new Vertex(0, 0);
    v2 = new Vertex(1, 0);
    v3 = new Vertex(1, 1);
    v4 = new Vertex(0, 1);

    e1 = new Edge(v1, v2, 1);
    e2 = new Edge(v2, v3, 2);
    e3 = new Edge(v1, v3, 3);
    e4 = new Edge(v3, v4, 4);
    e5 = new Edge(v1, v4, 5);

    vertex1 = new Vertex(0, 0);
    vertex2 = new Vertex(1, 1);
    vertex3 = new Vertex(2, 2);

    this.background = new WorldScene(4 * Vertex.CELL_SIZE, 4 * Vertex.CELL_SIZE);
    this.expectedScene = new WorldScene(4 * Vertex.CELL_SIZE, 4 * Vertex.CELL_SIZE);

    startPosition = new Vertex(0, 0);
    destination = new Vertex(1, 0);
    edge = new Edge(startPosition, destination, 1);
    arr = new ArrayList<>();
    arr.add(edge);
    player = new Player(startPosition);
  }

  // Test the kruskal method
  void testKruskal(Tester t) {
    initData();

    allVertices = maze.allVertices;
    ArrayList<Edge> mst = maze.kruskal(allEdges, allVertices);

    t.checkExpect(mst.size(), allVertices.size() - 1);
    t.checkExpect(mst.contains(new Edge(new Vertex(0, 0), new Vertex(1, 0), 1)), true);
    t.checkExpect(mst.contains(new Edge(new Vertex(1, 0), new Vertex(1, 1), 1)), true);
    t.checkExpect(mst.contains(new Edge(new Vertex(1, 1), new Vertex(0, 1), 1)), true);
  }

  // Test the initializeMaze method
  void testInitializeMaze(Tester t) {
    initData();

    maze.initializeMaze();

    t.checkExpect(maze.edgesInTree.size(), maze.allVertices.size());
    t.checkExpect(maze.edgesInTree.contains(new Edge(new Vertex(0, 0), new Vertex(1, 0), 1)),
        false);
    t.checkExpect(maze.edgesInTree.contains(new Edge(new Vertex(1, 0), new Vertex(1, 1), 1)),
        false);
    t.checkExpect(maze.edgesInTree.contains(new Edge(new Vertex(1, 1), new Vertex(0, 1), 1)),
        false);

    // Check if breadthSearch, depthSearch and solutionPath are populated correctly
    t.checkExpect(maze.breadthSearch.size() > 0, false);
    t.checkExpect(maze.depthSearch.size() > 0, false);
    t.checkExpect(maze.solutionPath.size() > 0, false);

    HashSet<Vertex> uniqueVertices = new HashSet<>();
    for (Edge e : maze.edgesInTree) {
      uniqueVertices.add(e.start);
      uniqueVertices.add(e.end);
    }

    int expectedVertices = maze.mazeWidth * maze.mazeHeight;
    t.checkExpect(uniqueVertices.size(), expectedVertices);
  }

  // Test the createRandomWeightedGraph method
  void testCreateRandomWeightedGraph(Tester t) {
    initData();

    int width = 2;
    int height = 2;
    ArrayList<Edge> edges = maze.createRandomWeightedGraph(width, height);

    HashSet<Vertex> uniqueVertices = new HashSet<>();
    for (Edge e : edges) {
      uniqueVertices.add(e.start);
      uniqueVertices.add(e.end);
    }

    int expectedVertices = width * height;
    t.checkExpect(uniqueVertices.size(), expectedVertices);
    t.checkExpect(edges.size(), 8);
  }

  // Test the makeScene method
  void testMakeScene(Tester t) {
    initData();

    mazeTest.initializeMaze();
    WorldScene ws = mazeTest.makeScene();

    // Check if the world scene has the expected dimensions
    t.checkExpect(ws.width, mazeTest.mazeWidth * mazeTest.responsiveSize);
    t.checkExpect(ws.height, (mazeTest.mazeHeight * mazeTest.responsiveSize) + 40);
  }

  // Test getCurrentState method
  void testGetCurrentState(Tester t) {
    initData();

    // Test when animateList is not empty
    maze.animateList.add(new Edge(new Vertex(0, 0), new Vertex(1, 0), 1));
    maze.drawing = Maze.WALLS;
    t.checkExpect(maze.getCurrentState(),
        "Making the maze, removing lines: " + maze.animateList.size());

    maze.drawing = Maze.SEARCH;
    t.checkExpect(maze.getCurrentState(), "Solving...");

    maze.drawing = Maze.SOLUTIONLINE;
    t.checkExpect(maze.getCurrentState(), "Solution:");

    // Test when player is at the end position
    maze.animateList.clear();
    maze.player = new Player(new Vertex(maze.mazeWidth - 1, maze.mazeHeight - 1));
    t.checkExpect(maze.getCurrentState(), "Maze Solved!");

    // Test when solutionDisp is true
    maze.animateList.clear();
    maze.solutionDisp = true;
    t.checkExpect(maze.getCurrentState(), "Maze Solved!");

    initData();
    // Test when maze is ready to be solved
    maze.animateList.clear();
    maze.solutionDisp = false;
    maze.player = new Player(new Vertex(0, 0));
    t.checkExpect(maze.getCurrentState(), "Maze Is Ready To Be Solved!");
  }

  // Test startComputerPath method
  void testStartComputerPath(Tester t) {
    initData();

    mazeTest.initializeMaze();
    mazeTest.startComputerPath(true);

    // Test when drawing == WALLS
    mazeTest.drawing = Maze.WALLS;
    mazeTest.startComputerPath(true);
    t.checkExpect(mazeTest.edgesInTree.isEmpty(), true);

    // Test when drawing == SEARCH
    mazeTest.drawing = Maze.SEARCH;
    mazeTest.startComputerPath(true);
    t.checkExpect(mazeTest.solutionDisp, true);

    // Test when drawing == SOLUTIONLINE
    mazeTest.drawing = Maze.SOLUTIONLINE;
    mazeTest.startComputerPath(true);
    t.checkExpect(mazeTest.solutionDisp, true);
  }

  // Test onKeyEvent method
  void testOnKeyEvent(Tester t) {
    initData();

    // Test player movement
    int rightIndex = maze.player.movePlayer(1, 0, maze.edgesInTree, maze.mazeWidth,
        maze.mazeHeight);
    if (rightIndex >= 0) {
      maze.onKeyEvent("right");
      t.checkExpect(maze.player.currentPosition, new Vertex(1, 0));
      t.checkExpect(maze.player.visitedEdges, Arrays.asList(maze.edgesInTree.get(rightIndex)));
    }

    int downIndex = maze.player.movePlayer(0, 1, maze.edgesInTree, maze.mazeWidth, maze.mazeHeight);
    if (downIndex >= 0) {
      maze.onKeyEvent("down");
      t.checkExpect(maze.player.currentPosition, new Vertex(1, 1));
      t.checkExpect(maze.player.visitedEdges,
          Arrays.asList(maze.edgesInTree.get(rightIndex), maze.edgesInTree.get(downIndex)));
    }

    // Test reset maze with 'r'
    maze.onKeyEvent("r");
    t.checkExpect(maze.player.currentPosition, new Vertex(0, 0));
    t.checkExpect(maze.player.visitedEdges.isEmpty(), true);

    // Test resetting the maze and creating a new one with 'q'
    maze.onKeyEvent("q");
    t.checkExpect(maze.player.currentPosition, new Vertex(0, 0));
    t.checkExpect(maze.player.visitedEdges.isEmpty(), true);
  }

  // Test calculateCellSize method
  void testCalculateCellSize(Tester t) {
    initData();
    t.checkExpect(maze.calculateCellSize(), Vertex.CELL_SIZE);

    Maze largeMaze = new Maze(50, 50);
    t.checkExpect(largeMaze.calculateCellSize(), Vertex.CELL_SIZE / 2);
  }

  // Test searchMaze method
  void testSearchMaze(Tester t) {
    initData();
    Vertex start = new Vertex(0, 0);
    Vertex end = new Vertex(1, 1);

    // Test breadth-first search
    ArrayList<Edge> breadthSearch = maze.searchMaze(start, end, allEdges, true, false);
    t.checkExpect(breadthSearch.size(), 4);

    // Test depth-first search
    ArrayList<Edge> depthSearch = maze.searchMaze(start, end, allEdges, false, false);
    t.checkExpect(depthSearch.size(), 4);
  }

  // Test searchMazeHelp method
  void testSearchMazeHelp(Tester t) {
    initData();
    Vertex start = new Vertex(0, 0);
    Vertex end = new Vertex(1, 1);
    HashMap<Vertex, Edge> cameFromEdge = new HashMap<>();
    cameFromEdge.put(new Vertex(1, 0), new Edge(new Vertex(0, 0), new Vertex(1, 0), 1));
    cameFromEdge.put(new Vertex(1, 1), new Edge(new Vertex(1, 0), new Vertex(1, 1), 1));

    ArrayList<Edge> result = maze.searchMazeHelp(start, cameFromEdge, end);
    t.checkExpect(result.size(), 2);
  }

  // Test scoreBreadthDepth method
  void testScoreBreadthDepth(Tester t) {
    initData();
    t.checkExpect(maze.scoreBreadthDepth(), "Breadth and Depth first search complete");
  }

  // Test the head method of Queue
  void testQueueHead(Tester t) {
    initData();

    queue.addAtHead(1);
    queue.addAtHead(2);
    queue.addAtHead(3);

    t.checkExpect(queue.head(), 3);
  }

  // Test the removeHead method of Queue
  void testQueueRemoveHead(Tester t) {
    initData();

    queue.addAtHead(1);
    queue.addAtHead(2);
    queue.addAtHead(3);

    t.checkExpect(queue.removeHead(), 3);
    t.checkExpect(queue.removeHead(), 2);
    t.checkExpect(queue.removeHead(), 1);
    t.checkException(new NullPointerException("Queue is Empty"), queue, "removeHead");
  }

  // Test the isEmpty method of Queue
  void testQueueIsEmpty(Tester t) {
    initData();

    t.checkExpect(queue.isEmpty(), true);
    queue.addAtHead(1);
    t.checkExpect(queue.isEmpty(), false);
  }

  // Test the head method of Stack
  void testStackHead(Tester t) {
    initData();

    stack.addAtHead(1);
    stack.addAtHead(2);
    stack.addAtHead(3);

    t.checkExpect(stack.head(), 3);
  }

  // Test the removeHead method of Stack
  void testStackRemoveHead(Tester t) {
    initData();

    stack.addAtHead(1);
    stack.addAtHead(2);
    stack.addAtHead(3);

    t.checkExpect(stack.removeHead(), 3);
    t.checkExpect(stack.removeHead(), 2);
    t.checkExpect(stack.removeHead(), 1);
    t.checkException(new NullPointerException("No items in list."), stack, "removeHead");
  }

  // Test the isEmpty method of Stack
  void testStackIsEmpty(Tester t) {
    initData();

    t.checkExpect(stack.isEmpty(), true);
    stack.addAtHead(1);
    t.checkExpect(stack.isEmpty(), false);
  }

  // Test the compare method of EdgeComparator
  void testEdgeComparator(Tester t) {
    initData();

    t.checkExpect(comparator.compare(edge1, edge2), -1);
    t.checkExpect(comparator.compare(edge2, edge1), 1);
    t.checkExpect(comparator.compare(edge1, edge1), 0);
  }

  public void testConcatenate(Tester t) {
    initData();
    ArrayList<Vertex> list1 = new ArrayList<>();
    list1.add(v1);
    list1.add(v2);
    ArrayList<Vertex> list2 = new ArrayList<>();
    list2.add(v3);
    list2.add(v4);
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(v1);
    expected.add(v2);
    expected.add(v3);
    expected.add(v4);
    t.checkExpect(utils.concatenate(list1, list2), expected);
  }

  // Test the quicksort method
  void testQuicksort(Tester t) {
    initData();

    ArrayList<Edge> edges = new ArrayList<>();
    edges.add(e3);
    edges.add(e2);
    edges.add(e1);
    edges.add(e4);

    utils.quicksort(edges, comp);

    t.checkExpect(edges.get(0), e1);
    t.checkExpect(edges.get(1), e2);
    t.checkExpect(edges.get(2), e3);
    t.checkExpect(edges.get(3), e4);
  }

  public void testSwap(Tester t) {
    initData();
    ArrayList<Vertex> list = new ArrayList<>();
    list.add(vertex1);
    list.add(vertex2);
    list.add(vertex3);
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(vertex3);
    expected.add(vertex2);
    expected.add(vertex1);
    utils.swap(list, 0, 2);
    t.checkExpect(list, expected);
  }

  public void testAddWithoutDuplicates(Tester t) {
    initData();
    ArrayList<Vertex> list = new ArrayList<>();
    list.add(vertex1);
    list.add(vertex2);
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(vertex1);
    expected.add(vertex2);
    t.checkExpect(utils.addWithoutDuplicates(list, vertex1), expected);
    expected.add(vertex3);
    t.checkExpect(utils.addWithoutDuplicates(list, vertex3), expected);
  }

  public void testCollectVertices(Tester t) {
    initData();
    ArrayList<Edge> edges = new ArrayList<>();
    edges.add(new Edge(v1, v2, 1));
    edges.add(new Edge(v2, v3, 1));
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(v1);
    expected.add(v2);
    expected.add(v3);
    t.checkExpect(utils.collectVertices(edges), expected);
  }

  public void testHasCycle(Tester t) {
    initData();
    HashMap<Vertex, Vertex> hash = new HashMap<>();
    hash.put(v1, v1);
    hash.put(v2, v2);
    t.checkExpect(utils.hasCycle(hash, v1, v2), false);
  }

  public void testFindRoot(Tester t) {
    initData();
    HashMap<Vertex, Vertex> hash = new HashMap<>();
    hash.put(v1, v1);
    hash.put(v2, v2);
    hash.put(v3, v1);
    t.checkExpect(utils.findRoot(hash, v3), v1);
  }

  // Test the getNeighbors method
  void testGetNeighbors(Tester t) {
    initData();

    ArrayList<Edge> edges = new ArrayList<>();
    edges.add(new Edge(v1, v2, 1));
    edges.add(new Edge(v2, v3, 1));
    edges.add(new Edge(v3, v4, 1));

    ArrayList<Vertex> v1Neighbors = utils.getNeighbors(v1, edges);
    ArrayList<Vertex> v2Neighbors = utils.getNeighbors(v2, edges);
    ArrayList<Vertex> v3Neighbors = utils.getNeighbors(v3, edges);

    t.checkExpect(v1Neighbors.size(), 1);
    t.checkExpect(v1Neighbors.contains(v2), true);

    t.checkExpect(v2Neighbors.size(), 2);
    t.checkExpect(v2Neighbors.contains(v1), true);
    t.checkExpect(v2Neighbors.contains(v3), true);

    t.checkExpect(v3Neighbors.size(), 2);
    t.checkExpect(v3Neighbors.contains(v2), true);
    t.checkExpect(v3Neighbors.contains(v4), true);
  }

  public void testReverse(Tester t) {
    initData();
    ArrayList<Vertex> list = new ArrayList<>();
    list.add(vertex1);
    list.add(vertex2);
    list.add(vertex3);
    ArrayList<Vertex> expected = new ArrayList<>();
    expected.add(vertex3);
    expected.add(vertex2);
    expected.add(vertex1);
    t.checkExpect(utils.reverse(list), expected);
  }

  public void testGetValues(Tester t) {
    initData();
    HashMap<Vertex, Integer> hash = new HashMap<>();
    hash.put(vertex1, 1);
    hash.put(vertex2, 2);
    hash.put(vertex3, 3);
    ArrayList<Vertex> keys = new ArrayList<>();
    keys.add(vertex1);
    keys.add(vertex2);
    ArrayList<Integer> expected = new ArrayList<>();
    expected.add(1);
    expected.add(2);
    t.checkExpect(utils.getValues(hash, keys), expected);
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

  void testEqualsEdge(Tester t) {
    initData();
    Edge e1Copy = new Edge(v1, v2, 1);
    Edge e1Reversed = new Edge(v2, v1, 1);

    t.checkExpect(e1.equals(e1Copy), true);
    t.checkExpect(e1.equals(e1Reversed), true);
    t.checkExpect(e1.equals(e2), false);
  }

  void testHashCodeEdge(Tester t) {
    initData();
    Edge e1Copy = new Edge(v1, v2, 1);

    t.checkExpect(e1.hashCode(), e1Copy.hashCode());
    t.checkExpect(e1.hashCode(), 1000);
    t.checkExpect(e1.hashCode() == e2.hashCode(), false);
    t.checkExpect(e2.hashCode(), 100001001);
  }
}
