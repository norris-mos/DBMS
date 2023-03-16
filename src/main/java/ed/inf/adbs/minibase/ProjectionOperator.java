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

        String[] oldschema = tuple.getSchema().split("\\s+");
        StringBuilder projectedSchema = new StringBuilder();

        int count = 0; // Keep track of number of fields added to projectedFields
        for (Variable fieldName : projectionList) {
            // System.out.println(fieldName);
            // check if the field name is in the relation field
            List<Term> relationField = tuple.getTermfield();
            if (!relationField.contains(fieldName)) {
                // skip this field and move on to the next one
                continue;
            }
            // System.out.println(relationField);
            int tupleIndex = relationField.indexOf(fieldName);
            // System.out.println(tupleIndex);
            Term term = fieldName;
            newFields.add(term);

            projectedFields.append(tuple.getFields()[tupleIndex]);
            count++;
            if (count != projectionList.size()) {
                projectedFields.append(",");
            }

            projectedSchema.append(oldschema[tupleIndex]);
            projectedSchema.append(" ");
            // System.out.println(projectedFields);
        }
        return new Tuple(projectedFields.toString(), projectedSchema.toString(), newFields);
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