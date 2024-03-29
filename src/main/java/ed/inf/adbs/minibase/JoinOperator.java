package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import ed.inf.adbs.minibase.base.OperatorException;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;
import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.ComparisonOperator;

public class JoinOperator extends Operator {

    private Operator leftInput;
    private Operator rightInput;
    private List<ComparisonAtom> joinAtom;
    private Tuple currentLeftTuple;
    private List<Tuple> rightTuples;
    private int currentRightTupleIndex;
    private int tupleCount;

    public JoinOperator(Operator leftInput, Operator rightInput,
            List<ComparisonAtom> joinAtom) {
        this.leftInput = leftInput;
        this.rightInput = rightInput;
        this.joinAtom = joinAtom;
        this.rightTuples = new ArrayList<>();
        this.tupleCount = 0;
    }

    public String getJoinAtom() {
        return joinAtom.get(0).toString();
    }

    @Override
    public void open() throws Exception {
        leftInput.open();
        rightInput.open();
        currentLeftTuple = leftInput.getNextTuple();
        currentRightTupleIndex = 0;
        loadRightTuples();
    }

    // Need to make sure that the join conditions passed are specific to the
    // Relations in question.
    @Override
    public Tuple getNextTuple() throws Exception {
        Tuple result = null;
        while (currentLeftTuple != null) {
            while (currentRightTupleIndex < rightTuples.size()) {
                Tuple currentRightTuple = rightTuples.get(currentRightTupleIndex);
                Tuple joinedTuple = joinTuples(currentLeftTuple, currentRightTuple);
                // System.out.println(joinedTuple.getTermfield());
                // System.out.println(joinedTuple.getName());
                boolean satisfiesJoinConditions = true;
                for (ComparisonAtom atom : joinAtom) {
                    // System.out.println(atom);
                    // System.out.println(evaluateJoin(joinedTuple, atom));
                    if (!evaluateJoin(joinedTuple, atom)) {

                        // System.out.println("Doesnt satisfy");
                        satisfiesJoinConditions = false;
                        // System.out.println(joinedTuple.getName());
                        // System.out.println(satisfiesJoinConditions);
                        break;
                    }
                }
                if (satisfiesJoinConditions) {
                    // System.out.println(satisfiesJoinConditions);
                    result = joinedTuple;
                    currentRightTupleIndex++;
                    tupleCount++;
                    // System.out.println(result);
                    return result;
                } else {
                    currentRightTupleIndex++;
                }
            }
            currentLeftTuple = leftInput.getNextTuple();
            currentRightTupleIndex = 0;
            loadRightTuples();
        }

        System.out.println("intermediate joins = " + tupleCount);
        return result;
    }

    public static boolean evaluateJoin(Tuple joinedTuple, ComparisonAtom joinCondition) {
        if (joinCondition.getOp().equals(ComparisonOperator.XR)) {
            return true;
        } else {
            Term attribute1 = joinCondition.getTerm1();
            Term attribute2 = joinCondition.getTerm2();
            List<Term> variableTerms = joinedTuple.getTermfield();
            List<Integer> indexes = new ArrayList<>();
            int leftIndex;
            int rightIndex;
            if (attribute1.equals(attribute2)) {
                IntStream.range(0, variableTerms.size())
                        .filter(i -> variableTerms.get(i).equals(attribute1))
                        .forEach(indexes::add);
                // System.out.println(indexes.get(0));
                // System.out.println(indexes.get(1));

                leftIndex = indexes.get(0);
                rightIndex = indexes.get(1);
            } else {

                leftIndex = variableTerms.indexOf(attribute1);
                rightIndex = variableTerms.indexOf(attribute2);

            }

            String value1 = joinedTuple.getFields()[leftIndex].trim();
            String value2 = joinedTuple.getFields()[rightIndex].trim();
            // System.out.println(variableTerms);
            // System.out.println(value1 + "=" + value2);
            // System.out.println(leftIndex + "=" + rightIndex);
            // System.out.println("value1: " + value1 + " Value2: " + value2);
            // System.out.println("comparison check " + compareValues(value1,
            // joinCondition.getOpString(), value2));

            return compareValues(value1, joinCondition.getOpString(), value2);

        }
    }

    public static boolean compareValues(String value1, String comparisonOperator,
            String value2) {
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
            case "X":
                return true;
            default:
                // handle the default case by returning false
                return false;
        }
    }

    public static Tuple joinTuples(Tuple left, Tuple right) {
        String joinedTuple = left.getName() + ", " + right.getName();
        String joinedSchema = left.getSchema() + " " + right.getSchema();
        List<Term> joinedTermField = new ArrayList<>(left.getTermfield());
        joinedTermField.addAll(right.getTermfield());
        return new Tuple(joinedTuple, joinedSchema, joinedTermField);

    }

    @Override
    public void close() throws Exception {
        leftInput.close();
        rightInput.close();
    }

    private void loadRightTuples() throws Exception {
        rightTuples.clear();
        Tuple currentRightTuple;
        while ((currentRightTuple = rightInput.getNextTuple()) != null) {
            rightTuples.add(currentRightTuple);
        }
        rightInput.reset();
    }
}
