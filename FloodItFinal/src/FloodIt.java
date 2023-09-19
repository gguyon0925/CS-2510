import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.Tester;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//Represents a single square of the game area
class Cell {
  // origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  Posn posn;

  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // list of colors
  ArrayList<Color> loColors;

  // constructor for playing the game
  Cell(int x, int y, boolean flooded, int color) {
    this.x = x;
    this.y = y;
    this.flooded = flooded;
    initColors();
    int random = new Random().nextInt(this.loColors.size());
    this.color = this.loColors.get(random);
    this.posn = new Posn(this.x, this.y);
  }

  // constructor for testing
  Cell(int x, int y, Color color, boolean flooded, Cell left, Cell top, Cell right, Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.posn = new Posn(this.x, this.y);
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }


  // initialize list of colors of which the cells can be
  void initColors() {
    this.loColors = new ArrayList<Color>(Arrays.asList(Color.red, Color.orange, Color.yellow,
        Color.green, Color.blue, Color.pink, Color.magenta, Color.darkGray));
  }

  // Draw the cell using its color
  WorldImage drawCell() {
    return new RectangleImage(50, 50, OutlineMode.SOLID, this.color);
  }
}

// Represents the world of the game, contains fields and methods needed to play the game
class FloodItWorld extends World {
  int sizeBoard = 10;
  int numColors = 8;

  ArrayList<Cell> board;

  int required;
  int clicks = 0;
  int time = 0;

  // constructor for playing the game
  FloodItWorld(int sizeBoard, int numColors) {
    this.sizeBoard = sizeBoard;
    this.numColors = numColors;
    this.board = new ArrayList<Cell>(sizeBoard * sizeBoard);
    this.required = (int) (sizeBoard * sizeBoard * 0.75);
    this.initBoard(sizeBoard);
  }

  // constructor used for testing
  FloodItWorld() {
    this.sizeBoard = 3;
    this.numColors = 5;
    this.required = (int) (this.sizeBoard * this.sizeBoard * 0.75);
  }

  // initialize the board with cells
  void initBoard(int size) {
    board = new ArrayList<Cell>(size * size);
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        boolean isFlooded = (i == 0 && j == 0);
        Cell cell = new Cell(i, j, isFlooded, this.numColors);
        if (i > 0) {
          cell.top = board.get((i - 1) * size + j);
          cell.top.bottom = cell;
        }
        if (j > 0) {
          cell.left = board.get(i * size + j - 1);
          cell.left.right = cell;
        }
        board.add(cell);
      }
    }
  }

  // draw the board
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(500, 500);
    int cellSize = 500 / this.sizeBoard;
    for (int i = 0; i < this.board.size(); i++) {
      scene.placeImageXY(this.board.get(i).drawCell(),
          this.board.get(i).posn.x * cellSize + cellSize / 2,
          this.board.get(i).posn.y * cellSize + cellSize / 2);
    }
    if (this.clicks <= this.required && this.isFlooded()) {
      scene.placeImageXY(new TextImage("You Won!", 30, Color.black), 250, 250);
    }
    else if (this.clicks >= this.required && !this.isFlooded()) {
      scene.placeImageXY(new TextImage("You Lost!", 30, Color.black), 250, 250);
    }
    return scene;
  }

  // is the board flooded? Return true if it is, false otherwise
  boolean isFlooded() {
    for (int i = 0; i < this.board.size(); i++) {
      if (!this.board.get(i).flooded) {
        return false;
      }
    }
    return true;
  }
}

// Examples of data used in the world and for tests
class ExamplesFlood {

  // Represent cells on the board 
  Cell cr1;
  Cell cg2;
  Cell co3;
  Cell cy4;
  Cell cb5;
  Cell cp6;
  Cell cm7;
  Cell cdg8;

  // Represent the board
  ArrayList<Cell> board;

  // Represent the colors the cells can have
  ArrayList<Color> colors;

  // Represent the world in game state
  FloodItWorld worldGame;

  // Represent the world in test state 
  FloodItWorld worldTest;

  // Represent the board in test state and its size
  int BOARDSIZE;
  ArrayList<Cell> boardTest;

