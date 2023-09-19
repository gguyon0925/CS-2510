import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.awt.Color;

import javalib.impworld.*;
import javalib.worldimages.*;
import tester.*;

// Things we did for Extra Credit
// 1) Randomizing color palates: Every time the board is initialized, a random color theme is 
//    chosen from a list of possible themes, there are 6 total color themes. Yes, we have named
//    them: Rainbow, Colorblind Mode, Sad Boi Hours, Minecraft Creeper mode, Frank's Red Hot, 
//    and Camo!
// 2) Displaying the Moves Counter: We keep track of how many moves have been done and display it
//    onto the board.
// 3) Keeping track of and displaying the time: Every single tick, the tick rate is added to the 
//    state of the world. We display the time on the board in a "MM:SS.SS" format. 
// 4) Health Bar: A rectangle is displayed right adjacent to the board, and with every turn, it 
//    decreases in length and the color gradually gradients from green to red.
// 5) Background Music: We allowed for audio to be played during the implementation of the game. 
//    If the user presses "Enter", the song Loving is Easy by Rex Orange County will play (Clean 
//    version from his Tonight Show appearance!). 
// 6) Random Sound Effects: In addition, for every time the user starts a flood, there 
//    is a 20% chance a random sound effect from FloodGame.SOUND_EFFECTS will be played.
// 7) End Screens: If you win, a special someone will appear with a congratulatory sound effect and
//    an effort affirming message across the screen. If you lose, a popular meme audio clip will 
//    play suggesting the user try to get the win next time, and an emotional child appears. 

