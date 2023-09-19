import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// Class to represnt an edge in the maze
class Edge implements Comparable<Edge> {
  int edgeWeight;
  Cell first;
  Cell second;
  Random random = new Random();

  // constructor
  Edge(Cell first, Cell second) {
    this.first = first;
    this.second = second;
    this.edgeWeight = random.nextInt(100);
  }

  // Compares the edge weights of two edges
  public int compareTo(Edge other) {
    return this.edgeWeight - other.edgeWeight;
  }

  // Draws the edge56
  public WorldImage drawEdge() {
    if (this.verticalEdge()) {
      return new LineImage(new Posn(0, 20), Color.black).movePinhole(0, -30);
    }
    else {
      return new LineImage(new Posn(20, 0), Color.black).movePinhole(-30, 0);
    }
  }

  // Returns true if the edge is vertical
  boolean verticalEdge() {
    return this.first.x == this.second.x;
  }
}

// Class to represent a cell in the maze
class Cell {
  int x;
  int y;
  boolean wasVisited;
  Color col;
  boolean staticTile = false;
  int tilesize = 20;

  // constructor
  Cell(int x, int y) {
    this.x = x;
    this.y = y;
    this.col = Color.white;
  }

  // Sets makeStatic to true
  void setStatic() {
    this.staticTile = true;
  }

  // Visits the cell, sets the color to cyan and wasVisited to true
  void visit() {
    if (!staticTile) {
      this.wasVisited = true;
      this.col = Color.cyan;
    }
  }

  // Unvisits the cell, sets the color back to white and wasVisited to false
  void unvisit() {
    this.wasVisited = false;
    this.col = Color.white;
  }

  // Draws the cell
  WorldImage drawCell() {
    return new RectangleImage(tilesize, tilesize, "solid", this.col);
  }
}

// Class to represent the maze
class Maze extends World {
  int sizex;
  int sizey;
  ArrayList<Cell> cellRow;
  ArrayList<ArrayList<Cell>> cellCol;
  ArrayList<Edge> edgeList;
  Map<Cell, Cell> cellMap;

  // constructor
  Maze(int sizex, int sizey) {
    this.sizex = sizex;
    this.sizey = sizey;
    cellRow = new ArrayList<Cell>();
    cellCol = new ArrayList<ArrayList<Cell>>();
    edgeList = new ArrayList<Edge>();
    cellMap = new HashMap<>();
    buildMaze();
  }

  // Builds the maze and applies Kruskal's algorithm
  void buildMaze() {
    for (int i = 0; i < this.sizex; i++) {
      cellRow = new ArrayList<Cell>();
      for (int j = 0; j < this.sizey; j++) {
        cellRow.add(new Cell(i, j));
        cellMap.put(cellRow.get(j), cellRow.get(j));
      }
      cellCol.add(cellRow);
    }
    this.cellCol.get(0).get(0).setStatic();
    this.cellCol.get(0).get(0).col = Color.green;
    this.cellCol.get(sizex - 1).get(sizey - 1).setStatic();
    this.cellCol.get(sizex - 1).get(sizey - 1).col = Color.red;

    for (int i = 0; i < this.sizex; i++) {
      for (int j = 0; j < this.sizey; j++) {
        if (i < this.sizex - 1) {
          this.edgeList.add(new Edge(cellCol.get(i).get(j), cellCol.get(i + 1).get(j)));
        }
        if (j < this.sizey - 1) {
          this.edgeList.add(new Edge(cellCol.get(i).get(j), cellCol.get(i).get(j + 1)));
        }
      }
    }
    applyKruskal();
  }

