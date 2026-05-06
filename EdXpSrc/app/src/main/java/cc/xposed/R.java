package cc.xposed;

import adrt.ADRT;
import adrt.ADRTThread;

/* JADX INFO: loaded from: classes.dex */
public final class R {
    private static boolean adrt$enabled;

    /* JADX INFO: Added by JADX */
    public static final class anim {

        /* JADX INFO: Added by JADX */
        public static final int fade_out = 0x7f010000;

        /* JADX INFO: Added by JADX */
        public static final int slide_in_bottom = 0x7f010001;

        /* JADX INFO: Added by JADX */
        public static final int slide_in_left = 0x7f010002;

        /* JADX INFO: Added by JADX */
        public static final int slide_in_right = 0x7f010003;

        /* JADX INFO: Added by JADX */
        public static final int slide_in_top = 0x7f010004;

        /* JADX INFO: Added by JADX */
        public static final int slide_out_bottom = 0x7f010005;

        /* JADX INFO: Added by JADX */
        public static final int slide_out_left = 0x7f010006;

        /* JADX INFO: Added by JADX */
        public static final int slide_out_right = 0x7f010007;

        /* JADX INFO: Added by JADX */
        public static final int slide_out_top = 0x7f010008;
    }

    /* JADX INFO: Added by JADX */
    public static final class array {

        /* JADX INFO: Added by JADX */
        public static final int colors = 0x7f020000;

        /* JADX INFO: Added by JADX */
        public static final int xposed_scope = 0x7f020001;
    }

    /* JADX INFO: Added by JADX */
    public static final class color {

        /* JADX INFO: Added by JADX */
        public static final int colorAccent = 0x7f030000;

        /* JADX INFO: Added by JADX */
        public static final int colorBackgroundDark = 0x7f030001;

        /* JADX INFO: Added by JADX */
        public static final int colorBackgroundLightGray = 0x7f030002;

        /* JADX INFO: Added by JADX */
        public static final int colorPrimary = 0x7f030003;

        /* JADX INFO: Added by JADX */
        public static final int colorPrimaryDark = 0x7f030004;

        /* JADX INFO: Added by JADX */
        public static final int colorPrimaryLight = 0x7f030005;

        /* JADX INFO: Added by JADX */
        public static final int ic_launcher_background = 0x7f030006;
    }

    /* JADX INFO: Added by JADX */
    public static final class mipmap {

        /* JADX INFO: Added by JADX */
        public static final int ic_launcher = 0x7f050000;

        /* JADX INFO: Added by JADX */
        public static final int ic_launcher_round = 0x7f050001;
    }

    static {
        ADRT.onClassLoad(144L, "cc.xposed.R");
    }

    public static final class attr {
        private static boolean adrt$enabled;

        static {
            ADRT.onClassLoad(144L, "cc.xposed.R$attr");
        }

        public attr() {
            if (adrt$enabled) {
                ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(144L);
                try {
                    aDRTThreadOnMethodEnter.onStatementStart(11);
                    aDRTThreadOnMethodEnter.onThisAvailable(this);
                    aDRTThreadOnMethodEnter.onStatementStart(12);
                } finally {
                    aDRTThreadOnMethodEnter.onMethodExit();
                }
            }
        }
    }

    public static final class drawable {
        private static boolean adrt$enabled = false;
        public static final int tb = 0x7f020000;

        /* JADX INFO: Added by JADX */
        public static final int ic_abort = 0x7f040000;

        /* JADX INFO: Added by JADX */
        public static final int ic_about = 0x7f040001;

        /* JADX INFO: Added by JADX */
        public static final int ic_action_bar_help = 0x7f040002;

        /* JADX INFO: Added by JADX */
        public static final int ic_action_collection = 0x7f040003;

        /* JADX INFO: Added by JADX */
        public static final int ic_activity = 0x7f040004;

        /* JADX INFO: Added by JADX */
        public static final int ic_add = 0x7f040005;

        /* JADX INFO: Added by JADX */
        public static final int ic_add_circle = 0x7f040006;

        /* JADX INFO: Added by JADX */
        public static final int ic_adjust = 0x7f040007;

        /* JADX INFO: Added by JADX */
        public static final int ic_airplane_mode = 0x7f040008;

        /* JADX INFO: Added by JADX */
        public static final int ic_app = 0x7f040009;

        /* JADX INFO: Added by JADX */
        public static final int ic_app_option_menu = 0x7f04000a;

        /* JADX INFO: Added by JADX */
        public static final int ic_apps_panel = 0x7f04000b;

        /* JADX INFO: Added by JADX */
        public static final int ic_arrow_down = 0x7f04000c;

        /* JADX INFO: Added by JADX */
        public static final int ic_arrow_up = 0x7f04000d;

        /* JADX INFO: Added by JADX */
        public static final int ic_as_default = 0x7f04000e;

        /* JADX INFO: Added by JADX */
        public static final int ic_auto_brightness = 0x7f04000f;

        /* JADX INFO: Added by JADX */
        public static final int ic_auto_rotation = 0x7f040010;

        /* JADX INFO: Added by JADX */
        public static final int ic_back = 0x7f040011;

        /* JADX INFO: Added by JADX */
        public static final int ic_backup_restore = 0x7f040012;

        /* JADX INFO: Added by JADX */
        public static final int ic_bluetooth = 0x7f040013;

        /* JADX INFO: Added by JADX */
        public static final int ic_brightness_down = 0x7f040014;

        /* JADX INFO: Added by JADX */
        public static final int ic_brightness_up = 0x7f040015;

        /* JADX INFO: Added by JADX */
        public static final int ic_caret = 0x7f040016;

        /* JADX INFO: Added by JADX */
        public static final int ic_clear_background_apps = 0x7f040017;

        /* JADX INFO: Added by JADX */
        public static final int ic_clipboard = 0x7f040018;

        /* JADX INFO: Added by JADX */
        public static final int ic_clips_add = 0x7f040019;

        /* JADX INFO: Added by JADX */
        public static final int ic_clips_clear_all = 0x7f04001a;

        /* JADX INFO: Added by JADX */
        public static final int ic_clips_edit = 0x7f04001b;

        /* JADX INFO: Added by JADX */
        public static final int ic_clips_sticky = 0x7f04001c;

        /* JADX INFO: Added by JADX */
        public static final int ic_clips_sticky_small = 0x7f04001d;

        /* JADX INFO: Added by JADX */
        public static final int ic_collect_activity = 0x7f04001e;

        /* JADX INFO: Added by JADX */
        public static final int ic_collect_activity_with_params = 0x7f04001f;

        /* JADX INFO: Added by JADX */
        public static final int ic_command = 0x7f040020;

        /* JADX INFO: Added by JADX */
        public static final int ic_conditional = 0x7f040021;

        /* JADX INFO: Added by JADX */
        public static final int ic_conditional_half = 0x7f040022;

        /* JADX INFO: Added by JADX */
        public static final int ic_content = 0x7f040023;

        /* JADX INFO: Added by JADX */
        public static final int ic_content_copy = 0x7f040024;

        /* JADX INFO: Added by JADX */
        public static final int ic_content_cut = 0x7f040025;

        /* JADX INFO: Added by JADX */
        public static final int ic_content_paste = 0x7f040026;

        /* JADX INFO: Added by JADX */
        public static final int ic_content_redo = 0x7f040027;

        /* JADX INFO: Added by JADX */
        public static final int ic_content_select_all = 0x7f040028;

        /* JADX INFO: Added by JADX */
        public static final int ic_content_undo = 0x7f040029;

        /* JADX INFO: Added by JADX */
        public static final int ic_custom_panel = 0x7f04002a;

        /* JADX INFO: Added by JADX */
        public static final int ic_delay = 0x7f04002b;

        /* JADX INFO: Added by JADX */
        public static final int ic_delete = 0x7f04002c;

        /* JADX INFO: Added by JADX */
        public static final int ic_dismiss_keyguard = 0x7f04002d;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_bottom = 0x7f04002e;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_bottom_center = 0x7f04002f;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_bottom_left = 0x7f040030;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_bottom_right = 0x7f040031;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_left = 0x7f040032;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_left_bottom = 0x7f040033;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_left_center = 0x7f040034;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_left_top = 0x7f040035;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_right = 0x7f040036;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_right_bottom = 0x7f040037;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_right_center = 0x7f040038;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_right_top = 0x7f040039;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_top = 0x7f04003a;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_top_center = 0x7f04003b;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_top_left = 0x7f04003c;

        /* JADX INFO: Added by JADX */
        public static final int ic_edge_top_right = 0x7f04003d;

        /* JADX INFO: Added by JADX */
        public static final int ic_edit = 0x7f04003e;

        /* JADX INFO: Added by JADX */
        public static final int ic_edit_back = 0x7f04003f;

        /* JADX INFO: Added by JADX */
        public static final int ic_edit_done = 0x7f040040;

        /* JADX INFO: Added by JADX */
        public static final int ic_fast_scroll = 0x7f040041;

        /* JADX INFO: Added by JADX */
        public static final int ic_finish_activity = 0x7f040042;

        /* JADX INFO: Added by JADX */
        public static final int ic_floating_widget = 0x7f040043;

        /* JADX INFO: Added by JADX */
        public static final int ic_freezer = 0x7f040044;

        /* JADX INFO: Added by JADX */
        public static final int ic_freezer_panel = 0x7f040045;

        /* JADX INFO: Added by JADX */
        public static final int ic_game_mode = 0x7f040046;

        /* JADX INFO: Added by JADX */
        public static final int ic_gesture = 0x7f040047;

        /* JADX INFO: Added by JADX */
        public static final int ic_gesture_enabled = 0x7f040048;

        /* JADX INFO: Added by JADX */
        public static final int ic_gesture_pointer = 0x7f040049;

        /* JADX INFO: Added by JADX */
        public static final int ic_gesture_record = 0x7f04004a;

        /* JADX INFO: Added by JADX */
        public static final int ic_gesture_records = 0x7f04004b;

        /* JADX INFO: Added by JADX */
        public static final int ic_get_pro_version = 0x7f04004c;

        /* JADX INFO: Added by JADX */
        public static final int ic_help = 0x7f04004d;