//====UTILS========================================================================================
class Utils {
  // Allows for songs to be accessed via file name and be played, the song will loop if given true
  public static Clip playSound(String fileName, boolean loop) {
    Clip clip = null;
    try {
      AudioInputStream audioInputStream = AudioSystem
          .getAudioInputStream(Utils.class.getClass().getResource(fileName));
      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      if (loop) {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
      }
      else {
        clip.start();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return clip;
  }
}

//====FLOODGAME====================================================================================
// Represents a FloodIt game configured by an ArrayList of Cells that represents the board, a 
// boolean representing if the game is currently flooding, an int representing the grid size, 
// a random seed for helping test, the number of colors the game is based upon, the number of total 
// moves the user has to fill the board, the current move the user is on, the current flood color, 
// the ArrayList of possible colors the cells can be, a double representing how much time has 
// passed, and a Clip representing the background music 
class FloodGame extends World {
  ArrayList<Cell> board;
  boolean isFlooding;
  int gridSize;
  Random rand;
  int numColors;
  int totalMoves;
  int currentMoves;
  HashSet<Cell> cellsToFlood = new HashSet<Cell>();
  Color floodColor;
  ArrayList<Color> possibleColors;
  double time;
  Clip backgroundMusic;

  // CONSTRUCTORS===================================================================================

  // These constants are for help generating the random color palatte of the game
  static final ArrayList<Color> POSSIBLE_COLORS1 = new ArrayList<Color>(
      Arrays.asList(Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN));

  static final ArrayList<Color> POSSIBLE_COLORS2 = new ArrayList<Color>(Arrays.asList(Color.BLACK,
      Color.LIGHT_GRAY, Color.WHITE, Color.DARK_GRAY, Color.GRAY, new Color(150, 150, 150)));

  static final ArrayList<Color> POSSIBLE_COLORS3 = new ArrayList<Color>(
      Arrays.asList(Color.BLUE, Color.CYAN, new Color(100, 100, 255), new Color(10, 10, 100),
          new Color(0, 0, 20), new Color(50, 50, 150)));

  static final ArrayList<Color> POSSIBLE_COLORS4 = new ArrayList<Color>(
      Arrays.asList(Color.GREEN, new Color(150, 255, 150), new Color(0, 150, 0),
          new Color(0, 50, 0), new Color(0, 200, 0), new Color(100, 200, 100)));

  static final ArrayList<Color> POSSIBLE_COLORS5 = new ArrayList<Color>(
      Arrays.asList(Color.RED, Color.ORANGE, Color.YELLOW, new Color(50, 0, 0),
          new Color(200, 100, 100), new Color(255, 200, 200)));

  static final ArrayList<Color> POSSIBLE_COLORS6 = new ArrayList<Color>(
      Arrays.asList(new Color(50, 50, 10), new Color(100, 100, 20), new Color(150, 150, 30),
          new Color(200, 200, 40), new Color(250, 250, 50), new Color(200, 200, 150)));

  static final ArrayList<ArrayList<Color>> POSSIBLE_PALLATES = new ArrayList<ArrayList<Color>>(
      Arrays.asList(POSSIBLE_COLORS1, POSSIBLE_COLORS2, POSSIBLE_COLORS3, POSSIBLE_COLORS4,
          POSSIBLE_COLORS5, POSSIBLE_COLORS6));

  // Constants for the configuration of the game
  static final int CELL_SIZE = 50;
  static final int FONT_SIZE = 15;

  // An arrayList of random sound effects that can be played
  static final ArrayList<String> SOUND_EFFECTS = new ArrayList<String>(
      Arrays.asList("/monsterkill.wav", "/wilhelm.wav"));

  // For testing the game with a deterministic random seed
  public FloodGame(int gridSize, int numColors, Random rand) {
    FloodGame.assertInRange(numColors, 2, FloodGame.POSSIBLE_COLORS1.size(), "Number of colors");
    FloodGame.assertInRange(gridSize, 2, 24, "Grid size");
    this.numColors = numColors;
    this.gridSize = gridSize;
    this.isFlooding = false;
    this.rand = rand;
    this.totalMoves = this.totalMoves();
    this.possibleColors = this.generatePalatte();
    this.initializeBoard(this.possibleColors);
  }

  // Throws an exception if the given number is not in the interval [low, high]
  public static void assertInRange(int num, int low, int high, String name) {
    if (num < low || num > high) {
      throw new IllegalArgumentException(
          String.format("%s must be between %d and %d", name, low, high));
    }
  }

  // For testing the game by setting fields manually
  public FloodGame(ArrayList<Cell> board, boolean isFlooding, int gridSize, Random rand,
      int numColors, int currentMoves, int totalMoves, HashSet<Cell> cellsToFlood, Color floodColor,
      ArrayList<Color> possibleColors, int time) {
    this.board = board;
    this.isFlooding = isFlooding;
    this.gridSize = gridSize;
    this.rand = rand;
    this.numColors = numColors;
    this.currentMoves = currentMoves;
    this.totalMoves = totalMoves;
    this.cellsToFlood = cellsToFlood;
    this.floodColor = floodColor;
    this.possibleColors = possibleColors;
    this.time = time;
  }

  // For starting the game in the BigBang
  public FloodGame(int gridSize, int numColors) {
    this(gridSize, numColors, new Random());
  }

  // INITIALIZATION=================================================================================
  // Initializes the board for a new game, and resets the move counter and time
  void initializeBoard(ArrayList<Color> possColors) {
    this.currentMoves = 0;
    this.time = 0;
    this.board = new ArrayList<Cell>(this.gridSize * this.gridSize);
    for (int y = 0; y < this.gridSize; y++) {
      for (int x = 0; x < this.gridSize; x++) {
        ArrayList<Cell> neighbors = new ArrayList<Cell>(4);
        if (this.boardHas(x - 1, y)) {
          neighbors.add(this.getCell(x - 1, y));
        }
        if (this.boardHas(x + 1, y)) {
          neighbors.add(this.getCell(x + 1, y));
        }
        if (this.boardHas(x, y - 1)) {
          neighbors.add(this.getCell(x, y - 1));
        }
        if (this.boardHas(x, y + 1)) {
          neighbors.add(this.getCell(x, y + 1));
        }
        this.board.add(new Cell(x, y, neighbors, this.randomColor(possColors)));
      }
    }
  }

  // Generates the total number of moves the user is allowed to make before losing
  int totalMoves() {
    return (int) Math.floor(25 * ((this.gridSize * 2) * this.numColors) / ((14 * 2) * 6));
  }

  // Returns a random Color palate that the cells on the board will be colored
  ArrayList<Color> generatePalatte() {
    return FloodGame.POSSIBLE_PALLATES.get(this.rand.nextInt(FloodGame.POSSIBLE_PALLATES.size()));
  }

  // Returns a random color from a subset of the list of possible colors (bounded by the total
  // number of colors used)
  Color randomColor(ArrayList<Color> possColors) {
    return possColors.get(this.rand.nextInt(this.numColors));
  }

  // Is the given x,y pair a valid cell location?
  boolean boardHas(int x, int y) {
    return x >= 0 && x < this.gridSize && y >= 0 && y < this.gridSize
        && (this.board.size() > this.getIndex(x, y));
  }

  // Returns the cell at the given x,y location
  Cell getCell(int x, int y) {
    return this.board.get(this.getIndex(x, y));
  }

  // Returns the index in the board of the cell with the given x,y
  int getIndex(int x, int y) {
    return x + y * this.gridSize;
  }

  // DRAWING========================================================================================

  // Returns the image of the board
  WorldImage drawBoard() {
    int boardSize = FloodGame.CELL_SIZE * this.gridSize;
    WorldImage bg = new RectangleImage(boardSize, boardSize, OutlineMode.SOLID, Color.BLACK);
    for (Cell c : this.board) {
      bg = c.draw(bg);
    }
    return bg;
  }

  // Returns the image of the score bar
  WorldImage drawScore() {
    String score = String.format("Moves: %s / %s", this.currentMoves, this.totalMoves);
    WorldImage scoreImg = new TextImage(score, FloodGame.FONT_SIZE, Color.MAGENTA);
    WorldImage rect = new RectangleImage(FloodGame.CELL_SIZE * this.gridSize, FloodGame.CELL_SIZE,
        OutlineMode.SOLID, Color.BLACK);
    return new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, scoreImg, 0, 0, rect);
  }

  // Returns the image of the time bar
  WorldImage drawTime() {
    String currentTime = String.format("Time : %02d:%05.2f", (int) (this.time / 60),
        (this.time % 60));
    WorldImage timeImg = new TextImage(currentTime, FloodGame.FONT_SIZE, Color.MAGENTA);
    WorldImage rect = new RectangleImage(FloodGame.CELL_SIZE * this.gridSize, FloodGame.CELL_SIZE,
        OutlineMode.SOLID, Color.BLACK);
    return new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, timeImg, 0, 0, rect);
  }