  // initialize data
  void initData() {

    // c1 is a red cell
    this.cr1 = new Cell(0, 0, Color.RED, true, null, null, null, null);
    // cg2 is a green cell
    this.cg2 = new Cell(1, 0, Color.GREEN, false, null, null, null, null);
    // co3 is an orange cell
    this.co3 = new Cell(0, 1, Color.ORANGE, false, null, null, null, null);
    // cy4 is a yellow cell
    this.cy4 = new Cell(1, 1, Color.YELLOW, false, null, null, null, null);
    // cb5 is a blue cell
    this.cb5 = new Cell(0, 2, Color.BLUE, false, null, null, null, null);
    // cp6 is a pink cell
    this.cp6 = new Cell(1, 2, Color.PINK, false, null, null, null, null);
    // cm7 is a magenta cell
    this.cm7 = new Cell(0, 3, Color.MAGENTA, false, null, null, null, null);
    // cdg8 is a dark gray cell
    this.cdg8 = new Cell(1, 3, Color.DARK_GRAY, false, null, null, null, null);

    // Connect the cells to each other
    this.cr1.right = this.cg2;
    this.cr1.bottom = this.co3;
    this.cg2.bottom = this.cy4;
    this.co3.right = this.cy4;
    this.co3.bottom = this.cb5;
    this.cy4.bottom = this.cp6;
    this.cb5.right = this.cp6;
    this.cb5.bottom = this.cm7;
    this.cp6.bottom = this.cdg8;
    this.cm7.right = this.cdg8;
    this.cdg8.top = this.cp6;

    // initialize the board
    // add this to all the cells
    this.board = new ArrayList<Cell>();
    this.board.add(this.cr1);
    this.board.add(this.cg2);
    this.board.add(this.co3);
    this.board.add(this.cy4);
    this.board.add(this.cb5);
    this.board.add(this.cp6);
    this.board.add(this.cm7);
    this.board.add(this.cdg8);

    // initialize the colors
    this.colors = new ArrayList<Color>();
    this.colors.add(Color.red);
    this.colors.add(Color.green);
    this.colors.add(Color.orange);
    this.colors.add(Color.yellow);
    this.colors.add(Color.blue);
    this.colors.add(Color.pink);
    this.colors.add(Color.magenta);
    this.colors.add(Color.darkGray);

    // World used in the game
    this.worldGame = new FloodItWorld(10, 8);

    // World used in the tests
    this.worldTest = new FloodItWorld();

    // initialize the board in the test world with a size of 6
    // has random colors
    this.BOARDSIZE = 6;
    this.worldTest.initBoard(BOARDSIZE);

    // initialize the test board in the test world with a size of 4
    // and has the cells cr1, co3, cg2, cy4
    this.boardTest = this.worldTest.board;
    this.worldTest.board = new ArrayList<Cell>();
    this.worldTest.board.add(this.cr1);
    this.worldTest.board.add(this.co3);
    this.worldTest.board.add(this.cg2);
    this.worldTest.board.add(this.cy4);

  }

  // test that initData works
  void testInitData(Tester t) {
    this.initData();
    t.checkExpect(cr1, new Cell(0, 0, Color.RED, true, null, null, cg2, co3));
    t.checkExpect(cg2, new Cell(1, 0, Color.GREEN, false, null, null, null, cy4));
    t.checkExpect(co3, new Cell(0, 1, Color.ORANGE, false, null, null, cy4, cb5));
    t.checkExpect(cy4, new Cell(1, 1, Color.YELLOW, false, null, null, null, cp6));
    t.checkExpect(cb5, new Cell(0, 2, Color.BLUE, false, null, null, cp6, cm7));
    t.checkExpect(cp6, new Cell(1, 2, Color.PINK, false, null, null, null, cdg8));
    t.checkExpect(cm7, new Cell(0, 3, Color.MAGENTA, false, null, null, cdg8, null));
    t.checkExpect(cdg8, new Cell(1, 3, Color.DARK_GRAY, false, null, cp6, null, null));

    t.checkExpect(board,
        new ArrayList<Cell>(Arrays.asList(cr1, cg2, co3, cy4, cb5, cp6, cm7, cdg8)));
    t.checkExpect(colors, new ArrayList<Color>(Arrays.asList(Color.red, Color.green, Color.orange,
        Color.yellow, Color.blue, Color.pink, Color.magenta, Color.darkGray)));
    t.checkExpect(boardTest.size(), 36);
  }

  /*
   * TESTS FOR ALL DEFINED METHODS
   */

  // test the initColors method
  void testInitColors(Tester t) {
    this.colors = null;
    t.checkExpect(colors, null);
    this.initData();
    t.checkExpect(colors.contains(Color.BLUE), true);
    t.checkExpect(colors.contains(Color.red), true);
    t.checkExpect(colors.contains(Color.green), true);
    t.checkExpect(colors.contains(Color.orange), true);
    t.checkExpect(colors.contains(Color.yellow), true);
    t.checkExpect(colors.contains(Color.pink), true);
    t.checkExpect(colors.contains(Color.magenta), true);
    t.checkExpect(colors.contains(Color.darkGray), true);
    t.checkExpect(colors.contains(Color.black), false);

  }

  // test the drawCell method
  void testDrawCell(Tester t) {
    this.initData();
    t.checkExpect(cr1.drawCell(), new RectangleImage(50, 50, OutlineMode.SOLID, Color.RED));
    t.checkExpect(cg2.drawCell(), new RectangleImage(50, 50, OutlineMode.SOLID, Color.GREEN));
    t.checkExpect(co3.drawCell(), new RectangleImage(50, 50, OutlineMode.SOLID, Color.ORANGE));
    t.checkExpect(cy4.drawCell(), new RectangleImage(50, 50, OutlineMode.SOLID, Color.YELLOW));
    t.checkExpect(cb5.drawCell(), new RectangleImage(50, 50, OutlineMode.SOLID, Color.BLUE));
    t.checkExpect(cp6.drawCell(), new RectangleImage(50, 50, OutlineMode.SOLID, Color.PINK));
    t.checkExpect(cm7.drawCell(), new RectangleImage(50, 50, OutlineMode.SOLID, Color.MAGENTA));
    t.checkExpect(cdg8.drawCell(), new RectangleImage(50, 50, OutlineMode.SOLID, Color.DARK_GRAY));
  }

