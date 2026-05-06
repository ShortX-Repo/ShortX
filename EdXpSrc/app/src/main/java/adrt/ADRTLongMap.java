package adrt;

/* JADX INFO: loaded from: classes.dex */
public class ADRTLongMap {
    private static final int LOAD_FACTOR = 2;
    private static final int[] SIZES = {5, 11, 17, 37, 67, 131, 257, 521, 1031, 2053, 4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319, 8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827, 2147383649};
    private ADRTLongSet added;
    private int count;
    private int iteratorIndex;
    private long iteratorKey;
    private int iteratorStep;
    private long iteratorThekey;
    private long iteratorValue;
    private long[] keys;
    private long[] oldkeys;
    private long[] oldvalues;
    private int sizeexp;
    private int slots;
    private long[] values;

    public ADRTLongMap(int i) {
        this.added = new ADRTLongSet();
        this.sizeexp = 0;
        while (SIZES[this.sizeexp] < i * LOAD_FACTOR) {
            this.sizeexp++;
        }
        this.keys = new long[SIZES[this.sizeexp]];
        this.values = new long[SIZES[this.sizeexp]];
        this.slots = 0;
        this.count = 0;
    }

    public ADRTLongMap() {
        this.added = new ADRTLongSet();
        this.sizeexp = 0;
        this.keys = new long[SIZES[this.sizeexp]];
        this.values = new long[SIZES[this.sizeexp]];
        this.slots = 0;
        this.count = 0;
    }

    public void closure(ADRTLongSet aDRTLongSet) {
        ADRTLongSet aDRTLongSet2 = this.added;
        while (true) {
            aDRTLongSet2.clear();
            aDRTLongSet.startIterator();
            while (aDRTLongSet.hasMoreIteratorElements()) {
                startIterator(aDRTLongSet.nextIteratorKey());
                while (hasMoreIteratorElements()) {
                    long jNextIteratorValue = nextIteratorValue();
                    if (!aDRTLongSet.contains(jNextIteratorValue)) {
                        aDRTLongSet2.put(jNextIteratorValue);
                    }
                }
            }
            if (aDRTLongSet2.size() != 0) {
                aDRTLongSet.put(aDRTLongSet2);
            } else {
                return;
            }
        }
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

    public void put(long j, long j2) {
        remove(j);
        insert(j, j2);
    }

    public void insert(long j, long j2) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        int length = (int) ((Long.MAX_VALUE & j) % ((long) this.keys.length));
        int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
        long j3 = this.keys[length];
        int i = -1;
        while (true) {
            int i2 = length;
            long j4 = j3;
            if (j4 != 0) {
                if (j4 == j) {
                    if (this.values[i2] == j2) {
                        return;
                    }
                } else if (j4 == -2147483648L) {
                    i = i2;
                }
                length = (i2 + length2) % this.keys.length;
                j3 = this.keys[length];
            } else {
                if (i != -1) {
                    i2 = i;
                }
                this.keys[i2] = j;
                this.values[i2] = j2;
                this.count++;
                if (i == -1) {
                    this.slots++;
                }
                if (this.slots * LOAD_FACTOR > this.keys.length) {
                    rehash();
                    return;
                }
                return;
            }
        }
    }

