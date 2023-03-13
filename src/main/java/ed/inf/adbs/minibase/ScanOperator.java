package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.OperatorException;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Tuple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ScanOperator extends Operator {
    private String RelationName;
    private String RelationSchema;
    private BufferedReader reader;
    private String currentLine;

    public ScanOperator(RelationalAtom Relation) {
        this.RelationName = Relation.getName();
        this.RelationSchema = DataCatalog.getInstance().getDatabaseSchema(RelationName);

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
            currentLine = reader.readLine();
            if (currentLine == null) {
                return null;
            }
            String[] fields = currentLine.split(",");
            return new Tuple(currentLine, RelationSchema);
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
