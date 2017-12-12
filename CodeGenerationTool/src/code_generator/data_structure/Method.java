package code_generator.data_structure;

public class Method {
    private int type;
    private String generatedCode;

    public Method(int type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "\t" + this.generatedCode;
    }
}
