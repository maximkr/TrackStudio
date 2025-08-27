package com.trackstudio.tools;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateTransformModel;
import net.jcip.annotations.Immutable;

/**
 * Вспомогательный класс для форматирования таблиц из текста
 */
@Immutable
public class TableTransform implements TemplateTransformModel {

    private static final Log log = LogFactory.getLog(TableTransform.class);

    /**
     * Класс для записи данных
     */
    public static class TableTextWriter extends Writer {
        char separator = ':';
        //        char spec = '\\';
        Writer internal;
        StringBuffer[] column;
        int[] columnPos; // size = column.length+1
        int currentColumn;
        boolean inWhiteSpace = true;
        boolean inSeparator = false;

        /**
         * Конструктор
         *
         * @param internal  объект для вывода данных
         * @param columns   количество колонок
         * @param columnPos позиции колонок
         */
        public TableTextWriter(Writer internal, int columns, int[] columnPos) {
            this.internal = internal;
            if (columns < 2)
                columns = 2;
            this.column = new StringBuffer[columns];
            for (int c = 0; c < columns; c++) {
                this.column[c] = new StringBuffer();
            }
            if (columnPos == null) {
                columnPos = new int[columns + 1];
                columnPos[0] = 0;
                columnPos[1] = 40;
                columnPos[2] = 80;
            }
            this.columnPos = columnPos;
        }

        /**
         * Пишет данные
         *
         * @param cbuf что пишет
         * @param off  куда
         * @param len  длина записи
         * @throws IOException при необходимости
         */
        public void write(char cbuf[], int off, int len) throws IOException {
            for (int i = off, end = off + len; i < end; i++) {
                char c = cbuf[i];
                if (c == '\n' || c == '\r') {
                    feedLine();
                    inWhiteSpace = true;
                } else if (c == separator) {
                    if (inSeparator) {
                        feedColumn();
                        inWhiteSpace = true;
                        inSeparator = false;
                    } else {
                        inSeparator = true;
                    }
                } else if (Character.isWhitespace(c)) {
                    if (inSeparator)
                        column[currentColumn].append(separator);
                    inSeparator = false;
                    if (!inWhiteSpace)
                        column[currentColumn].append(' ');
                    inWhiteSpace = true;
                } else {
                    inWhiteSpace = false;
                    if (inSeparator)
                        column[currentColumn].append(separator);
                    inSeparator = false;
                    column[currentColumn].append(c);
                }
            }
        }

        private void feedColumn() {
            currentColumn++;
            if (currentColumn >= column.length) {
                currentColumn--;
                column[currentColumn].append(separator);
            }
        }

        /**
         * Обрабатывает линию
         *
         * @throws IOException при необходимости
         */
        public void feedLine() throws IOException {
            // тут нужно уже форматитровать по колонкам
            boolean clear = false;
            while (!clear) {
                StringBuffer row = new StringBuffer();
                int prevPos = 0;
                clear = true;
                for (int c = 0; c < column.length; c++) {
                    int pos = columnPos[c];
                    if (pos > 0) {
                        int gap = pos - prevPos;
                        if (gap > 0) {
                            char[] fill = new char[gap];
                            prevPos = pos;
                            Arrays.fill(fill, ' '); // fill with spaces;
                            row.append(fill);
                        } else {
                            pos = prevPos;
                        }
                    }
                    int nextPos = columnPos[c + 1];
                    if (column[c].length() < (nextPos - pos)) {
                        row.append(column[c]);
                        prevPos = prevPos + column[c].length();
                        column[c] = new StringBuffer();// empty
                    } else {
                        //multirow
                        //нужно найти точку разрыва до (nextPos-pos)
                        int prev = 0;
                        while (true) {
                            int spacePos = column[c].indexOf(" ", prev + 1);
                            if (spacePos < 0) {
                                if (prev == 0) {
                                    // пишем целиком
                                    row.append(column[c].substring(prev));
                                    prevPos = prevPos + column[c].length() - prev;
                                    column[c] = new StringBuffer();// empty
                                    break;
                                } else {
                                    // feed line
                                    column[c] = column[c].delete(0, prev + 1);
                                    clear = false;
                                    break;
                                }
                            } else if (spacePos < (nextPos - pos)) {
                                // just write
                                row.append(column[c].substring(prev, spacePos));
                                prevPos = prevPos + spacePos - prev;
                                prev = spacePos;
                            } else {
                                // feed line
                                column[c] = column[c].delete(0, prev + 1);
                                clear = false;
                                break;
                            }
                        }
                    }
                }
                // write out row
                row.append("\n");
                currentColumn = 0;
                internal.write(row.toString());
            }
        }

