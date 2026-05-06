package adrt;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ADRTThread {
    private static boolean[] BOOLEAN_EMPTY = new boolean[0];
    private long currentFileId;
    private int framePos;
    private int stackFramePos;
    private int stackPos;
    private boolean[] currentBreakpointLines = new boolean[0];
    private String[] stackVariables = new String[10000];
    private char[] stackVariableTypes = new char[10000];
    private long[] stackLongs = new long[10000];
    private float[] stackFloats = new float[10000];
    private double[] stackDoubles = new double[10000];
    private Object[] stackObjects = new Object[10000];
    private long[] frameFiles = new long[10000];
    private int[] frameStackPoss = new int[10000];
    private int stepFramePos = -1;
    private int stepStartFramePos = -1;

    public void clearStep() {
        this.stepFramePos = -1;
        this.stepStartFramePos = -1;
    }

    public void setStepOver() {
        this.stepFramePos = this.framePos;
        this.stepStartFramePos = this.framePos;
    }

    public void setStepIn() {
        this.stepFramePos = this.framePos + 1;
        this.stepStartFramePos = this.framePos;
    }

    public void setStepOut() {
        this.stepFramePos = this.framePos - 1;
        this.stepStartFramePos = this.framePos;
    }

    private void sendBreakpointHit(int i) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (int i2 = this.stackFramePos; i2 <= this.stackPos; i2++) {
            if (this.stackVariables[i2] != null) {
                arrayList.add(this.stackVariables[i2]);
                arrayList2.add(getVariableToString(i2, false));
                arrayList3.add("V");
            }
        }
        ArrayList<String> arrayList4 = new ArrayList<>();
        ArrayList<String> arrayList5 = new ArrayList<>();
        ArrayList<String> arrayList6 = new ArrayList<>();
        getStackTrace(ADRT.getFileName(this.currentFileId), i, arrayList4, arrayList5, arrayList6);
        ADRTSender.sendBreakpointHit(ADRT.getPackageName(), arrayList, arrayList2, arrayList3, arrayList4, arrayList5, arrayList6);
    }

    private void getStackTrace(String str, int i, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<String> arrayList3) {
        String str2;
        String strSubstring;
        String str3;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String str4 = "D";
        boolean z = true;
        int i2 = 6;
        while (i2 < stackTrace.length) {
            StackTraceElement stackTraceElement = stackTrace[i2];
            String methodName = stackTraceElement.getMethodName();
            if (!methodName.startsWith("access$")) {
                String className = stackTraceElement.getClassName();
                if (className.endsWith("$0$debug")) {
                    i2++;
                    str4 = "D";
                    className = className.substring(0, className.length() - 8);
                    if (methodName.endsWith("$")) {
                        methodName = methodName.substring(0, methodName.length() - 1);
                    }
                    str2 = methodName + "() in " + className;
                } else if (methodName.equals("<init>")) {
                    str2 = className.substring(className.lastIndexOf(46) + 1) + "() in " + className;
                } else {
                    str4 = "E";
                    str2 = methodName + "() in " + className;
                }
                if (className.contains(".")) {
                    strSubstring = className.substring(0, className.lastIndexOf(46));
                } else {
                    strSubstring = "";
                }
                String str5 = strSubstring.replace('.', '/') + "/" + stackTraceElement.getFileName();
                if (z) {
                    str3 = str + ":" + i;
                } else {
                    str3 = str5 + ":" + stackTraceElement.getLineNumber();
                }
                z = false;
                arrayList.add(str2);
                arrayList2.add(str3);
                arrayList3.add(str4);
            }
            i2++;
            z = z;
        }
    }

    private String getVariableToString(int i, boolean z) {
        switch (this.stackVariableTypes[i]) {
            case 'B':
                return Byte.toString((byte) this.stackLongs[i]);
            case 'C':
                return "'" + Character.toString((char) this.stackLongs[i]) + "'";
            case 'D':
                return Double.toString(this.stackDoubles[i]);
            case 'F':
                return Float.toString(this.stackFloats[i]);
            case 'I':
                return Integer.toString((int) this.stackLongs[i]);
            case 'J':
                return Long.toString(this.stackLongs[i]);
            case 'S':
                return Short.toString((short) this.stackLongs[i]);
            case 'Z':
                return Boolean.toString(this.stackLongs[i] != 0);
            default:
                Object obj = this.stackObjects[i];
                if (z) {
                    return ADRTReflector.toObjectString(obj);
                }
                return ADRTReflector.toString(obj);
        }
    }

    public void sendFields(String str) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList<String> arrayList3 = new ArrayList<>();
        getPathToString(str, arrayList, arrayList2, arrayList3);
        getPathObjectFields(str, arrayList, arrayList2, arrayList3);
        ADRTSender.sendFields(ADRT.getPackageName(), str, arrayList, arrayList2, arrayList3);
    }

    private void getPathToString(String str, List<String> list, List<String> list2, ArrayList<String> arrayList) {
        Object obj;
        String[] accesses = getAccesses(str);
        if (accesses.length == 0) {
            list.add("");
            list2.add("<undefined>");
            arrayList.add("O");
            return;
        }
        int i = this.stackFramePos;
        while (true) {
            if (i > this.stackPos) {
                break;
            }
            if (!accesses[0].equals(this.stackVariables[i])) {
                i++;
            } else if (accesses.length == 1) {
                list.add("");
                list2.add(getVariableToString(i, true));
                arrayList.add(this.stackVariableTypes[i] == 'L' ? "O" : "V");
                return;
            } else if (this.stackVariableTypes[i] == 'L') {
                obj = this.stackObjects[i];
            }
        }
        obj = null;
        if (obj == null) {
            list.add("");
            list2.add("<undefined>");
            arrayList.add("O");
            return;
        }
        Object objectFieldValue = obj;
        for (int i2 = 1; i2 < accesses.length - 1; i2++) {
            objectFieldValue = ADRTReflector.getObjectFieldValue(objectFieldValue, accesses[i2]);
            if (objectFieldValue == null) {
                list.add("");
                list2.add("<undefined>");
                arrayList.add("O");
                return;
            }
        }
        ADRTReflector.getFieldValueToObjectString(objectFieldValue, accesses[accesses.length - 1], list, list2, arrayList);
    }

    private String[] getAccesses(String str) {
        String[] strArrSplit = str.split("\\.|\\[");
        String[] strArr = new String[strArrSplit.length];
        for (int i = 0; i < strArrSplit.length; i++) {
            String str2 = strArrSplit[i];
            if (str2.endsWith("]")) {
                strArr[i] = "[" + str2;
            } else {
                strArr[i] = str2;
            }
        }
        return strArr;
    }

    private void getPathObjectFields(String str, List<String> list, List<String> list2, ArrayList<String> arrayList) {
        String[] accesses = getAccesses(str);
        if (accesses.length != 0) {
            Object objectFieldValue = null;
            int i = this.stackFramePos;
            while (true) {
                if (i > this.stackPos) {
                    break;
                }
                if (!accesses[0].equals(this.stackVariables[i])) {
                    i++;
                } else if (this.stackVariableTypes[i] == 'L') {
                    objectFieldValue = this.stackObjects[i];
                }
            }
            if (objectFieldValue != null) {
                for (int i2 = 1; i2 < accesses.length; i2++) {
                    objectFieldValue = ADRTReflector.getObjectFieldValue(objectFieldValue, accesses[i2]);
                    if (objectFieldValue == null) {
                        return;
                    }
                }
                ADRTReflector.getFieldValues(objectFieldValue, list, list2, arrayList);
            }
        }
    }

    private void growStack() {
        String[] strArr = new String[this.stackVariables.length * 2];
        System.arraycopy(this.stackVariables, 0, strArr, 0, this.stackVariables.length);
        this.stackVariables = strArr;
        char[] cArr = new char[this.stackVariableTypes.length * 2];
        System.arraycopy(this.stackVariableTypes, 0, cArr, 0, this.stackVariableTypes.length);
        this.stackVariableTypes = cArr;
        Object[] objArr = new Object[this.stackObjects.length * 2];
        System.arraycopy(this.stackObjects, 0, objArr, 0, this.stackObjects.length);
        this.stackObjects = objArr;
        long[] jArr = new long[this.stackLongs.length * 2];
        System.arraycopy(this.stackLongs, 0, jArr, 0, this.stackLongs.length);
        this.stackLongs = jArr;
        float[] fArr = new float[this.stackFloats.length * 2];
        System.arraycopy(this.stackFloats, 0, fArr, 0, this.stackFloats.length);
        this.stackFloats = fArr;
        double[] dArr = new double[this.stackDoubles.length * 2];
        System.arraycopy(this.stackDoubles, 0, dArr, 0, this.stackDoubles.length);
        this.stackDoubles = dArr;
    }

    private void growFrames() {
        int[] iArr = new int[this.frameStackPoss.length * 2];
        System.arraycopy(this.frameStackPoss, 0, iArr, 0, this.frameStackPoss.length);
        this.frameStackPoss = iArr;
        long[] jArr = new long[this.frameFiles.length * 2];
        System.arraycopy(this.frameFiles, 0, jArr, 0, this.frameFiles.length);
        this.frameFiles = jArr;
    }

    public final void onVariableWrite(int i, int i2) {
        int i3 = this.stackFramePos + i;
        if (i3 > this.stackPos) {
            this.stackPos = i3;
        }
        if (i3 >= this.stackVariables.length) {
            growStack();
        }
        this.stackLongs[i3] = i2;
    }

    public final void onVariableWrite(int i, byte b2) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackLongs[i2] = b2;
    }

    public final void onVariableWrite(int i, short s) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackLongs[i2] = s;
    }

    public final void onVariableWrite(int i, char c2) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackLongs[i2] = c2;
    }

    public final void onVariableWrite(int i, boolean z) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackLongs[i2] = z ? 1L : 0L;
    }

    public final void onVariableWrite(int i, long j) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackLongs[i2] = j;
    }

    public final void onVariableWrite(int i, float f2) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackFloats[i2] = f2;
    }

    public final void onVariableWrite(int i, double d2) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackDoubles[i2] = d2;
    }

    public final void onVariableWrite(int i, Object obj) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackObjects[i2] = obj;
    }

    public final void onIntVariableDeclare(String str, int i) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i2] = 'I';
        this.stackVariables[i2] = str;
        this.stackLongs[i2] = 0;
    }

    public final void onByteVariableDeclare(String str, int i) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i2] = 'B';
        this.stackVariables[i2] = str;
        this.stackLongs[i2] = 0;
    }

    public final void onShortVariableDeclare(String str, int i) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i2] = 'S';
        this.stackVariables[i2] = str;
        this.stackLongs[i2] = 0;
    }

    public final void onCharVariableDeclare(String str, int i) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i2] = 'C';
        this.stackVariables[i2] = str;
        this.stackLongs[i2] = 0;
    }

    public final void onLongVariableDeclare(String str, int i) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i2] = 'J';
        this.stackVariables[i2] = str;
        this.stackLongs[i2] = 0;
    }

    public final void onBoolVariableDeclare(String str, int i) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i2] = 'Z';
        this.stackVariables[i2] = str;
        this.stackLongs[i2] = 0;
    }

    public final void onFloatVariableDeclare(String str, int i) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i2] = 'F';
        this.stackVariables[i2] = str;
        this.stackFloats[i2] = 0.0f;
    }

    public final void onDoubleVariableDeclare(String str, int i) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i2] = 'D';
        this.stackVariables[i2] = str;
        this.stackDoubles[i2] = 0.0d;
    }

    public final void onObjectVariableDeclare(String str, int i) {
        int i2 = this.stackFramePos + i;
        if (i2 > this.stackPos) {
            this.stackPos = i2;
        }
        if (i2 >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i2] = 'L';
        this.stackVariables[i2] = str;
        this.stackObjects[i2] = null;
    }

    public final void onThisAvailable(Object obj) {
        int i = this.stackFramePos + 0;
        if (i > this.stackPos) {
            this.stackPos = i;
        }
        if (i >= this.stackVariables.length) {
            growStack();
        }
        this.stackVariableTypes[i] = 'L';
        this.stackVariables[i] = "this";
        this.stackObjects[i] = obj;
    }

    public final void onMethodEnter(long j) {
        this.frameFiles[this.framePos] = this.currentFileId;
        if (j != this.currentFileId) {
            this.currentFileId = j;
            this.currentBreakpointLines = ADRT.getBreakpointLines(j);
            if (this.currentBreakpointLines == null) {
                this.currentBreakpointLines = BOOLEAN_EMPTY;
            }
        }
        this.frameStackPoss[this.framePos] = this.stackFramePos;
        this.stackFramePos = this.stackPos + 1;
        this.framePos++;
        if (this.framePos == this.stepStartFramePos) {
            this.stepFramePos = -1;
            this.stepStartFramePos = -1;
        }
        if (this.framePos >= this.frameFiles.length) {
            growFrames();
        }
    }

    public final void onStatementStart(int i) {
        if (this.stepFramePos == -1) {
            boolean[] zArr = this.currentBreakpointLines;
            if (zArr.length > i && zArr[i]) {
                onBreakpointHit(i);
                return;
            }
            return;
        }
        if (this.framePos <= this.stepFramePos) {
            onBreakpointHit(i);
        }
    }

    private void onBreakpointHit(int i) {
        if (ADRT.onBreakpointHit(this)) {
            sendBreakpointHit(i);
        }
        ADRT.suspendThread();
    }

    public final void onMethodExit() {
        for (int i = this.stackFramePos; i <= this.stackPos; i++) {
            this.stackVariables[i] = null;
            this.stackObjects[i] = null;
        }
        this.framePos--;
        this.stackPos = this.stackFramePos - 1;
        this.stackFramePos = this.frameStackPoss[this.framePos];
        long j = this.frameFiles[this.framePos];
        if (j != this.currentFileId) {
            this.currentFileId = j;
            this.currentBreakpointLines = ADRT.getBreakpointLines(j);
            if (this.currentBreakpointLines == null) {
                this.currentBreakpointLines = BOOLEAN_EMPTY;
            }
        }
    }
}
