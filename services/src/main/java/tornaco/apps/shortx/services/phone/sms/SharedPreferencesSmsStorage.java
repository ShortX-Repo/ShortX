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

import tornaco.apps.shortx.services.data.SettingsStore;

/**
 * SmsStorage implementation based on shared preferences.
 *
 * @author Pedro Vicente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
class SharedPreferencesSmsStorage implements SmsStorage {

    private static final String LAST_SMS_PARSED = "last_sms_parsed";
    private static final int DEFAULT_SMS_PARSED_VALUE = -1;

    private final SettingsStore store;

    public SharedPreferencesSmsStorage(SettingsStore store) {
        this.store = store;
    }

    @Override
    public void updateLastSmsIntercepted(int smsId) {
        store.putInt(LAST_SMS_PARSED, smsId);
    }

    @Override
    public int getLastSmsIntercepted() {
        return store.getInt(LAST_SMS_PARSED, DEFAULT_SMS_PARSED_VALUE);
    }

    @Override
    public boolean isFirstSmsIntercepted() {
        return getLastSmsIntercepted() == DEFAULT_SMS_PARSED_VALUE;
    }
}
