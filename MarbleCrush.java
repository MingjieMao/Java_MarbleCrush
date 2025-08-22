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
 * [C]: Define our own colour enumeration for marbles.
 * Each marble in the game must have one of these four colours.
 */
enum MarbleColour { 
    BLUE, 
    RED, 
    GREEN, 
    BLACK 
}

/**
 * Generate a random marble colour from the four available colours.
 * Each colour is equally likely to be chosen.
 * 
 * Examples:
 *   - If RandomNumber returns 0, MarbleColour.BLUE
 *   - If RandomNumber returns 1, MarbleColour.RED
 *   - If RandomNumber returns 2, MarbleColour.GREEN
 *   - If RandomNumber returns 3, MarbleColour.BLACK
 * 
 * Design Strategy: Case distinction
 * 
 * @return a randomly chosen MarbleColour (BLUE, RED, GREEN, or BLACK)
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

/**
 * Convert our MarbleColour enum into the Universe Colour type,
 * so that marbles can be drawn on the screen.
 * This acts as a bridge between the MarbleColour and Colour.
 *
 * Examples:
 *   - convertToColour(MarbleColour.BLUE)   -> Colour.BLUE
 *   - convertToColour(MarbleColour.RED)    -> Colour.RED
 *   - convertToColour(MarbleColour.GREEN)  -> Colour.GREEN
 *   - convertToColour(MarbleColour.BLACK)  -> Colour.BLACK
 *
 * Design Strategy: Case distinction
 *
 * @param marbleColour the MarbleColour fron enum
 * @return the corresponding Universe Colour used for drawing
 */
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
 * A Marble is represented by a record that contains its position
 * on the screen and its colour.
 * 
 * Examples:
 * - marble(50, 100, BLUE)
 * - marble(200, 150, RED)
 *
 * @param x the x-coordinate of the marble's centre in pixels (non-negative)
 * @param y the y-coordinate of the marble's centre in pixels (non-negative)
 * @param colour the MarbleColour of the marble (BLUE, RED, GREEN, BLACK)
 */
record Marble(int x, int y, MarbleColour colour) {}
/**
 * ... marble.x() ...
 * ... marble.y() ...
 * ... marble.colour() ...
 */

/**
 * [W]: world state is just the list of marbles currently present
 * A WorldState is represented by a record that contains
 * all the marbles currently present in the world.
 * 
 * Examples:
 * - An empty world: marbles is Nil
 * - A world with two marbles:
 *   marbles = Cons(Marble(50, 100, BLUE),
 *                  Cons(Marble(200, 150, RED), Nil))
 *
 * @param marbles the ConsList of Marble currently in the world
 */
record WorldState(ConsList<Marble> marbles) {}
/**
 * ... w.marbles() ...
 */

/**
 * Advance the world by one time step.
 * The world does not evolve over time, so the function simply returns the input world unchanged.
 * 
 * Examples:
 * - If w = WorldState(Nil), step(w) = WorldState(Nil).
 * - If w = WorldState(Cons(Marble(50, 100, BLUE), Nil)),
 *   step(w) = the same WorldState with the same marbles.
 *
 * Design Strategy: Simple expression
 *
 * @param w the current WorldState
 * @return the same WorldState, unchanged
 */
WorldState step(WorldState w) {
    return w;
}

/**
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Draw the entire world by starting from a white background,
 *   and then drawing all marbles on top of it.
 * 
 * Examples:
 *   - If w has no marbles, returns just a white background.
 *   - If w has some marbles, returns background plus those marbles.
 * 
 * Design Strategy: Combining functions
 * 
 * @param w the current WorldState containing all marbles
 * @return an Image with background and all marbles drawn
 */
Image draw(WorldState w) {
    Image bg = Rectangle(WORLD_WIDTH, WORLD_HEIGHT, Colour.WHITE);
    return drawMarbles(w.marbles(), bg);
}

/**
 * Problem analysis and data design
 * Function purpose statement and signature
 * - Draw all marbles in a list on background image.
 *   Each marble is drawn as a coloured circle at its (x, y) coordinate.
 * 
 * Examples:
 *   - If listOfMarbles is Nil, returns bg unchanged.
 * 
 *   - If listOfMarbles has one marble (x=10, y=20, red),
 *     returns bg with a red circle drawn at (10, 20).
 * 
 *   - If listOfMarbles has multiple marbles,
 *       recursively draws each marble on bg.
 * 
 * @param listOfMarbles a ConsList of marbles to be drawn
 * @param bg the background image on which marbles will be placed
 * @return an Image with all marbles drawn on top of the background
 */
Image drawMarbles(ConsList<Marble> listOfMarbles, Image bg) {
    return switch (listOfMarbles) {
        case Nil<Marble>() -> bg;
        case Cons<Marble>(Marble first, ConsList<Marble> rest) -> 
            drawMarbles(rest, PlaceXY(bg, Circle(marbleRadius, convertToColour(first.colour())), first.x(), first.y()));
    };
}