  // Draws the image of the health bar (whose color is dependent on how many moves the user
  // has left. Will Gradient from green to red).
  WorldImage drawHealthBar() {
    double movesRatio = (this.totalMoves - this.currentMoves) / (double) this.totalMoves;
    int fullBarHeight = (int) (FloodGame.CELL_SIZE * (this.gridSize + 3));
    int barHeight = (int) (fullBarHeight * movesRatio);
    Color barColor = new Color((int) (255 * (1 - movesRatio)), (int) (255 * movesRatio), 0);

    WorldImage bgRect = new RectangleImage(2 * FloodGame.CELL_SIZE, fullBarHeight,
        OutlineMode.SOLID, Color.BLACK);
    WorldImage healthRect = new RectangleImage((int) (2 * FloodGame.CELL_SIZE), barHeight,
        OutlineMode.SOLID, barColor);
    healthRect = new FrameImage(healthRect);

    return new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, healthRect, 0, 0, bgRect);
  }

  // Returns the image of the board
  WorldImage drawMusic() {
    WorldImage musicImg = new TextImage("Press ENTER to start music", FloodGame.FONT_SIZE,
        Color.MAGENTA);
    WorldImage rect = new RectangleImage(FloodGame.CELL_SIZE * this.gridSize, FloodGame.CELL_SIZE,
        OutlineMode.SOLID, Color.BLACK);
    return new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, musicImg, 0, 0, rect);
  }

  // Generates the WorldScene for the current state of the world
  public WorldScene makeScene() {
    WorldImage gameImage = new AboveImage(this.drawBoard(), this.drawScore(), this.drawTime(),
        this.drawMusic());
    gameImage = new BesideImage(gameImage, this.drawHealthBar());
    WorldScene ws = new WorldScene((int) gameImage.getWidth(), (int) gameImage.getHeight());
    ws.placeImageXY(gameImage, (int) gameImage.getWidth() / 2, (int) gameImage.getHeight() / 2);
    return ws;
  }

  // =ONMOUSEPRESSED================================================================================
  // Handle the mouse press that occurred at the given position
  // EFFECT: Starts a flood if the click occurred at a valid location
  public void onMousePressed(Posn pos) {
    if (this.isFlooding) {
      return;
    }
    int x = pos.x / FloodGame.CELL_SIZE;
    int y = pos.y / FloodGame.CELL_SIZE;
    if (this.boardHas(x, y)) {
      this.doFlood(this.getCell(x, y));
    }
  }

  // chooses a random sound effect from the FloodGame.SOUND_EFFECTS list once
  // Sound method cannot be tested
  public void playRandomEffect() {
    if (this.rand.nextFloat() < 0.2) {
      Utils.playSound(
          FloodGame.SOUND_EFFECTS.get(this.rand.nextInt(FloodGame.SOUND_EFFECTS.size())), false);
    }
  }

  // Given a cell that has been selected, flood the board appropriately
  // EFFECT: If the given cell is a different color than the existing flood,
  // change the flooding state and increment the move counter
  public void doFlood(Cell c) {
    this.floodColor = c.color;
    if (!this.floodColor.equals(this.getCell(0, 0).color)) {
      this.playRandomEffect();
      this.cellsToFlood = new HashSet<Cell>(Arrays.asList(this.getCell(0, 0)));
      this.isFlooding = true;
      this.currentMoves++;
    }
  }

  // =====ONTICK====================================================================================
  // Runs every tick of the world
  // EFFECT: Updates flooding state and floods all cells that are set to be flooded this tick
  public void onTick() {
    HashSet<Cell> newCells = new HashSet<Cell>();
    for (Cell c : this.cellsToFlood) {
      c.flood(this.floodColor, newCells);
    }
    this.isFlooding = (newCells.size() != 0);
    this.cellsToFlood = newCells;
    this.time += (1.0 / 30.0);
  }

  // Handles key events
  // EFFECT: Initializes the board if the r key is pressed and the game is not in a flooding state
  // EFFECT: Plays Loving is Easy by Rex Orange County if the enter key is pressed and there
  // is no current audio being played (Clean version dont worry)
  public void onKeyEvent(String key) {
    if (key.equals("r") && !this.isFlooding) {
      this.initializeBoard(this.generatePalatte());
    }
    if (key.equals("enter")) {
      if (backgroundMusic == null) {
        this.backgroundMusic = Utils.playSound("/lie.wav", true);
      }
    }
  }

  // ===STOPWHEN===================================================================================
  // Is the current game won? (Are the colors of the cells in the board the same as the current
  // floodColor?)
  boolean wonGame() {
    for (Cell c : this.board) {
      if (!c.color.equals(this.floodColor)) {
        return false;
      }
    }
    return true;
  }

  // Is the current game lost? (is the user out of moves?)
  // The user cannot officially lose until the game is no longer flooding
  boolean gameOver() {
    return (this.totalMoves - this.currentMoves <= 0 && !this.isFlooding);
  }

  // Returns a worldScene with the given WorldImage placed in the center
  static WorldScene toWorldScene(WorldImage wi) {
    WorldScene ws = new WorldScene((int) wi.getWidth(), (int) wi.getHeight());
    ws.placeImageXY(wi, (int) wi.getWidth() / 2, (int) wi.getHeight() / 2);
    return ws;
  }

  // Returns the scene to be displayed when the game is won (Thank you for a wonderful semester Prof
  // Shesh)
  // EFFECT: Plays a supportive winning audio clip
  WorldScene makeWinScene() {
    WorldImage amit = new FromFileImage("src/amit.jpg");
    WorldImage txt = new TextImage("WiNNER WINNER CHICKEN DINNER", FloodGame.FONT_SIZE * 3,
        Color.BLUE);
    WorldImage txtAndAmit = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, txt, 0, 40,
        amit);
    Utils.playSound("/win.wav", false);
    return FloodGame.toWorldScene(txtAndAmit);
  }

  // Returns the scene to be displayed when the game is lost
  // EFFECT: Plays a losing audio clip
  WorldScene makeLossScene() {
    WorldImage child = new FromFileImage("src/child.jpg");
    WorldImage txt = new TextImage("You deserve Nothing", 30, Color.BLUE);
    WorldImage txtAndChild = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE, txt, 0,
        40, child);
    Utils.playSound("/lose.wav", false);
    return FloodGame.toWorldScene(txtAndChild);
  }

  // Generates a WorldEnd instance for the current state of the world based on whether
  // the user has officially won or lost the game
  public WorldEnd worldEnds() {
    if (this.wonGame()) {
      if (backgroundMusic != null) {
        this.backgroundMusic.stop();
      }
      return new WorldEnd(true, this.makeWinScene());
    }
    else if (this.gameOver()) {
      if (backgroundMusic != null) {
        this.backgroundMusic.stop();
      }
      return new WorldEnd(true, this.makeLossScene());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

//====CELL=========================================================================================
// Represents a cell that exists in the game with a Color, an X and Y coordinate, and an 
// ArrayList of neighbor Cells
class Cell {
  Color color;
  int x;
  int y;
  ArrayList<Cell> neighbors;

  public Cell(int x, int y, ArrayList<Cell> neighbors, Color color) {
    this.x = x;
    this.y = y;
    this.neighbors = neighbors;
    this.color = color;
    for (Cell c : this.neighbors) {
      c.neighbors.add(this);
    }
  }

  // Returns the given background image with this cell drawn on it at the appropriate position
  public WorldImage draw(WorldImage bg) {
    return new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, this.color),
        -FloodGame.CELL_SIZE * this.x, -FloodGame.CELL_SIZE * this.y, bg);
  }

  // Floods this cell
  // EFFECT: Changes this cell's color to the given color
  // EFFECT: Adds all neighboring cells that should be flooded to the given set
  public void flood(Color c, HashSet<Cell> cellsToFlood) {
    for (Cell neighbor : this.neighbors) {
      if (neighbor.color.equals(this.color)) {
        cellsToFlood.add(neighbor);
      }
    }
    this.color = c;
  }

  public String toString() {
    return "[Color: " + this.color.toString() + ", (" + this.x + ", " + this.y + ")]";
  }
}