  // helps build the maze and draws the edges using Kruskal's algorithm
  void applyKruskal() {
    int n = this.edgeList.size();
    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n - i - 1; j++) {
        if (this.edgeList.get(j).compareTo(this.edgeList.get(j + 1)) > 0) {
          Edge temp = this.edgeList.get(j);
          this.edgeList.set(j, this.edgeList.get(j + 1));
          this.edgeList.set(j + 1, temp);
        }
      }
    }

    int treeEdges = 0;
    int index = 0;
    while (treeEdges < (this.sizex * this.sizey) - 1
        && index < (this.sizex * this.sizey) - 1) {
      if (!isCycle(this.edgeList.get(index))) {
        union(this.edgeList.get(index).first, this.edgeList.get(index).second);
        this.edgeList.remove(index);
        treeEdges++;
      } else {
        index++;
      }
    }
  }

  // Returns true if the edge creates a cycle
  boolean isCycle(Edge edge) {
    return find(edge.first).equals(find(edge.second));
  }

  // Finds the root of the cell
  Cell find(Cell cell) {
    if (this.cellMap.get(cell).equals(cell)) {
      return cell;
    }
    else {
      return find(this.cellMap.get(cell));
    }
  }

  // Unions the two cells
  void union(Cell cell1, Cell cell2) {
    Cell root1 = find(cell1);
    Cell root2 = find(cell2);

    if (!root1.equals(root2)) {
      cellMap.put(root1, root2);
    }
  }

  // draws the tiles within the maze
  WorldImage drawTiles() {
    WorldImage allTiles = new EmptyImage();
    for (int i = 0; i < this.sizex; i++) {
      WorldImage thisRow = new EmptyImage();
      for (int j = 0; j < this.sizey; j++) {
        thisRow = new AboveImage(thisRow, cellCol.get(i).get(j).drawCell());
      }
      allTiles = new BesideImage(allTiles, thisRow);
    }
    return allTiles;
  }

  // draws the edges within the maze
  void drawEdges(WorldScene scene) {
    for (int i = 0; i < this.edgeList.size(); i++) {
      scene.placeImageXY(this.edgeList.get(i).drawEdge(), this.edgeList.get(i).first.x * 20,
          this.edgeList.get(i).first.y * 20);
    }
  }

  // handles the mouse clicks
  public void onMousePressed(Posn pos) {
    this.whichCell(pos).visit();
  }

  // returns the cell which was clicked
  public Cell whichCell(Posn pos) {
    int x = (pos.x - 5) / 20;
    int y = (pos.y - 5) / 20;

    for (ArrayList<Cell> a : this.cellCol) {
      for (Cell c : a) {
        if (c.x == x && c.y == y) {
          return c;
        }
      }
    }
    return null;
  }

  // Method to find the solution path using Breadth-First Search
  public ArrayList<Cell> bfsSolution() {
    Queue<ArrayList<Cell>> queue = new LinkedList<>();
    Cell startCell = this.cellCol.get(0).get(0);
    Cell endCell = this.cellCol.get(sizex - 1).get(sizey - 1);

    ArrayList<Cell> startPath = new ArrayList<>();
    startPath.add(startCell);
    queue.add(startPath);

    while (!queue.isEmpty()) {
      ArrayList<Cell> currentPath = queue.remove();
      Cell currentCell = currentPath.get(currentPath.size() - 1);

      if (currentCell.equals(endCell)) {
        return currentPath;
      }

      for (Edge edge : this.edgeList) {
        if (edge.first.equals(currentCell) || edge.second.equals(currentCell)) {
          Cell neighbor = (edge.first.equals(currentCell)) ? edge.second : edge.first;

          if (!currentPath.contains(neighbor)) {
            ArrayList<Cell> newPath = new ArrayList<>(currentPath);
            newPath.add(neighbor);
            queue.add(newPath);
          }
        }
      }
    }

    return new ArrayList<>(); // Return an empty list if no solution is found
  }

  // Method to find the solution path using Depth-First Search
  public ArrayList<Cell> dfsSolution(Cell currentCell, ArrayList<Cell> path) {
    Cell endCell = this.cellCol.get(sizex - 1).get(sizey - 1);

    if (currentCell.equals(endCell)) {
      path.add(currentCell);
      return path;
    }

    path.add(currentCell);

    for (Edge edge : this.edgeList) {
      if (edge.first.equals(currentCell) || edge.second.equals(currentCell)) {
        Cell neighbor = (edge.first.equals(currentCell)) ? edge.second : edge.first;

        if (!path.contains(neighbor)) {
          ArrayList<Cell> result = dfsSolution(neighbor, new ArrayList<>(path));

          if (!result.isEmpty()) {
            return result;
          }
        }
      }
    }

    return new ArrayList<>(); // Return an empty list if no solution is found
  }

  // Method to draw the solution path on the scene
  public void drawSolution(WorldScene scene, ArrayList<Cell> solutionPath) {
    for (Cell cell : solutionPath) {
      if (!cell.staticTile) {
        scene.placeImageXY(new CircleImage(5, "solid", Color.blue), cell.x * 20, cell.y * 20);
      }
    }
  }

  // Update the makeScene method to include drawing the solution path
  public WorldScene makeScene() {
    WorldScene finalScene = new WorldScene(this.sizex * 20, this.sizey * 20);

    finalScene.placeImageXY(this.drawTiles(), this.sizex * 10, this.sizey * 10);
    this.drawEdges(finalScene);

    // Draw the solution path using BFS or DFS (choose one)
    ArrayList<Cell> solutionPath = bfsSolution(); 
    drawSolution(finalScene, solutionPath);

    return finalScene;
  }

  // Method to start the game and show the solution path
  public void startGame(int sizeX, int sizeY, boolean showSolution) {
    if (showSolution) {
      ArrayList<Cell> solutionPath = bfsSolution(); 
      WorldScene finalScene = this.makeScene();
      drawSolution(finalScene, solutionPath);
      this.bigBang(sizeX * 20, sizeY * 20, 0.1);
    }
    else {
      this.bigBang(sizeX * 20, sizeY * 20, 0.1);
    }
  }
}

