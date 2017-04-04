/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.restcomm.connect.rvd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.restcomm.connect.rvd.logging.system.RvdLoggers;
import org.restcomm.connect.rvd.utils.exceptions.ZipperException;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class Zipper {
    static Logger logger = RvdLoggers.system;

    ZipOutputStream zipOut;
    File zipFile;

    public Zipper(File tempFile) throws ZipperException {
        zipFile = tempFile;
        try {
            zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
        } catch (FileNotFoundException e) {
            throw new ZipperException("Error creating zip " + zipFile, e);
        }

    }

    public void addDirectory(String name) throws ZipperException {
        try {
            zipOut.putNextEntry(new ZipEntry(name));
            zipOut.closeEntry();
        } catch (IOException e) {
            throw new ZipperException("Error adding directory " + name + " to zip " + zipFile , e);
        }
    }

    public void addFile(String filepath, InputStream fileStream) throws ZipperException {
        try {
            zipOut.putNextEntry(new ZipEntry(filepath));
            IOUtils.copy(fileStream, zipOut);
            zipOut.closeEntry();
        } catch (IOException e) {
            throw new ZipperException("Error adding file " + filepath + " to zip " + zipFile, e);
        }

    }

    public void addFileContent(String filepath, String fileContent) throws ZipperException {
        try {
            zipOut.putNextEntry(new ZipEntry(filepath));
            IOUtils.write(fileContent, zipOut, "UTF-8");
            zipOut.closeEntry();
        } catch (IOException e) {
            throw new ZipperException("Error adding string content to zip " + zipFile, e);
        }

    }

    /**
     * Adds a directory recursively into the zip. Files starting with "." are excluded.
     * @param dirpath An absolute path to the directory to be added. It may contain a trailing slash - "/"
     * @param includeRoot Add the parent directory too in the zip file or just its children
     * @throws ZipperException
     */
    public void addDirectoryRecursively(String dirpath, boolean includeRoot) throws ZipperException {
        File dir = new File(dirpath);
        if ( dir.exists() && dir.isDirectory() ) {
            String dirName = dir.getName();
            String dirParent = dir.getParent();
            if ( includeRoot ) {
                addDirectory(dirName + "/");
                addNestedDirectoryContents(dirParent + "/", dirName);
            } else {
                addNestedDirectoryContents(dirParent + "/" + dirName, "");
            }
        } else {
            throw new ZipperException(dirpath + " is not a directory or does not exist");
        }
    }

    /**
     * Internal use function that implements the recursion logic. What changes throughout the recursion is the childPath that matches the
     * path used while storing in the zip.
     * @param rootPath
     * @param childPath
     * @throws ZipperException
     */
    private void addNestedDirectoryContents(String rootPath, String childPath) throws ZipperException {
        String nestedPath = rootPath + childPath;
        File dir = new File(nestedPath);

        assert dir.isDirectory();

        File[] childrenFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.startsWith(".");
            }
        });

        for ( File file : childrenFiles ) {
            if ( file.isDirectory() ) {
                addDirectory(childPath + "/" + file.getName() + "/");
                addNestedDirectoryContents(rootPath, childPath + "/" + file.getName());
            } else {
                FileInputStream inputStream;
                try {
                    inputStream = new FileInputStream(file);
                    try {
                        addFile(childPath + "/" + file.getName(), inputStream);
                    } finally {
                        inputStream.close();
                    }
                } catch (FileNotFoundException e) {
                    throw new ZipperException("Error adding file " + file + " to zip", e);
                } catch (IOException e ) {
                    throw new ZipperException("Error closingfile " + file + " after adding it to zip", e);
                }

            }
        }
    }

    /**
     * Best effort finish function. If it fails it looks there is more that can be done. We just log
     * the message.
     */
    public void finish() {
        try {
            zipOut.finish();
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.log(Level.WARNING, "Error closing Zipper " + zipFile + ". There is nothing more that can be done.", e);
        }
    }
}
