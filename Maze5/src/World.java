import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.*;

import javalib.worldimages.*;

// Represents a single square of the game area
class Square {
  // booleans representing if the right and bottom walls of the square exist
  public boolean rightExists = true;
  public boolean downExists = true;

  public boolean isVisited = false;

  // In logical coordinates, with the origin in the top-left corner of the screen
  int x;
  int y;

  // the four adjacent Squares to this one
  Square left;
  Square top;
  Square right;
  Square bottom;

  // the edges to the right and the bottom of the square
  Edge rightE;
  Edge downE;
  Edge leftE;
  Edge upE;

  Square() {
  }

  Square(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // checks if two squares are the same
  public boolean equals(Object other) {
    if (!(other instanceof Square)) {
      return false;
    }
    else {
      Square that = (Square) other;
      return this.x == that.x &&
        this.y == that.y;
    }
  }

  // produces a hashcode for the square
  public int hashCode() {
    return this.x * this.y * 10000;
  }

  // draws the vertical edge of the square
  WorldImage drawVert() {
    return new RectangleImage(1, Maze.SQUARE_SIZE, OutlineMode.SOLID,
      Color.BLACK);
  }

  // draws the horizontal edge of the square
  WorldImage drawHoriz() {
    return new RectangleImage(Maze.SQUARE_SIZE, 1, OutlineMode.SOLID,
      Color.BLACK);
  }

  // returns all non null neighbors
  ArrayList<Square> allNeighbors() {
    ArrayList<Square> temp = new ArrayList<Square>();
    if (Objects.nonNull(this.left)) {
      temp.add(this.left);
    }
    if (Objects.nonNull(this.top)) {
      temp.add(this.top);
    }
    if (Objects.nonNull(this.right)) {
      temp.add(this.right);
    }
    if (Objects.nonNull(this.bottom)) {
      temp.add(this.bottom);
    }
    return temp;
  }
}

class Edge implements Comparable<Edge> {
  Square from;
  Square to;
  int weight;

  Edge(Square from, Square to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  // for convenience
  Edge(int weight) {
    this.weight = weight;
  }

  Edge(Square from, Square to) {
    this.from = from;
    this.to = to;
  }

  // checks if two edges are equal
  public boolean equals(Object other) {
    if (!(other instanceof Edge)) {
      return false;
    }
    else {
      Edge that = (Edge) other;
      return (this.from.equals(that.from) && this.to.equals(that.to))
        || (this.from.equals(that.to) && this.to.equals(that.from));
    }
  }

  // produces a hashcode for this edge
  public int hashCode() {
    return this.from.hashCode() * this.to.hashCode() * 10000;
  }

  // compares two edges by weight
  public int compareTo(Edge o) {
    return this.weight - o.weight;
  }
}




class Maze extends World {
  // All the Squares of the game
  ArrayList<Square> board;

  // the edges that form the path of the maze
  ArrayList<Edge> edgesInTree;

  // the worklist
  ArrayList<Edge> workList;

  // the board width and height
  static int BOARD_WIDTH;
  static int BOARD_HEIGHT;
  static int SQUARE_SIZE = 20;

  HashMap<Square, Square> map = new HashMap<Square, Square>();
  ArrayList<Square> searchPath = new ArrayList<Square>();
  ArrayList<Square> fullPath = new ArrayList<Square>();

  int currIndex;

  Square currSquare;

  boolean finishDrawing;


  Maze() {
    this.BOARD_WIDTH = 50;
    this.BOARD_HEIGHT = 25;
    createBoard();
  }

  Maze(int width, int height) {
    this.BOARD_WIDTH = width;
    this.BOARD_HEIGHT = height;
    createBoard();
  }

