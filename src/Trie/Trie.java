package Trie;

import java.util.ArrayList;

/**
 * A trie is an ordered tree data structure.
 * It is being used to store road names to make it easy to search for a road.
 * This is the containing class for a TrieNode.
 *
 * This is an ordered tree, which indexes its nodes by the
 * order of their prefix. It is used to be able to search
 * up an object, and find it without looking through every
 * object which this contains.
 *
 * @param <T> - The class which is being indexed
 */
public class Trie<T> {

    // The root node
    private TrieNode<T> root;

    /**
     * Create a Trie.
     * All this does is initialise the root node.
     */
    public Trie() {
        root = new TrieNode<T>("");
    }

    /**
     * Adds the data to the Trie.
     *
     * @param name - The name which the data is to be indexed by
     * @param data - The data to be indexed
     */
    public void add(String name, T data) {
        root.add(name, data);
    }

    /**
     * Gets all objects which start with the prefix.
     *
     * @param name - The prefix to be searched for
     * @return An ArrayList containing all objects which start with the prefix
     */
    public ArrayList<T> get(String name) {
        return root.get(name);
    }

}