/**
 * Construct and return the initial state of the world.
 * The world starts with a full grid of marbles placed at regularly spaced positions.
 * 
 * Examples:
 *   - If numMarbleRows = 0 or numMarbleCols = 0，
 *     returns a WorldState with an empty marble list.
 * 
 *   - If numMarbleRows = 2 and numMarbleCols = 3,
 *     returns a WorldState with 6 marbles placed in a 2×3 grid.
 * 
 * Design Strategy: Combining functions
 * 
 * @return a WorldState containing all marbles
 */
WorldState getInitialState() {
    ConsList<Pair<Integer,Integer>> positions = 
        generateAllMarblesCenterPositionRecursively(marbleRadius, numMarbleRows, numMarbleCols);
    ConsList<Marble> marbles = marblesFromPositions(positions, MakeList());
    return new WorldState(marbles);
}

/**
 * Convert a list of marble center positions into a list of Marble objects.
 *   Each position (x, y) becomes a Marble with a randomly assigned colour.
 * 
 * Examples:
 *   - If Nil, returns acc (no new marbles to add).
 *   - If listOfPositions = [(10,20)] and acc = [],
 *       returns [Marble(10,20,colour)] with random colour.
 *   - If listOfPositions = [(10,20), (30,40)] and acc = [],
 *       returns [Marble(10,20,colour), Marble(30,40,colour)] with random colours.
 * 
 * Design Strategy: Template application
 * 
 * @param listOfPositions A list of (x, y) coordinate pairs for marbles
 * @param acc             An accumulator list of already-created marbles
 * @return                A ConsList<Marble> containing all marbles from the given positions
 */
ConsList<Marble> marblesFromPositions(ConsList<Pair<Integer,Integer>> listOfPositions, ConsList<Marble> acc) {
    return switch (listOfPositions) {
        case Nil<Pair<Integer,Integer>>() -> acc;
        case Cons<Pair<Integer,Integer>>(Pair<Integer,Integer> position, ConsList<Pair<Integer,Integer>> rest) -> 
            marblesFromPositions(rest, Append(acc, MakeList(new Marble(position.first(), position.second(), randomColour()))));
    };
}

/**
 * 1. Handle keyEvent
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
ConsList<Pair<Integer,Integer>> generateAllMarblesCenterPositionRecursively(
    int marbleRadius, int numMarbleRows, int numMarbleCols) {
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
 * Get the display colour (Universe Colour) of a given marble.
 * Convert a Marble's own MarbleColour into a Universe Colour
 * that can be drawn on the screen.
 *
 * Examples:
 * - m = Marble(10, 20, MarbleColour.BLUE), 
 *   getColour(m) = Colour.BLUE
 * - m = Marble(50, 70, MarbleColour.RED), 
 *   getColour(m) = Colour.RED
 *
 * Design Strategy: Combining functions
 *
 * @param m the Marble whose colour we want
 * @return the Colour corresponding to the Marble's MarbleColour
 */
Colour getColour(Marble marble) {
    return convertToColour(marble.colour());
}

/**
 * Count how many marbles in the current world have a given target colour.
 * 
 * Examples:  
 *   - If w has no marbles, return 0.  
 *   - If w has 3 marbles and 2 of them are RED, then 
 *     numberOfMarblesOfColour(w, Colour.RED) = 2.  
 * 
 * Design Strategy: Combining functions  
 *  
 * @param w the current WorldState containing all marbles  
 * @param colour the target Colour to count  
 * @return the number of marbles of the given colour in the world  
 */
int numberOfMarblesOfColour(WorldState w, Colour colour) {
    return countColour(w.marbles(), colour);
}

/**
 * Count how many marbles in a given list match the target colour.  
 * Works recursively: check the first marble, then recurse on the rest.  
 * 
 * Examples:  
 *   - If marbles = Nil, return 0.  
 *   - If marbles = [RED, BLUE, RED] and colour = RED, return 2.  
 *   - If marbles = [GREEN, GREEN] and colour = RED, return 0.  
 * 
 * Design Strategy: Case distinction
 * 
 * @param marbles the ConsList of marbles to search  
 * @param colour the target Colour to count  
 * @return the number of marbles in the list that have the given colour  
 */
int countColour(ConsList<Marble> marbles, Colour colour) {
    return switch (marbles) {
        case Nil<Marble>() -> 0;
        case Cons<Marble>(Marble first, ConsList<Marble> rest) ->
            (Equals(convertToColour(first.colour()), colour) ? 1 : 0) + countColour(rest, colour);
    };
}

/**
 * Returns the number of empty locations in the grid of marbles
 * Achieved by finding all vacancies and then taking their length.
 * 
 * Examples:  
 *   - If w has a full grid of marbles, return 0.  
 *   - If w has 3 empty positions, return 3.  
 * 
 * Design Strategy: Combining functions
 * 
 * @param w the current WorldState containing all marbles  
 * @return the number of empty grid locations (int) 
 */