  // EFFECT: acts as a constructor for the board, creating a board and setting up ArrayLists
  public void createBoard() {
    ArrayList<Square> tempBoard = new ArrayList<Square>();
    this.edgesInTree = new ArrayList<Edge>();
    for (int i = 0; i < BOARD_HEIGHT; i++) {
      for (int j = 0; j < BOARD_WIDTH; j++) {
        tempBoard.add(new Square(j, i));
      }
    }
    this.board = new ArrayList<Square>(tempBoard);
    for (int i = 0; i < this.board.size(); i++) {
      this.fillNeighbors(this.board.get(i), i);
    }
    this.workList = this.initEdges(this.board);
    this.randomWeights(this.workList);
    this.unionFind();
    this.edgesExist(this.board);
    this.fixNeighbors(this.board);
    this.map = new HashMap<Square, Square>();
    this.searchPath = new ArrayList<Square>();
  }

  // EFFECT: sets the rightExists and downExists boolean in each square to dictate
  // if that edge should be drawn or not
  public void edgesExist(ArrayList<Square> squares) {
    for (int i = 0; i < squares.size(); i++) {
      Square curr = squares.get(i);
      if (this.edgesInTree.contains(curr.rightE)) {
        curr.rightExists = false;
      }
      if (this.edgesInTree.contains(curr.downE)) {
        curr.downExists = false;
      }
    }
  }

  // EFFECT: carries out the union/find algorithm for the maze, filling the edgesInTree array with
  // the edges that make up the path
  public void unionFind() {
    Collections.sort(workList);
    HashMap<Square, Square> maps = this.initHash();
    int i = 0;
    while (!this.isTreeComplete() && i < this.workList.size() - 1) {
      Square to = this.workList.get(i).to;
      Square from = this.workList.get(i).from;
      if (this.find(maps, to).equals(this.find(maps, from))) {
        i = i + 1;
      } else {
        this.edgesInTree.add(this.workList.remove(i));
        this.union(maps, to, from);
      }
    }
  }

  // carries out the union part of union/find
  void union(HashMap<Square, Square> maps, Square to, Square from) {
    maps.put(this.find(maps, to), this.find(maps, from));
  }

  // carries out the find part of union/find
  Square find(HashMap<Square, Square> maps, Square key) {
    Square val = maps.get(key);
    while (!maps.get(val).equals(val)) {
      val = maps.get(val);
    }
    return val;
  }


  public HashMap<Square, Square> initHash() {
    HashMap<Square, Square> temp = new HashMap<Square, Square>();
    for (int i = 0; i < this.workList.size(); i++) {
      Square to = this.workList.get(i).to;
      Square from = this.workList.get(i).from;
      temp.put(to, to);
      temp.put(from, from);
    }
    return temp;
  }

  // checks if the edgesInTree array is full
  public boolean isTreeComplete() {
    return (BOARD_HEIGHT * BOARD_WIDTH) - 1 == this.edgesInTree.size();
  }

  // assigns random weights to every edge
  public void randomWeights(ArrayList<Edge> edges) {
    for (int i = 0; i < edges.size(); i++) {
      edges.get(i).weight = new Random().nextInt(100);
    }
  }

  // adds edges to squares and returns an array of all added edges
  public ArrayList<Edge> initEdges(ArrayList<Square> squares) {
    ArrayList<Edge> arr = new ArrayList<Edge>();
    for (int i = 0; i < squares.size(); i++) {
      Square curr = squares.get(i);
      if (curr.x < BOARD_WIDTH - 1) {
        curr.rightE = new Edge(curr, curr.right);
        arr.add(curr.rightE);
      }
      if (curr.y < BOARD_HEIGHT - 1) {
        curr.downE = new Edge(curr, curr.bottom);
        arr.add(curr.downE);
      }
      if (curr.y > 0) {
        curr.upE = new Edge(curr, curr.top);
      }
      if (curr.x > 0) {
        curr.leftE = new Edge(curr, curr.left);
      }
    }
    return arr;
  }

  // EFFECT: fills the neighboring Square values for each Square
  public void fillNeighbors(Square s, int index) {
    if (s.y > 0) {
      s.top = this.board.get(index - BOARD_WIDTH);
    }
    if (s.x > 0) {
      s.left = this.board.get(index - 1);
    }
    if (s.y < BOARD_HEIGHT - 1) {
      s.bottom = this.board.get(index + BOARD_WIDTH);
    }
    if (s.x < BOARD_WIDTH - 1) {
      s.right = this.board.get(index + 1);
    }
  }

