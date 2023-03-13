package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Tuple;
import ed.inf.adbs.minibase.base.Head;
import ed.inf.adbs.minibase.parser.QueryParser;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory database system
 *
 */
public class Minibase {

    public static void main(String[] args) {

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

    public static void evaluateCQ(String databaseDir, String inputFile, String outputFile) {
        // TODO: add your implementation
    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static List<String> comparisonExtractor(String filename) {
        try {
            Query query = QueryParser.parse(Paths.get(filename));

            List<Atom> body = query.getBody();

            List<String> catoms = body.stream()
                    .filter(atom -> atom instanceof ComparisonAtom)
                    .map(atom -> ((ComparisonAtom) atom).toString())
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

            // System.out.println("Entire query: " + query);
            // Head head = query.getHead();
            // System.out.println("Head: " + head);

            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
            List<String> compatoms = comparisonExtractor(filename);

            ScanOperator child1 = new ScanOperator((RelationalAtom) body.get(0));
            child1.open();

            SelectionOperator selecta = new SelectionOperator(child1, 1, compatoms.get(0).toSring(),
                    ((String) compatoms.get(0).term2));

        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
