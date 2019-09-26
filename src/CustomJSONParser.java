public class CustomJSONParser {

    private static final char SEPARATOR = ',', OBJ_START = '{', OBJ_END = '}', ARR_START = '[', ARR_END = ']', STRING_MARKER = '\'', VAL_MARKER = ':';

    private Map<String, >

    private CustomJSONParser(String s) {

    }

    /**
     * Keys don't have quotes around them
     * Strings are surrounded by single quotes
     */
    public static CustomJSONParser initialize(String s) {
        return new CustomJSONParser(s);
    }
}