int numberOfEmptyLocations(WorldState w) {
    return Length(findVacancies(w.marbles()));
}

/**
 * Launch the Marble Crush game using the BigBang.  
 * Provide initial world state, draw/step/key/mouse handlers, and then run tests. 
 * 
 * Examples:  
 *   - When run, opens a game window titled "Marble Crush".  
 *   - World starts with the initial arrangement of marbles.  
 *   - User interactions (keyboard/mouse) update the world accordingly.  
 * 
 * Design Strategy: Combining functions  
 * 
 * @return Opens a Marble Crush game window, executes the interactive loop until the user closes it.  
 */
void main() {
    BigBang("Marble Crush", getInitialState(), this::draw, this::step, this::keyEvent, this::mouseEvent);
    test();
}

void test() {
    runAsTest(this::testRandomColour);
    runAsTest(this::testConvertToColour);
    runAsTest(this::testStep);
    runAsTest(this::testDraw);
    runAsTest(this::testGetInitialState);
    runAsTest(this::testMarblesFromPositions);

    runAsTest(this::testKeyEvent);
    runAsTest(this::testProcessKeyEvent);
    runAsTest(this::testFindVacancies);
    runAsTest(this::testGenerateAllPositions);
    runAsTest(this::testPositionsFromMarbles);
    runAsTest(this::testSubtractPositions);
    runAsTest(this::testContainsPos);
    runAsTest(this::testAddMarblesAtVacancies);

    runAsTest(this::testMouseEvent);
    runAsTest(this::testLeftClick);
    runAsTest(this::testFindClickedMarble);
    runAsTest(this::testFilterOutSameColour);

    runAsTest(this::testGetMarbleAt);
    runAsTest(this::testGetColour);
    runAsTest(this::testCountColour);
    runAsTest(this::testNumberOfMarblesOfColour);
    runAsTest(this::testNumberOfEmptyLocations);
}

void testRandomColour() {
    MarbleColour c = randomColour();
    boolean valid = (c == MarbleColour.BLUE || c == MarbleColour.RED 
                        || c == MarbleColour.GREEN || c == MarbleColour.BLACK);
    testEqual(true, valid, "randomColour() must return one of the 4 colours");
}

void testConvertToColour() {
    testEqual(Colour.BLUE, convertToColour(MarbleColour.BLUE), "convert BLUE");
    testEqual(Colour.RED, convertToColour(MarbleColour.RED), "convert RED");
    testEqual(Colour.GREEN, convertToColour(MarbleColour.GREEN), "convert GREEN");
    testEqual(Colour.BLACK, convertToColour(MarbleColour.BLACK), "convert BLACK");
}

void testStep() {
    WorldState empty = new WorldState(MakeList());
    testEqual(empty, step(empty), "step() on empty world should not change");

    WorldState one = new WorldState(MakeList(new Marble(10, 20, MarbleColour.BLUE)));
    testEqual(one, step(one), "step() on non-empty world should not change");

    WorldState many = new WorldState(
        MakeList(
            new Marble(10, 10, MarbleColour.RED),
            new Marble(30, 40, MarbleColour.GREEN),
            new Marble(70, 80, MarbleColour.BLACK),
            new Marble(90, 15, MarbleColour.BLUE)
        )
    );
    testEqual(many, step(many), "step(many) should not change the world");

    WorldState edges = new WorldState(
        MakeList(
            new Marble(0, 0, MarbleColour.RED),
            new Marble(WORLD_WIDTH, 0, MarbleColour.GREEN),
            new Marble(0, WORLD_HEIGHT, MarbleColour.BLUE)
        )
    );
    testEqual(edges, step(edges), "step(edges) should not move or alter marbles at boundaries");

}

void testDraw() {
    Image img = draw(getInitialState());
    testEqual(false, img == null, "draw should not return null.");
    
    Image bg = Rectangle(WORLD_WIDTH, WORLD_HEIGHT, Colour.WHITE);
    Image res = drawMarbles(MakeList(), bg);
    testEqual(true, Equals(res, bg), "empty world should equal plain white background.");
}


void testGetInitialState() {
    // Case 1: Normal case
    WorldState init = getInitialState();
    int expectedCount = numMarbleRows * numMarbleCols;
    testEqual(expectedCount, Length(init.marbles()),
        "initial state should have rows * cols marbles");

    // Case 2: Edge case (0 rows or 0 cols: empty world)
    WorldState emptyInit = new WorldState(MakeList());
    testEqual(true, Equals(Length(emptyInit.marbles()), 0),
        "if no rows or cols, initial state should be empty");

    // Case 3: Sanity check (length is never negative)
    testEqual(true, Length(init.marbles()) >= 0,
        "initial state marble count should never be negative");
}

