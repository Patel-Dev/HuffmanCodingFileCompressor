import java.io.*;

/**
 * @author Dev Patel
 * date: 01/21/2021
 * description: This class is used to decode and decompress the huffman.ser file to an output file that matches the 
 * original encoded file. 
 */
public class HuffmanDecode {

	/**
	 * Takes in an integer and converts it to a string of its binary representation.
	 * @param intValue The integer, between 0 and 255 inclusive, to be converted to binary.
	 * @return A string containing the binary representation of the integer as a byte.  
	 */
	private static String intToBinary(int intValue) {
		String binaryString = "";
		
		for (int exponent = 7; exponent >= 0; exponent--) {	
			int placeValue = (int) Math.pow(2, exponent);
			
			if (intValue < placeValue) {
				binaryString += "0";
			}
			else {
				intValue -= placeValue;
				binaryString += "1";
			}
		}
		
		return binaryString;
	}
	
	/**
	 * Checks if the original input file and the decoded output file are different. Also checks compression ratio. 
	 */
	public static void diffCheck() {
		System.out.println("\nComparing the input and output...");
		
		File input = new File("warandpeace.txt");
		File output = new File("output.txt");
		
		//If the lengths of both files are the same, check the characters in both files to see if they match.
		if (input.length() == output.length()) {
			try {
				int in, out = 0;
				
				BufferedReader inputRead = new BufferedReader(new InputStreamReader(new FileInputStream(input), "UTF-8"));
				BufferedReader outputRead = new BufferedReader(new InputStreamReader(new FileInputStream(output), "UTF-8"));
				
				//Compares each character in both files by reading them one at a time.
				while ((in = inputRead.read()) != -1 && (out = outputRead.read()) != -1) {
					if (in != out) {
						System.out.println("The input and output files are not identical.");
						System.exit(0);
					}
				}
				
				inputRead.close();
				outputRead.close();
				
				System.out.println("The input and output files are identical!");
				
				/* 
				 * Calculates the compression ratio by comparing the sum of the sizes of huffman.ser and tree.ser and 
				 * dividing it by the size of the original file.
				 */
				double huffmanSerSize = new File("huffman.ser").length();
				double treeSerSize = new File("tree.ser").length();
				double inputFileSize = (double)input.length();
				double compressionRatio = (huffmanSerSize + treeSerSize) / (inputFileSize);
				double relativeSize = compressionRatio * 100;
				double reduction = 100 - relativeSize;
				
				System.out.println("\nCompression Ratio: " + compressionRatio);
				System.out.printf("The compressed files are %.2f percent the size of the original file! In other words, this is a %.2f percent size reduction!", relativeSize, reduction);
			}
			catch (IOException i) {
				i.printStackTrace();
				System.exit(0);
			}
		}
		else {
			System.out.println("The input and output files are not identical.");
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		try {
			System.out.println("Reading serialized Huffman file...");
			
			//Store the contents of huffman.ser in an array of bytes.
			File huffmanSer = new File("huffman.ser");
			FileInputStream huffmanFis = new FileInputStream(huffmanSer);
			
			byte huffmanBytes[] = new byte[(int)huffmanSer.length()];
						
			huffmanFis.read(huffmanBytes);
			huffmanFis.close();
			
			System.out.println("Reading serialized tree file...");
			FileInputStream treeFis = new FileInputStream("tree.ser");
			ObjectInputStream treeInput = new ObjectInputStream(treeFis);
			
			//Read the node from tree.ser to tree.
			Node tree = (Node) treeInput.readObject();
			treeInput.close();
			
			//Decode the Huffman encoding.
			System.out.println("\nDecompressing...");
			
			Node treeNode = tree;
			String binaryString = "";
			
			//Use StringBuilder as a more efficient method than string concatenation to create final string for output. 
			StringBuilder strBuild = new StringBuilder(huffmanBytes.length);
			
			//Iterate through all characters in the encoded file. 
			for (int i = 0; i < huffmanBytes.length - 1; i++) {
				
				//Convert the current character to a string of bits.
				int currentChar = (int)(char)(int)huffmanBytes[i];
				binaryString = intToBinary(currentChar % 256);
				
				//If the current character is the second last character
				if (huffmanBytes.length - 2 == i) {
					
					/* 
					 * Determine how many additional zeroes to be removed from the string of bits, 
					 * using the last character. 
					 */
					int lastChar = (int)(char)(int)huffmanBytes[huffmanBytes.length - 1];
					binaryString = binaryString.substring(0, binaryString.length() - (lastChar % 256));
				}
				
				//Iterate through every bit in the string of bits.
				while (binaryString.length() > 0) {
					Node childNode;
					
					//Traverse through the Huffman tree based on the value of the bit. 
					if (binaryString.charAt(0) == '1') {
						childNode = treeNode.rightNode;
					}
					else {
						childNode = treeNode.leftNode;
					}
					
					
					//If the current node has a child, set treeNode to the child.
					if (childNode != null) {
						treeNode = childNode;
						
						/* 
						 * If the char is the second last char in the encoded file or if the binary string has one char
						 * in it, add the char value of treeNode to the StringBuilder.
						 */
						if (huffmanBytes.length - 2 == i && binaryString.length() == 1) {
							strBuild.append(treeNode.value);
						}
					}
					
					//If the current node does not have a child, add the character value of treeNode to the StringBuilder.
					else {
						strBuild.append(treeNode.value);
						
						/* 
						 * Depending on the first char in the binary string, traverse to the left or right child node.
						 * Left if the char is '0' and right if the char is '1' (else). 
						 */
						if (binaryString.charAt(0) == '0') {
							treeNode = tree.leftNode;
						}
						else {
							treeNode = tree.rightNode;
						}
					}
					
					//Remove the current bit from the string of bits.
					binaryString = binaryString.substring(1);
				}
			}
			
			System.out.println("Substituting replacement chars with substrings...\n");
			
			//Further decompress the file replacing substitution chars with the substrings they represent.
			String decompressedStr = Optimization.decompress(strBuild.toString());
			
			System.out.println("\nWriting to output file");
			
			FileOutputStream outputFis = new FileOutputStream("output.txt");
			OutputStreamWriter outputWrite = new OutputStreamWriter(outputFis, "UTF-8");
			PrintWriter outputPrint = new PrintWriter(outputWrite);
						
			//Write the decompressed string to output.txt.
			outputPrint.write(decompressedStr);
			outputPrint.close();
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		
		System.out.println("Done! Check output.txt");
		
		diffCheck();
	}
}