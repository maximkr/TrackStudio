package com.trackstudio.tools;

import java.util.ArrayList;
import java.util.Collection;

import net.jcip.annotations.Immutable;

@Immutable
public class Key {
    public interface KeyID {
    }

    public static class FourParam implements KeyID  {
        private final String a;
        private final String b;
        private final String c;
        private final String d;

        public FourParam(String a, String b, String c, String d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FourParam fourParam = (FourParam) o;

            if (a != null ? !a.equals(fourParam.a) : fourParam.a != null) return false;
            if (b != null ? !b.equals(fourParam.b) : fourParam.b != null) return false;
            if (c != null ? !c.equals(fourParam.c) : fourParam.c != null) return false;
            if (d != null ? !d.equals(fourParam.d) : fourParam.d != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            result = 31 * result + (c != null ? c.hashCode() : 0);
            result = 31 * result + (d != null ? d.hashCode() : 0);
            return result;
        }
    }

    public static class ListPlusTwoParam implements KeyID {
        final private ArrayList<String> list; // Use non-concurrent version , for performance
        final private String b;
        final private String c;

        public ListPlusTwoParam(Collection<String> list, String b, String c) {
            this.list = new ArrayList(list);
            this.b = b;
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListPlusTwoParam that = (ListPlusTwoParam) o;

            if (b != null ? !b.equals(that.b) : that.b != null) return false;
            if (c != null ? !c.equals(that.c) : that.c != null) return false;
            if (list != null ? !list.equals(that.list) : that.list != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = list != null ? list.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            result = 31 * result + (c != null ? c.hashCode() : 0);
            return result;
        }
    }

    public static class ListPlusTwoFour implements KeyID {
        final private ArrayList<String> list;
        final private String b;
        final private String c;
        final private String d;
        final private String e;


        public ListPlusTwoFour(Collection<String> list, String b, String c, String d, String e) {
            this.list = new ArrayList(list); // Use non-concurrent version , for performance
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListPlusTwoFour that = (ListPlusTwoFour) o;

            if (b != null ? !b.equals(that.b) : that.b != null) return false;
            if (c != null ? !c.equals(that.c) : that.c != null) return false;
            if (d != null ? !d.equals(that.d) : that.d != null) return false;
            if (e != null ? !e.equals(that.e) : that.e != null) return false;
            if (list != null ? !list.equals(that.list) : that.list != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = list != null ? list.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            result = 31 * result + (c != null ? c.hashCode() : 0);
            result = 31 * result + (d != null ? d.hashCode() : 0);
            result = 31 * result + (e != null ? e.hashCode() : 0);
            return result;
        }
    }

    public static class ListPlusOneParam implements KeyID {
        final private ArrayList<String> list;
        final private String b;
        
        public ListPlusOneParam(final Collection<String> list, final String b) {
            this.list = new ArrayList(list); // Use non-concurrent version , for performance
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListPlusOneParam that = (ListPlusOneParam) o;

            if (b != null ? !b.equals(that.b) : that.b != null) return false;
            if (list != null ? !list.equals(that.list) : that.list != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = list != null ? list.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            return result;
        }
    }
}
