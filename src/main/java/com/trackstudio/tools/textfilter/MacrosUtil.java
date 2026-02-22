package com.trackstudio.tools.textfilter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.kevinsawicki.http.HttpRequest;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.macros.SimpleNameLink;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.AttachmentManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.model.Attachment;
import com.trackstudio.model.Filter;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.ParserNumber;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortUdf;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.view.TaskView;
import com.trackstudio.view.TaskViewEmailText;
import com.trackstudio.view.TaskViewHTML;

import net.iharder.Base64;
import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.Null.isNull;

@Immutable
public class MacrosUtil {
    private static final Log log = LogFactory.getLog(MacrosUtil.class);
    public static final String[][] keyboardUS_RU = {
            {"q","й"}, {"w","ц"}, {"e","у"}, {"r","к"}, {"t","е"}, {"y","н"}, {"u","г"}, {"i","ш"}, {"o","щ"}, {"p","з"}, {"\\[","х"}, {"\\]","ъ"},
            {"a","ф"}, {"s","ы"}, {"d","в"}, {"f","а"}, {"g","п"}, {"h","р"}, {"j","о"}, {"k","л"}, {"l","д"}, {";","ж"}, {"\"","э"},
            {"z","я"}, {"x","ч"}, {"c","с"}, {"v","м"}, {"b","и"}, {"n","т"}, {"m","ь"}, {",","б"}, {"\\.","ю"}
    };

    public static final Map<String, String> matchesLatinRu = new LinkedHashMap<String, String>();
    static {
        String[][] array = {
                {"й","y"}, {"ц","c"}, {"у","u"}, {"к","k"}, {"е","e"}, {"н","n"}, {"г","g"}, {"ш","sh"}, {"щ","sch"}, {"з","z"}, {"х","h"}, {"ъ",""},
                {"ф","f"}, {"ы","y"}, {"в","v"}, {"а","a"}, {"п","p"}, {"р","r"}, {"о","o"}, {"л","l"}, {"д","d"}, {"ж","j"}, {"э","e"},
                {"я","ya"}, {"ч","ch"}, {"с","s"}, {"м","m"}, {"и","i"}, {"т","t"}, {"ь",""}, {"б","b"}, {"ю","yu"}
        };
        for (String[] value : array) {
            matchesLatinRu.put(value[0], value[1]);
        }
    }

    /**
     * <a class="example-image-link" href="/TrackStudio/download/task/29721/4028816a5f8d8ecf015f8dacdf250094" data-lightbox="example-1">
     <img alt="" src="/TrackStudio/TSImageServlet?attId=4028816a5f8d8ecf015f8dacdf250094&amp;width=100&amp;height=75" hspace="0" vspace="0">
     </a>
     */
    public static final String lyteImg = "<a href=\"%s/download/task/%s/%s?type=image\" data-lightbox=\"%s\">" +
            "<img src=\"%s/TSImageServlet?attId=%s&amp;width="+Config.getProperty("trackstudio.preview.image.width", "400")+"&amp;height="+Config.getProperty("trackstudio.preview.image.height", "400")+"\" alt=\"\" hspace=\"0\" vspace=\"0\" />" +
            "</a>";

    public static int getIntegerOrDefault(String property, int def) {
        Integer value = getIntegerOrNull(property);
        return value != null ? value : def;
    }

    public enum InputLocal {
        US, RU
    }

    public static String divTextByKey(String desc, int limit, String key) {
        StringBuilder bf = new StringBuilder("<span style=\"font-family: arial, helvetica, sans-serif;\">");
        int pos = limit;
        while (desc.length() > pos) {
            bf.append(desc.substring(pos - limit, pos)).append(key);
            pos += limit;
        }
        bf.append(desc.substring(pos - limit, desc.length())).append(key).append("</span>");
        return bf.toString();
    }

