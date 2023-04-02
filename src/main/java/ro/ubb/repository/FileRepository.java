package ro.ubb.repository;

import ro.ubb.domain.BaseEntity;
import ro.ubb.domain.EventType;
import ro.ubb.domain.additional.DateTimeValidatorAndParser;
import ro.ubb.domain.additional.GenericReflect;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileRepository<ID , T extends BaseEntity<ID>> extends InMemoryRepository<ID, T> {

    private String fileName;
    public static Set<Class> classSet;
    private Object ID;


    public FileRepository(String fileName){
        fileName = "./src/Data/" + fileName;
        this.fileName = fileName;
        loadData();
    }

    private Path path() {
        Path path = Paths.get(fileName);
        System.out.println(path.toAbsolutePath());
        return path;
    }



    public void loadData() {
        try {

            Files.lines(path()).forEach(line -> {
                Map<String, String> matchedFieldNames = new LinkedHashMap<>();
                Object id = "";
                Object entity = "";

                Pattern idTypePattern = Pattern.compile("(```.{1,}```)");
                Matcher idTypeMatch = idTypePattern.matcher(line);
                idTypeMatch.find();
                String idTypeString = idTypeMatch.group().replace("```","");


                Pattern fieldNamePattern = Pattern.compile("(\\s[A-Za-z]{1,}=`)");
                Matcher fieldMatcher = fieldNamePattern.matcher(line);

                Pattern valuePattern = Pattern.compile("(`[^`]{1,}`)");
                Matcher valueMatcher = valuePattern.matcher(line);
                String fieldName;
                String value;

                /// Placing field and values in an array
                while (fieldMatcher.find() && valueMatcher.find()) {
                    fieldName = fieldMatcher.group().trim().replace("=`", "");
                    value = valueMatcher.group().replace("`", "");
                    matchedFieldNames.putIfAbsent(fieldName, value);
                }
                System.out.println(matchedFieldNames);

                // creating new entity for InMemoryRepository
                Pattern classPattern = Pattern.compile("(^[A-Z][a-z]{1,}\\{)");
                Matcher classMatcher = classPattern.matcher(line);
                classMatcher.find();
                String theClass = classMatcher.group().replace("{", "");
                System.out.println(theClass);


                Class classEntity = null;
                try {
                    classEntity = Class.forName("ro.ubb.domain." + theClass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    Object entityToSave = classEntity.newInstance();

                    List<Field> fieldsList = new ArrayList<>();
                    Field[] superClassFields = classEntity.getSuperclass().getFields();
                    Field[] fields = classEntity.getDeclaredFields();
//                    fieldsList.addAll(List.of(superClassFields));
                    fieldsList.addAll(List.of(fields));

                    try {
                        Class idClass = Class.forName(idTypeString);
                        //seting the idObject with its Type
                        id = GenericReflect.parseTypeHelper(idClass,
                                matchedFieldNames.get(superClassFields[0].getName()));
                        superClassFields[0].setAccessible(true);
                        superClassFields[0].set( entityToSave, id);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    fieldsList.forEach(field -> {
                        field.setAccessible(true);
                        if (matchedFieldNames.containsKey(field.getName())) {
                            try {
                                field.set(entityToSave,
                                        GenericReflect.parseTypeHelper(field.getType(),
                                                matchedFieldNames.get(field.getName())));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    entity = (T) entityToSave;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                super.save((T)entity);
            });
        }catch (IOException e){
                e.printStackTrace();}
    }



    @Override
    public Optional<T> save(T entity) {
        Optional<T> optional = super.save(entity);
        if (optional.isPresent()) {
            saveToFile(entity);
            return optional;
        }
        return Optional.empty();
    }



    public void saveToFile(T entity) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path(), StandardOpenOption.APPEND)){
            bufferedWriter.write(entity.toString() + "```"+entity.idEntity.getClass().getName()+"```"
            );
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public Optional<T> findOne(ID id) {
        return super.findOne(id);
    }

    @Override
    public Iterable<T> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<T> update(T entity) throws RepositoryException {
        return Optional.empty();
    }

    @Override
    public Optional<T> delete(ID id) throws RepositoryException, FileNotFoundException {
        return Optional.empty();
    }






//    private void readFromFile(Path path) throws IOException{
//
//        Files.lines(path).forEach(
//                line -> {
//
//            Map<String, String> matchedFieldNames = new LinkedHashMap<>();
//            Pattern classPattern = Pattern.compile("^[A-Z][a-z]{1,}\\{");
//            Matcher classMatcher = classPattern.matcher(line);
//            String theClass = classMatcher.group().replace("{", "");
//
//            try {
//                Class classEntity = Class.forName("ro.ubb.domain." + theClass);
//                for (Field field : classEntity.getDeclaredFields()) {
//                    String value;
//                    Pattern fieldNamePattern = Pattern.compile("(?:\\s[A-Za-z]{1,}=`)");
//                    Matcher fieldMatcher = fieldNamePattern.matcher(line);
//
//                    Pattern valuePattern = Pattern.compile("(?:`.{1,}`)");
//                    Matcher valueMather = valuePattern.matcher(line);
////                    List<String> matchedFieldNames = new ArrayList<>();
//                    while (fieldMatcher.find()) {
//                        //index reference is for the .split() method that returns an Array of String[]
//                        String fieldName = fieldMatcher.group().trim().split("=`")[0];
//                        matchedFieldNames.putIfAbsent(fieldName,valueMather.group().replace("`",""));
//                        System.out.println(matchedFieldNames.get(fieldName));
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        });
//    }



//    public static void loadPackageResources () {
//        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//        InputStream line = classLoader.getResourceAsStream("ro/ubb/domain");
//        BufferedReader readLines = new BufferedReader(new InputStreamReader(line));
//        readLines.lines().forEach(
//                lineRow ->{
//                    try {
//                        Class classToInsertInSet = Class.forName("ro.ubb.entity"+lineRow.replace("class","."));
//                        classSet.add(classToInsertInSet);
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                });
//    }

}
