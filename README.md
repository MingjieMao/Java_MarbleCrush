Part 1: The File System Calculator
In a file called FileSystem.java
Model a simple file system and implement core functions for it using recursion. For example, one of the goals in this assignment is to calculate the total disk space used by a directory, including all files and sub-directories it contains. This is a classic application of recursion on hierarchical data.
1. Designing the File System Model
2. Testing interface
3. Populating a Directory
4. Calculating Total Size
5. Finding an Item


Part 2: Marble Crush
In a file called MarbleCrush.java
Design a world program which handles a two-dimensional (2D) grid of coloured marbles that the user will crush (delete) using the mouse. The user will also be able to create new marbles by typing space. Similar to the BouncingMarbles from Assignment 1, the world is 300 pixels wide and 500 pixels high and all marbles are drawn as circles of radius 10 pixels. The marbles are positioned to be touching the adjacent marbles in the grid, with no space in between them and no overlapping. Therefore the world should have 25 rows of marbles with 15 marbles per row (i.e., 15 columns of marbles). The marble colours are BLUE, RED, GREEN, and BLACK. Unlike the previous assignment, the marbles in this world are stationary and do not move on their own. At the start of the program, the entire grid is populated with marbles, and the colour for each individual marble is decided randomly among the four colours enumerated above.
The program must handle user interaction through the mouse and keyboard. When the user left-clicks the mouse, the program must first determine if the coordinates of the click belong to the space occupied by the marble. We assume that the space occupied by a marble is the space occupied by its bounding box, that is, the area represented by a square of side length 20 pixels in which the marble is inscribed, with the marble’s center placed on the center of the bounding box. If a marble is clicked (i.e., the click is within the bounding box of the marble), that marble and all other marbles currently on the grid that share the same colour must be crushed (deleted), leaving their former occupied space as empty spaces. If the user clicks on an empty space where no marble exists, the state of the world should not change. Furthermore, if the user hits the SPACE bar, the program must identify all empty spaces on the grid (that is, bounding boxes without a marble inside) and fill each one with a new marble. The colour of each new marble must be randomly chosen from the four available colours.
