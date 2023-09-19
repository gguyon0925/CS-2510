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

  // constructor
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
    int index = this.loColors.indexOf(this.color);
    if (index != -1) {
        return new RectangleImage(20, 20, OutlineMode.SOLID, this.loColors.get(index));
    } else {
        return new RectangleImage(20, 20, OutlineMode.SOLID, Color.darkGray);
    }
}


  // Change the color of the cell to the given color
  void changeColor(Color color) {
    this.color = color;
  }


  // Update the neighbors of the cell so that they are flooded if
  // they are the same color as the cell
//   void updateNeighbors(Color color) {
//     Cell[] neighbors = {this.left, this.top, this.right, this.bottom};
//     for (Cell neighbor : neighbors) {
//         if (neighbor != null && !neighbor.flooded && neighbor.color.equals(color)) {
//             neighbor.flooded = true;
//         }
//     }
// }

// void updateNeighbors(Color color) {
//   if (this.left != null
//       && !this.left.flooded
//       && this.left.color.equals(color)) {
//     this.left.flooded = true;
//   }
//   if (this.top != null
//       && !this.top.flooded 
//       && this.top.color.equals(color)) {
//     this.top.flooded = true;
//   }
//   if (this.right != null
//       && !this.right.flooded
//       && this.right.color.equals(color)) {
//     this.right.flooded = true;
//   }
//   if (this.bottom != null
//       && !this.bottom.flooded 
//       && this.bottom.color.equals(color)) {
//     this.bottom.flooded = true;
//   }
// }

void updateNeighbors(Color color) {
  for (Cell neighbor : new Cell[]{this.left, this.top, this.right, this.bottom}) {
    if (neighbor != null && !neighbor.flooded && neighbor.color.equals(color)) {
      neighbor.flooded = true;
    }
  }
}

}

class FloodItWorld extends World {
  int sizeBoard;
  int numColors;

  ArrayList<Cell> board;

  int required;
  int clicks = 0;
  int time = 0;

  // constructor for playing the game
  FloodItWorld(int sizeBoard, int numColors) {
    this.sizeBoard = sizeBoard;
    this.numColors = numColors;
    this.board = new ArrayList<Cell>(sizeBoard * sizeBoard);
    this.required = sizeBoard * numColors / 3;
    this.initBoard(sizeBoard);
  }

  // constructor for testing
  FloodItWorld() {
    this.sizeBoard = 2;
    this.numColors = 3;
    this.required = 3;
  }

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

//   // draw the board
public WorldScene makeScene() {
  WorldScene finalScene = new WorldScene(1200, 800);
  finalScene.placeImageXY(new CircleImage(600, "solid", Color.BLUE), 600, 400);
  finalScene.placeImageXY( new TextImage(Integer.toString(clicks) + "     ", 30, FontStyle.BOLD_ITALIC, Color.CYAN), 1000, 450);
  finalScene.placeImageXY( new TextImage("  /  " + Integer.toString(required), 30, FontStyle.BOLD_ITALIC, Color.CYAN), 1030, 450);
  finalScene.placeImageXY(new TextImage("MY FLOOD IT GAME :)", 50, FontStyle.BOLD_ITALIC, Color.MAGENTA), 600, 650);
  WorldImage outline = new RectangleImage((sizeBoard + 2) * 20,(sizeBoard + 2) * 20, OutlineMode.SOLID, new Color(42, 64, 99));
  finalScene.placeImageXY(outline, ((sizeBoard + 2) * 10) + 50, ((sizeBoard + 2) * 10) + 50);
  finalScene.placeImageXY(new TextImage("Time: " + time / 10 + "seconds", 35, FontStyle.BOLD_ITALIC, Color.CYAN), 1020, 600);
  if (clicks >= required && (!isFlooded())) {
      finalScene.placeImageXY(new TextImage("YOU LOSE :(", 25, Color.RED), 1020, 500);
      finalScene.placeImageXY(new TextImage("Click Q to restart", 25, Color.RED), 1020, 550);
  } else if (clicks <= required && isFlooded()) {
      finalScene.placeImageXY(new TextImage("YOU WIN!!!! :)", 25, Color.GREEN), 1020, 500);
      finalScene.placeImageXY(new TextImage("Click Q to restart", 25, Color.GREEN), 1020, 550);
  }
  for (Cell c: board) {
      finalScene.placeImageXY(c.drawCell(), 80 + 20 * c.x, 80 + 20 * c.y);
  }
  return finalScene;
}


//   // is the board flooded?
  boolean isFlooded() {
    for (int i = 0; i < this.board.size(); i++) {
      if (!this.board.get(i).flooded) {
        return false;
      }
    }
    return true;
  }