//====EXAMPLESFLOODIT==============================================================================
class ExamplesFloodIt {
  // EXAMPLES======================================================================================
  Random r;
  Cell c1;
  Cell c2;
  Cell c3;
  Cell c4;
  Cell c5;
  Cell c6;
  Cell c7;
  Cell c8;
  Cell c9;
  Cell c9a;
  
  Cell cA;
  Cell cB;
  Cell cC;
  Cell cD;
  Cell cE;
  Cell cF;
  Cell cG;
  Cell cH;
  Cell cI;

  ArrayList<Cell> b;
  ArrayList<Cell> b1;
  ArrayList<Cell> bA;
  FloodGame fg0;
  FloodGame fg1;
  FloodGame fga;

  WorldImage bg;
  WorldImage c1OnBoard;
  WorldImage c2OnBoard;
  WorldImage c3OnBoard;
  WorldImage c4OnBoard;
  WorldImage c5OnBoard;
  WorldImage c6OnBoard;
  WorldImage c7OnBoard;
  WorldImage c8OnBoard;
  WorldImage initBoardImg;

  WorldImage initHB;
  WorldScene initWS;

  // initializes the data for testing
  void initData() {
    this.c1 = new Cell(0, 0, new ArrayList<Cell>(), Color.RED);
    this.c2 = new Cell(1, 0, new ArrayList<Cell>(), Color.BLUE);
    this.c3 = new Cell(2, 0, new ArrayList<Cell>(), Color.BLUE);
    this.c4 = new Cell(0, 1, new ArrayList<Cell>(), Color.RED);
    this.c5 = new Cell(1, 1, new ArrayList<Cell>(), Color.BLUE);
    this.c6 = new Cell(2, 1, new ArrayList<Cell>(), Color.BLUE);
    this.c7 = new Cell(0, 2, new ArrayList<Cell>(), Color.RED);
    this.c8 = new Cell(1, 2, new ArrayList<Cell>(), Color.BLUE);
    this.c9 = new Cell(2, 2, new ArrayList<Cell>(), Color.BLUE);
    this.b = new ArrayList<Cell>(Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8, c9));

