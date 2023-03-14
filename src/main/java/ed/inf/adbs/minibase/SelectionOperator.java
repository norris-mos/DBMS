package ed.inf.adbs.minibase;

import java.util.List;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;

public class SelectionOperator extends Operator {
    private Operator child;
    private List<ComparisonAtom> ComparisonAtoms;
    private List<Term> childschema;

    public SelectionOperator(Operator child, List<ComparisonAtom> ComparisonAtoms) {
        this.child = child;
        this.ComparisonAtoms = ComparisonAtoms;
        // these are the variables in the query of relation being handled
        this.childschema = ((ScanOperator) child).getChildTerms();
    }

    @Override
    public void open() throws Exception {
        child.open();
    }

    @Override
    public Tuple getNextTuple() throws Exception {
        Tuple tuple;
        while ((tuple = child.getNextTuple()) != null) {

            boolean match = true;
            String[] fields = tuple.getFields();

            for (ComparisonAtom ComparisonAtom : ComparisonAtoms) {
                Term comparisonVar = ComparisonAtom.getTerm1();

                int comparisonIndex = childschema.indexOf(comparisonVar);
                String columnValue = fields[comparisonIndex].trim();
                String selectionTerm = ComparisonAtom.getTerm2String().trim();

                if (!compareValues(columnValue, ComparisonAtom.getOpString(),
                        selectionTerm)) {
                    match = false;

                    break;
                }
            }
            if (match) {
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
                // handle the default case by returning false
                return false;
        }
    }

}
