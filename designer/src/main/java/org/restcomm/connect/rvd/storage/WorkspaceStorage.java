package org.restcomm.connect.rvd.storage;

import org.restcomm.connect.rvd.storage.exceptions.StorageException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public interface WorkspaceStorage {
    boolean entityExists(String entityName, String relativePath);

    List<String> listContents(String path, String regexNameFilter, boolean onlyDirectories) throws StorageException;

    void removeEntity(String entityName, String entityPath);

    String loadEntityString(String entityName, String entityPath) throws StorageException;

    void storeEntityString(String entityString, String entityName, String entityPath) throws StorageException;

    void storeBinaryFile(File sourceFile, String entityName, String entityPath) throws StorageException;

    InputStream loadBinaryFile(String projectName, String entityName, String entityPath) throws FileNotFoundException;

    InputStream loadStream(String entityName, String entityPath) throws StorageException;

    String resolveWorkspacePath(String path);
}
