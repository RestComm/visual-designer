package org.restcomm.connect.rvd.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.restcomm.connect.rvd.model.ModelMarshaller;
import org.restcomm.connect.rvd.model.project.StateHeader;
import org.restcomm.connect.rvd.storage.exceptions.BadProjectHeader;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


/**
 * Provides all types of access to the storage backend. In addition, it handles conversion to/from json.
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

    public StateHeader loadStateHeader(String projectName) throws StorageException {
        String stateData = workspaceStorage.loadEntityString("state", projectName);
        JsonParser parser = new JsonParser();
        JsonElement header_element = null;
        try {
            header_element = parser.parse(stateData).getAsJsonObject().get("header");
        } catch (JsonSyntaxException e) {
            throw new StorageException("Error loading header for project '" + projectName +"'",e);
        }
        if ( header_element == null )
            throw new BadProjectHeader("No header found. This is probably an old project");

        Gson gson = new Gson();
        StateHeader header = gson.fromJson(header_element, StateHeader.class);

        return header;
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
        workspaceStorage.storeEntityString(entityString, entityName, entityPath);
    }

    @Override
    public void storeBinaryFile(File sourceFile, String entityName, String entityPath) throws StorageException {
        workspaceStorage.storeBinaryFile(sourceFile, entityName, entityPath);
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
    public void copyDirToWorkspace(String sourcePath, String workspaceParentPath) throws StorageException {
        workspaceStorage.copyDirToWorkspace(sourcePath, workspaceParentPath);
    }

    @Override
    public String resolveWorkspacePath(String path) {
        return workspaceStorage.resolveWorkspacePath(path);
    }
}
