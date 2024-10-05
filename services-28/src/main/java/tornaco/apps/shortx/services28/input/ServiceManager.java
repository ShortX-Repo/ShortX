package tornaco.apps.shortx.services28.input;

import android.annotation.SuppressLint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressLint("PrivateApi,DiscouragedPrivateApi")
public final class ServiceManager {
    private static InputManager inputManager;

    private ServiceManager() {
        /* not instantiable */
    }

    public static InputManager getInputManager() {
        if (inputManager == null) {
            try {
                Method getInstanceMethod = android.hardware.input.InputManager.class.getDeclaredMethod("getInstance");
                android.hardware.input.InputManager im = (android.hardware.input.InputManager) getInstanceMethod.invoke(null);
                inputManager = new InputManager(im);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new AssertionError(e);
            }
        }
        return inputManager;
    }
}
