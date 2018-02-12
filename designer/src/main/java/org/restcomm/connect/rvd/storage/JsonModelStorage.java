package org.restcomm.connect.rvd.storage;

import com.google.gson.JsonSyntaxException;
import org.restcomm.connect.rvd.model.ModelMarshaller;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


/**
 * Wraps OldWorkspaceStorage and handles converting models to/from json strings
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class JsonModelStorage implements WorkspaceStorage {

    WorkspaceStorage workspaceStorage;
    ModelMarshaller marshaller;


    public JsonModelStorage(WorkspaceStorage workspaceStorage, ModelMarshaller marshaller) {
        this.workspaceStorage = workspaceStorage;
        this.marshaller = marshaller;
    }

    public <T> T loadEntity(String entityName, String entityPath, Class<T> entityClass) throws StorageException {
        String data = workspaceStorage.loadEntityString(entityName, entityPath);
        try {
            T instance = marshaller.toModel(data, entityClass);
            return instance;
        } catch (JsonSyntaxException e) {
            throw new StorageException("Error loading json from " + entityName, e);
        }
    }

    public void storeEntity(Object entity, Class<?> entityClass, String entityName, String entityPath ) throws StorageException {
        String data = marshaller.getGson().toJson(entity, entityClass);
        workspaceStorage.storeEntityString(data,entityName, entityPath);
    }

    public void storeEntity(Object entity, String entityName, String entityPath ) throws StorageException {
        String data = marshaller.getGson().toJson(entity);
        workspaceStorage.storeEntityString(data, entityName, entityPath);
    }

    @Override
    public boolean entityExists(String entityName, String relativePath) {
        return workspaceStorage.entityExists(entityName, relativePath);
    }

    @Override
    public List<String> listContents(String path, String regexNameFilter, boolean onlyDirectories) throws StorageException {
        return workspaceStorage.listContents(path, regexNameFilter,onlyDirectories);
    }

    @Override
    public void removeEntity(String entityName, String entityPath) {
        workspaceStorage.removeEntity(entityName, entityPath);
    }

    @Override
    public String loadEntityString(String entityName, String entityPath) throws StorageException {
        return workspaceStorage.loadEntityString(entityName, entityPath);
    }

    @Override
    public void storeEntityString(String entityString, String entityName, String entityPath) throws StorageException {
        storeEntityString(entityString, entityName, entityPath);
    }

    @Override
    public void storeBinaryFile(File sourceFile, String entityName, String entityPath) throws StorageException {
        storeBinaryFile(sourceFile, entityName, entityPath);
    }

    @Override
    public InputStream loadBinaryFile(String projectName, String entityName, String entityPath) throws FileNotFoundException {
        return workspaceStorage.loadBinaryFile(projectName, entityName, entityPath);
    }

    @Override
    public InputStream loadStream(String entityName, String entityPath) throws StorageException {
        return workspaceStorage.loadStream(entityName, entityPath);
    }

    @Override
    public String resolveWorkspacePath(String path) {
        return resolveWorkspacePath(path);
    }
}
