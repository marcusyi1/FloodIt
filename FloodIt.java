import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//A class to represent the game of Flood
class FloodItWorld extends World {
  // the size of the board
  int boardSize;
  // the number of colors, between 3-8
  int colors;
  // represents the total clicks a user hase
  // to complete the board
  int totalClicks;
  // represents how many times the user has clicked
  int countClicks = 0;
  // the time in seconds
  int timeSeconds = 0;
  // the current color
  Color thisColor;
  // if a user can click
  boolean canClick;
  //has all the possible board colors
  ArrayList<Color> boardColors;
  // holds cells for flooding
  ArrayList<Cell> cellList;
  // is the flood-it board
  ArrayList<ArrayList<Cell>> board;
  Random rand;

  // this is the normal game constructor
  FloodItWorld(int boardSize, int colors) {
    this.rand = new Random();
    this.boardSize = boardSize;
    this.colors = colors;
    this.boardColors = this.makeColors(colors);
    this.board = this.buildBoard();
    this.cellList = new ArrayList<Cell>();
    this.totalClicks = Math.round((25 * ((boardSize + boardSize) * colors)
        / 168));
    this.thisColor = this.board.get(0).get(0).color;
    this.canClick = true;
  }

  // this constructor is for testing because we have rand for the
  // new Random() input
  FloodItWorld(int boardSize, int colors, int rand) {
    this.boardSize = boardSize;
    this.colors = colors;
    this.rand = new Random(rand);
    this.boardColors = this.makeColors(colors);
    this.board = this.buildBoard();
    this.cellList = new ArrayList<Cell>();
    this.totalClicks = Math.round((25 * ((boardSize + boardSize) * colors)
        / 168));
    this.thisColor = this.board.get(0).get(0).color;
    this.canClick = true;
  }

  // Creates a world scene for flood-it
  public WorldScene makeScene() {
    WorldScene floodItWorld = this.buildWorld();
    int messagePosnX = boardSize * 20;
    int messagePosnY = boardSize * 35;
    int fontSize = boardSize * 2;
    boolean boardFlooded = true;
    WorldImage pressR =  new TextImage("Press r To Play Again", fontSize, Color.black);
    for (ArrayList<Cell> row : this.board) {
      for (Cell c : row) {
        if (c.color != this.thisColor) {
          boardFlooded = false;
        }
      }
    }
    if (!boardFlooded && this.countClicks >= this.totalClicks) {
      floodItWorld.placeImageXY(new TextImage("You Lost :(", fontSize, FontStyle.BOLD, Color.black),
          messagePosnX, messagePosnY + 5);
      floodItWorld.placeImageXY(pressR,messagePosnX, messagePosnY + fontSize * 2);
    }
    else if (boardFlooded && this.countClicks <= this.totalClicks) {
      floodItWorld.placeImageXY(
          new TextImage("You Won! :)", fontSize, FontStyle.BOLD, Color.black),
          messagePosnX, messagePosnY + fontSize);
      floodItWorld.placeImageXY(pressR, messagePosnX, messagePosnY + fontSize * 2);
    }
    floodItWorld.placeImageXY(new TextImage("Flood-It", fontSize + boardSize, FontStyle.BOLD,
        thisColor.darker().darker()), messagePosnX + 2, boardSize * 5 - 2);
    floodItWorld.placeImageXY(new TextImage("Flood-It", fontSize + boardSize, FontStyle.BOLD,
        thisColor), messagePosnX, boardSize * 5);
    floodItWorld.placeImageXY(new TextImage("Time: " + this.timeSeconds / 180 + " Seconds",
        fontSize - boardSize / 2, Color.black), messagePosnX, messagePosnY - boardSize * 2);
    floodItWorld.placeImageXY(new TextImage(this.countClicks + "/" + this.totalClicks, 
        fontSize - boardSize / 2,
        Color.black), messagePosnX, messagePosnY);

    return floodItWorld;
  }

