import java.util.HashMap;
import java.util.Map;

public class CustomJSONParser extends Value {

    private static final String SEPARATOR = ",", OBJ_START = "{", OBJ_END = "}", ARR_START = "[", ARR_END = "]", STRING_MARKER = "'", VAL_MARKER = ":";

    /*
    private Map<String, CustomJSONParser> objectMap;
    private Map<String, String> stringMap;
    private Map<String, Integer> intMap;
    private Map<String, Float> floatMap;
    private Map<String, JsonArray> arrayMap;
     */

    private Map<String, Value> map;

    private CustomJSONParser(String s) {
        super(s);

        map = new HashMap<>();

        //get to the start of the json object
        int idx = s.indexOf(OBJ_START);

        //loop while ______
        while (true) {
            //get the key by searching from the current position to the next colon
            String key = s.substring(idx + 1, s.indexOf(VAL_MARKER, idx)).trim();

            //put the marker in the position one after the colon
            idx += key.length() + 1;

            //get to next nonwhitespace character
            while(s.charAt(idx) == ' ') {
                idx++;
            }

            //test if it is an object, an array, or something else (meaning a normal value)

        }
    }

    /**
     * Keys don't have quotes around them
     * Strings are surrounded by single quotes
     */
    public static CustomJSONParser initialize(String s) {
        return new CustomJSONParser(s);
    }

    /**
     * Returns the ending index of the object in string that starts at the index "start"
     */
    private static int getObjectEnd(String s, int start) {
        return getBoundedValue(s, start, OBJ_START, OBJ_END);
    }

    private static int getArrayEnd(String s, int start) {
        return getBoundedValue(s, start, ARR_START, ARR_END);
    }

    private static int getBoundedValue(String s, int start, final String START_STRING, final String END_STRING) {
        int closedVal = 1;

        while (closedVal > 0) {
            //shift start one ahead of starting brace
            start++;
            int openBrace = s.indexOf(START_STRING, start), closedBrace = s.indexOf(END_STRING, start);
            if (openBrace < closedBrace) {
                closedVal++;
                start = openBrace;
            } else {
                closedVal--;
                start = closedBrace;
            }
        }

        return start;
    }
}

class Value {
    private String rawString;

    protected Value(String s) {
        rawString = s;
    }
}

class JsonArray {

}