        /* JADX INFO: Added by JADX */
        public static final int ic_hidden_apps = 0x7f04004e;

        /* JADX INFO: Added by JADX */
        public static final int ic_hide_soft_keyboard = 0x7f04004f;

        /* JADX INFO: Added by JADX */
        public static final int ic_home = 0x7f040050;

        /* JADX INFO: Added by JADX */
        public static final int ic_immersive_nav_bar = 0x7f040051;

        /* JADX INFO: Added by JADX */
        public static final int ic_immersive_status_bar = 0x7f040052;

        /* JADX INFO: Added by JADX */
        public static final int ic_inject_key_event = 0x7f040053;

        /* JADX INFO: Added by JADX */
        public static final int ic_inject_text = 0x7f040054;

        /* JADX INFO: Added by JADX */
        public static final int ic_input_method_menu = 0x7f040055;

        /* JADX INFO: Added by JADX */
        public static final int ic_keep_screen_on = 0x7f040056;

        /* JADX INFO: Added by JADX */
        public static final int ic_key_enabled = 0x7f040057;

        /* JADX INFO: Added by JADX */
        public static final int ic_kill = 0x7f040058;

        /* JADX INFO: Added by JADX */
        public static final int ic_kill_app = 0x7f040059;

        /* JADX INFO: Added by JADX */
        public static final int ic_kill_custom_app = 0x7f04005a;

        /* JADX INFO: Added by JADX */
        public static final int ic_last_app = 0x7f04005b;

        /* JADX INFO: Added by JADX */
        public static final int ic_last_input_method = 0x7f04005c;

        /* JADX INFO: Added by JADX */
        public static final int ic_launcher = 0x7f04005d;

        /* JADX INFO: Added by JADX */
        public static final int ic_launcher_foreground = 0x7f04005e;

        /* JADX INFO: Added by JADX */
        public static final int ic_left_side_bar = 0x7f04005f;

        /* JADX INFO: Added by JADX */
        public static final int ic_location = 0x7f040060;

        /* JADX INFO: Added by JADX */
        public static final int ic_media_next = 0x7f040061;

        /* JADX INFO: Added by JADX */
        public static final int ic_media_play = 0x7f040062;

        /* JADX INFO: Added by JADX */
        public static final int ic_media_play_pause = 0x7f040063;

        /* JADX INFO: Added by JADX */
        public static final int ic_media_previous = 0x7f040064;

        /* JADX INFO: Added by JADX */
        public static final int ic_media_stop = 0x7f040065;

        /* JADX INFO: Added by JADX */
        public static final int ic_menu = 0x7f040066;

        /* JADX INFO: Added by JADX */
        public static final int ic_mobile_data = 0x7f040067;

        /* JADX INFO: Added by JADX */
        public static final int ic_more = 0x7f040068;

        /* JADX INFO: Added by JADX */
        public static final int ic_more_triggers = 0x7f040069;

        /* JADX INFO: Added by JADX */
        public static final int ic_multi_action = 0x7f04006a;

        /* JADX INFO: Added by JADX */
        public static final int ic_next_app = 0x7f04006b;

        /* JADX INFO: Added by JADX */
        public static final int ic_nfc = 0x7f04006c;

        /* JADX INFO: Added by JADX */
        public static final int ic_none = 0x7f04006d;

        /* JADX INFO: Added by JADX */
        public static final int ic_notifications_panel = 0x7f04006e;

        /* JADX INFO: Added by JADX */
        public static final int ic_notify = 0x7f04006f;

        /* JADX INFO: Added by JADX */
        public static final int ic_off_corner = 0x7f040070;

        /* JADX INFO: Added by JADX */
        public static final int ic_ok = 0x7f040071;

        /* JADX INFO: Added by JADX */
        public static final int ic_on_corner = 0x7f040072;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_arrow = 0x7f040073;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_arrow_fill = 0x7f040074;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_close = 0x7f040075;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_continue_gesture = 0x7f040076;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_eraser = 0x7f040077;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_gesture = 0x7f040078;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_hide = 0x7f040079;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_line = 0x7f04007a;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_more = 0x7f04007b;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_pause_gesture = 0x7f04007c;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_rect = 0x7f04007d;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_rect_fill = 0x7f04007e;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_redo = 0x7f04007f;

        /* JADX INFO: Added by JADX */
        public static final int ic_painter_undo = 0x7f040080;

        /* JADX INFO: Added by JADX */
        public static final int ic_pen = 0x7f040081;

        /* JADX INFO: Added by JADX */
        public static final int ic_pie = 0x7f040082;

        /* JADX INFO: Added by JADX */
        public static final int ic_play = 0x7f040083;

        /* JADX INFO: Added by JADX */
        public static final int ic_play_sound = 0x7f040084;

        /* JADX INFO: Added by JADX */
        public static final int ic_play_sound_repeat = 0x7f040085;

        /* JADX INFO: Added by JADX */
        public static final int ic_play_store = 0x7f040086;

        /* JADX INFO: Added by JADX */
        public static final int ic_power_menu = 0x7f040087;

        /* JADX INFO: Added by JADX */
        public static final int ic_preferences = 0x7f040088;

        /* JADX INFO: Added by JADX */
        public static final int ic_previous_app = 0x7f040089;

        /* JADX INFO: Added by JADX */
        public static final int ic_quick_settings_panel = 0x7f04008a;

        /* JADX INFO: Added by JADX */
        public static final int ic_quick_settings_tiles = 0x7f04008b;

        /* JADX INFO: Added by JADX */
        public static final int ic_rate = 0x7f04008c;

        /* JADX INFO: Added by JADX */
        public static final int ic_read_clipboard = 0x7f04008d;

        /* JADX INFO: Added by JADX */
        public static final int ic_reboot = 0x7f04008e;

        /* JADX INFO: Added by JADX */
        public static final int ic_recent_apps = 0x7f04008f;

        /* JADX INFO: Added by JADX */
        public static final int ic_refreeze = 0x7f040090;

        /* JADX INFO: Added by JADX */
        public static final int ic_repeat = 0x7f040091;

        /* JADX INFO: Added by JADX */
        public static final int ic_repeat_corner = 0x7f040092;

        /* JADX INFO: Added by JADX */
        public static final int ic_right_side_bar = 0x7f040093;

        /* JADX INFO: Added by JADX */
        public static final int ic_ringer_mode = 0x7f040094;

        /* JADX INFO: Added by JADX */
        public static final int ic_ringer_mode_silent = 0x7f040095;

        /* JADX INFO: Added by JADX */
        public static final int ic_ringer_mode_vibrate = 0x7f040096;

        /* JADX INFO: Added by JADX */
        public static final int ic_rotate_landscape = 0x7f040097;

        /* JADX INFO: Added by JADX */
        public static final int ic_rotate_portrait = 0x7f040098;

        /* JADX INFO: Added by JADX */
        public static final int ic_rotate_screen = 0x7f040099;

        /* JADX INFO: Added by JADX */
        public static final int ic_running_apps_panel = 0x7f04009a;

        /* JADX INFO: Added by JADX */
        public static final int ic_save = 0x7f04009b;

        /* JADX INFO: Added by JADX */
        public static final int ic_save_small = 0x7f04009c;

        /* JADX INFO: Added by JADX */
        public static final int ic_saved_multi_action = 0x7f04009d;

        /* JADX INFO: Added by JADX */
        public static final int ic_schedule = 0x7f04009e;

        /* JADX INFO: Added by JADX */
        public static final int ic_screen_filter = 0x7f04009f;

        /* JADX INFO: Added by JADX */
        public static final int ic_screenshot = 0x7f0400a0;

        /* JADX INFO: Added by JADX */
        public static final int ic_screenshot_selected_region = 0x7f0400a1;

        /* JADX INFO: Added by JADX */
        public static final int ic_scroll_to_bottom = 0x7f0400a2;

        /* JADX INFO: Added by JADX */
        public static final int ic_scroll_to_top = 0x7f0400a3;

        /* JADX INFO: Added by JADX */
        public static final int ic_select = 0x7f0400a4;

        /* JADX INFO: Added by JADX */
        public static final int ic_set_variable = 0x7f0400a5;

        /* JADX INFO: Added by JADX */
        public static final int ic_share = 0x7f0400a6;

        /* JADX INFO: Added by JADX */
        public static final int ic_shortcut = 0x7f0400a7;

        /* JADX INFO: Added by JADX */
        public static final int ic_shortcut_toggle_gesture = 0x7f0400a8;

        /* JADX INFO: Added by JADX */
        public static final int ic_shortcut_toggle_key = 0x7f0400a9;

        /* JADX INFO: Added by JADX */
        public static final int ic_shortcuts_panel = 0x7f0400aa;

        /* JADX INFO: Added by JADX */
        public static final int ic_show_soft_keyboard = 0x7f0400ab;

        /* JADX INFO: Added by JADX */
        public static final int ic_shut_down = 0x7f0400ac;

        /* JADX INFO: Added by JADX */
        public static final int ic_sleep_wake = 0x7f0400ad;

        /* JADX INFO: Added by JADX */
        public static final int ic_soft_keyboard = 0x7f0400ae;

        /* JADX INFO: Added by JADX */
        public static final int ic_soft_reboot = 0x7f0400af;

        /* JADX INFO: Added by JADX */
        public static final int ic_speech = 0x7f0400b0;

        /* JADX INFO: Added by JADX */
        public static final int ic_split_screen = 0x7f0400b1;

        /* JADX INFO: Added by JADX */
        public static final int ic_stop_all = 0x7f0400b2;

        /* JADX INFO: Added by JADX */
        public static final int ic_stop_play_sound = 0x7f0400b3;

        /* JADX INFO: Added by JADX */
        public static final int ic_stop_speech = 0x7f0400b4;

        /* JADX INFO: Added by JADX */
        public static final int ic_sub_gesture = 0x7f0400b5;

        /* JADX INFO: Added by JADX */
        public static final int ic_successive_adjust = 0x7f0400b6;

        /* JADX INFO: Added by JADX */
        public static final int ic_support = 0x7f0400b7;

        /* JADX INFO: Added by JADX */
        public static final int ic_swipe_adjust = 0x7f0400b8;

