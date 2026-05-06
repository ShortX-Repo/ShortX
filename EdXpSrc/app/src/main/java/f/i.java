package f;

/* JADX INFO: loaded from: classes.dex */
public abstract class i {

    private static class a extends i {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final i f441a;

        a(i iVar) {
            this.f441a = iVar;
        }

        @Override // f.i
        public int a(f fVar) {
            return this.f441a.a(fVar) ^ (-1);
        }

        @Override // f.i
        public Integer b() {
            Integer numB = this.f441a.b();
            if (numB != null) {
                return Integer.valueOf(numB.intValue() ^ (-1));
            }
            return null;
        }

        public String toString() {
            return '~' + this.f441a.toString();
        }
    }

    private static class b extends i {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final i f442a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final char f443b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        private final i f444c;

        b(i iVar, char c2, i iVar2) {
            this.f442a = iVar;
            this.f443b = c2;
            this.f444c = iVar2;
        }

        private int g(int i, int i2) {
            char c2 = this.f443b;
            if (c2 == '#') {
                return i >> i2;
            }
            if (c2 == '-') {
                return i - i2;
            }
            if (c2 == '/') {
                return i / i2;
            }
            if (c2 == '@') {
                return i << i2;
            }
            if (c2 == '^') {
                return i ^ i2;
            }
            if (c2 == '|') {
                return i | i2;
            }
            if (c2 == '%') {
                return i % i2;
            }
            if (c2 == '&') {
                return i & i2;
            }
            if (c2 == '*') {
                return i * i2;
            }
            if (c2 == '+') {
                return i + i2;
            }
            throw new RuntimeException("Invalid operator: " + this.f443b);
        }

        @Override // f.i
        public int a(f fVar) {
            return g(this.f442a.a(fVar), this.f444c.a(fVar));
        }

        @Override // f.i
        public Integer b() {
            Integer numB;
            Integer numB2 = this.f442a.b();
            if (numB2 == null || (numB = this.f444c.b()) == null) {
                return null;
            }
            return Integer.valueOf(g(numB2.intValue(), numB.intValue()));
        }

        public String toString() {
            char c2 = this.f443b;
            return '(' + this.f442a.toString() + ' ' + (c2 == '@' ? "<<" : c2 == '#' ? ">>" : Character.toString(c2)) + ' ' + this.f444c.toString() + ')';
        }
    }

    private static class c extends i {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final String f445a;

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        private final char f446b;

        c(String str, char c2) {
            this.f445a = str;
            this.f446b = c2;
        }

        @Override // f.i
        public int a(f fVar) {
            t tVarB = fVar.b(this.f445a);
            if (tVarB == null) {
                throw new RuntimeException("Variable not exists: " + this.f445a);
            }
            char c2 = this.f446b;
            if (c2 == '\"') {
                int i = tVarB.f488a + 1;
                tVarB.f488a = i;
                return i;
            }
            if (c2 == '\'') {
                int i2 = tVarB.f488a;
                tVarB.f488a = i2 + 1;
                return i2;
            }
            if (c2 == ',') {
                int i3 = tVarB.f488a;
                tVarB.f488a = i3 - 1;
                return i3;
            }
            if (c2 == ';') {
                int i4 = tVarB.f488a - 1;
                tVarB.f488a = i4;
                return i4;
            }
            throw new RuntimeException("Invalid operator: " + this.f446b);
        }

        @Override // f.i
        public Integer b() {
            return null;
        }

        public String toString() {
            StringBuilder sb;
            char c2 = this.f446b;
            String str = "++";
            if (c2 != '\"') {
                if (c2 != '\'') {
                    str = "--";
                    if (c2 == ',') {
                        sb = new StringBuilder();
                    } else {
                        if (c2 != ';') {
                            throw new RuntimeException("Invalid operator: " + this.f446b);
                        }
                        sb = new StringBuilder();
                    }
                } else {
                    sb = new StringBuilder();
                }
                sb.append(this.f445a);
                sb.append(str);
                return sb.toString();
            }
            sb = new StringBuilder();
            sb.append(str);
            sb.append(this.f445a);
            return sb.toString();
        }
    }

    private static class d extends i {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final int f447a;

        d(int i) {
            this.f447a = i;
        }

        @Override // f.i
        public int a(f fVar) {
            return this.f447a;
        }

        @Override // f.i
        public Integer b() {
            return Integer.valueOf(this.f447a);
        }

        public String toString() {
            return Integer.toString(this.f447a);
        }
    }

    private static class e extends i {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final i f448a;

        e(i iVar) {
            this.f448a = iVar;
        }

        @Override // f.i
        public int a(f fVar) {
            return -this.f448a.a(fVar);
        }

        @Override // f.i
        public Integer b() {
            Integer numB = this.f448a.b();
            if (numB != null) {
                return Integer.valueOf(-numB.intValue());
            }
            return null;
        }

        public String toString() {
            return '-' + this.f448a.toString();
        }
    }

    public interface f {
        int a(String str);

        t b(String str);
    }

    private static class g extends i {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final String f449a;

        g(String str) {
            this.f449a = str;
        }

        @Override // f.i
        public int a(f fVar) {
            return fVar.a(this.f449a);
        }

        @Override // f.i
        public Integer b() {
            return null;
        }