  // EFFECT: fixes the neighbors of the squares
  public void fixNeighbors(ArrayList<Square> squares) {
    for (int i = 0; i < squares.size(); i++) {
      Square curr = squares.get(i);
      if (this.workList.contains(curr.leftE)) {
        curr.left = null;
      }
      if (this.workList.contains(curr.rightE)) {
        curr.right = null;
      }
      if (this.workList.contains(curr.upE)) {
        curr.top = null;
      }
      if (this.workList.contains(curr.downE)) {
        curr.bottom = null;
      }
    }
  }

  // makes the scene
  public WorldScene makeScene() {
    WorldScene bg = new WorldScene(BOARD_WIDTH * SQUARE_SIZE, BOARD_HEIGHT * SQUARE_SIZE);
    WorldImage endPoint = new RectangleImage(SQUARE_SIZE, SQUARE_SIZE,
        OutlineMode.SOLID, Color.MAGENTA);
    WorldImage startPoint =
        new RectangleImage(SQUARE_SIZE,SQUARE_SIZE,OutlineMode.SOLID, Color.GREEN);
    Square endSquare = this.board.get(this.board.size() - 1);
    // draws the endpoint
    bg.placeImageXY(endPoint, (endSquare.x + 1) * SQUARE_SIZE
        - SQUARE_SIZE / 2, (endSquare.y + 1) * SQUARE_SIZE - SQUARE_SIZE / 2);
    // draws the starting point
    bg.placeImageXY(startPoint, SQUARE_SIZE / 2, SQUARE_SIZE / 2);


    // draws the search
    if (!this.finishDrawing && this.currIndex < this.fullPath.size()) {
      WorldImage path = new RectangleImage(SQUARE_SIZE, SQUARE_SIZE,
          OutlineMode.SOLID, Color.CYAN);
      bg.placeImageXY(path, (this.currSquare.x + 1) * SQUARE_SIZE - SQUARE_SIZE / 2,
          (this.currSquare.y + 1) * SQUARE_SIZE - SQUARE_SIZE / 2);
    }

    if (this.finishDrawing) {
      for (Square s : this.board) {
        Color col = Color.cyan;
        if (s.isVisited) {
          WorldImage searchPath = new RectangleImage(SQUARE_SIZE, SQUARE_SIZE,
              OutlineMode.SOLID, col.brighter());
          bg.placeImageXY(searchPath, (s.x + 1) * SQUARE_SIZE - SQUARE_SIZE / 2,
              (s.y + 1) * SQUARE_SIZE - SQUARE_SIZE / 2);
        }
      }
    }

    // draws the search's path
    for (Square s : this.searchPath) {
      Color col;
      if (this.finishDrawing) {
        col = Color.MAGENTA;
      } else {
        col = Color.CYAN;
      }
      WorldImage searchPath = new RectangleImage(SQUARE_SIZE, SQUARE_SIZE,
          OutlineMode.SOLID, col.brighter());
      bg.placeImageXY(searchPath, (s.x + 1) * SQUARE_SIZE - SQUARE_SIZE / 2,
          (s.y + 1) * SQUARE_SIZE - SQUARE_SIZE / 2);
    }

    // draws the walls
    for (Square s: this.board) {
      if (s.rightExists) {
        bg.placeImageXY(s.drawVert(),
            (s.x + 1) * SQUARE_SIZE, (s.y + 1) * SQUARE_SIZE - SQUARE_SIZE / 2);
      }
      if (s.downExists) {
        bg.placeImageXY(s.drawHoriz(), (s.x + 1) * SQUARE_SIZE
            - SQUARE_SIZE / 2, (s.y + 1) * SQUARE_SIZE);
      }
    }
    return bg;
  }