  // changes the world based on mouse clicks
  // EFFECT: changes the world when mouse is clicked
  public void onMouseClicked(Posn pos) {
    if (this.canClick) {
      this.canClick = false;
      // Find the square clicked and set a the current color to that color
      for (ArrayList<Cell> alc : this.board) {
        for (Cell c : alc) {
          if (pos.x > c.x && pos.x < c.x + 20 && pos.y > c.y
              && pos.y < c.y + 20 && c.color != this.board.get(0).get(0).color) {
            this.thisColor = c.color;
            this.countClicks++;
            this.cellList.add(this.board.get(0).get(0));
          }
        }
      }
    }
  }

  // changes the world on tick
  public void onTick() {
    if (this.cellList.size() > 0) {
      // this makes the waterfall effect on tick
      cellList.addAll(cellList.get(0).waterfall(cellList.get(0).color));
      cellList.remove(0).color = this.thisColor;
    }
    else { 
      for (ArrayList<Cell> row : this.board) {
        for (Cell c : row) {
          c.flooded = false;
        } 
      }
      this.canClick = true;
      this.timeSeconds ++;
    }
  }

  //changes the world based on key event
  // EFFECT: restarts the game and its elements when r pressed
  public void onKeyEvent(String s) {
    if (s.equals("r")) {
      this.board = this.buildBoard();
      this.countClicks = 0;
      this.timeSeconds = 0;
      this.thisColor = this.board.get(0).get(0).color;
      this.canClick = true;
    }
  }

  // makes the color list needed for game play
  ArrayList<Color> makeColors(int colors) {
    ArrayList<Color> allColors = new ArrayList<Color>(Arrays.asList(
        new Color(0, 76, 153), new Color(0, 153, 153),  new Color(0, 204, 0), 
        new Color(220, 53, 53), new Color(255, 153, 51),  new Color(255, 255, 53),
        new Color(178, 102, 255), new Color(255, 102, 178)));
    if (colors < 3) {
      throw new IllegalArgumentException("Cannot have less than 3 colors");
    }
    if (colors > 8) {
      throw new IllegalArgumentException("Cannot have more than 8 colors");
    }
    ArrayList<Color> colorList = new ArrayList<Color>();
    for (int i = 0; i < colors; i = i + 1) {
      colorList.add(allColors.get(i));
    }
    return colorList;
  }

  //creates the flood it by building the board of cells
  WorldScene buildWorld() {
    // the + 1 is so a black box outline isn't visible
    WorldScene buildBoard = new WorldScene(boardSize * 40 + 1, boardSize * 40 + 1);
    WorldImage blackOutline = new RectangleImage(boardSize * 20 + 25, boardSize * 20 + 25,
        OutlineMode.SOLID, Color.BLACK);
    WorldImage colorOutline = new RectangleImage(boardSize * 20 + 50, boardSize * 20 + 50,
        OutlineMode.SOLID, this.thisColor);
    // this adds a fun color changing outline
    buildBoard.placeImageXY(colorOutline, boardSize * 20, boardSize * 20);
    buildBoard.placeImageXY(blackOutline, boardSize * 20, boardSize * 20);
    for (int i = 0; i < this.boardSize; i++) {
      for (int j = 0; j < this.boardSize; j++) {
        Cell thisCell = this.board.get(i).get(j);
        buildBoard.placeImageXY(thisCell.drawCell(), thisCell.x, thisCell.y);
      }
    }
    return buildBoard;
  }

  //builds the flood it board
  ArrayList<ArrayList<Cell>> buildBoard() {
    ArrayList<ArrayList<Cell>> floodBoard = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < this.boardSize; i++) {
      ArrayList<Cell> row = new ArrayList<Cell>();
      for (int j = 0; j < this.boardSize; j++) {
        row.add(new Cell(20 * j + (this.boardSize * 20 / 2), 20 * i + (this.boardSize 
            * 20 / 2),
            boardColors.get(this.rand.nextInt(this.boardColors.size())), false));
      }
      floodBoard.add(row);
    }
    return this.neighborCells(floodBoard);
  }

  //links neighbor cells together with the given ArrayList in ArrayLists
  ArrayList<ArrayList<Cell>> neighborCells(ArrayList<ArrayList<Cell>> neighborList) {
    for (int i = 0; i < neighborList.size(); i++) {
      for (int j = 0; j < neighborList.size(); j++) {
        Cell left = null;
        Cell top = null;
        Cell right = null;
        Cell bottom = null;
        if (j != 0) {
          left = neighborList.get(i).get(j - 1);
        }
        if (j + 1 != neighborList.get(i).size()) {
          right = neighborList.get(i).get(j + 1);
        }
        if (i != 0) {
          top = neighborList.get(i - 1).get(j);
        }
        if (i + 1 != neighborList.size()) {
          bottom = neighborList.get(i + 1).get(j);
        }
        neighborList.get(i).get(j).linkCell(left, top, right, bottom);
      }
    }
    return neighborList;
  }
}

