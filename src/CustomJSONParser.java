import sun.awt.X11.XSystemTrayPeer;

import javax.xml.crypto.Data;
import java.util.*;

enum VALUE {
    OBJECT, ARRAY, NUMBER, STRING;
}

public class CustomJSONParser {

    /**
     * Keys don't have quotes around them
     * Strings are surrounded by single quotes
     */
    public static CustomJSONParser initialize(String s) {
        return new CustomJSONParser(s, null);
    }

    public String getString(String key) {
        return data.stringMap.get(key);
    }

    public List<Object> getJSONArray(String key) {
        return data.arrayMap.get(key);
    }

    public float getFloat(String key) {
        return data.floatMap.get(key);
    }

    public int getInt(String key) {
        return data.intMap.get(key);
    }

    public String getJSONObject(String key) {
        return data.objectMap.get(key).asString;
    }

    public List<String> listAllKeysAtCurrentLevel(int depth) {
        List<String> keys = new ArrayList<>();

        for (String key : data.allKeys) {
            keys.add(key);
        }

        return keys;
    }

    public boolean checkIfTagExists(String tag) {
        return data.allKeys.contains(tag);
    }

    public int maxDepth() {
        int max = depth;
        for (CustomJSONParser child : children) {
            int childMaxDepth = child.maxDepth();
            if (childMaxDepth > max) {
                max = childMaxDepth;
            }
        }
        return max;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final char SEPARATOR = ',', OBJ_START = '{', OBJ_END = '}', ARR_START = '[', ARR_END = ']', STRING_MARKER = '\'', VAL_MARKER = ':', FLOAT_MARKER = '.';

    private DataMaps data;

    private int depth;

    private String asString;

    private List<CustomJSONParser> children;

    class DataMaps {
        private Map<String, CustomJSONParser> objectMap;
        private Map<String, String> stringMap;
        private Map<String, Integer> intMap;
        private Map<String, Float> floatMap;
        private Map<String, List<Object>> arrayMap;

        private Set<String> allKeys;

        DataMaps() {
            objectMap = new HashMap<>();
            stringMap = new HashMap<>();
            intMap = new HashMap<>();
            floatMap = new HashMap<>();
            arrayMap = new HashMap<>();

            allKeys = new HashSet<>();
        }

        CustomJSONParser getObject(String key) {
            return objectMap.get(key);
        }

        void putString(String key, String string) {
            if (allKeys.add(key))
                stringMap.put(key, string);
        }

        void putArr(String key, List<Object> array) {
            if (allKeys.add(key))
                arrayMap.put(key, array);
        }

        void putFloat(String key, Float aFloat) {
            if (allKeys.add(key))
                floatMap.put(key, aFloat);
        }

        void putInt(String key, Integer integer) {
            if (allKeys.add(key))
                intMap.put(key, integer);
        }

        void putObj(String key, CustomJSONParser object) {
            if (allKeys.add(key))
                objectMap.put(key, object);
        }
    }


    private CustomJSONParser(String s, CustomJSONParser parent) {

        if (parent == null) {
            depth = 1;
        } else {
            depth = parent.depth + 1;
            parent.children.add(this);
        }

        children = new ArrayList<>();

        data = new DataMaps();

        asString = s;

        //get to the start of the json object
        int idx = s.indexOf(OBJ_START);

        //loop while ______
        do {
            //get the key by searching from the current position to the next colon
            String key = s.substring(idx + 1, s.indexOf(VAL_MARKER, idx)).trim();

            //put the marker in the position one after the colon
            idx += key.length() + 1;

            //get to next nonwhitespace character

            char currChar;

            while ((currChar = s.charAt(idx)) == ' ') {
                idx++;
            }

            //now, idx is not on a space

            switch (getType(currChar)) {
                case STRING:
                    data.putString(key,
                            s.substring(idx + 1, idx = s.indexOf(STRING_MARKER, idx))
                    );
                    break;
                case NUMBER:
                    int start = idx;
                    boolean isFloat = false;
                    while (Character.isDigit(currChar = s.charAt(idx)) || currChar == FLOAT_MARKER ? isFloat = true : false) {
                        idx++;
                    }

                    String number = s.substring(start, idx);

                    if (isFloat) {
                        data.putFloat(key, Float.parseFloat(number));
                    } else {
                        data.putInt(key, Integer.parseInt(number));
                    }
                    break;
                case OBJECT:
                    data.putObj(key, new CustomJSONParser(s.substring(idx, getObjectEnd(s, idx) + 1), this));
                    break;
                case ARRAY:
                    data.putArr(key, getArray(s.substring(idx, getArrayEnd(s, idx) + 1)));
                    break;
            }


            idx = s.indexOf(SEPARATOR, idx) + 1;
            //now, idx is one space after the next comma

        } while (s.indexOf(SEPARATOR, idx) > -1);
    }

    private List<Object> getArray(String s) {

        List<Object> array = new ArrayList<>();

        int idx = 1;

        //loop while ______
        do {
            //get to next nonwhitespace character

            char currChar;

            while ((currChar = s.charAt(idx)) == ' ') {
                idx++;
            }

            //now, idx is not on a space

            switch (getType(currChar)) {
                case STRING:
                    array.add(s.substring(idx + 1, idx = s.indexOf(STRING_MARKER, idx)));
                    break;
                case NUMBER:
                    int start = idx;
                    boolean isFloat = false;
                    while (Character.isDigit(currChar = s.charAt(idx)) || currChar == FLOAT_MARKER ? isFloat = true : false) {
                        idx++;
                    }

                    String number = s.substring(start, idx);

                    if (isFloat) {
                        array.add(Float.parseFloat(number));
                    } else {
                        array.add(Integer.parseInt(number));
                    }
                    break;
                case OBJECT:
                    array.add(new CustomJSONParser(s.substring(idx, getObjectEnd(s, idx) + 1), this));
                    break;
                case ARRAY:
                    array.add(getArray(s.substring(idx, getArrayEnd(s, idx) + 1)));
                    break;
            }


            idx = s.indexOf(SEPARATOR, idx) + 1;
            //now, idx is one space after the next comma

        } while (s.indexOf(SEPARATOR, idx) > -1);

        return array;

    }

    private static VALUE getType(char c) {
        if (c == '\'') {
            // the current value is a string
            return VALUE.STRING;
        } else if (c == ARR_START) {
            // the current value is a json array
            return VALUE.ARRAY;
        } else if (c == OBJ_START) {
            //the current value is a json object
            return VALUE.OBJECT;
        } else {
            //the current value is either a string or a float
            return VALUE.NUMBER;
        }
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

    private static int getBoundedValue(String s, int start, final char START_STRING, final char END_STRING) {
        int closedVal = 1;

        start = s.indexOf(START_STRING);

        while (closedVal > 0) {
            //shift start one ahead of starting brace
            start++;
            int openBrace = s.indexOf(START_STRING, start), closedBrace = s.indexOf(END_STRING, start);
            if (openBrace > 0 && openBrace < closedBrace) {
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