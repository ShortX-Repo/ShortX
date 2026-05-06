package f;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class u<K, V> implements Map<K, V>, RandomAccess {
    private Object[] A;
    private int B;
    private Object[] z;

    class a extends AbstractSet<Map.Entry<K, V>> {

        /* JADX INFO: renamed from: f.u$a$a, reason: collision with other inner class name */
        class C0009a implements Iterator<Map.Entry<K, V>> {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            int f489a = 0;

            /* JADX INFO: renamed from: f.u$a$a$a, reason: collision with other inner class name */
            class C0010a implements Map.Entry<K, V> {
                final int z;

                C0010a() {
                    int i = C0009a.this.f489a;
                    C0009a.this.f489a = i + 1;
                    this.z = i;
                }

                @Override // java.util.Map.Entry
                public K getKey() {
                    return (K) u.this.e(this.z);
                }

                @Override // java.util.Map.Entry
                public V getValue() {
                    return (V) u.this.k(this.z);
                }

                @Override // java.util.Map.Entry
                public V setValue(V v) {
                    if (this.z < u.this.B) {
                        V v2 = (V) u.this.A[this.z];
                        u.this.A[this.z] = v;
                        return v2;
                    }
                    throw new IndexOutOfBoundsException("Invalid index " + this.z + ", size is " + u.this.B);
                }
            }

            C0009a() {
            }

            @Override // java.util.Iterator
            /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
            public Map.Entry<K, V> next() {
                return new C0010a();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.f489a < u.this.B;
            }

            @Override // java.util.Iterator
            public void remove() {
                u.this.i(this.f489a);
            }
        }

        a() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            u.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new C0009a();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return u.this.B;
        }
    }

    class b extends AbstractSet<K> {

        class a implements Iterator<K> {

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            int f491a = 0;

            a() {
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.f491a < u.this.B;
            }

            @Override // java.util.Iterator
            public K next() {
                Object[] objArr = u.this.z;
                int i = this.f491a;
                this.f491a = i + 1;
                return (K) objArr[i];
            }

            @Override // java.util.Iterator
            public void remove() {
                u.this.i(this.f491a);
            }
        }

        b() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            u.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            return new a();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return u.this.B;
        }
    }

    public u() {
        this(16);
    }

    public u(int i) {
        this.z = new Object[i];
        this.A = new Object[i];
        this.B = 0;
    }

    @Override // java.util.Map
    public void clear() {
        for (int i = 0; i < this.B; i++) {
            this.z[i] = null;
            this.A[i] = null;
        }
        this.B = 0;
    }

    @Override // java.util.Map
    public boolean containsKey(Object obj) {
        if (obj != null) {
            for (int i = 0; i < this.B; i++) {
                if (obj.equals(this.z[i])) {
                    return true;
                }
            }
        } else {
            for (int i2 = 0; i2 < this.B; i2++) {
                if (this.z[i2] == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // java.util.Map
    public boolean containsValue(Object obj) {
        if (obj != null) {
            for (int i = 0; i < this.B; i++) {
                if (obj.equals(this.A[i])) {
                    return true;
                }
            }
        } else {
            for (int i2 = 0; i2 < this.B; i2++) {
                if (this.A[i2] == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public int d(K k) {
        return p0.b(this.z, k, this.B);
    }

    public K e(int i) {
        if (i >= 0 && i < this.B) {
            return (K) this.z[i];
        }
        throw new IndexOutOfBoundsException("Invalid index " + i + ", size is " + this.B);
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return new a();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ArrayList<K> f() {
        ArrayList<K> arrayList = (ArrayList<K>) new ArrayList(this.B);
        for (int i = 0; i < this.B; i++) {
            arrayList.add(this.z[i]);
        }
        return arrayList;
    }

    public void g(int i) {
        int i2;
        if (i < 0 || (i2 = i + 1) >= this.B) {
            v.c("Invalid move up map item: " + i + ", size: " + this.B);
            return;
        }
        Object[] objArr = this.z;
        Object obj = objArr[i];
        objArr[i] = objArr[i2];
        objArr[i2] = obj;
        Object[] objArr2 = this.A;
        Object obj2 = objArr2[i];
        objArr2[i] = objArr2[i2];
        objArr2[i2] = obj2;
    }

    @Override // java.util.Map
    public V get(Object obj) {
        int iB = p0.b(this.z, obj, this.B);
        if (iB == -1) {
            return null;
        }
        return (V) this.A[iB];
    }

    public void h(int i) {
        if (i <= 0 || i >= this.B) {
            v.c("Invalid move up map item: " + i + ", size: " + this.B);
            return;
        }
        Object[] objArr = this.z;
        Object obj = objArr[i];
        int i2 = i - 1;
        objArr[i] = objArr[i2];
        objArr[i2] = obj;
        Object[] objArr2 = this.A;
        Object obj2 = objArr2[i];
        objArr2[i] = objArr2[i2];
        objArr2[i2] = obj2;
    }

    public V i(int i) {
        int i2 = this.B;
        if (i >= i2) {
            throw new IndexOutOfBoundsException("Invalid index " + i + ", size is " + this.B);
        }
        V v = (V) this.A[i];
        this.B = i2 - 1;
        while (i < this.B) {
            Object[] objArr = this.z;
            int i3 = i + 1;
            objArr[i] = objArr[i3];
            Object[] objArr2 = this.A;
            objArr2[i] = objArr2[i3];
            i = i3;
        }
        return v;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.B == 0;
    }

    public V j(int i, V v) {
        if (i >= 0 && i < this.B) {
            Object[] objArr = this.A;
            V v2 = (V) objArr[i];
            objArr[i] = v;
            return v2;
        }
        throw new IndexOutOfBoundsException("Invalid index " + i + ", size is " + this.B);
    }

    public V k(int i) {
        if (i >= 0 && i < this.B) {
            return (V) this.A[i];
        }
        throw new IndexOutOfBoundsException("Invalid index " + i + ", size is " + this.B);
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return new b();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    /* JADX INFO: renamed from: l, reason: merged with bridge method [inline-methods] */
    public ArrayList<V> values() {
        ArrayList<V> arrayList = (ArrayList<V>) new ArrayList(this.B);
        for (int i = 0; i < this.B; i++) {
            arrayList.add(this.A[i]);
        }
        return arrayList;
    }

    @Override // java.util.Map
    public V put(K k, V v) {
        int iD = d(k);
        if (iD != -1) {
            Object[] objArr = this.A;
            V v2 = (V) objArr[iD];
            objArr[iD] = v;
            return v2;
        }
        Object[] objArr2 = this.z;
        int length = objArr2.length;
        int i = this.B;
        if (length == i) {
            int i2 = i * 2;
            this.z = new Object[i2];
            Object[] objArr3 = this.A;
            this.A = new Object[i2];
            for (int i3 = 0; i3 < this.B; i3++) {
                this.z[i3] = objArr2[i3];
                this.A[i3] = objArr3[i3];
            }
        }
        Object[] objArr4 = this.z;
        int i4 = this.B;
        objArr4[i4] = k;
        this.A[i4] = v;
        this.B = i4 + 1;
        return null;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        int size = this.B + map.size();
        Object[] objArr = this.z;
        if (objArr.length < size) {
            this.z = new Object[size];
            Object[] objArr2 = this.A;
            this.A = new Object[size];
            for (int i = 0; i < this.B; i++) {
                this.z[i] = objArr[i];
                this.A[i] = objArr2[i];
            }
        }
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.Map
    public V remove(Object obj) {
        int iB = p0.b(this.z, obj, this.B);
        if (iB == -1) {
            return null;
        }
        return i(iB);
    }

    @Override // java.util.Map
    public int size() {
        return this.B;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("Map{ ");
        for (int i = 0; i < this.B; i++) {
            sb.append('[');
            sb.append(this.z[i]);
            sb.append(", ");
            sb.append(this.A[i]);
            sb.append("] ");
        }
        sb.append('}');
        return sb.toString();
    }
}
