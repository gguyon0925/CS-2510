import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.Tester;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

/*
 * You will need to create a two-dimensional grid of these Cells to represent the board.
Since the game should be easily configurable, you should have a 
constructor that takes in two numbers: the size of the board and the number of colors.
Because the size of your board is determined by this constructor, 
you cannot simply hard-code lists of data, because they may be of the wrong size. 
Instead, you’ll need to use loops and ArrayLists.
You will need to implement the makeScene method in 
the FloodItWorld class to be able to render the game state. Note that 
although makeScene returns a WorldScene in the impworld library, the placeImageXY method is now a void method.
 (The other event handlers, which you will need to use in Part 2, are also void methods now.)
For Part 1, as always, your code should be well-designed and well-tested.
 */
abstract class ACell {
  // the four adjacent cells to this one
  ACell left;
  ACell top;
  ACell right;
  ACell bottom;
  
  abstract WorldImage tileToImage();
}

//Represents a single square of the game area
class Cell extends ACell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;

  Cell (int x, int y, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
  }
  
  WorldImage tileToImage() {
    return new RectangleImage(10, 10, OutlineMode.SOLID, this.color);
  }
}

// represents an empty cell outside the game area
class MtCell extends ACell {
  
  WorldImage tileToImage() {
    return null; // should probably throw an exception here
  }
}

class FloodItWorld extends World {
  
  // All the cells of the game
  ArrayList<ArrayList<ACell>> boardCol;
  ArrayList<ACell> boardRow;

  int size;
  int numColors;
  ArrayList<Color> loColors;

  FloodItWorld(int size, int numColors) {
    this.size = size;
    this.numColors = numColors;
    this.loColors = new ArrayList<Color>(Arrays.asList(Color.red, Color.orange, Color.yellow, 
        Color.green, Color.blue, Color.pink, Color.magenta));

    createGrid();
  }

  //create a 2D array representing the game board
  void createGrid() {
    boardCol = new ArrayList<ArrayList<ACell>>(size);

    for (int i = 0; i < this.size; i++) {
      boardRow = new ArrayList<ACell>(size);
      boardCol.set(i, this.boardRow);
    }

    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        this.boardCol.get(i).set(j, newRandCell(i, j));
      }
    }
    
    updateCells();
  }

  // create a new random cell at location x, y
  Cell newRandCell(int x, int y) {
    Random rand = new Random();
    int randInt;
    do {
      randInt = rand.nextInt(this.numColors);
    } while (randInt >= this.loColors.size());

    return new Cell(x, y, this.loColors.get(randInt));
  }
  
  void updateCells() {
    // loop thru whole list
    for (int i = 0; i < this.size; i++) {
      for (int j = 0; j < this.size; j++) {
        if (i == 0) {
          // top row
          this.boardCol.get(i).get(j).top = new MtCell();
          // rest
          this.boardCol.get(i).get(j).bottom = this.boardCol.get(i + 1).get(j);
          this.boardCol.get(i).get(j).left = this.boardCol.get(i).get(j - 1);
          this.boardCol.get(i).get(j).right = this.boardCol.get(i).get(j + 1);
        } else if (i == this.size - 1) {
          // bottom row
          this.boardCol.get(i).get(j).bottom = new MtCell();
          // rest
          this.boardCol.get(i).get(j).top = this.boardCol.get(i - 1).get(j);
          this.boardCol.get(i).get(j).left = this.boardCol.get(i).get(j - 1);
          this.boardCol.get(i).get(j).right = this.boardCol.get(i).get(j + 1);
        } else if (j == 0) {
          // left column
          this.boardCol.get(i).get(j).left = new MtCell();
          // rest
          this.boardCol.get(i).get(j).top = this.boardCol.get(i - 1).get(j);
          this.boardCol.get(i).get(j).bottom = this.boardCol.get(i + 1).get(j);
          this.boardCol.get(i).get(j).right = this.boardCol.get(i).get(j + 1);
        } else if (j == this.size - 1) {
          // right column
          this.boardCol.get(i).get(j).right = new MtCell();
          // rest
          this.boardCol.get(i).get(j).top = this.boardCol.get(i - 1).get(j);
          this.boardCol.get(i).get(j).bottom = this.boardCol.get(i + 1).get(j);
          this.boardCol.get(i).get(j).left = this.boardCol.get(i).get(j - 1);
        } else {
          // middle
          this.boardCol.get(i).get(j).top = this.boardCol.get(i - 1).get(j);
          this.boardCol.get(i).get(j).bottom = this.boardCol.get(i + 1).get(j);
          this.boardCol.get(i).get(j).left = this.boardCol.get(i).get(j - 1);
          this.boardCol.get(i).get(j).right = this.boardCol.get(i).get(j + 1);
        }
      }
    }
  }

  public WorldScene makeScene() {
    WorldScene world = new WorldScene(this.size * 10, this.size * 10);
    
    // add all cells to one image
    WorldImage allCells = new EmptyImage();
    for (int i = 0; i < this.size; i++) {
      WorldImage thisRow = new EmptyImage();
      for (int j = 0; j < this.size; j++) {
        thisRow = new BesideImage(this.boardCol.get(i).get(j).tileToImage(), thisRow);
      }
      allCells = new AboveImage(allCells, thisRow);
    }
    
    // place image in the world
    world.placeImageXY(allCells, 0, 0);
    
    return world;
  }
}

class ExamplesFlood {
  // implement big bang here?

  
}