void testMarblesFromPositions() {
    // Case 1: Empty input: should return empty list
    ConsList<Pair<Integer,Integer>> emptyPos = MakeList();
    ConsList<Marble> resultEmpty = marblesFromPositions(emptyPos, MakeList());
    testEqual(0, Length(resultEmpty), "empty positions should give no marbles");

    // Case 2: Single position: produces exactly one marble with same x,y
    ConsList<Pair<Integer,Integer>> onePos = MakeList(new Pair<Integer,Integer>(10,20));
    ConsList<Marble> resultOne = marblesFromPositions(onePos, MakeList());
    testEqual(1, Length(resultOne), "one position should give one marble");
    testEqual(10, Nth(resultOne,0).x(), "x coordinate matches input");
    testEqual(20, Nth(resultOne,0).y(), "y coordinate matches input");

    // Case 3: Multiple positions: produces same number of marbles in same order
    ConsList<Pair<Integer,Integer>> twoPos = MakeList(
        new Pair<Integer,Integer>(5, 5),new Pair<Integer,Integer>(100, 200));
    ConsList<Marble> resultTwo = marblesFromPositions(twoPos, MakeList());
    testEqual(2, Length(resultTwo), "two positions should give two marbles");
    testEqual(5, Nth(resultTwo,0).x(), "first marble x");
    testEqual(5, Nth(resultTwo,0).y(), "first marble y");
    testEqual(100, Nth(resultTwo,1).x(), "second marble x");
    testEqual(200, Nth(resultTwo,1).y(), "second marble y");

    // Case 4: Boundary positions
    ConsList<Pair<Integer,Integer>> edgePos = MakeList(
        new Pair<Integer,Integer>(0, 0), new Pair<Integer,Integer>(WORLD_WIDTH, WORLD_HEIGHT));
    ConsList<Marble> resultEdges = marblesFromPositions(edgePos, MakeList());
    testEqual(2, Length(resultEdges), "two edge positions → two marbles");
    testEqual(0, Nth(resultEdges,0).x(), "left/top corner x");
    testEqual(0, Nth(resultEdges,0).y(), "left/top corner y");
    testEqual(WORLD_WIDTH, Nth(resultEdges,1).x(), "right/bottom corner x");
    testEqual(WORLD_HEIGHT, Nth(resultEdges,1).y(), "right/bottom corner y");
}


void testKeyEvent() {
    // Prepare a partially-filled world: occupy 1 known grid position.
    ConsList<Pair<Integer,Integer>> all =
        generateAllMarblesCenterPositionRecursively(marbleRadius, numMarbleRows, numMarbleCols);
    Pair<Integer,Integer> p0 = Nth(all, 0); // first grid position
    ConsList<Marble> partial = MakeList(new Marble(p0.first(), p0.second(), MarbleColour.BLUE));
    WorldState w = new WorldState(partial);

    int total = numMarbleRows * numMarbleCols;
    int before = Length(w.marbles());

    // SPACE (KEY_PRESSED + "Space") should fill all vacancies
    KeyEvent space = new KeyEvent(KeyEventKind.KEY_PRESSED, "Space");
    WorldState afterSpace = keyEvent(w, space);
    testEqual(total, Length(afterSpace.marbles()),
        "SPACE should fill all vacancies (length == rows*cols)");

    // Non-space key should keep world unchanged in size
    KeyEvent other = new KeyEvent(KeyEventKind.KEY_PRESSED, "A");
    WorldState afterOther = keyEvent(w, other);
    testEqual(before, Length(afterOther.marbles()),
        "Non-space key should leave world unchanged in size");

    // Wrong kind (e.g., KEY_RELEASED "Space") should not trigger fill
    KeyEvent releasedSpace = new KeyEvent(KeyEventKind.KEY_RELEASED, "Space");
    WorldState afterReleased = keyEvent(w, releasedSpace);
    testEqual(before, Length(afterReleased.marbles()),
        "KEY_RELEASED Space should not change the world");
}

void testProcessKeyEvent() {
    ConsList<Pair<Integer,Integer>> all =
        generateAllMarblesCenterPositionRecursively(marbleRadius, numMarbleRows, numMarbleCols);

    ConsList<Marble> full =
        marblesFromPositions(all, MakeList());  

    WorldState fullWorld = new WorldState(full);
        int total = numMarbleRows * numMarbleCols;

    // SPACE on full world: still full
    WorldState ws1 = processKeyEvent(fullWorld, KeyEventKind.KEY_PRESSED, "Space");
    testEqual(total, Length(ws1.marbles()),
        "SPACE on full world should keep it full (no vacancies to fill)");

    // Wrong key or wrong kind: unchanged
    WorldState ws2 = processKeyEvent(fullWorld, KeyEventKind.KEY_PRESSED, "X");
    testEqual(total, Length(ws2.marbles()),
        "Non-space key should not change a full world");

    WorldState ws3 = processKeyEvent(fullWorld, KeyEventKind.KEY_RELEASED, "Space");
    testEqual(total, Length(ws3.marbles()),
        "KEY_RELEASED Space should not change a full world");
}

