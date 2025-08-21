import comp1110.lib.*;
import comp1110.lib.Date;
import static comp1110.lib.Functions.*;
import static comp1110.testing.Comp1110Unit.*;

// [I]: The data type that represents any item in the file system.
// [F]: The data type that represents a single file.
// [D]: The data type that represents a directory.

// 1. Designing the File System Model
/**
 * 1. Problem analysis and data design 
 * We will use a general itemization in order to represent Item.
 * 2. Function purpose statement and signature
 * A file system consists of two kind of items: file and directory.
 *  - File, a simple item, cannot contain other items.
 *  - Directory, a more complex item, can contain both files and other directories.
 * 3. Examples:
 *    - Item file1 = new File("1110.txt", 1200);
 *    - Item dir1 = new Directory("emptyFolder", MakeList()); 
 *    - Item dir2 = new Directory("docs", MakeList(new File("a.txt", 10), new File("b.txt", 200)));
 *    - Item dir4 = new Directory("workspace", MakeList(
        new File("notes.md", 1500),
        new Directory("images", MakeList(new File("cat.png", 20480), new File("dog.png", 18000)))));
 * 4. Design Strategy: Template application
 * 5. Implementation
 */
sealed interface Item permits File, Directory {
    String name();
}
/** Code template for the Item general itemization
 * {
 * ...
 * ... return switch(item) {
 *        case File(String name, int size) -> ...;
 *        case Directory(String name, ConsList<Item> items) -> ...;
 *     }...; 
 * }
 */

/**
 * A file is a simple item. It has a name and a size measured in bytes. 
 * It cannot contain other items.
 * Examples:
 * - file1 = new File("empty.log", 0)   
 *   An empty file (0 bytes)
 * - file2 = new File("1110.txt", 1200)
 *   A non-empty file (1200 bytes)
 * @param name - The name of File.
 * @param size - The size of the file in bytes (must be greater than or equal to 0).
 */
record File(String name, int size) implements Item {}
/**
 * ... file.name() ...
 * ... file.size() ...
 */

/**
 * A directory (also known as a folder) is a more complex item. 
 * It has a name, and can contain both files and other directories.
 * A directory can be empty, that is, it might not contain other file system items.
 * Examples:
 * - dir1 = new Directory("emptyFolder", MakeList());
 *   An empty directory
 * - dir2 = new Directory("1110", MakeList(new File("notes.txt", 517), MakeList(new File("workshop.pdf", 4000), new File("assign.pdf", 2000)))
 *   A directory containing one file and another directory with two files.
 * @param name - The name of Directory.
 * @param items - The items of Directory, which can be both file and other directory (also can be an empty list).
 */
record Directory(String name, ConsList<Item> items) implements Item {}
/**
 * ... directory.name() ...
 * ... ConsList.Cons<Item>(item, directory.items()) ...
 */

// 2. Testing interface
// To interpret the item [I]
boolean isFile(Item item) {
    return switch (item) {
        case File f -> true;
        case Directory d -> false;
    };
}

boolean isDirectory(Item item) {
    return switch (item) {
        case File f -> false;
        case Directory d -> true;
    };
}

// To construct file system items for testing
Item makeFile(String name, int size) {
    return new File(name, size);
}

Item makeEmptyDirectory(String name) {
    return new Directory(name, MakeList());
}

void testIsFile_File() {
    File f = new File("1110.txt", 1200); 
    Item file = f; 
    testEqual(true, isFile(file), "isFile() should return true for a file item.");
    testEqual("1110.txt", f.name(), "File name should be stored.");
    testEqual(1200, f.size(), "File size should be stored.");
}

void testIsFile_Directory() {
    Item dir = makeEmptyDirectory("emptyFolder");
    testEqual(false, isFile(dir), "isFile() should return false for a directory item.");
    testEqual("emptyFolder", dir.name(), "Directory name should be preserved.");
}

void testIsDirectory_Directory() {
    // Empty directory case
    Item empty = makeEmptyDirectory("emptyFolder");
    testEqual(true, isDirectory(empty), "isDirectory() should return true for a directory item.");

    // Non-empty directory should still be recognized as a directory
    Directory nonEmpty = new Directory("nonEmpty", MakeList(new File("a.txt", 1)));
    testEqual(true, isDirectory(nonEmpty), "Non-empty directory should also be recognized as a directory.");
}

