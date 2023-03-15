package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.base.OperatorException;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;
import ed.inf.adbs.minibase.base.Variable;

public class ProjectionOperator extends Operator {
    private Operator childOperator;
    private List<Variable> projectionList;

    public ProjectionOperator(Operator childOperator, List<Variable> projectionList) {
        this.childOperator = childOperator;
        this.projectionList = projectionList;
    }

    @Override
    public void open() throws OperatorException {
        try {
            childOperator.open();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Tuple getNextTuple() throws Exception {
        Tuple tuple = childOperator.getNextTuple();
        if (tuple == null) {
            return null;
        }
        StringBuilder projectedFields = new StringBuilder();
        List<Term> newFields = new ArrayList<>();

        for (Variable fieldName : projectionList) {
            // just get correct fields from string tuple
            List<Term> relationField = tuple.getTermfield();
            int tupleIndex = relationField.indexOf(fieldName);
            Term term = fieldName;
            newFields.add(term);

            projectedFields.append(tuple.getFields()[tupleIndex]);
        }
        return new Tuple(projectedFields.toString(), tuple.getSchema(), newFields);
    }

    @Override
    public void close() throws OperatorException {
        try {
            childOperator.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}