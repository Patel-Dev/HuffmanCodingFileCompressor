import java.io.*;
import java.util.*;

/**
 * @author Dev Patel
 * date: 01/21/2021
 * description: This class is used to compress a text file using Huffman encoding as well as the substitution of 
 * substrings with characters. 
 */
public class HuffmanEncode {
	
	/**
	 * Takes in an ArrayList of nodes and sorts them from least to greatest based on frequency using bubble sort.
	 * @param nodes An ArrayList of nodes.
	 */
	private static void bubbleSort(ArrayList<Node> nodes) {
		for (int i = 0; i < nodes.size() - 1; i++) {
			for (int j = 0; j < nodes.size() - 1; j++) {
				if (nodes.get(j).freq > nodes.get(j+1).freq) {
					Node tempNode = nodes.get(j);
					nodes.set(j, nodes.get(j + 1));
					nodes.set(j + 1, tempNode);
				}
			}
		}
	}
	
	/**
	 * Uses a string to build a Huffman tree that encodes each character to a binary value. 
	 * @param str The string to be compressed and encoded.
	 * @return The Huffman tree made for the string.
	 */
	private static Node makeTree(String str) {
		
		//ArrayList of all nodes within the binary tree.
		ArrayList<Node> nodes = new ArrayList<>();
		
		//Makes an array of all chars within the string.
		char[] chars = str.toCharArray();
		
		//Iterate through the array of chars representing those in the string.
		for (int i = 0; i < chars.length; i++) {
			
			//Boolean value representing whether or not a character has been used in a node. 
			boolean notFound = true;
			
			//Iterates through the ArrayList of nodes.
			for (int j = 0; j < nodes.size(); j++) {
				
				/*
				 * If a node is made for a character already, increase its frequency by 1 and set the notFound boolean 
				 * value to false and break the iterative loop because the char has been found.
				 */
				if (nodes.get(j).value == chars[i]) {
					notFound = false;
					nodes.get(j).freq++;
					break;
				}
			}
			
			/*
			 * If the char has not been found to be used in a node, make a new node that encodes the character with an 
			 * initial frequency of 1 and add it to the ArrayList of nodes.
			 */
			if (notFound) {
				Node newNode = new Node(chars[i], 1);
				nodes.add(newNode);
			}
		}
		
		//Use bubble sort to sort the ArrayList of nodes based on frequency, from least to greatest.
		bubbleSort(nodes);
		
		/*
		 * This loops uses the nodes ArrayList to build the Huffman tree by assigning child nodes to parent nodes 
		 * based on frequency until there is one node left (the root node of the tree).
		 */
		while (nodes.size() > 1) {
			
			//Assigns the two nodes with the lowest frequencies in the ArrayList as children nodes.
			Node childLeft = nodes.remove(0);
			Node childRight = nodes.remove(0);
			
			//Add the frequency of both child nodes to build a parent node. 
			Node parent = new Node(childLeft.freq + childRight.freq);
			parent.leftNode = childLeft;
			parent.rightNode = childRight;
			
			/*
			 * If there are no more nodes in the nodes ArrayList, or if the frequency of the parent node is greater than
			 * the frequency of all the other nodes within the ArrayList, add the parent node to the end of the list.
			 */
			if (nodes.size() == 0 || parent.freq >= nodes.get(nodes.size() - 1).freq) {
				nodes.add(parent);
			}
			
			/*
			 * In all other situations, iterate through the nodes ArrayList and determine where to place the parent node
			 * based on the order of frequency.
			 */
			else {
				for (int i = 0; i < nodes.size(); i++) {
					if (parent.freq < nodes.get(i).freq) {
						nodes.add(i, parent);
						break;
					}
				}
			}
		}
		
		System.out.println ("Huffman tree created!");
		
		//Return the root node (which holds the entire Huffman tree). 
		return nodes.get(0);
	}
	
	/**
	 * Use the root node (Huffman tree) to encode a string representing a binary value for each character, 
	 * and store these representations in a hashtable.
	 * @param n The node holding the contents of the part of the tree to be traversed through.
	 * @param bits The string representing the binary value of the character.
	 * @param h The hashtable used to store the representations.
	 */
	private static void charsToBinary(Node n, String bits, Hashtable<Character , String> h) {
		
		/* 
		 * If the node is the last node of the current path within the Huffman tree, store the character represented by
		 * the node, as well as its corresponding binary string, to the hashtable.
		 */
		if (n.leftNode == null && n.rightNode == null) {
			h.put(n.value, bits);
		}
		
		/*
		 * If the node has a left child node, call this method using the left child and add a 0 to the binary string 
		 * (representing the path taken to the left).
		 */
		if (n.leftNode != null) {
			charsToBinary(n.leftNode, bits + "0", h);
		}
		
		/*
		 * If the node has a right child node, call this method using the right child and add a 1 to the binary string
		 * (representing the path taken to the right).
		 */
		if (n.rightNode != null) {
			charsToBinary(n.rightNode, bits + "1", h);
		}
	}
	
