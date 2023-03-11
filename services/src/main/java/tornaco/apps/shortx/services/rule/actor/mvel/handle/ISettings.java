package tornaco.apps.shortx.services.rule.actor.mvel.handle;

@HandlerName("settings")
public interface ISettings {
    void enableDarkMode();

    void disableDarkMode();

    boolean isDarkModeEnabled();
}