        /**
         * Сбрасывает данные
         *
         * @throws IOException при необходимости
         */
        public void flush() throws IOException {
            feedLine();
            internal.flush();
        }

        /**
         * ЗАкрывает поток
         *
         * @throws IOException при необходимости
         */
        public void close() throws IOException {
        }
    }

    /**
     * Возвращает объект для вывода в поток
     *
     * @param writer объект для вывода
     * @param map    карта параметров
     * @return объект для вывода
     * @throws TemplateModelException при необходимости
     * @throws IOException            при необходимости
     */
    public Writer getWriter(Writer writer, Map map) throws TemplateModelException, IOException {
        int columns = 2;
        int[] cols = null;
        if (map != null) {
            try {
                TemplateNumberModel num = (TemplateNumberModel) map.get("columns");
                if (num != null)
                    columns = num.getAsNumber().intValue();
            } catch (ClassCastException e) {
                throw new TemplateModelException("Expecting numerical argument to columns");
            }
            try {
                TemplateScalarModel flag = (TemplateScalarModel) map.get("positions");
                if (flag != null) {
                    cols = new int[columns + 1];
                    int cur = 0;
                    String positions = flag.getAsString();
                    StringTokenizer tk = new StringTokenizer(positions, ",");
                    while (tk.hasMoreElements()) {
                        String pos = tk.nextToken().trim();
                        cols[cur] = Integer.parseInt(pos);
                        cur++;
                    }
                }
            } catch (ClassCastException e) {
                throw new TemplateModelException("Expecting boolean argument to positions");
            }
        }
        return getWriter(writer, columns, cols);
    }

    private TableTextWriter getWriter(Writer writer, int columns, int[] cols) {
        return new TableTextWriter(writer, columns, cols);
    }

    /**
     * Для проверки
     *
     * @param args аргументы
     */
    public static void main(String[] args) {
        System.out.println("+123456789+123456789+123456789+123456789+123456789+123456789+123456789+123456789+123456789+123456789+123456789+123456789+");

        TableTransform o = new TableTransform();
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        /*
        Writer w = o.getWriter(writer, 2, new int[]{0,30,80});

        try {
            w.write("Submitter Department:                         Administrative");
            w.cleanSession();
            w.write("Submitter Department: Administrative");
            w.cleanSession();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        try {
            w.write("Submitter Department Archeticture:            Administrative");
            w.cleanSession();
            w.write("Submitter Department: Administrative //To change body of catch statement use File | Settings | File Templates.");
            w.cleanSession();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        try {
            w.write("Submitter Department Archeticture //To change body of catch statement use File | Settings | File Templates.:           Administrative");
            w.cleanSession();
            w.write("Submitter Department //To change body of catch statement use File | Settings | File Templates.: Administrative //To change body of catch statement use File | Settings | File Templates.");
            w.cleanSession();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
          */
        Writer t = o.getWriter(writer, 3, new int[]{0, 30, 80, 120});
        try {
//            t.write("Это первая колонка: This is a second column: return getWriter(writer, columns, cols);");
//            t.cleanSession();
//            t.write("Это первая колонка Это первая колонка Это первая колонка: This is a second column This is a second column This is a second column: return getWriter(writer, columns, cols); return getWriter(writer, columns, cols);");
//            t.cleanSession();
            t.write("ЭтоперваяколонкаЭтоперваяколонка Это первая колонка: This is a second column This is a second column This is a second column: return getWriter(writer, columns, cols); return getWriter(writer, columns, cols);");
            t.flush();

        } catch (IOException e) {
            log.error("Exception ", e);
        }
    }
}