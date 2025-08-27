package com.trackstudio.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.udf.CalendarValue;
import com.trackstudio.app.udf.FloatValue;
import com.trackstudio.app.udf.GenericValue;
import com.trackstudio.app.udf.LinkValue;
import com.trackstudio.app.udf.ListMultiValue;
import com.trackstudio.app.udf.ListValue;
import com.trackstudio.app.udf.NumericValue;
import com.trackstudio.app.udf.StringValue;
import com.trackstudio.app.udf.TaskValue;
import com.trackstudio.app.udf.UserValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskUDFValueScript;
import com.trackstudio.external.UserUDFValueScript;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.CompiledPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.cache.UdfvalCacheItem;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.Secured;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Intern;
import com.trackstudio.tools.Pair;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * This class works with Udf value
 */
@ThreadSafe
public class UdfValue {

    /**
     * This class is used for work with time entity
     */

    @Immutable
    public static class TimeCapsula {

        private final long timestamp;
        private final Object object;

        /**
         * Constructor
         *
         * @param timestamp time long
         * @param object    Object
         */
        public TimeCapsula(long timestamp, Object object) {
            this.timestamp = timestamp;
            this.object = object;
        }

        /**
         * This method returns timestamp
         *
         * @return timestamp
         */
        public long getTimestamp() {
            return timestamp;
        }


        /**
         * This method gets object
         *
         * @return object
         */
        public Object getObject() {
            return object;
        }

    }

    // logger can cause performance problems here
    /**
     * This instance has general data about udf value
     */
    protected volatile GenericValue valueContainer;
    private static Log log = LogFactory.getLog(UdfValue.class);

    /**
     * String
     */
    public static final int STRING = 0;
    /**
     * Float
     */
    public static final int FLOAT = 1;
    /**
     * Date
     */
    public static final int DATE = 2;
    /**
     * List
     */
    public static final int LIST = 3;
    /**
     * Multilist
     */
    public static final int MULTILIST = 6;
    /**
     * Task
     */
    public static final int TASK = 7;
    /**
     * User
     */
    public static final int USER = 8;
    /**
     * Integer
     */
    public static final int INTEGER = 4;
    /**
     * Memo
     */
    public static final int MEMO = 5;
    /**
     * Url
     */
    public static final int URL = 9;

    public static final int TOTAL_STANDESR_FIELDS = 100;

    private final String udfId;
    private final String caption;
    private final String captionReference;
    private final String udfLookupScript;
    private final String udfScript;
    private final int type;
    private final boolean udfRequired;
    private final boolean udfHtmlview;
    private final boolean udfCachevalues;
    private final int order;
    private final ConcurrentHashMap<String, TimeCapsula> calcUdfCache;
    /**
     * This field points that udf uses only lookup scripts
     */
    protected final boolean lookupOnly;

    /**
     * This method gets name of script
     *
     * @return name of script
     */
    public String getUdfScript() {
        return udfScript;
    }

    /**
     * This method returns captions which consists from name of udf and name of workflow
     *
     * @return name of udf
     */
    public String getCaptionReference() {
        return captionReference;
    }


    /**
     * This method sets udf value
     *
     * @param val udf value
     * @throws GranException for necessary
     */
    public void setValue(UdfvalCacheItem val) throws GranException {
        switch (type) {
        case STRING:
        case MEMO:
            valueContainer = new StringValue(val);
            break;
        case URL:
            valueContainer = new LinkValue(val);
            break;
        case INTEGER:
            valueContainer = new NumericValue(val);
            break;
        case FLOAT:
            valueContainer = new FloatValue(val);
            break;
        case DATE:
            valueContainer = new CalendarValue(val);
            break;
        case LIST:
            valueContainer = new ListValue(val);
            break;
        case MULTILIST:
            valueContainer = new ListMultiValue((ListMultiValue)valueContainer, val);
            break;
        case TASK:
            valueContainer = new TaskValue((TaskValue)valueContainer, val);
            break;
        case USER:
            valueContainer = new UserValue((UserValue)valueContainer, val);
            break;
        }
    }


