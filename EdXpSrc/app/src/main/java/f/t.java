package f;

/* JADX INFO: loaded from: classes.dex */
public final class t {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f488a;

    public t(int i) {
        this.f488a = i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && t.class == obj.getClass() && this.f488a == ((t) obj).f488a;
    }

    public int hashCode() {
        return this.f488a;
    }

    public String toString() {
        return Integer.toString(this.f488a);
    }
}
