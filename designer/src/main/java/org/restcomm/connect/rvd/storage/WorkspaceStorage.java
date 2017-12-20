package org.restcomm.connect.rvd.storage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import org.restcomm.connect.rvd.model.CallControlInfo;
import org.restcomm.connect.rvd.model.ModelMarshaler;
import org.restcomm.connect.rvd.storage.exceptions.StorageEntityNotFound;
import org.restcomm.connect.rvd.storage.exceptions.StorageException;

public class WorkspaceStorage {

    String rootPath; // path of the .../workspace directory (trailing slash NOT included
    ModelMarshaler marshaler;

    public WorkspaceStorage(String rootPath, ModelMarshaler marshaler ) {
        this.rootPath = rootPath;
        this.marshaler = marshaler;
    }

    public boolean entityExists(String entityName, String relativePath) {
        if ( !relativePath.startsWith( "/") )
            relativePath = File.separator + relativePath;
        String pathname = rootPath + relativePath + File.separator + entityName;
        File file = new File(pathname);
        return file.exists();
    }

    /**
     * Returns all directory entries under 'path' with names matching regexNameFilter
     *
     * @param path
     * @param regexNameFilter
     * @return
     * @throws StorageException
     */
    public List<String> listIds(String path, String regexNameFilter) throws StorageException {
        File parentDir;
        if ( path.startsWith( "/") )
            parentDir = new File(path);
        else
            parentDir = new File(rootPath + File.separator + path);

        final Pattern pattern = Pattern.compile(regexNameFilter);
        if (parentDir.exists()) {
            File[] entries = parentDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File anyfile) {
                    if ( anyfile.isDirectory() && pattern.matcher(anyfile.getName()).matches())
                        return true;
                    return false;
                }
            });
            // sort results by modification date
            Arrays.sort(entries, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    long f1_modified = f1.lastModified();
                    long f2_modified = f2.lastModified();
                    return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                }
            });

            List<String> items = new ArrayList<String>();
            for (File entry : entries)
                items.add(entry.getName());

            return items;
        } else
            throw new StorageException("No parent directory found to list its contents: " + path);
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

    public void removeEntity(String entityName, String entityPath) {
        // convert relative paths to absolute
        if ( ! entityPath.startsWith( "/") )
            entityPath = rootPath + File.separator + entityPath;
        String pathname = entityPath + File.separator + entityName;

        File file = new File(pathname);
        FileUtils.deleteQuietly(file);
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

    public String loadEntityString(String entityName, String entityPath) throws StorageException {
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
            return data;
        } catch (IOException e) {
            throw new StorageException("Error loading file " + file.getPath(), e);
        }
    }

    public void storeEntityString(String entityString, String entityName, String entityPath) throws StorageException {
        // convert relative paths to absolute
        if ( ! entityPath.startsWith( "/") )
            entityPath = rootPath + File.separator + entityPath;
        String pathname = entityPath + File.separator + entityName;

        File file = new File(pathname);
        try {
            FileUtils.writeStringToFile(file, entityString, "UTF-8");
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

    public static void storeInfo(CallControlInfo info, String projectName, WorkspaceStorage workspaceStorage) throws StorageException {
        workspaceStorage.storeEntity(info, CallControlInfo.class, "cc", projectName);
    }

    public <T> T loadModelFromXMLFile(String filepath, Class<T> modelClass) throws StorageException {
        File file = new File(filepath);
        return loadModelFromXMLFile(file, modelClass);
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

    public <T> T loadModelFromFile(String filepath, Type gsonType) throws StorageException {
        File file = new File(filepath);
        return loadModelFromFile(file, gsonType);
    }

    public <T> T loadModelFromFile(File file, Type gsonType) throws StorageException {
        if ( !file.exists() )
            throw new StorageEntityNotFound("Cannot find file: " + file.getPath() );

        try {
            String data = FileUtils.readFileToString(file, "UTF-8");
            T instance = marshaler.getGson().fromJson(data, gsonType);
            return instance;

        } catch (IOException e) {
            throw new StorageException("Error loading model from file '" + file + "'", e);
        }
    }

    /**
     * Resolves a path under the workspace. If absolute it does nothing.
     *
     * For example, path "AP123/wavs" will resolve to "/home/.../workspace/AP123/wavs".
     * Empty ("") path will resolve to workspace root.
     *
     * @param path
     * @return a path string or null
     */
    public String resolveWorkspacePath(String path) {
        if (path == null)
            return null;
        // if this is a relative path append workspace root
        if ( ! path.startsWith( "/") ) {
            if ( path.isEmpty() )
                path = rootPath;
            else
                path = rootPath + File.separator + path;
        }
        return path;
    }



}