void testIsDirectory_File() {
    Item file = makeFile("notes.txt", 517);
    testEqual(false, isDirectory(file), "isDirectory() should return false for a file item.");
}

void testMakeFile_SizeZero() {
    File f0 = new File("empty.txt", 0);
    testEqual(true, isFile(f0), "makeFile() with size 0 should still be a valid file.");
    testEqual("empty.txt", f0.name(), "File name should be stored.");
    testEqual(0, f0.size(), "Zero size should be preserved.");
}

void testMakeFile_HugeSize() {
    File huge = new File("huge.bin", 2147483647); // Integer.MAX_VALUE
    testEqual(true, isFile(huge), "File with very large size should still be valid.");
    testEqual(2147483647, huge.size(), "Large size should be preserved.");
}

void testMakeEmptyDirectory_IsEmpty() {
    Item dir = makeEmptyDirectory("newFolder");
    testEqual(true, isDirectory(dir), "makeEmptyDirectory() should create a directory.");
}



// 3. Populating a Directory
/**
 * 1. Problem Analysis and Data Definitions
 * We need to add an Item (file or directory) into a Directory’s items.
 * 2. Function Purpose Statement and Signature
 * Adds a given item (file or subdirectory) to a directory. (prepend)
 * Directory addItemToDirectory(Directory directory, Item item)
 * 3. Examples:
 *    - Given: Directory dir0 = new Directory("root", MakeList());
 *             First, Add to empty directory: Item fileA = new File("a.txt", 1200);
 *      Expect: Directory dir1 = addItemToDirectory(dir0, fileA);
 *              [a.txt]
 *    - Given: Add second file: Item fileB = new File("b.txt", 900);
 *      Expect: Directory dir2 = addItemToDirectory(dir1, fileB);
 *              [b.txt, a.txt]
 *    - Given: Add a subdirectory: Item docs = new Directory("docs", MakeList(new File("notes.txt", 517)));
 *      Expect: Directory dir3 = addItemToDirectory(dir2, docs);
 *              [docs, b.txt, a.txt]
 * 4. Design Strategy: Simple expression
 * 5. Implementation
 * @param directory The directory to which the item will be added.
 * @param item      The item to add to the directory. It can be a file or another directory.
 * @return          Returns a new Directory, which contains all original items plus the new item at the front.
 */
Directory addItemToDirectory(Directory directory, Item item) {
    return new Directory(
        directory.name(), 
        new Cons<Item>(item, directory.items())
    );
}

/**
 * 1. Problem Analysis and Data Definitions
 * We want to fetch the name of the Item at a given index in a ConsList<Item>.
 * 2. Function Purpose Statement and Signature
 * Returns the name of the element at position index in the list.
 * String getNameAt(ConsList<Item> items, int index)
 * 3. Examples:
 *    - getNameAt([a.txt, b.txt], 0) ==> "a.txt"
 *    - getNameAt([a.txt, b.txt], 1) ==> "b.txt"
 *    - getNameAt([], 0) ==> "" 
 * 4. Design Strategy: Template application and Combining functions
 * 5. Implementation
 * @param items ConsList<Item>, consist of case Nil<Item> and Cons<Item>
 * @param index position in the list, begin of 0
 * @return      the name of the Item at index
 */
String getNameAt(ConsList<Item> items, int index) {
    if (LessThan(index, 0)) return "";
    return switch(items) {
        case Nil<Item>() -> "";
        case Cons<Item>(var first, var rest) -> indexAt(first, rest, index);
    };
}

/**
 * 1. Problem Analysis and Data Definitions
 * Helper function for getNameAt: if index is 0, return current Item’s name;
 * otherwise recurse on rest with index-1.
 * 2. Function Purpose Statement and Signature
 * Returns the name of the Item at the given index, given current first item and rest elements.
 * String indexAt(Item first, ConsList<Item> rest, int index)
 * 3. Examples:
 *    - indexAt([a.txt], [b.txt], 0) ==> "a.txt"
 *    - indexAt([a.txt], [b.txt], 1) ==> "b.txt"
 * 4. Design Strategy: Case distinction and Combining functions
 * 5. Implementation
 * @param first the current first Item
 * @param rest  the rest elements of the list
 * @param index target index
 * @return      the name of the Item at index
 */
String indexAt(Item first, ConsList<Item> rest, int index) {
    if (Equals(index,0)) {
        return first.name();
    } else {
        return getNameAt(rest, index-1);
    }
}

