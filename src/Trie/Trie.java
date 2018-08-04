package Trie;

import java.util.ArrayList;
import java.util.List;

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
        char[] word = name.toCharArray();
        TrieNode<T> node = root;

        for (char c : word) {
            // If the correct child node doesn't exist, make it.
            if(!node.getChildren().containsKey(c)) {
                node.getChildren().put(c, new TrieNode<T>(node.getPrefix() + c));
            }

            // Move onto the child node
            node = node.getChildren().get(c);
        }

        // Add the data to the node
        node.getData().add(data);
    }

    /**
     * Gets all objects which start with the prefix.
     *
     * @param name - The prefix to be searched for
     * @return An ArrayList containing all objects which start with the prefix
     */
    public ArrayList<T> get(String name) {
        char[] word = name.toCharArray();
        TrieNode<T> node = root;

        for(char c : word) {
            // If there is no correct child, return null
            if(!node.getChildren().containsKey(c)) {return null;}
            // Otherwise mode on to the child
            node = node.getChildren().get(c);
        }
        // Return the data
        return node.getData();
    }

    /**
     * Returns a list containing all the objects contained by the node which contains the name (and all that node's children)
     * @param name - The name of the object to be searched
     * @return A list containing all objects which begin with the searched name.
     */
    public ArrayList<T> getAll(String name) {
        ArrayList<T> results = new ArrayList<T>();
        char[] word = name.toCharArray();
        TrieNode<T> node = root;

        for(char c : word) {
            // If there is no correct child, return null
            if(!node.getChildren().containsKey(c)) {return null;}
            // Otherwise mode on to the child
            node = node.getChildren().get(c);
        }

        getAllFrom(node, results);
        return results;
    }

    /**
     * Adds all objects contained in the node, and all it's child nodes to the "results" list.
     * @param node - The node to get all objects from
     * @param results - The list to add all the objects to
     */
    public void getAllFrom(TrieNode<T> node, List<T> results) {
        results.addAll(node.getData());

        node.getChildren().keySet().stream()
                .map(k->node.getChildren().get(k))
                .forEach(child->getAllFrom(child, results));
    }

}