void testFindVacancies() {
    // Start with empty world: all positions are vacant
    ConsList<Marble> none = MakeList();
    ConsList<Pair<Integer,Integer>> v1 = findVacancies(none);
    int total = numMarbleRows * numMarbleCols;
    testEqual(total, Length(v1), "Empty world: all positions vacant");

    // Occupy one known position: vacancies = total-1, and not containing that position
    ConsList<Pair<Integer,Integer>> all =
        generateAllMarblesCenterPositionRecursively(marbleRadius, numMarbleRows, numMarbleCols);
    Pair<Integer,Integer> p0 = Nth(all, 0);
    ConsList<Marble> one = MakeList(new Marble(p0.first(), p0.second(), MarbleColour.RED));
    ConsList<Pair<Integer,Integer>> v2 = findVacancies(one);
    testEqual(total - 1, Length(v2), "One occupied: vacancies == total-1");
    testEqual(false, containsPos(v2, p0), "Vacancies should not contain occupied p0");

    // If we occupy two positions, both must be removed from vacancies
    Pair<Integer,Integer> p1 = Nth(all, 1);
    ConsList<Marble> two = MakeList(new Marble(p0.first(), p0.second(), MarbleColour.RED),
                                    new Marble(p1.first(), p1.second(), MarbleColour.BLUE));
    ConsList<Pair<Integer,Integer>> v3 = findVacancies(two);
    testEqual(total - 2, Length(v3), "Two occupied: vacancies == total-2");
    testEqual(false, containsPos(v3, p0), "Vacancies should not contain p0");
    testEqual(false, containsPos(v3, p1), "Vacancies should not contain p1");
}

void testGenerateAllPositions() {
    // Use a tiny grid to verify exact coordinates (r=10, rows=2, cols=3)
    int r = 10, rows = 2, cols = 3;
    ConsList<Pair<Integer,Integer>> expect =
        MakeList(new Pair<Integer,Integer>(10,10),
                 new Pair<Integer,Integer>(30,10),
                 new Pair<Integer,Integer>(50,10),
                 new Pair<Integer,Integer>(10,30),
                 new Pair<Integer,Integer>(30,30),
                 new Pair<Integer,Integer>(50,30));

    ConsList<Pair<Integer,Integer>> got =
        generateAllMarblesCenterPositionRecursively(r, rows, cols);
    testEqual(true, Equals(expect, got), "generateAllMarblesCenterPositionRecursively should match row-major grid");

    // Helper base case: if row >= rows, returns acc unchanged
    ConsList<Pair<Integer,Integer>> acc = MakeList(new Pair<Integer,Integer>(1,1));
    ConsList<Pair<Integer,Integer>> base =
        generateAllGridCentersRecursively(r, rows, 0, rows, cols, acc);
    testEqual(true, Equals(acc, base), "generateAllGridCentersRecursively base case should return acc");
}

void testPositionsFromMarbles() {
    // Empty
    ConsList<Pair<Integer,Integer>> p0 = positionsFromMarbles(MakeList());
    testEqual(0, Length(p0), "Empty marbles: empty positions");

    // One
    Marble m1 = new Marble(10, 20, MarbleColour.BLUE);
    ConsList<Pair<Integer,Integer>> p1 = positionsFromMarbles(MakeList(m1));
    testEqual(1, Length(p1), "One marble: one position");
    testEqual(true, Equals(new Pair<Integer,Integer>(10,20), Nth(p1,0)), "Position equals (10,20)");

    // Many (preserve order)
    Marble m2 = new Marble(30, 40, MarbleColour.RED);
    Marble m3 = new Marble(50, 60, MarbleColour.GREEN);
    ConsList<Pair<Integer,Integer>> p = positionsFromMarbles(MakeList(m1, m2, m3));
    testEqual(3, Length(p), "Three marbles: three positions");
    testEqual(true, Equals(new Pair<Integer,Integer>(10,20), Nth(p,0)), "pos[0] == (10,20)");
    testEqual(true, Equals(new Pair<Integer,Integer>(30,40), Nth(p,1)), "pos[1] == (30,40)");
    testEqual(true, Equals(new Pair<Integer,Integer>(50,60), Nth(p,2)), "pos[2] == (50,60)");
}

void testSubtractPositions() {
    ConsList<Pair<Integer,Integer>> all =
        MakeList(new Pair<Integer,Integer>(10,10),
                 new Pair<Integer,Integer>(30,10),
                 new Pair<Integer,Integer>(50,10));

    // No occupied: get all
    ConsList<Pair<Integer,Integer>> occ0 = MakeList();
    ConsList<Pair<Integer,Integer>> v0 = subtractPositions(all, occ0);
    testEqual(true, Equals(all, v0), "No occupied: vacancies == all");

    // Occupy middle
    ConsList<Pair<Integer,Integer>> occ1 = MakeList(new Pair<Integer,Integer>(30,10));
    ConsList<Pair<Integer,Integer>> v1 = subtractPositions(all, occ1);
    ConsList<Pair<Integer,Integer>> exp1 =
        MakeList(new Pair<Integer,Integer>(10,10), new Pair<Integer,Integer>(50,10));
    testEqual(true, Equals(exp1, v1), "Occupied {30,10}: vacancies exclude it, preserve order");

    // Occupy all
    ConsList<Pair<Integer,Integer>> occAll =
        MakeList(new Pair<Integer,Integer>(10,10),
                 new Pair<Integer,Integer>(30,10),
                 new Pair<Integer,Integer>(50,10));
    ConsList<Pair<Integer,Integer>> vAll = subtractPositions(all, occAll);
    testEqual(0, Length(vAll), "Occupied all: vacancies empty");
}

