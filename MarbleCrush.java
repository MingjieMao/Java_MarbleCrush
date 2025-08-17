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
int BALL_RADIUS = 10;
int BALL_DIAM = BALL_RADIUS * 2;
int WORLD_COLS = WORLD_WIDTH / DIAM;   // 15 columns
int WORLD_ROWS = WORLD_HEIGHT / DIAM;  // 25 rows

/**
 * [C]: our own colour enumeration for marbles (not the Universe Colour)
 */
enum ColourC { 
    BLUE, 
    RED, 
    GREEN, 
    BLACK 
}

ColourC randomColour() {
    int r = RandomNumber(0,4);
    return switch (r) {
        case 0 -> ColourC.BLUE;
        case 1 -> ColourC.RED;
        case 2 -> ColourC.GREEN;
        default -> ColourC.BLACK;
    };
}


/**
 * [M]: a marble with its centre (x,y) in pixels and a colour
 */
record Marble(int x, int y, ColourC colour) {}


/**
 * [W]: world state is just the list of marbles currently present
 */
record World(ConsList<Marble> marbles) {}



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



World step(World w) {
    return w;
}

Image draw(World w) {
    Image bg = Rectangle(WORLD_WIDTH, WORLD_HEIGHT, WHITE());
    return drawList(w.marbles(), bg);
}

Image drawList(ConsList<Marble> ms, Image base) {
    return switch (ms) {
        case Nil<Marble>() -> base;
        case Cons<Marble>(var h, var t) -> ;
    };
}

World mouseEvent(World w, MouseEvent mouseEvent) {
    return switch (mouseEvent.kind()) {
        case LEFT_CLICKED -> ;
        default -> w;
    };
}

World keyEvent(World w, KeyEvent keyEvent) {
    return switch (keyEvent.kind()) {
        case KEY_PRESSED -> ;
        default -> w;
    };
}

World getInitialState() {
    return ;
}

/**
 * returns Something with the marble at pixel coordinate (x, y) 
 * or Nothing if no marble is there
 */
Maybe<Marble> getMarbleAt(World w, int x, int y) {
    return ;
}

/**
 * returns the colour of a given marble
 */
ColourC getColour(Marble m) {
    return m.colour();
}

/**
 * returns the number of marbles of a given colour
 */
int numberOfMarblesOfColour(World w, ColourC c) {
    return ;
}

/**
 * returns the number of empty locations in the grid of marbles
 */
int numberOfEmptyLocations(World w) {
    return ;
}




void main() {
    BigBang("Marble Crush", getInitialState(), this::draw, this::step, this::keyEvent, this::mouseEvent);
    test();
}

void test() {

}