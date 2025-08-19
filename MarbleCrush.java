import comp1110.universe.*;
import static comp1110.universe.Colour.*;
import static comp1110.universe.Image.*;
import static comp1110.universe.Universe.*;
import static comp1110.lib.Functions.*;
import comp1110.lib.*;
import comp1110.lib.Date;
import static comp1110.testing.Comp1110Unit.*;

/* The width and height of the world (in pixels) */
int WORLD_WIDTH = 300;
int WORLD_HEIGHT = 500;

/* Radius of the balls */
int RADIUS = 10;
int ROWS = 25;
int COLS = 15;

/**
 * [C]: our own colour enumeration for marbles (not the Universe Colour)
 */
enum MarbleColour { 
    BLUE, 
    RED, 
    GREEN, 
    BLACK 
}

MarbleColour randomColour() {
    int r = RandomNumber(0,4);
    return switch (r) {
        case 0 -> MarbleColour.BLUE;
        case 1 -> MarbleColour.RED;
        case 2 -> MarbleColour.GREEN;
        default -> MarbleColour.BLACK;
    };
}

/**
 * [M]: a marble with its centre (x,y) in pixels and a colour
 */
record Marble(int x, int y, MarbleColour colour) {}


/**
 * [W]: world state is just the list of marbles currently present
 */
record WorldState(ConsList<Marble> marbles) {}


WorldState step(WorldState w) {
    return w;
}

Image draw(WorldState w) {
    Image bg = Rectangle(WORLD_WIDTH, WORLD_HEIGHT, WHITE());
    return drawList(w.marbles(), bg);
}

Image drawList(ConsList<Marble> ms, Image base) {
    return switch (ms) {
        case Nil<Marble>() -> base;
        case Cons<Marble>(var h, var t) -> ;
    };
}

WorldState mouseEvent(WorldState w, MouseEvent mouseEvent) {
    return switch (mouseEvent.kind()) {
        case LEFT_CLICKED -> ;
        default -> w;
    };
}

WorldState keyEvent(WorldState w, KeyEvent keyEvent) {
    return switch (keyEvent.kind()) {
        case KEY_PRESSED -> ;
        default -> w;
    };
}

/**
 * 
 */
WorldState getInitialState() {
    var positions = buildAllGridCenters(RADIUS, ROWS, COLS);
    var marbles   = marblesFromPositions(positions, EmptyMarbleList());
    return new WorldState(marbles);
}

/**
 * 
 */
ConsList<Pair<Integer,Integer>> buildAllGridCenters(int radius, int rows, int cols) {
    return buildAllGridCentersRec(radius, 0, 0, rows, cols, EmptyList());
}

ConsList<Pair<Integer,Integer>> buildAllGridCentersRec(
    int radius, int row, int col, int rows, int cols, ConsList<Pair<Integer,Integer>> acc) {
        if (row >= rows) {
            return acc;
        }
        int diameter = radius * 2;
        int x = radius + col * diameter;
        int y = radius + row * diameter;
        var nextAcc = Append(acc, MakeList(new Pair<Integer,Integer>(x, y)));
        int nextCol = col + 1;
        int nextRow = row;
        if (nextCol >= cols) {
            nextCol = 0;
            nextRow = row + 1;
        }
    return buildAllGridCentersRec(radius, nextRow, nextCol, rows, cols, nextAcc);
}

/**
 * 
 */
ConsList<Marble> marblesFromPositions(ConsList<Pair<Integer,Integer>> listOfPositions, ConsList<Marble> acc) {
    return switch (listOfPositions) {
        case Nil<Pair<Integer,Integer>>() -> acc;
        case Cons<Pair<Integer,Integer>>(var first, var rest) -> 
            marblesFromPositions(rest, Append(acc, MakeList(new Marble(first.left(), first.right(), randomColour()))));
    }
}


/**
 * returns Something with the marble at pixel coordinate (x, y) 
 * or Nothing if no marble is there
 */
Maybe<Marble> getMarbleAt(WorldState w, int x, int y) {
    return ;
}

/**
 * returns the colour of a given marble
 */
MarbleColour getColour(Marble m) {
    return m.colour();
}

/**
 * returns the number of marbles of a given colour
 */
int numberOfMarblesOfColour(WorldState w, MarbleColour c) {
    return ;
}

/**
 * returns the number of empty locations in the grid of marbles
 */
int numberOfEmptyLocations(WorldState w) {
    return ;
}

/**
* Generates a list of pairs with the coordinates of the centers of the marbles 
* laid out in a two-dimensional grid of marbles with numMarbleRows rows and numMarbleCols columns. 
* The marbles are positioned to be touching the adjacent marbles in the grid, 
* with no space in between them and no overlapping. 
* The positions of the marble centers are ordered in row-major order,i.e., first by rows, and then by columns. 
* The origin of coordinates is assumed to be placed in the lower left corner of 
* the bounding box of the marble positioned in the lower left corner of the grid of marbles. 
* The x-axis points right, and the y-axis points down.
* Design strategy: Iteration (week 5/6)
* Example:
*   - Given: marbleRadius=10, numMarbleRows=2, numMarbleCols=3
*   - Expect: [(10,10),(30,10),(50,10),(10,30),(30,30),(50,30)]
* @param marbleRadius The radius of the marble in pixels (>0)
* @param numMarbleRows The number of rows in the 2D grid of marbles (>0)
* @param numMarbleCols The number of columns in the 2D grid of marbles (>0)
* @return A list with the coordinates of the marble centers in the 2D grid
*/ 
ConsList<Pair<Integer,Integer>> generateAllMarblesCenterPositionRecursively(int marbleRadius, int numMarbleRows, int numMarbleCols) {
    ConsList<Pair<Integer,Integer>> outputList = new Nil<>();
    int marbleDiameter = marbleRadius*2;
    int currentCenterCoordY = marbleRadius;
    for (int i=0; i<numMarbleRows; i++) {
        int currentCenterCoordX = marbleRadius;
        for (int j=0; j<numMarbleCols; j++) {
            outputList = Append(outputList,
                                MakeList(new Pair<Integer,Integer>(currentCenterCoordX, currentCenterCoordY)));
            currentCenterCoordX+=marbleDiameter;
        }
        currentCenterCoordY+=marbleDiameter;
    }
    return outputList;
}



void main() {
    BigBang("Marble Crush", getInitialState(), this::draw, this::step, this::keyEvent, this::mouseEvent);
    test();
}

void test() {

}