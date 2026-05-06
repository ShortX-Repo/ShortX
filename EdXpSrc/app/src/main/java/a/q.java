package a;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class q {
    public static int a(float f2) {
        if (f2 == 0.0f) {
            return 0;
        }
        return Float.floatToIntBits(f2);
    }

    public static float b(int i) {
        if (i == 0) {
            return 0.0f;
        }
        return Float.intBitsToFloat(i);
    }

    public static int c(int i) {
        switch (i) {
            case 36:
                return 0;
            case 37:
                return 8;
            case 38:
                return 9;
            default:
                f.v.d(new IllegalArgumentException("Unknown flag: " + i));
                return i;
        }
    }
}
