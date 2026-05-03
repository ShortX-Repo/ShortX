package adrt;

/* JADX INFO: loaded from: classes.dex */
public class ADRTThreadLocal extends ThreadLocal<ADRTThread> {
    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.lang.ThreadLocal
    public ADRTThread initialValue() {
        return new ADRTThread();
    }
}
