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

/**
 * 
 */
MarbleColour randomColour() {
    int r = RandomNumber(0,4);
    return switch(r) {
        case 0 -> MarbleColour.BLUE;
        case 1 -> MarbleColour.RED;
        case 2 -> MarbleColour.GREEN;
        default -> MarbleColour.BLACK;
    };
}

Colour convertToColour(MarbleColour marbleColour) {
    return switch (marbleColour) {
        case BLUE -> Colour.BLUE;
        case RED -> Colour.RED;
        case GREEN -> Colour.GREEN;
        case BLACK -> Colour.BLACK;
    };
}

/**
 * [M]: a marble with its centre (x,y) in pixels and a colour
 */
record Marble(int x, int y, MarbleColour colour) {}
/**
 * ... marble.x() ...
 * ... marble.y() ...
 * ... marble.colour() ...
 */

/**
 * [W]: world state is just the list of marbles currently present
 */
record WorldState(ConsList<Marble> marbles) {}
/**
 * ... w.marbles() ...
 */

WorldState step(WorldState w) {
    return w;
}

/**
 * 
 */
Image draw(WorldState w) {
    Image bg = Rectangle(WORLD_WIDTH, WORLD_HEIGHT, WHITE);
    return drawMarbles(w.marbles(), bg);
}

Image drawMarbles(ConsList<Marble> listOfMarbles, Image bg) {
    return switch (listOfMarbles) {
        case Nil<Marble>() -> bg;
        case Cons<Marble>(Marble first, ConsList<Marble> rest) -> 
            drawMarbles(rest, PlaceXY(bg, Circle(marbleRadius, convertToColour(first.colour())), first.x(), first.y()));
    };
}

/**
 * returns the initial state of your world
 */
WorldState getInitialState() {
    ConsList<Pair<Integer,Integer>> positions = 
        generateAllMarblesCenterPositionRecursively(marbleRadius, numMarbleRows, numMarbleCols);
    ConsList<Marble> marbles = marblesFromPositions(positions, MakeList());
    return new WorldState(marbles);
}

/**
 * 
 */
ConsList<Marble> marblesFromPositions(ConsList<Pair<Integer,Integer>> listOfPositions, ConsList<Marble> acc) {
    return switch (listOfPositions) {
        case Nil<Pair<Integer,Integer>>() -> acc;
        case Cons<Pair<Integer,Integer>>(Pair<Integer,Integer> p, ConsList<Pair<Integer,Integer>> rest) -> 
            marblesFromPositions(rest, Append(acc, MakeList(new Marble(p.first(), p.second(), randomColour()))));
    };
}

/**
 * 1. Handle SPACE key
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Handle a keyboard event on the world state.  
 * - If the SPACE key is pressed, generate new marbles at all vacant positions;  
 * - Otherwise, leave the world unchanged.
 * 
 * Examples:
 * - We have a world with has 300 marbles and 75 vacancies, total 15*25=375 marbles.
 *   Pressing SPACE, return a new world with 375 marbles.
 *   Pressing any other key, return w unchanged.
 * 
 * Design Strategy: Case Distinction (on KeyEvent)
 * 
 * @param w the current WorldState
 * @param keyEvent the keyboard event to handle
 * @return a new WorldState reflecting the event
 */
WorldState keyEvent(WorldState w, KeyEvent keyEvent) {
    println("keyEvent kind: " + keyEvent.kind());
    println("Current marble count: " + Length(w.marbles()));
    return processKeyEvent(w, keyEvent.kind(), keyEvent.key());
}

/**
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Handle a keyboard event on the world state.  
 * - If the SPACE key is pressed, firstly we need to find vacancies, 
 *   than add new marbles to the vacancies,
 *   finally get new marbles at all vacant positions;  
 * - Otherwise, leave the world unchanged.
 * 
 * Examples:
 * - Given: processKeyEvent(w, KEY_PRESSED, "Space") 
 *   Expect: new WorldState with added marbles
 * 
 * Design Strategy: Case Distinction
 * 
 * @param w the current WorldState
 * @param keyEvent the keyboard event to handle
 * @param key the actual key string (e.g., "Space")
 * @return a new WorldState after applying the event
 */
