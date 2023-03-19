package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.OperatorException;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Tuple;
import ed.inf.adbs.minibase.base.Variable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanOperator extends Operator {
    private String RelationName;
    private String RelationSchema;
    private BufferedReader reader;
    private String currentLine;
    private List<Term> terms;

    private List<Integer> index;

    public ScanOperator(RelationalAtom Relation) throws Exception {
        this.RelationName = Relation.getName();
        this.RelationSchema = DataCatalog.getInstance().getDatabaseSchema(RelationName);
        this.terms = Relation.getTerms();

        // Check that all terms are of the type variable else add to index
        this.index = new ArrayList<>();

        for (Term term : terms) {

            if (!(term instanceof Variable)) {

                index.add(terms.indexOf(term));

            }
        }
    }

    public List<Term> getChildTerms() {
        return terms;
    }

    public String getRelationName() {
        return RelationName;
    }

    @Override
    public void open() throws Exception {
        try {
            String relationpath = DataCatalog.getInstance().getFilePath(RelationName);
            reader = new BufferedReader(new FileReader(relationpath));
        } catch (FileNotFoundException e) {
            throw new Exception("Could not open data source: " + DataCatalog.getInstance().getFilePath(RelationName));
        }
    }

    @Override
    public Tuple getNextTuple() throws Exception {
        try {
            while (true) {
                currentLine = reader.readLine();
                if (currentLine == null) {
                    return null;
                }

                String[] fields = currentLine.split(",");
                if (index.isEmpty()) {

                    return new Tuple(currentLine, RelationSchema, terms);
                } else {
                    boolean check = true;
                    for (int i : index) {
                        String constantcheck = fields[i];
                        Term constinq = terms.get(i);
                        String constinqstring = constinq.toString();
                        if (!fields[i].equals(constinqstring)) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        return new Tuple(currentLine, RelationSchema, terms);
                    }
                }
            }
        } catch (IOException e) {
            throw new OperatorException(
                    "Could not read from data source: " + DataCatalog.getInstance().getFilePath(RelationName));
        }
    }

    @Override
    public void close() throws Exception {
        try {
            reader.close();
        } catch (IOException e) {
            throw new OperatorException(
                    "Could not close data source: " + DataCatalog.getInstance().getFilePath(RelationName));
        }
    }
}