void testAddItemToDirectory() {
    // Step 1: empty
    Directory dir0 = new Directory("root", MakeList());
    testEqual(0, Length(dir0.items()), "Initial empty directory should have 0 items.");

    // Step 2: add file, a.txt
    Item fileA = new File("a.txt", 1200);
    Directory dir1 = addItemToDirectory(dir0, fileA);
    testEqual(1, Length(dir1.items()), "After adding first file, length should be 1.");
    testEqual("a.txt", getNameAt(dir1.items(), 0), "First item should be a.txt.");
    // Immutability: dir0 unchanged
    testEqual(0, Length(dir0.items()), "dir0 must still be empty after creating dir1."); 

    // Step 3: add file, b.txt (prepend)
    Item fileB = new File("b.txt", 900);
    Directory dir2 = addItemToDirectory(dir1, fileB);
    testEqual(2, Length(dir2.items()), "After adding second file, length should be 2.");
    testEqual("b.txt", getNameAt(dir2.items(), 0), "First item should now be b.txt (prepend).");
    testEqual("a.txt", getNameAt(dir2.items(), 1), "Second item should still be a.txt.");
    // Immutability: dir1 unchanged
    testEqual(1, Length(dir1.items()), "dir1 should still contain exactly one item after creating dir2."); 
    testEqual("a.txt", getNameAt(dir1.items(), 0), "dir1 head remains a.txt.");

    // Step 4: add directory, docs (prepend)
    Item docs = new Directory("docs", MakeList(makeFile("notes.txt", 517)));
    Directory dir3 = addItemToDirectory(dir2, docs);
    testEqual(3, Length(dir3.items()), "After adding a directory, length should be 3.");
    testEqual("docs", getNameAt(dir3.items(), 0), "First item should now be docs (prepend).");
    testEqual("b.txt", getNameAt(dir3.items(), 1), "Second item should be b.txt.");
    testEqual("a.txt", getNameAt(dir3.items(), 2), "Third item should be a.txt.");
    testEqual("root", dir3.name(), "Directory name should remain unchanged.");
    // Immutability: dir2 unchanged
    testEqual(2, Length(dir2.items()), "dir2 should still contain two items after creating dir3.");
    testEqual("b.txt", getNameAt(dir2.items(), 0), "dir2 head remains b.txt.");
    testEqual("a.txt", getNameAt(dir2.items(), 1), "dir2 tail remains a.txt.");

    // Step 5: add an empty subdirectory (prepend)
    Item emptyDir = new Directory("empty", MakeList());
    Directory dir4 = addItemToDirectory(dir3, emptyDir);
    testEqual(4, Length(dir4.items()), "Adding an empty subdirectory should still increase the count.");
    testEqual("empty", getNameAt(dir4.items(), 0), "New head should be the empty directory.");
    testEqual("docs",  getNameAt(dir4.items(), 1), "Second should be docs.");
    testEqual("b.txt", getNameAt(dir4.items(), 2), "Third should be b.txt.");
    testEqual("a.txt", getNameAt(dir4.items(), 3), "Fourth should be a.txt.");
    testEqual("root", dir4.name(), "Directory name should remain unchanged for the new directory too.");
    // Immutability: dir3 unchanged
    testEqual(3, Length(dir3.items()), "dir3 should still contain three items after creating dir4.");
    testEqual("docs", getNameAt(dir3.items(), 0), "dir3 head remains docs.");

    // Boundary tests: Empty list -> always ""
    ConsList<Item> empty = MakeList();
    testEqual("", getNameAt(empty, 0),  "Empty list: index 0 should return empty string.");
    testEqual("", getNameAt(empty, -1), "Empty list: negative index should return empty string.");

    // Boundary tests: Single-element list: index 0, index 1 out-of-bounds
    ConsList<Item> one = MakeList(new File("solo.txt", 42));
    testEqual("solo.txt", getNameAt(one, 0), "Single-element list index 0 should return the element.");
    testEqual("", getNameAt(one, 1), "Single-element list index 1 is out-of-bounds -> empty string.");

    // Boundary tests: Size-2 list: index equal to length is out-of-bounds
    ConsList<Item> two = MakeList(new File("a.txt", 1), new File("b.txt", 2));
    int len = Length(two);
    testEqual("", getNameAt(two, len), "Index equal to length is out-of-bounds -> empty string.");

    // Boundary tests: out-of-bounds / negative on a non-empty list
    testEqual("", getNameAt(dir3.items(), 99), "Far out-of-bounds should return empty string.");
    testEqual("", getNameAt(dir3.items(), -1), "Negative index should return empty string.");
}