        /* JADX INFO: Added by JADX */
        public static final int ic_swipe_adjust_brightness = 0x7f0400b9;

        /* JADX INFO: Added by JADX */
        public static final int ic_swipe_adjust_screen_filter = 0x7f0400ba;

        /* JADX INFO: Added by JADX */
        public static final int ic_swipe_adjust_volume = 0x7f0400bb;

        /* JADX INFO: Added by JADX */
        public static final int ic_sync = 0x7f0400bc;

        /* JADX INFO: Added by JADX */
        public static final int ic_toast = 0x7f0400bd;

        /* JADX INFO: Added by JADX */
        public static final int ic_torch = 0x7f0400be;

        /* JADX INFO: Added by JADX */
        public static final int ic_translate = 0x7f0400bf;

        /* JADX INFO: Added by JADX */
        public static final int ic_tutorial_online = 0x7f0400c0;

        /* JADX INFO: Added by JADX */
        public static final int ic_unfreeze = 0x7f0400c1;

        /* JADX INFO: Added by JADX */
        public static final int ic_universal_copy = 0x7f0400c2;

        /* JADX INFO: Added by JADX */
        public static final int ic_vibrate = 0x7f0400c3;

        /* JADX INFO: Added by JADX */
        public static final int ic_visibility = 0x7f0400c4;

        /* JADX INFO: Added by JADX */
        public static final int ic_volume_down = 0x7f0400c5;

        /* JADX INFO: Added by JADX */
        public static final int ic_volume_mute = 0x7f0400c6;

        /* JADX INFO: Added by JADX */
        public static final int ic_volume_up = 0x7f0400c7;

        /* JADX INFO: Added by JADX */
        public static final int ic_wifi = 0x7f0400c8;

        /* JADX INFO: Added by JADX */
        public static final int ic_wifi_ap = 0x7f0400c9;

        static {
            ADRT.onClassLoad(144L, "cc.xposed.R$drawable");
        }

        public drawable() {
            if (adrt$enabled) {
                ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(144L);
                try {
                    aDRTThreadOnMethodEnter.onStatementStart(14);
                    aDRTThreadOnMethodEnter.onThisAvailable(this);
                    aDRTThreadOnMethodEnter.onStatementStart(15);
                } finally {
                    aDRTThreadOnMethodEnter.onMethodExit();
                }
            }
        }
    }

    public static final class layout {
        private static boolean adrt$enabled = false;
        public static final int main = 0x7f030000;

        static {
            ADRT.onClassLoad(144L, "cc.xposed.R$layout");
        }

        public layout() {
            if (adrt$enabled) {
                ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(144L);
                try {
                    aDRTThreadOnMethodEnter.onStatementStart(17);
                    aDRTThreadOnMethodEnter.onThisAvailable(this);
                    aDRTThreadOnMethodEnter.onStatementStart(18);
                } finally {
                    aDRTThreadOnMethodEnter.onMethodExit();
                }
            }
        }
    }

    public static final class string {
        private static boolean adrt$enabled = false;
        public static final int app_name = 0x7f040000;

        /* JADX INFO: Added by JADX */
        public static final int about = 0x7f060000;

        /* JADX INFO: Added by JADX */
        public static final int about_description = 0x7f060001;

        /* JADX INFO: Added by JADX */
        public static final int action = 0x7f060002;

        /* JADX INFO: Added by JADX */
        public static final int action_abort = 0x7f060003;

        /* JADX INFO: Added by JADX */
        public static final int action_action_collection = 0x7f060004;

        /* JADX INFO: Added by JADX */
        public static final int action_activity = 0x7f060005;

        /* JADX INFO: Added by JADX */
        public static final int action_adjust = 0x7f060006;

        /* JADX INFO: Added by JADX */
        public static final int action_adjust_volume = 0x7f060007;

        /* JADX INFO: Added by JADX */
        public static final int action_airplane_mode = 0x7f060008;

        /* JADX INFO: Added by JADX */
        public static final int action_app = 0x7f060009;

        /* JADX INFO: Added by JADX */
        public static final int action_app_option_menu = 0x7f06000a;

        /* JADX INFO: Added by JADX */
        public static final int action_apps_drawer = 0x7f06000b;

        /* JADX INFO: Added by JADX */
        public static final int action_as_default = 0x7f06000c;

        /* JADX INFO: Added by JADX */
        public static final int action_auto_brightness = 0x7f06000d;

        /* JADX INFO: Added by JADX */
        public static final int action_auto_rotation = 0x7f06000e;

        /* JADX INFO: Added by JADX */
        public static final int action_back = 0x7f06000f;

        /* JADX INFO: Added by JADX */
        public static final int action_bluetooth = 0x7f060010;

        /* JADX INFO: Added by JADX */
        public static final int action_brightness_down = 0x7f060011;

        /* JADX INFO: Added by JADX */
        public static final int action_brightness_up = 0x7f060012;

        /* JADX INFO: Added by JADX */
        public static final int action_clear_background_apps = 0x7f060013;

        /* JADX INFO: Added by JADX */
        public static final int action_clipboard = 0x7f060014;

        /* JADX INFO: Added by JADX */
        public static final int action_collection = 0x7f060015;

        /* JADX INFO: Added by JADX */
        public static final int action_command = 0x7f060016;

        /* JADX INFO: Added by JADX */
        public static final int action_conditional = 0x7f060017;

        /* JADX INFO: Added by JADX */
        public static final int action_content = 0x7f060018;

        /* JADX INFO: Added by JADX */
        public static final int action_custom_panel = 0x7f060019;

        /* JADX INFO: Added by JADX */
        public static final int action_delay = 0x7f06001a;

        /* JADX INFO: Added by JADX */
        public static final int action_dismiss_keyguard = 0x7f06001b;

        /* JADX INFO: Added by JADX */
        public static final int action_editable_action = 0x7f06001c;

        /* JADX INFO: Added by JADX */
        public static final int action_expand_notifications_panel = 0x7f06001d;

        /* JADX INFO: Added by JADX */
        public static final int action_expand_quick_settings_panel = 0x7f06001e;

        /* JADX INFO: Added by JADX */
        public static final int action_fast_scroll = 0x7f06001f;

        /* JADX INFO: Added by JADX */
        public static final int action_finish_activity = 0x7f060020;

        /* JADX INFO: Added by JADX */
        public static final int action_floating_widget = 0x7f060021;

        /* JADX INFO: Added by JADX */
        public static final int action_freezer_drawer = 0x7f060022;

        /* JADX INFO: Added by JADX */
        public static final int action_game_mode = 0x7f060023;

        /* JADX INFO: Added by JADX */
        public static final int action_gesture_enabled = 0x7f060024;

        /* JADX INFO: Added by JADX */
        public static final int action_gesture_pointer = 0x7f060025;

        /* JADX INFO: Added by JADX */
        public static final int action_gesture_record = 0x7f060026;

        /* JADX INFO: Added by JADX */
        public static final int action_home = 0x7f060027;

        /* JADX INFO: Added by JADX */
        public static final int action_immersive_nav_bar = 0x7f060028;

        /* JADX INFO: Added by JADX */
        public static final int action_immersive_status_bar = 0x7f060029;

        /* JADX INFO: Added by JADX */
        public static final int action_info = 0x7f06002a;

        /* JADX INFO: Added by JADX */
        public static final int action_inject_gesture = 0x7f06002b;

        /* JADX INFO: Added by JADX */
        public static final int action_inject_key_event = 0x7f06002c;

        /* JADX INFO: Added by JADX */
        public static final int action_inject_text = 0x7f06002d;

        /* JADX INFO: Added by JADX */
        public static final int action_keep_screen_on = 0x7f06002e;

        /* JADX INFO: Added by JADX */
        public static final int action_key_enabled = 0x7f06002f;

        /* JADX INFO: Added by JADX */
        public static final int action_kill_app = 0x7f060030;

        /* JADX INFO: Added by JADX */
        public static final int action_kill_foreground_app = 0x7f060031;

        /* JADX INFO: Added by JADX */
        public static final int action_last_app = 0x7f060032;

        /* JADX INFO: Added by JADX */
        public static final int action_location = 0x7f060033;

        /* JADX INFO: Added by JADX */
        public static final int action_media_control = 0x7f060034;

        /* JADX INFO: Added by JADX */
        public static final int action_media_next = 0x7f060035;

        /* JADX INFO: Added by JADX */
        public static final int action_media_play = 0x7f060036;

        /* JADX INFO: Added by JADX */
        public static final int action_media_play_pause = 0x7f060037;

        /* JADX INFO: Added by JADX */
        public static final int action_media_previous = 0x7f060038;

        /* JADX INFO: Added by JADX */
        public static final int action_media_stop = 0x7f060039;

        /* JADX INFO: Added by JADX */
        public static final int action_menu = 0x7f06003a;

        /* JADX INFO: Added by JADX */
        public static final int action_mobile_data = 0x7f06003b;

        /* JADX INFO: Added by JADX */
        public static final int action_multi_action = 0x7f06003c;

        /* JADX INFO: Added by JADX */
        public static final int action_next_app = 0x7f06003d;

        /* JADX INFO: Added by JADX */
        public static final int action_nfc = 0x7f06003e;

        /* JADX INFO: Added by JADX */
        public static final int action_none = 0x7f06003f;

        /* JADX INFO: Added by JADX */
        public static final int action_notify = 0x7f060040;

        /* JADX INFO: Added by JADX */
        public static final int action_pen = 0x7f060041;

        /* JADX INFO: Added by JADX */
        public static final int action_pie = 0x7f060042;

        /* JADX INFO: Added by JADX */
        public static final int action_play_sound = 0x7f060043;

        /* JADX INFO: Added by JADX */
        public static final int action_power_menu = 0x7f060044;

        /* JADX INFO: Added by JADX */
        public static final int action_previous_app = 0x7f060045;

        /* JADX INFO: Added by JADX */
        public static final int action_read_clipboard = 0x7f060046;

        /* JADX INFO: Added by JADX */
        public static final int action_reboot = 0x7f060047;

        /* JADX INFO: Added by JADX */
        public static final int action_recent_apps = 0x7f060048;

        /* JADX INFO: Added by JADX */
        public static final int action_refreeze = 0x7f060049;

