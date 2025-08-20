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

Image draw(WorldState w) {
    Image bg = Rectangle(WORLD_WIDTH, WORLD_HEIGHT, WHITE);
    return drawMarbles(w.marbles(), bg);
}

Image drawMarbles(ConsList<Marble> listOfMarbles, Image bg) {
    return switch (listOfMarbles) {
        case Nil<Marble>() -> bg;
        case Cons<Marble>(var first, var rest) -> 
            drawMarbles(rest, PlaceXY(bg, Circle(marbleRadius, convertToColour(first.colour())), first.x(), first.y()));
    };
}

/**
 * returns the initial state of your world
 */
WorldState getInitialState() {
    var positions = generateAllMarblesCenterPositionRecursively(marbleRadius, numMarbleRows, numMarbleCols);
    var marbles   = marblesFromPositions(positions, MakeList());
    return new WorldState(marbles);
}

/**
 * 
 */
ConsList<Marble> marblesFromPositions(ConsList<Pair<Integer,Integer>> listOfPositions, ConsList<Marble> acc) {
    return switch (listOfPositions) {
        case Nil<Pair<Integer,Integer>>() -> acc;
        case Cons<Pair<Integer,Integer>>(var p, var rest) -> 
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
        var vacancies = findVacancies(w.marbles());
        var added = addMarblesAtVacancies(vacancies, w.marbles());
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
  var allPositions = generateAllMarblesCenterPositionRecursively(marbleRadius, numMarbleRows, numMarbleCols);
  var occupiedPositions = positionsFromMarbles(listOfMarbles);
  return subtractPositions(allPositions, occupiedPositions); 
}

/**
* 1.1.1 Generates a list of pairs with the coordinates of the centers of the marbles 
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
    return generateAllGridCentersRecursively(marbleRadius, 0, 0, numMarbleRows, numMarbleCols, MakeList());
}

ConsList<Pair<Integer,Integer>> generateAllGridCentersRecursively(
    int marbleRadius, int row, int Col, int numMarbleRows, int numMarbleCols, ConsList<Pair<Integer,Integer>> acc) {
        if (row >= numMarbleRows) {
            return acc;
        }
        int marbleDiameter = marbleRadius * 2;
        int x = marbleRadius + Col * marbleDiameter;
        int y = marbleRadius + row * marbleDiameter;
        var nextAcc = Append(acc, MakeList(new Pair<Integer,Integer>(x, y)));
        int nextCol = Col + 1;
        int nextRow = row;
        if (nextCol >= numMarbleCols) {
            nextCol = 0;
            nextRow = row + 1;
        }
    return generateAllGridCentersRecursively(marbleRadius, nextRow, nextCol, numMarbleRows, numMarbleCols, nextAcc);
}

/**
 * 1.1.2 Base case: if the marble list is empty (Nil), return an empty list of coordinates.
 * Recursive case: if there is a Cons node (i.e., one marble + the remaining list):
 *   - Take the current marble’s (x, y) coordinates and wrap them into a Pair.
 *   - Use Append to add this coordinate to the front of the recursive result.
 *   - Recursively call positionsFromMarbles(remainingMarbles) until all marbles are processed.
 * In other words, this function converts all Marble objects into their coordinate list.
 *
 */
ConsList<Pair<Integer,Integer>> positionsFromMarbles(ConsList<Marble> marbles) {
  return switch (marbles) {
    case Nil<Marble>() -> MakeList();
    case Cons<Marble>(var m, var rest) -> 
        Append(MakeList(new Pair<Integer,Integer>(m.x(), m.y())), positionsFromMarbles(rest));
  };
}

/** 
 * 1.1.3 allPositions - occupiedPositions → produces the list of vacant positions.
 * Base case: no remaining positions.
 * Recursive case: current position + remaining positions.
 */
ConsList<Pair<Integer,Integer>> subtractPositions(ConsList<Pair<Integer,Integer>> allPositions, 
                                                  ConsList<Pair<Integer,Integer>> occupiedPositions) {
  return switch (allPositions) {
    case Nil<Pair<Integer,Integer>>() -> MakeList();
    case Cons<Pair<Integer,Integer>>(var position, var remaining) ->
      containsPos(occupiedPositions, position)
        ? subtractPositions(remaining, occupiedPositions)
        : Append(MakeList(position), subtractPositions(remaining, occupiedPositions));
  };
}

/**
 * 
 */
boolean containsPos(ConsList<Pair<Integer,Integer>> positions, Pair<Integer,Integer> target) {
  return switch (positions) {
    case Nil<Pair<Integer,Integer>>() -> false;
    case Cons<Pair<Integer,Integer>>(var p, var rest) -> 
        Equals(p, target) || containsPos(rest, target);
  };
}

/**
 * 1.2 Place new marbles into these vacancies.
 * Base case: no vacant positions
 * Recursive case: directly return the recursive result
 */
ConsList<Marble> addMarblesAtVacancies(ConsList<Pair<Integer,Integer>> emptyPositions,
                                       ConsList<Marble> currentMarbles) {
  return switch (emptyPositions) {
    case Nil<Pair<Integer,Integer>>() -> currentMarbles;
    case Cons<Pair<Integer,Integer>>(var position, var remainingPositions) ->
      addMarblesAtVacancies(remainingPositions,
        Append(currentMarbles, MakeList(new Marble(position.first(), position.second(), randomColour()))));
  };
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

/** 5.1.1 Hit detection (using the assignment’s "bounding box" rule: center ± R) */
Maybe<Marble> findClickedMarble(MouseEvent mouseEvent, ConsList<Marble> list) {
  return switch (list) {
    case Nil<Marble>() -> new Nothing<>();
    case Cons<Marble>(Marble first, ConsList<Marble> rest) -> 
      (mouseEvent.x() >= first.x() - marbleRadius) && 
      (mouseEvent.x() <= first.x() + marbleRadius) &&
      (mouseEvent.y() >= first.y() - marbleRadius) && 
      (mouseEvent.y() <= first.y() + marbleRadius)
        ? new Something<Marble>(first)
        : findClickedMarble(mouseEvent, rest);
  };
}

/** 5.1.2 Filter out all marbles of the same colour as the target (recursive, left -> right) */
ConsList<Marble> filterOutSameColour(Marble target, ConsList<Marble> current) {
  return switch (current) {
    case Nil<Marble>() -> MakeList();
    case Cons<Marble>(var first, var rest) -> 
      (Equals(first.colour(), target.colour())) 
        ? filterOutSameColour(target, rest)
        : Append(MakeList(first), filterOutSameColour(target, rest));
  };
}

/**
 * returns Something with the marble at pixel coordinate (x, y) 
 * or Nothing if no marble is there
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
    case Cons<Marble>(var first, var rest) ->
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