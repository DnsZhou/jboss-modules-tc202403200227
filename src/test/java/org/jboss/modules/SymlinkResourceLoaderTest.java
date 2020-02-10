/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.modules;

import java.io.File;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.security.AccessController;

import org.jboss.modules.filter.PathFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;

/**
 * Test the functionality of the PathResourceLoader with resources containing symbolic links
 *
 * @author Bartosz Spyrko-Smietanko
 */
public class SymlinkResourceLoaderTest extends AbstractResourceLoaderTestCase {

    private File resourceRoot;

    @After
    public void tearDown() throws Exception {
        File base = getResource("test");
        File symlink = new File(base, "symlink");
        if (symlink.exists()) {
            symlink.delete();
        }
    }

    protected ResourceLoader createLoader(final PathFilter exportFilter) throws Exception {
        File base = getResource("test");
        File realRoot = getResource("test/fileresourceloader");
        try {
            resourceRoot = Files.createSymbolicLink(new File(base, "symlink").toPath(), realRoot.toPath()).toFile();
        } catch (UnsupportedOperationException | FileSystemException e) {
            Assume.assumeNoException(e);
        }

        // Copy the classfile over
        copyResource("org/jboss/modules/test/TestClass.class", "test/fileresourceloader", "org/jboss/modules/test");
        return new PathResourceLoader("test-root", resourceRoot.toPath(), AccessController.getContext());
    }

    @Override
    protected void assertResource(Resource resource, String fileName) {
        final File resourceFile = getExpectedFile(fileName);

        Assert.assertEquals(resourceFile.length(), resource.getSize());
    }

    public void testGetClassSpec() throws Exception {
        super.testGetClassSpec();
    }

    protected File getExpectedFile(String fileName) {
        return new File(resourceRoot, fileName);
    }
}