        /* JADX INFO: Added by JADX */
        public static final int action_repeat = 0x7f06004a;

        /* JADX INFO: Added by JADX */
        public static final int action_ringer_mode = 0x7f06004b;

        /* JADX INFO: Added by JADX */
        public static final int action_rotate_screen = 0x7f06004c;

        /* JADX INFO: Added by JADX */
        public static final int action_running_apps_drawer = 0x7f06004d;

        /* JADX INFO: Added by JADX */
        public static final int action_saved_multi_action = 0x7f06004e;

        /* JADX INFO: Added by JADX */
        public static final int action_screen_filter = 0x7f06004f;

        /* JADX INFO: Added by JADX */
        public static final int action_screenshot = 0x7f060050;

        /* JADX INFO: Added by JADX */
        public static final int action_set_variable = 0x7f060051;

        /* JADX INFO: Added by JADX */
        public static final int action_shortcut = 0x7f060052;

        /* JADX INFO: Added by JADX */
        public static final int action_shortcuts_panel = 0x7f060053;

        /* JADX INFO: Added by JADX */
        public static final int action_shutdown = 0x7f060054;

        /* JADX INFO: Added by JADX */
        public static final int action_side_bar = 0x7f060055;

        /* JADX INFO: Added by JADX */
        public static final int action_sleep_wake = 0x7f060056;

        /* JADX INFO: Added by JADX */
        public static final int action_slide_adjust = 0x7f060057;

        /* JADX INFO: Added by JADX */
        public static final int action_soft_keyboard = 0x7f060058;

        /* JADX INFO: Added by JADX */
        public static final int action_soft_reboot = 0x7f060059;

        /* JADX INFO: Added by JADX */
        public static final int action_speech = 0x7f06005a;

        /* JADX INFO: Added by JADX */
        public static final int action_split_screen = 0x7f06005b;

        /* JADX INFO: Added by JADX */
        public static final int action_stop_all = 0x7f06005c;

        /* JADX INFO: Added by JADX */
        public static final int action_sub_gestures = 0x7f06005d;

        /* JADX INFO: Added by JADX */
        public static final int action_successive_adjust = 0x7f06005e;

        /* JADX INFO: Added by JADX */
        public static final int action_sync = 0x7f06005f;

        /* JADX INFO: Added by JADX */
        public static final int action_toast = 0x7f060060;

        /* JADX INFO: Added by JADX */
        public static final int action_torch = 0x7f060061;

        /* JADX INFO: Added by JADX */
        public static final int action_universal_copy = 0x7f060062;

        /* JADX INFO: Added by JADX */
        public static final int action_vibrate = 0x7f060063;

        /* JADX INFO: Added by JADX */
        public static final int action_wifi = 0x7f060064;

        /* JADX INFO: Added by JADX */
        public static final int action_wifi_ap = 0x7f060065;

        /* JADX INFO: Added by JADX */
        public static final int add = 0x7f060066;

        /* JADX INFO: Added by JADX */
        public static final int add_backup = 0x7f060067;

        /* JADX INFO: Added by JADX */
        public static final int add_backup_to = 0x7f060068;

        /* JADX INFO: Added by JADX */
        public static final int add_key = 0x7f060069;

        /* JADX INFO: Added by JADX */
        public static final int add_tile = 0x7f06006a;

        /* JADX INFO: Added by JADX */
        public static final int add_translation_or_fix = 0x7f06006b;

        /* JADX INFO: Added by JADX */
        public static final int added_to_collection = 0x7f06006c;

        /* JADX INFO: Added by JADX */
        public static final int adjust_brightness = 0x7f06006d;

        /* JADX INFO: Added by JADX */
        public static final int adjust_caret = 0x7f06006e;

        /* JADX INFO: Added by JADX */
        public static final int adjust_down = 0x7f06006f;

        /* JADX INFO: Added by JADX */
        public static final int adjust_screen_filter_alpha = 0x7f060070;

        /* JADX INFO: Added by JADX */
        public static final int adjust_scroll = 0x7f060071;

        /* JADX INFO: Added by JADX */
        public static final int adjust_up = 0x7f060072;

        /* JADX INFO: Added by JADX */
        public static final int alarm = 0x7f060073;

        /* JADX INFO: Added by JADX */
        public static final int allows_perform_by_broadcast = 0x7f060074;

        /* JADX INFO: Added by JADX */
        public static final int app_frozen_f = 0x7f060075;

        /* JADX INFO: Added by JADX */
        public static final int app_got_focus = 0x7f060076;

        /* JADX INFO: Added by JADX */
        public static final int app_icons = 0x7f060077;

        /* JADX INFO: Added by JADX */
        public static final int app_info = 0x7f060078;

        /* JADX INFO: Added by JADX */
        public static final int app_lost_focus = 0x7f060079;

        /* JADX INFO: Added by JADX */
        public static final int app_name_pro = 0x7f06007b;

        /* JADX INFO: Added by JADX */
        public static final int app_started = 0x7f06007c;

        /* JADX INFO: Added by JADX */
        public static final int app_state = 0x7f06007d;

        /* JADX INFO: Added by JADX */
        public static final int app_stopped = 0x7f06007e;

        /* JADX INFO: Added by JADX */
        public static final int app_thawed_f = 0x7f06007f;

        /* JADX INFO: Added by JADX */
        public static final int as_date = 0x7f060080;

        /* JADX INFO: Added by JADX */
        public static final int as_default = 0x7f060081;

        /* JADX INFO: Added by JADX */
        public static final int as_week = 0x7f060082;

        /* JADX INFO: Added by JADX */
        public static final int auto_dark_mode = 0x7f060083;

        /* JADX INFO: Added by JADX */
        public static final int auto_freeze_only_app_died = 0x7f060084;

        /* JADX INFO: Added by JADX */
        public static final int backup_restore = 0x7f060085;

        /* JADX INFO: Added by JADX */
        public static final int backups = 0x7f060086;

        /* JADX INFO: Added by JADX */
        public static final int black_theme = 0x7f060087;

        /* JADX INFO: Added by JADX */
        public static final int both = 0x7f060088;

        /* JADX INFO: Added by JADX */
        public static final int built_in = 0x7f060089;

        /* JADX INFO: Added by JADX */
        public static final int by_two_finger = 0x7f06008a;

        /* JADX INFO: Added by JADX */
        public static final int caret_left = 0x7f06008b;

        /* JADX INFO: Added by JADX */
        public static final int caret_right = 0x7f06008c;

        /* JADX INFO: Added by JADX */
        public static final int check_clear_all = 0x7f06008d;

        /* JADX INFO: Added by JADX */
        public static final int check_import_from_free_version = 0x7f06008e;

        /* JADX INFO: Added by JADX */
        public static final int check_save_message = 0x7f06008f;

        /* JADX INFO: Added by JADX */
        public static final int circle_icon = 0x7f060090;

        /* JADX INFO: Added by JADX */
        public static final int circle_icon_on_panels = 0x7f060091;

        /* JADX INFO: Added by JADX */
        public static final int click_a_key_to_add = 0x7f060092;

        /* JADX INFO: Added by JADX */
        public static final int click_to_continue = 0x7f060093;

        /* JADX INFO: Added by JADX */
        public static final int clip_history_size = 0x7f060094;

        /* JADX INFO: Added by JADX */
        public static final int clipboard_color = 0x7f060095;

        /* JADX INFO: Added by JADX */
        public static final int code = 0x7f060096;

        /* JADX INFO: Added by JADX */
        public static final int collect_activity = 0x7f060097;

        /* JADX INFO: Added by JADX */
        public static final int collect_activity_with_params = 0x7f060098;

        /* JADX INFO: Added by JADX */
        public static final int colorful_circle_icon = 0x7f060099;

        /* JADX INFO: Added by JADX */
        public static final int column_index_f = 0x7f06009a;

        /* JADX INFO: Added by JADX */
        public static final int command_hint = 0x7f06009b;

        /* JADX INFO: Added by JADX */
        public static final int command_on_system_process = 0x7f06009c;

        /* JADX INFO: Added by JADX */
        public static final int command_root_access = 0x7f06009d;

        /* JADX INFO: Added by JADX */
        public static final int condition_airplane_mode_on = 0x7f06009e;

        /* JADX INFO: Added by JADX */
        public static final int condition_and = 0x7f06009f;

        /* JADX INFO: Added by JADX */
        public static final int condition_app_focused = 0x7f0600a0;

        /* JADX INFO: Added by JADX */
        public static final int condition_auto_brightness = 0x7f0600a1;

        /* JADX INFO: Added by JADX */
        public static final int condition_auto_rotation = 0x7f0600a2;

        /* JADX INFO: Added by JADX */
        public static final int condition_battery_level = 0x7f0600a3;

        /* JADX INFO: Added by JADX */
        public static final int condition_bluetooth_on = 0x7f0600a4;

        /* JADX INFO: Added by JADX */
        public static final int condition_gesture_enabled = 0x7f0600a5;

        /* JADX INFO: Added by JADX */
        public static final int condition_headset_plugged = 0x7f0600a6;

        /* JADX INFO: Added by JADX */
        public static final int condition_in_days_of_month = 0x7f0600a7;

        /* JADX INFO: Added by JADX */
        public static final int condition_in_days_of_week = 0x7f0600a8;

        /* JADX INFO: Added by JADX */
        public static final int condition_in_months = 0x7f0600a9;

        /* JADX INFO: Added by JADX */
        public static final int condition_invalid = 0x7f0600aa;

        /* JADX INFO: Added by JADX */
        public static final int condition_key_enabled = 0x7f0600ab;

        /* JADX INFO: Added by JADX */
        public static final int condition_keyguard_on = 0x7f0600ac;

        /* JADX INFO: Added by JADX */
        public static final int condition_location_on = 0x7f0600ad;

        /* JADX INFO: Added by JADX */
        public static final int condition_mobile_data_on = 0x7f0600ae;

        /* JADX INFO: Added by JADX */
        public static final int condition_music_active = 0x7f0600af;

        /* JADX INFO: Added by JADX */
        public static final int condition_nav_bar_showing = 0x7f0600b0;

