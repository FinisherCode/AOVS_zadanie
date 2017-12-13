package code_generator.controller;

import code_generator.data_structure.AspectClass;
import code_generator.data_structure.Model;
import code_generator.data_structure.Parameter;
import code_generator.data_structure.supportive.GeneralizationPair;
import code_generator.data_structure.Method;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class XMIParser {

    private final static String ATTRIBUTES = "UML:Attribute";
    private final static String INTERFACES = "UML:Interface";
    private final static String METHODS = "UML:Operation";
    private final static String CLASSES = "UML:Classifier.feature"; //masih salah
    private final static String COUPLINGS = "UML:AssociationEnd";
    private final static String CLASS_INHERITANCES = "UML:GeneralizableElement.generalization";

    private File inputFile = null;
    private File outputDirectory = null;
    private Model actualModel;

    public XMIParser(String filePath) throws FileNotFoundException {
        inputFile = new File(filePath);
        if (!inputFile.exists()) {
            throw new FileNotFoundException();
        }
    }

    public XMIParser(File selectedFile) {
        inputFile = selectedFile;
    }

    public void parseStuff() throws Exception {

        String content = readFile();
        content = content.replace("\t", "");
        Document document = loadXMLFromString(content);
        actualModel = new Model();
        processNode(document.getDocumentElement(), true);
        processNode(document.getDocumentElement(), false);
        System.out.println(actualModel);
        actualModel.saveModel();
    }


    private void processNode(Node node, boolean isLookingForGeneralization) {
        if ("Class".equals(node.getLocalName())) {
            if(!isLookingForGeneralization){
                boolean isAspect = checkIfNodeIsAspectClass(node);
                if (isAspect) {
                    actualModel.addClass(processNodeAsAspectClass(node));
                    return;
                }
            }
        } else if ("Generalization".equals(node.getLocalName())) {
            if(isLookingForGeneralization){
                searchForGeneralizationPairs(node);
            }
        }
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i).getLocalName() != null) {
                processNode(node.getChildNodes().item(i), isLookingForGeneralization);
            }
        }
    }

    List<GeneralizationPair> pairs = null;

    private void searchForGeneralizationPairs(Node node) {
        pairs = new ArrayList<>();
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if ("ModelElement.taggedValue".equals(node.getChildNodes().item(i).getLocalName())) {
                String source = null;
                String target = null;
                NodeList values = node.getChildNodes().item(i).getChildNodes();
                for (int j = 0; j < values.getLength(); j++) {
                    if ("ea_sourceName".equals(getAtributeByName(values.item(j), "tag"))) {
                        source = getAtributeByName(values.item(j), "value");
                    } else if ("ea_targetName".equals(getAtributeByName(values.item(j), "tag"))) {
                        target = getAtributeByName(values.item(j), "value");
                    }

                    if (source != null && target != null) {
                        break;
                    }
                }
                if (source != null && target != null) {
                    GeneralizationPair pair = new GeneralizationPair(source, target);
                    pairs.add(pair);
                }
            }
        }
    }

    private AspectClass processNodeAsAspectClass(Node node) {
        AspectClass tmpClass = new AspectClass();
        tmpClass.setVisibility(getAtributeByName(node, "visibility"));
        String abstractString = getAtributeByName(node, "isAbstract");
        if (abstractString != null) {
            tmpClass.setAbstract(Boolean.parseBoolean(abstractString));
        }
//        String className = getAtributeByName(node, "name");
        tmpClass.setName(getAtributeByName(node, "name"));
        tmpClass.setParrent(findClassesParent(tmpClass.getName()));
        tmpClass.setMethods(findClassesMethods(node));
        return tmpClass;
//        StringBuilder generatedCode = new StringBuilder();
//        generatedCode.append(visibilty).append(" ");
//        if (isAbstract) {
//            generatedCode.append("abstract ");
//        }
//        generatedCode.append("aspect").append(" ");
//        generatedCode.append(className).append(findInGeneralized(className)).append(" {\n");
//        generatedCode.append(processMethodsFromAspectClass(node));
//        generatedCode.append("}");
//        System.out.println(generatedCode.toString());

//        saveClassFile(className, generatedCode.toString());

    }

    private List<Method> findClassesMethods(Node classNode) {
        NodeList classesChildren = classNode.getChildNodes();
        for (int i = 0; i < classesChildren.getLength(); i++) {
            if ("Classifier.feature".equals(classesChildren.item(i).getLocalName())) {
                return createMethods(classesChildren.item(i));
            }
        }
        return null;
    }

    private List<Method> createMethods(Node methodsNode) {
        NodeList methods = methodsNode.getChildNodes();
        List<Method> generatedMethodList = new ArrayList<>();
        for (int i = 0; i < methods.getLength(); i++) {
            if ("Operation".equals(methods.item(i).getLocalName())) {
                Method tmpMethod = generateMethod(methods.item(i));
                if (tmpMethod != null) {
                    generatedMethodList.add(tmpMethod);
                }
            }
        }

        generatedMethodList.sort(Comparator.comparingInt(Method::getType));
        return generatedMethodList;
    }

    private String findClassesParent(String className) {
        if (pairs != null) {
            for (GeneralizationPair pair : pairs) {
                if (pair.getSource().equals(className)) {
                    return pair.getTarget();
                }
            }
        }
        return null;
    }


    private String findInGeneralized(String sourceClassName) {
        if (pairs != null) {
            for (GeneralizationPair pair : pairs) {
                if (pair.getSource().equals(sourceClassName)) {
                    return " extends " + pair.getTarget();
                }
            }
        }
        return "";
    }

    private String processMethodsFromAspectClass(Node classNode) {
        NodeList classesChildren = classNode.getChildNodes();
        for (int i = 0; i < classesChildren.getLength(); i++) {
            if ("Classifier.feature".equals(classesChildren.item(i).getLocalName())) {
                return createMethodsAsString(classesChildren.item(i));
            }
        }
        return "";
    }

    private String createMethodsAsString(Node methodsNode) {
        NodeList methods = methodsNode.getChildNodes();
        List<Method> generatedMethodList = new ArrayList<>();
        for (int i = 0; i < methods.getLength(); i++) {
            if ("Operation".equals(methods.item(i).getLocalName())) {
                generatedMethodList.add(generateMethod(methods.item(i)));
            }
        }

        generatedMethodList.sort(Comparator.comparingInt(Method::getType));
        StringBuilder builder = new StringBuilder();
        for (Method method : generatedMethodList) {
            builder.append(method.toString()).append("\n");
        }

        return builder.toString();
    }

    private Method generateMethod(Node item) {
        Method resultMethod = null;
        if (methodIsPointcut(item)) {
            resultMethod = new Method(0);
            resultMethod.setVisibility(getAtributeByName(item, "visibility"));
            resultMethod.setName(getAtributeByName(item, "name"));
            resultMethod.setParameters(createMethodParameters(item));
            resultMethod.setReturnType(createMethodReturn(item));
            resultMethod.setAbstract(getMethodAttributeByName(item, "isAbstract"));
            resultMethod.setStatic(getMethodAttributeByName(item, "static"));
        } else if (methodIsAdvice(item)) {
            resultMethod = new Method(1);
            resultMethod.setName(getAtributeByName(item, "name"));
            resultMethod.setReturnType(createMethodReturn(item));
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
        }
        return resultMethod;
    }

    private boolean getMethodAttributeByName(Node method, String attributeName) {
        if (attributeName == null) {
            return false;
        }
        NodeList methodsChildren = method.getChildNodes();
        for (int i = 0; i < methodsChildren.getLength(); i++) {
            if ("ModelElement.taggedValue".equals(methodsChildren.item(i).getLocalName())) {
                NodeList values = methodsChildren.item(i).getChildNodes();
                for (int j = 0; j < values.getLength(); j++) {
                    if (values.item(j).getAttributes() != null) {
                        if (attributeName.equals(getAtributeByName(values.item(j), "tag"))) {
                            return Integer.parseInt(getAtributeByName(values.item(j), "value")) == 1;
                        }
                    }
                }
            }
        }
        return false;
    }

    private String createMethodReturn(Node node) {
        NodeList classesChildren = node.getChildNodes();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < classesChildren.getLength(); i++) {
            if ("BehavioralFeature.parameter".equals(classesChildren.item(i).getLocalName())) {
                NodeList paramLists = classesChildren.item(i).getChildNodes();
                for (int j = 0; j < paramLists.getLength(); j++) {
                    if ("Parameter".equals(paramLists.item(j).getLocalName())) {
                        if ("return".equals(getAtributeByName(paramLists.item(j), "kind"))) {
                            return getParamType(paramLists.item(j));
                        }
                    }
                }
            }
        }
        String resultString = builder.toString();
        if (resultString.length() > 0) {
            return resultString.substring(0, resultString.length() - 2);
        } else {
            return null;
        }
    }

    private boolean methodIsAdvice(Node item) {
        Node stereotypesNode = findSterotypes(item.getChildNodes());
        if (stereotypesNode == null) {
            return false;
        } else {
            for (int i = 0; i < stereotypesNode.getChildNodes().getLength(); i++) {
                String stereotypeName = getAtributeByName(stereotypesNode.getChildNodes().item(i), "name");
                if ("advice".equals(stereotypeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Parameter> createMethodParameters(Node node) {
        NodeList classesChildren = node.getChildNodes();
        List<Parameter> parameters = new ArrayList<>();
        for (int i = 0; i < classesChildren.getLength(); i++) {
            if ("BehavioralFeature.parameter".equals(classesChildren.item(i).getLocalName())) {
                NodeList paramLists = classesChildren.item(i).getChildNodes();
                for (int j = 0; j < paramLists.getLength(); j++) {
                    if ("Parameter".equals(paramLists.item(j).getLocalName())) {
                        if ("in".equals(getAtributeByName(paramLists.item(j), "kind"))) {
                            String paramName = getAtributeByName(paramLists.item(j), "name");
                            String paramType = getParamType(paramLists.item(j));
                            parameters.add(new Parameter(paramName, paramType));
//                            builder.append(paramType).append(" ").append(paramName).append(", ");
                        }
                    }
                }
            }
        }
        return parameters;
    }

    private String createMethodParametersAsString(Node node) {
        NodeList classesChildren = node.getChildNodes();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < classesChildren.getLength(); i++) {
            if ("BehavioralFeature.parameter".equals(classesChildren.item(i).getLocalName())) {
                NodeList paramLists = classesChildren.item(i).getChildNodes();
                for (int j = 0; j < paramLists.getLength(); j++) {
                    if ("Parameter".equals(paramLists.item(j).getLocalName())) {
                        if ("in".equals(getAtributeByName(paramLists.item(j), "kind"))) {
                            String paramName = getAtributeByName(paramLists.item(j), "name");
                            String paramType = getParamType(paramLists.item(j));
                            builder.append(paramType).append(" ").append(paramName).append(", ");
                        }
                    }
                }
            }
        }
        String resultString = builder.toString();
        if (resultString.length() > 0) {
            resultString = resultString.substring(0, resultString.length() - 2);
        }
        return resultString;
    }

    private String getParamType(Node param) {
        for (int i = 0; i < param.getChildNodes().getLength(); i++) {
            if ("ModelElement.taggedValue".equals(param.getChildNodes().item(i).getLocalName())) {
                NodeList toggleValuesList = param.getChildNodes().item(i).getChildNodes();
                for (int j = 0; j < toggleValuesList.getLength(); j++) {
                    if ("type".equals(getAtributeByName(toggleValuesList.item(j), "tag"))) {
                        return getAtributeByName(toggleValuesList.item(j), "value");
                    }
                }
                break;
            }
        }
        return null;
    }

    private void printChildrenNodes(Node item) {
        NodeList childrenNodes = item.getChildNodes();
        for (int i = 0; i < childrenNodes.getLength(); i++) {
            if (childrenNodes.item(i).getLocalName() != null) {
                System.out.println(childrenNodes.item(i).getLocalName());
            }
        }
    }

    private boolean methodIsPointcut(Node item) {
        Node stereotypesNode = findSterotypes(item.getChildNodes());
        if (stereotypesNode == null) {
            return false;
        } else {
            for (int i = 0; i < stereotypesNode.getChildNodes().getLength(); i++) {
                String stereotypeName = getAtributeByName(stereotypesNode.getChildNodes().item(i), "name");
                if ("pointcut".equals(stereotypeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIfNodeIsAspectClass(Node documentElement) {
        if (documentElement.getChildNodes().getLength() == 0) {
            return false;
        }
        Node stereotypesNode = findSterotypes(documentElement.getChildNodes());
        if (stereotypesNode == null) {
            return false;
        } else {
            for (int i = 0; i < stereotypesNode.getChildNodes().getLength(); i++) {
                String stereotypeName = getAtributeByName(stereotypesNode.getChildNodes().item(i), "name");
                if ("aspect".equals(stereotypeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getAtributeByName(Node item, String name) {
        if (item != null) {
            if (item.getAttributes() != null) {
                Node node = item.getAttributes().getNamedItem(name);
                if (node != null) {
//                    System.out.println(node + ", ");
                    String nodeToString = node.toString();
                    String splitted[] = nodeToString.split("=");
                    if (splitted.length == 2) {
                        return splitted[1].replace("\"", "");
                    }
                }
            }
        }
        return null;
    }

    private Node findSterotypes(NodeList childNodes) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            if ("ModelElement.stereotype".equals(childNodes.item(i).getLocalName())) {
                return childNodes.item(i);
            }
        }
        return null;
    }

    private void printAtributes(Node item) {
        if (item != null) {
            if (item.getAttributes() != null) {
                for (int i = 0; i < item.getAttributes().getLength(); i++) {
                    Node node = item.getAttributes().item(i);
                    if (node != null) {
                        System.out.println(node + ", ");
                    }
                }
            }
        }
    }


    private String readFile() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    private Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

}
