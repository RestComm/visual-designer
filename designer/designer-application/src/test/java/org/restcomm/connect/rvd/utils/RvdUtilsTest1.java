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

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import junit.framework.Assert;
import org.restcomm.connect.rvd.exceptions.StreamDoesNotFitInFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Tests RvdUtils.streamToFile()
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */

public class RvdUtilsTest1 {

    public static final int SOURCE_SIZE = 1024*1024; // 1 MB

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    File sourceFile;

    @Before
    public void before() throws IOException {
        sourceFile = folder.newFile("source");
        FileOutputStream tempStream = new FileOutputStream(sourceFile);
        byte[] buffer = "a random string to be used in RvdUtilsTest1 and detect whether streamToFiele works".getBytes();
        int writtenCount = 0;
        while (writtenCount < SOURCE_SIZE) {
            int sizeToWrite = (SOURCE_SIZE - writtenCount < buffer.length) ? SOURCE_SIZE-writtenCount : buffer.length;
            tempStream.write(buffer, 0, sizeToWrite );
            writtenCount += sizeToWrite;
        }
        tempStream.close();
    }

    // Copying small files should not throw an exception. Also compare the contents of the resulting files.
    @Test
    public void copyAllowedFile() throws IOException, StreamDoesNotFitInFile {
        File destFile = folder.newFile("dest1");
        RvdUtils.streamToFile(new FileInputStream(sourceFile), destFile, SOURCE_SIZE );
        Assert.assertTrue(IOUtils.contentEquals(new FileInputStream(sourceFile), new FileInputStream(destFile)));
        destFile = folder.newFile("dest2");
        RvdUtils.streamToFile(new FileInputStream(sourceFile), destFile, SOURCE_SIZE + 1024 );
        Assert.assertTrue(IOUtils.contentEquals(new FileInputStream(sourceFile), new FileInputStream(destFile)));
    }

    // Copying too large files should throw an exception while the destination file should be removed too.
    @Test(expected=StreamDoesNotFitInFile.class)
    public void copyTooLargeFile() throws IOException, StreamDoesNotFitInFile {
        File destFile = folder.newFile("dest3");
        RvdUtils.streamToFile(new FileInputStream(sourceFile), destFile, SOURCE_SIZE - 1024 );
        Assert.assertFalse(destFile.exists());
    }


}
