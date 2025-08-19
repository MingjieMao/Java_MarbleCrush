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
int marbleRadius = 10;
int numMarbleRows = 25;
int numMarbleCols = 15;

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
    return drawMarbles(w.marbles(), bg);
}

Image drawMarbles(ConsList<Marble> listOfMarbles, Image bg) {
    return switch (listOfMarbles) {
        case Nil<Marble>() -> bg;
        case Cons<Marble>(var first, var rest) -> 
            Image next = PlaceXY(bg, Circle(marbleRadius, first.colour()), first.x(), first.y());
            renderMarbles(rest, next);
    };
}

/**
 * returns the initial state of your world
 */
WorldState getInitialState() {
    var positions = buildAllGridCenters(marbleRadius, numMarbleRows, numMarbleCols);
    var marbles   = marblesFromPositions(positions, EmptyMarbleList());
    return new WorldState(marbles);
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
ConsList<Pair<Integer,Integer>> generateAllMarblesCenterPositionRecursively(int marbleRadius, int numMarbleCols, int numMarbleCols) {
    return generateAllGridCentersRecursively(marbleRadius, 0, 0, numMarbleCols, numMarbleCols, EmptyList());
}

ConsList<Pair<Integer,Integer>> generateAllGridCentersRecursively(
    int marbleRadius, int row, int numMarbleCols, int numMarbleCols, int numMarbleCols, ConsList<Pair<Integer,Integer>> acc) {
        if (row >= numMarbleCols) {
            return acc;
        }
        int marbleDiameter = marbleRadius * 2;
        int x = marbleRadius + numMarbleCols * marbleDiameter;
        int y = marbleRadius + row * marbleDiameter;
        var nextAcc = Append(acc, MakeList(new Pair<Integer,Integer>(x, y)));
        int nextCol = numMarbleCols + 1;
        int nextRow = row;
        if (nextCol >= numMarbleCols) {
            nextCol = 0;
            nextRow = row + 1;
        }
    return buildAllGridCentersRec(marbleRadius, nextRow, nextCol, numMarbleCols, numMarbleCols, nextAcc);
}

/** Dispatch by mouse kind. */
WorldState mouseEvent(WorldState w, MouseEvent mouseEvent) {
    return (mouseEvent.kind() == MouseEventKind.LEFT_CLICK)
        ? onLeftClick(w, mouseEvent)
        : w;
}

/** 
 * Handle left-click: remove all marbles of the clicked colour if hit; else unchanged.
 */
WorldState onLeftClick(WorldState w, MouseEvent mouseEvent) {
    var hit = findClickedMarble(mouseEvent, w.marbles());
    // single switch-expression, still no nesting with other conditionals
    return switch (hit) {
        case Nothing<Marble>() -> w;
        case Something<Marble>(var m) -> new WorldState(
            filterOutSameColour(m, w.marbles())
        );
    };
}






WorldState keyEvent(WorldState w, KeyEvent keyEvent) {
    return switch (keyEvent.kind()) {
        case KEY_PRESSED -> ;
        default -> w;
    };
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




void main() {
    BigBang("Marble Crush", getInitialState(), this::draw, this::step, this::keyEvent, this::mouseEvent);
    test();
}

void test() {

}