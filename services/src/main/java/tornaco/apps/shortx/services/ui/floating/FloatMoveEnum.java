package tornaco.apps.shortx.services.ui.floating;

/**
 * <b>Package:</b> com.zh.touchassistant.floating <br>
 * <b>FileName:</b> FloatMoveEnum <br>
 * <b>Create Date:</b> 2018/12/5  下午6:36 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public enum FloatMoveEnum {
    /**
     * 不可拖动
     */
    INACTIVE(1, "不可拖动"),
    /**
     * 可拖动
     */
    ACTIVE(2, "可拖动"),
    /**
     * 边界回弹
     */
    SLIDE(3, "边界回弹");

    private int mCode;
    private String mDesc;

    FloatMoveEnum(int code, String desc) {
        this.mCode = code;
        this.mDesc = desc;
    }

    public int getCode() {
        return mCode;
    }

    public String getDesc() {
        return mDesc;
    }

    @Override
    public String toString() {
        return this.mDesc;
    }
}