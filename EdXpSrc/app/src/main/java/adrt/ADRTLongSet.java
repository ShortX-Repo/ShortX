package adrt;

/* JADX INFO: loaded from: classes.dex */
public class ADRTLongSet {
    private static final int LOAD_FACTOR = 2;
    private static final int[] SIZES = {5, 11, 17, 37, 67, 131, 257, 521, 1031, 2053, 4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319, 8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827, 2147383649};
    private int count;
    private int iteratorIndex;
    private long iteratorKey;
    private long[] keys;
    private long[] oldkeys;
    private int sizeexp;
    private int slots;

    public ADRTLongSet(int i) {
        this.sizeexp = 1;
        while (SIZES[this.sizeexp] < i * LOAD_FACTOR) {
            this.sizeexp++;
        }
        this.keys = new long[SIZES[this.sizeexp]];
        this.slots = 0;
        this.count = 0;
    }

    public ADRTLongSet() {
        this.sizeexp = 1;
        this.keys = new long[SIZES[this.sizeexp]];
        this.slots = 0;
        this.count = 0;
    }

    public void clear() {
        if (this.slots > 0) {
            for (int i = 0; i < this.keys.length; i++) {
                this.keys[i] = 0;
            }
            this.slots = 0;
            this.count = 0;
        }
    }

    public void put(ADRTLongSet aDRTLongSet) {
        for (int i = 0; i < aDRTLongSet.keys.length; i++) {
            long j = aDRTLongSet.keys[i];
            if (j == Long.MAX_VALUE) {
                put(0L);
            } else if (j != 0 && j != Long.MIN_VALUE) {
                put(j);
            }
        }
    }

    public void put(long j) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        int length = (int) ((j & Long.MAX_VALUE) % ((long) this.keys.length));
        int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
        int i = -1;
        int length3 = length;
        long j2 = this.keys[length];
        while (j2 != 0) {
            if (j2 != j) {
                if (j2 == Long.MIN_VALUE) {
                    i = length3;
                }
                length3 = (length3 + length2) % this.keys.length;
                j2 = this.keys[length3];
            } else {
                return;
            }
        }
        if (i != -1) {
            length3 = i;
        }
        this.keys[length3] = j;
        if (i == -1) {
            this.slots++;
        }
        this.count++;
        if (this.slots * LOAD_FACTOR > this.keys.length) {
            rehash();
        }
    }

    public void remove(long j) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        int length = (int) ((j & Long.MAX_VALUE) % ((long) this.keys.length));
        int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
        long j2 = this.keys[length];
        while (j2 != j) {
            if (j2 != 0) {
                length = (length + length2) % this.keys.length;
                j2 = this.keys[length];
            } else {
                return;
            }
        }
        this.keys[length] = Long.MIN_VALUE;
        this.count--;
    }

    public boolean contains(long j) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        int length = (int) ((j & Long.MAX_VALUE) % ((long) this.keys.length));
        int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
        long j2 = this.keys[length];
        while (j2 != j) {
            if (j2 == 0) {
                return false;
            }
            length = (length + length2) % this.keys.length;
            j2 = this.keys[length];
        }
        return true;
    }

    public boolean empty() {
        return this.count == 0;
    }

    private void rehash() {
        long[] jArr;
        if (this.count * LOAD_FACTOR > this.keys.length) {
            this.sizeexp++;
            this.oldkeys = null;
            jArr = new long[SIZES[this.sizeexp]];
        } else if (this.oldkeys != null && this.oldkeys.length == this.keys.length) {
            long[] jArr2 = this.oldkeys;
            for (int i = 0; i < jArr2.length; i++) {
                jArr2[i] = 0;
            }
            this.oldkeys = this.keys;
            jArr = jArr2;
        } else {
            this.oldkeys = this.keys;
            jArr = new long[SIZES[this.sizeexp]];
        }
        int i2 = 0;
        for (int i3 = 0; i3 < this.keys.length; i3++) {
            long j = this.keys[i3];
            if (j != 0 && j != Long.MIN_VALUE) {
                int length = (int) ((Long.MAX_VALUE & j) % ((long) jArr.length));
                int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) jArr.length) - 2))) + 1;
                while (jArr[length] != 0) {
                    length = (length + length2) % jArr.length;
                }
                jArr[length] = j;
                i2++;
            }
        }
        this.keys = jArr;
        this.slots = i2;
    }

    public int size() {
        return this.count;
    }

    public String toString() {
        String str = "{";
        for (int i = 0; i < this.keys.length; i++) {
            long j = this.keys[i];
            if (j != 0 && j != Long.MIN_VALUE) {
                str = j == Long.MAX_VALUE ? str + "0, " : str + j + ", ";
            }
        }
        return str + "}";
    }

    public void startIterator() {
        this.iteratorIndex = 0;
    }

    public boolean hasMoreIteratorElements() {
        while (this.iteratorIndex < this.keys.length) {
            this.iteratorKey = this.keys[this.iteratorIndex];
            this.iteratorIndex++;
            if (this.iteratorKey != 0 && this.iteratorKey != Long.MIN_VALUE) {
                if (this.iteratorKey == Long.MAX_VALUE) {
                    this.iteratorKey = 0L;
                }
                return true;
            }
        }
        return false;
    }

    public long nextIteratorKey() {
        return this.iteratorKey;
    }
}
