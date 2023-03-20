package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;
import ed.inf.adbs.minibase.base.Variable;
import ed.inf.adbs.minibase.base.Head;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory database system
 *
 */
public class Minibase {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            System.err.println("Usage: Minibase database_dir input_file output_file");
            return;
        }

        String databaseDir = args[0];
        String inputFile = args[1];
        String outputFile = args[2];

        // evaluateCQ(databaseDir, inputFile, outputFile);

        parsingExample(inputFile);

    }

    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) throws Exception {
        // TODO: add your implementation
        Query query = QueryParser.parse(Paths.get(inputFile));
        QueryInterpreter plan = new QueryInterpreter();
        plan.QueryPlan(query);
        List<Operator> ops = plan.getOperators();
        for (Operator op : ops) {
            System.out.println(op);
        }
        Operator projectionOp = ops.get(ops.size() - 1);
        projectionOp.open();
        projectionOp.dump(System.out);

    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static List<ComparisonAtom> comparisonExtractor(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));

            List<Atom> body = query.getBody();

            List<ComparisonAtom> catoms = body.stream()
                    .filter(atom -> atom instanceof ComparisonAtom)
                    .map(atom -> ((ComparisonAtom) atom))
                    .collect(Collectors.toList());

            System.out.println("Comparison atoms: " + catoms);

            return catoms;
        } catch (Exception e) {
            System.err.println("Error parsing query: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static void parsingExample(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));

            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w), z < w");
            // Query query = QueryParser.parse("Q(SUM(x * 2 * x)) :- R(x, 'z'), S(4, z, w),
            // 4 < 'test string' ");

            System.out.println("Entire query: " + query);
            // Head head = query.getHead();
            // System.out.println("Head: " + head);

            List<Atom> body = query.getBody();
            Head head = query.getHead();
            List<RelationalAtom> ratoms = query.getRelationalAtoms();
            List<ComparisonAtom> catoms = query.getComparisonAtoms();
            List<ComparisonAtom> joins = query.getJoins();
            ComparisonAtom join = joins.get(0);
            List<ComparisonAtom> selections = query.getSelections();

            List<Variable> projectionlist = head.getVariables();
            System.out.println("Body: " + body);
            System.out.println("head: " + head);
            System.out.println("sum agg: " + head.getSumAggregate().getProductTerms());

            // System.out.println("relational atoms : " + ratoms);
            // System.out.println("comparison atoms: " + catoms);
            // System.out.println("joins: " + joins);
            // System.out.println("selections: " + selections);

            // List<ComparisonAtom> catoms = query.getComparisonAtoms();
            // List<RelationalAtom> ratoms = query.getRelationalAtoms();
            // List<ComparisonAtom> joinop = query.getJoinAtoms();
            // System.out.println("catoms: " + catoms);
            // System.out.println("ratoms: " + ratoms);

            ScanOperator child1 = new ScanOperator((RelationalAtom) body.get(0));
            ScanOperator child2 = new ScanOperator((RelationalAtom) body.get(1));
            JoinOperator join1 = new JoinOperator(child1, child2, joins);
            join1.open();
            join1.dump(System.out);

            // ProjectionOperator p1 = new ProjectionOperator(child1, projectionlist);
            // p1.open();
            // p1.dump(System.out);

            // SelectionOperator selecta = new SelectionOperator(child1, catoms);
            // selecta.open();
            // selecta.dump(System.out);
            // System.out.println("tuple results" + selecta.getNextTuple());
            // System.out.println("tuple results" + selecta.getNextTuple());
            // System.out.println("tuple results" + selecta.getNextTuple());
            // compatoms.get(0).toSring(),
            // ((String) compatoms.get(0).term2));

        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