WorldState processKeyEvent(WorldState w, KeyEventKind keyEventKind, String key) {
    boolean isSpace = (keyEventKind == KeyEventKind.KEY_PRESSED) && Equals(key, "Space");
    if (isSpace) {
        ConsList<Pair<Integer,Integer>> vacancies = findVacancies(w.marbles());
        ConsList<Marble> added = addMarblesAtVacancies(vacancies, w.marbles());
        return new WorldState(added);
    } else {
        return w;
    }
}

/** 
 * 1.1 Find all vacant positions
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Firstly, we hava a world with all possible coordinates (generated recursively).
 *   Then, find the coordinates of existing marbles.
 *   Return positions are currently empty (not occupied by a marble).
 * 
 * Examples:
 * - Given: Suppose numMarbleRows = 2, numMarbleCols = 2, marbleRadius = 1.
 *          allPositions = [(1,1), (1,2), (2,1), (2,2)]
 *          listOfMarbles = [(2,2)]  (occupiedPositions)
 *   Expect: findVacancies(list) = [(1,1), (1,2), (2,1)]
 *
 * Design Strategy: Combining Functions
 * 
 * @param listOfMarbles the current marbles in the world
 * @return a ConsList of (x,y) coordinate pairs representing vacant positions
*/
ConsList<Pair<Integer,Integer>> findVacancies(ConsList<Marble> listOfMarbles) {
    ConsList<Pair<Integer,Integer>> allPositions = 
        generateAllMarblesCenterPositionRecursively(marbleRadius, numMarbleRows, numMarbleCols);
    ConsList<Pair<Integer,Integer>> occupiedPositions = 
        positionsFromMarbles(listOfMarbles);
    return subtractPositions(allPositions, occupiedPositions); 
}

/**
* 1.1.1 
* Problem analysis and data design
* Function purpose statement and signature
* - Generates a list of pairs with the coordinates of the centers of the marbles 
*   laid out in a two-dimensional grid of marbles with numMarbleRows rows and numMarbleCols columns. 
* - The marbles are positioned to be touching the adjacent marbles in the grid, 
*   with no space in between them and no overlapping. 
* - The positions of the marble centers are ordered in row-major order,i.e., 
*   first by rows, and then by columns. 
* - The x-axis points right, and the y-axis points down.
* 
* Example:
*   - Given: marbleRadius=10, numMarbleRows=2, numMarbleCols=3
*   - Expect: [(10,10),(30,10),(50,10),(10,30),(30,30),(50,30)]
* 
* Design strategy: Case distinction (Recursion) and Combining functions

* @param marbleRadius The radius of the marble in pixels (>0)
* @param numMarbleRows The number of rows in the 2D grid of marbles (>0)
* @param numMarbleCols The number of columns in the 2D grid of marbles (>0)
* @return A list with the coordinates of the marble centers in the 2D grid
*/ 
ConsList<Pair<Integer,Integer>> generateAllMarblesCenterPositionRecursively(int marbleRadius, int numMarbleRows, int numMarbleCols) {
    return generateAllGridCentersRecursively(marbleRadius, 0, 0, numMarbleRows, numMarbleCols, MakeList());
}

/**
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Recursive helper to generate marble center coordinates row by row.  
 * - Uses accumulator to build the list increasely.
 * 
 * Example:
 *   - Given: marbleRadius=10, numMarbleRows=2, numMarbleCols=3
 *   - Expect: [(10,10),(30,10),(50,10),(10,30),(30,30),(50,30)]
 * 
 * Design Strategy: Case distinction (Recursion) and Combining functions
 * 
 * @param marbleRadius the radius of each marble
 * @param row current row index (0-based)
 * @param col current column index (0-based)
 * @param numMarbleRows total rows
 * @param numMarbleCols total cols
 * @param acc accumulated positions so far
 * @return completed ConsList of positions after traversal
 */
ConsList<Pair<Integer,Integer>> generateAllGridCentersRecursively(
    int marbleRadius, int row, int Col, int numMarbleRows, int numMarbleCols, ConsList<Pair<Integer,Integer>> acc) {
        if (row >= numMarbleRows) {
            return acc;
        }
        int marbleDiameter = marbleRadius * 2;
        int x = marbleRadius + Col * marbleDiameter;
        int y = marbleRadius + row * marbleDiameter;
        ConsList<Pair<Integer,Integer>> nextAcc = Append(acc, MakeList(new Pair<Integer,Integer>(x, y)));
        int nextCol = Col + 1;
        int nextRow = row;
        if (nextCol >= numMarbleCols) {
            nextCol = 0;
            nextRow = row + 1;
        }
        return generateAllGridCentersRecursively(marbleRadius, nextRow, nextCol, numMarbleRows, numMarbleCols, nextAcc);
}

