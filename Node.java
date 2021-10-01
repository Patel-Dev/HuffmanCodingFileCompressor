import java.io.Serializable;

/**
 * @author Dev Patel
 * date: 01/21/2021
 * description: This class is used to construct Node objects (with various properties/classifications) to build a 
 * Huffman tree.   
 */
public class Node implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/*
	 * Nodes can have certain char values that occur a number of times within the text file. 
	 * Some nodes may not represent a char, but its frequency is the sum of the frequency of its child nodes.
	 */
	public int freq;
	public char value;
	
	//Nodes also have children nodes that traverse in the left (0) or right (1) direction.
	public Node leftNode, rightNode;
	
	/**
	 * Constructs a Node object using only an integer.
	 * @param n The frequency of the node. 
	 */
	public Node (int n) {
		this.freq = n;
	}
	
	/**
	 * Constructs a Node object using a char and an integer.
	 * @param a The char value represented by the node.
	 * @param n The frequency representing the number of times the char occurs within the text file.
	 */
	public Node (char a, int n) {
		this.value = a;
		this.freq = n;
	}
}