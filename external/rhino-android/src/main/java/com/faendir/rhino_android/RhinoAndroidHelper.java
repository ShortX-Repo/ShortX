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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.SecurityController;

import java.io.File;

/**
 * Helps to prepare a Rhino Context for usage on android.
 *
 * @author F43nd1r
 * @since 11.01.2016
 */
public class RhinoAndroidHelper {

    private final File cacheDirectory;

    public RhinoAndroidHelper(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    /**
     * call this instead of {@link Context#enter()}
     *
     * @return a context prepared for android
     */
    public Context enterContext() {
        if (!SecurityController.hasGlobal())
            SecurityController.initGlobal(new NoSecurityController());
        return getContextFactory().enterContext();
    }

    /**
     * @return The Context factory which has to be used on android.
     */
    public AndroidContextFactory getContextFactory() {
        AndroidContextFactory factory;
        if (!ContextFactory.hasExplicitGlobal()) {
            factory = createAndroidContextFactory(cacheDirectory);
            ContextFactory.getGlobalSetter().setContextFactoryGlobal(factory);
        } else if (!(ContextFactory.getGlobal() instanceof AndroidContextFactory)) {
            throw new IllegalStateException("Cannot initialize factory for Android Rhino: There is already another factory");
        } else {
            factory = (AndroidContextFactory) ContextFactory.getGlobal();
        }
        return factory;
    }

    /**
     * Override this to modify the context factory.
     *
     * @return a new {@link AndroidContextFactory}
     */
    protected AndroidContextFactory createAndroidContextFactory(File cacheDirectory) {
        return new AndroidContextFactory(cacheDirectory);
    }
}