/**
 * 1.1.2 
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Convert a list of Marble into a list of (x,y) coordinate pairs.  
 *   Preserves the original left-to-right order.
 * - Base case: if the marble list is empty (Nil), return an empty list of coordinates.
 * - Recursive case: if there is a Cons node (i.e., one marble + the remaining list):
 *   Take the current marble’s (x, y) coordinates and wrap them into a Pair.
 *   Use Append to add this coordinate to the front of the recursive result.
 *   Recursively call positionsFromMarbles(remainingMarbles) until all marbles are processed.
 * 
 * Examples:
 * - Given: (Empty list) positionsFromMarbles(Nil())
 *   Expect: Nil()
 * - Given: m1 = Marble(10, 20, Red), positionsFromMarbles(MakeList(m1))
 *   Expect: [(10,20)]
 * - Given: m1 = Marble(10, 10, Red), m2 = Marble(30, 10, Blue), m3 = Marble(50, 10, Green)
 *          positionsFromMarbles(MakeList(m1, m2, m3))
 *   Expect: [(10,10), (30,10), (50,10)]
 * 
 * Design Strategy: Template application（Recursion）
 * 
 * @param marbles the list of marbles (ConsList<Marble>)
 * @return a ConsList<Pair<Integer,Integer>> of (x,y) centers
 */
ConsList<Pair<Integer,Integer>> positionsFromMarbles(ConsList<Marble> marbles) {
  return switch (marbles) {
    case Nil<Marble>() -> MakeList();
    case Cons<Marble>(Marble marble, ConsList<Marble> rest) -> 
        Append(MakeList(new Pair<Integer,Integer>(marble.x(), marble.y())), positionsFromMarbles(rest));
  };
}

/** 
 * 1.1.3 
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Compute the list of vacant positions by subtracting occupied coordinates from all possible coordinates    
 *   Traverses allPositions recursively and excludes any position found in occupiedPositions.
 * - Base case: no remaining positions.
 * - Recursive case: current position + remaining positions.
 * 
 * Examples:
 * - Given: allPositions = [(10,10), (30,10), (50,10)]  
 *          occupiedPositions = [(30,10)]  
 *          subtractPositions(all, occupied) 
 *   Expect: [(10,10), (50,10)]
 * 
 * Design Strategy: Structural Template Application (Recursion on ConsList)
 * 
 * @param allPositions all possible grid positions (ConsList<Pair<Integer,Integer>>)  
 * @param occupiedPositions coordinates of marbles currently present (ConsList<Pair<Integer,Integer>>)  
 * @return a ConsList<Pair<Integer,Integer>> of positions that are vacant
 */
ConsList<Pair<Integer,Integer>> subtractPositions(ConsList<Pair<Integer,Integer>> allPositions, 
                                                  ConsList<Pair<Integer,Integer>> occupiedPositions) {
  return switch (allPositions) {
    case Nil<Pair<Integer,Integer>>() -> MakeList();
    case Cons<Pair<Integer,Integer>>(Pair<Integer,Integer> position, ConsList<Pair<Integer,Integer>> remaining) ->
      containsPos(occupiedPositions, position)
        ? subtractPositions(remaining, occupiedPositions)                              // occupied
        : Append(MakeList(position), subtractPositions(remaining, occupiedPositions)); // empty
  };
}

/**
 * Check whether a given target coordinate (x,y) exists in the positions list.
 * 
 * Examples:
 * - Given: positions = [(10,10), (20,20), (30,30)], target = (20,20)  
 *   Expect: containsPos(positions, target) = true
 * - Given: positions = [(10,10), (20,20), (30,30)], target = (40,40)  
 *   Expect: containsPos(positions, target) = false
 * 
 * Design Strategy: Template application (recursion) 
 * 
 * @param positions a list of coordinate pairs (ConsList<Pair<Integer,Integer>>)
 * @param target the coordinate pair to search for
 * @return true if target is in positions, false otherwise
 */
