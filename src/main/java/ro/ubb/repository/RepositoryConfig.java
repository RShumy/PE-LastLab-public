package ro.ubb.repository;

import ro.ubb.domain.BaseEntity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RepositoryConfig {

    public static Repository<?,? extends BaseEntity> createRepository(String fileName){
        try{
            String fileRepository = readProperties().getProperty("fileRepository");
            Class <Repository<?,? extends BaseEntity>> repositoryClassToCreate =
                    (Class <Repository<?,? extends BaseEntity>>) Class.forName(fileRepository);
            return repositoryClassToCreate.getDeclaredConstructor(String.class).newInstance(fileName);
        } catch (Exception e)        {
            e.printStackTrace();
        }
        return null;
    }

    public static Properties readProperties() {
        try (InputStream input = new FileInputStream("repository-config.properties")){
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
