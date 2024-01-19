package tornaco.apps.shortx.services.ui.pie;

import android.graphics.drawable.Drawable;


public class AppMenu extends CanvasPieMenu {
    public static class AppIcon extends CanvasPieMenu.CanvasIcon {
        public final String label;

        public AppIcon(String label, Drawable icon) {
            super(Converter.getBitmapFromDrawable(icon));
            this.label = label;
        }
    }
}
