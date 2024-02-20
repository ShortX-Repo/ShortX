/*
 * Copyright (C) 2016 Francisco José Montiel Navarro.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tornaco.apps.shortx.core.http.persistentcookiejar;


import android.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import tornaco.apps.shortx.core.BuildProp;
import tornaco.apps.shortx.core.http.persistentcookiejar.cache.CookieCache;
import tornaco.apps.shortx.core.http.persistentcookiejar.persistence.CookiePersistor;
import tornaco.apps.shortx.core.util.Logger;

public class PersistentCookieJar implements ClearableCookieJar {
    private static final Logger LOGGER = new Logger("PersistentCookieJar");

    private final CookieCache cache;
    private final CookiePersistor persistor;

    public PersistentCookieJar(CookieCache cache, CookiePersistor persistor) {
        this.cache = cache;
        this.persistor = persistor;

        this.cache.addAll(persistor.loadAll());
    }

    @Override
    synchronized public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        cache.addAll(cookies);
        persistor.saveAll(filterPersistentCookies(cookies));
        if (BuildProp.APP_IS_DEBUG) {
            LOGGER.d("saveFromResponse: " + url + " --> " + Arrays.toString(cookies.toArray()));
        }
    }

    private static List<Cookie> filterPersistentCookies(List<Cookie> cookies) {
        List<Cookie> persistentCookies = new ArrayList<>();

        for (Cookie cookie : cookies) {
            if (cookie.persistent()) {
                persistentCookies.add(cookie);
            }
        }
        return persistentCookies;
    }

    @Override
    @NonNull
    synchronized public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        List<Cookie> cookiesToRemove = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();

        for (Iterator<Cookie> it = cache.iterator(); it.hasNext(); ) {
            Cookie currentCookie = it.next();

            if (isCookieExpired(currentCookie)) {
                cookiesToRemove.add(currentCookie);
                it.remove();

            } else if (currentCookie.matches(url)) {
                validCookies.add(currentCookie);
            }
        }

        persistor.removeAll(cookiesToRemove);

        if (BuildProp.APP_IS_DEBUG) {
            LOGGER.d("loadForRequest: " + url + " --> " + Arrays.toString(validCookies.toArray()));
        }
        return validCookies;
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    @Override
    synchronized public void clearSession() {
        cache.clear();
        cache.addAll(persistor.loadAll());
    }

    @Override
    synchronized public void clear() {
        cache.clear();
        persistor.clear();
    }
}