// 4. Calculating Total Size
/**
 * 1. Problem Analysis and Data Definitions
 * Calculates the total size of a given file system item in bytes.
 * - If the item is a file, its size is its own defined size in bytes.
 * - If the item is a directory, its size is the sum of the sizes
 *   of all items it contains, calculated recursively, in bytes.
 * 2. Function Purpose Statement and Signature
 * Returns the total size in bytes of a file-system item.
 * int calculateSize(Item item)
 * 3. Examples:
 *    - given: new Directory("empty", MakeList())               
 *      expect: 0
 *    - given: new File("a.txt", 1200)         
 *      expect: 1200
 *    - given: new Directory("flat", MakeList(new File("a", 3), new File("b", 7)))   
 *      expect: 10
 *    - given: new Directory("nested", MakeList(new File("a", 5), 
 *                                              new Directory("docs", MakeList(new File("n.txt", 12))), 
 *                                              new File("b", 8)))                               
 *      expect: 25
 * 4. Design Strategy: Template application
 * 5. Implementation
 * @param item The file system item to calculate the size of.
 * @return The total size in bytes.
 */
int calculateSize(Item item) {
    return switch(item) {
        case File(var name, var size) -> size;
        case Directory(var name, var items) -> sumSizes(items);
    };
}

/**
 * 1. Problem Analysis and Data Definitions
 * Helper function for ConsList<Item> that sums sizes of all elements recursively.
 * 2. Function Purpose Statement and Signature
 *  Calculate sum of ConsList<Item>.
 * int sumSizes(ConsList<Item> items)
 * 3. Examples:
 *    - given: []                                 expect: 0
 *    - given: [File("a", 1200)]                  expect: 1200
 *    - given: [File("a", 1200), File("b", 3000)] expect: 4200
 * 4. Design Strategy: Template application
 * 5. Implementation
 * @param items A ConsList<Item> of file system items.
 * @return The sum of sizes of all items in bytes.
 */
int sumSizes(ConsList<Item> items) {
    return switch(items) {
        case Nil<Item>() -> 0;
        case Cons<Item>(var first, var rest) -> calculateSize(first) + sumSizes(rest);
    };
}

// Test1: file returns its own size
void testCalculateSize_File() {
    Item f = new File("a.txt", 1200);
    testEqual(1200, calculateSize(f), "A file's size should be its stored size.");
}

// Test2: empty directory is size 0
void testCalculateSize_EmptyDir() {
    Item d = new Directory("empty", MakeList());
    testEqual(0, calculateSize(d), "Empty directory size should be 0.");
}

// Test3: flat directory sum
void testCalculateSize_FlatDir() {
    Item flat = new Directory("flat", MakeList(new File("a", 3), new File("b", 7)));
    testEqual(10, calculateSize(flat), "Flat directory size should be sum of files (3+7).");
}

// Test4: single nested directory
void testCalculateSize_NestedDir() {
    Item nested = new Directory("nested",
            MakeList(
                new File("a", 5),
                new Directory("docs", MakeList(new File("n.txt", 12))),
                new File("b", 8)
            ));
    testEqual(25, calculateSize(nested), "Nested directory size should be 5 + 12 + 8 = 25.");
}

// Test5: deeper nesting
void testCalculateSize_DeepNested() {
    Item deep = new Directory("root",
            MakeList(
                new File("a", 1),
                new Directory("L1", MakeList(
                    new File("b", 2),
                    new Directory("L2", MakeList(
                        new File("c", 3),
                        new Directory("L3", MakeList(new File("d", 4)))
                    ))
                ))
            ));
    testEqual(10, calculateSize(deep), "Deeply nested size should sum all leaf files, which should be 1+2+3+4 = 10.");
}

// Edge: zero-size file contributes 0
void testCalculateSize_FileZero() {
    Item f0 = new File("zero.bin", 0);
    testEqual(0, calculateSize(f0), "Zero-size file should contribute 0.");
}

