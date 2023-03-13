package ed.inf.adbs.minibase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataCatalog {

    private static DataCatalog instance = null;

    private Map<String, String> filePaths;
    private Map<String, String> databaseSchema;

    private DataCatalog() {
        filePaths = new HashMap<>();
        databaseSchema = new HashMap<>();
        initialize();
    }

    public static DataCatalog getInstance() {
        if (instance == null) {
            instance = new DataCatalog();
        }
        return instance;
    }

    void initialize() {
        String currentWorkingDir = System.getProperty("user.dir");
        String fileDirectory = currentWorkingDir + "/data/evaluation/db/files";

        File directory = new File(fileDirectory);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                String relationName = fileName.substring(0, fileName.lastIndexOf('.'));
                String filePath = file.getAbsolutePath();
                filePaths.put(relationName, filePath);
            }
        }

        String schemaFilePath = currentWorkingDir + "/data/evaluation/db/schema.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(schemaFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String relationName = parts[0];
                String schema = line.substring(relationName.length() + 1);
                databaseSchema.put(relationName, schema);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilePath(String relationName) {
        return filePaths.get(relationName);
    }

    public String getDatabaseSchema(String relationName) {
        return databaseSchema.get(relationName);
    }
}