void testContainsPos() {
    ConsList<Pair<Integer,Integer>> ps =
        MakeList(new Pair<Integer,Integer>(1,1),
                 new Pair<Integer,Integer>(2,2),
                 new Pair<Integer,Integer>(3,3));

    testEqual(true,  containsPos(ps, new Pair<Integer,Integer>(2,2)), "Should find existing (2,2)");
    testEqual(false, containsPos(ps, new Pair<Integer,Integer>(4,4)), "Should not find absent (4,4)");
    testEqual(false, containsPos(MakeList(), new Pair<Integer,Integer>(1,1)), "Empty list: always false");
}

void testAddMarblesAtVacancies() {
    // current: one marble at (5,5)
    ConsList<Marble> current = MakeList(new Marble(5, 5, MarbleColour.BLACK));

    // empty positions to fill: (10,10), (30,10)
    ConsList<Pair<Integer,Integer>> empties =
        MakeList(new Pair<Integer,Integer>(10,10),
                 new Pair<Integer,Integer>(30,10));

    ConsList<Marble> out = addMarblesAtVacancies(empties, current);

    // Length check: 1 existing + 2 new = 3
    testEqual(3, Length(out), "Length should be current + vacancies");

    // Order check: first is the original (5,5)
    testEqual(5, Nth(out,0).x(), "First marble keeps original x=5");
    testEqual(5, Nth(out,0).y(), "First marble keeps original y=5");

    // New marbles appear in the order of vacancies, coordinates must match
    testEqual(10, Nth(out,1).x(), "Second marble x from first vacancy");
    testEqual(10, Nth(out,1).y(), "Second marble y from first vacancy");
    testEqual(30, Nth(out,2).x(), "Third marble x from second vacancy");
    testEqual(10, Nth(out,2).y(), "Third marble y from second vacancy");
}

void testMouseEvent() {
    // Prepare a world with two RED marbles and one BLUE marble.
    Marble r1 = new Marble(50, 50, MarbleColour.RED);
    Marble r2 = new Marble(90, 50, MarbleColour.RED);
    Marble b1 = new Marble(200, 200, MarbleColour.BLUE);
    WorldState w = new WorldState(MakeList(r1, r2, b1));
    int before = Length(w.marbles());

    // LEFT_CLICK exactly on r1 center: all RED marbles removed
    MouseEvent hitR1 = new MouseEvent(MouseEventKind.LEFT_CLICK, r1.x(), r1.y());
    WorldState afterLeft = mouseEvent(w, hitR1);
    testEqual(1, Length(afterLeft.marbles()),
        "LEFT_CLICK on RED should remove all RED marbles (only BLUE remains)");
    testEqual(true, Equals(Nth(afterLeft.marbles(), 0), b1),
        "Remaining marble should be the BLUE one");

    // LEFT_CLICK that misses everything: unchanged
    MouseEvent miss = new MouseEvent(MouseEventKind.LEFT_CLICK, 9999, 9999);
    WorldState afterMiss = mouseEvent(w, miss);
    testEqual(before, Length(afterMiss.marbles()),
        "LEFT_CLICK on empty space should leave world unchanged");
    testEqual(true, Equals(afterMiss.marbles(), w.marbles()),
        "World content unchanged when clicking empty space");

    // Non-left event: unchanged
    MouseEvent notLeft = new MouseEvent(MouseEventKind.RIGHT_CLICK, r1.x(), r1.y());
    WorldState afterRight = mouseEvent(w, notLeft);
    testEqual(true, Equals(afterRight.marbles(), w.marbles()),
        "Non-LEFT mouse event should not change the world");
}

