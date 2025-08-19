package tornaco.apps.shortx.core.http.persistentcookiejar.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import tornaco.apps.shortx.core.store.BaseSettingsStore;

public class SettingsStoreCookiePersistor implements CookiePersistor {

    private final BaseSettingsStore store;

    public SettingsStoreCookiePersistor(BaseSettingsStore store) {
        this.store = store;
    }

    @Override
    public List<Cookie> loadAll() {
        List<Cookie> cookies = new ArrayList<>(store.getAllString().size());
        for (Map.Entry<String, ?> entry : store.getAllString().entrySet()) {
            String serializedCookie = (String) entry.getValue();
            Cookie cookie = new SerializableCookie().decode(serializedCookie);
            if (cookie != null) {
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    @Override
    public void saveAll(Collection<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            store.putString(createCookieKey(cookie), new SerializableCookie().encode(cookie));
        }
    }

    @Override
    public void removeAll(Collection<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            store.removeString(createCookieKey(cookie));
        }
    }

    private static String createCookieKey(Cookie cookie) {
        return (cookie.secure() ? "https" : "http") + "://" + cookie.domain() + cookie.path() + "|" + cookie.name();
    }

    @Override
    public void clear() {
        store.clear();
    }
}
