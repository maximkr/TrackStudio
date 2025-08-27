package com.trackstudio.secured;

import java.util.Locale;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.tools.PropertyComparable;

import net.jcip.annotations.Immutable;

@Immutable
public abstract class SecuredSearchItem extends PropertyComparable {
    protected final String word;
    protected final int pos = 0;
    protected final String surroundText;
    protected final SessionContext sc;

    public SecuredSearchItem(String surroundText, SessionContext sc, String word) {
        this.surroundText = surroundText;
        this.sc = sc;
        this.word = word;
    }

    public int getPos() {
        return pos;
    }

    public String getSurroundText() {
        return surroundText;
    }

    public String getHighlightText(String text) {
        //делаю так, потому что непонятно как через люценовский хайлайтер сделать, у него другие цели
        String word = this.word.toLowerCase(Locale.ENGLISH);
        String _text = text.toLowerCase(Locale.ENGLISH);
        String[] parts = _text.split(word);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
            pos += parts[i].length();
            if (i < parts.length - 1) {
                sb.append("<cite>");
                sb.append(text.substring(pos, pos + word.length()));
                sb.append("</cite>");
            }
        }
        return sb.toString();
    }

    public abstract String getName();

    public abstract String getHighlightName();

    public abstract String getId();


}