void testLeftClick() {
    // World with two REDs and one BLUE
    Marble r1 = new Marble(50, 50, MarbleColour.RED);
    Marble r2 = new Marble(150, 150, MarbleColour.RED);
    Marble b1 = new Marble(200, 200, MarbleColour.BLUE);
    WorldState w = new WorldState(MakeList(r1, r2, b1));

    // Hit r2: remove all REDs
    MouseEvent hitR2 = new MouseEvent(MouseEventKind.LEFT_CLICK, r2.x(), r2.y());
    WorldState afterHitR2 = leftClick(w, hitR2);
    testEqual(1, Length(afterHitR2.marbles()),
        "Hitting a RED should remove all REDs, leaving only BLUE");
    testEqual(true, Equals(Nth(afterHitR2.marbles(), 0), b1),
        "Remaining marble should be BLUE");

    // Not hit: unchanged
    MouseEvent notHit = new MouseEvent(MouseEventKind.LEFT_CLICK, 20, 20);
    WorldState afterMiss = leftClick(w, notHit);
    testEqual(true, Equals(afterMiss.marbles(), w.marbles()),
        "Not hit Left-click should not change the world");

    // Edge hit (on bounding box edge): x = center + R, y = center
    MouseEvent edgeHit = new MouseEvent(MouseEventKind.LEFT_CLICK, r1.x() + marbleRadius, r1.y());
    WorldState afterEdge = leftClick(w, edgeHit);
    testEqual(1, Length(afterEdge.marbles()),
        "Edge hit should count as hit (remove all REDs)");
    testEqual(true, Equals(Nth(afterEdge.marbles(), 0), b1),
        "After edge hit, only BLUE remains");
}

void testFindClickedMarble() {
    // Case 1: Empty list -> Nothing
    testEqual(
        new Nothing<Marble>(),
        findClickedMarble(new MouseEvent(MouseEventKind.LEFT_CLICK, 0, 0), MakeList()),
        "Empty list should return Nothing"
    );

    // Prepare a single marble
    Marble m = new Marble(100, 100, MarbleColour.BLUE);
    ConsList<Marble> one = MakeList(m);

    // Case 2: Center hit -> Something(m)
    testEqual(
        new Something<Marble>(m),
        findClickedMarble(new MouseEvent(MouseEventKind.LEFT_CLICK, 100, 100), one),
        "Center click should hit the marble"
    );

    // Case 3: Edge hit (x+R, y) -> Something(m)
    testEqual(
        new Something<Marble>(m),
        findClickedMarble(new MouseEvent(MouseEventKind.LEFT_CLICK, m.x() + marbleRadius, m.y()), one),
        "Edge point should be considered a hit"
    );

    // Case 4: Outside by 1px (x+R+1, y) -> Nothing
    testEqual(
        new Nothing<Marble>(),
        findClickedMarble(new MouseEvent(MouseEventKind.LEFT_CLICK, m.x() + marbleRadius + 1, m.y()), one),
        "Outside bounding box by 1px should be miss"
    );

    // Case 5: Overlapping two marbles at same center -> first wins
    Marble g = new Marble(300, 300, MarbleColour.GREEN);
    Marble k = new Marble(300, 300, MarbleColour.BLACK);
    ConsList<Marble> two = MakeList(g, k);
    testEqual(
        new Something<Marble>(g),
        findClickedMarble(new MouseEvent(MouseEventKind.LEFT_CLICK, 300, 300), two),
        "When overlapping, should return the first in the list"
    );
}

void testFilterOutSameColour() {
    // Empty list -> empty
    ConsList<Marble> empty = MakeList();
    ConsList<Marble> out0 = filterOutSameColour(new Marble(0, 0, MarbleColour.RED), empty);
    testEqual(0, Length(out0), "Empty list remains empty");

    // All same colour -> all removed
    Marble r1 = new Marble(10, 10, MarbleColour.RED);
    Marble r2 = new Marble(20, 20, MarbleColour.RED);
    ConsList<Marble> reds = MakeList(r1, r2);
    ConsList<Marble> outR = filterOutSameColour(r1, reds);
    testEqual(0, Length(outR), "All RED removed");

    // Mixed colours -> only target colour removed; order preserved for others
    Marble b = new Marble(30, 30, MarbleColour.BLUE);
    Marble g = new Marble(40, 40, MarbleColour.GREEN);
    Marble r3 = new Marble(50, 50, MarbleColour.RED);
    ConsList<Marble> mixed = MakeList(r1, b, g, r3);
    ConsList<Marble> outMix = filterOutSameColour(r1, mixed);
    testEqual(2, Length(outMix), "Should keep only non-RED marbles");
    testEqual(true, Equals(Nth(outMix, 0), b), "First kept should be BLUE (order preserved)");
    testEqual(true, Equals(Nth(outMix, 1), g), "Second kept should be GREEN (order preserved)");

    // No target colour present -> list unchanged
    Marble k = new Marble(60, 60, MarbleColour.BLACK);
    ConsList<Marble> onlyBGK = MakeList(b, g, k);
    ConsList<Marble> outNoRed = filterOutSameColour(r1, onlyBGK);
    testEqual(true, Equals(outNoRed, onlyBGK), "If target colour absent, list unchanged");
}


