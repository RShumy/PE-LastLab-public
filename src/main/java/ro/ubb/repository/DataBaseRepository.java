package ro.ubb.repository;

import ro.ubb.domain.BaseEntity;
import ro.ubb.domain.additional.GenericReflect;
import ro.ubb.domain.additional.Tuple;
import ro.ubb.domain.validators.Validator;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class DataBaseRepository<ID,T extends BaseEntity<ID>> implements Repository<ID,T>{
    private final String URL;
    private final String USER;
    private final String PASSWORD;
    private final Class<T> entityClass;
    private Validator<T> validator;

    public DataBaseRepository(Class<T> tClass, String URL, String USER, String PASSWORD){
        this.URL = URL;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        entityClass = tClass;
        try {
            this.validator = (Validator<T>) Class.forName("ro.ubb.domain.validators." + entityClass.getSimpleName() + "Validator").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<T> save(T entity) {
        List<Field> fieldList = GenericReflect.getEntityFieldsList(entity.getClass());
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        validator.validate(entity);

        // Am adaugat ghilimele pentru ca PostGres vedea user si event ca si tabele interioare de sistem
        String entityString = "\"" + entity.getClass().getSimpleName().toLowerCase(Locale.ROOT) + "\"";

        var sql = "insert into " + entityString + "("+stringFieldList(entity).getFirst()+") values (" + stringFieldList(entity).getSecond() + ")";
        try (var connection = DriverManager.getConnection(URL, USER, PASSWORD);
             var preparedStatement = connection.prepareStatement(sql);) {
            for (int i=0; i<fieldList.size(); i++) {
                    Field field = fieldList.get(i);

                    // Adaugat un if sa evitam index outofbounds la preparedStatement
                    if (i+1<=fieldList.size())
                    GenericReflect.setPreparedStatement(field.getType(),(T)entity,i+1,preparedStatement,GenericReflect.getGetterMethod(entityClass,field));
                }

            // Adaugat execute update
            preparedStatement.executeUpdate();
            return Optional.empty();
            } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }

     private Tuple<String,String> stringFieldList(T entity) {

        AtomicReference<String> fields = new AtomicReference<>("");
        AtomicReference<String> placeHolders = new AtomicReference<>("");
//        AtomicReference<String> values = new AtomicReference<>("");
        GenericReflect.getEntityFieldsList(entity.getClass()).forEach(field -> {
                field.setAccessible(true);
                fields.set(fields + "\"" + field.getName() + "\"" + ",");
//                values.set(values + GenericReflect.getStringFieldValueForSQL(entity,field) + ",");
                placeHolders.set(placeHolders + "?,");

        });
        return new Tuple<>(
                fields.get().replaceAll(",$",""),
                placeHolders.get().replaceAll(",$",""));
    }

    @Override
    public Optional<T> findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        try {
            var entityString = "\"" + entityClass.getSimpleName().toLowerCase(Locale.ROOT) + "\"";
            var sql = "select * from " + entityString + " where " + "\"" + "idEntity" + "\"" + "= " + id;
            T finalEntity = entityClass.getConstructor().newInstance();

            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 var preparedStatement = connection.prepareStatement(sql);
                 var resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {

                    finalEntity.setIdEntity((ID) GenericReflect.getColumnValueFromResultSet(
                            id.getClass(), resultSet, "idEntity")
                    );

                    GenericReflect.getEntityFieldsList(finalEntity.getClass()).forEach(field -> {
                        field.setAccessible(true);
                        try {
                            field.set(finalEntity, GenericReflect.getColumnValueFromResultSet(field.getType(), resultSet, field.getName()));
                        } catch (IllegalAccessException | SQLException e) {
                            e.printStackTrace();
                        }
                    });

                    return Optional.ofNullable(finalEntity);
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Iterable<T> findAll() {
        List<T> entityList = new ArrayList<>();
        try {
            ID id = (ID) ((Number) 0);
            var entityString = "\"" + entityClass.getSimpleName().toLowerCase(Locale.ROOT) + "\"";
            var sql = "select * from " + entityString;
            try (var connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 var preparedStatement = connection.prepareStatement(sql);
                 var resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {

                    T finalEntity = entityClass.getConstructor().newInstance();

                    finalEntity.setIdEntity((ID)GenericReflect.getColumnValueFromResultSet(
                            id.getClass(),resultSet,"idEntity")
                    );

                    GenericReflect.getEntityFieldsList(finalEntity.getClass()).forEach(field -> {
                        field.setAccessible(true);
                        try {
                            field.set(finalEntity, GenericReflect.getColumnValueFromResultSet(field.getType(), resultSet, field.getName()));
                        } catch (IllegalAccessException | SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    entityList.add(finalEntity);
                }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return entityList;
    }

    @Override
    public Optional<T> update(T entity) throws RepositoryException {
        List<Field> fieldList = GenericReflect.getEntityFieldsList(entity.getClass());
        if (entity == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        validator.validate(entity);

        if(findOne(entity.getIdEntity()).isEmpty()) {
            return Optional.ofNullable(entity);
        }

        String entityString = "\"" + entity.getClass().getSimpleName().toLowerCase(Locale.ROOT) + "\"";

        var sql = "update " + entityString + " set " + getStatementForUpdate(entity) + "where "+ "\"" + "idEntity" + "\"=" + entity.getIdEntity();

        try (var connection = DriverManager.getConnection(URL, USER, PASSWORD);
             var preparedStatement = connection.prepareStatement(sql);) {
            for (int i=0; i<fieldList.size(); i++) {
                Field field = fieldList.get(i);

                // Adaugat un if sa evitam index outofbounds la preparedStatement
                if (i+1<=fieldList.size())
                    GenericReflect.setPreparedStatement(field.getType(),entity,i+1,preparedStatement,GenericReflect.getGetterMethod(entityClass,field));
            }

            // Adaugat execute update
            preparedStatement.executeUpdate();
            return Optional.empty();
        } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }

    public String getStatementForUpdate(T entity){
        AtomicReference<String> statement = new AtomicReference<>("");
        GenericReflect.getEntityFieldsList(entity.getClass()).forEach(field -> {
            statement.set(statement + "\"" + field.getName()  + "\"" + " =?");
//            statement.set(statement + "\"" + field.getName()  + "\"" + "=" + GenericReflect.getStringFieldValueForSQL(entity,field) + ",");
        });
        return statement.get().replaceAll(",$","");
    }

    @Override
    public Optional<T> delete(ID id) throws RepositoryException, FileNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        T entity = findOne(id).get();
        var entityString = "\"" + entityClass.getSimpleName().toLowerCase(Locale.ROOT) + "\"";
        var sql = "delete from " + entityString + " where " + "\"" + "idEntity" + "\"" + "=" + id;
        try(var connection = DriverManager.getConnection(URL, USER, PASSWORD);
            var preparedStatement = connection.prepareStatement(sql);){
            preparedStatement.executeUpdate();
            return Optional.ofNullable(entity);

        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

//    public Class<?> getGenericType(Object obj) {
//        Class<?> genericType = null;
//        Type gnrcType = obj.getClass().getGenericSuperclass();
//        if (gnrcType instanceof ParameterizedType) {
//            ParameterizedType parameterizedType = (ParameterizedType) gnrcType;
//            Type types[] = parameterizedType.getActualTypeArguments();
//
//            if (types != null && types.length > 0) {
//                Type type = types[0];
//                if (type instanceof Class) {
//                    genericType = (Class<?>) type;
//                }
//            }
//        }
//        return genericType;
//    }
}