//Represents a single square of the game area
class Cell {
  //In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  //the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // The constructor that represents a single square of the game area
  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
  }

  //links the cells adjacent to it
  // EFFECT: links this Cells adjacent cells to input cells
  void linkCell(Cell left, Cell right, Cell top, Cell bottom) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  // draws a cell, and aligns it with a pinhole
  WorldImage drawCell() {
    return new RectangleImage(20, 20, OutlineMode.SOLID, this.color)
        // this aligns the colors correctly
        .movePinhole(-20 / 2, -20 / 2);
  }

  // this adds neighbor cells that are the same color
  // so the board can flood them like a waterfall
  ArrayList<Cell> waterfall(Color color) {
    ArrayList<Cell> neighborCells = new ArrayList<Cell>();
    if (!this.flooded) {
      this.flooded = true;
      neighborCells.add(this);

      if (this.right != null && this.right.color == color) {
        neighborCells.add(this.right);
      }
      if (this.left != null && this.left.color == color) {
        neighborCells.add(this.left);
      }
      if (this.top != null && this.top.color == color) {
        neighborCells.add(this.top);
      }
      if (this.bottom != null && this.bottom.color == color) {
        neighborCells.add(this.bottom);
      }
    }
    return neighborCells;
  }
}

// Represents the examples for flood-It
class ExamplesFloodIt {
  // a flood it world example
  FloodItWorld floodExample;
  FloodItWorld floodExample1;
  FloodItWorld floodExample14;
  // this has the random int input
  FloodItWorld floodExampleRand;
  FloodItWorld floodExampleRand1;
  // all the cell colors
  Cell cRed;
  Cell cOrange;
  Cell cYellow;
  Cell cYellowRand;
  Cell cGreen;
  Cell cGreenRand;
  Cell cTeal;
  Cell cBlue;
  Cell cPurple;
  Cell cPink;
  //the list of cells we expect from floodExample
  ArrayList<ArrayList<Cell>> cellsExample;
  //the list of cells we expect from floodExample1
  ArrayList<ArrayList<Cell>> cellsExample1;
  //the list of cells we expect from floodExampleRand
  ArrayList<ArrayList<Cell>> cellsExampleRand;
  //the list of cells we expect from floodExampleRand
  ArrayList<ArrayList<Cell>> cellsExampleRand1;