void testGetMarbleAt() {
    // Case 1: Empty world -> Nothing
    WorldState empty = new WorldState(MakeList());
    testEqual(
        new Nothing<Marble>(),
        getMarbleAt(empty, 0, 0), "Empty world should return Nothing"
    );

    // Prepare single-marble world
    Marble m = new Marble(100, 100, MarbleColour.BLUE);
    WorldState one = new WorldState(MakeList(m));

    // Case 2: Center hit -> Something(m)
    testEqual(
        new Something<Marble>(m),
        getMarbleAt(one, 100, 100), "Center click should hit"
    );

    // Case 3: Edge hit (x+R, y) -> Something(m)
    testEqual(
        new Something<Marble>(m),
        getMarbleAt(one, m.x() + marbleRadius, m.y()), "Edge point should be considered a hit"
    );

    // Case 4: Outside -> Nothing
    testEqual(
        new Nothing<Marble>(),
        getMarbleAt(one, m.x() + marbleRadius + 1, m.y()), "Outside by 1px should miss"
    );

    // Case 5: Overlapping two marbles -> first wins
    Marble g = new Marble(300, 300, MarbleColour.GREEN);
    Marble k = new Marble(300, 300, MarbleColour.BLACK);
    WorldState two = new WorldState(MakeList(g, k));
    testEqual(
        new Something<Marble>(g),
        getMarbleAt(two, 300, 300), "When overlapping, should return the first in the list"
    );
}

void testGetColour() {
    // BLUE
    Marble mb = new Marble(10, 10, MarbleColour.BLUE);
    testEqual(Colour.BLUE, getColour(mb), "BLUE should map to Colour.BLUE");

    // RED
    Marble mr = new Marble(10, 10, MarbleColour.RED);
    testEqual(Colour.RED, getColour(mr), "RED should map to Colour.RED");

    // GREEN
    Marble mg = new Marble(10, 10, MarbleColour.GREEN);
    testEqual(Colour.GREEN, getColour(mg), "GREEN should map to Colour.GREEN");

    // BLACK
    Marble mk = new Marble(10, 10, MarbleColour.BLACK);
    testEqual(Colour.BLACK, getColour(mk), "BLACK should map to Colour.BLACK");
} 

void testCountColour() {
    // Case 1: Empty list
    testEqual(0, countColour(MakeList(), Colour.RED), "Empty list should count 0");

    // Case 2: Single match
    ConsList<Marble> oneR = MakeList(new Marble(10, 10, MarbleColour.RED));
    testEqual(1, countColour(oneR, Colour.RED), "Single matching marble should count 1");

    // Case 3: Multiple: two RED, one BLUE
    ConsList<Marble> mix = MakeList(
        new Marble(0, 0, MarbleColour.RED),
        new Marble(1, 1, MarbleColour.BLUE),
        new Marble(2, 2, MarbleColour.RED)
    );
    testEqual(2, countColour(mix, Colour.RED), "Two RED should count 2");
    testEqual(1, countColour(mix, Colour.BLUE), "One BLUE should count 1");
    testEqual(0, countColour(mix, Colour.GREEN), "No GREEN should count 0");
}

void testNumberOfMarblesOfColour() {
    // Case 1: Empty world
    WorldState empty = new WorldState(MakeList());
    testEqual(0, numberOfMarblesOfColour(empty, Colour.RED), "Empty world: 0");

    // Case 2: Mixed colours
    WorldState w = new WorldState(MakeList(
        new Marble(0, 0, MarbleColour.RED),
        new Marble(1, 1, MarbleColour.BLUE),
        new Marble(2, 2, MarbleColour.RED),
        new Marble(3, 3, MarbleColour.GREEN)
    ));
    testEqual(2, numberOfMarblesOfColour(w, Colour.RED), "Two RED in world: 2");
    testEqual(1, numberOfMarblesOfColour(w, Colour.BLUE), "One BLUE in world: 1");
    testEqual(1, numberOfMarblesOfColour(w, Colour.GREEN), "One GREEN in world: 1");
    testEqual(0, numberOfMarblesOfColour(w, Colour.BLACK), "No BLACK in world: 0");
}

void testNumberOfEmptyLocations() {
    int total = numMarbleRows * numMarbleCols;

    // Case 1: Empty world -> all vacant
    WorldState empty = new WorldState(MakeList());
    testEqual(total, numberOfEmptyLocations(empty), "Empty world should have all positions vacant");

    // Prepare all grid centers
    ConsList<Pair<Integer,Integer>> all =
        generateAllMarblesCenterPositionRecursively(marbleRadius, numMarbleRows, numMarbleCols);

    // Case 2: Full world -> 0 vacancies
    ConsList<Marble> fullList = marblesFromPositions(all, MakeList()); 
    WorldState full = new WorldState(fullList);
    testEqual(0, numberOfEmptyLocations(full), "Full grid should have 0 vacancies");

    // Case 3: Occupy first two centers -> total-2 
    Pair<Integer,Integer> p0 = Nth(all, 0);
    Pair<Integer,Integer> p1 = Nth(all, 1);
    WorldState twoTaken = new WorldState(MakeList(
        new Marble(p0.first(), p0.second(), MarbleColour.RED),
        new Marble(p1.first(), p1.second(), MarbleColour.BLUE)
    ));
    testEqual(total - 2, numberOfEmptyLocations(twoTaken),
        "With two occupied centers, vacancies should be total-2");
}
