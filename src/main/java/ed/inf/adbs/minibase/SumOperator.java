
package ed.inf.adbs.minibase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ed.inf.adbs.minibase.base.Constant;
import ed.inf.adbs.minibase.base.OperatorException;
import ed.inf.adbs.minibase.base.SumAggregate;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;
import ed.inf.adbs.minibase.base.Variable;

public class SumOperator extends Operator {
    private Operator child;
    private List<Term> groupByTerms;
    private List<String> groupByColumns;
    private String sumColumn;
    private List<Term> aggterms;
    private List<Tuple> batch;
    private int aggtotal;
    private Map<String, Integer> results;
    private Iterator<Map.Entry<String, Integer>> resultIterator;
    private int tupleCount;
    private Map<String, Integer> lookforward;

    public SumOperator(Operator child, List<Term> groupByTerms, SumAggregate agg) throws Exception {
        this.child = child;
        this.groupByTerms = groupByTerms;
        this.groupByColumns = new ArrayList<>();
        this.aggterms = agg.getProductTerms();
        this.batch = new ArrayList<>();
        this.aggtotal = 0;
        this.tupleCount = 0;

        for (Term term : groupByTerms) {
            this.groupByColumns.add(term.toString());
        }
        this.sumColumn = sumColumn;

    }

    @Override
    public void open() throws Exception {
        child.open();
        this.lookforward = lookForward();

        System.out.println(lookforward);

    }

    @Override
    public Tuple getNextTuple() throws Exception {
        if (lookforward.isEmpty()) {
            System.out.println("Total intermediate tuples" + tupleCount);
            return null;
        }
        String nextKey = lookforward.keySet().iterator().next();
        int nextValue = lookforward.remove(nextKey);
        String resultsTuple = nextKey + ", " + nextValue;
        String schema = "string int";
        List<Term> termfield = new ArrayList<>();

        termfield.add(new Term());
        termfield.add(new Term());
        Tuple t = new Tuple(resultsTuple, schema, termfield);
        System.out.println(t);

        return t;
    }

    public Map<String, Integer> lookForward() throws Exception {
        Map<String, Integer> results = new HashMap<>();
        System.out.println("TEST" + groupByTerms);

        if (Objects.isNull(groupByTerms) || groupByTerms.isEmpty()) {
            System.out.println("No Groupby terms");
            if (aggterms.size() < 2) {
                if (aggterms.get(0) instanceof Constant) {

                    while (child.getNextTuple() != null) {
                        tupleCount++;
                        aggtotal++;
                    }
                    String aggtermint = aggterms.get(0).toString();
                    results.put("Fix", Integer.parseInt(aggtermint) * aggtotal);

                } else {
                    Tuple tup;
                    while ((tup = child.getNextTuple()) != null) {

                        tupleCount++;
                        List<Term> tfields = tup.getTermfield();
                        // System.out.println(aggterms.get(0));
                        int tindex = tfields.indexOf(aggterms.get(0));
                        // System.out.println(tup.getFields());
                        System.out.println(Arrays.toString(tup.getFields()));

                        aggtotal += (int) tup.getItems()[tindex];

                    }
                    results.put("Fix", aggtotal);
                }
            } else {
                List<Integer> aggindex = new ArrayList<>();
                Tuple tup;
                while ((tup = child.getNextTuple()) != null) {

                    tupleCount++;

                    List<Term> tfields = tup.getTermfield();
                    String[] tupValues = tup.getFields();

                    int tupProduct = 1;
                    for (Term agg : aggterms) {

                        int tindex = tfields.indexOf(agg);
                        int val = Integer.parseInt(tupValues[tindex]);
                        tupProduct *= tupProduct * val;
                    }
                    aggtotal += tupProduct;

                }
                String fix = "fix";
                results.put(fix, aggtotal);

            }
        } else {
            Map<String, Integer> groupSums = new HashMap<>();
            Tuple tup;
            while ((tup = child.getNextTuple()) != null) {

                tupleCount++;
                List<String> aggValue = new ArrayList<>();
                for (Term aggTerm : aggterms) {
                    List<Term> tfields = tup.getTermfield();
                    int tindex = tfields.indexOf(aggTerm);
                    aggValue.add(tup.getFields()[tindex]);
                }
                Integer product = aggValue.stream()
                        .map(String::trim) // remove trailing whitespaces
                        .mapToInt(Integer::parseInt)
                        .reduce(1, (a, b) -> a * b);

                List<String> groupValues = new ArrayList<>();
                for (Term groupByTerm : groupByTerms) {
                    List<Term> tfields = tup.getTermfield();
                    int tindex = tfields.indexOf(groupByTerm);
                    String val = tup.getFields()[tindex];
                    if (groupSums.containsKey(val)) {
                        groupSums.put(val, groupSums.get(val) + product);
                    } else {
                        groupSums.put(val, product);
                    }
                }
            }

            results = groupSums;

        }

        return results;
    }

