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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class Unzipper {
    File outputDirectory;

    public Unzipper(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void unzip(InputStream zipStream) {
        try{
           ZipInputStream zipInputStream = new ZipInputStream(zipStream);

           ZipEntry zipEntry = zipInputStream.getNextEntry();

           while ( zipEntry != null ) {
              String fileName = zipEntry.getName();
              String pathname = outputDirectory.getPath() + File.separator + fileName;

              String destinationDirPath = FilenameUtils.getFullPath(pathname);
              File destinationDir = new File(destinationDirPath);

              // create the destination directory if it does not exist (works for both file and dir entries)
              if (!destinationDir.exists()) {
                  destinationDir.mkdirs();
              }

              if (!zipEntry.isDirectory()) {
                  FileOutputStream fileEntryStream = new FileOutputStream(new File(pathname));
                  IOUtils.copy(zipInputStream, fileEntryStream);
                  fileEntryStream.close();
              }

              zipEntry = zipInputStream.getNextEntry();
           }
           zipInputStream.closeEntry();

       } catch(IOException ex) {
          ex.printStackTrace();
       }
    }
}
