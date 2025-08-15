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
 *    - file1 = new File("1110.txt", 1200) 
 *    - file2 = new Directory("emptyFolder", MakeList())
 * 4. Design Strategy: Template application
 * 5. Implementation
 */
sealed interface Item permits File, Directory {}
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
    Item file = makeFile("1110.txt", 1200); 
    testEqual(true, isFile(file), "isFile() should return true for a file item.");
}

void testIsFile_Directory() {
    Item dir = makeEmptyDirectory("emptyFolder");
    testEqual(false, isFile(dir), "isFile() should return false for a directory item.");
}

void testIsDirectory_Directory() {
    Item dir = makeEmptyDirectory("emptyFolder");
    testEqual(true, isDirectory(dir),  "isDirectory() should return true for a directory item.");
}

void testIsDirectory_File() {
    Item file = makeFile("notes.txt", 517);
    testEqual(false, isDirectory(file), "isDirectory() should return false for a file item.");
}

void testMakeFile_SizeZero() {
    Item file = makeFile("empty.txt", 0);
    testEqual(true, isFile(file), "makeFile() with size 0 should still be a valid file.");
}

void testMakeEmptyDirectory_IsEmpty() {
    Item dir = makeEmptyDirectory("newFolder");
    testEqual(true, isDirectory(dir), "makeEmptyDirectory() should create a directory.");
}

void test() {
    // Tests for isFile, isDirectory, makeFile, makeEmptyDirectory function.
    runAsTest(this::testIsFile_File);
    runAsTest(this::testIsFile_Directory);
    runAsTest(this::testIsDirectory_Directory);
    runAsTest(this::testIsDirectory_File);
    runAsTest(this::testMakeFile_SizeZero);
    runAsTest(this::testMakeEmptyDirectory_IsEmpty);
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
 *    - Given: Add another file: Item fileB = new File("b.txt", 90 0);
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
        ConsList.Cons<Item>(item, directory.items())
    );
}

int length(ConsList<Item> items) {
    return switch(items) {
        case Nil<Item>() -> 0;
        case Cons<Item>(var element, var rest) -> 1 + Length(rest);
    }
}

String getName(ConsList<Item> items, int index) {
    return switch(items) {
        case Nil<Item>() -> ;
        case Cons<Item>(var element, var rest) -> ;
    }
}

void testAddItemToDirectory() {
    // Step 1: empty
    Directory dir0 = new Directory("root", MakeList());
    testEqual(0, length(dir0.items()), "Initial empty directory should have 0 items.");

    // Step 2: add file, a.txt
    Item fileA = new File("a.txt", 1200);
    Directory dir1 = addItemToDirectory(dir0, fileA);
    testEqual(1, length(dir1.items()), "After adding first file, length should be 1.");
    testEqual("a.txt", getNameAt(dir1.items(), 0), "First item should be a.txt.");

    // Step 3: add file, b.txt
    Item fileB = new File("b.txt", 900);
    Directory dir2 = addItemToDirectory(dir1, fileB);
    testEqual(2, length(dir2.items()), "After adding second file, length should be 2.");
    testEqual("b.txt", getNameAt(dir2.items(), 0), "First item should now be b.txt (prepend).");
    testEqual("a.txt", getNameAt(dir2.items(), 1), "Second item should still be a.txt.");

    // Step 4: add directory, docs
    Item docs = new Directory("docs", MakeList(new File("notes.txt", 517)));
    Directory dir3 = addItemToDirectory(dir2, docs);
    testEqual(3, length(dir3.items()), "After adding a directory, length should be 3.");
    testEqual("docs", getNameAt(dir3.items(), 0), "First item should now be docs (prepend).");
    testEqual("b.txt", getNameAt(dir3.items(), 1), "Second item should be b.txt.");
    testEqual("a.txt", getNameAt(dir3.items(), 2), "Third item should be a.txt.");
}



// 4. Calculating Total Size
/**
 * Calculates the total size of a given file system item in bytes.
 * - If the item is a file, its size is its own defined size in bytes.
 * - If the item is a directory, its size is the sum of the sizes
 *   of all items it contains, calculated recursively, in bytes.
 *
 * @param item The file system item to calculate the size of.
 * @return The total size in bytes.
 */
//int calculateSize([I] item)


// 5. Finding an Item#
/**
 * Recursively searches the file system rooted at 
 * initialItem for an entry with the given name.
 *
 * @param name The name of the file or directory 
 *             to search for.
 * @param initialItem The file system item where the search 
 *                    should begin.
 * @return The first item found with the matching name, 
 *         or Nothing if no such item exists in the file 
 *         system rooted at initialItem.
 */
//Maybe<[I]> findByName(String name, [I] initialItem)


void main() {
    test();
}