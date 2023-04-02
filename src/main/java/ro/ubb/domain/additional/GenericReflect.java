package ro.ubb.domain.additional;

import ro.ubb.domain.BaseEntity;
import ro.ubb.domain.EventType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericReflect {

    public static Object parseTypeHelper(Class<?> type, String valueFromFile){
        // Pentru primitive
        if (type == char.class){ return valueFromFile.charAt(0); }

        if (type == boolean.class){ return Boolean.parseBoolean(valueFromFile); }

        if (type == short.class){ return Short.parseShort(valueFromFile); }
        if (type == byte.class){ return Byte.parseByte(valueFromFile); }
        if (type == long.class){ return Long.parseLong(valueFromFile); }
        if (type == int.class){ return Integer.parseInt(valueFromFile); }
        if (type == float.class){ return Float.parseFloat(valueFromFile); }
        if (type == double.class){ return Double.parseDouble(valueFromFile); }

        // Pentru Clase wrapper
        if (type == Character.class){ return valueFromFile.charAt(0); }

        if (type == Boolean.class){ return Boolean.parseBoolean(valueFromFile); }

        if (type == Short.class){ return Short.parseShort(valueFromFile); }
        if (type == Byte.class){ return Byte.parseByte(valueFromFile); }
        if (type == Long.class){ return Long.parseLong(valueFromFile); }
        if (type == Integer.class){ return Integer.parseInt(valueFromFile); }
        if (type == Float.class){ return Float.parseFloat(valueFromFile); }
        if (type == Double.class){ return Double.parseDouble(valueFromFile); }
        //Trebuie sa fim atenti aici
        if (type.getName().contains("EventType") && type.isEnum()){ return EventType.valueOf(valueFromFile); }

        if (type == LocalDate.class){ return DateTimeValidatorAndParser.parseDate(valueFromFile);}
        if (type == LocalTime.class){ return DateTimeValidatorAndParser.parseTime(valueFromFile);}
        return valueFromFile;
    }

    public static List<Field> getEntityFieldsList(Class<? extends BaseEntity> entityClass){
        List<Field> fieldsList = List.of(entityClass.getDeclaredFields());
        return fieldsList;
    }

    public static Class getEntityClassFromString(String fileLine){
        Pattern classPattern = Pattern.compile("(^[A-Z][a-z]{1,}\\{)");
        Matcher classMatcher = classPattern.matcher(fileLine);
        classMatcher.find();
        String theClass = classMatcher.group().replace("{", "");
        try {
            Class classEntity = Class.forName("ro.ubb.domain." + theClass);
            return classEntity;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getEntityInstanceFromString(String fileLine){
        try {
            Object entityToSave = getEntityClassFromString(fileLine).newInstance();
            return entityToSave;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class getIdClassFromString(String fileLine){
        Pattern idTypePattern = Pattern.compile("(```.{1,}```)");
        Matcher idTypeMatch = idTypePattern.matcher(fileLine);
        idTypeMatch.find();
        String idTypeString = idTypeMatch.group().replace("```","");
        try {
            Class idClass = Class.forName(idTypeString);
            return idClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getFieldNamesAndValuesMapFromString(String fileLine){
        Map<String, String> matchedFieldNames = new LinkedHashMap<>();
        Pattern fieldNamePattern = Pattern.compile("(\\s[A-Za-z]{1,}=`)");
        Matcher fieldMatcher = fieldNamePattern.matcher(fileLine);

        Pattern valuePattern = Pattern.compile("(`[^`]{1,}`)");
        Matcher valueMatcher = valuePattern.matcher(fileLine);
        String fieldName;
        String value;

        /// Placing field and values in a Map
        while (fieldMatcher.find() && valueMatcher.find()) {
            fieldName = fieldMatcher.group().trim().replace("=`", "");
            value = valueMatcher.group().replace("`", "");
            matchedFieldNames.putIfAbsent(fieldName, value);
        }
        return matchedFieldNames;
    }

    public static Type getIDType(Object id){
        List<Field> fields = List.of(id.getClass().getDeclaredFields());
        return fields.get(0).getType();
    }

    public void createStringFromEntity(Object entity){
        System.out.println(entity.getClass().getSimpleName());
    }

    public static String getStringFieldValueForSQL(Object entity,Field field){
        Type fieldType = field.getType();
        field.setAccessible(true);
        try {
            if (fieldType == String.class ||
                    fieldType == char.class ||
                    fieldType == Character.class ||
                    fieldType == LocalDate.class ||
                    fieldType == LocalTime.class ||
                    fieldType == boolean.class ||
                    fieldType == Boolean.class ||
                    fieldType == EventType.class) {
//                if (String.valueOf(field.get(entity)).contains(";"))
//                    String.valueOf(field.get(entity)).replaceAll("[-]+|[-]+\s+$","");
                String stringValue = "'" + field.get(entity) + "'";
                return stringValue;
            }
            else return String.valueOf(field.get(entity));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void setPreparedStatement(Class<?> type, Object entity, int index, PreparedStatement prepstmt, Method method)
            throws InvocationTargetException, IllegalAccessException, SQLException {
        // Pentru primitive
        if (type == char.class){ prepstmt.setString(index,(String) method.invoke(entity)); }

        if (type == boolean.class){ prepstmt.setBoolean(index,(boolean) method.invoke(entity)); }

        if (type == short.class){ prepstmt.setShort(index,(short) method.invoke(entity)); }
        if (type == byte.class){ prepstmt.setByte(index,(byte) method.invoke(entity)); }
        if (type == long.class){ prepstmt.setLong(index,(long) method.invoke(entity)); }
        if (type == int.class){ prepstmt.setInt(index,(int) method.invoke(entity)); }
        if (type == float.class){ prepstmt.setFloat(index,(float) method.invoke(entity)); }
        if (type == double.class){ prepstmt.setDouble(index,(double) method.invoke(entity)); }

        // Pentru Clase wrapper
        if (type == Character.class){ prepstmt.setString(index,(String) method.invoke(entity));  }

        if (type == Boolean.class){ prepstmt.setBoolean(index,(Boolean) method.invoke(entity)); }

        if (type == Short.class){ prepstmt.setShort(index,(Short) method.invoke(entity)); }
        if (type == Byte.class){ prepstmt.setByte(index,(Byte) method.invoke(entity)); }
        if (type == Long.class){ prepstmt.setLong(index,(Long) method.invoke(entity)); }
        if (type == Integer.class){  prepstmt.setInt(index,(Integer) method.invoke(entity)); }
        if (type == Float.class){ prepstmt.setFloat(index,(Float) method.invoke(entity)); }
        if (type == Double.class){  prepstmt.setDouble(index,(Double) method.invoke(entity));}

        //Trebuie sa fim atenti aici
        if (type.getName().contains("EventType") && type.isEnum()){
            EventType eventType = (EventType) method.invoke(entity);
            prepstmt.setString(index, eventType.name()); }

        if (type == LocalDate.class){ prepstmt.setDate(index, Date.valueOf((LocalDate)method.invoke(entity)));}
        if (type == LocalTime.class){ prepstmt.setTime(index, Time.valueOf((LocalTime)method.invoke(entity)));}
        if (type == String.class){ prepstmt.setString(index,(String) method.invoke(entity));}
    }

    public static Object getColumnValueFromResultSet(Class<?> type, ResultSet resultSet, String fieldName) throws SQLException {
        // ("\"" + fieldName + "\"");
//        fieldName = "\"" + fieldName + "\"";
        if (type == char.class){ return resultGetStringAdjusted(resultSet,fieldName); }
        if (type == boolean.class){ return  resultSet.getBoolean(fieldName); }

        if (type == short.class){ return resultSet.getShort(fieldName); }
        if (type == byte.class){ return resultSet.getByte(fieldName); }
        if (type == long.class){ return resultSet.getLong(fieldName); }
        if (type == int.class){ return resultSet.getInt(fieldName); }
        if (type == float.class){ return resultSet.getFloat(fieldName); }
        if (type == double.class){ return resultSet.getDouble(fieldName); }

        // Pentru Clase wrapper
        if (type == Character.class){ return resultGetStringAdjusted(resultSet,fieldName);  }

        if (type == Boolean.class){ return resultSet.getBoolean(fieldName); }

        if (type == Short.class){ return resultSet.getDouble(fieldName); }
        if (type == Byte.class){ return resultSet.getByte(fieldName); }
        if (type == Long.class){ return resultSet.getLong(fieldName); }
        if (type == Integer.class){ return resultSet.getInt(fieldName); }
        if (type == Float.class){ return resultSet.getFloat(fieldName); }
        if (type == Double.class){ return resultSet.getDouble(fieldName);}

        //Trebuie sa fim atenti aici
        if (type.getName().contains("EventType") && type.isEnum()){ return EventType.valueOf(resultGetStringAdjusted(resultSet,fieldName)); }

        if (type == LocalDate.class){ return resultSet.getDate(fieldName).toLocalDate();}
        if (type == LocalTime.class){ return resultSet.getTime(fieldName).toLocalTime();}

        return resultGetStringAdjusted(resultSet,fieldName);
    }

    private static String resultGetStringAdjusted(ResultSet resultSet, String fieldName) throws SQLException {
        return resultSet.getString(fieldName).replaceAll("\s{1,}$","");
    }

    public static Method getGetterMethod(Class entityClass, Field field){
        Method getter;
        try {
            // Se poate face si pentru conventia de getter "is" pentru boolean
            String methodName = field.getName().replaceAll("^[a-z]", "get" + field.getName().toUpperCase().charAt(0));
            getter = entityClass.getMethod(methodName);
            return getter;

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

 }
