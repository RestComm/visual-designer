package org.restcomm.connect.rvd.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.restcomm.connect.rvd.model.StepMarshaler;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

public class OldWorkspaceStorage extends FsWorkspaceStorage {

    StepMarshaler marshaler;

    public OldWorkspaceStorage(String rootPath, StepMarshaler marshaler ) {
        super(rootPath);
        this.marshaler = marshaler;
    }

    public <T> T loadEntity(String entityName, String entityPath, Class<T> entityClass) throws StorageException {
        // convert relative paths to absolute
        if ( ! entityPath.startsWith( "/") )
            entityPath = rootPath + File.separator + entityPath;

        String pathname = entityPath + File.separator + entityName;

        File file = new File(pathname);
        if ( !file.exists() )
            throw new StorageEntityNotFound("File " + file.getPath() + " does not exist");

        String data;
        try {
            data = FileUtils.readFileToString(file, Charset.forName("UTF-8"));
            T instance = marshaler.toModel(data, entityClass);
            return instance;
        } catch (IOException | JsonSyntaxException e) {
            throw new StorageException("Error loading file " + file.getPath(), e);
        }
    }

    public InputStream loadStream(String entityName, String entityPath) throws StorageException {
        // convert relative paths to absolute
        if ( ! entityPath.startsWith( "/") )
            entityPath = rootPath + File.separator + entityPath;

        String pathname = entityPath + File.separator + entityName;

        File file = new File(pathname);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new StorageEntityNotFound("File " + file.getPath() + " does not exist");
        }
    }



    public void storeEntity(Object entity, Class<?> entityClass, String entityName, String entityPath ) throws StorageException {
        // convert relative paths to absolute
        if ( ! entityPath.startsWith( "/") )
            entityPath = rootPath + File.separator + entityPath;
        String pathname = entityPath + File.separator + entityName;

        File file = new File(pathname);
        String data = marshaler.getGson().toJson(entity, entityClass);
        try {
            FileUtils.writeStringToFile(file, data, "UTF-8");
        } catch (IOException e) {
            throw new StorageException("Error creating file in storage: " + file, e);
        }
    }

    public void storeEntity(Object entity, String entityName, String entityPath ) throws StorageException {
        // convert relative paths to absolute
        if ( ! entityPath.startsWith( "/") )
            entityPath = rootPath + File.separator + entityPath;
        String pathname = entityPath + File.separator + entityName;

        File file = new File(pathname);
        String data = marshaler.getGson().toJson(entity);
        try {
            FileUtils.writeStringToFile(file, data, "UTF-8");
        } catch (IOException e) {
            throw new StorageException("Error creating file in storage: " + file, e);
        }
    }


    public void storeFile( Object item, Class<?> itemClass, File file) throws StorageException {
        String data;
        data = marshaler.getGson().toJson(item, itemClass);

        try {
            FileUtils.writeStringToFile(file, data, "UTF-8");
        } catch (IOException e) {
            throw new StorageException("Error creating file in storage: " + file, e);
        }
    }

    public void storeBinaryFile(File sourceFile, String entityName, String entityPath) throws StorageException {
        // convert relative paths to absolute
        if ( ! entityPath.startsWith( "/") )
            entityPath = rootPath + File.separator + entityPath;
        String pathname = entityPath + File.separator + entityName;

        //File destFile = new File(getProjectBasePath(projectName) + File.separator + RvdConfiguration.PACKAGING_DIRECTORY_NAME + File.separator + "app.zip");
        File destFile = new File( pathname );
        try {
            FileUtils.copyFile(sourceFile, destFile);
            FileUtils.deleteQuietly(sourceFile);
        } catch (IOException e) {
            throw new StorageException("Error copying binary file into project", e);
        }
    }

    public InputStream loadBinaryFile(String projectName, String entityName, String entityPath) throws FileNotFoundException {
        // convert relative paths to absolute
        if ( ! entityPath.startsWith( "/") )
            entityPath = rootPath + File.separator + entityPath;
        String pathname = entityPath + File.separator + entityName;

        File packageFile = new File( pathname );
        return new FileInputStream(packageFile);
    }

    // CAUTION! what happens if the typecasting fails? solve this..
    public <T> T loadModelFromXMLFile(File file, Class<T> modelClass) throws StorageException {
        if ( !file.exists() )
            throw new StorageEntityNotFound("Cannot find file: " + file.getPath() );

        try {
            String data = FileUtils.readFileToString(file, "UTF-8");
            T instance = (T) marshaler.getXStream().fromXML(data);
            return instance;

        } catch (IOException e) {
            throw new StorageException("Error loading model from file '" + file + "'", e);
        }
    }

}
