/*
 * Copyright (c) 2016 Lukas Morawietz
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

package com.faendir.rhino_android;

import android.os.Build;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

import java.io.File;

/**
 * Ensures that the classLoader used is correct
 *
 * @author F43nd1r
 * @since 11.01.2016
 */
public class AndroidContextFactory extends ContextFactory {

    private final File cacheDirectory;

    /**
     * Create a new factory. It will cache generated code in the given directory
     *
     * @param cacheDirectory the cache directory
     */
    public AndroidContextFactory(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
        initApplicationClassLoader(createClassLoader(AndroidContextFactory.class.getClassLoader()));
    }

    /**
     * Create a ClassLoader which is able to deal with bytecode
     *
     * @param parent the parent of the create classloader
     * @return a new ClassLoader
     */
    @Override
    protected BaseAndroidClassLoader createClassLoader(ClassLoader parent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new InMemoryAndroidClassLoader(parent);
        }
        return new FileAndroidClassLoader(parent, cacheDirectory);
    }

    @Override
    protected void onContextReleased(final Context cx) {
        super.onContextReleased(cx);
        ((BaseAndroidClassLoader) cx.getApplicationClassLoader()).reset();
    }
}