  //initializes the flood data
  //EFFECT: initializes the examples
  void initData() {
    floodExample = new FloodItWorld(2, 3);
    floodExample1 = new FloodItWorld(2, 4);
    floodExample14 = new FloodItWorld(14, 3);
    floodExampleRand = new FloodItWorld(1, 4, 6);
    floodExampleRand1 = new FloodItWorld(1, 8, 2);
    cRed = new Cell(20, 20, new Color(220, 53, 53), false);
    cOrange = new Cell(20, 0, new Color(255, 153, 51), false);
    cYellow = new Cell(0, 20, new Color(255, 255, 53), false);
    cYellowRand = new Cell(10, 10, new Color(255, 255, 53), false);
    cGreen = new Cell(20, 0, new Color(0, 204, 0), false);
    cGreenRand = new Cell(10, 10, new Color(0, 204, 0), false);
    cTeal = new Cell(20, 20, new Color(0, 153, 153), false);
    cBlue = new Cell(40, 20, new Color(0, 76, 153), false);
    cPurple = new Cell(0, 20, new Color(178, 102, 255), false);
    cPink = new Cell(20, 0, new Color(255, 102, 178), false);
    cellsExample = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> row = new ArrayList<Cell>();
    row.add(cRed);
    row.add(cOrange);
    cellsExample.add(row);
    ArrayList<Cell> col = new ArrayList<Cell>();
    col.add(cYellow);
    col.add(cGreen);
    cellsExample.add(col);
    cellsExample1 = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> row1 = new ArrayList<Cell>();
    row1.add(cTeal);
    row1.add(cBlue);
    cellsExample1.add(row1);
    ArrayList<Cell> col1 = new ArrayList<Cell>();
    col1.add(cPurple);
    col1.add(cPink);
    cellsExample1.add(col1);
    // this is for testing with random
    cellsExampleRand = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> rowNeighbor = new ArrayList<Cell>();
    rowNeighbor.add(cGreenRand);
    cellsExampleRand.add(rowNeighbor);
    // another rand test board
    cellsExampleRand1 = new ArrayList<ArrayList<Cell>>();
    ArrayList<Cell> rowNeighbor1 = new ArrayList<Cell>();
    rowNeighbor1.add(cYellowRand);
    cellsExampleRand1.add(rowNeighbor1);
  }

  //tests the makeScene method
  void testMakeScene(Tester t) {
    initData();
    WorldScene makeBoard = floodExample.buildWorld();
    makeBoard.placeImageXY(new TextImage("Flood-It", 6, FontStyle.BOLD,
        floodExample.thisColor.darker().darker()), 42, 8);
    makeBoard.placeImageXY(new TextImage( "Flood-It", 6, FontStyle.BOLD,
        floodExample.thisColor), 40, 10);
    makeBoard.placeImageXY(
        new TextImage("Time: " + 0 + " Seconds", 3, Color.black), 40, 66);
    makeBoard.placeImageXY(new TextImage( 0 + "/" + 1, 3, Color.black),
        40, 70);
    WorldScene makeBoard1 = floodExample1.buildWorld();
    makeBoard1.placeImageXY(new TextImage("Flood-It", 6, FontStyle.BOLD,
        floodExample1.thisColor.darker().darker()), 42, 8);
    makeBoard1.placeImageXY(new TextImage("Flood-It", 6, FontStyle.BOLD,
        floodExample1.thisColor), 40, 10);
    makeBoard1.placeImageXY(
        new TextImage("Time: " + 0 + " Seconds", 3, Color.black), 40, 66);
    makeBoard1.placeImageXY(new TextImage( 0 + "/" + 2, 3, Color.black),
        40, 70);
    t.checkExpect(floodExample.makeScene(), makeBoard);
    t.checkExpect(floodExample1.makeScene(), makeBoard1);
    t.checkExpect(floodExample.totalClicks, 1);
    t.checkExpect(floodExample1.totalClicks, 2);
    t.checkExpect(floodExample.countClicks, 0);
    t.checkExpect(floodExample1.countClicks, 0);
    t.checkExpect(floodExample.timeSeconds, 0);
    t.checkExpect(floodExample1.timeSeconds, 0);
  }

