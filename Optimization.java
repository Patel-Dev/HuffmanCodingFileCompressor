import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map.Entry;

/**
 * @author Dev Patel
 * date: 01/21/2021
 * description: This class is used to compress and decompress a string by means of substituting strings of various 
 * lengths with characters unique to the string.
 */
public class Optimization {
	
	/**
	 * Compresses a string by substituting unique characters for common substrings.
	 * @param str The string to be compressed.
	 * @return The compressed version of the string.
	 */
	public static String compress(String str) {				
		
		//Compresses substrings with 6-2 chars (length of the substring being compressed is substrLen, from greatest to least).
		for (int substrLen = 6; substrLen > 1; substrLen--) {
			System.out.println("Compressing substrings of length " + substrLen);
			
			//Hashtable with substrings as well as the chars (converted to integers) that represent them. 
			Hashtable<String, Integer> substrings = new Hashtable<>();
			
			/* 
			 * This array is used to represent which one-byte characters have been used in the string (there are 256).
			 * Index i represents the integer conversion of a char, and the value at that index represents if the char has 
			 * been used.
			 */
			boolean[] usedChars = new boolean[256];
			
			//Iterates through the last substrLen characters of the string.
			for (int i = 1; i < substrLen; i++) {
				int currentChar = (int) str.charAt(str.length()-i);
				
				//If the char is a one-byte character, set that char to used.
				if (currentChar < 256) {
					usedChars[currentChar] = true;
				}
			}
			
			for (int i = 0; i <= str.length() - substrLen; i++) {
				int charValue = (int)str.charAt(i);
				
				//Identifies which chars have been used in the rest of the string
				if (charValue < 256) {
					usedChars[charValue] = true;
				}
				
				//Stores the frequency of every string of length substrLen into the hashtable.
				String substr = str.substring(i, i + substrLen);
				
				if (substrings.get(substr) == null) {
					substrings.put(substr, 1);
				}
				else {
					substrings.replace(substr, substrings.get(substr) + 1);
				}
			}
			
			//Uses the entries of the hashtable to create a list.
			ArrayList<Entry<String, Integer>> hashEntryList = new ArrayList<>(new LinkedList<>(substrings.entrySet()));
			
			//Sorts the list based on the frequencies of the substrings from greatest to least. 
			hashEntryList.sort((entry1, entry2) -> {
				return entry2.getValue() - entry1.getValue();
			});
			
			//Using the keys of the entries of the previous list, create a list of substrings.
			ArrayList<String> substrList = new ArrayList<>(hashEntryList.size());
			
			hashEntryList.forEach(entry -> {
				substrList.add(entry.getKey());
			});
			
			//Iterate through the usedChars array to count the number of one-byte chars that are yet to be used.
			int available = 0;
			
			for (int i = 0; i < usedChars.length; i++) {
				if (usedChars[i] == false) {
					available++;
				}
			}
			
			//1/5 of the unused chars are reserved for each of the 5 different string lengths (2-6).
			available /= substrLen - 1;
			
			//replacementChar represents the char that replaces a certain substring.
			int replacementChar = 0;
			
			//How many unused chars were used to replace substrings.
			int counter = 0;
			
			//This string is used for decoding and is added to the end of string str.
			String strDecode = "";
			
			//Iterate through the sorted list of the most common substrLen letter substrings.  
			for (int i = 0; i < substrList.size(); i++) {
				
				/* 
				 * If the number of unused chars used to replace substrLen letter substrings reaches the number reserved for 
				 * the substrLen length, break.
				 */
				if (counter == available) {
					break;
				}
				
				/* 
				 * Check if the string still contains the substring as it may have been removed due to the substitution 
				 * of a different substring.
				 */
				String substr = substrList.get(i);
				
				if (str.contains(substr)) {
					
					//Use the next unused char.
					while (replacementChar < 256) {
						if (usedChars[replacementChar] == false) {
							break;
						}
						
						replacementChar++;
					}
					
					//Use the current unused char to replace all instances of the current substring.
					str = str.replace(substr, (char)replacementChar + "");
					
					/* 
					 * Add the char and the the substring that the char replaces to decodeString, which will be used 
					 * in the decompress method to determine which chars need to be replaced with which 
					 * corresponding substrings.
					 */
					strDecode += (char)replacementChar + substr;
					
					//Use the next unused char and increase the number of unique substrings replaced by 1.
					replacementChar++;
					counter++;	
				}
				
			}
		
			//To strDecode, the char value for the number of chars used to replace substrings.
			strDecode += (char)counter;
			
			//Add strDecode to string str.
			str += strDecode;
		}
		
		//Return the compressed version of string str.
		return str;
	}

	/**
	 * Decompresses a string that has been compressed using chars to replace substrings.
	 * @param str The string to be decompressed.
	 * @return The decompressed string.
	 */
	public static String decompress(String str) {
		
		//Decompresses substrings with lengths (represented by i) from 2-6, in order from least to greatest.
		for (int substrLen = 2; substrLen < 7; substrLen++) {
			System.out.println("Decompressing substrings of length " + substrLen);
			
			//Create a hashtable with the key as the char and the value as the substring it replaces.
			Hashtable<String, String> substitutions = new Hashtable<>();
			
			/* 
			 * The integer representing the last char of the string is the number of unique chars used to replace 
			 * substrings.
			 */
			int numSubstrings = (int)(str.charAt(str.length() - 1));
			
			/* 
			 * The final numSubstrings number of substrings (before the last char in the string) of size (substrLen + 1) include
			 * information as to what to add to the hashtable.
			 */
			for (int i = str.length() - 1 - ((substrLen+1) * numSubstrings); i < str.length() - 1; i += substrLen+1) {
				
				/* 
				 * Within each substring, the first char was put as the key and the remaining i chars were put as the 
				 * value. For example, if a substring is 'ACR', 'A' would be the key and 'CR' would be the value.
				 */
				substitutions.put(str.substring(i, i+1), str.substring(i+1, i+1+substrLen));
			}

			//Remove the information for decoding from the string.
			str = str.substring(0, str.length() - 1 - ((substrLen+1) * numSubstrings));
			
			/*
			 * Substitute each replacement char in String s with its corresponding substring by using the hashtable.
			 */
			for (Entry<String, String> entry : substitutions.entrySet()) {
				str = str.replace(entry.getKey(), entry.getValue());
			}
		}
		
		//Return the decompressed string.
		return str;	
	}
}