    public static String buildImageForState(SecuredStatusBean ssb, String url) {
        String color = ssb.getColor() != null ? ssb.getColor() : "transparent";
        String safeName = HTMLEncoder.encode(ssb.getName());
        if (safeName == null) {
            safeName = "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<img title=\"").append(safeName)
                .append("\" class=\"fancytree-icon state\" border=\"0\" style=\"margin-left: 0 !important; --sc: ")
                .append(color).append(";\" src=\"").append(url).append(ssb.getImage()).append("\"/>");
        return sb.toString();
    }

    public static String getFullPath(SessionContext sc, int number, String delime) {
        try {
            StringBuilder bf = new StringBuilder();
            String byNumber = KernelManager.getTask().findByNumber(String.valueOf(number));
            if (byNumber != null) {
                TaskRelatedInfo tci = TaskRelatedManager.getInstance().find(byNumber);
                if (null != tci) {
                    Iterator i1 = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, tci.getId()).iterator();
                    if (i1.hasNext()) {
                        SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                        bf.append(stb.getName());
                    }
                    while (i1.hasNext()) {
                        SecuredTaskBean stb = (SecuredTaskBean) i1.next();
                        bf.append(delime);
                        bf.append(stb.getName());
                    }
                    return bf.toString();
                }
            }
        } catch (Exception e) {
            log.error("Exception ", e);
        }
        return "";
    }

    public static int parseNumberString(String sb) {
        Pattern filterPattern = Pattern.compile("#(\\d+)");
        Matcher matcher = filterPattern.matcher(sb);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return -1;
            }
        } else return -1;

    }

    public static boolean goodPreviousChars(StringBuffer sb, int index) {
        if (index == 0 || sb.charAt(index - 1) == ' ' || sb.charAt(index - 1) == '\n')
            return true;
        if (index > 5) {
            if (sb.charAt(index - 1) == ';' && sb.charAt(index - 2) == 'p' && sb.charAt(index - 3) == 's' && sb.charAt(index - 4) == 'b' && sb.charAt(index - 5) == 'n' && sb.charAt(index - 6) == '&')
                return true;
            if (sb.charAt(index - 1) == '>' && sb.charAt(index - 2) == '/' && sb.charAt(index - 3) == ' ' && sb.charAt(index - 4) == 'r' && sb.charAt(index - 5) == 'b' && sb.charAt(index - 6) == '<')
                return true;
        }
        if (sb.charAt(index - 1) == ',' || sb.charAt(index - 1) == ';' || sb.charAt(index - 1) == '.' || sb.charAt(index - 1) == '\"' || sb.charAt(index - 1) == '\'')
            return true;
        if (index > 3) {
            if (sb.charAt(index - 1) == '>' && sb.charAt(index - 2) == 'r' && sb.charAt(index - 3) == 'b' && sb.charAt(index - 4) == '<')
                return true;
        }
        if (index > 4) {
            if (sb.charAt(index - 1) == '>' && sb.charAt(index - 2) == '/' && sb.charAt(index - 3) == 'r' && sb.charAt(index - 4) == 'b' && sb.charAt(index - 5) == '<')
                return true;
        }
        return false;
    }

    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     *
     * @param tc     пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
     * @param flthm  пїЅпїЅпїЅпїЅпїЅпїЅ
     * @param locale пїЅпїЅпїЅпїЅпїЅпїЅ
     * @return пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @throws com.trackstudio.exception.GranException
     *          пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     */
    public static LinkedHashMap<String, String> getHeadersMap(SecuredTaskBean tc, FValue flthm, String locale) throws GranException {
        try {
            List<String> view = flthm.getView();
            List<Pair<String>> list = new ArrayList<Pair<String>>();
            for (String key : view) {
                Pair<String> pair = getPair(key, locale);
                if (pair != null) {
                    list.add(pair);
                }
            }
            List<SecuredUDFBean> udfcol = tc.getFilterUDFs();
            Collections.sort(udfcol, new Comparator<SecuredUDFBean>() {
                @Override
                public int compare(SecuredUDFBean o1, SecuredUDFBean o2) {
                    return o1.getOrder().compareTo(o2.getOrder());
                }
            });
            for (SecuredUDFBean udf : udfcol) {
                for (String key : view) {
                    Pair<String> pair = getPair(key, udf);
                    if (pair != null) {
                        list.add(pair);
                    }
                }
            }
            Comparator<Pair<String>> cmpPair = new Comparator<Pair<String>>() {
                @Override
                public int compare(Pair o1, Pair o2) {
                    Integer value1 = Integer.parseInt(o1.getValueSort());
                    Integer value2 = Integer.parseInt(o2.getValueSort());
                    return value1.compareTo(value2);
                }
            };
            Block<String, String, String> lock = new Block<String, String, String>() {
                @Override
                public void make(Map<String, String> map, Pair<String> pair) {
                    map.put(pair.getKey(), pair.getValue());
                }
            };
            return sortMap(list, cmpPair, lock);
        } catch (Exception e) {
            throw new GranException(e);
        }
    }


    public interface Block<T, K, S> {
        void make(final Map<T, K> map, Pair<S> pair);
    }

    public static <T, K, S> LinkedHashMap<T, K> sortMap(List<Pair<S>> list, Comparator<Pair<S>> comparator, Block<T, K, S> block) {
        LinkedHashMap<T, K> map = new LinkedHashMap<T, K>();
        Collections.sort(list, comparator);
        for (Pair<S> data : list) {
            block.make(map, data);
        }
        return map;
    }

    public static Pair<String> getPair(String key, String locale) throws GranException {
        if (key.equals(FieldMap.TASK_NUMBER.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_NUMBER.getFilterKey(), I18n.getString(locale, "TASK_NUMBER"), "0");
        }
        if (key.equals(FieldMap.FULLPATH.getFilterKey())) {
            return new Pair<String>(FieldMap.FULLPATH.getFilterKey(), I18n.getString(locale, "RELATIVE_PATH"), "1");
        }
        if (key.equals(FieldMap.TASK_NAME.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_NAME.getFilterKey(), I18n.getString(locale, "NAME"), "2");
        }
        if (key.equals(FieldMap.TASK_SHORTNAME.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_SHORTNAME.getFilterKey(), I18n.getString(locale, "ALIAS"), "3");
        }
        if (key.equals(FieldMap.TASK_CATEGORY.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_CATEGORY.getFilterKey(), I18n.getString(locale, "CATEGORY"), "4");
        }
        if (key.equals(FieldMap.TASK_STATUS.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_STATUS.getFilterKey(), I18n.getString(locale, "TASK_STATE"), "5");
        }
        if (key.equals(FieldMap.TASK_RESOLUTION.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_RESOLUTION.getFilterKey(), I18n.getString(locale, "RESOLUTION"), "6");
        }
        if (key.equals(FieldMap.TASK_PRIORITY.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_PRIORITY.getFilterKey(), I18n.getString(locale, "PRIORITY"), "7");
        }
        if (key.equals(FieldMap.SUSER_NAME.getFilterKey())) {
            return new Pair<String>(FieldMap.SUSER_NAME.getFilterKey(), I18n.getString(locale, "SUBMITTER"), "8");
        }
        if (key.equals(FieldMap.SUSER_STATUS.getFilterKey())) {
            return new Pair<String>(FieldMap.SUSER_STATUS.getFilterKey(), I18n.getString(locale, "SUBMITTER_STATUS"), "9");
        }
        if (key.equals(FieldMap.HUSER_NAME.getFilterKey())) {
            return new Pair<String>(FieldMap.HUSER_NAME.getFilterKey(), I18n.getString(locale, "HANDLER"), "10");
        }
        if (key.equals(FieldMap.HUSER_STATUS.getFilterKey())) {
            return new Pair<String>(FieldMap.HUSER_STATUS.getFilterKey(), I18n.getString(locale, "HANDLER_STATUS"), "11");
        }
        if (key.equals(FieldMap.TASK_SUBMITDATE.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_SUBMITDATE.getFilterKey(), I18n.getString(locale, "SUBMIT_DATE"), "12");
        }
        if (key.equals(FieldMap.TASK_UPDATEDATE.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_UPDATEDATE.getFilterKey(), I18n.getString(locale, "UPDATE_DATE"), "13");
        }
        if (key.equals(FieldMap.TASK_CLOSEDATE.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_CLOSEDATE.getFilterKey(), I18n.getString(locale, "CLOSE_DATE"), "14");
        }
        if (key.equals(FieldMap.TASK_DEADLINE.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_DEADLINE.getFilterKey(), I18n.getString(locale, "DEADLINE"), "15");
        }
        if (key.equals(FieldMap.TASK_BUDGET.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_BUDGET.getFilterKey(), I18n.getString(locale, "BUDGET"), "16");
        }
        if (key.equals(FieldMap.TASK_ABUDGET.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_ABUDGET.getFilterKey(), I18n.getString(locale, "ABUDGET"), "17");
        }
        if (key.equals(FieldMap.TASK_CHILDCOUNT.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_CHILDCOUNT.getFilterKey(), I18n.getString(locale, "SUBTASKS_AMOUNT"), "18");
        }
        if (key.equals(FieldMap.TASK_MESSAGECOUNT.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_MESSAGECOUNT.getFilterKey(), I18n.getString(locale, "MESSAGES_AMOUNT"), "19");
        }
        if (key.equals(FieldMap.TASK_DESCRIPTION.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_DESCRIPTION.getFilterKey(), I18n.getString(locale, "DESCRIPTION"), "20");
        }
        if (key.equals(FieldMap.TASK_PARENT.getFilterKey())) {
            return new Pair<String>(FieldMap.TASK_PARENT.getFilterKey(), I18n.getString(locale, "TASK_PARENT"), "21");
        }
        return null;
    }

    public static Pair<String> getPair(String key, SecuredUDFBean udf) {
        if (key.equals("_udf_" + udf.getId())) {
            return new Pair<String>("_udf_" + udf.getId(), udf.getCaption(), String.valueOf(Integer.MAX_VALUE));
        }
        if (key.equals("UDF" + udf.getId())) {
            return new Pair<String>("UDF" + udf.getId(), udf.getCaption(), String.valueOf(Integer.MAX_VALUE));
        }
        return null;
    }

    public static HashMap<String, HashMap<String, String>> getTasksMap(Map<String, String> headersMap, List<SecuredTaskBean> list, TaskView view) throws GranException {
        HashMap<String, HashMap<String, String>> tasksMap = new HashMap<String, HashMap<String, String>>();
        String locale = view.getTask().getSecure().getLocale();
        for (SecuredTaskBean task : list) {
            HashMap<String, String> taskValuesMap = new HashMap<String, String>();
            TaskView taskView = view.getView(task);

            Set<String> hdrs = headersMap.keySet();

            if (hdrs.contains(FieldMap.TASK_DESCRIPTION.getFilterKey())) {
                if (view instanceof TaskViewEmailText)
                    taskValuesMap.put(FieldMap.TASK_DESCRIPTION.getFilterKey(), new Wiki(taskView).toMacros(task.getTextDescription()));
                else
                    taskValuesMap.put(FieldMap.TASK_DESCRIPTION.getFilterKey(), new Wiki(taskView).toMacros(task.getDescription()));
            }
            if (hdrs.contains(FieldMap.TASK_PARENT.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_PARENT.getFilterKey(), task.getParent() != null ? task.getParent().getName() : "");
            }
            if (hdrs.contains(FieldMap.TASK_NUMBER.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_NUMBER.getFilterKey(), task.getNumber());
            }
            if (hdrs.contains(FieldMap.FULLPATH.getFilterKey())) {
                taskValuesMap.put(FieldMap.FULLPATH.getFilterKey(), taskView.getFullPath());
            }
            if (hdrs.contains(FieldMap.TASK_NAME.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_NAME.getFilterKey(), taskView.getName());
            }
            if (hdrs.contains(FieldMap.TASK_SHORTNAME.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_SHORTNAME.getFilterKey(), taskView.getAlias());
            }
            if (hdrs.contains(FieldMap.TASK_CATEGORY.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_CATEGORY.getFilterKey(), taskView.getCategory());
            }
            if (hdrs.contains(FieldMap.TASK_STATUS.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_STATUS.getFilterKey(), taskView.getStatus());
            }
            if (hdrs.contains(FieldMap.TASK_RESOLUTION.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_RESOLUTION.getFilterKey(), taskView.getResolution());
            }
            if (hdrs.contains(FieldMap.TASK_PRIORITY.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_PRIORITY.getFilterKey(), taskView.getPriority());
            }
            if (hdrs.contains(FieldMap.SUSER_NAME.getFilterKey())) {
                taskValuesMap.put(FieldMap.SUSER_NAME.getFilterKey(), taskView.getSubmitter());
            }
            if (hdrs.contains(FieldMap.SUSER_STATUS.getFilterKey())) {
                taskValuesMap.put(FieldMap.SUSER_STATUS.getFilterKey(), taskView.getSubmitterPrstatuses());
            }
            if (hdrs.contains(FieldMap.HUSER_NAME.getFilterKey())) {
                taskValuesMap.put(FieldMap.HUSER_NAME.getFilterKey(), taskView.getHandler());
            }
            if (hdrs.contains(FieldMap.HUSER_STATUS.getFilterKey())) {
                taskValuesMap.put(FieldMap.HUSER_STATUS.getFilterKey(), taskView.getHandlerPrstatuses());
            }
            if (hdrs.contains(FieldMap.TASK_SUBMITDATE.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_SUBMITDATE.getFilterKey(), taskView.getSubmitdate());
            }
            if (hdrs.contains(FieldMap.TASK_UPDATEDATE.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_UPDATEDATE.getFilterKey(), taskView.getUpdatedate());
            }
            if (hdrs.contains(FieldMap.TASK_CLOSEDATE.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_CLOSEDATE.getFilterKey(), taskView.getClosedate());
            }
            if (hdrs.contains(FieldMap.TASK_DEADLINE.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_DEADLINE.getFilterKey(), taskView.getDeadline());
            }
            if (hdrs.contains(FieldMap.TASK_BUDGET.getFilterKey())) {

                HourFormatter budget = new HourFormatter(task.getBudget(), task.getBudgetFormat(), locale);

                taskValuesMap.put(FieldMap.TASK_BUDGET.getFilterKey(), budget.getString());
            }
            if (hdrs.contains(FieldMap.TASK_ABUDGET.getFilterKey())) {
                HourFormatter abudget = new HourFormatter(task.getActualBudget(), task.getBudgetFormat(), locale);
                taskValuesMap.put(FieldMap.TASK_ABUDGET.getFilterKey(), abudget.getString());
            }
            if (hdrs.contains(FieldMap.TASK_CHILDCOUNT.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_CHILDCOUNT.getFilterKey(), task.getTotalChildrenCount().toString());
            }
            if (hdrs.contains(FieldMap.TASK_MESSAGECOUNT.getFilterKey())) {
                taskValuesMap.put(FieldMap.TASK_MESSAGECOUNT.getFilterKey(), task.getMessageCount().toString());
            }

            ArrayList<SecuredUDFValueBean> udfcol = task.getUDFValuesList();
            Collections.sort(udfcol, new SortUdf(FieldSort.ORDER));
            for (SecuredUDFValueBean udf : udfcol) {
                if (udf != null) {
                    if (hdrs.contains(FValue.UDF + udf.getId()))
                        taskValuesMap.put(FValue.UDF + udf.getId(), view.getUDFValueView(udf).getValue(task));
                }
            }
            tasksMap.put(task.getId(), taskValuesMap);
        }
        return tasksMap;
    }


    public static String getFilterId(String nameFilter, String taskId, String userId, boolean replaceNbsc) throws GranException {
        if (nameFilter.contains("|num:")) {
            nameFilter = nameFilter.substring(0, nameFilter.indexOf("|num:"));
        }
        if (replaceNbsc) {
            nameFilter = nameFilter.replaceAll("&nbsp;", " ");
        }
        List<Filter> listFilter = KernelManager.getFilter().getTaskFilterList(taskId, userId);
        for (Filter filter : listFilter) {
            if (nameFilter.equals(filter.getName())) {
                return filter.getId();
            }
        }
        return null;
    }

    public static <T> List<List<T>> getMatrixAttachment(List<T> list, int column) throws GranException {
        List<List<T>> attachments = new ArrayList<List<T>>();
        int countColumn = 0;
        List<T> tempList = new ArrayList<T>();
        if (list != null) {
            for (T value : list) {
                countColumn++;
                if (countColumn <= column) {
                    tempList.add(value);
                }
                if (countColumn == column) {
                    attachments.add(tempList);
                    tempList = new ArrayList<T>();
                    countColumn = 0;
                }
            }
        }
        if (countColumn != 0) {
            attachments.add(tempList);
        }
        return attachments;
    }

    public static String buildRealURL(HttpServletRequest request) {
        return request.getContextPath();
    }

    public static Integer getIntegerOrNull(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String readFile(File file) {
        StringBuilder sb = new StringBuilder();
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        try {
            fstream = new FileInputStream(file);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                sb.append(strLine).append("\n");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (br != null)
                    br.close();
                if (in != null)
                    in.close();
                if (fstream != null)
                    fstream.close();
            } catch (IOException ignore) {}
        }
        return sb.toString();
    }

    public static String convertWord(String value, InputLocal type) throws GranException {
        String path = Config.getInstance().getPluginsDir();
        String text =  readFile(new File(path + "convert/ru_en.txt"));
        String old = value;
        for (String line : text.split("\n")) {
            String[] pair = line.split("=");
            String en = pair[0];
            String ru = pair[1];
            if (type == InputLocal.US) {
                value = value.replaceAll(en, ru);
            } else {
                if (!en.equals("\\.")) {
                    value = value.replaceAll(ru, en);
                }
            }
        }
        log.debug("convert work before : " + old + " after : " + value);
        return value;
    }

    public static int parseNumberOfImage(String name) {
        int count = 0;
        if (name.matches("(attach_)(\\d+)(\\.png)")) {
            Integer fileNumber = (Integer) ParserNumber.parseValueOrNull(name.replaceAll("[^\\d+]", ""), Integer.class);
            if (fileNumber != null && fileNumber > count) {
                count = fileNumber;
            }
        }
        return count;
    }

    private static int initNumberOfImages(String taskId) throws GranException {
        int count=0;
        if (taskId != null) {
            for (AttachmentCacheItem cacheItem : AttachmentManager.getAttachment().getAttachmentList(taskId, null, null)) {
                count = parseNumberOfImage(cacheItem.getName());
            }
        }
        return count;
    }

    public static List<Pair<byte[]>> convertImagesToByte(String text, StringBuffer sb) {
        List<Pair<byte[]>> images = new ArrayList<Pair<byte[]>>();
        if (!isNull(text)) {
            Pattern filterPattern = Pattern.compile("(<img src=\")(.*?)(\")");
            Matcher matcher = filterPattern.matcher(text);
            while (matcher.find()) {
                String url = matcher.group(2);
                String contentId = String.valueOf(url.hashCode());
                images.add(new Pair<byte[]>(contentId, "", readByteFromURL(url), true));
                if (sb != null) matcher.appendReplacement(sb, "<img src=\"cid:"+contentId+"\"");
            }
            if (sb != null) matcher.appendTail(sb);
        }
        return images;
    }

    public static String convertUrlToText(String text) {
        if (isNull(text)) return "";
        Pattern filterPattern = Pattern.compile("(<img)(.*?)(src=\")(.*?)(\")(.*?)(/?)(>)");
        Matcher matcher = filterPattern.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String url = matcher.group(4);
            byte[] imgByte = readByteFromURL(url);
            if (imgByte.length > 0) {
                String image = "<img src=\"data:image/png;base64," + Base64.encodeBytes(imgByte) + "\"/>";
                matcher.appendReplacement(result, image);
            }
        }
        matcher.appendTail(result);
        String resultTxt = result.toString();
        return resultTxt.isEmpty() ? text : resultTxt;
    }

    public static byte[] readByteFromURL(String url) {
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        url = url.replaceAll("&amp;", "&");
        try {
            if (url.contains("/TSImageServlet?attId=")) {
                url = url.substring(url.indexOf("/TSImageServlet?attId=") + "/TSImageServlet?attId=".length(), url.length());
                url = url.substring(0, url.indexOf("&"));
                Attachment attachment = KernelManager.getFind().findAttachment(url);
                File file = AttachmentManager.getInstance().getAttachmentFile(attachment.getTask().getId(), null, attachment.getId(), false);
                bais.write(FileUtils.readFileToByteArray(file));
            } else {
                url = url.startsWith("http") ? url : Config.getProperty("trackstudio.siteURL") + url;
                fetchFileFromUrl(url, bais);
            }
        } catch (Exception e) {
            log.error("Could not get image from url : " + url);
        }
        return bais.toByteArray();
    }

    /**
     * This method fetches a file from url. This method supports http/https connection
     * @param url URL http or https
     * @param output outstream where will be stored file
     * @throws IOException for unpredictable situation
     */
    public static void fetchFileFromUrl(final String url, final OutputStream output) throws IOException {
        HttpRequest request = HttpRequest.post(url);
        request.trustAllCerts();
        request.trustAllHosts();
        HttpURLConnection conn = request.getConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        BufferedInputStream reader = new BufferedInputStream(conn.getInputStream(), 51200);
        try {
            try {
                IOUtils.copy(reader, output);
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public static String parseImagesFromText(final List<AttachmentArray> atts, String text, String taskId) throws GranException {
        StringBuffer sb = new StringBuffer();
        Pattern filterPattern = Pattern.compile("(<img)(.*?)(src=\"data:image/png;base64,)(.*?)(\")(.*?)(/?)(>)");
        Matcher matcher = filterPattern.matcher(text);
        int count = initNumberOfImages(taskId);
        String context = Config.getProperty("trackstudio.siteURL");
        while (matcher.find()) {
            String imageText = matcher.group(4);
            String nameAttach = "attach_" + (++count) + ".png";
            byte[] image = DatatypeConverter.parseBase64Binary(imageText);
            ByteArrayInputStream is = new ByteArrayInputStream(image);
            AttachmentArray arr = new AttachmentArray(SafeString.createSafeString(nameAttach), SafeString.createSafeString(""), is);
            arr.setTinyMCEImage(true);
            arr.setContext(context);
            atts.add(arr);
            matcher.appendReplacement(sb, nameAttach);
        }
        matcher.appendTail(sb);
        if (sb.toString().isEmpty()) {
            return text;
        }  else {
            return sb.toString();
        }
    }


    public static String removeLocaleCharacters(String str, boolean name, boolean convert) {
        String ret = "";
        char[] spec = {'>', '<', '|', '?', '*', '/', '\\', ':', '\"'};
        Arrays.sort(spec);
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            if (Arrays.binarySearch(spec, aChar) < 0) {
                if (name) {
                    ret += aChar;
                } else {
                    if (aChar < 128) {
                        ret += aChar;
                    } else {
                        String latin = matchesLatinRu.get(String.valueOf(aChar).toLowerCase());
                        ret += latin != null && convert ? latin : '-';

                    }
                }
            } else {
                ret += '-';
            }
        }
        return ret;
    }

    public static List<SecuredTaskBean> getListTask(SecuredTaskBean taskInfo, TaskFValue taskFValue, String filterId) throws GranException {
        TaskFilter taskList = new TaskFilter(taskInfo);
        FilterSettings filterSettings = new FilterSettings(taskFValue, taskInfo.getId(), filterId);
        boolean taskUDFView = filterSettings.getSettings().needFilterUDF() && !taskInfo.getFilterUDFs().isEmpty();
        boolean notwithsub = taskFValue.get(FValue.SUBTASK) == null;
        return taskList.getTaskList(taskFValue, taskUDFView, notwithsub, filterSettings.getSortedBy());
    }

    /**
     * This method convert tags img from outlook message
     * @param text text
     * @param arrays attachments
     * @return replace's text
     */
    public static String convertContentImg(String text, List<AttachmentArray> arrays) {
//        log.debug(String.format("body %s attaches %s", text, arrays));
        Pattern pattern = Pattern.compile("(<img)([^>]*)(\"cid:)([^>,^\"]*)(\")([^>]*)(>)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String contentId = matcher.group(4);
            String name = matcher.group();
            for (AttachmentArray attach : arrays) {
                if (contentId.equals(attach.getContentId())) {
                    name = attach.getName().toString();
                    break;
                }
            }
            matcher.appendReplacement(sb, name);
        }
        matcher.appendTail(sb);
        return sb.toString().isEmpty() ? text : sb.toString();
    }


    /**
     * This method cuts the the first line from description
     * @param desc description
     * @return line
     */
    public static String cutLine(final String desc) {
        String result = desc;
        if (result.contains("\r\n")) {
            result = result.split("\r\n")[0];
        }
        if (result.contains("<br>")) {
            result = result.split("<br>")[0];
        }
        if (result.contains("<BR>")) {
            result = result.split("<BR>")[0];
        }
        if (result.contains("<br/>")) {
            result = result.split("<br/>")[0];
        }
        if (result.contains("<BR/>")) {
            result = result.split("<BR/>")[0];
        }
        return cutHtml(result).replaceAll("\\s+", " ").replaceAll("\r\n", "");
    }

    /**
     * This method cuts the html by reg exp from string
     * @param value input string
     * @return result without html
     */
    public static String cutHtml(String value) {
        return value.replaceAll("\\<.*?>","");
    }

    public static String getRandomGUID() {
        String s_id = "";
        try {
            s_id = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        StringBuffer sbValueBeforeMD5 = new StringBuffer(s_id);
        sbValueBeforeMD5.append(":");
        sbValueBeforeMD5.append(Long.toString(System.currentTimeMillis()));
        sbValueBeforeMD5.append(":");
        sbValueBeforeMD5.append(Long.toString(new Random(new SecureRandom().nextLong()).nextLong()));
        return getMD5(sbValueBeforeMD5.toString());
    }

    public static String getMD5(String str) {
        try {
            if (str == null)
                return null;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte hash[] = md.digest(str.getBytes("UTF-8"));
            StringBuffer buf = new StringBuffer(hash.length * 2);

            for (byte b : hash) {
                if ((b & 0xff) < 0x10)
                    buf.append("7");
                buf.append(Long.toString(b & 0xff, 16));
            }
            String enc = buf.toString();
            enc = enc.toLowerCase(Locale.ENGLISH);
            return enc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void buildLabelUdf(final StringBuilder sb, String id, SecuredUserBean user, int index, boolean checked) throws GranException {
        sb.append("<label for='").append(id).append("_").append(user.getId()).append("' class='sel").append(index % 2).append("'>");
        sb.append("<input type='checkbox' ").append(checked ? "checked" : "").append(" onclick=\"moveToParticipate(this.id, this.checked, 'participants_');\" name=\"udflist(").append(id).append(")\" value='").append(user.getId()).append("' id='").append(id).append("_").append(user.getId()).append("'/>");
        sb.append("<span class=\"user\" ").append(user.getId().equals(user.getSecure().getUserId()) ? "id='loggedUser'" : "").append("}>");
        sb.append(user.getName());
        sb.append(" [").append(user.getPrstatus().getName()).append("]");
        sb.append("</span>");
        sb.append("</label>");
    }

    public static Long calendarToMls(Calendar cal, Long def) {
        Long rsl = def;
        if (cal != null) {
            rsl = cal.getTimeInMillis();
        }
        return rsl;
    }

    public static Calendar mlsToCalendar(Long mls, Calendar def) {
        Calendar cal = def;
        if (mls != null && mls != 0) {
            cal = Calendar.getInstance();
            cal.setTimeInMillis(mls);
        }
        return cal;
    }
}
