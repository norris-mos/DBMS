package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Tuple;

public class SelectionOperator extends Operator {
    private Operator child;
    private int columnIndex;
    private String comparisonOperator;
    private String value;

    public SelectionOperator(Operator child, int columnIndex, String comparisonOperator, String value) {
        this.child = child;
        this.columnIndex = columnIndex;
        this.comparisonOperator = comparisonOperator;
        this.value = value;
    }

    @Override
    public void open() throws Exception {
        child.open();
    }

    @Override
    public Tuple getNextTuple() throws Exception {
        Tuple tuple;
        while ((tuple = child.getNextTuple()) != null) {
            String[] fields = tuple.getFields();
            String columnValue = fields[columnIndex];
            if (compareValues(columnValue, comparisonOperator, value)) {
                return tuple;
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        child.close();
    }

    private boolean compareValues(String value1, String comparisonOperator, String value2) {
        switch (comparisonOperator) {
            case "=":
                return value1.equals(value2);
            case "!=":
                return !value1.equals(value2);
            case ">":
                return Double.parseDouble(value1) > Double.parseDouble(value2);
            case "<":
                return Double.parseDouble(value1) < Double.parseDouble(value2);
            case ">=":
                return Double.parseDouble(value1) >= Double.parseDouble(value2);
            case "<=":
                return Double.parseDouble(value1) <= Double.parseDouble(value2);
            default:
                throw new IllegalArgumentException("Invalid comparison operator: " + comparisonOperator);
        }
    }
}