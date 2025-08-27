package com.trackstudio.app.adapter;

import java.util.regex.Pattern;

import com.trackstudio.exception.GranException;
import com.trackstudio.view.TaskView;

public interface MacrosTaskAdapter extends Adapter {
	public final static Pattern optionPattern =Pattern.compile("'?([^']+?)'?:\\s*'([^'].+?)'(,|$)");
    /**
     * Через ИЛИ, т.к. скобки бывают и у фильтров
     * 1 или 5 номер
     * 2 или 6 тип макроса
     * 3 или 7 название фильтра
     * 4 опции
     */
    public final static Pattern filterPattern = Pattern.compile("#(\\d+)\\{(\\w+?):\\s?([^\\<\\r\\n]+?)(?:\\,\\s*\\()(.+?)\\)\\}|#(\\d+)\\{(\\w+?):\\s?(.+?)\\}");
    public String convert(TaskView view, String current) throws GranException;
}