    @Override
    public void close() throws OperatorException {
        try {
            child.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

// package ed.inf.adbs.minibase;

// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Map;
// import java.util.Objects;

// import ed.inf.adbs.minibase.base.Constant;
// import ed.inf.adbs.minibase.base.OperatorException;
// import ed.inf.adbs.minibase.base.SumAggregate;
// import ed.inf.adbs.minibase.base.Term;
// import ed.inf.adbs.minibase.base.Tuple;
// import ed.inf.adbs.minibase.base.Variable;

// public class SumOperator extends Operator {
// private Operator child;
// private List<Term> groupByTerms;
// private List<String> groupByColumns;
// private String sumColumn;
// private List<Term> aggterms;
// private List<Tuple> batch;
// private int aggtotal;
// private Map<String, Integer> results;
// private Iterator<Map.Entry<String, Integer>> resultIterator;
// private int tupleCount;

// public SumOperator(Operator child, List<Term> groupByTerms, SumAggregate agg)
// {
// this.child = child;
// this.groupByTerms = groupByTerms;
// this.groupByColumns = new ArrayList<>();
// this.aggterms = agg.getProductTerms();
// this.batch = new ArrayList<>();
// this.aggtotal = 0;
// this.tupleCount = 0;

// for (Term term : groupByTerms) {
// this.groupByColumns.add(term.toString());
// }
// this.sumColumn = sumColumn;

// }

// @Override
// public void open() throws Exception {
// child.open();

// Tuple tuple;
// while ((tuple = child.getNextTuple()) != null) {
// tupleCount++;
// // System.out.println(tuple);
// batch.add(tuple);
// }
// }

// @Override
// public Tuple getNextTuple() throws Exception {
// if (resultIterator == null) {

// // Generate the iterator for the result map
// resultIterator = sumAgg().entrySet().iterator();
// }

// if (resultIterator.hasNext()) {
// tupleCount++;
// Map.Entry<String, Integer> entry = resultIterator.next();

// // Construct the tuple using the group-by values and aggregate value
// List<String> fields = new ArrayList<>();
// fields.add(entry.getKey().toString());
// fields.add(entry.getValue().toString());

// String schema = String.join(" ", Arrays.asList("string", "int"));
// // System.out.println(fields);
// // System.out.println(schema);
// // System.out.println(Utils.join(fields, ", "));
// // if()
// // List<Term> terms = new ArrayList<>();
// // Variable t1 = 'x';
// // char t2 = 'y';

// Tuple tuple = new Tuple(Utils.join(fields, ", "), schema, null);
// return tuple;
// } else {
// System.out.println("Total tuple count for SUM OP " + tupleCount);
// // All tuples have been returned
// return null;
// }
// }

// public Map<String, Integer> sumAgg() throws Exception {
// List<Tuple> tuples = batch;
// Map<String, Integer> results = new HashMap<>();

// if (tuples == null) {

// return results;
// }

// if (Objects.isNull(groupByTerms) || groupByTerms.isEmpty()) {

// System.out.println("No groupby terms");
// if (aggterms.size() < 2) {
// if (aggterms.get(0) instanceof Constant) {
// for (Tuple tup : tuples) {
// tupleCount++;
// System.out.println(tup);
// aggtotal++;
// }
// String aggtermint = aggterms.get(0).toString();
// results.put("Fix", Integer.parseInt(aggtermint) * aggtotal);

// } else {
// for (Tuple tup : tuples) {
// tupleCount++;
// // System.out.println(tup);
// List<Term> tfields = tup.getTermfield();
// // System.out.println(aggterms.get(0));
// int tindex = tfields.indexOf(aggterms.get(0));
// // System.out.println(tup.getFields());
// System.out.println(Arrays.toString(tup.getFields()));

// aggtotal += (int) tup.getItems()[tindex];
// }
// results.put("Fix", aggtotal);

// }

// } else {
// List<Integer> aggindex = new ArrayList<>();
// for (Tuple tup : tuples) {
// tupleCount++;

// List<Term> tfields = tup.getTermfield();
// String[] tupValues = tup.getFields();

// int tupProduct = 1;
// for (Term agg : aggterms) {

// int tindex = tfields.indexOf(agg);
// int val = Integer.parseInt(tupValues[tindex]);
// tupProduct *= tupProduct * val;
// }
// aggtotal += tupProduct;

// }
// String fix = "fix";
// results.put(fix, aggtotal);
// }
// } else {
// Map<String, Integer> groupSums = new HashMap<>();
// for (Tuple tup : tuples) {
// tupleCount++;
// List<String> aggValue = new ArrayList<>();
// for (Term aggTerm : aggterms) {
// List<Term> tfields = tup.getTermfield();
// int tindex = tfields.indexOf(aggTerm);
// aggValue.add(tup.getFields()[tindex]);
// }
// Integer product = aggValue.stream()
// .map(String::trim) // remove trailing whitespaces
// .mapToInt(Integer::parseInt)
// .reduce(1, (a, b) -> a * b);

// List<String> groupValues = new ArrayList<>();
// for (Term groupByTerm : groupByTerms) {
// List<Term> tfields = tup.getTermfield();
// int tindex = tfields.indexOf(groupByTerm);
// String val = tup.getFields()[tindex];
// if (groupSums.containsKey(val)) {
// groupSums.put(val, groupSums.get(val) + product);
// } else {
// groupSums.put(val, product);
// }
// }
// }

// results = groupSums;

// }

// return results;
// }

// @Override
// public void close() throws OperatorException {
// try {
// child.close();
// } catch (Exception e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }
// }