  //tests the buildWorld method
  void testBuildWorld(Tester t) {
    initData();
    WorldScene floodScene = new WorldScene(floodExample.boardSize * 40 + 1, 
        floodExample.boardSize * 40 + 1);
    WorldImage blackOutline = new RectangleImage(floodExample.boardSize * 20 
        + 25, floodExample.boardSize * 20 + 25,
        OutlineMode.SOLID, Color.BLACK);
    WorldImage colorOutline = new RectangleImage(floodExample.boardSize * 20 
        + 50, floodExample.boardSize * 20 + 50,
        OutlineMode.SOLID, floodExample.thisColor);
    floodScene.placeImageXY(colorOutline, floodExample.boardSize * 20, 
        floodExample.boardSize * 20);
    floodScene.placeImageXY(blackOutline, floodExample.boardSize * 20, 
        floodExample.boardSize * 20);
    for (int i = 0; i < floodExample.boardSize; i++) {
      for (int j = 0; j < floodExample.boardSize; j++) {
        t.checkExpect(floodExample.board.get(i).get(j).flooded, false);
        Cell current = floodExample.board.get(i).get(j);
        floodScene.placeImageXY(current.drawCell(), current.x, current.y);
      }
    }
    WorldScene floodScene2 = new WorldScene(floodExample1.boardSize * 40 + 1,
        floodExample1.boardSize * 40 + 1);
    WorldImage blackOutline1 = new RectangleImage(floodExample1.boardSize * 20 
        + 25, floodExample1.boardSize * 20 + 25,
        OutlineMode.SOLID, Color.BLACK);
    WorldImage colorOutline1 = new RectangleImage(floodExample1.boardSize * 20 
        + 50, floodExample.boardSize * 20 + 50,
        OutlineMode.SOLID, floodExample1.thisColor);
    floodScene2.placeImageXY(colorOutline1, floodExample1.boardSize * 20, 
        floodExample1.boardSize * 20);
    floodScene2.placeImageXY(blackOutline1, floodExample1.boardSize * 20, 
        floodExample1.boardSize * 20);
    for (int i = 0; i < floodExample1.boardSize; i++) {
      for (int j = 0; j < floodExample1.boardSize; j++) {
        t.checkExpect(floodExample.board.get(i).get(j).flooded, false);
        Cell current = floodExample1.board.get(i).get(j);
        floodScene2.placeImageXY(current.drawCell(), current.x, current.y);
      }
    }
    WorldScene floodScene3 = new WorldScene(floodExample.boardSize * 40 + 1,
        floodExample14.boardSize * 40 + 1);
    for (int i = 0; i < floodExample14.boardSize; i++) {
      for (int j = 0; j < floodExample14.boardSize; j++) {
        t.checkExpect(floodExample14.board.get(i).get(j).flooded, false);
        Cell current = floodExample14.board.get(i).get(j);
        floodScene3.placeImageXY(current.drawCell(), current.x, current.y);
      }
    }
    t.checkExpect(floodExample.buildWorld(), floodScene);
    t.checkExpect(floodExample1.buildWorld(), floodScene2);
  }

  //tests the waterfall method
  void testWaterfall(Tester t) {
    initData();
    ArrayList<Cell> cellList = new ArrayList<Cell>();
    cellList.add(floodExample1.board.get(0).get(0));
    floodExample.board.get(0).get(0).flooded = true;
    t.checkExpect(floodExample1.board.get(0).get(0).waterfall(Color.black), cellList);
    t.checkExpect(floodExample.board.get(0).get(0).waterfall(Color.green), 
        new ArrayList<Cell>());
  }

  //tests the makeColors method
  void testMakeColors(Tester t) {
    initData();
    ArrayList<Color> colorList = new ArrayList<Color>();
    colorList.add(new Color(0, 76, 153));
    colorList.add(new Color(0, 153, 153));
    colorList.add(new Color(0, 204, 0));
    t.checkExpect(floodExample.makeColors(3), colorList);
    colorList.add(new Color(220, 53, 53));
    colorList.add(new Color(255, 153, 51));
    colorList.add(new Color(255, 255, 53));
    colorList.add(new Color(178, 102, 255));
    colorList.add(new Color(255, 102, 178));
    t.checkExpect(floodExample.makeColors(8), colorList);
    t.checkException(new IllegalArgumentException("Cannot have less than 3 colors"),
        floodExample, "makeColors", 2);
    t.checkException(new IllegalArgumentException("Cannot have less than 3 colors"),
        floodExample, "makeColors", 0);
    t.checkException( new IllegalArgumentException("Cannot have more than 8 colors"),
        floodExample, "makeColors", 9);
    t.checkException( new IllegalArgumentException("Cannot have more than 8 colors"),
        floodExample, "makeColors", 100);
  }

  //tests the linkCell method
  void testLinkCell(Tester t) {
    initData();

    cRed.left = null;
    cRed.right = null;
    cRed.top = cOrange;
    cRed.bottom = cYellow;
    Cell cRedNeighbors = new Cell(20, 20, new Color(220, 53, 53), false);
    cRedNeighbors.linkCell(null, null, cOrange, cYellow);
    t.checkExpect(cRedNeighbors, cRed);
    cGreen.left = cBlue;
    cGreen.right = cGreen;
    cGreen.top = cBlue; 
    cGreen.bottom = cPink;
    Cell cGreenNeighbors = new Cell(20, 0, new Color(0, 204, 0), false);
    cGreenNeighbors.linkCell(cBlue, cGreen, cBlue, cPink);
    t.checkExpect(cGreenNeighbors, cGreen);
  }