        public String toString() {
            return this.f449a;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0050  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0064  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int c(java.lang.String r10, int r11, int r12) {
        /*
            int r12 = r12 + (-1)
            r0 = -1
            r1 = 0
            r2 = -1
            r3 = -1
            r4 = -1
            r5 = -1
            r6 = -1
        L9:
            java.lang.String r7 = "Invalid expression."
            if (r12 <= r11) goto L74
            char r8 = r10.charAt(r12)
            r9 = 35
            if (r8 == r9) goto L64
            r9 = 45
            if (r8 == r9) goto L50
            r9 = 47
            if (r8 == r9) goto L4a
            r9 = 64
            if (r8 == r9) goto L64
            r9 = 94
            if (r8 == r9) goto L44
            r9 = 124(0x7c, float:1.74E-43)
            if (r8 == r9) goto L41
            r9 = 37
            if (r8 == r9) goto L4a
            r9 = 38
            if (r8 == r9) goto L3b
            switch(r8) {
                case 40: goto L38;
                case 41: goto L35;
                case 42: goto L4a;
                case 43: goto L50;
                default: goto L34;
            }
        L34:
            goto L69
        L35:
            int r1 = r1 + 1
            goto L69
        L38:
            int r1 = r1 + (-1)
            goto L69
        L3b:
            if (r1 != 0) goto L69
            if (r5 >= 0) goto L69
            r5 = r12
            goto L69
        L41:
            if (r1 != 0) goto L69
            return r12
        L44:
            if (r1 != 0) goto L69
            if (r2 >= 0) goto L69
            r2 = r12
            goto L69
        L4a:
            if (r1 != 0) goto L69
            if (r4 >= 0) goto L69
            r4 = r12
            goto L69
        L50:
            if (r1 != 0) goto L69
            if (r6 >= 0) goto L69
            int r8 = r12 + (-1)
            char r8 = r10.charAt(r8)
        */
        //  java.lang.String r9 = "|^&<>+-*/%@#\"';,"
        /*
            int r8 = r9.indexOf(r8)
            if (r8 != r0) goto L69
            r6 = r12
            goto L69
        L64:
            if (r1 != 0) goto L69
            if (r3 >= 0) goto L69
            r3 = r12
        L69:
            if (r1 < 0) goto L6e
            int r12 = r12 + (-1)
            goto L9
        L6e:
            java.lang.RuntimeException r10 = new java.lang.RuntimeException
            r10.<init>(r7)
            throw r10
        L74:
            char r10 = r10.charAt(r11)
            r11 = 40
            if (r10 != r11) goto L7e
            int r1 = r1 + (-1)
        L7e:
            if (r1 != 0) goto L91
            if (r2 <= 0) goto L83
            goto L90
        L83:
            if (r5 <= 0) goto L87
            r2 = r5
            goto L90
        L87:
            if (r3 <= 0) goto L8b
            r2 = r3
            goto L90
        L8b:
            if (r6 <= 0) goto L8f
            r2 = r6
            goto L90
        L8f:
            r2 = r4
        L90:
            return r2
        L91:
            java.lang.RuntimeException r10 = new java.lang.RuntimeException
            r10.<init>(r7)
            goto L98
        L97:
            throw r10
        L98:
            goto L97
        */
        throw new UnsupportedOperationException("Method not decompiled: f.i.c(java.lang.String, int, int):int");
    }

    public static i d(String str) {
        if (str == null || str.length() == 0) {
            throw new RuntimeException("Empty expression.");
        }
        String strF = f(str);
        return e(strF, 0, strF.length());
    }

    private static i e(String str, int i, int i2) {
        int iC = c(str, i, i2);
        if (iC != -1) {
            i iVarE = e(str, i, iC);
            char cCharAt = str.charAt(iC);
            i iVarE2 = e(str, iC + 1, i2);
            Integer numB = new b(iVarE, cCharAt, iVarE2).b();
            return numB != null ? new d(numB.intValue()) : new b(iVarE, cCharAt, iVarE2);
        }
        char cCharAt2 = str.charAt(i);
        if (cCharAt2 != '\"' && cCharAt2 != ';') {
            if (cCharAt2 == '~') {
                i iVarE3 = e(str, i + 1, i2);
                Integer numB2 = iVarE3.b();
                return numB2 != null ? new d(numB2.intValue() ^ (-1)) : new a(iVarE3);
            }
            if (cCharAt2 != '\'') {
                if (cCharAt2 != '(') {
                    switch (cCharAt2) {
                        case '+':
                            return e(str, i + 1, i2);
                        case '-':
                            i iVarE4 = e(str, i + 1, i2);
                            Integer numB3 = iVarE4.b();
                            return numB3 != null ? new d(-numB3.intValue()) : new e(iVarE4);
                    }
                }
                int i3 = i2 - 1;
                if (str.charAt(i3) == ')') {
                    return e(str, i + 1, i3);
                }
                return ((cCharAt2 < 'a' || cCharAt2 > 'z') && !((cCharAt2 >= 'A' && cCharAt2 <= 'Z') || cCharAt2 == '_' || cCharAt2 == '$')) ? new d(Integer.parseInt(str.substring(i, i2))) : new g(str.substring(i, i2));
            }
        }
        int i4 = i + 1;
        char cCharAt3 = str.charAt(i4);
        if ((cCharAt3 < 'a' || cCharAt3 > 'z') && !((cCharAt3 >= 'A' && cCharAt3 <= 'Z') || cCharAt3 == '_' || cCharAt3 == '$')) {
            throw new RuntimeException("Invalid expression.");
        }
        return new c(str.substring(i4, i2), cCharAt2);
    }

    /* JADX WARN: Removed duplicated region for block: B:62:0x00a9  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.lang.String f(java.lang.String r13) {
        /*
            Method dump skipped, instruction units count: 213
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: f.i.f(java.lang.String):java.lang.String");
    }

    public abstract int a(f fVar);

    public abstract Integer b();
}