    c1.neighbors = new ArrayList<Cell>(Arrays.asList(c2, c4));
    c2.neighbors = new ArrayList<Cell>(Arrays.asList(c1, c3, c5));
    c3.neighbors = new ArrayList<Cell>(Arrays.asList(c2, c6));
    c4.neighbors = new ArrayList<Cell>(Arrays.asList(c1, c5, c7));
    c5.neighbors = new ArrayList<Cell>(Arrays.asList(c4, c2, c6, c8));
    c6.neighbors = new ArrayList<Cell>(Arrays.asList(c5, c3, c9));
    c7.neighbors = new ArrayList<Cell>(Arrays.asList(c4, c8));
    c8.neighbors = new ArrayList<Cell>(Arrays.asList(c7, c5, c9));
    c9.neighbors = new ArrayList<Cell>(Arrays.asList(c8, c6));
    
    this.cA = new Cell(0, 0, new ArrayList<Cell>(), new Color(0,255,255));
    this.cB = new Cell(1, 0, new ArrayList<Cell>(), Color.BLUE);
    this.cC = new Cell(2, 0, new ArrayList<Cell>(), Color.BLUE);
    this.cD = new Cell(0, 1, new ArrayList<Cell>(), new Color(0,255,255));
    this.cE = new Cell(1, 1, new ArrayList<Cell>(), new Color(0,255,255));
    this.cF = new Cell(2, 1, new ArrayList<Cell>(), Color.BLUE);
    this.cG = new Cell(0, 2, new ArrayList<Cell>(), Color.BLUE);
    this.cH = new Cell(1, 2, new ArrayList<Cell>(), new Color(0,255,255));
    this.cI = new Cell(2, 2, new ArrayList<Cell>(), Color.BLUE);

    cA.neighbors = new ArrayList<Cell>(Arrays.asList(cB, cD));
    cB.neighbors = new ArrayList<Cell>(Arrays.asList(cA, cC, cE));
    cC.neighbors = new ArrayList<Cell>(Arrays.asList(cB, cF));
    cD.neighbors = new ArrayList<Cell>(Arrays.asList(cA, cE, cG));
    cE.neighbors = new ArrayList<Cell>(Arrays.asList(cD, cB, cF, cH));
    cF.neighbors = new ArrayList<Cell>(Arrays.asList(cE, cC, cI));
    cG.neighbors = new ArrayList<Cell>(Arrays.asList(cD, cH));
    cH.neighbors = new ArrayList<Cell>(Arrays.asList(cG, cE, cI));
    cI.neighbors = new ArrayList<Cell>(Arrays.asList(cH, cF));
    this.bA = new ArrayList<Cell>(Arrays.asList(cA, cB, cC, cD, cE, cF, cG, cH, cI));

