import java.util.List;

public class Tester {
    public static void main(String[] args) {
        //test jsonparser here

        //create a json object using paul's syntax
        // {weather: 'good'}
        //note that the keys don't have quotes and strings are single quotes
        //assumes arrays are wrapped with [] and separated by commas

        String json = "{weather: 'good', stuff: 'bad', other: 'okay', int: 3, float: 4.5, object: {name: 'anObj', list: [1, 2.0, '3', [2], {name: 'child'} ] } }";

        CustomJSONParser parser = CustomJSONParser.initialize(json);

        System.out.println(parser.getString("bloop")); //null
        System.out.println(parser.getString("other")); //okay
        System.out.println(parser.getInt("int")); //3
        System.out.println(parser.getFloat("float")); //4.5
        System.out.println(parser.getJSONObject("object"));
        CustomJSONParser child = CustomJSONParser.initialize(parser.getJSONObject("object"));
        System.out.println(child.getString("name")); //anObj

        List<Object> array = child.getJSONArray("list");

        System.out.println(array.get(2)); //3
        System.out.println(((CustomJSONParser) array.get(4)).getString("name"));

        System.out.println(parser.checkIfTagExists("stuff")); //true
        System.out.println(parser.checkIfTagExists("sad")); //false

        System.out.println(parser.maxDepth()); //3

        System.out.println(child.maxDepth()); //2

    }
}