        /* JADX INFO: Added by JADX */
        public static final int condition_net_connected = 0x7f0600b1;

        /* JADX INFO: Added by JADX */
        public static final int condition_nfc_on = 0x7f0600b2;

        /* JADX INFO: Added by JADX */
        public static final int condition_not = 0x7f0600b3;

        /* JADX INFO: Added by JADX */
        public static final int condition_off_hook = 0x7f0600b4;

        /* JADX INFO: Added by JADX */
        public static final int condition_or = 0x7f0600b5;

        /* JADX INFO: Added by JADX */
        public static final int condition_orientation = 0x7f0600b6;

        /* JADX INFO: Added by JADX */
        public static final int condition_power_connected = 0x7f0600b7;

        /* JADX INFO: Added by JADX */
        public static final int condition_ringing = 0x7f0600b8;

        /* JADX INFO: Added by JADX */
        public static final int condition_screen_on = 0x7f0600b9;

        /* JADX INFO: Added by JADX */
        public static final int condition_soft_keyboard_showing = 0x7f0600ba;

        /* JADX INFO: Added by JADX */
        public static final int condition_status_bar_showing = 0x7f0600bb;

        /* JADX INFO: Added by JADX */
        public static final int condition_sync_on = 0x7f0600bc;

        /* JADX INFO: Added by JADX */
        public static final int condition_time_period = 0x7f0600bd;

        /* JADX INFO: Added by JADX */
        public static final int condition_usb_plugged = 0x7f0600be;

        /* JADX INFO: Added by JADX */
        public static final int condition_variable = 0x7f0600bf;

        /* JADX INFO: Added by JADX */
        public static final int condition_wifi_ap_on = 0x7f0600c0;

        /* JADX INFO: Added by JADX */
        public static final int condition_wifi_connected = 0x7f0600c1;

        /* JADX INFO: Added by JADX */
        public static final int condition_wifi_connected_to = 0x7f0600c2;

        /* JADX INFO: Added by JADX */
        public static final int condition_wifi_on = 0x7f0600c3;

        /* JADX INFO: Added by JADX */
        public static final int conditional = 0x7f0600c4;

        /* JADX INFO: Added by JADX */
        public static final int conditional_else = 0x7f0600c5;

        /* JADX INFO: Added by JADX */
        public static final int conditional_if = 0x7f0600c6;

        /* JADX INFO: Added by JADX */
        public static final int conditional_then = 0x7f0600c7;

        /* JADX INFO: Added by JADX */
        public static final int confirm_freeze = 0x7f0600c8;

        /* JADX INFO: Added by JADX */
        public static final int count = 0x7f0600c9;

        /* JADX INFO: Added by JADX */
        public static final int custom_panels = 0x7f0600ca;

        /* JADX INFO: Added by JADX */
        public static final int dark_theme = 0x7f0600cb;

        /* JADX INFO: Added by JADX */
        public static final int default_icon = 0x7f0600cc;

        /* JADX INFO: Added by JADX */
        public static final int delete = 0x7f0600cd;

        /* JADX INFO: Added by JADX */
        public static final int delete_all = 0x7f0600ce;

        /* JADX INFO: Added by JADX */
        public static final int description = 0x7f0600cf;

        /* JADX INFO: Added by JADX */
        public static final int disable = 0x7f0600d0;

        /* JADX INFO: Added by JADX */
        public static final int disable_auto_brightness_when_brightness_adjusted = 0x7f0600d1;

        /* JADX INFO: Added by JADX */
        public static final int disable_gesture_on_keyguard = 0x7f0600d2;

        /* JADX INFO: Added by JADX */
        public static final int disable_gesture_on_soft_keyboard = 0x7f0600d3;

        /* JADX INFO: Added by JADX */
        public static final int disable_tile = 0x7f0600d4;

        /* JADX INFO: Added by JADX */
        public static final int distance_to_trigger_swipe = 0x7f0600d5;

        /* JADX INFO: Added by JADX */
        public static final int do_not_show_pen_toolbar_automatically = 0x7f0600d6;

        /* JADX INFO: Added by JADX */
        public static final int do_not_show_pen_toolbar_automatically_hint = 0x7f0600d7;

        /* JADX INFO: Added by JADX */
        public static final int down_f = 0x7f0600d8;

        /* JADX INFO: Added by JADX */
        public static final int download_apps_drawer = 0x7f0600d9;

        /* JADX INFO: Added by JADX */
        public static final int drawer_color = 0x7f0600da;

        /* JADX INFO: Added by JADX */
        public static final int edge_bottom = 0x7f0600db;

        /* JADX INFO: Added by JADX */
        public static final int edge_bottom_center = 0x7f0600dc;

        /* JADX INFO: Added by JADX */
        public static final int edge_bottom_left = 0x7f0600dd;

        /* JADX INFO: Added by JADX */
        public static final int edge_bottom_right = 0x7f0600de;

        /* JADX INFO: Added by JADX */
        public static final int edge_height = 0x7f0600df;

        /* JADX INFO: Added by JADX */
        public static final int edge_left = 0x7f0600e0;

        /* JADX INFO: Added by JADX */
        public static final int edge_left_bottom = 0x7f0600e1;

        /* JADX INFO: Added by JADX */
        public static final int edge_left_center = 0x7f0600e2;

        /* JADX INFO: Added by JADX */
        public static final int edge_left_top = 0x7f0600e3;

        /* JADX INFO: Added by JADX */
        public static final int edge_right = 0x7f0600e4;

        /* JADX INFO: Added by JADX */
        public static final int edge_right_bottom = 0x7f0600e5;

        /* JADX INFO: Added by JADX */
        public static final int edge_right_center = 0x7f0600e6;

        /* JADX INFO: Added by JADX */
        public static final int edge_right_top = 0x7f0600e7;

        /* JADX INFO: Added by JADX */
        public static final int edge_top = 0x7f0600e8;

        /* JADX INFO: Added by JADX */
        public static final int edge_top_center = 0x7f0600e9;

        /* JADX INFO: Added by JADX */
        public static final int edge_top_left = 0x7f0600ea;

        /* JADX INFO: Added by JADX */
        public static final int edge_top_right = 0x7f0600eb;

        /* JADX INFO: Added by JADX */
        public static final int edge_width = 0x7f0600ec;

        /* JADX INFO: Added by JADX */
        public static final int edit = 0x7f0600ed;

        /* JADX INFO: Added by JADX */
        public static final int edit_action = 0x7f0600ee;

        /* JADX INFO: Added by JADX */
        public static final int edit_icon_and_label = 0x7f0600ef;

        /* JADX INFO: Added by JADX */
        public static final int edit_tile = 0x7f0600f0;

        /* JADX INFO: Added by JADX */
        public static final int effect_only_screen_on = 0x7f0600f1;

        /* JADX INFO: Added by JADX */
        public static final int effect_sound = 0x7f0600f2;

        /* JADX INFO: Added by JADX */
        public static final int empty_as_default = 0x7f0600f3;

        /* JADX INFO: Added by JADX */
        public static final int enable = 0x7f0600f4;

        /* JADX INFO: Added by JADX */
        public static final int enable_tile = 0x7f0600f5;

        /* JADX INFO: Added by JADX */
        public static final int enter_count = 0x7f0600f6;

        /* JADX INFO: Added by JADX */
        public static final int enter_duration = 0x7f0600f7;

        /* JADX INFO: Added by JADX */
        public static final int enter_name = 0x7f0600f8;

        /* JADX INFO: Added by JADX */
        public static final int error_freeze_app_f = 0x7f0600f9;

        /* JADX INFO: Added by JADX */
        public static final int error_thaw_app_f = 0x7f0600fa;

        /* JADX INFO: Added by JADX */
        public static final int export_as_command = 0x7f0600fb;

        /* JADX INFO: Added by JADX */
        public static final int floating_widget_timeout = 0x7f0600fc;

        /* JADX INFO: Added by JADX */
        public static final int focused = 0x7f0600fd;

        /* JADX INFO: Added by JADX */
        public static final int force_has_nav_bar = 0x7f0600fe;

        /* JADX INFO: Added by JADX */
        public static final int force_nav_bar_state = 0x7f0600ff;

        /* JADX INFO: Added by JADX */
        public static final int force_no_nav_bar = 0x7f060100;

        /* JADX INFO: Added by JADX */
        public static final int freeze = 0x7f060101;

        /* JADX INFO: Added by JADX */
        public static final int freezer = 0x7f060102;

        /* JADX INFO: Added by JADX */
        public static final int gallery_apps = 0x7f060103;

        /* JADX INFO: Added by JADX */
        public static final int general = 0x7f060104;

        /* JADX INFO: Added by JADX */
        public static final int gesture_double_tap = 0x7f060105;

        /* JADX INFO: Added by JADX */
        public static final int gesture_double_tap_timeout = 0x7f060106;

        /* JADX INFO: Added by JADX */
        public static final int gesture_hold = 0x7f060107;

        /* JADX INFO: Added by JADX */
        public static final int gesture_hold_timeout = 0x7f060108;

        /* JADX INFO: Added by JADX */
        public static final int gesture_injecting = 0x7f060109;

        /* JADX INFO: Added by JADX */
        public static final int gesture_long_press = 0x7f06010a;

        /* JADX INFO: Added by JADX */
        public static final int gesture_long_press_timeout = 0x7f06010b;

        /* JADX INFO: Added by JADX */
        public static final int gesture_pointer_color = 0x7f06010c;

        /* JADX INFO: Added by JADX */
        public static final int gesture_pointer_modes = 0x7f06010d;

        /* JADX INFO: Added by JADX */
        public static final int gesture_pointer_scale = 0x7f06010e;

        /* JADX INFO: Added by JADX */
        public static final int gesture_recording = 0x7f06010f;

        /* JADX INFO: Added by JADX */
        public static final int gesture_records = 0x7f060110;

        /* JADX INFO: Added by JADX */
        public static final int gesture_swipe_down = 0x7f060111;

        /* JADX INFO: Added by JADX */
        public static final int gesture_swipe_left = 0x7f060112;

        /* JADX INFO: Added by JADX */
        public static final int gesture_swipe_right = 0x7f060113;

        /* JADX INFO: Added by JADX */
        public static final int gesture_swipe_up = 0x7f060114;

