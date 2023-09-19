import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the
  // screen
  int x;
  int y;
  String color;
  boolean flooded;
  Posn pos;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;
  // The list of colors
  ArrayList<String> colors;

  // The constructor.
  Cell(int x, int y, boolean flooded, int colorNum) {
    this.x = x; 
    this.y = y;
    initColors();
    int random = (int) (Math.random() * colorNum);
    this.color = colors.get(random);
    this.flooded = flooded;
    this.pos = new Posn(this.x, this.y);
  }

  // Convenience Constructor only for testing.
  Cell(int x, int y, String color, boolean flooded,
      Cell left, Cell top, Cell right, Cell bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.pos = new Posn(this.x, this.y);
    this.left = left;
    this.top = top; 
    this.right = right;
    this.bottom = bottom;
  }

  // Draws the cell
  WorldImage image() {
    if (this.color.equals("RED")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.RED);
    }
    else if (this.color.equals("BLUE")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.BLUE);
    }
    else if (this.color.equals("GREEN")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.GREEN);
    }
    else if (this.color.equals("PURPLE")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, 
          new Color(184, 0, 245));
    }
    else if (this.color.equals("BLACK")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.BLACK);
    }
    else if (this.color.equals("YELLOW")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.YELLOW);
    }
    else if (this.color.equals("PINK")) {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.PINK);
    }
    else {
      return new RectangleImage(20, 20, OutlineMode.SOLID, Color.ORANGE);
    }
  }

  // Initializes the color list
  // Effect: modifies the colors list to include the allowed colors.
  void initColors() {
    colors = new ArrayList<String>();
    colors.add("BLUE");
    colors.add("PURPLE");
    colors.add("PINK");
    colors.add("RED");
    colors.add("GREEN");
    colors.add("BLACK");
    colors.add("YELLOW");
    colors.add("ORANGE");
  }

  // Changes the color of this cell
  // Effect: Changes the color value of this cell to the input string.
  void setColor(String color) {
    this.color = color;
  }

  // Updates this cell's neighbors. 
  // Effect: Changes the isFlooded value of non-null non-flooded cells with
  // the correct color to be flooded.
  void update(String color) {
    if (this.left != null
        && !this.left.flooded
        && this.left.color.equals(color)) {
      this.left.flooded = true;
    }
    if (this.top != null
        && !this.top.flooded 
        && this.top.color.equals(color)) {
      this.top.flooded = true;
    }
    if (this.right != null
        && !this.right.flooded
        && this.right.color.equals(color)) {
      this.right.flooded = true;
    }
    if (this.bottom != null
        && !this.bottom.flooded 
        && this.bottom.color.equals(color)) {
      this.bottom.flooded = true;
    }
  }
}

// To represent the FloodItWorld
class FloodItWorld extends World {
  // Defines an int 
  int boardSize = 20;
  // Determines how many colors are used to create the game
  // Note: this value should not exceed 8 as there are only 8 colors.
  int colorsUsed = 8;
  // All the cells of the game
  ArrayList<Cell> board;
  // DO NOT MODIFY THIS VALUE AS IT IS SET SMALL FOR TESTING PURPOSES.
  static final int testBoardSize = 6;
  int required;
  int clicks = 0;
  int time = 0; 

  // Constructor 
  FloodItWorld(int bdSize, int colors) {
    boardSize = bdSize;
    colorsUsed = colors;
    createCells(boardSize);
    if (boardSize > 12) {
      required = boardSize + colorsUsed + 10;
    }
    else if (boardSize < 4) {
      required = boardSize + colorsUsed - 2;
    }
    else {
      required = boardSize + colorsUsed - 1;
    }
  }

  // Convenience Constructor for testing
  FloodItWorld() {
    boardSize = 2;
    colorsUsed = 3;
    required = 3;
  }

