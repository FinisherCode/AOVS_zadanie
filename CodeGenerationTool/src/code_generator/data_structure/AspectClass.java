package code_generator.data_structure;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AspectClass {

    private String name;
    private String visibility;
    private boolean isAbstract;
    private boolean isStatic;
    private String parrent;
    private List<Method> methods = null;

    public AspectClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public String getParrent() {
        return parrent;
    }

    public void setParrent(String parrent) {
        this.parrent = parrent;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(visibility.equals("public")){
            builder.append(visibility).append(" ");
        }

        if (isAbstract) {
            builder.append("abstract ");
        }
        builder.append("aspect").append(" ");
        builder.append(name);
        if (parrent != null) {
            builder.append(" extends ").append(parrent);
        }
        builder.append(" {\n\n");

        if (methods != null) {
            for (Method method : methods) {
                builder.append(method).append("\n");
            }
        }

        builder.append("\n}");

        return builder.toString();
    }

    void saveClass(File outputDirectory) {
        PrintWriter out = null;
        try {
            File outputFile = new File(outputDirectory.getAbsolutePath() + "/" + name + ".aj");
            outputFile.createNewFile();
            out = new PrintWriter(outputFile.getAbsolutePath());
            out.println("package " + "generated." + outputDirectory.getName() + ";\n");
            out.println(this);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }


}
