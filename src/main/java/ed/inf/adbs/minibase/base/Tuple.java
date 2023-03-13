package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.List;

public class Tuple {
    private String schema;
    private String tuple;
    private Object[] tupleobject;

    public Tuple(String tuple, String schema) {

        this.tuple = tuple;
        this.schema = schema;
        this.tupleobject = parseString(tuple, schema);

    }

    public String getName() {
        return tuple;
    }

    public String getSchema() {
        return schema;
    }

    public Object[] getItems() {
        return tupleobject;
    }

    public String[] getFields() {
        String[] fields = tuple.split(",");
        return fields;
    }

    public static Object[] parseString(String input, String types) {
        // Split the input string by commas and trim the whitespace from each value
        String[] values = input.split(",");
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
        }

        // Split the types string by spaces and trim the whitespace from each type
        String[] typeNames = types.split("\\s+");
        for (int i = 0; i < typeNames.length; i++) {
            typeNames[i] = typeNames[i].trim().toLowerCase();
        }

        // Check that the number of types matches the number of values
        if (typeNames.length != values.length) {
            throw new IllegalArgumentException("Number of types does not match number of values");
        }

        // Convert each value to the appropriate object type based on its type name
        Object[] output = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            String typeName = typeNames[i];
            switch (typeName) {
                case "int":
                    output[i] = Integer.parseInt(value);
                    break;
                case "double":
                    output[i] = Double.parseDouble(value);
                    break;
                case "boolean":
                    output[i] = Boolean.parseBoolean(value);
                    break;
                case "string":
                    output[i] = value;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type: " + typeName);
            }
        }

        return output;
    }

}