// Edge: directory with all zero-size files
void testCalculateSize_FlatAllZero() {
    Item flatZero = new Directory("zeros",
        MakeList(new File("a", 0), new File("b", 0), new File("c", 0)));
    testEqual(0, calculateSize(flatZero), "All-zero files should sum to 0.");
}

// Edge: directory tree that contains only empty subdirectories
void testCalculateSize_NestedEmptyDirsOnly() {
    Item onlyEmptyDirs =
        new Directory("root",
            MakeList(
                new Directory("e1", MakeList()),
                new Directory("e2",
                    MakeList(
                        new Directory("e21", MakeList()),
                        new Directory("e22", MakeList())
                    )
                )
            )
        );
    testEqual(0, calculateSize(onlyEmptyDirs), "Nested empty directories should have total size 0.");
}

// Edge: large numbers within int range
void testCalculateSize_LargeButWithinInt() {
    // 2_000_000_000 + 147_483_647 = 2_147_483_647 (Integer.MAX_VALUE)
    Item big = new Directory("big", MakeList(new File("x", 2_000_000_000), new File("y", 147_483_647)));
    testEqual(2_147_483_647, calculateSize(big), "Sum should reach Integer.MAX_VALUE without overflow.");
}

// Property: size(addItem(dir, it)) == size(dir) + size(it)
void testCalculateSize_AddItemIncreasesByItemSize() {
    Directory base = new Directory("root", MakeList(new File("a", 3), new File("b", 7))); // size 10
    Item added = new Directory("docs", MakeList(new File("n.txt", 12), new File("m.txt", 8))); // size 20
    Directory result = addItemToDirectory(base, added);
    testEqual(calculateSize(base) + calculateSize(added), calculateSize(result), 
        "Adding an item should increase directory size by that item's size.");
}

// Property: adding an empty directory doesn't change size
void testCalculateSize_AddEmptyDirNoChange() {
    Directory base = new Directory("root", MakeList(new File("a", 5)));
    Item empty = new Directory("empty", MakeList()); // size 0
    Directory result = addItemToDirectory(base, empty);
    testEqual(calculateSize(base), calculateSize(result), 
        "Adding an empty directory should not change total size.");
}



// 5. Finding an Item
/**
 * 1. Problem Analysis and Data Definitions
 * We need to can find an item anywhere in a file system based on its name.
 * 2. Function Purpose Statement and Signature
 * Recursively searches the file system rooted at initialItem for an entry with the given name.
 * Returns the first match.
 * Maybe<Item> findByName(String name, Item initialItem)
 * 3. Examples:
 *   - Given: findByName("a.txt", new File("a.txt", 1))
 *     Expect: Something(File("a.txt", 1))
 *   - Given: findByName("empty", new Directory("empty", MakeList()))
 *     Expect: Something(Directory("empty", []))
 *   - Given:  findByName("x", new Directory("root", MakeList()))
 *     Expect: Nothing
 *   - Given:  findByName("n.txt", Directory("root", MakeList(new File("a",5),
 *                                                   new Directory("docs", MakeList(new File("n.txt",12))))))
 *     Expect: Something(File("n.txt",12))
 *   - Given duplicates: Directory("root", [ Directory("dup",[]), File("dup",9) ])
 *                       findByName("dup", root)
 *     Expect: returns the Directory (the first match).
 * 4. Design Strategy: Template application and Combining functions
 * 5. Implementation:
 * Uses `Equals`  for String equality.
 * @param name The name of the file or directory to search for.
 * @param initialItem The file system item where the search should begin.
 * @return The first item found with the matching name, 
 *         or Nothing if no such item exists in the file system rooted at initialItem.
 */
Maybe<Item> findByName(String name, Item initialItem) {
    return switch(initialItem) {
        case File(var n, var size) ->
            Equals(n, name) ? new Something<Item>(initialItem) 
                            : new Nothing<Item>();
        case Directory(var n, var items) ->
            Equals(n, name) ? new Something<Item>(initialItem) 
                            : findByNameInList(name, items);
    };
}

