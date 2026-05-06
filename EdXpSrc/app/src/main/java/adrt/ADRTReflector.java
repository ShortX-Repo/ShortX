package adrt;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ADRTReflector {
    private static Set<String> toStringClasses;

    public static void getFieldValueToObjectString(Object obj, String str, List<String> list, List<String> list2, ArrayList<String> arrayList) {
        if ("toString()".equals(str)) {
            list.add("");
            list2.add("\"" + obj.toString() + "\"");
            arrayList.add("M");
            return;
        }
        getFieldValueToObjectString(obj, obj.getClass(), str, list, list2, arrayList);
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x009b -> B:16:0x005c). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:26:0x009d -> B:32:0x0044). Please report as a decompilation issue!!! */
    private static void getFieldValueToObjectString(Object obj, Class<?> cls, String str, List<String> list, List<String> list2, ArrayList<String> arrayList) {
        Field declaredField;
        if (cls.isArray()) {
            if (str.startsWith("[") && str.endsWith("]")) {
                try {
                    int i = Integer.parseInt(str.substring(1, str.length() - 1));
                    if (cls.getComponentType().isPrimitive()) {
                        list.add("");
                        list2.add(toString(Array.get(obj, i)));
                        arrayList.add("F");
                    } else {
                        list.add("");
                        list2.add(toObjectString(Array.get(obj, i)));
                        arrayList.add("O");
                    }
                    return;
                } catch (Throwable th) {
                }
            }
            list.add("");
            list2.add("<undefined>");
            arrayList.add("O");
        }
        try {
            declaredField = cls.getDeclaredField(str);
        } catch (Throwable th2) {
        }
        if (declaredField != null) {
            declaredField.setAccessible(true);
            if (declaredField.getType().isPrimitive()) {
                list.add("");
                list2.add(toString(declaredField.get(obj)));
                arrayList.add("F");
            } else {
                list.add("");
                list2.add(toObjectString(declaredField.get(obj)));
                arrayList.add("O");
            }
        } else {
            Class<? super Object> superclass = cls.getSuperclass();
            if (superclass != null) {
                getFieldValueToObjectString(obj, superclass, str, list, list2, arrayList);
            } else {
                list.add("");
                list2.add("<undefined>");
                arrayList.add("O");
            }
        }
    }

    public static Object getObjectFieldValue(Object obj, String str) {
        return getObjectFieldValue(obj, obj.getClass(), str);
    }

    private static Object getObjectFieldValue(Object obj, Class<?> cls, String str) {
        if (cls.isArray()) {
            if (cls.getComponentType().isPrimitive() || !str.startsWith("[") || !str.endsWith("]")) {
                return null;
            }
            try {
                return Array.get(obj, Integer.parseInt(str.substring(1, str.length() - 1)));
            } catch (Throwable th) {
                return null;
            }
        }
        try {
            Field declaredField = cls.getDeclaredField(str);
            if (declaredField != null) {
                if (declaredField.getType().isPrimitive() || (declaredField.getModifiers() & 8) != 0) {
                    return null;
                }
                declaredField.setAccessible(true);
                return declaredField.get(obj);
            }
        } catch (Throwable th2) {
        }
        Class<? super Object> superclass = cls.getSuperclass();
        if (superclass != null) {
            return getObjectFieldValue(obj, superclass, str);
        }
        return null;
    }

    public static void getFieldValues(Object obj, List<String> list, List<String> list2, List<String> list3) {
        Class<?> cls = obj.getClass();
        if (!cls.isArray()) {
            list.add("toString()");
            list2.add("");
            list3.add("M");
        }
        getFieldValues(obj, cls, false, list, list2, list3);
    }

    private static void getFieldValues(Object obj, Class<?> cls, boolean z, List<String> list, List<String> list2, List<String> list3) {
        if (cls.isArray()) {
            int iMin = Math.min(100, Array.getLength(obj));
            for (int i = 0; i < iMin; i++) {
                list.add("[" + i + "]");
                list2.add(toString(Array.get(obj, i)));
                list3.add("E");
            }
            return;
        }
        for (Field field : cls.getDeclaredFields()) {
            if ((field.getModifiers() & 8) == 0) {
                try {
                    String name = field.getName();
                    if (!list.contains(name) && !name.startsWith("shadow$")) {
                        field.setAccessible(true);
                        String string = toString(field.get(obj));
                        list.add(name);
                        list2.add(string);
                        list3.add(z ? "I" : "F");
                    }
                } catch (Throwable th) {
                }
            }
        }
        Class<? super Object> superclass = cls.getSuperclass();
        if (superclass != null) {
            getFieldValues(obj, superclass, true, list, list2, list3);
        }
    }

    public static String toString(Object obj) {
        int i = 0;
        if (obj == null) {
            return "null";
        }
        if (obj instanceof int[]) {
            int[] iArr = (int[]) obj;
            if (iArr.length < 30) {
                StringBuilder sb = new StringBuilder();
                sb.append("{ ");
                while (i < iArr.length) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(iArr[i]);
                    i++;
                }
                sb.append(" }");
                return sb.toString();
            }
        }
        if (obj instanceof byte[]) {
            byte[] bArr = (byte[]) obj;
            if (bArr.length < 30) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("{ ");
                while (i < bArr.length) {
                    if (i > 0) {
                        sb2.append(", ");
                    }
                    sb2.append((int) bArr[i]);
                    i++;
                }
                sb2.append(" }");
                return sb2.toString();
            }
        }
        if (obj instanceof short[]) {
            short[] sArr = (short[]) obj;
            if (sArr.length < 30) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("{ ");
                while (i < sArr.length) {
                    if (i > 0) {
                        sb3.append(", ");
                    }
                    sb3.append((int) sArr[i]);
                    i++;
                }
                sb3.append(" }");
                return sb3.toString();
            }
        }
        if (obj instanceof float[]) {
            float[] fArr = (float[]) obj;
            if (fArr.length < 30) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append("{ ");
                while (i < fArr.length) {
                    if (i > 0) {
                        sb4.append(", ");
                    }
                    sb4.append(fArr[i]);
                    i++;
                }
                sb4.append(" }");
                return sb4.toString();
            }
        }
        if (obj instanceof double[]) {
            double[] dArr = (double[]) obj;
            if (dArr.length < 30) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append("{ ");
                while (i < dArr.length) {
                    if (i > 0) {
                        sb5.append(", ");
                    }
                    sb5.append(dArr[i]);
                    i++;
                }
                sb5.append(" }");
                return sb5.toString();
            }
        }
        if (obj instanceof boolean[]) {
            boolean[] zArr = (boolean[]) obj;
            if (zArr.length < 30) {
                StringBuilder sb6 = new StringBuilder();
                sb6.append("{ ");
                while (i < zArr.length) {
                    if (i > 0) {
                        sb6.append(", ");
                    }
                    sb6.append(zArr[i]);
                    i++;
                }
                sb6.append(" }");
                return sb6.toString();
            }
        }
        if (obj instanceof char[]) {
            char[] cArr = (char[]) obj;
            if (cArr.length < 30) {
                StringBuilder sb7 = new StringBuilder();
                sb7.append("{ ");
                while (i < cArr.length) {
                    if (i > 0) {
                        sb7.append(", ");
                    }
                    sb7.append("'");
                    sb7.append(cArr[i]);
                    sb7.append("'");
                    i++;
                }
                sb7.append(" }");
                return sb7.toString();
            }
        }
        if (obj instanceof Throwable) {
            return obj.toString();
        }
        Class<?> cls = obj.getClass();
        if (obj instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) obj;
            if (charSequence.length() < 100) {
                return "\"" + charSequence.toString() + "\"";
            }
            return cls.getName() + "(" + charSequence.length() + ")@" + getObjectId(obj);
        }
        if (cls.isEnum()) {
            return obj.toString();
        }
        if (cls.isArray()) {
            int length = Array.getLength(obj);
            if (length < 30) {
                StringBuilder sb8 = new StringBuilder();
                sb8.append("{ ");
                while (i < length) {
                    if (i > 0) {
                        sb8.append(", ");
                    }
                    sb8.append(toString(Array.get(obj, i)));
                    i++;
                }
                sb8.append(" }");
                return sb8.toString();
            }
            return toObjectString(obj);
        }
        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            if (collection.size() <= 30) {
                StringBuilder sb9 = new StringBuilder();
                sb9.append("{ ");
                for (Object obj2 : collection) {
                    int i2 = i + 1;
                    if (i > 0) {
                        sb9.append(", ");
                    }
                    sb9.append(toString(obj2));
                    i = i2;
                }
                sb9.append(" }");
                return sb9.toString();
            }
            return cls.getName() + "(" + collection.size() + ")@" + getObjectId(obj);
        }
        if (obj instanceof Map) {
            Map map = (Map) obj;
            if (map.size() <= 30) {
                StringBuilder sb10 = new StringBuilder();
                sb10.append("{ ");
                for (Map.Entry entry : map.entrySet()) {
                    int i3 = i + 1;
                    if (i > 0) {
                        sb10.append(", ");
                    }
                    sb10.append(toString(entry.getKey()));
                    sb10.append(" -> ");
                    sb10.append(toString(entry.getValue()));
                    i = i3;
                }
                sb10.append(" }");
                return sb10.toString();
            }
            return cls.getName() + "(" + map.size() + ")@" + getObjectId(obj);
        }
        if (cls == Character.class) {
            return "'" + obj.toString() + "'";
        }
        if (toStringClasses == null) {
            toStringClasses = new HashSet(Arrays.asList("java.lang.Integer", "java.lang.Boolean", "java.lang.Byte", "java.lang.Short", "java.lang.Character", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Class", "java.io.File", "java.math.BigInteger", "android.graphics.Rect", "android.graphics.RectF", "android.graphics.Point", "android.graphics.PointF"));
        }
        if (toStringClasses.contains(cls.getName())) {
            return obj.toString();
        }
        return toObjectString(obj);
    }

    private static String getObjectId(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    public static String toObjectString(Object obj) {
        if (obj == null) {
            return "null";
        }
        Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            String str = "";
            Class<?> componentType = cls.getComponentType();
            while (componentType.isArray()) {
                str = str + "[]";
                componentType = componentType.getComponentType();
            }
            return componentType.getName() + "[" + Array.getLength(obj) + "]" + str + "@" + getObjectId(obj);
        }
        return cls.getName() + "@" + getObjectId(obj);
    }
}
