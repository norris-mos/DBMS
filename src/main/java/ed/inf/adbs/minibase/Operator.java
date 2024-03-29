package ed.inf.adbs.minibase;

import java.io.PrintStream;
import java.util.List;

import ed.inf.adbs.minibase.base.Attribute;
import ed.inf.adbs.minibase.base.Tuple;

public abstract class Operator {
    protected List<Attribute> schema; // the schema of the operator's output tuples

    public abstract void open() throws Exception; // initializes the operator

    public abstract Tuple getNextTuple() throws Exception; // gets the next tuple from the operator's output

    public abstract void close() throws Exception; // closes the operator

    public void dump(PrintStream out) throws Exception {
        Tuple tuple;
        while ((tuple = getNextTuple()) != null) {
            out.println(tuple.getName());
        }
    }

    public List<Attribute> getSchema() {
        return schema;
    }

    public void reset() {
        // The implementation of this method will depend on the specific subclass of
        // Operator
        // that is being used. However, a common approach is to close and then reopen
        // the operator.
        try {
            close();
            open();
        } catch (Exception e) {
            // Handle any exceptions that occur during the reset process
            e.printStackTrace();
        }
    }

}
