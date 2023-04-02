package ro.ubb.repository;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ro.ubb.domain.BaseEntity;
import ro.ubb.domain.additional.GenericReflect;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


public class XMLRepository<ID,T extends BaseEntity<ID>> extends InMemoryRepository<ID, T> {

    private String fileName;
    public XMLRepository(String fileName){
       fileName = "./src/Data/XML/" + fileName;
       this.fileName = fileName;
        try {
            loadDataFromXML();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Path path() {
        Path path = Paths.get(fileName);
        System.out.println(path.toAbsolutePath());
        return path;
    }

    private Path path(String stringPath) {
        Path path = Paths.get(stringPath);
        System.out.println(path.toAbsolutePath());
        return path;
    }


    private void loadDataFromXML() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(fileName);
        Element rootElement = document.getDocumentElement();
        NodeList entityNodes = rootElement.getChildNodes();
        for (int i = 0; i < entityNodes.getLength(); i++) {
            Node entityNode = entityNodes.item(i);
            if (!(entityNode instanceof Element)) {
                continue;
            }
            Element entityElement = (Element) entityNode;
            T entity = buildEntityFromElement(entityElement);
            super.save(entity);
        }
    }

    private T buildEntityFromElement(Element entityElement) throws Exception {
        //daca nu merge, facem cu citire by tagname, si o Clasa cu .forname()
        Class idClass = Class.forName(entityElement.getAttribute("idClass"));
        T entity =getEntityInstanceFromElement(entityElement);

        Object id = GenericReflect.parseTypeHelper(idClass,entityElement.getAttribute("idEntity"));
        entity.setIdEntity((ID)id);

        getEntityFieldsList((T)entity).forEach(field -> {
            Node fieldNode = entityElement.getElementsByTagName(field.getName()).item(0);
            Object fieldValue = GenericReflect.parseTypeHelper(field.getClass(),fieldNode.getTextContent());
            field.setAccessible(true);
            try {
                field.set(entity, fieldValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return entity;
    }

    @Override
    public Iterable<T> findAll() { return super.findAll(); }

    @Override
    public Optional<T> findOne(ID id) {
        return super.findOne(id);
    }

    @Override
    public Optional<T> save(T entity) {
        Optional<T> optional = super.save(entity);
        if (optional.isPresent()) {

            try { saveToXML(entity); }
            catch (Exception e){ e.printStackTrace(); }

            return optional;
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> update(T entity) {
        Optional<T> optional = super.update(entity);
        if (optional.isPresent()) {

            try { updateInXML(entity); }
            catch (Exception e){ e.printStackTrace();}

            return optional;
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> delete(ID id) throws FileNotFoundException {
        Optional<T> optional = super.delete(id);
        try {
            deleteFromXML(id);
        } catch (Exception e) { e.printStackTrace(); }

        return Optional.empty();
    }

    private Document getXMLDocument() throws Exception{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        // Entity document
        Document document = documentBuilder.parse(fileName);

        return document;
    }

    private void writeXMLDoc (Document doc) throws Exception{
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(new StreamSource(new File("./src/transformer.xslt")));
        transformer.transform(
                new DOMSource(doc),
                new StreamResult(new File(fileName)));
    }

    private void saveToXML(T entity) throws Exception{
        Document document = getXMLDocument();

        addEntityToDOM(entity, document);

        writeXMLDoc(document);
    }

    private void addEntityToDOM(T entity, Document document) {
        Element rootElement = document.getDocumentElement();
        Node entityNode = createNodeFromEntity(entity, document);
        rootElement.appendChild(entityNode);
    }

    private Node createNodeFromEntity(T entity, Document document) {
        // Create Entity node with its respective children
        // TODO: Use Reflection to get Class Name and set it as tag for the element

        Element entityElement = document.createElement(getStringEntityClass(entity));

        // TODO: Use Reflection to set ID class and value as attributes
        entityElement.setAttribute("idClass", entity.getIdEntity().getClass().getName());
        entityElement.setAttribute("idEntity", String.valueOf(entity.getIdEntity()) );

        // append entity Elements with their respective values
        getEntityFieldsList(entity).forEach(field -> {
            Element fieldElement = document.createElement(field.getName());
            field.setAccessible(true);
            try {
            fieldElement.setTextContent(String.valueOf(field.get(entity)));
            } catch (IllegalAccessException e){
                e.printStackTrace();
            }
            entityElement.appendChild(fieldElement);
        });

        return entityElement;
    }

    private String getStringEntityClass(T entity){
        String entityClass = entity.getClass().getName().replace("ro.ubb.domain.","");
        return entityClass;
    }

    private List<Field> getEntityFieldsList(T entity){
        List<Field> fieldsList = List.of(entity.getClass().getDeclaredFields());
//        Field[] superClassFields = entity.getClass().getSuperclass().getFields();
//        fieldsList.addAll(List.of(superClassFields));
        return fieldsList;
    }

    private T getEntityInstanceFromElement(Element element){
        try {
            T entity= (T) Class.forName("ro.ubb.domain." + element.getTagName()).newInstance();
            return entity;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateInXML(T entity) throws Exception{
        Document document = getXMLDocument();

        updateEntityInDOM(entity, document);

        writeXMLDoc(document);
    }

    private void updateEntityInDOM(T entity, Document document) {
        Element rootElement = document.getDocumentElement();
        Node entityUpdatedNode = createNodeFromEntity(entity, document);
        Node entityOldNode = getOldNodeFromId(entity.getIdEntity(),document);
        rootElement.replaceChild(entityUpdatedNode, entityOldNode);
    }

    private Node getOldNodeFromId(ID id, Document document) {
        Element rootElement = document.getDocumentElement();
        Node checkedNode = rootElement.getFirstChild();
        while (checkedNode != null) {
            if (checkedNode instanceof Element)
                 if (checkedNode.getAttributes().getNamedItem("idEntity").getNodeValue()  .equals  (String.valueOf(id)))
                    return checkedNode;
            checkedNode = checkedNode.getNextSibling();
        }

        //Also for() method  Update
//        NodeList entityNodes = rootElement.getChildNodes();
//        for (int i = 0; i < entityNodes.getLength(); i++) {
//            Node entityNode = entityNodes.item(i);
//            if (!(entityNode instanceof Element)) {
//                continue;
//            }
//            Element entityElement = (Element) entityNode;
//            Integer idCurrent = Integer.parseInt(entityElement.getAttribute("idEntity"));
//            if( idCurrent == id) {
//                return entityNodes.item(i);
//            }
//        }
        return null;
    }

    private void deleteFromXML(ID id) throws Exception{
        Document document = getXMLDocument();

        deleteEntityFromDOM(id, document);

        writeXMLDoc(document);
    }

    private void deleteEntityFromDOM(ID id, Document document) {
        Element rootElement = document.getDocumentElement();
        NodeList entityNodes = rootElement.getChildNodes();
        for (int i = 0; i < entityNodes.getLength(); i++) {
            Node entityNode = entityNodes.item(i);
            if (!(entityNode instanceof Element)) {
                continue;
            }
            Element entityElement = (Element) entityNode;
            Integer idCurrent = Integer.parseInt(entityElement.getAttribute("idEntity"));
            if( idCurrent == id) {
                entityElement.getParentNode().removeChild(entityNodes.item(i));
                break;
            }
        }
    }
}
