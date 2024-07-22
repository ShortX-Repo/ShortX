package tornaco.apps.shortx.services.ui.floating;

/**
 * <b>Package:</b> com.zh.touchassistant.floating <br>
 * <b>FileName:</b> FloatWindowViewStateCallback <br>
 * <b>Create Date:</b> 2018/12/5  下午6:54 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public interface FloatWindowViewStateCallback {
    /**
     * 当悬浮窗位置更新时回调
     *
     * @param oldX 上一次的x坐标
     * @param oldY 上一次的y坐标
     * @param newX 新的x坐标
     * @param newY 新的y坐标
     */
    void onPositionUpdate(int oldX, int oldY, int newX, int newY);

    /**
     * 当显示时回调
     */
    void onShow();

    /**
     * 当隐藏时回调
     */
    void onHide();

    /**
     * 当被移除时回调
     */
    void onRemove();

    /**
     * 是否可以允许长按
     */
    boolean isCanLongPress();

    /**
     * 当长按时回调
     */
    void onLongPress();

    /**
     * 当准备拖动时回调
     */
    void onPrepareDrag();

    /**
     * 拽托中回调
     *
     * @param moveX 当前移动的X坐标
     * @param moveY 当前移动的Y坐标
     */
    void onDragging(float moveX, float moveY);

    /**
     * 当拽托结束时回调
     */
    void onDragFinish();

    /**
     * 是否允许拽托
     *
     * @param moveX 当前移动的X坐标
     * @param moveY 当前移动的Y坐标
     * @return 返回true标识允许拖动，false则不允许，默认允许
     */
    boolean isCanDrag(float moveX, float moveY);

    /**
     * 当移动动画开始时回调
     */
    void onMoveAnimStart();

    /**
     * 当移动动画结束时回调
     */
    void onMoveAnimEnd();

    /**
     * 当返回桌面时回调
     */
    void onBackToDesktop();

    /**
     * 当点击悬浮窗以外区域时回调
     */
    void onClickFloatOutsideArea(float x, float y);
}