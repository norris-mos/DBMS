package ed.inf.adbs.minibase;

public class ProjectionOperator implements Operator {
    private Operator childOperator;
    private List<String> projectionList;

    public ProjectionOperator(Operator childOperator, List<String> projectionList) {
        this.childOperator = childOperator;
        this.projectionList = projectionList;
    }

    @Override
    public void open() throws OperatorException {
        childOperator.open();
    }

    @Override
    public Tuple getNextTuple() throws OperatorException {
        Tuple tuple = childOperator.getNextTuple();
        if (tuple == null) {
            return null;
        }
        List<String> projectedFields = new ArrayList<>();
        for (String fieldName : projectionList) {
            projectedFields.add(tuple.getField(fieldName));
        }
        return new Tuple(projectedFields.toArray(new String[0]));
    }

    @Override
    public void close() throws OperatorException {
        childOperator.close();
    }
}