    public void remove(long j, long j2) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        int length = (int) ((j & Long.MAX_VALUE) % ((long) this.keys.length));
        int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
        long j3 = this.keys[length];
        while (true) {
            if (j3 == j && this.values[length] == j2) {
                this.keys[length] = Long.MIN_VALUE;
                this.count--;
                return;
            } else if (j3 != 0) {
                length = (length + length2) % this.keys.length;
                j3 = this.keys[length];
            } else {
                return;
            }
        }
    }

    public boolean contains(long j, long j2) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        int length = (int) ((j & Long.MAX_VALUE) % ((long) this.keys.length));
        int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
        long j3 = this.keys[length];
        while (true) {
            if (j3 == j && this.values[length] == j2) {
                return true;
            }
            if (j3 == 0) {
                return false;
            }
            length = (length + length2) % this.keys.length;
            j3 = this.keys[length];
        }
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

    public int count(long j) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        int length = (int) ((j & Long.MAX_VALUE) % ((long) this.keys.length));
        int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
        int i = 0;
        int length3 = length;
        long j2 = this.keys[length];
        while (j2 != 0) {
            if (j2 == j) {
                i++;
            }
            length3 = (length3 + length2) % this.keys.length;
            j2 = this.keys[length3];
        }
        return i;
    }

    public void remove(long j) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        int length = (int) ((j & Long.MAX_VALUE) % ((long) this.keys.length));
        int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
        long j2 = this.keys[length];
        while (j2 != 0) {
            if (j2 == j) {
                this.keys[length] = Long.MIN_VALUE;
                this.count--;
            }
            length = (length + length2) % this.keys.length;
            j2 = this.keys[length];
        }
    }

    public long get(long j) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        int length = (int) ((j & Long.MAX_VALUE) % ((long) this.keys.length));
        int length2 = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
        long j2 = this.keys[length];
        while (j2 != j) {
            if (j2 == 0) {
                return -1L;
            }
            length = (length + length2) % this.keys.length;
            j2 = this.keys[length];
        }
        return this.values[length];
    }

    private void rehash() {
        long[] jArr;
        long[] jArr2;
        if (this.count * LOAD_FACTOR > this.keys.length) {
            this.sizeexp++;
            this.oldkeys = null;
            this.oldvalues = null;
            jArr = new long[SIZES[this.sizeexp]];
            jArr2 = new long[SIZES[this.sizeexp]];
        } else if (this.oldkeys != null && this.oldkeys.length == this.keys.length) {
            jArr = this.oldkeys;
            for (int i = 0; i < jArr.length; i++) {
                jArr[i] = 0;
            }
            jArr2 = this.oldvalues;
            this.oldkeys = this.keys;
            this.oldvalues = this.values;
        } else {
            this.oldkeys = this.keys;
            this.oldvalues = this.values;
            jArr = new long[SIZES[this.sizeexp]];
            jArr2 = new long[SIZES[this.sizeexp]];
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
                jArr2[length] = this.values[i3];
                i2++;
            }
        }
        this.keys = jArr;
        this.values = jArr2;
        this.slots = i2;
    }

    public int size() {
        return this.count;
    }

    public String toString() {
        return getClass().getName() + "[Size=" + this.count + "]";
    }

    public void startIterator() {
        this.iteratorIndex = 0;
        this.iteratorStep = 0;
    }

    public void startIterator(long j) {
        if (j == 0) {
            j = Long.MAX_VALUE;
        }
        this.iteratorThekey = j;
        this.iteratorIndex = (int) ((j & Long.MAX_VALUE) % ((long) this.keys.length));
        this.iteratorStep = ((int) ((Long.MAX_VALUE & j) % (((long) this.keys.length) - 2))) + 1;
    }

    public boolean hasMoreIteratorElements() {
        if (this.iteratorStep == 0) {
            while (this.iteratorIndex < this.keys.length) {
                this.iteratorKey = this.keys[this.iteratorIndex];
                if (this.iteratorKey != 0 && this.iteratorKey != Long.MIN_VALUE) {
                    if (this.iteratorKey == Long.MAX_VALUE) {
                        this.iteratorKey = 0L;
                    }
                    this.iteratorValue = this.values[this.iteratorIndex];
                    this.iteratorIndex++;
                    return true;
                }
                this.iteratorIndex++;
            }
            return false;
        }
        while (true) {
            this.iteratorKey = this.keys[this.iteratorIndex];
            if (this.iteratorKey == 0) {
                return false;
            }
            if (this.iteratorKey == this.iteratorThekey) {
                if (this.iteratorKey == Long.MAX_VALUE) {
                    this.iteratorKey = 0L;
                }
                this.iteratorValue = this.values[this.iteratorIndex];
                this.iteratorIndex = (this.iteratorIndex + this.iteratorStep) % this.keys.length;
                return true;
            }
            this.iteratorIndex = (this.iteratorIndex + this.iteratorStep) % this.keys.length;
        }
    }

    public long nextIteratorKey() {
        return this.iteratorKey;
    }

    public long nextIteratorValue() {
        return this.iteratorValue;
    }
}
