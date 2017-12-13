package code_generator.data_structure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Model {

    private List<AspectClass> classes = null;
    private File outputDirectory = null;

    public Model() {
        classes = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (classes != null) {
            for (AspectClass aspectClass : classes) {
                builder.append(aspectClass).append("\n");
            }
        }
        return builder.toString();
    }

    public void addClass(AspectClass aspectClass) {
        this.classes.add(aspectClass);
    }

    private void createFileSystem() {
        File f = new File("");
        outputDirectory = new File(f.getAbsolutePath() + "/src/generated/time" + System.currentTimeMillis());
        outputDirectory.mkdirs();
    }

    public void saveModel() {
        createFileSystem();
        if(classes != null){
            for (AspectClass aspectClass:classes) {
                aspectClass.saveClass(outputDirectory);
            }
        }
    }
}
