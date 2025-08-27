package com.trackstudio.tools.textfilter;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;

import net.jcip.annotations.ThreadSafe;

/**
 * This class is used for working for HTML
 */
@ThreadSafe
public class HTMLEncoder {

    /**
     *  This is JS actions
     */
    public static final String[] BAD_ATTRIBUTES = new String[]{
            "onAbort",
            "onBlur",
            "onChange",
            "onClick",
            "onDblClick",
            "onDragDrop",
            "onError",
            "onFocus",
            "onKeyDown",
            "onKeyPress",
            "onKeyUp",
            "onLoad",
            "onMouseDown",
            "onMouseMove",
            "onMouseOut",
            "onMouseOver",
            "onMouseUp",
            "onMove",
            "onReset",
            "onResize",
            "onSelect",
            "onSubmit",
            "onUnload",
            "class"
    };


    //private static Log log = LogFactory.getLog(HTMLEncoder.class);

    /**
     * Inner Buffer
     */
    private volatile StringBuffer innerBuffer;

    /**
     * Lower cased buffer
     */
    private volatile StringBuffer lowercased;

    /**
     * Constructor
     *
     * @param buf Pre buffer
     */
    public HTMLEncoder(StringBuffer buf) {
        this.innerBuffer = buf != null ? buf : new StringBuffer();
        this.lowercased = new StringBuffer(this.innerBuffer.toString().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Constructor
     *
     * @param buf Pre buffer
     */
    public HTMLEncoder(String buf) {
        this(new StringBuffer(buf));
    }

    /**
     * This method replaces one text to another
     *
     * @param what searching word
     * @param with replacing word
     */
    public void replace(String what, String with) {
        int index;
        int offset = 0;
        while ((index = innerBuffer.indexOf(what, offset)) > -1) {
            innerBuffer.replace(index, index + what.length(), with);
            lowercased.replace(index, index + what.length(), with.toLowerCase(Locale.ENGLISH));
            offset = index + with.length();
        }
    }

    /**
     * �������� ���� ������ �� ������ ��� ����� ��������
     *
     * @param what ��� ��������
     * @param with �� ��� ��������
     */
    public void replaceIgnoreCase(String what, String with) {
        int index;
        int offset = 0;
        while ((index = lowercased.indexOf(what.toLowerCase(Locale.ENGLISH), offset)) > -1) {
            innerBuffer.replace(index, index + what.length(), with);
            lowercased.replace(index, index + what.length(), with.toLowerCase(Locale.ENGLISH));
            offset = index + with.length();
        }
    }

    /**
     * ���������� ��������� ���������
     *
     * @return ���������
     */
    public StringBuffer getResult() {
        return innerBuffer;
    }

    /**
     * �������� ������
     *
     * @param n ��� ��������
     * @return ������������ ������
     */
    public static String encode(String n) {
        if (n == null) return null;
        return AccentedFilter.escape(new StringBuffer(n)).toString();
    }

    public HTMLEncoder cutSplitLine() {
        this.replace("\r\n", " ");
        this.replace("\n", " ");
        return this;
    }

    /**
     * �������� ������ ��� ������
     *
     * @param n ��� ��������
     * @return ������������ ������
     */
    public static String encodeTree(String n) {
        if (n == null) {
            return null;
        }
        n = n.replaceAll("\\n|\\r\\n", " ");
        return AccentedFilter.escape(new StringBuffer(backlashReplace(n))).toString();
    }

    public static String backlashReplace(String myStr){
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(myStr);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '\\') {
                result.append("/");
            } else {
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    /**
     * �������� ������� � ������� ������ ��� ������ � ���������
     */
    public void replaceForTextArea() {
        replaceIgnoreCase("&amp;", "&amp;amp;");
        replaceIgnoreCase("&quot;", "&amp;quot;");
        replaceIgnoreCase("&lt;", "&amp;lt;");
        replaceIgnoreCase("&gt;", "&amp;gt;");
        replaceIgnoreCase("&nbsp;", "&amp;nbsp;");
    }

    public static String escape2HTML(String text) {
        return text
                .replaceAll("&nbsp;", " ")
                .replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">");
    }

    /**
     * ������� �� ������ HTML-����
     *
     * @param from �������� �����
     * @return ������������ �����
     */
    public static String stripHtmlTags(String from) {
        if (from == null) return ""; // maximkr: required
        Lexer lexer = new Lexer(from);
        Parser parser = new Parser(lexer);
        TextExtractingVisitor visitor = new TextExtractingVisitor();
        try {
            parser.visitAllNodesWith(visitor);
        } catch (ParserException e) {
            return from;
        }
        return visitor.getExtractedText();
    }

    /**
     * ������� ��������� �� ������
     */
    public void removeAttributes() {
        StringBuffer sb = new StringBuffer();
        Lexer lexer = new Lexer(innerBuffer.toString());
        try {
            Node node;
            while (null != (node = lexer.nextNode())) {
                if (node instanceof TagNode) {
                    Tag tag = (TagNode) node;
                    for (String a : BAD_ATTRIBUTES)
                        if (!tag.getAttributesEx().isEmpty())
                            tag.removeAttribute(a);
                    sb.append(tag.toHtml());
                } else
                    sb.append(node.toHtml());
            }
        } catch (ParserException ex) {
            System.out.println("parser error");
        }
        HTMLEncoder coder = new HTMLEncoder(sb);
        coder.replaceTag("form");
        coder.replaceTag("script");
        coder.replaceTag("object");
        coder.replaceTag("embed");
        coder.replaceTag("iframe");
        coder.replaceTag("frameset");
        coder.replaceTag("frame");
        coder.replaceTag("applet");
        coder.replaceTag("noframes");
        coder.replaceTag("noscript");

        this.innerBuffer = coder.innerBuffer;
        this.lowercased = coder.lowercased;

    }

    /**
     * ������������ ������� ������ ������ �� ��� ���������
     *
     * @param s ������� ������
     * @return ������������ ������
     */
    public static String safe(String s) {
        HTMLEncoder c = new HTMLEncoder(s);
        c.removeAttributes();
        return c.toString();
    }

    /**
     * �������� � ������ ���
     *
     * @param tag ���
     */
    private void replaceTag(String tag) {
        replaceIgnoreCase("<" + tag, "< " + tag);
        replaceIgnoreCase("</" + tag, "< /" + tag);
    }

    /**
     * ����������� ����� � HTML
     *
     * @param str ������� ������
     * @return ������������ ������
     */
    public static String text2HTML(String str) {
        HTMLEncoder coder = new HTMLEncoder(new StringBuffer(encode(str)));
        coder.replace("\r\n", "<br/>");
        coder.replace("\n", "<br/>");
        coder.replace("  ", " &nbsp;");
        coder.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
        return coder.innerBuffer.toString();
    }

    public static String replaceNewLine(String str) {
        return str
                .replaceAll("\r\n", "<br/>")
                .replaceAll("\n", "<br/>")
                .replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }

    public static String html2Text(String str) {
        str = str.replaceAll("&nbsp;&nbsp;&nbsp;&nbsp;", "\t");
        str = str.replaceAll("<br />", "\n");
        str = str.replaceAll("<br/>", "\n");
        str = str.replaceAll("<br/>", "\n");
        str = str.replaceAll("<br>", "\n");
        str = str.replaceAll("<p>", "\n");
        str = str.replaceAll("</p>", "\n");
        return str.replaceAll("&nbsp;", " ");
    }

    /**
     * �������� html
     *
     * @param str    �������� ������
     * @param length ������� ��������
     * @return �������� ������
     */
    public static String htmlCut(String str, int length) {

        if (str == null || str.length() == 0)
            return "";
        if (str.indexOf('&') == -1) {
            if (str.length() > length)
                return str.substring(0, length) + "...";
            else
                return str;
        }
        StringBuffer sb = new StringBuffer();
        int i;
        int charLength = 0;
        boolean count = true;
        for (i = 0; i < str.length() && charLength < length; i++) {
            char ch = str.charAt(i);
            sb.append(ch);
            if (ch != '&' && count) {
                charLength++;
            } else {
                if (count) {
                    count = false;
                    continue;
                }
                if (ch == ';' || ch == ' ' || ch == '&') {
                    count = true;
                    charLength++;
                }
            }
        }
        if (i < str.length())
            sb.append("...");
        return sb.toString();
    }

    /**
     * ���������� ��������� ������������� �������
     *
     * @return ��������� �������������
     */
    public String toString() {
        return getResult().toString();
    }

    /**
     * �������� �� ������� ������ ������� �������� ������ ���� <br> �� �� ��������� �������
     *
     * @param str �������� ������
     * @return �������� ������
     */
    public static String br2nl(String str) {
        HTMLEncoder coder = new HTMLEncoder(new StringBuffer(str));
        coder.replace("<br>", "\r\n");
        coder.replace("<br/>", "\r\n");
        coder.replace("<br />", "\r\n");
        return coder.innerBuffer.toString();
    }

    public static String nl2br(String origin) {
        return origin.replaceAll("\n", "<br>");
    }
}