    /**
     * Constructor
     *
     * @param ud Udf Cache
     * @throws GranException for necessary
     */
    public UdfValue(UDFCacheItem ud) throws GranException {
        udfId = Intern.process(ud.getId());
        caption = Intern.process(ud.getCaption());
        captionReference = Intern.process(ud.getReferencedbycaption());
        type = ud.getType();
        if (ud.getOrder()!=null) {
			order = ud.getOrder();
		} else {
        	order = 0;
		}
        udfRequired = ud.getRequired() != null && ud.getRequired() == 1;
        udfHtmlview = ud.getHtmlview() != null && ud.getHtmlview() == 1;
        udfCachevalues = ud.getCachevalues() != null && ud.getCachevalues() == 1;
        udfScript = Intern.process(ud.getScript());
        udfLookupScript = Intern.process(ud.getLookupscript());
        lookupOnly = ud.getLookuponly() != null && ud.getLookuponly() == 1;
        int typeA = this.type;
        switch (typeA) {
            case STRING:
            case MEMO:
                valueContainer = new StringValue(udfId);
                break;
            case URL:
                valueContainer = new LinkValue(udfId);
                break;
            case INTEGER:
                valueContainer = new NumericValue(udfId);
                break;
            case FLOAT:
                valueContainer = new FloatValue(udfId);
                break;
            case DATE:
                valueContainer = new CalendarValue(udfId);
                break;
            case LIST:
                valueContainer = new ListValue(udfId);
                break;
            case MULTILIST:
                valueContainer = new ListMultiValue(udfId);
                break;
            case TASK:
                valueContainer = new TaskValue(udfId);
                break;
            case USER:
                valueContainer = new UserValue(udfId);
                break;
        }
        if (udfCachevalues) {
            calcUdfCache = new ConcurrentHashMap<>();
        } else {
            calcUdfCache = null;
        }
    }

    /**
     * This method gets udf ID
     *
     * @return ID udf id
     */
    public String getUdfId() {
        return udfId;
    }

    /**
     * This method gets type of udf
     *
     * @return type of udf
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method gets caption for udf
     *
     * @return caption of udf
     */
    public String getCaption() {
        return caption;
    }

    /**
     * This method gets caption of workflow if udf is wired to workflow
     *
     * @return caption of workflow
     * @throws com.trackstudio.exception.GranException
     *          for necessary
     */
    public String getCaptionWorkflow() throws GranException {
        return KernelManager.getFind().buildName(caption, udfId);
    }

    /**
     * This method gets order
     *
     * @return order of udf
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * This method checks if udf is required
     *
     * @return TRUE - yes, FALSE - no
     */
    public boolean isRequired() {
        return udfRequired;
    }

    /**
     * This method gets info about html view of udf
     *
     * @return TRUE - html view, FALSE - not
     */
    public boolean isHtmlview() {
        return udfHtmlview;
    }

