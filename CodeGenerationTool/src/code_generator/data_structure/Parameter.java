package code_generator.data_structure;

public class Parameter {

    private String type;
    private String name;

    public Parameter() {

    }

    public Parameter(String paramName, String paramType) {
        this.name = paramName;
        this.type = paramType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