  // Creates cells with a random color based on boardSize
  // Effect: creates the board of cells with the correct number of cells and
  // modifies their fields to refer to the correct neighbor cells.
  void createCells(int size) {
    board = new ArrayList<Cell>();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (i == 0 && j == 0) {
          board.add(new Cell(0, 0, true, this.colorsUsed));
        }
        else {
          board.add(new Cell(i, j, false, this.colorsUsed)); 
        }
      }
    }
    // Modifies each cell to refer to the correct top, left, right
    // and bottom cells.
    for (int k = 0; k < board.size(); k++) {
      Cell modifyThis = board.get(k);
      if (board.get(k).x == 0) { 
        modifyThis.left = null;
      }
      else {
        modifyThis.left = board.get(k - size);
      }
      if (board.get(k).x == size - 1) {
        modifyThis.right = null;
      }
      else {
        modifyThis.right = board.get(k + size);
      }
      if (board.get(k).y == 0) {
        modifyThis.top = null;
      } 
      else {
        modifyThis.top = board.get(k - 1);
      }
      if (board.get(k).y == size - 1) {
        modifyThis.bottom = null;
      } 
      else {
        modifyThis.bottom = board.get(k + 1);
      }
    }
  }

  // ==============================On Mouse============================== //
  // Determines which cell in the board was clicked on and returns that cell.
  public Cell whichCell(Posn pos) {
    Cell thisCell = null;
    for (Cell c: board) {
      if ((c.x <= ((pos.x - 71) / 20)) && (((pos.x - 71) / 20) <= c.x )
          && (c.y <= ((pos.y - 71) / 20)) && (((pos.y - 71) / 20) <= c.y )) {
        thisCell = c;
      }
    }
    return thisCell;
  }

  // Updates the first cell in the board
  // Effect: Changes the first cell to have the color that has been clicked on.
  public void updateOnClick(Cell cell) {
    if (cell != null) {
      Cell modifyThis = board.get(0);
      modifyThis.color = cell.color;
      board.set(0, modifyThis);
    }
  }

  // Parses mouse click events.
  // Effect: Changes the world state according to which cell was clicked.
  public void onMouseClicked(Posn pos) {
    if ((pos.x < 70 || pos.x > (boardSize * 20 + 70))
        || (pos.y < 70 || pos.y > (boardSize * 20 + 70))) {
      // Do nothing
    } else {
      this.updateOnClick(this.whichCell(pos)); 
      clicks++;
    }
  }

  // ==============================Make Scene============================== //
  // Makes the world scene which is displayed to the player.
  public WorldScene makeScene() {
    WorldScene finalScene = new WorldScene(1200, 800);
    finalScene.placeImageXY(new FromFileImage("./background.jpg"), 600, 400);
    finalScene.placeImageXY(
        new TextImage(Integer.toString(clicks) + "    ", 30, FontStyle.BOLD_ITALIC, 
            Color.CYAN), 1000, 450);
    finalScene.placeImageXY(
        new TextImage("/ " + Integer.toString(required), 30, FontStyle.BOLD_ITALIC,
            Color.CYAN), 1030, 450);
    finalScene.placeImageXY(new TextImage("FLOOD-IT", 50, 
        FontStyle.BOLD_ITALIC, Color.MAGENTA), 600, 650);
    WorldImage outline = 
        new RectangleImage((boardSize + 2) * 20,(boardSize + 2) * 20,
            OutlineMode.SOLID, new Color(42, 64, 99));
    finalScene.placeImageXY(outline, ((boardSize + 2) * 10) + 50, ((boardSize + 2) * 10) + 50);
    finalScene.placeImageXY(new TextImage("Time: " + time / 10 + "s", 35,
        FontStyle.BOLD_ITALIC, Color.CYAN), 1020, 600);
    if (clicks >= required
        && (!allFlooded())) {
      finalScene.placeImageXY(new TextImage("YOU LOSE", 25, Color.RED), 1020, 500);
    }
    else if (clicks <= required
        && allFlooded()) {
      finalScene.placeImageXY(new TextImage("YOU WIN", 25, Color.GREEN), 1020, 500);
    }
    for (Cell c: board) {
      finalScene.placeImageXY(c.image(), 80 + 20 * c.x, 80 + 20 * c.y);
    }
    return finalScene;
  }

  // Updates cells in the world.
  // Effect: Changes all the flooded cells in the board to have the color
  // of the first cell in the board which already has been changed to have 
  // the color of the cell that was clicked on.
  public void updateWorld() {
    Cell floodingFromCell = this.board.get(0);
    String floodingTo = floodingFromCell.color;
    for (int i = 0; i < board.size(); i++) {
      Cell cell = board.get(i);
      if (cell.flooded) {
        cell.setColor(floodingTo);
        cell.update(floodingTo);
      }
      makeScene();
    }
  }

  // Checks if all the cells in the board are flooded
  boolean allFlooded() {
    boolean result = true;
    for (Cell c: board) {
      result = result && c.flooded;
    }
    return result;
  }

  // ==============================On Tick============================== //
  // Changes the state of the world at each tick
  // Effect: modifies the colors and isFlooded values for neighbor cells.
  public void onTick() {
    time++;
    updateWorld();
  }

  // ==============================On Key============================== //
  // Resets the game when the player presses r.
  // Effect: re-initializes the board of cells and creates the cells again
  // with random colors.
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.board = new ArrayList<Cell>();
      clicks = 0;
      createCells(boardSize);
    }
  }

  // ==============================START GAME============================== //
  // This method runs the game based upon the inputs of the player for
  // grid size and number of colors used.
  // Effect: changes the arguments given to bigBang based on user input.
  public void startGame(int gridSize, int numberOfColor) {
    if (numberOfColor > 8) {
      throw new IllegalArgumentException("Number of colors cannot exceed 8");
    }
    boardSize = gridSize;
    colorsUsed = numberOfColor;
    FloodItWorld w = new FloodItWorld(gridSize, numberOfColor);
    w.bigBang(1200, 800, 0.1);
  }
}

