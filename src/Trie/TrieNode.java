package Trie;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is an ordered tree, which indexes its nodes by the
 * order of their prefix. It is used to be able to search
 * up an object, and find it without looking through every
 * object which this contains.
 *
 * @param <T> - The class which is being indexed
 */
public class TrieNode<T> {

    // The children of this node
    private HashMap<Character, TrieNode<T>> children = new HashMap<Character, TrieNode<T>>();

    // The data held by the node (allows multiple of the same data.
    private ArrayList<T> data;

    // Whether or not this node actually holds data
    private boolean holdsData = false;

    // The prefix up until here (including this node)
    private String prefix;

    /**
     * Creates the node, data is not required,
     * although the alternate constructor allows for data to be entered at the same time
     *
     * @param prefix - The prefix up until here (including this node)
     */
    public TrieNode(String prefix) {
        this.prefix = prefix;
        this.data = new ArrayList<T>();
    }

    /**
     * Creates the node while adding data at the same time.
     * The data will not always be stored in this node, it may be stored in the branches which
     * this node is the root of.
     *
     * @param prefix - The prefix up until here (including this node)
     * @param data - The data that this node is being added to organise
     */
    public TrieNode(String prefix, T data) {
        this.prefix = prefix;
        this.data = new ArrayList<T>();
        this.add(prefix, data);
    }

    /**
     * This will either add the data to this TrieNode,
     * or it will create a new TrieNode in the children collection
     * which the data will be added to.
     *
     * @param name - The name of the data
     * @param data - The data itself
     */
    public void add(String name, T data) {
        // If this is the right place, add the data.
        if(prefix.toLowerCase().equals(name.toLowerCase())) { // I want it to be case insensitive
            this.data.add(data);
            holdsData = true;
        }

        if(name.length() > prefix.length() && name.toLowerCase().contains(prefix.toLowerCase())) { // Just check that we're not having an error
            char c = name.toLowerCase().charAt(prefix.length());
            children.put(c, new TrieNode<T>(prefix + c, data));
        }
    }

    /**
     * Returns the object corresponding to the name provided,
     * contained in an ArrayList in case of duplicate objects.
     * Can also return null if the object is not contained in the trie.
     *
     * @param name - The name of the object we are looking for
     * @return An array list containing the object(s) we are looking for, or null.
     */
    public ArrayList<T> get(String name) {
        // If the data we hold is the correct data, return it
        if(holdsData && name.toLowerCase().equals(prefix.toLowerCase())) {
            return data;
        }

        ArrayList<T> results = new ArrayList<T>();

        if(name.length() > prefix.length() && name.toLowerCase().startsWith(prefix.toLowerCase())) { // Just check that we're not having an error
            // Work out which of the children the name will be in
            char c = name.toLowerCase().charAt(prefix.length());

            if(children.containsKey(c)) { // Work out if there is another relevant child.
                results.addAll(children.get(c).get(name)); // Add the results from the children to this result
            }

            // Now add the data from this Node.
            results.addAll(data);
        }
        // Return results. Will still be empty if there are no valid children
        return results;
    }

    //  ------------------------------------------------------------------------------------------------
    //  THE GETTERS AND SETTERS WERE AUTO GENERATED BY INTELLIJ, I HAVE ONLY ADDED THE METHOD COMMENTS.
    //  ------------------------------------------------------------------------------------------------

    /**
     * @return The ArrayList containing the data
     */
    public ArrayList<T> getData() {
        return data;
    }

    /**
     * @return Whether or not the TrieNode contains data
     */
    public boolean isHoldsData() {
        return holdsData;
    }

    /**
     * @return The prefix of the TrieNode, including the character this node represents
     */
    public String getPrefix() {
        return prefix;
    }
}
