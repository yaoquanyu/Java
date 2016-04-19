import java.util.Map;
import java.util.TreeMap;

/**
 * Parses command-line arguments into flag/value pairs for easy access.
 */
public class ArgumentParser {

	/** Stores arguments in a map, where the key is a flag. */
	private final Map<String, String> argumentMap;
	/**
	 * Initializes an empty argument map. The method {@link #parseArguments(String[])}
	 * should eventually be called to populate the map.
	 */
	public ArgumentParser() {
		argumentMap = new TreeMap<String, String>();
	}

	/**
	 * Initializes the argument map with the provided command-line arguments.
	 * Uses {@link #parseArguments(String[])} to populate the map.
	 *
	 * @param args - command-line arguments
	 * @see #parseArguments(String[])
	 */
	public ArgumentParser(String[] args) {
		this();
		parseArguments(args);
	}

	/**
	 * Iterates through the array of command-line arguments. If a flag is
	 * found, will attempt to see if it is followed by a value. If so, the
	 * flag/value pair is added to the map. If the flag is followed by
	 * another flag, then the first flag is added to the map with a null
	 * value.e
	 *
	 * @param args - command-line arguments
	 *
	 * @see #isFlag(String)
	 * @see #isValue(String)
	 */
	public void parseArguments(String[] args) {
		for(int i = 0; i < args.length; i++)
		{
			if(isFlag(args[i]))
			{
				i++;
				if(i < args.length)
				{
					if(isValue(args[i]))
					{
						argumentMap.put(args[i - 1], args[i]);
					}
				}
				else
				{
					argumentMap.put(args[i - 1], null);
				}
			}
		}
	}

	/**
	 * Tests if the provided argument is a flag by checking that it starts with
	 * a "-" dash symbol, and is followed by at least one non-whitespace
	 * character. For example, "-a" and "-1" are valid flags, but "-" and "- "
	 * are not valid flags.
	 *
	 * @param arg - command-line argument
	 * @return true if the argument is a flag
	 */
	public static boolean isFlag(String arg) {
		if(!arg.equals(""))
		{
			if(arg.charAt(0) == '-')
			{
				if(arg.length() < 2 || arg.charAt(1) == ' ')
				{
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if the provided argument is a value by checking that it does not
	 * start with a "-" dash symbol, and contains at least one non-whitespace
	 * character. For example, "a" and "1" are valid values, but "-" and " "
	 * are not valid values.
	 *
	 * @param arg - command-line argument
	 * @return true if the argument is a value
	 */
	public static boolean isValue(String arg) {
		if(!arg.equals(""))
		{
			if(arg.charAt(0) == '-' || arg.charAt(0) == ' ')
			{
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns the number of flags stored.
	 *
	 * @return number of flags
	 */
	public int numFlags() {
		return argumentMap.size();
	}

	/**
	 * Tests if the provided flag is stored in the map.
	 *
	 * @param flag - flag to check
	 * @return value if flag exists and has a value, or null if the flag
	 * does not exist or does not have a value
	 */
	public boolean hasFlag(String flag) {
		return argumentMap.containsKey(flag);
	}

	/**
	 * Tests if the provided flag has a value.
	 *
	 * @param flag - flag to check
	 * @return true if the flag exists and has a non-null value
	 */
	public boolean hasValue(String flag) {
		return argumentMap.containsKey(flag);
	}

	/**
	 * Returns the value of a flag if it exists, and null otherwise.
	 *
	 * @param flag - flag to check
	 * @return value of flag or null if flag does not exist or has no value
	 */
	public String getValue(String flag) {
		return argumentMap.get(flag);
	}

	@Override
	public String toString() {
		return argumentMap.toString();
	}
}
