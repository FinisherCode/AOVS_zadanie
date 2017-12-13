package code_generator.data_structure;

import java.util.ArrayList;
import java.util.List;

public class Method {

    private int type;

    private String generatedCode;

    private List<Parameter> parameters;

    private String returnType;

    private String visibility;

    private String name;

    private boolean isAbstract;
    private boolean isStatic;

    public Method(int type) {
        this.type = type;
        parameters = new ArrayList<>();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCode(String generatedCode) {
        this.generatedCode = generatedCode;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
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
        if (type == 0) {
            builder.append(visibility).append(" ");
            if(isStatic){
                builder.append("static ");
            }
            if(isAbstract){
                builder.append("abstract ");
            }
            builder.append("pointcut ");
        }
        builder.append(name).append("(");
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                if (i != 0) {
                    builder.append(", ");
                }
                builder.append(parameters.get(i));
            }
        }
        builder.append(")");
        if (returnType != null) {
            builder.append(" : ").append(returnType);
        }

        if (type == 0) {
            builder.append(";");
        } else if (type == 1) {
            builder.append("{}");
        }

//            if (methodReturn == null) {
//                resultMethod.setGeneratedCode(visibilty + " pointcut " + name + "(" + createMethodParameters(item) + ");");
//            } else {
//                resultMethod.setGeneratedCode(visibilty + " pointcut " + name + "(" + createMethodParameters(item) + ") : " + methodReturn + ";");
//            }
//            if (methodReturn == null) {
//                resultMethod.setGeneratedCode(name + "(" + createMethodParameters(item) + ");");
//            } else {
//                resultMethod.setGeneratedCode(name + "(" + createMethodParameters(item) + ") : " + methodReturn + "{}");
//            }
        return "\t" + builder.toString();
    }
}