boolean containsPos(ConsList<Pair<Integer,Integer>> occupiedPositions, Pair<Integer,Integer> target) {
  return switch (occupiedPositions) {
    case Nil<Pair<Integer,Integer>>() -> false;
    case Cons<Pair<Integer,Integer>>(Pair<Integer,Integer> first, ConsList<Pair<Integer,Integer>> rest) -> 
        Equals(first, target) || containsPos(rest, target);
  };
}

/**
 * 1.2 
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Place new marbles into all vacant positions.  
 * - Takes a list of empty positions and adds a new Marble (with random colour) at each coordinate,
 *   appending them to the current list of marbles.  
 * - Base case: no vacant positions
 * - Recursive case: directly return the recursive result
 * 
 * Examples:
 *  - Given: emptyPositions = [(10,10), (30,10)], currentMarbles = [Marble(50,10,Red)]  
 *    Expect: [Marble(50,10,Red), Marble(10,10,Blue), Marble(30,10,Green)]  
 *   (colours chosen randomly, order preserved by recursion)
 * 
 * Design Strategy: Template application (recursion)
 * 
 * @param emptyPositions a list of vacant coordinates 
 * @param currentMarbles the list of marbles currently in the world 
 * @return a ConsList<Marble> containing both old marbles and newly created ones
 */
ConsList<Marble> addMarblesAtVacancies(ConsList<Pair<Integer,Integer>> emptyPositions,
                                       ConsList<Marble> currentMarbles) {
  return switch (emptyPositions) {
    case Nil<Pair<Integer,Integer>>() -> currentMarbles;
    case Cons<Pair<Integer,Integer>>(Pair<Integer,Integer> position, ConsList<Pair<Integer,Integer>> remainingPositions) ->
      addMarblesAtVacancies(remainingPositions,
        Append(currentMarbles, MakeList(new Marble(position.first(), position.second(), randomColour()))));
  };
}


/**
 * 2. Handle a mouse event
 * Problem analysis and data design
 * Function purpose statement and signature
 * - If the event is a LEFT_CLICK, use onLeftClick to update the world.  
 * - Otherwise, ignore the event and return the world unchanged.
 * 
 * Examples:
 *  - Given: Left click on a marble 
 *    Expect: use onLeftClick function, world may change
 *  - Given: Right click or other kind 
 *    Expect: return w unchanged
 * 
 * Design Strategy: Case Distinction
 * 
 * @param w the current WorldState
 * @param mouseEvent the mouse event to handle
 * @return a new WorldState after the mouseEvent
*/
WorldState mouseEvent(WorldState w, MouseEvent mouseEvent) {
    return (mouseEvent.kind() == MouseEventKind.LEFT_CLICK) ? leftClick(w, mouseEvent) : w;
}

/** 
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Handle a left-click event on the world
 * - If the click hits a marble, remove all marbles of the same colour.  
 * - If no marble is hit, return the world unchanged.
 * 
 * Examples:
 *  - Given: WorldState( [Marble(10, 10, RED), Marble(30, 10, BLUE), Marble(50, 10, RED)] )
 *           Left-click at Marble(10, 10, RED)
 *    Expect: removes all RED marbles, return WorldState( [Marble(30, 10, BLUE)] )
 * 
 *  - Given: Left-click at Marble(10, 11, RED) hits nothing
 *    Expect: returns the original world unchanged
 *
 * Design Strategy: Case Distinction
 * 
 * @param w the current WorldState
 * @param mouseEvent the MouseEvent describing the click
 * @return a new WorldState after applying the left-click logic
 */
WorldState leftClick(WorldState w, MouseEvent mouseEvent) {
    Maybe<Marble> hit = findClickedMarble(mouseEvent, w.marbles());
    return switch (hit) {
        case Nothing<Marble>() -> w;
        case Something<Marble>(Marble marble) -> new WorldState(filterOutSameColour(marble, w.marbles()));
    };
}