        /* JADX INFO: Added by JADX */
        public static final int gesture_tap = 0x7f060115;

        /* JADX INFO: Added by JADX */
        public static final int gestures = 0x7f060116;

        /* JADX INFO: Added by JADX */
        public static final int gestures_triggers_when_swiping_off_hint = 0x7f060117;

        /* JADX INFO: Added by JADX */
        public static final int gestures_triggers_when_swiping_on_hint = 0x7f060118;

        /* JADX INFO: Added by JADX */
        public static final int get_gesture_hint = 0x7f060119;

        /* JADX INFO: Added by JADX */
        public static final int get_pro_version = 0x7f06011a;

        /* JADX INFO: Added by JADX */
        public static final int help_and_support = 0x7f06011b;

        /* JADX INFO: Added by JADX */
        public static final int help_how_to_set = 0x7f06011c;

        /* JADX INFO: Added by JADX */
        public static final int help_item_0 = 0x7f06011d;

        /* JADX INFO: Added by JADX */
        public static final int help_item_1 = 0x7f06011e;

        /* JADX INFO: Added by JADX */
        public static final int help_item_2 = 0x7f06011f;

        /* JADX INFO: Added by JADX */
        public static final int help_item_3 = 0x7f060120;

        /* JADX INFO: Added by JADX */
        public static final int help_item_4 = 0x7f060121;

        /* JADX INFO: Added by JADX */
        public static final int help_item_5 = 0x7f060122;

        /* JADX INFO: Added by JADX */
        public static final int help_item_6 = 0x7f060123;

        /* JADX INFO: Added by JADX */
        public static final int hidden_apps = 0x7f060124;

        /* JADX INFO: Added by JADX */
        public static final int hide_soft_keyboard = 0x7f060125;

        /* JADX INFO: Added by JADX */
        public static final int icon = 0x7f060126;

        /* JADX INFO: Added by JADX */
        public static final int icon_style_in_app = 0x7f060127;

        /* JADX INFO: Added by JADX */
        public static final int ignore_edges = 0x7f060128;

        /* JADX INFO: Added by JADX */
        public static final int import_from_free_version = 0x7f060129;

        /* JADX INFO: Added by JADX */
        public static final int important = 0x7f06012a;

        /* JADX INFO: Added by JADX */
        public static final int input_expression = 0x7f06012b;

        /* JADX INFO: Added by JADX */
        public static final int input_method_menu = 0x7f06012c;

        /* JADX INFO: Added by JADX */
        public static final int insert = 0x7f06012d;

        /* JADX INFO: Added by JADX */
        public static final int intercept_keys = 0x7f06012e;

        /* JADX INFO: Added by JADX */
        public static final int invalid_color = 0x7f06012f;

        /* JADX INFO: Added by JADX */
        public static final int invalid_file = 0x7f060130;

        /* JADX INFO: Added by JADX */
        public static final int invalid_widget = 0x7f060131;

        /* JADX INFO: Added by JADX */
        public static final int key_back = 0x7f060132;

        /* JADX INFO: Added by JADX */
        public static final int key_click = 0x7f060133;

        /* JADX INFO: Added by JADX */
        public static final int key_click_f = 0x7f060134;

        /* JADX INFO: Added by JADX */
        public static final int key_double_click = 0x7f060135;

        /* JADX INFO: Added by JADX */
        public static final int key_double_click_f = 0x7f060136;

        /* JADX INFO: Added by JADX */
        public static final int key_double_click_timeout = 0x7f060137;

        /* JADX INFO: Added by JADX */
        public static final int key_headset_hook = 0x7f060138;

        /* JADX INFO: Added by JADX */
        public static final int key_home = 0x7f060139;

        /* JADX INFO: Added by JADX */
        public static final int key_long_click_timeout = 0x7f06013a;

        /* JADX INFO: Added by JADX */
        public static final int key_long_press = 0x7f06013b;

        /* JADX INFO: Added by JADX */
        public static final int key_long_press_f = 0x7f06013c;

        /* JADX INFO: Added by JADX */
        public static final int key_menu = 0x7f06013d;

        /* JADX INFO: Added by JADX */
        public static final int key_power = 0x7f06013e;

        /* JADX INFO: Added by JADX */
        public static final int key_recent_apps = 0x7f06013f;

        /* JADX INFO: Added by JADX */
        public static final int key_volume_down = 0x7f060140;

        /* JADX INFO: Added by JADX */
        public static final int key_volume_up = 0x7f060141;

        /* JADX INFO: Added by JADX */
        public static final int keys = 0x7f060142;

        /* JADX INFO: Added by JADX */
        public static final int label = 0x7f060143;

        /* JADX INFO: Added by JADX */
        public static final int last_day_of_month = 0x7f060144;

        /* JADX INFO: Added by JADX */
        public static final int last_input_method = 0x7f060145;

        /* JADX INFO: Added by JADX */
        public static final int launch = 0x7f060146;

        /* JADX INFO: Added by JADX */
        public static final int left_side_bar = 0x7f060147;

        /* JADX INFO: Added by JADX */
        public static final int light = 0x7f060148;

        /* JADX INFO: Added by JADX */
        public static final int light_gray_theme = 0x7f060149;

        /* JADX INFO: Added by JADX */
        public static final int light_theme = 0x7f06014a;

        /* JADX INFO: Added by JADX */
        public static final int logs = 0x7f06014b;

        /* JADX INFO: Added by JADX */
        public static final int low_priority = 0x7f06014c;

        /* JADX INFO: Added by JADX */
        public static final int memory_released_size_f = 0x7f06014d;

        /* JADX INFO: Added by JADX */
        public static final int mode_default = 0x7f06014e;

        /* JADX INFO: Added by JADX */
        public static final int mode_double_tap = 0x7f06014f;

        /* JADX INFO: Added by JADX */
        public static final int mode_long_press = 0x7f060150;

        /* JADX INFO: Added by JADX */
        public static final int mode_tap = 0x7f060151;

        /* JADX INFO: Added by JADX */
        public static final int mode_transform = 0x7f060152;

        /* JADX INFO: Added by JADX */
        public static final int modify_days_of_month = 0x7f060153;

        /* JADX INFO: Added by JADX */
        public static final int modify_months = 0x7f060154;

        /* JADX INFO: Added by JADX */
        public static final int module_not_activated = 0x7f060155;

        /* JADX INFO: Added by JADX */
        public static final int more = 0x7f060156;

        /* JADX INFO: Added by JADX */
        public static final int more_triggers = 0x7f060157;

        /* JADX INFO: Added by JADX */
        public static final int move_down = 0x7f060158;

        /* JADX INFO: Added by JADX */
        public static final int move_up = 0x7f060159;

        /* JADX INFO: Added by JADX */
        public static final int mute = 0x7f06015a;

        /* JADX INFO: Added by JADX */
        public static final int never = 0x7f06015b;

        /* JADX INFO: Added by JADX */
        public static final int never_intercept_power_key = 0x7f06015c;

        /* JADX INFO: Added by JADX */
        public static final int next_alarm = 0x7f06015d;

        /* JADX INFO: Added by JADX */
        public static final int no_alarm = 0x7f06015e;

        /* JADX INFO: Added by JADX */
        public static final int not_need_to_exec_su_explicitly = 0x7f06015f;

        /* JADX INFO: Added by JADX */
        public static final int nothing_fetched = 0x7f060160;

        /* JADX INFO: Added by JADX */
        public static final int notification = 0x7f060161;

        /* JADX INFO: Added by JADX */
        public static final int notification_hint = 0x7f060162;

        /* JADX INFO: Added by JADX */
        public static final int notify_for_actions = 0x7f060163;

        /* JADX INFO: Added by JADX */
        public static final int off_f = 0x7f060164;

        /* JADX INFO: Added by JADX */
        public static final int on_f = 0x7f060165;

        /* JADX INFO: Added by JADX */
        public static final int only_if_keyguard_not_secure = 0x7f060166;

        /* JADX INFO: Added by JADX */
        public static final int open_from = 0x7f060167;

        /* JADX INFO: Added by JADX */
        public static final int orientation_landscape = 0x7f060168;

        /* JADX INFO: Added by JADX */
        public static final int orientation_portrait = 0x7f060169;

        /* JADX INFO: Added by JADX */
        public static final int panel_circle_background_color = 0x7f06016a;

        /* JADX INFO: Added by JADX */
        public static final int panel_color = 0x7f06016b;

        /* JADX INFO: Added by JADX */
        public static final int panel_icon_color = 0x7f06016c;

        /* JADX INFO: Added by JADX */
        public static final int panel_scale = 0x7f06016d;

        /* JADX INFO: Added by JADX */
        public static final int pen_colors = 0x7f06016e;

        /* JADX INFO: Added by JADX */
        public static final int perform = 0x7f06016f;

        /* JADX INFO: Added by JADX */
        public static final int perform_by_broadcast_not_allowed = 0x7f060170;

        /* JADX INFO: Added by JADX */
        public static final int perform_failed = 0x7f060171;

        /* JADX INFO: Added by JADX */
        public static final int permission_denied = 0x7f060172;

        /* JADX INFO: Added by JADX */
        public static final int pie = 0x7f060173;

        /* JADX INFO: Added by JADX */
        public static final int pie_color = 0x7f060174;

        /* JADX INFO: Added by JADX */
        public static final int pie_icon_color = 0x7f060175;

        /* JADX INFO: Added by JADX */
        public static final int pie_level_1_item_index_f = 0x7f060176;

        /* JADX INFO: Added by JADX */
        public static final int pie_level_2_item_index_f = 0x7f060177;

        /* JADX INFO: Added by JADX */
        public static final int pie_scale = 0x7f060178;

        /* JADX INFO: Added by JADX */
        public static final int pie_slide_adjust_support = 0x7f060179;

        /* JADX INFO: Added by JADX */
        public static final int play_once = 0x7f06017a;

        /* JADX INFO: Added by JADX */
        public static final int preferences = 0x7f06017b;

        /* JADX INFO: Added by JADX */
        public static final int pro_only = 0x7f06017c;

        /* JADX INFO: Added by JADX */
        public static final int put_on_launcher = 0x7f06017d;