  // EFFECT: moves the player/changes maps/performs search or DFS according to
  // the given key
  public void onKeyEvent(String key) {

    // to restart the game
    if (key.equals("r")) {
      this.createBoard();
    }
    // to perform bfs
    else if (key.equals("b")) {
      this.finishDrawing = false;
      this.resetVisited();
      this.fullPath = new ArrayList<Square>();
      this.searchPath = new ArrayList<Square>();
      this.map = this.depthSearch();
      ArrayList<Square> temp = new ArrayList<Square>(this.fullPath);
      this.fullPath = new ArrayList<Square>();
      this.searchPath = new ArrayList<Square>();
      this.resetVisited();
      this.breadthSearch();
      this.currSquare = this.board.get(0);
      this.currIndex = 0;
    }
    // to perform dfs
    else if (key.equals("d")) {
      this.finishDrawing = false;
      this.resetVisited();
      this.fullPath = new ArrayList<Square>();
      this.searchPath = new ArrayList<Square>();
      this.map = this.depthSearch();
      this.currSquare = this.board.get(0);
      this.currIndex = 0;
    }
  }

  // EFFECT: animates the bfs/dfs search
  public void onTick() {
    if (this.currIndex < this.fullPath.size()) {
      this.searchPath.add(this.fullPath.get(this.currIndex));
      this.currIndex += 1;
    } else if (!this.finishDrawing) {
      this.finishDrawing = true;
      this.searchPath = new ArrayList<Square>();
      this.currSquare = this.board.get(0);
    }
    if (!this.map.isEmpty() && this.finishDrawing) {
      this.searchPath.add(this.currSquare);
      this.currSquare = this.map.get(this.currSquare);
    }

  }

  // performs search or dfs on this maze depending on whether a queue or a stack
  // is given, respectively
  public HashMap<Square, Square> depthSearch() {
    Stack<Square> worklist = new Stack<Square>();
    HashMap<Square, Square> cameFromEdge = new HashMap<Square, Square>();
    worklist.add(this.board.get(0));
    while (!worklist.isEmpty()) {
      Square curr = worklist.pop();
      if (!curr.isVisited) {
        if (curr.x == BOARD_WIDTH - 1 && curr.y == BOARD_HEIGHT - 1) {
          return cameFromEdge;
        }
        else {
          curr.isVisited = true;
          this.fullPath.add(curr);
          for (Square s : curr.allNeighbors()) {

            worklist.add(s);
            cameFromEdge.put(s, curr);
          }
        }
      }
    }
    return cameFromEdge;
  }

  // performs search or dfs on this maze depending on whether a queue or a stack
  // is given, respectively
  public HashMap<Square, Square> breadthSearch() {
    LinkedList<Square> worklist = new LinkedList<Square>();
    HashMap<Square, Square> cameFromEdge = new HashMap<Square, Square>();
    worklist.addLast(this.board.get(0));
    while (!worklist.isEmpty()) {
      Square curr = worklist.poll();
      if (!curr.isVisited) {
        if (curr.x == BOARD_WIDTH - 1 && curr.y == BOARD_HEIGHT - 1) {
          return cameFromEdge;
        }
        else {
          curr.isVisited = true;
          this.fullPath.add(curr);
          for (Square s : curr.allNeighbors()) {
            worklist.addLast(s);
            cameFromEdge.put(s, curr);
          }
        }
      }
    }
    return cameFromEdge;
  }

  public void resetVisited() {
    for (Square s: this.board) {
      s.isVisited = false;
    }
  }
}

class ExamplesGame {
  void testGame(Tester t) {
    Maze g = new Maze();
    g.bigBang(g.BOARD_WIDTH * g.SQUARE_SIZE, g.BOARD_HEIGHT * g.SQUARE_SIZE, .01);
  }

  Maze world = new Maze();
  ArrayList<Edge> edges = new ArrayList<Edge>();
  HashMap<Square, Square> map = new HashMap<Square, Square>();

  Square s00 = new Square(0, 0);
  Square s10 = new Square(1, 0);
  Square s20 = new Square(2, 0);
  Square s01 = new Square(0, 1);
  Square s11 = new Square(1, 1);
  Square s21 = new Square(2, 1);