  //tests the drawCell method
  void testdrawCell(Tester t) {
    initData();
    t.checkExpect(cRed.drawCell(),
        new RectangleImage(20, 20, "solid", new Color(220, 53, 53))
        .movePinhole(-10, -10));
    t.checkExpect(cOrange.drawCell(),
        new RectangleImage(20, 20, "solid", new Color(255, 153, 51))
        .movePinhole(-10, -10));
    t.checkExpect(cYellow.drawCell(),
        new RectangleImage(20, 20, "solid", new Color(255, 255, 53))
        .movePinhole(-10, -10));
    t.checkExpect(cGreen.drawCell(),
        new RectangleImage(20, 20, "solid", new Color(0, 204, 0))
        .movePinhole(-10, -10));
    t.checkExpect(cTeal.drawCell(),
        new RectangleImage(20, 20, "solid", new Color(0, 153, 153))
        .movePinhole(-10, -10));
    t.checkExpect(cBlue.drawCell(),
        new RectangleImage(20, 20, "solid", new Color(0, 76, 153))
        .movePinhole(-10, -10));
    t.checkExpect(cPurple.drawCell(),
        new RectangleImage(20, 20, "solid", new Color(178, 102, 255))
        .movePinhole(-10, -10));
    t.checkExpect(cPink.drawCell(),
        new RectangleImage(20, 20, "solid", new Color(255, 102, 178))
        .movePinhole(-10, -10));
  }

  //tests the neighborCells method
  void testNeighborCells(Tester t) {
    initData();
    // tests using Random
    t.checkExpect(floodExampleRand.board, 
        floodExampleRand.neighborCells(cellsExampleRand));
    t.checkExpect(floodExampleRand1.board, 
        floodExampleRand1.neighborCells(cellsExampleRand1));
  }

  //tests the onTick method
  void testOnTick(Tester t) {
    initData();
    FloodItWorld floodEx = new FloodItWorld(2, 3);
    floodEx.onTick();
    floodEx.timeSeconds = 1;
    t.checkExpect(floodEx.timeSeconds, 1);
    floodEx.onTick();
    t.checkExpect(floodEx.timeSeconds, 2);
  }

  //tests the on mouseClicked method
  void testOnMouseClicked(Tester t) {
    initData();
    floodExample1.onMouseClicked(new Posn(0, 0));
    t.checkExpect(floodExample1, floodExample1);
    floodExample1.onMouseClicked(new Posn(100, 100));
    floodExample1.thisColor = new Color(0, 153, 153);
    floodExample1.totalClicks += 1;
    floodExample1.board.get(0).get(0).flooded = true;
    floodExample1.cellList.add(floodExample1.board.get(0).get(0));
    floodExample1.canClick = false;
    t.checkExpect(floodExample1, floodExample1);
  }

  //tests the onKeyEvent Method
  void testOnKeyEvent(Tester t) {
    initData();
    t.checkExpect(floodExample, floodExample);
    floodExample.onKeyEvent("s");
    t.checkExpect(floodExample, floodExample);
    floodExample.onKeyEvent("r");
    //FloodItWorld worldGame1example = new FloodItWorld(2, 3);
    t.checkExpect(floodExample, floodExample);

  }

  // Runs the game
  void testGame(Tester t) {
    // You can modify the game here
    // first int is board size
    // second int is number of colors
    // you can pick 3 - 8 colors

    FloodItWorld floodIt = new FloodItWorld(14, 8);
    if (floodIt.boardSize > 26 || floodIt.boardSize < 2) {
      throw new RuntimeException("Please pick a boardsize between 2 and 26");
    }
    floodIt.bigBang(floodIt.boardSize * 40, floodIt.boardSize * 40, 0.005);
  }
}