// To represent examples of worlds, cells and tests.
class ExamplesFloodIt {
  // Examples of cells
  Cell cBlue;
  Cell cRed;
  Cell cGreen;
  Cell cYellow;
  Cell cOrange;
  Cell cBlack;
  Cell cPink;
  Cell cPurple;
  // List of cell colors
  ArrayList<String> loColors;
  // List of cells to test on
  ArrayList<Cell> testBoard;
  // Example FloodItWorld to test on.
  FloodItWorld exWorld;
  FloodItWorld exWorld2;
  // The example list of cells
  ArrayList<Cell> exBoard;

  // Initial condition
  void initWorld() {
    cBlue = new Cell(0, 0, "BLUE", true,
        null, null, null, null);
    cRed = new Cell(1, 0, "RED", false,
        cBlue, null, null, null);
    cGreen = new Cell(0, 1, "GREEN", false,
        null, cBlue, null, null);
    cYellow = new Cell(1, 1, "YELLOW", false,
        cGreen, cRed, null, null);
    cOrange = new Cell(0, 2, "ORANGE", false, 
        null, cGreen, null, null);
    cBlack = new Cell(1, 2, "BLACK", false,
        cOrange, cYellow, null, null);
    cPink = new Cell(0, 3, "PINK", false,
        null, cOrange, null, null);
    cPurple = new Cell(1, 3, "PURPLE", false,
        cPink, cBlack, null, null);
    cBlue.right = cRed;
    cBlue.bottom = cGreen;
    cRed.bottom = cYellow;
    cGreen.right = cYellow;
    cGreen.bottom = cOrange;
    cYellow.bottom = cBlack;
    cOrange.bottom = cPink;
    cOrange.right = cBlack;
    cBlack.bottom = cPurple;
    cPink.right = cPurple;

    // The list of available colors.
    loColors = new ArrayList<String>();
    loColors.add("BLUE");
    loColors.add("RED");
    loColors.add("GREEN");
    loColors.add("PURPLE");
    loColors.add("BLACK");
    loColors.add("YELLOW");
    loColors.add("PINK");
    loColors.add("ORANGE");

    // Mimics the initColors method for testing
    ArrayList<Cell> testBoard = new ArrayList<Cell>();
    testBoard.add(cBlue);
    testBoard.add(cGreen);
    testBoard.add(cOrange);
    testBoard.add(cPink);
    testBoard.add(cRed);
    testBoard.add(cYellow);
    testBoard.add(cBlack);
    testBoard.add(cPurple);

    // Initializing the exampleFloodItWorld
    exWorld = new FloodItWorld();
    exWorld2 = new FloodItWorld();

    // Creating the cells that we will use to test
    exWorld.createCells(FloodItWorld.testBoardSize);

    // The board of cells we will use to test.
    exBoard = exWorld.board;
    exWorld2.board = new ArrayList<Cell>();
    exWorld2.board.add(cBlue);
    exWorld2.board.add(cGreen);
    exWorld2.board.add(cRed);
    exWorld2.board.add(cYellow);
  }