  Edge s00s10 = new Edge(s00, s10, 5);
  Edge s10s20 = new Edge(s10, s20, 3);
  Edge s01s11 = new Edge(s01, s11, 10);
  Edge s11s21 = new Edge(s11, s21, 12);
  Edge s00s01 = new Edge(s00, s01, 15);
  Edge s10s11 = new Edge(s10, s11, 20);
  Edge s20s21 = new Edge(s20, s21, 4);

  ArrayList<Edge> arrEdge1 = new ArrayList<Edge>();

  ArrayList<ArrayList<Square>> arrSquare = new ArrayList<ArrayList<Square>>();

  ArrayList<Edge> mtEdge = new ArrayList<Edge>();
  ArrayList<Edge> loe1 = new ArrayList<Edge>(Arrays.asList(s00s10, s10s20));

  void initData() {
    world = new Maze();
    s00 = new Square(0, 0);
    s10 = new Square(1, 0);
    s20 = new Square(2, 0);
    s01 = new Square(0, 1);
    s11 = new Square(1, 1);
    s21 = new Square(2, 1);
    this.map = new HashMap<Square, Square>();
    map.put(s00, s00);
    map.put(s10, s00);
    map.put(s20, s10);
    map.put(s11, s21);
    map.put(s21, s21);
    this.arrSquare = new ArrayList<ArrayList<Square>>();
    arrSquare.add(0, new ArrayList<Square>());
    arrSquare.add(1, new ArrayList<Square>());
    arrSquare.add(2, new ArrayList<Square>());
    arrSquare.get(0).add(s00);
    arrSquare.get(0).add(s01);
    arrSquare.get(1).add(s10);
    arrSquare.get(1).add(s11);
    arrSquare.get(2).add(s20);
    arrSquare.get(2).add(s21);
    s00s10 = new Edge(s00, s10, 5);
    s10s20 = new Edge(s10, s20, 3);
    s01s11 = new Edge(s01, s11, 10);
    s11s21 = new Edge(s11, s21, 12);
    s00s01 = new Edge(s00, s01, 15);
    s10s11 = new Edge(s10, s11, 20);
    s20s21 = new Edge(s20, s21, 4);
    this.arrEdge1 = new ArrayList<Edge>();
  }

  void testSquareEquals(Tester t) {
    Square s1 = new Square(1, 1);
    Square s2 = new Square(2, 1);
    Object s3 = new Square(1, 1);
    t.checkExpect(s1.equals(s3), true);
    t.checkExpect(s1.equals(s1), true);
    t.checkExpect(s1.equals(s2), false);
    t.checkExpect(s2.equals(s3), false);
  }

  void testSquareHashCode(Tester t) {
    Square s1 = new Square(1, 1);
    Square s2 = new Square(1, 1);
    Square s3 = new Square(2, 1);
    t.checkExpect(s1.hashCode() == s2.hashCode(), true);
    t.checkExpect(s1.hashCode() == s3.hashCode(), false);
  }

  void testDrawVert(Tester t) {
    Square s1 = new Square();
    t.checkExpect(s1.drawVert(),
        new RectangleImage(1, 20, OutlineMode.SOLID, Color.BLACK));
  }

  void testDrawHoriz(Tester t) {
    Square s1 = new Square();
    t.checkExpect(s1.drawHoriz(),
        new RectangleImage(20, 1, OutlineMode.SOLID, Color.BLACK));
  }

  void testEdgeEquals(Tester t) {
    Square s1 = new Square(1, 1);
    Square s2 = new Square(1, 1);
    Square s3 = new Square(1, 2);
    Edge e1 = new Edge(s1, s2);
    Edge e2 = new Edge(s2, s1);
    Edge e3 = new Edge(s3, s2);
    Object e4 = new Edge(s2, s3);
    t.checkExpect(e1.equals(e2), true);
    t.checkExpect(e3.equals(e4), true);
    t.checkExpect(e1.equals(e3), false);
    t.checkExpect(e1.equals(e1), true);
    t.checkExpect(e1.equals(e4), false);
  }

