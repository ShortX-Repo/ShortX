package util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReflectionsExt {
    private static final Map<String, Integer> METHOD_PARAM_INDEX_CACHE = new HashMap<>();

    public static Object getObjectFieldOrNull(Object obj, String fieldName) {
        try {
            return Reflections.getObjectField(obj, fieldName);
        } catch (Throwable e) {
            return null;
        }
    }

    // @RequiresApi(api = Build.VERSION_CODES.N)
    public static Method findMethodWithMostArgs(Class<?> clazz, String methodName) {
        List<Method> methodList = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                methodList.add(m);
            }
        }
        if (methodList.isEmpty()) {
            return null;
        }
        methodList.sort(new Comparator<Method>() {
            @Override
            public int compare(Method a, Method b) {
                return -Integer.compare(a.getParameterTypes().length, b.getParameterTypes().length);
            }
        });
        return methodList.get(0);
    }

    // @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getFirstArgIndexWithTypeForMethod(Method method, String className) {
        try {
            Parameter[] parameters = method.getParameters();
            if (parameters.length == 0) {
                return -1;
            }
            for (int i = 0; i < parameters.length; i++) {
                Parameter p = parameters[i];
                if (Objects.equals(p.getType().getName(), className)) {
                    return i;
                }
            }
        } catch (Throwable ignored) {
        }
        return -1;
    }

    public static int getFirstArgIndexLikeTypeForMethod(Method method, String classNameOrSimpleName) {
        try {
            Parameter[] parameters = method.getParameters();
            if (parameters.length == 0) {
                return -1;
            }
            for (int i = 0; i < parameters.length; i++) {
                Parameter p = parameters[i];
                if (Objects.equals(p.getType().getName(), classNameOrSimpleName) || Objects.equals(p.getType().getSimpleName(), classNameOrSimpleName)) {
                    return i;
                }
            }
        } catch (Throwable ignored) {
        }
        return -1;
    }

    public static Class<?> anyClassFromNames(ClassLoader classLoader, String[] classNames) throws ClassNotFoundException {
        for (String className : classNames) {
            try {
                return Reflections.findClass(className, classLoader);
            } catch (Throwable ignored) {
            }
        }
        throw new ClassNotFoundException(Arrays.toString(classNames));
    }

    public static Class<?> anyClassFromNames(ClassLoader classLoader, String methodNameToFind, String[] classNames) throws ClassNotFoundException {
        for (String className : classNames) {
            try {
                Class<?> res = Reflections.findClass(className, classLoader);
                boolean methodExists = false;
                for (Method method : res.getDeclaredMethods()) {
                    if (method.getName().equals(methodNameToFind)) {
                        methodExists = true;
                    }
                }
                if (methodExists) return res;
            } catch (Throwable ignored) {
            }
        }
        throw new ClassNotFoundException(Arrays.toString(classNames));
    }

    public static int getIntFieldWithPotentialNames(Object obj, String... potentialFieldNames) throws NoSuchFieldException {
        for (String fieldName : potentialFieldNames) {
            try {
                return Reflections.getIntField(obj, fieldName);
            } catch (Throwable ignored) {
            }
        }
        throw new NoSuchFieldException(Arrays.toString(potentialFieldNames));
    }

    public static boolean matchMethodNameAndArgs(Method methodToCheck, String methodNameExpected, Class<?>... argTypesExpected) {
        if (!methodNameExpected.equals(methodToCheck.getName())) {
            return false;
        }
        return Arrays.equals(methodToCheck.getParameterTypes(), argTypesExpected);
    }

    public static Object callMethodWithPreferredNames(Object obj, String[] methodNames, Object... args) {
        for (String methodName : methodNames) {
            try {
                return Reflections.callMethod(obj, methodName, args);
            } catch (NoSuchMethodError ignored) {
                // Try next one.
            }
        }
        return null;
    }
}