/**
 * 1. Problem Analysis and Data Design
 * We need search a ConsList<Item> from left to right and return the first match.
 * 2. Function Purpose Statement and Signature
 * Maybe<Item> findByNameInList(String name, ConsList<Item> items)
 * Returns the first matching item, or Nothing.
 * 3. Examples:
 *   - Given: name "x", items []
 *     Expect: Nothing
 *   - Given: name "a", items [ File("a",1), File("b",2) ]
 *     Expect: Something(File("a",1)) 
 *   - Given: name "n", items [ Directory("d",[File("n",3)]), File("n",4) ]
 *     Expect: Something(File("n",3))
 * 4. Design Strategy: 
 * Template application and Combining functions
 * 5. Implementation
 * @param name  Target name to look for.
 * @param items A ConsList<Item> (Nil or Cons).
 * @return Something if a matching item is found in left-to-right order, otherwise Nothing.
 */
Maybe<Item> findByNameInList(String name, ConsList<Item> items) {
    return switch(items){
        case Nil<Item> () -> new Nothing<Item>();
        case Cons<Item>(Item first, ConsList<Item> rest) -> 
            Equals(findByName(name, first), new Nothing<Item>()) 
                ? findByNameInList(name, rest)
                : findByName(name, first);
    };
}

void testfindByName() {
    // 1) FileMatch: root is a file with the same name -> should return Something(file)
    Item f1 = new File("a.txt", 1);
    testEqual(new Something<Item>(f1), findByName("a.txt", f1),
                "Should match a file with the same name at the root.");

    // 2) FileNoMatch: root is a file with a different name -> should return Nothing
    Item f2 = new File("a.txt", 1);
    testEqual(new Nothing<Item>(), findByName("b.txt", f2),
                "Should NOT match a file with a different name.");

    // 3) DirectoryMatch: root is a directory with the same name -> should return Something(dir)
    Item d1 = new Directory("docs", MakeList());
    testEqual(new Something<Item>(d1), findByName("docs", d1),
                "Should match a directory with the same name at the root.");

    // 4) EmptyDirectory: searching an empty directory for a missing name -> should return Nothing
    Item d2 = new Directory("empty", MakeList());
    testEqual(new Nothing<Item>(), findByName("nonexistent", d2),
                "Searching an empty directory for a non-existent name should yield Nothing.");

    // 5) RecursiveSearch: target only exists in children -> should find nested file
    Item nestedFile = new File("target.txt", 5);
    Item d3 = new Directory("docs", MakeList(nestedFile));
    testEqual(new Something<Item>(nestedFile), findByName("target.txt", d3),
              "Should search recursively into children and find a nested file.");
    
    // 6) RootBeatsChild: both root and a child have the same name -> should return root first
    Item rootDup = new Directory("dup", MakeList(new File("dup", 1)));
    testEqual(new Something<Item>(rootDup), findByName("dup", rootDup),
                "When root matches, it should be returned before searching children.");
    
    // 7) Two siblings have the same name; file comes before directory -> should return the file (first match)
    Item fileFirst = new File("dup", 1);
    Item dirSecond = new Directory("dup", MakeList());
    Item root = new Directory("root", MakeList(fileFirst, dirSecond));
    testEqual(new Something<Item>(fileFirst), findByName("dup", root),
                "When duplicates exist at the same level, return the first occurrence (file before dir).");

    // 8) LeftToRightOrder: both left and right subtrees contain a match -> should return left match
    Item tLeft = new File("target", 1);
    Item left = new Directory("L", MakeList(tLeft));
    Item tRight = new File("target", 2);
    Item right = new Directory("R", MakeList(tRight));
    Item rootLR = new Directory("root", MakeList(left, right));
    testEqual(new Something<Item>(tLeft), findByName("target", rootLR),
              "Should return the match from the left subtree before the right subtree.");

    // 9) Left subtree has no match; right subtree contains the target -> should return the right match
    Item rightHit = new File("hit", 1);
    Item leftNoHit = new Directory("L", MakeList(new File("x", 2)));
    Item rightOnly = new Directory("R", MakeList(rightHit));
    Item rootRight = new Directory("root", MakeList(leftNoHit, rightOnly));
    testEqual(new Something<Item>(rightHit), findByName("hit", rootRight),
              "Should continue to the right subtree when the left subtree has no match.");
    
    // 10) NotFoundDeep: deep structure with no matching name -> should return Nothing
    Item deep = new Directory("root",
                    MakeList(new Directory("L1",
                            MakeList(new Directory("L2",
                                    MakeList(new File("x", 1)))
                            ))
                    ));
    testEqual(new Nothing<Item>(), findByName("zzz", deep),
              "Should return Nothing when no matching name exists anywhere.");
}


