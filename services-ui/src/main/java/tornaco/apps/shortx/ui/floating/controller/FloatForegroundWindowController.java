package tornaco.apps.shortx.ui.floating.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zh.touchassistant.R;
import com.zh.touchassistant.constant.AccessibilityConstant;
import com.zh.touchassistant.floating.FloatMoveEnum;
import com.zh.touchassistant.floating.FloatWindowOption;
import com.zh.touchassistant.model.ForegroundAppInfoModel;
import com.zh.touchassistant.util.AppBroadcastManager;
import com.zh.touchassistant.util.StatusBarUtil;

import tornaco.apps.shortx.ui.floating.FloatWindowManager;

/**
 * <b>Package:</b> com.zh.touchassistant.controller <br>
 * <b>FileName:</b> FloatForegroundWindowController <br>
 * <b>Create Date:</b> 2018/12/17  下午7:50 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class FloatForegroundWindowController extends BaseFloatWindowController {
    private static final String TAG_FOREGROUND = "foreground";

    private View mRootView;
    private FloatWindowManager mFloatWindowManager;

    public FloatForegroundWindowController(Context context, FloatWindowManager floatWindowManager) {
        super(context);
        this.mFloatWindowManager = floatWindowManager;
        init();
    }

    private void init() {
        this.mRootView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.float_foreground, null);
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getApplicationContext());
        this.mRootView.setPadding(mRootView.getPaddingLeft(),
                mRootView.getTop() + statusBarHeight,
                mRootView.getPaddingRight(),
                mRootView.getPaddingBottom());
        attachFloatWindow();
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //当前Activity切换
                if (mRootView != null) {
                    TextView foregroundAppPackageName = mRootView.findViewById(R.id.foreground_app_package_name);
                    TextView foregroundAppActivityName = mRootView.findViewById(R.id.foreground_app_activity_name);
                    ForegroundAppInfoModel model = (ForegroundAppInfoModel) intent.getSerializableExtra(AccessibilityConstant.Extras.EXTRAS_FOREGROUND_APP_DATA);
                    foregroundAppPackageName.setText(model.getForegroundAppPackageName());
                    foregroundAppActivityName.setText(model.getForegroundActivityClassName());
                }
            }
        };
        mRootView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                AppBroadcastManager
                        .registerReceiver(getApplicationContext(),
                                receiver, AccessibilityConstant.Action.ACTION_FOREGROUND_APP_CHANGE);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                AppBroadcastManager
                        .unregisterReceiver(getApplicationContext(), receiver);
            }
        });
    }

    private void attachFloatWindow() {
        mFloatWindowManager.makeFloatWindow(mRootView, TAG_FOREGROUND, FloatWindowOption
                .create(new FloatWindowOption
                        .Builder()
                        .setX(0)
                        .setY(0)
                        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setShow(AccessibilityConstant.isDebug)
                        //设置无需接收点击事件，否则点击事件无法穿透到底下的Window
                        .setNotTouchable(true)
                        .setFloatMoveType(FloatMoveEnum.INACTIVE)
                        .desktopShow(true)));
    }

    public void showFloatWindow() {
        if (!AccessibilityConstant.isDebug) {
            this.mFloatWindowManager
                    .getFloatWindow(TAG_FOREGROUND)
                    .show();
        }
    }

    public void hideFloatWindow() {
        if (!AccessibilityConstant.isDebug) {
            this.mFloatWindowManager
                    .getFloatWindow(TAG_FOREGROUND)
                    .hide();
        }
    }

    @Override
    public View getView() {
        return mRootView;
    }
}
