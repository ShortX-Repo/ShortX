package tornaco.apps.shortx.services.rule.actor.mvel.handle;

/**
 * Battery API
 */
@HandlerName("battery")
public interface IBattery {
    BatteryState getBatteryState();
}