    this.c9a = new Cell(2, 2, new ArrayList<Cell>(), Color.BLUE);
    c9a.neighbors = new ArrayList<Cell>(Arrays.asList(c8, c6));
    this.b1 = new ArrayList<Cell>(Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8, c9a));

    this.fga = new FloodGame(b, false, 3, new Random(11), 2, 0, 3, new HashSet<Cell>(), null,
        FloodGame.POSSIBLE_COLORS1, 0);
    this.fg0 = new FloodGame(3, 2, new Random(11));
    this.fg1 = new FloodGame(b1, false, 3, new Random(11), 2, 0, 3, new HashSet<Cell>(), null,
        FloodGame.POSSIBLE_COLORS1, 0);

    // init board images
    this.bg = new RectangleImage(FloodGame.CELL_SIZE * fg0.gridSize,
        FloodGame.CELL_SIZE * fg0.gridSize, OutlineMode.SOLID, Color.BLACK);
    this.c1OnBoard = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, c1.color),
        -FloodGame.CELL_SIZE * c1.x, -FloodGame.CELL_SIZE * c1.y, bg);
    this.c2OnBoard = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, c2.color),
        -FloodGame.CELL_SIZE * c2.x, -FloodGame.CELL_SIZE * c2.y, c1OnBoard);
    this.c3OnBoard = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, c3.color),
        -FloodGame.CELL_SIZE * c3.x, -FloodGame.CELL_SIZE * c3.y, c2OnBoard);
    this.c4OnBoard = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, c4.color),
        -FloodGame.CELL_SIZE * c4.x, -FloodGame.CELL_SIZE * c4.y, c3OnBoard);
    this.c5OnBoard = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, c5.color),
        -FloodGame.CELL_SIZE * c5.x, -FloodGame.CELL_SIZE * c5.y, c4OnBoard);
    this.c6OnBoard = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, c6.color),
        -FloodGame.CELL_SIZE * c6.x, -FloodGame.CELL_SIZE * c6.y, c5OnBoard);
    this.c7OnBoard = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, c7.color),
        -FloodGame.CELL_SIZE * c7.x, -FloodGame.CELL_SIZE * c7.y, c6OnBoard);
    this.c8OnBoard = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, c8.color),
        -FloodGame.CELL_SIZE * c8.x, -FloodGame.CELL_SIZE * c8.y, c7OnBoard);
    this.initBoardImg = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
        new RectangleImage(FloodGame.CELL_SIZE, FloodGame.CELL_SIZE, OutlineMode.SOLID, c9.color),
        -FloodGame.CELL_SIZE * c9.x, -FloodGame.CELL_SIZE * c9.y, c8OnBoard);

    this.initWS = new WorldScene(
        (int) new BesideImage(
            new AboveImage(fg1.drawBoard(), fg1.drawScore(), fg1.drawTime(), fg1.drawMusic()),
            fg1.drawHealthBar()).getWidth(),
        (int) new BesideImage(
            new AboveImage(fg1.drawBoard(), fg1.drawScore(), fg1.drawTime(), fg1.drawMusic()),
            fg1.drawHealthBar()).getHeight());
    this.initWS.placeImageXY(
        new BesideImage(
            new AboveImage(fg1.drawBoard(), fg1.drawScore(), fg1.drawTime(), fg1.drawMusic()),
            fg1.drawHealthBar()),
        (int) new BesideImage(
            new AboveImage(fg1.drawBoard(), fg1.drawScore(), fg1.drawTime(), fg1.drawMusic()),
            fg1.drawHealthBar()).getWidth() / 2,
        (int) new BesideImage(
            new AboveImage(fg1.drawBoard(), fg1.drawScore(), fg1.drawTime(), fg1.drawMusic()),
            fg1.drawHealthBar()).getHeight() / 2);
  }

  // BIGBANG========================================================================================
  static final int BOARD_LENGTH = 18;

  // tests the big bang function
  FloodGame testFG = new FloodGame(5, 3);

  void testTest(Tester t) {
    testFG.bigBang(ExamplesFloodIt.BOARD_LENGTH * FloodGame.CELL_SIZE,
        ExamplesFloodIt.BOARD_LENGTH * FloodGame.CELL_SIZE + 200, 1.0 / 30.0);
  }

  // TESTS=========================================================================================
  // INITIALIZATION=================================================================================
  // tests the constructor for the FloodGame
  void testFloodGameConstructor(Tester t) {
    this.initData();
    t.checkConstructorException(
        new IllegalArgumentException("Number of colors" + " must be between 2 and 6"), "FloodGame",
        4, 8);
    t.checkConstructorException(
        new IllegalArgumentException("Grid size" + " must be between 2 and 24"), "FloodGame", 123,
        4);
  }

  // tests the total moves method(Tester t)
  void testTotalMoves(Tester t) {
    this.initData();
    t.checkExpect(fg0.totalMoves(), 1);
    t.checkExpect(new FloodGame(5, 5).totalMoves(), 7);
    t.checkExpect(new FloodGame(22, 3).totalMoves(), 19);
  }

  // tests the total generatePalatte method
  void testGeneratePalatte(Tester t) {
    this.initData();
    // the random seed 11 returns FloodGame.POSSIBLE_COLORS3
    t.checkExpect(fg0.generatePalatte(), FloodGame.POSSIBLE_COLORS3);
  }

  // tests the randomColor method
  void testRandomColor(Tester t) {
    this.initData();
    // the random seed 11 returns blue
    t.checkExpect(fg0.randomColor(FloodGame.POSSIBLE_COLORS1), Color.blue);
  }

  // tests the boardHas method
  void testBoardHas(Tester t) {
    this.initData();
    t.checkExpect(fg0.boardHas(0, 0), true);
    t.checkExpect(fg0.boardHas(4, 0), false);
    t.checkExpect(fg0.boardHas(1, 0), true);
    t.checkExpect(fg0.boardHas(-1, -1), false);
    t.checkExpect(fg0.boardHas(0, 3), false);
    t.checkExpect(fg0.boardHas(2, 2), true);
    t.checkExpect(fg0.boardHas(3, 3), false);
    t.checkExpect(fg0.boardHas(3, 0), false);
  }

  // tests the getCell method
  void testGetCell(Tester t) {
    this.initData();
    t.checkExpect(fg1.getCell(0, 0), c1);
    t.checkExpect(fg1.getCell(0, 1), c4);
    t.checkExpect(fg1.getCell(1, 0), c2);
    t.checkExpect(fg1.getCell(1, 1), c5);
    t.checkExpect(fg1.getCell(2, 2), c9);
  }

  //tests the initializeBoard method
  void testInitializeBoard(Tester t) {
    this.initData();
    t.checkExpect(fg0.board, b);
  }

  // tests the getIndex method
  void testGetIndex(Tester t) {
    this.initData();
    t.checkExpect(fg1.getIndex(0, 1), 3);
    t.checkExpect(fg1.getIndex(1, 0), 1);
    t.checkExpect(fg1.getIndex(0, 0), 0);
    t.checkExpect(fg1.getIndex(1, 1), 4);
    t.checkExpect(fg1.getIndex(2, 2), 8);
    t.checkExpect(fg1.getIndex(1, 2), 7);
  }

  // DRAWING========================================================================================
  // tests the draw method
  void testDraw(Tester t) {
    this.initData();
    t.checkExpect(c1.draw(bg), c1OnBoard);
    t.checkExpect(c2.draw(c1OnBoard), c2OnBoard);
    t.checkExpect(c3.draw(c2OnBoard), c3OnBoard);
    t.checkExpect(c4.draw(c3OnBoard), c4OnBoard);
    t.checkExpect(c9.draw(c8OnBoard), initBoardImg);
  }

  // tests the drawBoard method
  void testDrawBoard(Tester t) {
    this.initData();
    t.checkExpect(fg0.drawBoard(), initBoardImg);
  }

  // tests the drawHealthBar method
  void testDrawHealthBar(Tester t) {
    this.initData();
    t.checkExpect(fg1.drawHealthBar(), new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM,
        new FrameImage(new RectangleImage((int) (2 * FloodGame.CELL_SIZE),
            ((int) (((int) (FloodGame.CELL_SIZE * (fg1.gridSize + 3)))
                * ((fg1.totalMoves - fg1.currentMoves) / (double) fg1.totalMoves))),
            OutlineMode.SOLID,
            new Color(
                (int) (255 * (1 - ((fg1.totalMoves - fg1.currentMoves) / (double) fg1.totalMoves))),
                (int) (255 * ((fg1.totalMoves - fg1.currentMoves) / (double) fg1.totalMoves)), 0))),
        0, 0, new RectangleImage(2 * FloodGame.CELL_SIZE,
            ((int) (FloodGame.CELL_SIZE * (fg1.gridSize + 3))), OutlineMode.SOLID, Color.BLACK)));
  }

  // tests the drawScore method
  void testDrawScore(Tester t) {
    this.initData();
    t.checkExpect(fg1.drawScore(),
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
            new TextImage("Moves: 0 / 3", FloodGame.FONT_SIZE, Color.MAGENTA), 0, 0,
            new RectangleImage(FloodGame.CELL_SIZE * fg1.gridSize, FloodGame.CELL_SIZE,
                OutlineMode.SOLID, Color.BLACK)));
  }

  // tests the drawTime method
  void testDrawTime(Tester t) {
    this.initData();
    t.checkExpect(fg1.drawTime(),
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
            new TextImage("Time : 00:00.00", FloodGame.FONT_SIZE, Color.MAGENTA), 0, 0,
            new RectangleImage(FloodGame.CELL_SIZE * fg1.gridSize, FloodGame.CELL_SIZE,
                OutlineMode.SOLID, Color.BLACK)));
  }

  // tests the drawMusic method
  void testDrawMusic(Tester t) {
    this.initData();
    t.checkExpect(fg1.drawMusic(),
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
            new TextImage("Press ENTER to start music", FloodGame.FONT_SIZE, Color.MAGENTA), 0, 0,
            new RectangleImage(FloodGame.CELL_SIZE * fg1.gridSize, FloodGame.CELL_SIZE,
                OutlineMode.SOLID, Color.BLACK)));
  }

  // tests the makeScene method
  void testMakeScene(Tester t) {
    this.initData();
    t.checkExpect(fg1.makeScene(), this.initWS);
  }

  // ONMOUSE========================================================================================
  // tests the doFlood method
  void testDoFlood(Tester t) {
    this.initData();
    fg1.doFlood(fg1.getCell(0, 1));
    t.checkExpect(fg1.isFlooding, false);
    t.checkExpect(fg1.currentMoves, 0);
    t.checkExpect(fg1.cellsToFlood, new HashSet<Cell>());
    fg1.doFlood(fg1.getCell(1, 1));
    t.checkExpect(fg1.currentMoves, 1);
    t.checkExpect(fg1.isFlooding, true);
    t.checkExpect(fg1.cellsToFlood, new HashSet<Cell>(Arrays.asList(fg1.getCell(0, 0))));
  }

  // tests the onMousePressed method
  void testonMousePressed(Tester t) {
    this.initData();
    fg1.onMousePressed(new Posn(200, 200));
    t.checkExpect(fg1.currentMoves, 0);
    t.checkExpect(fg1.isFlooding, false);
    t.checkExpect(fg1.cellsToFlood, new HashSet<Cell>());
    fg1.onMousePressed(new Posn((FloodGame.CELL_SIZE * 3) / 2, (FloodGame.CELL_SIZE * 3) / 2));
    t.checkExpect(fg1.isFlooding, true);
    t.checkExpect(fg1.currentMoves, 1);
    t.checkExpect(fg1.cellsToFlood, new HashSet<Cell>(Arrays.asList(c1)));
    fg1.onMousePressed(new Posn((FloodGame.CELL_SIZE * 2 + (FloodGame.CELL_SIZE / 2)),
        (FloodGame.CELL_SIZE * 3) / 2));
    t.checkExpect(fg1.isFlooding, true);
    t.checkExpect(fg1.currentMoves, 1);
  }

  // ONTICK
  // =========================================================================================
  // tests the onTick method
  void testOnTick(Tester t) {
    this.initData();
    this.fga.onTick();
    t.checkExpect(this.fga.isFlooding, false);
    t.checkExpect(this.fga.cellsToFlood, new HashSet<Cell>());
    t.checkInexact(this.fga.time, (1.0 / 30.0), 0.001);
    this.fga.floodColor = Color.BLUE;
    this.fga.cellsToFlood = new HashSet<Cell>(Arrays.asList(this.fga.board.get(0)));
    this.fga.onTick();
    t.checkExpect(this.fga.isFlooding, true);
    t.checkExpect(this.fga.cellsToFlood, new HashSet<Cell>(Arrays.asList(this.fga.board.get(3))));
    t.checkInexact(this.fga.time, (1.0 / 30.0) * 2, 0.001);
  }

  // tests the flood method
  void testFlood(Tester t) {
    this.initData();
    HashSet<Cell> cellsToFlood = new HashSet<Cell>();
    t.checkExpect(this.fga.board.get(0).color, Color.RED);
    this.fga.board.get(0).flood(Color.BLUE, cellsToFlood);
    t.checkExpect(this.fga.board.get(0).color, Color.BLUE);
    t.checkSet(cellsToFlood, new HashSet<Cell>(Arrays.asList(this.fga.board.get(3))));

    this.initData();
    cellsToFlood = new HashSet<Cell>();
    t.checkExpect(this.fga.board.get(3).color, Color.RED);
    this.fga.board.get(3).flood(Color.BLUE, cellsToFlood);
    t.checkExpect(this.fga.board.get(3).color, Color.BLUE);
    t.checkSet(cellsToFlood,
        new HashSet<Cell>(Arrays.asList(this.fga.board.get(0), this.fga.board.get(6))));
  }

  // STOPWHEN
  // =======================================================================================
  // tests the wonGame method
  void testWonGame(Tester t) {
    this.initData();
    t.checkExpect(this.fg1.wonGame(), false);
    this.fg1.floodColor = Color.BLUE;
    for (Cell c : this.fg1.board) {
      c.color = Color.blue;
    }
    t.checkExpect(this.fg1.wonGame(), true);
  }

  // tests the gameOver method
  void testGameOver(Tester t) {
    this.initData();
    t.checkExpect(this.fg1.gameOver(), false);
    this.fg1.currentMoves = 3;
    t.checkExpect(this.fg1.gameOver(), true);
  }

  // tests the worldEnds method
  void testWorldEnds(Tester t) {
    this.initData();
    t.checkExpect(this.fg1.worldEnds(), new WorldEnd(false, this.fg1.makeScene()));
    this.fg1.currentMoves = 3;
    t.checkExpect(this.fg1.worldEnds(), new WorldEnd(true, this.fg1.makeLossScene()));
    this.fg1.floodColor = Color.BLUE;
    for (Cell c : this.fg1.board) {
      c.color = Color.blue;
    }
    t.checkExpect(this.fg1.worldEnds(), new WorldEnd(true, this.fg1.makeWinScene()));
  }

  // tests the toWorldScene method
  void testToWorldScene(Tester t) {
    this.initData();
    WorldScene ws = new WorldScene((int) this.bg.getWidth(), (int) this.bg.getHeight());
    ws.placeImageXY(this.bg, (int) this.bg.getWidth() / 2, (int) this.bg.getHeight() / 2);
    t.checkExpect(FloodGame.toWorldScene(this.bg), ws);
  }

  // tests the makeWinScene method
  void testMakeWinScene(Tester t) {
    this.initData();
    t.checkExpect(this.fg1.makeWinScene(),
        FloodGame.toWorldScene(new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
            new TextImage("WiNNER WINNER CHICKEN DINNER", FloodGame.FONT_SIZE * 3, Color.BLUE), 0,
            40, new FromFileImage("src/amit.jpg"))));
  }

  // tests the makeLossScene method
  void testMakeLossScene(Tester t) {
    this.initData();
    t.checkExpect(this.fg1.makeLossScene(),
        FloodGame.toWorldScene(new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
            new TextImage("You deserve Nothing", 30, Color.BLUE), 0, 40,
            new FromFileImage("src/child.jpg"))));
  }

  // tests the onKeyEvent Handler
  void testOnKeyEvent(Tester t) {
    this.initData();
    fg1.onKeyEvent("g");
    t.checkExpect(fg1.board, b1);
    fg0.onKeyEvent("r");
    t.checkExpect(fg0.board, bA);
  }
}