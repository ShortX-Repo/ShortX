package tornaco.apps.shortx.services.ui.floating;

import java.util.ArrayList;

/**
 * <b>Package:</b> com.zh.touchassistant <br>
 * <b>FileName:</b> FloatViewLiveData <br>
 * <b>Create Date:</b> 2018/12/12  上午12:03 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class FloatViewLiveData {
    private ArrayList<OnDataChangeCallback> mCallbacks;
    private boolean isOpen;

    public interface OnDataChangeCallback {
        void onDataChange(boolean isOpen);
    }

    public void addOnDataChangeCallback(OnDataChangeCallback dataChangeCallback) {
        if (mCallbacks == null) {
            mCallbacks = new ArrayList<>();
        }
        this.mCallbacks.add(dataChangeCallback);
    }

    public void setValue(boolean newValue) {
        this.isOpen = newValue;
        if (mCallbacks != null) {
            for (OnDataChangeCallback callback : mCallbacks) {
                callback.onDataChange(isOpen);
            }
        }
    }

    public boolean isOpen() {
        return isOpen;
    }
}