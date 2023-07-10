package tornaco.apps.shortx.services.audio.cast;

import java.util.ArrayList;
import java.util.List;

public abstract class ValidResolutions {

    public static final int INDEX_MASK_AUTO = 0;

    public static final String DESC[] = {
            "AUTO",
            // CEA Resolutions
            "640x480",
            "720x480",
            "720x576",
            "1280x720",
            "1920x1080",
            "2560x1440",
            // VESA Resolutions
            "800x600",
            "1024x768",
            "1152x864",
            "1280x768",
            "1280x800",
            "1360x768",
            "1366x768",
            "1280x1024",
            " 1400x1050 ",
            " 1440x900 ",
            " 1600x900 ",
            "1600x1200",
            "1680x1024",
            "1680x1050",
            "1920x1200",
            // HH Resolutions
            "800x480",
            "854x480",
            "864x480",
            "640x360",
            "960x540",
            "848x480"
    };

    public static final int $[][] = {
            // MASK
            {0, 0},
            // CEA Resolutions
            {640, 480},
            {720, 480},
            {720, 576},
            {1280, 720},
            {1920, 1080},
            {2560, 1440},
            // VESA Resolutions
            {800, 600},
            {1024, 768},
            {1152, 864},
            {1280, 768},
            {1280, 800},
            {1360, 768},
            {1366, 768},
            {1280, 1024},
            {1400, 1050},
            {1440, 900},
            {1600, 900},
            {1600, 1200},
            {1680, 1024},
            {1680, 1050},
            {1920, 1200},
            // HH Resolutions
            {800, 480},
            {854, 480},
            {864, 480},
            {640, 360},
            {960, 540},
            {848, 480}
    };

    public static int indexOf(String res) {
        for (int i = 0; i < DESC.length; i++) {
            if (DESC[i].equalsIgnoreCase(res.trim())) {
                return i;
            }
        }
        return INDEX_MASK_AUTO;
    }

    public static List<String> string() {
        List<String> out = new ArrayList<>();
        for (int[] re : $) {
            out.add(re[0] + "x" + re[1]);
        }
        return out;
    }
}