        /* JADX INFO: Added by JADX */
        public static final int rate = 0x7f06017e;

        /* JADX INFO: Added by JADX */
        public static final int redo = 0x7f06017f;

        /* JADX INFO: Added by JADX */
        public static final int remove_activity_params = 0x7f060180;

        /* JADX INFO: Added by JADX */
        public static final int remove_tile = 0x7f060181;

        /* JADX INFO: Added by JADX */
        public static final int rename = 0x7f060182;

        /* JADX INFO: Added by JADX */
        public static final int repeat = 0x7f060183;

        /* JADX INFO: Added by JADX */
        public static final int restore = 0x7f060184;

        /* JADX INFO: Added by JADX */
        public static final int right_side_bar = 0x7f060185;

        /* JADX INFO: Added by JADX */
        public static final int ringer_mode_normal = 0x7f060186;

        /* JADX INFO: Added by JADX */
        public static final int ringer_mode_silent = 0x7f060187;

        /* JADX INFO: Added by JADX */
        public static final int ringer_mode_vibrate = 0x7f060188;

        /* JADX INFO: Added by JADX */
        public static final int ringtone = 0x7f060189;

        /* JADX INFO: Added by JADX */
        public static final int rotate_180 = 0x7f06018a;

        /* JADX INFO: Added by JADX */
        public static final int rotate_270 = 0x7f06018b;

        /* JADX INFO: Added by JADX */
        public static final int rotate_90 = 0x7f06018c;

        /* JADX INFO: Added by JADX */
        public static final int rotate_landscape = 0x7f06018d;

        /* JADX INFO: Added by JADX */
        public static final int rotate_portrait = 0x7f06018e;

        /* JADX INFO: Added by JADX */
        public static final int rotate_seascape = 0x7f06018f;

        /* JADX INFO: Added by JADX */
        public static final int rotate_upside_down = 0x7f060190;

        /* JADX INFO: Added by JADX */
        public static final int row_index_f = 0x7f060191;

        /* JADX INFO: Added by JADX */
        public static final int run_command_failed = 0x7f060192;

        /* JADX INFO: Added by JADX */
        public static final int save_clip_data = 0x7f060193;

        /* JADX INFO: Added by JADX */
        public static final int save_failed = 0x7f060194;

        /* JADX INFO: Added by JADX */
        public static final int saved = 0x7f060195;

        /* JADX INFO: Added by JADX */
        public static final int saved_icons = 0x7f060196;

        /* JADX INFO: Added by JADX */
        public static final int saved_multi_actions = 0x7f060197;

        /* JADX INFO: Added by JADX */
        public static final int schedule = 0x7f060198;

        /* JADX INFO: Added by JADX */
        public static final int screen_filter_color = 0x7f060199;

        /* JADX INFO: Added by JADX */
        public static final int screenshot_fullscreen = 0x7f06019a;

        /* JADX INFO: Added by JADX */
        public static final int screenshot_selected_region = 0x7f06019b;

        /* JADX INFO: Added by JADX */
        public static final int scroll_down = 0x7f06019c;

        /* JADX INFO: Added by JADX */
        public static final int scroll_speed = 0x7f06019d;

        /* JADX INFO: Added by JADX */
        public static final int scroll_to_bottom = 0x7f06019e;

        /* JADX INFO: Added by JADX */
        public static final int scroll_to_top = 0x7f06019f;

        /* JADX INFO: Added by JADX */
        public static final int scroll_up = 0x7f0601a0;

        /* JADX INFO: Added by JADX */
        public static final int select = 0x7f0601a1;

        /* JADX INFO: Added by JADX */
        public static final int select_action = 0x7f0601a2;

        /* JADX INFO: Added by JADX */
        public static final int select_activity = 0x7f0601a3;

        /* JADX INFO: Added by JADX */
        public static final int select_app = 0x7f0601a4;

        /* JADX INFO: Added by JADX */
        public static final int select_condition = 0x7f0601a5;

        /* JADX INFO: Added by JADX */
        public static final int select_first_condition = 0x7f0601a6;

        /* JADX INFO: Added by JADX */
        public static final int select_floating_widget = 0x7f0601a7;

        /* JADX INFO: Added by JADX */
        public static final int select_focused_app = 0x7f0601a8;

        /* JADX INFO: Added by JADX */
        public static final int select_icon = 0x7f0601a9;

        /* JADX INFO: Added by JADX */
        public static final int select_key_event = 0x7f0601aa;

        /* JADX INFO: Added by JADX */
        public static final int select_second_condition = 0x7f0601ab;

        /* JADX INFO: Added by JADX */
        public static final int select_shortcut = 0x7f0601ac;

        /* JADX INFO: Added by JADX */
        public static final int set_level = 0x7f0601ad;

        /* JADX INFO: Added by JADX */
        public static final int settings_restored = 0x7f0601ae;

        /* JADX INFO: Added by JADX */
        public static final int share = 0x7f0601af;

        /* JADX INFO: Added by JADX */
        public static final int shortcut_name = 0x7f0601b0;

        /* JADX INFO: Added by JADX */
        public static final int shortcut_name_pro = 0x7f0601b1;

        /* JADX INFO: Added by JADX */
        public static final int shortcuts_panel = 0x7f0601b2;

        /* JADX INFO: Added by JADX */
        public static final int show_activities_exported_only = 0x7f0601b3;

        /* JADX INFO: Added by JADX */
        public static final int show_all_activities = 0x7f0601b4;

        /* JADX INFO: Added by JADX */
        public static final int show_soft_keyboard = 0x7f0601b5;

        /* JADX INFO: Added by JADX */
        public static final int show_text_on_other_drawers = 0x7f0601b6;

        /* JADX INFO: Added by JADX */
        public static final int show_text_on_shortcut_panel = 0x7f0601b7;

        /* JADX INFO: Added by JADX */
        public static final int show_text_on_side_bar = 0x7f0601b8;

        /* JADX INFO: Added by JADX */
        public static final int show_value = 0x7f0601b9;

        /* JADX INFO: Added by JADX */
        public static final int side_left = 0x7f0601ba;

        /* JADX INFO: Added by JADX */
        public static final int side_right = 0x7f0601bb;

        /* JADX INFO: Added by JADX */
        public static final int since_nougat = 0x7f0601bc;

        /* JADX INFO: Added by JADX */
        public static final int slide_adjust_color = 0x7f0601bd;

        /* JADX INFO: Added by JADX */
        public static final int sound = 0x7f0601be;

        /* JADX INFO: Added by JADX */
        public static final int sound_effect = 0x7f0601bf;

        /* JADX INFO: Added by JADX */
        public static final int speech_description = 0x7f0601c0;

        /* JADX INFO: Added by JADX */
        public static final int speech_hint = 0x7f0601c1;

        /* JADX INFO: Added by JADX */
        public static final int state_airplane_mode_off = 0x7f0601c2;

        /* JADX INFO: Added by JADX */
        public static final int state_airplane_mode_on = 0x7f0601c3;

        /* JADX INFO: Added by JADX */
        public static final int state_battery_full = 0x7f0601c4;

        /* JADX INFO: Added by JADX */
        public static final int state_battery_low = 0x7f0601c5;

        /* JADX INFO: Added by JADX */
        public static final int state_battery_okay = 0x7f0601c6;

        /* JADX INFO: Added by JADX */
        public static final int state_boot_completed = 0x7f0601c7;

        /* JADX INFO: Added by JADX */
        public static final int state_exit_full_screen = 0x7f0601c8;

        /* JADX INFO: Added by JADX */
        public static final int state_full_screen = 0x7f0601c9;

        /* JADX INFO: Added by JADX */
        public static final int state_headset_plugged = 0x7f0601ca;

        /* JADX INFO: Added by JADX */
        public static final int state_headset_unplugged = 0x7f0601cb;

        /* JADX INFO: Added by JADX */
        public static final int state_incoming_call_connected = 0x7f0601cc;

        /* JADX INFO: Added by JADX */
        public static final int state_incoming_call_ended = 0x7f0601cd;

        /* JADX INFO: Added by JADX */
        public static final int state_incoming_call_ringed = 0x7f0601ce;

        /* JADX INFO: Added by JADX */
        public static final int state_nav_bar_hidden = 0x7f0601cf;

        /* JADX INFO: Added by JADX */
        public static final int state_nav_bar_shown = 0x7f0601d0;

        /* JADX INFO: Added by JADX */
        public static final int state_net_connected = 0x7f0601d1;

        /* JADX INFO: Added by JADX */
        public static final int state_net_disconnected = 0x7f0601d2;

        /* JADX INFO: Added by JADX */
        public static final int state_outgoing_call_connected = 0x7f0601d3;

        /* JADX INFO: Added by JADX */
        public static final int state_outgoing_call_dialed = 0x7f0601d4;

        /* JADX INFO: Added by JADX */
        public static final int state_outgoing_call_ended = 0x7f0601d5;

        /* JADX INFO: Added by JADX */
        public static final int state_power_connected = 0x7f0601d6;

        /* JADX INFO: Added by JADX */
        public static final int state_power_disconnected = 0x7f0601d7;

        /* JADX INFO: Added by JADX */
        public static final int state_screen_off = 0x7f0601d8;

        /* JADX INFO: Added by JADX */
        public static final int state_screen_on = 0x7f0601d9;

        /* JADX INFO: Added by JADX */
        public static final int state_screen_rotated = 0x7f0601da;

        /* JADX INFO: Added by JADX */
        public static final int state_screen_rotated_landscape = 0x7f0601db;

        /* JADX INFO: Added by JADX */
        public static final int state_screen_rotated_portrait = 0x7f0601dc;

        /* JADX INFO: Added by JADX */
        public static final int state_sms_received = 0x7f0601dd;

        /* JADX INFO: Added by JADX */
        public static final int state_soft_keyboard_hidden = 0x7f0601de;

        /* JADX INFO: Added by JADX */
        public static final int state_soft_keyboard_shown = 0x7f0601df;

        /* JADX INFO: Added by JADX */
        public static final int state_status_bar_hidden = 0x7f0601e0;

        /* JADX INFO: Added by JADX */
        public static final int state_status_bar_shown = 0x7f0601e1;

