/*
 * Copyright (c) Tuenti Technologies S.L. All rights reserved.
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
package tornaco.apps.shortx.services.phone.sms;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import tornaco.apps.shortx.services.data.SettingsStore;


/**
 * Service created to handle the SmsContentObserver registration. This service has the responsibility of register and
 * unregister the content observer in sms content provider when it's created and destroyed.
 * <p/>
 * The SmsContentObserver will be registered over the CONTENT_SMS_URI to be notified each time the system update the
 * sms content provider.
 *
 * @author Pedro Vcente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
public class SmsRadarService {

    private static final String CONTENT_SMS_URI = "content://sms";

    private ContentResolver contentResolver;
    private SmsObserver smsObserver;
    private boolean initialized;

    private final Context context;
    private final Handler handler;
    private final SettingsStore store;

    public SmsRadarService(Context context, Handler handler, SettingsStore store) {
        this.context = context;
        this.handler = handler;
        this.store = store;
    }

    public void onStart() {
        if (!initialized) {
            initializeService();
        }
    }

    public void onDestroy() {
        finishService();
    }

    private void initializeService() {
        initialized = true;
        initializeDependencies();
        registerSmsContentObserver();
    }

    private void initializeDependencies() {
        if (!areDependenciesInitialized()) {
            initializeContentResolver();
            initializeSmsObserver();
        }
    }

    private boolean areDependenciesInitialized() {
        return contentResolver != null && smsObserver != null;
    }

    private void initializeSmsObserver() {
        SmsCursorParser smsCursorParser = initializeSmsCursorParser();
        this.smsObserver = new SmsObserver(contentResolver, handler, smsCursorParser);
    }

    private SmsCursorParser initializeSmsCursorParser() {
        SmsStorage smsStorage = new SharedPreferencesSmsStorage(store);
        return new SmsCursorParser(smsStorage, new TimeProvider());
    }

    private void initializeContentResolver() {
        this.contentResolver = context.getContentResolver();
    }

    private void finishService() {
        initialized = false;
        unregisterSmsContentObserver();
    }

    private void registerSmsContentObserver() {
        Uri smsUri = Uri.parse(CONTENT_SMS_URI);
        boolean notifyForDescendents = true;
        contentResolver.registerContentObserver(smsUri, notifyForDescendents, smsObserver);
    }

    private void unregisterSmsContentObserver() {
        contentResolver.unregisterContentObserver(smsObserver);
    }
}
