package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ed.inf.adbs.minibase.base.Constant;
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

    public SumOperator(Operator child, List<Term> groupByTerms, SumAggregate agg) {
        this.child = child;
        this.groupByTerms = groupByTerms;
        this.groupByColumns = new ArrayList<>();
        this.aggterms = agg.getProductTerms();
        this.batch = new ArrayList<>();
        this.aggtotal = 0;

        for (Term term : groupByTerms) {
            this.groupByColumns.add(term.toString());
        }
        this.sumColumn = sumColumn;

    }

    @Override
    public void open() throws Exception {
        child.open();

        Tuple tuple;
        while ((tuple = child.getNextTuple()) != null) {
            // System.out.println(tuple);
            batch.add(tuple);
        }
    }

    @Override
    public Tuple getNextTuple() throws Exception {
        if (resultIterator == null) {
            // Generate the iterator for the result map
            resultIterator = sumAgg().entrySet().iterator();
        }

        if (resultIterator.hasNext()) {
            Map.Entry<String, Integer> entry = resultIterator.next();

            // Construct the tuple using the group-by values and aggregate value
            List<String> fields = new ArrayList<>();
            fields.add(entry.getKey().toString());
            fields.add(entry.getValue().toString());

            String schema = String.join(" ", Arrays.asList("string", "int"));
            // System.out.println(fields);
            // System.out.println(schema);
            // System.out.println(Utils.join(fields, ", "));
            // if()
            // List<Term> terms = new ArrayList<>();
            // Variable t1 = 'x';
            // char t2 = 'y';

            Tuple tuple = new Tuple(Utils.join(fields, ", "), schema, null);
            return tuple;
        } else {
            // All tuples have been returned
            return null;
        }
    }

    public Map<String, Integer> sumAgg() throws Exception {
        List<Tuple> tuples = batch;
        Map<String, Integer> results = new HashMap<>();

        if (tuples == null) {

            return results;
        }
        System.out.println(groupByTerms);
        if (Objects.isNull(groupByTerms) || groupByTerms.isEmpty()) {

            System.out.println("No groupby terms");
            if (aggterms.size() < 2) {
                if (aggterms.get(0) instanceof Constant) {
                    for (Tuple tup : tuples) {
                        System.out.println(tup);
                        aggtotal++;
                    }
                    String aggtermint = aggterms.get(0).toString();
                    results.put("Fix", Integer.parseInt(aggtermint) * aggtotal);

                } else {
                    for (Tuple tup : tuples) {
                        // System.out.println(tup);
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
                for (Tuple tup : tuples) {

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
            for (Tuple tup : tuples) {
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

    // public Map<List<String>, Integer> sumAgg() throws Exception {
    // List<Tuple> tuples = batch;
    // Map<List<String>, Integer> result = new HashMap<>();
    // if (tuples == null) {

    // return new HashMap<>();
    // ;
    // }

    // if (groupByTerms == null) {
    // if (aggterms.size() < 2) {
    // if (aggterms.get(0) instanceof Constant) {
    // for (Tuple tup : tuples) {
    // aggtotal++;
    // }

    // } else {
    // for (Tuple tup : tuples) {
    // List<Term> tfields = tup.getTermfield();
    // int tindex = tfields.indexOf(aggterms.get(0));
    // aggtotal += (int) tup.getItems()[tindex];
    // }

    // }
    // return result.put(Collections.emptyList(), aggtotal);

    // } else {
    // List<Integer> aggindex = new ArrayList<>();
    // for (Tuple tup : tuples) {

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

    // }
    // return aggtotal;
    // } else {
    // // Groupby case
    // for (Tuple tup : tuples) {
    // List<String> groupByValues = new ArrayList<>();
    // for (Term groupByTerm : groupByTerms) {
    // int tindex = tup.getTermfield().indexOf(groupByTerm);
    // groupByValues.add(tup.getFields()[tindex]);
    // }
    // int aggValue = 0;
    // if (aggterms.size() == 1 && aggterms.get(0) instanceof SumAggregate) {
    // // Single SUM aggregate case
    // Term sumTerm = ((SumAggregate) aggterms.get(0)).getTerm();
    // int tindex = tup.getTermfield().indexOf(sumTerm);
    // aggValue = Integer.parseInt(tup.getFields()[tindex]);
    // } else {
    // // Multi-term aggregate case
    // int tupProduct = 1;
    // for (Term agg : aggterms) {
    // int tindex = tup.getTermfield().indexOf(agg);
    // int val = Integer.parseInt(tup.getFields()[tindex]);
    // tupProduct *= tupProduct * val;
    // }
    // aggValue = tupProduct;
    // }
    // if (result.containsKey(groupByValues)) {
    // result.put(groupByValues, result.get(groupByValues) + aggValue);
    // } else {
    // result.put(groupByValues, aggValue);
    // }
    // }
    // }
    // return result;
    // }

    // if(aggterms.size()<2){

    // }else{
    // //product section
    // }

    // for (Tuple tup : tuples) {
    // List<Term> tfields = tup.getTermfield();
    // int aggindex = tfields.get(aggterms)
    // int tupleValue = tfields

    // }
    // }

    // Map<Term, Integer> groupSums = new HashMap<>();for(
    // Tuple tuple:tuples)
    // {
    // List<Term> termfield = tuple.getTermfield();
    // List<Term> groupByValues = new ArrayList<>();
    // for (Term term : groupByTerms) {

    // int termIndex = termfield.indexOf(term);

    // groupByValues.add(tuple.getFields()[termIndex]);
    // }
    // int sum = groupSums.getOrDefault(groupByValues, 0);
    // sum += ((int) tuple.get(sumColumn));
    // groupSums.put(groupByValues, sum);
    // }

    // List<Tuple> resultTuples = new ArrayList<>();for(
    // Map.Entry<List<Object>, Integer> entry:groupSums.entrySet())
    // {
    // List<Object> groupByValues = entry.getKey();
    // int sum = entry.getValue();
    // Tuple resultTuple = new Tuple();
    // for (int i = 0; i < groupByColumns.size(); i++) {
    // resultTuple.add(groupByColumns.get(i), groupByValues.get(i));
    // }
    // resultTuple.add(sumColumn, sum);
    // resultTuples.add(resultTuple);
    // }

    // return resultTuples.get(0);
    // }

    @Override
    public void close() throws Exception {
        child.close();
    }
}