  void testEdgeHashCode(Tester t) {
    Square s1 = new Square(1, 1);
    Square s2 = new Square(1, 1);
    Square s3 = new Square(1, 2);
    Edge e1 = new Edge(s1, s2);
    Edge e2 = new Edge(s2, s1);
    Edge e3 = new Edge(s3, s2);
    t.checkExpect(e1.hashCode() == e2.hashCode(), true);
    t.checkExpect(e1.hashCode() == e3.hashCode(), false);
  }

  void testCompareTo(Tester t) {
    Edge e1 = new Edge(10);
    Edge e2 = new Edge(5);
    t.checkExpect(e1.compareTo(e2), 5);
    t.checkExpect(e1.compareTo(e1), 0);
    t.checkExpect(e2.compareTo(e1), -5);
  }

  void testUnionFind(Tester t) {
    this.initData();
    world.createBoard();
    t.checkExpect(this.world.edgesInTree.size(), Maze.BOARD_HEIGHT *
        Maze.BOARD_WIDTH - 1);
    t.checkExpect(this.world.workList.size() < this.world.edgesInTree.size(), true);
    t.checkExpect(this.world.workList.contains(this.world.edgesInTree.get(0)), false);
  }

  void testIsTreeComplete(Tester t) {
    this.initData();
    world.edgesInTree = new ArrayList<Edge>();
    t.checkExpect(world.isTreeComplete(), false);
    world.createBoard();
    t.checkExpect(world.isTreeComplete(), true);
  }

  void testInitHash(Tester t) {
    this.initData();
    ArrayList<Edge> arr = new ArrayList<Edge>();
    arr.add(s00s01);
    arr.add(s01s11);
    arr.add(s11s21);
    world.workList = arr;
    HashMap<Square, Square> map = world.initHash();
    t.checkExpect(map.get(world.workList.get(0).from), s00);
    t.checkExpect(map.get(world.workList.get(0).to), s01);
  }

  void testUnion(Tester t) {
    this.initData();
    this.world.union(map, s11, s20);
    t.checkExpect(map.get(s21), s00);
  }

  void testFind(Tester t) {
    this.initData();
    t.checkExpect(this.world.find(map, s20), s00);
    t.checkExpect(this.world.find(map, s00), s00);
    t.checkExpect(this.world.find(map, s11), s21);
  }

  void testRandomWeights(Tester t) {
    this.initData();
    world.createBoard();
    t.checkRange(world.workList.get(0).weight, 0, 99);
    t.checkRange(world.workList.get(5).weight, 0, 99);
  }

  void testInitEdges(Tester t) {
    this.initData();
    world.initEdges(this.world.board);
    t.checkExpect(s00.downE, new Edge(s00, s01));
    t.checkExpect(s00.rightE, new Edge(s00, s10));
    t.checkExpect(s20.rightE, null);
  }



  void testOnKey(Tester t) {
    Maze resetMaze = new Maze();
    resetMaze.onTick();
    resetMaze.onKeyEvent("b");
    t.checkExpect(resetMaze.finishDrawing, false);
    t.checkExpect(resetMaze.fullPath, new ArrayList<Square>());
    t.checkExpect(resetMaze.searchPath, new ArrayList<Square>());
    resetMaze = new Maze();
    resetMaze.onKeyEvent("d");
    t.checkExpect(resetMaze.finishDrawing, false);
    t.checkExpect(resetMaze.fullPath, new ArrayList<Square>());
    t.checkExpect(resetMaze.searchPath, new ArrayList<Square>());
    Maze temp = resetMaze;
    resetMaze = new Maze();
    resetMaze.onKeyEvent("r");
    t.checkExpect(resetMaze.equals(temp), false);
  }

  void testResetVisited(Tester t) {
    Maze resetMaze = new Maze();
    resetMaze.onKeyEvent("b");
    t.checkExpect(resetMaze.board.get(0).isVisited, true);
    resetMaze.resetVisited();
    t.checkExpect(resetMaze.board.get(0).isVisited, false);
  }




}