  // Tests image method
  void testImage(Tester t) {
    initWorld();
    t.checkExpect(this.cBlue.image(), 
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.blue));
    t.checkExpect(this.cRed.image(), 
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.red));
    t.checkExpect(this.cGreen.image(), 
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.green));
    t.checkExpect(this.cYellow.image(), 
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.yellow));
    t.checkExpect(this.cOrange.image(), 
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.orange));
    t.checkExpect(this.cBlack.image(), 
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.black));
    t.checkExpect(this.cPink.image(), 
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.pink));
    t.checkExpect(this.cPurple.image(), 
        new RectangleImage(20, 20, OutlineMode.SOLID, new Color(184, 0, 245)));
  }

  // Tests initColors method
  void testInitColors(Tester t) {
    this.loColors = null;
    t.checkExpect(loColors, null);
    initWorld();
    t.checkExpect(loColors.contains("BLUE"), true);
    t.checkExpect(loColors.contains("RED"), true);
    t.checkExpect(loColors.contains("GREEN"), true);
    t.checkExpect(loColors.contains("BLACK"), true);
    t.checkExpect(loColors.contains("PURPLE"), true);
    t.checkExpect(loColors.contains("YELLOW"), true);
    t.checkExpect(loColors.contains("PINK"), true);
    t.checkExpect(loColors.contains("ORANGE"), true);
  }

  // Tests the setColor method.
  void testSetColor(Tester t) {
    initWorld();
    t.checkExpect(cBlue.color, "BLUE");
    t.checkExpect(cRed.color, "RED");
    t.checkExpect(cGreen.color, "GREEN");
    cGreen.setColor("PINK");
    cBlue.setColor("RED");
    cRed.setColor("BLUE");
    t.checkExpect(cBlue.color, "RED");
    t.checkExpect(cRed.color, "BLUE");
    t.checkExpect(cGreen.color, "PINK");
  }

  // Tests the update method
  void testUpdate(Tester t) {
    initWorld();
    t.checkExpect(cBlue.right.flooded, false);
    t.checkExpect(cBlue.bottom.flooded, false);
    cBlue.update("BLUE");
    t.checkExpect(cBlue.right.flooded, false);
    t.checkExpect(cBlue.bottom.flooded, false);
    cBlue.color = "RED";
    cBlue.update("RED");
    t.checkExpect(cBlue.right.flooded, true);
    t.checkExpect(cBlue.bottom.flooded, false);
    cBlue.update("GREEN");
    t.checkExpect(cBlue.right.flooded, true);
    t.checkExpect(cBlue.bottom.flooded, true);
  }

  // Tests for whichCell method
  void testWhichCell(Tester t) {
    initWorld();
    t.checkExpect(exWorld.whichCell(new Posn(70, 70)), exBoard.get(0));
    t.checkExpect(exWorld.whichCell(new Posn(125, 125)), exBoard.get(14));
    t.checkExpect(exWorld.whichCell(new Posn(190, 190)), exBoard.get(35));
  }

  // Tests createCells method
  void testCreateCells(Tester t) {
    initWorld();
    // Checks to make sure the first cell is always flooded
    t.checkExpect(exBoard.get(0).flooded, true);
    // Checks all cells in the board to make sure they have value colors and
    // x and y coordinates.
    for (int i = 0; i < exWorld.board.size(); i++) {
      Cell temp = exWorld.board.get(i);
      int bdSize = 6;
      t.checkRange(temp.x, 0, bdSize);
      t.checkRange(temp.y, 0, bdSize);
      t.checkExpect(loColors.contains(temp.color), true);
      if (temp.x == 0) {
        t.checkExpect(temp.left, null);
      }
      else {
        t.checkExpect(temp.left, exWorld.board.get(i - bdSize));
      }
      if (temp.y == 0) {
        t.checkExpect(temp.top, null);
      }
      else {
        t.checkExpect(temp.top, exWorld.board.get(i - 1));
      }
      if (temp.x == (bdSize - 1)) {
        t.checkExpect(temp.right, null);
      }
      else {
        t.checkExpect(temp.right, exWorld.board.get(i + bdSize));
      }
      if (temp.y == (bdSize - 1)) {
        t.checkExpect(temp.bottom, null);
      }
      else {
        t.checkExpect(temp.bottom, exWorld.board.get(i + 1));
      }
    }
    // Checks all cells in the board except the first to make sure they
    // are not flooded.
    for (int i = 1; i < exBoard.size(); i++) {
      t.checkExpect(exBoard.get(i).flooded, false);
    }
  }

  // Tests makeScene Method
  void testMakeScene(Tester t) {
    initWorld();
    WorldScene finalScene = new WorldScene(1200, 800);
    WorldImage outline = 
        new RectangleImage(80 , 80,
            OutlineMode.SOLID, new Color(42, 64, 99));
    finalScene.placeImageXY(new FromFileImage("./background.jpg"), 600, 400);
    finalScene.placeImageXY(
        new TextImage(0 + "    ", 30, FontStyle.BOLD_ITALIC, Color.CYAN), 1000, 450);
    finalScene.placeImageXY(new TextImage("/ " 
        + Integer.toString(this.exWorld2.required), 30, FontStyle.BOLD_ITALIC,
        Color.CYAN), 1030, 450);
    finalScene.placeImageXY(new TextImage("FLOOD-IT", 50,
        FontStyle.BOLD_ITALIC, Color.MAGENTA), 600, 650);
    finalScene.placeImageXY(outline, 90, 90);
    finalScene.placeImageXY(new TextImage("Time: " + exWorld2.time / 10 + "s", 35,
        FontStyle.BOLD_ITALIC, Color.CYAN), 1020, 600);
    finalScene.placeImageXY(cBlue.image(), 80, 80);
    finalScene.placeImageXY(cGreen.image(), 80, 100);
    finalScene.placeImageXY(cRed.image(), 100, 80);
    finalScene.placeImageXY(cYellow.image(), 100, 100);
    t.checkExpect(exWorld2.makeScene(), finalScene);
  }

  // Tests for updateOnClick
  void testUpdateOnClick(Tester t) {
    initWorld();
    exWorld2.board = new ArrayList<Cell>();
    exWorld2.board.add(cBlue);
    t.checkExpect(exWorld2.board.get(0), cBlue);
    exWorld2.updateOnClick(cRed);
    t.checkExpect(exWorld2.board.get(0).color, "RED");
  }

  // Tests for onMouseClicked method
  void testOnMouseClicked(Tester t) {
    initWorld();
    exWorld2.makeScene();
    ArrayList<Cell> test = exWorld2.board;
    t.checkExpect(test.get(0), cBlue);
    t.checkExpect(exWorld2.clicks, 0);
    exWorld2.onMouseClicked(new Posn(0, 0));
    t.checkExpect(test.get(0), cBlue);
    t.checkExpect(exWorld2.clicks, 0);
    exWorld2.onMouseClicked(new Posn(100, 100));
    t.checkExpect(test.get(0).color, "YELLOW");
    t.checkExpect(exWorld2.clicks, 1);
    exWorld2.onMouseClicked(new Posn(100, 100));
    t.checkExpect(exWorld2.clicks, 2);
  }

  // Tests on key event
  void testOnKeyEvent(Tester t) {
    initWorld();
    exWorld2.createCells(FloodItWorld.testBoardSize);
    ArrayList<Cell> tester = new ArrayList<Cell>();
    tester = exWorld2.board;
    int testBdSize = FloodItWorld.testBoardSize;
    t.checkExpect(exWorld2.boardSize == 2, true);
    t.checkExpect(exWorld2.colorsUsed == 3, true);
    t.checkExpect(testBdSize == 6, true);
    t.checkExpect(exWorld2.board.equals(tester), true);
    exWorld2.onKeyEvent("a");
    t.checkExpect(exWorld2.boardSize == 2, true);
    t.checkExpect(exWorld2.colorsUsed == 3, true);
    t.checkExpect(testBdSize == 6, true);
    t.checkExpect(exWorld2.board.equals(tester), true);
    exWorld2.onKeyEvent("r");
    t.checkExpect(exWorld2.boardSize == 2, true);
    t.checkExpect(exWorld2.colorsUsed == 3, true);
    t.checkExpect(testBdSize == 6, true);
    t.checkExpect(exWorld2.board.equals(tester), false);
  }

  // Tests for allFlooded method
  void testAllFlooded(Tester t) {
    initWorld();
    t.checkExpect(exWorld2.allFlooded(), false);
    for (Cell c: exWorld2.board) {
      c.flooded = true;
    }
    t.checkExpect(exWorld2.allFlooded(), true);
  }

  // Tests for updateWorld method
  // updateWorld is the only method/action called/taken by 
  // the onTick method and therefore serves as tests for onTick as well.
  void testUpdateWorld(Tester t) {
    initWorld();
    t.checkExpect(exWorld2.board.get(0), cBlue);
    exWorld2.board.get(1).flooded = true;
    exWorld2.board.get(2).flooded = true;
    exWorld2.updateWorld();
    t.checkExpect(exWorld2.board.get(1).color, "BLUE");
    t.checkExpect(exWorld2.board.get(2).color, "BLUE");
  }

  // Runs the game
  // The player gives the values for grid size and number of colors used.
  void testGame(Tester t) {
    FloodItWorld w = new FloodItWorld(20, 6);
    t.checkException(
        new IllegalArgumentException("Number of colors cannot exceed 8"),
        w, "startGame", 20, 9);
    // Modify game inputs here.
    // Arguments: Grid Size & Number of colors
    w.startGame(20, 6);
    t.checkExpect(w.boardSize, 20);
    t.checkExpect(w.colorsUsed, 6);
    t.checkExpect(w.clicks, 0);
    t.checkExpect(w.required, 36);
  }
}
