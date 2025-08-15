import comp1110.lib.*;
import comp1110.lib.Date;
import static comp1110.lib.Functions.*;
import static comp1110.testing.Comp1110Unit.*;

// 1. Designing the File System Model
/**
 * [I]: Represents any item in the file system.
 * A file system consists of two kind of items: file and directory.
 *  - File, a simple item, cannot contain other items.
 *  - Directory, a more complex item, can contain both files and other directories.
 */
sealed interface Item permits File, Directory {}

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


// 2. Testing interface
// To interpret the item [I]
boolean isFile(Item item) {
    return item == Item.File;
}

boolean isDirectory(Item item) {
    return item == Item.Directory;
}


// To construct file system items for testing
/**
 * [I] makeFile(String name, int size);
 */
Item makeFile(String name, int size) {
    return new File(name, size);
}

/**
 * [I] makeEmptyDirectory(String name);
 */
Item makeEmptyDirectory(String name) {
    return new Directory(name, MakeList());
}


// 3. Populating a Directory
/**
 * Adds a given item (file or subdirectory) to a directory.
 *
 * @param directory The directory to which the item will be added.
 * @param item      The item to add to the directory. It can be a file or another directory.
 * @return The directory containing all the original items plus the new item.
 */
[D] addItemToDirectory([D] directory, [I] item);


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
int calculateSize([I] item)


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
Maybe<[I]> findByName(String name, [I] initialItem)


void main() {

}

void test() {

}
