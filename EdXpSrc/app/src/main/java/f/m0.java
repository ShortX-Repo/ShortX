package f;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class m0 implements l {
    private TextToSpeech A = null;
    private boolean B = false;
    private Locale C = null;
    private int D = 0;
    private String E = null;
    private long F = 0;
    private final Context z;

    class a implements TextToSpeech.OnInitListener {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        final /* synthetic */ String f464a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        final /* synthetic */ boolean f465b;

        /* JADX INFO: renamed from: f.m0$a$a, reason: collision with other inner class name */
        class C0008a extends UtteranceProgressListener {
            C0008a() {
            }

            @Override // android.speech.tts.UtteranceProgressListener
            public void onDone(String str) {
                m0 m0Var;
                long jUptimeMillis;
                if (m0.this.E == null || !m0.this.E.equals(str)) {
                    m0Var = m0.this;
                    jUptimeMillis = 0;
                } else {
                    m0Var = m0.this;
                    jUptimeMillis = SystemClock.uptimeMillis();
                }
                m0Var.F = jUptimeMillis;
            }

            @Override // android.speech.tts.UtteranceProgressListener
            public void onError(String str) {
            }

            @Override // android.speech.tts.UtteranceProgressListener
            public void onStart(String str) {
            }
        }

        a(String str, boolean z) {
            this.f464a = str;
            this.f465b = z;
        }

        @Override // android.speech.tts.TextToSpeech.OnInitListener
        public void onInit(int i) {
            if (i != 0) {
                v.c("Failed to init TextToSpeech.");
                return;
            }
            m0 m0Var = m0.this;
            m0Var.C = m0Var.A.getDefaultLanguage();
            int language = m0.this.A.setLanguage(m0.this.C);
            if (language == -2 || language == -1) {
                m0.this.C = Locale.ENGLISH;
                int language2 = m0.this.A.setLanguage(m0.this.C);
                if (language2 == -2 || language2 == -1) {
                    v.c("Failed to set locale of text to speech.");
                    return;
                }
            }
            m0.this.B = true;
            m0.this.F = 0L;
            m0.this.A.setOnUtteranceProgressListener(new C0008a());
            try {
                m0.this.p(this.f464a, this.f465b, true);
            } catch (Throwable th) {
                v.d(th);
            }
        }
    }

    public m0(Context context) {
        this.z = context;
    }

    private static String h(String str, Locale locale, Context context) {
        return j(str, locale, locale.getLanguage().startsWith("zh") && l(context));
    }

    public static String i(String str, Context context) {
        return j(str, Locale.getDefault(), l(context));
    }

    private static String j(String str, Locale locale, boolean z) {
        String string;
        String string2;
        StringBuilder sb;
        String str2;
        if (str == null) {
            return null;
        }
        if (str.length() < 2) {
            return str;
        }
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(calendar.getTimeInMillis());
        if (str.contains("%t")) {
            if (z) {
                int i = calendar.get(11);
                int i2 = calendar.get(12);
                if (i > 9) {
                    string2 = Integer.toString(i);
                } else {
                    string2 = "0" + i;
                }
                if (i2 > 9) {
                    sb = new StringBuilder();
                    sb.append(string2);
                    str2 = ":";
                } else {
                    sb = new StringBuilder();
                    sb.append(string2);
                    str2 = ":0";
                }
                sb.append(str2);
                sb.append(i2);
                string = sb.toString();
            } else {
                string = DateFormat.getTimeInstance(3, locale).format(date);
            }
            str = str.replace("%t", string);
        }
        if (str.contains("%d")) {
            str = str.replace("%d", DateFormat.getDateInstance(1, locale).format(date));
        }
        return str.contains("%w") ? str.replace("%w", new DateFormatSymbols(locale).getWeekdays()[calendar.get(7)]) : str;
    }

    private void k(String str, boolean z) {
        this.A = new TextToSpeech(this.z, new a(str, z));
    }

    private static boolean l(Context context) {
        return "24".equals(Settings.System.getString(context.getContentResolver(), "time_12_24"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void p(String str, boolean z, boolean z2) {
        if (!this.B || str == null || str.length() == 0) {
            return;
        }
        if (z) {
            str = h(str, this.C, this.z);
        }
        int i = this.D + 1;
        this.D = i;
        String string = Integer.toString(i);
        if (Build.VERSION.SDK_INT >= 21) {
            this.A.speak(str, z2 ? 1 : 0, null, string);
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("utteranceId", string);
            this.A.speak(str, z2 ? 1 : 0, map);
        }
        this.E = string;
    }

    public boolean m() {
        TextToSpeech textToSpeech = this.A;
        return textToSpeech != null && textToSpeech.isSpeaking();
    }

    public void n() {
        TextToSpeech textToSpeech = this.A;
        if (textToSpeech != null) {
            try {
                textToSpeech.shutdown();
            } catch (Throwable th) {
                v.d(th);
            }
            this.A = null;
        }
    }

    public void o(String str, boolean z, boolean z2) {
        if (this.A != null) {
            if (this.F == 0 || SystemClock.uptimeMillis() - this.F < 30000) {
                p(str, z, z2);
                return;
            }
            n();
        }
        k(str, z);
    }
}