    /**
     * This method gets udf value. It depends from udf type
     *
     * @param securedTaskOrUserBean Udf value
     * @return Udf value or null
     * @throws GranException for necessary
     */
    public Object getValue(Secured securedTaskOrUserBean) throws GranException {
        PluginType scripttype;
        if (securedTaskOrUserBean instanceof SecuredUserBean) {
            scripttype = PluginType.USER_CUSTOM_FIELD_VALUE;
        } else {
            scripttype = PluginType.TASK_CUSTOM_FIELD_VALUE;
        }
        AbstractPluginCacheItem pluginCacheItem = null;
        Object calculatedValue = null;
        if (udfScript != null)
            pluginCacheItem = PluginCacheManager.getInstance().find(scripttype, udfScript);
        if (udfScript != null && pluginCacheItem != null && securedTaskOrUserBean != null && udfCachevalues && calcUdfCache.get(securedTaskOrUserBean.getSecure().getUserId()) != null) {
            // we have script, value cached an cache contains something
            TimeCapsula timeCapsula = calcUdfCache.get(securedTaskOrUserBean.getSecure().getUserId());
            if (timeCapsula.getTimestamp() >= pluginCacheItem.getLastModified())
                // script not changed since last calc
                return timeCapsula.getObject();
            else
                calcUdfCache.remove(securedTaskOrUserBean.getSecure().getUserId());
               // script has been changed since value was calculatedValue for this user
        }

        // we cannot find anything in cache, so try to calculate
        if (udfScript != null && securedTaskOrUserBean != null) {
            {
                if (pluginCacheItem instanceof PluginCacheItem) // run interpreted script
                    calculatedValue = CalculatedValue.getInstance().getValue((PluginCacheItem) pluginCacheItem, securedTaskOrUserBean);
                else if (pluginCacheItem instanceof CompiledPluginCacheItem) { // run compiled script
                    Class compiledClass = ((CompiledPluginCacheItem) pluginCacheItem).getCompiled();
                    if (compiledClass == null) {
                        log.error("The script " + pluginCacheItem.getName() + " does not exist!");
                        return null;
                    }
                    if (pluginCacheItem.getType().equals(PluginType.USER_CUSTOM_FIELD_VALUE)) {
                        try {
                            Object compiled = compiledClass.newInstance();
                            UserUDFValueScript script = (UserUDFValueScript) compiled;
                            calculatedValue = script.calculate((SecuredUserBean) securedTaskOrUserBean);
                        } catch (Exception cce) {
                            log.error("Error", cce);
                            throw new UserException("This script " + pluginCacheItem.getName() + " has type " + pluginCacheItem.getType() + " for this reason you need to implement a com.trackstudio.external.UserUDFValueScript interface!");
                        }
                    } else if (pluginCacheItem.getType().equals(PluginType.TASK_CUSTOM_FIELD_VALUE)) {
                        try {
                            Object compiled = compiledClass.newInstance();
                            TaskUDFValueScript script = (TaskUDFValueScript) compiled;
                            calculatedValue = script.calculate((SecuredTaskBean) securedTaskOrUserBean);
                        } catch (Exception cce) {
                            log.error("The script " + compiledClass.getName() + " has the error.", cce);
                            throw new UserException("The script " + compiledClass.getName() + " has the error. Detail : " + cce.getMessage(), false);
                        }
                    }
                }
            }
        }
        Object value = valueContainer.getValue(calculatedValue);
        if (udfCachevalues && udfScript != null && securedTaskOrUserBean != null &&  pluginCacheItem != null)
            calcUdfCache.put(securedTaskOrUserBean.getSecure().getUserId(), new TimeCapsula(pluginCacheItem.getLastModified(), value));
        return value;
    }


    /**
     * This method gets list for udf type of list
     *
     * @return list of values
     * @throws GranException for necessary
     */
    public List<Pair> getList() throws GranException {
        HashMap<String, String> m = KernelManager.getUdf().getUdflist(udfId);
        ArrayList<Pair> list = new ArrayList<Pair>();
        for (Map.Entry e : m.entrySet()) {
            list.add(new Pair(e.getKey().toString(), e.getValue().toString()));
        }
        return list;
    }

    /**
     * This method gets udf form. It is used on html form for viewer and save
     *
     * @param timezone   timezone
     * @param locale     locale
     * @param sortcolumn order of column
     * @param disabled   show field or not
     * @return Customizer form
     * @throws GranException for necessary
     */
    public Customizer getCustomizer(String timezone, String locale, String sortcolumn, boolean disabled) throws GranException {
        return valueContainer.getCustomizer(getCaptionWorkflow(), timezone, locale, sortcolumn, disabled);

    }

    /**
     * This method equals two objects
     *
     * @param obj compared object
     * @return TRUE - equals, FALSE - not
     */
    public boolean equals(Object obj) {
        return obj instanceof UdfValue && getUdfId().equals(((UdfValue) obj).getUdfId());
    }

    /**
     * This method gets name of lookup script
     *
     * @return name of lookup script
     */
    public String getUdfLookupScript() {
        return udfLookupScript;
    }

    /**
     * This method gets info about to be used lookup script or not
     *
     * @return TRUE - be used lookup, FALSE - no be used
     */
    public boolean isLookupOnly() {
        return lookupOnly;
    }

    /**
     * This method gets info about to be used lookup script or not
     *
     * @return TRUE - be used lookup, FALSE - no be used
     */
    public boolean getLookupOnly() {
        return lookupOnly;
    }


    /**
     * This method gets general value
     *
     * @return gets general value
     */
    public GenericValue getValueContainer() {
        return valueContainer;
    }

    /**
     * This method sets general value
     *
     * @param valueContainer general value
     */
    public void setValueContainer(GenericValue valueContainer) {
        this.valueContainer = valueContainer;
    }

    @Override
    public String toString() {
        return "UdfValue{" +
                "caption='" + caption + '\'' +
                ", type=" + type +
                '}';
    }
}