    // Updates the first cell in the board
  // Effect: Changes the first cell to have the color that has been clicked on.
  // public void updateOnClick(Cell cell) {
  //   if (cell != null) {
  //     Cell modifyThis = board.get(0);
  //     modifyThis.color = cell.color;
  //     board.set(0, modifyThis);
  //   }
  // }

  // on tick, update the time
  public void onTick() {
    this.time++;
    updateWorld();
  }

  // update the world   // Updates cells in the world.
  // Effect: Changes all the flooded cells in the board to have the color
  // of the first cell in the board which already has been changed to have 
  // the color of the cell that was clicked on. Use updateonclick to change
  // the first cell to have the color of the cell that was clicked on.
  public void updateWorld() {
    Cell floodingFromCell = this.board.get(0);
    Color floodingTo = floodingFromCell.color;
    for (int i = 0; i < board.size(); i++) {
        Cell cell = board.get(i);
        if (cell.flooded) {
            cell.changeColor(floodingTo);
            cell.updateNeighbors(floodingTo);
        }
    }
    makeScene();
}

public Cell whichCell(Posn pos) {
  int x = (pos.x - 71) / 20;
  int y = (pos.y - 71) / 20;
  for (Cell c : board) {
      if (c.x == x && c.y == y) {
          return c;
      }
  }
  return null;
}

public void updateOnClick(Cell cell) {
  if (cell != null) {
      board.get(0).color = cell.color;
  }
}

public void onMouseClicked(Posn pos) {
  if ((pos.x >= 70 && pos.x <= (sizeBoard * 20 + 70))
          && (pos.y >= 70 && pos.y <= (sizeBoard * 20 + 70))) {
      this.updateOnClick(this.whichCell(pos));
      clicks++;
  }
}

// flood the board with the given color
void flood(Color color) {
    for (int i = 0; i < this.board.size(); i++) {
        if (this.board.get(i).flooded) {
            this.board.get(i).color = color;
            this.board.get(i).updateNeighbors(color);
        }
    }
}


// on key to reset the game with all random colors when the q button is clicked
  public void onKeyEvent(String key) {
    if (key.equals("q")) {
      clicks = 0;
      time = 0;
      this.board = new ArrayList<Cell>();
      this.initBoard(this.sizeBoard);
    }
  }
  

  // This method runs the game based upon the inputs of the player for
  // grid size and number of colors used.
  // Effect: changes the arguments given to bigBang based on user input.
  // use the int sizeBoard and int numColors to change the arguments given to
  // bigBang.
  public void startGame(int gridSize, int numberOfColor) {
    if (numberOfColor > 8) {
      throw new IllegalArgumentException("Number of colors cannot exceed 8");
    }
    sizeBoard = gridSize;
    numColors = numberOfColor;
    FloodItWorld w = new FloodItWorld(gridSize, numberOfColor);
    w.bigBang(1200, 800, 0.1);
  }

  }



  

class ExamplesFlood {

  // Examples of data used in the world and for tests

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
    t.checkExpect(this.cr1.drawCell(), new RectangleImage(10, 10, OutlineMode.SOLID, Color.RED));
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


  // test whichCell method
  void testWhichCell(Tester t) {
    this.initData();

    t.checkExpect(worldTest.whichCell(new Posn(70, 70)), boardTest.get(0));
    // t.checkExpect(exWorld.whichCell(new Posn(125, 125)), exBoard.get(14));
    // t.checkExpect(exWorld.whichCell(new Posn(190, 190)), exBoard.get(35));
  }
  //Run the game 
  // The player gives the values for grid size and number of colors used.
  void testGame(Tester t) {
    FloodItWorld w = new FloodItWorld(20, 6);
    t.checkException(
        new IllegalArgumentException("Number of colors cannot exceed 8"),
        w, "startGame", 20, 9);
    // Modify game inputs here.
    // Arguments: Grid Size & Number of colors
    w.startGame(10, 5);
    t.checkExpect(w.sizeBoard, 20);
    t.checkExpect(w.numColors, 6);
    t.checkExpect(w.clicks, 0);
    t.checkExpect(w.required, 36);
  }

}
