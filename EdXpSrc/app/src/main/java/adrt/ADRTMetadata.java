package adrt;

/* JADX INFO: loaded from: classes.dex */
public class ADRTMetadata {
    public static int[][] FILE_CALLED_FILES;
    public static int[][] FILE_CALLING_FILES;
    public static int[] FILE_IDS = {133, 134, 135, 143, 144};
    public static String[] FILE_NAMES = {"cc/xposed/Mainyudao.java", "cc/xposed/MainActivity.java", "cc/xposed/qq.java", "cc/xposed/BuildConfig.java", "cc/xposed/R.java"};

    static {
        int[][] iArr = new int[5][];
        iArr[1] = new int[]{135};
        FILE_CALLED_FILES = iArr;
        FILE_CALLING_FILES = new int[5][];
    }
}
