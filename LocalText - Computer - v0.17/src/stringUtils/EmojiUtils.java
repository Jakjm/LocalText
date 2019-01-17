package stringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Hardcoded class to get emojis and descriptions.
 * @author jordan
 *
 */
public final class EmojiUtils {
	//The range of emoticons
	private static final int EMOTICON_START = 0x1F600;
	private static final int EMOTICON_FINISH = 0x1F650;
	private static Emoji [] emojiList;
	public static String [] getEmojis () {
		String [] emojiArray = new String[EMOTICON_FINISH - EMOTICON_START];
		for(int i = 0;i < emojiArray.length;i++) {
			char [] emojiCharacters = Character.toChars(EMOTICON_START + i);
			String newEmoji = new String(emojiCharacters,0,emojiCharacters.length);
			emojiArray[i] = newEmoji;
		}
		return emojiArray;
	}
	public static void main (String [] args) {
		getRanges("emoji-data.txt");
	}
	public static Emoji [] getEmojiList(){
		getRanges("emoji-data.txt");
		return emojiList;
	}
	private static InputStream getStream(String path)
	{
	  return EmojiUtils.class.getResourceAsStream(path);
	}
	private static void getRanges(String path) {
		ArrayList<String> infoLines = new ArrayList<String>();
		/*
		 * Adding each of the information lines from the file to an arraylist.
		 */
		InputStream stream = getStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		try {
			line = reader.readLine();
			while(line != null) {
				//If the line isn't blank, or a comment, we add the line to our arraylist.
				if(line.length() > 0 && line.charAt(0) != ' ' && line.charAt(0) != '#') {
					if(line.substring(0, line.indexOf(" ;")).length() < 6){
					     line = line.substring(0, line.indexOf(" ;")) + "|" +  line.substring(line.lastIndexOf(")") + 1);
					     infoLines.add(line);
					}
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Adding each of the emoji groups.
		emojiList = new Emoji[infoLines.size()];
		int lineCount = 0;
		for(String currentLine : infoLines) {
			//Adding the emoji
			char [] emojiChars = Character.toChars(Integer.parseInt(currentLine.substring(0,currentLine.indexOf("|")),16));
			String emoji = new String(emojiChars,0,emojiChars.length);
			String emojiDescription = currentLine.substring(currentLine.indexOf("|") + 1);
			emojiList[lineCount] = new Emoji(emoji,emojiDescription);
			lineCount++;
		}
	}
}