	/**
	 * Converts a string representing a binary number, to an integer.
	 * @param binaryString The binary string to be converted to an integer.
	 * @return The integer conversion of the binary string.
	 */
	private static int binaryToInt(String binaryString) {		
		char[] bits = binaryString.toCharArray();
		int intValue = 0;
		
		//Add the place value to the total number representing the string if the place value holds a '1'.
		for (int i = 0; i < bits.length; i++) {
			if (bits[i] == '1') {
				int placeValue = (int) Math.pow(2, 7-i);
				intValue += placeValue;
			}
		}
		
		return intValue;
	}

	/*
	 * Encodes and compresses the text file, writes the Huffman tree to tree.ser and writes the encoded text to 
	 * huffman.ser.
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Reading the text file...");
												
			FileInputStream fis = new FileInputStream("warandpeace.txt");
			
			//Use UTF-8 in the InputStreamReader to read characters that are valued greater than one byte.
			InputStreamReader fisReader = new InputStreamReader(fis, "UTF-8");
			BufferedReader bufferedFis = new BufferedReader(fisReader);
			
			//Make an array of the integer values of all chars within the inputed text file.
			int charVal;
			int index = 0;
			int charValues[] = new int[(int) new File("warandpeace.txt").length()];						
			
			while ((charVal = bufferedFis.read()) != -1) {
				charValues[index] = charVal;
				index++;
			}
			
			bufferedFis.close();
			
			/* 
			 * Trim null chars that may occur at the end of the string. Null chars have an integer value of 0 and 
			 * if the string has chars with values greater than one byte (like chars in other languages with 
			 * accents), null chars are left at the end of the char list.
			 */
			char[] charValsReduced = new char[index];
			
			for (int i = 0; i < index; i++) {
				charValsReduced[i] = (char)charValues[i];
			}
			
			//Use a string to store the trimmed contents of the inputed text file.
			String str = new String(charValsReduced);
			
			System.out.println("Compressing file by substituting strings with chars...\n");
			
			//Substitute strings within the text with chars to further compress the file.
			str = Optimization.compress(str);
			
			System.out.println("\nWriting to serialized files...");
			
			//Serialize the Huffman tree.
			FileOutputStream tree = new FileOutputStream("tree.ser");
			ObjectOutputStream treeOut = new ObjectOutputStream(tree);
			
			//Build the Huffman binary tree for the characters within the text file.
			Node huffmanNode = makeTree(str);
			
			treeOut.writeObject(huffmanNode);
			treeOut.close();
			
			/* 
			 * Use a hashtable to store each char from the Huffman tree and their corresponding binary values (as 
			 * strings) by traversing through all possible paths within the tree.
			 */
			Hashtable<Character, String> h = new Hashtable<>();
			charsToBinary(huffmanNode, "", h);
						
			FileOutputStream huffmanOut = new FileOutputStream("huffman.ser");
			String binaryString = "";
			char[] compressedChars = str.toCharArray();
			
			//Iterate through the compressed string.
			for (int i = 0; i < compressedChars.length; i++) {
				
				//Add the binary representation of the current character to the binary string.
				binaryString += h.get(compressedChars[i]);
				
				/*
				 * When there are >= 8 bits in the binary string, write the first 8 bits to huffman.ser as one character 
				 * and remove them from the binary string.
				 */
				while (binaryString.length() >= 8) {
					
					//First 8 bits.
					String byteString = binaryString.substring(0, 8);
					huffmanOut.write(binaryToInt(byteString));
					binaryString = binaryString.substring(8);
				}
			}
			
			/* 
			 * If the binary string has bits that have not been written, add additional zeroes at the end of the binary 
			 * string until there are 8 bits (chars) to form a byte.
			 */
			int zeroesAdded = 8 - binaryString.length();
			
			for (int i = 0; i < zeroesAdded; i++) {
				binaryString += '0';
			}
			
			/* 
			 * Write the character representation of the bits in binary to huffman.ser. We need to know how many 
			 * additional bits we added at the end of the binary string so we can remove them when decoding, so write 
			 * the number of extra zeroes to huffman.ser as well.
			 */
			huffmanOut.write(binaryToInt(binaryString));
			huffmanOut.write(zeroesAdded);
			huffmanOut.close();
			
			System.out.println("Done! Check huffman.ser and tree.ser");
		}
		catch (IOException i) {
			i.printStackTrace();
		}
	}
}