  // test the initBoard method
  void testInitBoard(Tester t) {
    this.initData();

    t.checkExpect(worldTest.board.size(), 4);

    this.worldTest.initBoard(this.BOARDSIZE);
    t.checkExpect(worldTest.board.size(), 36);
    t.checkExpect(worldTest.board.get(0).posn.x, 0);
    t.checkExpect(worldTest.board.get(0).posn.y, 0);
    t.checkExpect(worldTest.board.get(1).posn.x, 0);
    t.checkExpect(worldTest.board.get(1).posn.y, 1);
    t.checkExpect(worldTest.board.get(2).posn.x, 0);
    t.checkExpect(worldTest.board.get(2).posn.y, 2);

    int size = this.BOARDSIZE;
    t.checkExpect(worldTest.board.size(), size * size);
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        Cell cell = worldTest.board.get(i * size + j);
        t.checkExpect(cell.x, i);
        t.checkExpect(cell.y, j);
        if (i == 0 && j == 0) {
          t.checkExpect(cell.flooded, true);
        }
        else {
          t.checkExpect(cell.flooded, false);
        }
        if (i > 0) {
          t.checkExpect(cell.top, worldTest.board.get((i - 1) * size + j));
          t.checkExpect(cell.top.bottom, cell);
        }
        if (j > 0) {
          t.checkExpect(cell.left, worldTest.board.get(i * size + j - 1));
          t.checkExpect(cell.left.right, cell);
        }
      }
    }
  }

  // test the makeScene method
  void testMakeScene(Tester t) {
    this.initData();

    worldTest.board = boardTest;
    worldTest.board.get(0).flooded = true;
    worldTest.board.get(1).flooded = true;
    worldTest.board.get(2).flooded = true;
    worldTest.board.get(3).flooded = true;
    worldTest.board.get(0).color = Color.red;
    worldTest.board.get(1).color = Color.red;
    worldTest.board.get(2).color = Color.red;
    worldTest.board.get(3).color = Color.red;

    worldTest.makeScene();

    t.checkExpect(worldTest.board.get(0).color, Color.red);
    t.checkExpect(worldTest.board.get(1).color, Color.red);
    t.checkExpect(worldTest.board.get(2).color, Color.red);
    t.checkExpect(worldTest.board.get(3).color, Color.red);

    t.checkExpect(worldTest.board.get(0).flooded, true);
    t.checkExpect(worldTest.board.get(1).flooded, true);
    t.checkExpect(worldTest.board.get(2).flooded, true);
    t.checkExpect(worldTest.board.get(3).flooded, true);

    // create a new world with a size of two and a number of colors of three
    FloodItWorld world = new FloodItWorld(2, 4);

    // add sample colors to the cells
    world.board.get(0).color = Color.red;
    world.board.get(1).color = Color.green;
    world.board.get(2).color = Color.blue;
    world.board.get(3).color = Color.yellow;

    // create the expected scene
    WorldScene expectedScene = new WorldScene(500, 500);
    int cellSize = 250;
    int cells = 50;
    expectedScene.placeImageXY(new RectangleImage(cells, cells, OutlineMode.SOLID, Color.red),
        cellSize / 2, cellSize / 2);
    expectedScene.placeImageXY(new RectangleImage(cells, cells, OutlineMode.SOLID, Color.green),
        cellSize / 2, cellSize * 3 / 2);
    expectedScene.placeImageXY(new RectangleImage(cells, cells, OutlineMode.SOLID, Color.blue),
        cellSize * 3 / 2, cellSize / 2);
    expectedScene.placeImageXY(new RectangleImage(cells, cells, OutlineMode.SOLID, Color.yellow),
        cellSize * 3 / 2, cellSize * 3 / 2);

    // test the makeScene method
    t.checkExpect(world.makeScene(), expectedScene);

  }

  // test the isFlooded method
  void testIsFlooded(Tester t) {
    this.initData();

    worldTest.board = boardTest;
    worldTest.board.get(0).flooded = true;
    worldTest.board.get(1).flooded = true;
    worldTest.board.get(2).flooded = true;
    worldTest.board.get(3).flooded = true;
    t.checkExpect(worldTest.isFlooded(), false);

    for (Cell cell : worldTest.board) {
      cell.flooded = true;
    }
    t.checkExpect(worldTest.isFlooded(), true);

  }

  // Uncomment the below lines to run the game 

  // Run the game `
  void testGame(Tester t) {
    this.initData();
    FloodItWorld w = new FloodItWorld(10, 4);
    w.bigBang(1200, 800);
  }
// public static void main(String[] args){}
// }

}