void testfindByNameInList() {
    // Empty
    ConsList<Item> empty = MakeList();
    testEqual(new Nothing<Item>(), findByNameInList("x", empty), "Empty list should return Nothing.");

    // Single file
    Item sFile = new File("a.txt", 1);
    ConsList<Item> list1 = MakeList(sFile);
    testEqual(new Something<Item>(sFile), findByNameInList("a.txt", list1),
                "Singleton list should return its only matching element.");
    testEqual(new Nothing<Item>(), findByNameInList("b.txt", list1),
                "Singleton list miss should return Nothing.");

    // Multiple items: should return the first match
    Item f1 = new File("a.txt", 1);
    Item f2 = new File("a.txt", 2);
    ConsList<Item> list2 = MakeList(f1, f2);
    testEqual(new Something<Item>(f1), findByNameInList("a.txt", list2),
                "Should return the first matching element from left to right.");

    // Directory subtree match should prefer the first
    Item nestedFile = new File("n", 3);
    Item dirWithTarget = new Directory("d", MakeList(nestedFile));
    Item laterSameName = new File("n", 4);
    ConsList<Item> list3 = MakeList(dirWithTarget, laterSameName);
    testEqual(new Something<Item>(nestedFile), findByNameInList("n", list3),
                "Should return the match from the left directory subtree.");

    // Left directory has no match, continue to the right
    Item noHitFile = new File("zzz", 1);
    Item emptyDir  = new Directory("empty", MakeList());
    Item deepHit   = new File("hit.txt", 2);
    Item dirHit    = new Directory("folder", MakeList(deepHit));
    ConsList<Item> list4 = MakeList(emptyDir, noHitFile, dirHit);
    testEqual(new Something<Item>(deepHit), findByNameInList("hit.txt", list4),
                "Should continue past non-matching left elements.");

    // Duplicates at same level: return the first occurrence */
    Item dup1 = new File("dup", 1);
    Item dup2 = new File("dup", 2);
    ConsList<Item> list5 = MakeList(dup1, dup2);
    testEqual(new Something<Item>(dup1), findByNameInList("dup", list5),
                "Should return the first duplicate.");

    // Deeply nested match (two levels)
    Item deep = new File("deep", 42);
    Item inner = new Directory("inner", MakeList(deep));
    Item outer = new Directory("outer", MakeList(inner));
    ConsList<Item> list6 = MakeList(outer);
    testEqual(new Something<Item>(deep),
              findByNameInList("deep", list6),
              "Should find within deeper files.");

    // No match anywhere
    Item otherFile = new File("a.txt", 1);
    Item emptyDir2  = new Directory("docs", MakeList());
    ConsList<Item> list7 = MakeList(otherFile, emptyDir2);
    testEqual(new Nothing<Item>(), findByNameInList("notfound.txt", list7),
                "Should return Nothing when no element matches.");
}

void test() {
    // 2. isFile, isDirectory, makeFile, makeEmptyDirectory tests
    runAsTest(this::testIsFile_File);
    runAsTest(this::testIsFile_Directory);
    runAsTest(this::testIsDirectory_Directory);
    runAsTest(this::testIsDirectory_File);
    runAsTest(this::testMakeFile_SizeZero);
    runAsTest(this::testMakeFile_HugeSize);
    runAsTest(this::testMakeEmptyDirectory_IsEmpty);

    // 3. addItemToDirectory tests
    runAsTest(this::testAddItemToDirectory);

    // 4. calculateSize tests
    runAsTest(this::testCalculateSize_File);
    runAsTest(this::testCalculateSize_EmptyDir);
    runAsTest(this::testCalculateSize_FlatDir);
    runAsTest(this::testCalculateSize_NestedDir);
    runAsTest(this::testCalculateSize_DeepNested);
    runAsTest(this::testCalculateSize_FileZero);
    runAsTest(this::testCalculateSize_FlatAllZero);
    runAsTest(this::testCalculateSize_NestedEmptyDirsOnly);
    runAsTest(this::testCalculateSize_LargeButWithinInt);
    runAsTest(this::testCalculateSize_AddItemIncreasesByItemSize);
    runAsTest(this::testCalculateSize_AddEmptyDirNoChange);

    // 5. findByName tests
    runAsTest(this::testfindByName);
    runAsTest(this::testfindByNameInList);
}

void main() {
    CheckVersion("2025S1-7");
    test();
}