        /* JADX INFO: Added by JADX */
        public static final int state_usb_plugged = 0x7f0601e2;

        /* JADX INFO: Added by JADX */
        public static final int state_usb_unplugged = 0x7f0601e3;

        /* JADX INFO: Added by JADX */
        public static final int state_user_present = 0x7f0601e4;

        /* JADX INFO: Added by JADX */
        public static final int state_wifi_connected = 0x7f0601e5;

        /* JADX INFO: Added by JADX */
        public static final int state_wifi_disconnected = 0x7f0601e6;

        /* JADX INFO: Added by JADX */
        public static final int stop_app = 0x7f0601e7;

        /* JADX INFO: Added by JADX */
        public static final int stop_play_sound = 0x7f0601e8;

        /* JADX INFO: Added by JADX */
        public static final int stop_speech = 0x7f0601e9;

        /* JADX INFO: Added by JADX */
        public static final int su_not_supported_on_system = 0x7f0601ea;

        /* JADX INFO: Added by JADX */
        public static final int sub_action_sleep = 0x7f0601eb;

        /* JADX INFO: Added by JADX */
        public static final int sub_action_switch = 0x7f0601ec;

        /* JADX INFO: Added by JADX */
        public static final int sub_action_wake_up = 0x7f0601ed;

        /* JADX INFO: Added by JADX */
        public static final int sub_gesture_triggers_when_swiping = 0x7f0601ee;

        /* JADX INFO: Added by JADX */
        public static final int swipe_triggers_after_finger_upped = 0x7f0601ef;

        /* JADX INFO: Added by JADX */
        public static final int system_apps_drawer = 0x7f0601f0;

        /* JADX INFO: Added by JADX */
        public static final int system_default = 0x7f0601f1;

        /* JADX INFO: Added by JADX */
        public static final int text_copied = 0x7f0601f2;

        /* JADX INFO: Added by JADX */
        public static final int theme = 0x7f0601f3;

        /* JADX INFO: Added by JADX */
        public static final int tile_1 = 0x7f0601f4;

        /* JADX INFO: Added by JADX */
        public static final int tile_10 = 0x7f0601f5;

        /* JADX INFO: Added by JADX */
        public static final int tile_11 = 0x7f0601f6;

        /* JADX INFO: Added by JADX */
        public static final int tile_12 = 0x7f0601f7;

        /* JADX INFO: Added by JADX */
        public static final int tile_2 = 0x7f0601f8;

        /* JADX INFO: Added by JADX */
        public static final int tile_3 = 0x7f0601f9;

        /* JADX INFO: Added by JADX */
        public static final int tile_4 = 0x7f0601fa;

        /* JADX INFO: Added by JADX */
        public static final int tile_5 = 0x7f0601fb;

        /* JADX INFO: Added by JADX */
        public static final int tile_6 = 0x7f0601fc;

        /* JADX INFO: Added by JADX */
        public static final int tile_7 = 0x7f0601fd;

        /* JADX INFO: Added by JADX */
        public static final int tile_8 = 0x7f0601fe;

        /* JADX INFO: Added by JADX */
        public static final int tile_9 = 0x7f0601ff;

        /* JADX INFO: Added by JADX */
        public static final int tile_added = 0x7f060200;

        /* JADX INFO: Added by JADX */
        public static final int tile_removed = 0x7f060201;

        /* JADX INFO: Added by JADX */
        public static final int tiles = 0x7f060202;

        /* JADX INFO: Added by JADX */
        public static final int time_from = 0x7f060203;

        /* JADX INFO: Added by JADX */
        public static final int time_in_milliseconds = 0x7f060204;

        /* JADX INFO: Added by JADX */
        public static final int time_to = 0x7f060205;

        /* JADX INFO: Added by JADX */
        public static final int toast_description = 0x7f060206;

        /* JADX INFO: Added by JADX */
        public static final int toast_level = 0x7f060207;

        /* JADX INFO: Added by JADX */
        public static final int toggle = 0x7f060208;

        /* JADX INFO: Added by JADX */
        public static final int turn_off = 0x7f060209;

        /* JADX INFO: Added by JADX */
        public static final int turn_off_f = 0x7f06020a;

        /* JADX INFO: Added by JADX */
        public static final int turn_on = 0x7f06020b;

        /* JADX INFO: Added by JADX */
        public static final int turn_on_f = 0x7f06020c;

        /* JADX INFO: Added by JADX */
        public static final int tutorial_online = 0x7f06020d;

        /* JADX INFO: Added by JADX */
        public static final int unable_to_freeze_this_app = 0x7f06020e;

        /* JADX INFO: Added by JADX */
        public static final int undo = 0x7f06020f;

        /* JADX INFO: Added by JADX */
        public static final int unfreeze = 0x7f060210;

        /* JADX INFO: Added by JADX */
        public static final int unsafe_to_freeze_system_app = 0x7f060211;

        /* JADX INFO: Added by JADX */
        public static final int up_f = 0x7f060212;

        /* JADX INFO: Added by JADX */
        public static final int user_not_supported = 0x7f060213;

        /* JADX INFO: Added by JADX */
        public static final int variable_expression_hint = 0x7f060214;

        /* JADX INFO: Added by JADX */
        public static final int variable_name_hint = 0x7f060215;

        /* JADX INFO: Added by JADX */
        public static final int variables = 0x7f060216;

        /* JADX INFO: Added by JADX */
        public static final int verbose = 0x7f060217;

        /* JADX INFO: Added by JADX */
        public static final int vibrate = 0x7f060218;

        /* JADX INFO: Added by JADX */
        public static final int vibrate_click = 0x7f060219;

        /* JADX INFO: Added by JADX */
        public static final int vibrate_custom = 0x7f06021a;

        /* JADX INFO: Added by JADX */
        public static final int vibrate_disable = 0x7f06021b;

        /* JADX INFO: Added by JADX */
        public static final int vibrate_double_click = 0x7f06021c;

        /* JADX INFO: Added by JADX */
        public static final int vibrate_duration = 0x7f06021d;

        /* JADX INFO: Added by JADX */
        public static final int vibrate_effect = 0x7f06021e;

        /* JADX INFO: Added by JADX */
        public static final int vibrate_heavy_click = 0x7f06021f;

        /* JADX INFO: Added by JADX */
        public static final int vibrate_strength = 0x7f060220;

        /* JADX INFO: Added by JADX */
        public static final int vibrate_tick = 0x7f060221;

        /* JADX INFO: Added by JADX */
        public static final int view = 0x7f060222;

        /* JADX INFO: Added by JADX */
        public static final int view_on_play_store = 0x7f060223;

        /* JADX INFO: Added by JADX */
        public static final int visit_xda_thread = 0x7f060224;

        /* JADX INFO: Added by JADX */
        public static final int visual_effect = 0x7f060225;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_alarm = 0x7f060226;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_bluetooth_sco = 0x7f060227;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_default = 0x7f060228;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_dtmf = 0x7f060229;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_music = 0x7f06022a;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_notification = 0x7f06022b;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_ring = 0x7f06022c;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_system = 0x7f06022d;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_system_enforced = 0x7f06022e;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_tts = 0x7f06022f;

        /* JADX INFO: Added by JADX */
        public static final int volume_stream_voice_call = 0x7f060230;

        /* JADX INFO: Added by JADX */
        public static final int widget_hint = 0x7f060231;

        /* JADX INFO: Added by JADX */
        public static final int widgets = 0x7f060232;

        /* JADX INFO: Added by JADX */
        public static final int wifi_ssid = 0x7f060233;

        /* JADX INFO: Added by JADX */
        public static final int windows_not_focusable = 0x7f060234;

        /* JADX INFO: Added by JADX */
        public static final int windows_not_focusable_hint = 0x7f060235;

        /* JADX INFO: Added by JADX */
        public static final int xposed_not_installed = 0x7f060236;

        static {
            ADRT.onClassLoad(144L, "cc.xposed.R$string");
        }

        public string() {
            if (adrt$enabled) {
                ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(144L);
                try {
                    aDRTThreadOnMethodEnter.onStatementStart(20);
                    aDRTThreadOnMethodEnter.onThisAvailable(this);
                    aDRTThreadOnMethodEnter.onStatementStart(21);
                } finally {
                    aDRTThreadOnMethodEnter.onMethodExit();
                }
            }
        }
    }

    public static final class style {
        public static final int AppTheme = 0x7f050000;
        private static boolean adrt$enabled;

        /* JADX INFO: Added by JADX */
        public static final int ActionBarStyle = 0x7f070000;

        /* JADX INFO: Added by JADX */
        public static final int AppThemeAuto = 0x7f070002;

        /* JADX INFO: Added by JADX */
        public static final int AppThemeBlack = 0x7f070003;

        /* JADX INFO: Added by JADX */
        public static final int AppThemeDark = 0x7f070004;

        /* JADX INFO: Added by JADX */
        public static final int AppThemeLightGray = 0x7f070005;

        /* JADX INFO: Added by JADX */
        public static final int ListViewStyleAuto = 0x7f070006;

        /* JADX INFO: Added by JADX */
        public static final int ListViewStyleDark = 0x7f070007;

        /* JADX INFO: Added by JADX */
        public static final int ListViewStyleLight = 0x7f070008;

        static {
            ADRT.onClassLoad(144L, "cc.xposed.R$style");
        }

        public style() {
            if (adrt$enabled) {
                ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(144L);
                try {
                    aDRTThreadOnMethodEnter.onStatementStart(23);
                    aDRTThreadOnMethodEnter.onThisAvailable(this);
                    aDRTThreadOnMethodEnter.onStatementStart(24);
                } finally {
                    aDRTThreadOnMethodEnter.onMethodExit();
                }
            }
        }
    }

    public R() {
        if (adrt$enabled) {
            ADRTThread aDRTThreadOnMethodEnter = ADRT.onMethodEnter(144L);
            try {
                aDRTThreadOnMethodEnter.onStatementStart(24);
                aDRTThreadOnMethodEnter.onThisAvailable(this);
                aDRTThreadOnMethodEnter.onStatementStart(25);
            } finally {
                aDRTThreadOnMethodEnter.onMethodExit();
            }
        }
    }
}