/** 
 * 2.1
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Detect whether a mouse click hits any marble on the screen,
 *   (click coordinates must lie within the square centered at the marble with 2R).  
 * - If a marble is hit, return Something<Marble>(that marble);  
 * - otherwise, return Nothing.
 * 
 * Examples:
 *  - Given: World contains [Marble(50, 50, RED), Marble(100, 100, BLUE)], marbleRadius = 10
 *           mouseEvent at (55,55)
 *    Expect: Within bounding box of Marble(50,50,RED), return Something(Marble(50,50,RED))
 * 
 *  - Given: mouseEvent at (95,105)
 *    Expect: Within bounding box of Marble(100,100,BLUE), return Something(Marble(100,100,BLUE))
 * 
 *  - Given: mouseEvent at (200,200)
 *    Expect: Nothing
 * 
 * Design Strategy: Template application
 * 
 * @param mouseEvent  the MouseEvent containing click coordinates
 * @param list        the list of marbles currently in the world
 * @return            Maybe<Marble> (Something if hit, Nothing if no hit)
 */
Maybe<Marble> findClickedMarble(MouseEvent mouseEvent, ConsList<Marble> list) {
  return switch (list) {
    case Nil<Marble>() -> new Nothing<>();
    case Cons<Marble>(Marble first, ConsList<Marble> rest) -> 
      ((mouseEvent.x() >= first.x() - marbleRadius) && 
      (mouseEvent.x() <= first.x() + marbleRadius) &&
      (mouseEvent.y() >= first.y() - marbleRadius) && 
      (mouseEvent.y() <= first.y() + marbleRadius)) ? new Something<Marble>(first)
                                                    : findClickedMarble(mouseEvent, rest);
  };
}

/** 
 * 2.2
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Remove all marbles that have the same colour as the given target marble.
 *   Traverses the list recursively from left to right, keeping only marbles of a different colour.
 * 
 * Examples:
 *  - Given: filterOutSameColour(RED, [RED, BLUE, RED, GREEN])
 *    Expect: [BLUE, GREEN]
 *  - Given: filterOutSameColour(BLUE, [GREEN, RED])
 *    Expect: [GREEN, RED]
 *  - Given: filterOutSameColour(GREEN, []) 
 *    Expect: []
 * 
 * Design Strategy: Template application
 * 
 * @param target  the marble clicked, all same colour marbles are removed
 * @param current the current list of marbles (possibly empty)
 * @return        a new list of marbles with all marbles of target.colour() removed
 */
ConsList<Marble> filterOutSameColour(Marble target, ConsList<Marble> current) {
  return switch (current) {
    case Nil<Marble>() -> MakeList();
    case Cons<Marble>(Marble first, ConsList<Marble> rest) -> 
      (Equals(first.colour(), target.colour())) ? filterOutSameColour(target, rest)
                                                : Append(MakeList(first), filterOutSameColour(target, rest));
  };
}

/**
 * Get marble at a pixel coordinate.
 * - Given the current world state and a pixel coordinate (x, y),
 *   returns the marble at pixel coordinate (x, y) or Nothing.
 * 
 * Examples:
 * - If a marble exists at (10, 20), return Something(marble).
 * - If no marble exists at (10, 10), return Nothing.
 * 
 * Design Strategy: Combining functions
 * 
 * @param w the current world state containing marbles
 * @param x the x-coordinate (in pixels)
 * @param y the y-coordinate (in pixels)
 * @return Something<Marble> if a marble is found at (x, y), otherwise Nothing<Marble>
 */
Maybe<Marble> getMarbleAt(WorldState w, int x, int y) {
  return findClickedMarble(new MouseEvent(MouseEventKind.LEFT_CLICK, x, y), w.marbles());
}

/**
 * returns the colour of a given marble
 */
Colour getColour(Marble m) {
    return convertToColour(m.colour());
}

// returns the number of marbles of a given colour
int numberOfMarblesOfColour(WorldState w, Colour colour) {
  return countColour(w.marbles(), colour);
}

int countColour(ConsList<Marble> marbles, Colour colour) {
  return switch (marbles) {
    case Nil<Marble>() -> 0;
    case Cons<Marble>(Marble first, ConsList<Marble> rest) ->
      (Equals(first.colour(), colour) ? 1 : 0) + countColour(rest, colour);
  };
}

/**
 * returns the number of empty locations in the grid of marbles
 */
int numberOfEmptyLocations(WorldState w) {
  return Length(findVacancies(w.marbles()));
}

void main() {
    BigBang("Marble Crush", getInitialState(), this::draw, this::step, this::keyEvent, this::mouseEvent);
    test();
}

void test() {

}