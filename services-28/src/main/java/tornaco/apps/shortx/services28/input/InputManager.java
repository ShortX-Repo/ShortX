package tornaco.apps.shortx.services28.input;

import android.view.InputEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import tornaco.apps.shortx.core.util.Logger;

public final class InputManager {
    private static final Logger LOGGER = new Logger("InputManager");

    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;

    private final android.hardware.input.InputManager manager;
    private Method injectInputEventMethod;

    private static Method setDisplayIdMethod;

    public InputManager(android.hardware.input.InputManager manager) {
        this.manager = manager;
    }

    private Method getInjectInputEventMethod() throws NoSuchMethodException {
        if (injectInputEventMethod == null) {
            injectInputEventMethod = manager.getClass().getMethod("injectInputEvent", InputEvent.class, int.class);
        }
        return injectInputEventMethod;
    }

    public boolean injectInputEvent(InputEvent inputEvent, int mode) {
        try {
            Method method = getInjectInputEventMethod();
            return (boolean) method.invoke(manager, inputEvent, mode);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            LOGGER.e(e, "Could not invoke method");
            return false;
        }
    }

    private static Method getSetDisplayIdMethod() throws NoSuchMethodException {
        if (setDisplayIdMethod == null) {
            setDisplayIdMethod = InputEvent.class.getMethod("setDisplayId", int.class);
        }
        return setDisplayIdMethod;
    }

    public static boolean setDisplayId(InputEvent inputEvent, int displayId) {
        try {
            Method method = getSetDisplayIdMethod();
            method.invoke(inputEvent, displayId);
            return true;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            LOGGER.e(e, "Cannot associate a display id to the input event");
            return false;
        }
    }
}