// Class to represent examples and tests for the maze
class ExamplesMaze {
  // Maze used in game
  Maze mazeG = new Maze(10, 10);

  // UNCOMMENT THIS METHOD TO START / PLAY THE GAME
  // void testStart(Tester t) {
  //     this.maze1.startGame(30, 10, true); 
  //     // Set the third parameter to true to show the solution path
  // }

  // Used for tests, checks if the image exists at the given position
  boolean imageExistsAtPosn(WorldScene scene, Posn pos, WorldImage image) {
    try {
      scene.placeImageXY(image, pos.x, pos.y);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  // Examples of data for testing
  Maze maze1;
  Maze maze2;
  Cell cell1;
  Cell cell2;
  Cell cell3;
  Cell cell4;
  Edge edge1;
  Edge edge2;
  Edge edge3;

  // Initializes the data for testing
  void initData() {
    maze1 = new Maze(10, 10);
    maze2 = new Maze(5, 5);
    cell1 = new Cell(0, 0);
    cell2 = new Cell(1, 0);
    cell3 = new Cell(0, 1);
    cell4 = new Cell(2, 2);
    edge1 = new Edge(cell1, cell2);
    edge2 = new Edge(cell1, cell3);
    edge3 = new Edge(cell3, cell3);
  }

  // ALL TESTS FOR EDGE CLASS
  
  // Tests the compareTo method
  void testCompareTo(Tester t) {
    initData();
    t.checkExpect(edge1.compareTo(edge1), 0);
    t.checkExpect(edge2.compareTo(edge2), 0);
    t.checkExpect(edge1.compareTo(edge2), edge1.edgeWeight - edge2.edgeWeight);
  }

  // Tests the drawEdge method
  void testDrawEdge(Tester t) {
    initData();
    t.checkExpect(edge1.drawEdge(),
        new LineImage(new Posn(20, 0), Color.black).movePinhole(-30, 0));
    t.checkExpect(edge2.drawEdge(),
        new LineImage(new Posn(0, 20), Color.black).movePinhole(0, -30));
  }

  // Tests the verticalEdge method
  void testVerticalEdge(Tester t) {
    initData();
    t.checkExpect(edge1.verticalEdge(), false);
    t.checkExpect(edge2.verticalEdge(), true);
  }
  
  // ALL TESTS FOR CELL CLASS

  // Tests for the setStatic method
  void testSetStatic(Tester t) {
    initData();
    cell1.setStatic();
    t.checkExpect(cell1.staticTile, true);
    t.checkExpect(cell2.staticTile, false);
    cell2.setStatic();
    t.checkExpect(cell2.staticTile, true);
  }

  // Tests for the testVisit method
  void testVisit(Tester t) {
    initData();
    cell1.visit();
    t.checkExpect(cell1.wasVisited, true);
    t.checkExpect(cell1.col, Color.cyan);
    cell2.visit();
    t.checkExpect(cell2.wasVisited, true);
    t.checkExpect(cell2.col, Color.cyan);
  }

  // Tests for the testUnvisit method
  void testUnvisit(Tester t) {
    initData();
    cell1.visit();
    cell1.unvisit();
    t.checkExpect(cell1.wasVisited, false);
    t.checkExpect(cell1.col, Color.white);
    cell2.visit();
    cell2.unvisit();
    t.checkExpect(cell2.wasVisited, false);
    t.checkExpect(cell2.col, Color.white);
  }

  // Tests for the testDrawCell method
  void testDrawCell(Tester t) {
    initData();
    t.checkExpect(cell2.drawCell(), new RectangleImage(20, 20, "solid", Color.white));
    t.checkExpect(cell1.drawCell(), new RectangleImage(20, 20, "solid", Color.white));
  }
  
  // ALL TESTS FOR MAZE CLASS

  // Tests for the buildMaze method
  // Build maze always calls ApplyKruskal so its effects are always shown
  // in the results of buildMaze
  void testBuildMaze(Tester t) {
    initData();
    t.checkExpect(maze1.cellCol.size(), 10);
    t.checkExpect(maze1.cellCol.get(0).size(), 10);
    t.checkExpect(maze2.cellCol.size(), 5);
    t.checkExpect(maze2.cellCol.get(0).size(), 5);
  }
  
  // Tests for the isCycle method
  void testIsCycle(Tester t) {
    initData();
    maze1.buildMaze();
    t.checkExpect(maze1.isCycle(new Edge(maze1.cellCol.get(0).get(0), 
       maze1.cellCol.get(1).get(0))), true);
    t.checkExpect(maze1.isCycle(new Edge(maze1.cellCol.get(0).get(0), 
       maze1.cellCol.get(0).get(1))), true);
    t.checkExpect(maze1.isCycle(edge1), true);
    t.checkExpect(maze1.isCycle(edge2), false);
    maze1.union(cell1, cell3);
    t.checkExpect(maze1.isCycle(edge2), true);
  }
  
  // Tests for the find method
  void testFind(Tester t) {
    initData();
    maze1.buildMaze();
    t.checkExpect(maze1.find(cell1), cell1);
    t.checkExpect(maze1.find(cell2), cell2);
    maze1.union(cell1, cell2);
    t.checkExpect(maze1.find(cell2), cell1);
    t.checkExpect(maze1.find(cell1), cell2);
    t.checkExpect(maze1.find(cell2), cell2);
    t.checkExpect(maze1.find(cell3), cell3);
    t.checkExpect(maze1.find(cell4), cell4);
  }
  
  // Tests for the union method
  void testUnion(Tester t) {
    initData();
    t.checkExpect(maze1.find(cell2), cell2);
    maze1.union(cell1, cell2);
    t.checkExpect(maze1.find(cell2), cell1);
    maze1.union(cell2, cell4);
    t.checkExpect(maze1.find(cell1), cell4);
    t.checkExpect(maze1.find(cell2), cell4);
    t.checkExpect(maze1.find(cell3), cell4);
    t.checkExpect(maze1.find(cell4), cell4);
    
    maze1.union(maze1.cellCol.get(0).get(0), maze1.cellCol.get(1).get(0));
    t.checkExpect(maze1.cellMap.get(maze1.cellCol.get(0).get(0)), maze1.cellCol.get(1).get(0));
    maze1.union(maze1.cellCol.get(0).get(0), maze1.cellCol.get(0).get(1));
    t.checkExpect(maze1.cellMap.get(maze1.cellCol.get(0).get(0)), maze1.cellCol.get(0).get(1));
  }

  // Tests for the drawTiles method
  void testDrawTiles(Tester t) {
    initData();
    WorldImage allTiles = new EmptyImage();
    for (int i = 0; i < maze1.sizex; i++) {
      WorldImage thisRow = new EmptyImage();
      for (int j = 0; j < maze1.sizey; j++) {
        thisRow = new AboveImage(thisRow, maze1.cellCol.get(i).get(j).drawCell());
      }
      allTiles = new BesideImage(allTiles, thisRow);
    }
    t.checkExpect(maze1.drawTiles(), allTiles);
  }
  
  // Tests for the drawEdges method
  void testDrawEdges(Tester t) {
    initData();
    WorldImage allEdges = new EmptyImage();
    WorldScene emptyScene = new WorldScene(10, 10);
    for (int i = 0; i < maze1.sizex; i++) {
      WorldImage thisRow = new EmptyImage();
      for (int j = 0; j < maze1.sizey; j++) {
        thisRow = new AboveImage(thisRow, maze1.cellCol.get(i).get(j).drawCell());
      }
      allEdges = new BesideImage(allEdges, thisRow);
    }
    maze1.drawEdges(emptyScene);
    t.checkExpect(emptyScene, allEdges);

  }
  
  // Tests for the onMousePressed method
  void testOnMousePressed(Tester t) {
    initData();
    t.checkExpect(maze1.cellCol.get(0).get(0).col, Color.white);
    t.checkExpect(maze1.cellCol.get(2).get(2).col, Color.white);
    maze1.onMousePressed(new Posn(10, 10));
    maze1.onMousePressed(new Posn(50, 50));
    t.checkExpect(maze1.cellCol.get(0).get(0).col, Color.cyan);
    t.checkExpect(maze1.cellCol.get(2).get(2).col, Color.cyan);
  }
  
  // Tests for the whichCell method
  void testWhichCell(Tester t) {
    initData();
    t.checkExpect(maze1.whichCell(new Posn(10, 10)), maze1.cellCol.get(0).get(0));
    t.checkExpect(maze1.whichCell(new Posn(50, 50)), maze1.cellCol.get(2).get(2));
    t.checkExpect(maze2.whichCell(new Posn(5, 5)), maze2.cellCol.get(0).get(0));
    t.checkExpect(maze2.whichCell(new Posn(25, 5)), maze2.cellCol.get(1).get(0));
    t.checkExpect(maze2.whichCell(new Posn(5, 25)), maze2.cellCol.get(0).get(1));
    t.checkExpect(maze2.whichCell(new Posn(45, 45)), maze2.cellCol.get(2).get(2));
  }

  // FOLLOWING THREE ARE NOT NEEDE FOR PT.1
  // void testBfsSolution(Tester t) {
  //   initData();
  //   ArrayList<Cell> solutionPath = maze1.bfsSolution();
  //   t.checkExpect(solutionPath.get(0), maze1.cellCol.get(0).get(0));
  //   t.checkExpect(solutionPath.get(solutionPath.size() - 1), maze1.cellCol.get(9).get(9));
  //   }

  //   void testDfsSolution(Tester t) {
  //   initData();
  //     ArrayList<Cell> solutionPath = maze1.dfsSolution(maze1.cellCol.get(0).get(0), 
  //         new ArrayList<>());
  //   t.checkExpect(solutionPath.get(0), maze1.cellCol.get(0).get(0));
  //   t.checkExpect(solutionPath.get(solutionPath.size() - 1), maze1.cellCol.get(9).get(9));
  //   }

  //   void testDrawSolution(Tester t) {
  //   initData();
  //   WorldScene scene = new WorldScene(maze1.sizex * 20, maze1.sizey * 20);
  //   ArrayList<Cell> solutionPath = maze1.bfsSolution();
  //   maze1.drawSolution(scene, solutionPath);
  //   for (Cell cell : solutionPath) {
  //   if (!cell.staticTile) {
  //     t.checkExpect(imageExistsAtPosn(scene, new Posn(cell.x * 20, cell.y * 20), 
  //                   new CircleImage(5, "solid", Color.blue)), true);
  //   }
  //   }
  //   }

  // Tests for the makeScene method
  void testMakeScene(Tester t) {
    initData();
    WorldScene scene = maze1.makeScene();
    for (int i = 0; i < maze1.sizex; i++) {
      for (int j = 0; j < maze1.sizey; j++) {
        Cell currentCell = maze1.cellCol.get(i).get(j);
        t.checkExpect(imageExistsAtPosn(scene, new Posn(currentCell.x * 20, currentCell.y * 20),
            currentCell.drawCell()), true);
      }
    }
  }

  // Continue testing the makeScene method
  void testMakeSceneCells(Tester t) {
    initData();
    WorldScene scene = maze1.makeScene();

    for (int i = 0; i < maze1.sizex; i++) {
      for (int j = 0; j < maze1.sizey; j++) {
        Cell cell = maze1.cellCol.get(i).get(j);
        t.checkExpect(imageExistsAtPosn(scene, new Posn(cell.x * 20, cell.y * 20), cell.drawCell()),
            true);
      }
    }
  }

  // Continue testing the makeScene method
  void testMakeSceneEdges(Tester t) {
    initData();
    WorldScene scene = maze1.makeScene();

    for (Edge edge : maze1.edgeList) {
      t.checkExpect(
          imageExistsAtPosn(scene, new Posn(edge.first.x * 20, edge.first.y * 20), edge.drawEdge()),
          true);
    }
  }
}
