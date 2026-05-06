package f;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

/* JADX INFO: loaded from: classes.dex */
public class f0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    private static final f0 f433a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    private static f0 f434b;

    private static final class b extends f0 {

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private final BiFunction<Integer, Object[], Object> f435c;

        b(BiFunction<Integer, Object[], Object> biFunction) {
            this.f435c = biFunction;
            e.w(biFunction);
        }

        private <T> T v(int i, Object... objArr) {
            return (T) this.f435c.apply(Integer.valueOf(i), objArr);
        }

        @Override // f.f0
        public Class<?> d(String str, ClassLoader classLoader) {
            Class<?> cls = (Class) v(2, str, classLoader);
            return cls != null ? cls : super.d(str, classLoader);
        }

        @Override // f.f0
        public <T> Constructor<T> e(Class<?> cls, Class<?>... clsArr) {
            Constructor<T> constructor = (Constructor) v(5, cls, clsArr);
            return constructor != null ? constructor : super.e(cls, clsArr);
        }

        @Override // f.f0
        public Field g(Class<?> cls, String str) {
            Field field = (Field) v(4, cls, str);
            return field != null ? field : super.g(cls, str);
        }

        @Override // f.f0
        public Method h(Class<?> cls, String str, Class<?>... clsArr) {
            Method method = (Method) v(8, cls, str, clsArr);
            return method != null ? method : super.h(cls, str, clsArr);
        }

        @Override // f.f0
        public Method j(Class<?> cls, String str, Class<?>... clsArr) {
            Method method = (Method) v(7, cls, str, clsArr);
            return method != null ? method : super.j(cls, str, clsArr);
        }

        @Override // f.f0
        public Method[] m(Class<?> cls) {
            Method[] methodArr = (Method[]) v(12, cls);
            return methodArr != null ? methodArr : super.m(cls);
        }

        @Override // f.f0
        public void s(AccessibleObject accessibleObject) {
            if (v(1, accessibleObject) == null) {
                super.s(accessibleObject);
            }
        }
    }

    private static class c extends Pair<String, Object> implements Application.OnProvideAssistDataListener {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        static final String f436a = l.j + ".REFLECT_ADAPTER";

        public c(Object obj) {
            super(f436a, obj);
        }

        @Override // android.app.Application.OnProvideAssistDataListener
        public void onProvideAssistData(Activity activity, Bundle bundle) {
            v.c("Unused method!!!");
        }
    }

    public static abstract class d implements InvocationHandler {
        protected abstract Object a(String str, Object[] objArr);

        @Override // java.lang.reflect.InvocationHandler
        public final Object invoke(Object obj, Method method, Object[] objArr) throws CloneNotSupportedException {
            String name;
            name = method.getName();
            name.hashCode();
            switch (name) {
                case "toString":
                    return toString();
                case "equals":
                    return Boolean.valueOf(this == objArr[0]);
                case "clone":
                    throw new CloneNotSupportedException("InterfaceHandler " + getClass().getName() + " doesn't cloneable");
                case "hashCode":
                    return Integer.valueOf(hashCode());
                default:
                    return a(name, objArr);
            }
        }
    }

    private static final class e extends f0 implements BiFunction<Integer, Object[], Object> {
        private e() {
        }

        static void w(BiFunction<Integer, Object[], Object> biFunction) {
            if (biFunction.getClass().getClassLoader() == f0.class.getClassLoader() || !c.f436a.equals(biFunction.apply(0, null))) {
                throw new IllegalArgumentException("Not available function: " + biFunction);
            }
        }

        @Override // java.util.function.BiFunction
        /* JADX INFO: renamed from: v, reason: merged with bridge method [inline-methods] */
        public Object apply(Integer num, Object[] objArr) {
            try {
                switch (num.intValue()) {
                    case 0:
                        return c.f436a;
                    case 1:
                        s((AccessibleObject) objArr[0]);
                        return Boolean.TRUE;
                    case 2:
                        return d((String) objArr[0], (ClassLoader) objArr[1]);
                    case 3:
                        return i((Class) objArr[0], (String) objArr[1]);
                    case 4:
                        return g((Class) objArr[0], (String) objArr[1]);
                    case 5:
                        return e((Class) objArr[0], (Class[]) objArr[1]);
                    case 6:
                        return f((Class) objArr[0], (Class[]) objArr[1]);
                    case 7:
                        return j((Class) objArr[0], (String) objArr[1], (Class[]) objArr[2]);
                    case 8:
                        return h((Class) objArr[0], (String) objArr[1], (Class[]) objArr[2]);
                    case 9:
                        return k((Class) objArr[0]);
                    case 10:
                        return l((Class) objArr[0]);
                    case 11:
                        return o((Class) objArr[0]);
                    case 12:
                        return m((Class) objArr[0]);
                    default:
                        return null;
                }
            } catch (Throwable unused) {
                return null;
            }
        }
    }

    static {
        f0 f0Var = new f0();
        f433a = f0Var;
        f434b = f0Var;
    }

    f0() {
    }

    private static boolean a(Class<?>[] clsArr, Class<?>[] clsArr2) {
        int length;
        if (clsArr == clsArr2) {
            return true;
        }
        if (clsArr == null || clsArr2 == null || clsArr2.length != (length = clsArr.length)) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (clsArr[i] != clsArr2[i]) {
                return false;
            }
        }
        return true;
    }

    public static f0 n() {
        return f434b;
    }

    public static Application.OnProvideAssistDataListener p() {
        if (Build.VERSION.SDK_INT >= 29) {
            return new c(new e());
        }
        return null;
    }

    private static void q(BiFunction<Integer, Object[], Object> biFunction) {
        if (biFunction != null) {
            t(new b(biFunction));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void r(Application.OnProvideAssistDataListener onProvideAssistDataListener) {
        if (Build.VERSION.SDK_INT >= 29 && f434b == f433a && (onProvideAssistDataListener instanceof Pair)) {
            Pair pair = (Pair) onProvideAssistDataListener;
            if (c.f436a.equals(pair.first)) {
                Object obj = pair.second;
                if (obj instanceof BiFunction) {
                    q((BiFunction) obj);
                }
            }
        }
    }

    private static void t(f0 f0Var) {
        if (f0Var == null) {
            f0Var = f433a;
        }
        f434b = f0Var;
    }

    public static boolean u(ClassLoader classLoader) {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                f0 f0VarN = n();
                f0VarN.b(f0VarN.d(f0.class.getName(), classLoader), BiFunction.class).invoke(null, new e());
            } catch (Throwable th) {
                v.d(th);
                return false;
            }
        }
        return true;
    }

    public final Method b(Class<?> cls, Class<?>... clsArr) throws NoSuchMethodException {
        for (Method method : m(cls)) {
            if (a(method.getParameterTypes(), clsArr)) {
                s(method);
                return method;
            }
        }
        throw new NoSuchMethodException(cls.getName() + "#(unnamed)");
    }

    public final Class<?> c(String str) {
        return d(str, getClass().getClassLoader());
    }

    public Class<?> d(String str, ClassLoader classLoader) {
        return Class.forName(str, true, classLoader);
    }

    public <T> Constructor<T> e(Class<?> cls, Class<?>... clsArr) {
        Constructor<T> constructor = (Constructor<T>) cls.getConstructor(clsArr);
        constructor.setAccessible(true);
        return constructor;
    }

    public <T> Constructor<T> f(Class<?> cls, Class<?>... clsArr) {
        Constructor<T> constructor = (Constructor<T>) cls.getDeclaredConstructor(clsArr);
        constructor.setAccessible(true);
        return constructor;
    }

    public Field g(Class<?> cls, String str) throws NoSuchFieldException {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField;
    }

    public Method h(Class<?> cls, String str, Class<?>... clsArr) throws NoSuchMethodException {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }

    public Field i(Class<?> cls, String str) throws NoSuchFieldException {
        Field field = cls.getField(str);
        field.setAccessible(true);
        return field;
    }

    public Method j(Class<?> cls, String str, Class<?>... clsArr) throws NoSuchMethodException {
        Method method = cls.getMethod(str, clsArr);
        method.setAccessible(true);
        return method;
    }

    public Constructor<?>[] k(Class<?> cls) {
        return cls.getConstructors();
    }

    public Constructor<?>[] l(Class<?> cls) {
        return cls.getDeclaredConstructors();
    }

    public Method[] m(Class<?> cls) {
        return cls.getDeclaredMethods();
    }

    public Method[] o(Class<?> cls) {
        return cls.getMethods();
    }

    public void s(AccessibleObject accessibleObject) {
        if (accessibleObject != null) {
            accessibleObject.setAccessible(true);
        }
    }
}
