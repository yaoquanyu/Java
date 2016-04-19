import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Contains several methods for parsing text into words. Assumes words are
 * separated by whitespace.
 * 
 * @author // Yaoquan Yu.
 */
public class WordParser {

	/**
	 * Converts text into a consistent format by converting text to lower-
	 * case, replacing non-word characters and underscores with a single
	 * space, and finally removing leading and trailing whitespace. (See
	 * the {@link String} class for several helpful methods.)
	 *
	 * @param text - original text
	 * @return text without special characters and leading or trailing spaces
	 */
	public static String cleanText(String text) {
		String regex = "[\\W_]";
		text = text.replaceAll(regex, " ");
		text = text.replaceAll("( )+", " ");
		text = text.toLowerCase().trim();
		return text;
	}

	/**
	 * Splits text into words by whitespaces, cleans the resulting words
	 * using {@link #cleanText(String)} so that they are in a consistent
	 * format, and adds non-empty words to an {@link ArrayList}.
	 *
	 * <p><em>
	 * You must use the {@link #cleanText(String)} method and an enhanced
	 * for loop to receive full credit for this method.
	 * </em></p>
	 *
	 * @param text - original text
	 * @return list of cleaned words
	 */
	public static List<String> parseText(String text) {
		return Arrays.asList(cleanText(text).split("\\s"));
	}
}
