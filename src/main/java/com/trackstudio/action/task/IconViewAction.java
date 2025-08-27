package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.tools.ExternalAdapterManagerUtil;

public class IconViewAction extends TSDispatchAction {
    public static class IconListItem implements Comparable {
        public String name;
        public TreeSet<String> uses;

        public IconListItem(String name) {
            this.name = name;
            uses = new TreeSet<String>();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TreeSet<String> getUses() {
            return uses;
        }

        public void setUses(TreeSet<String> uses) {
            this.uses = uses;
        }


        public int compareTo(Object o) {
            if (o instanceof IconListItem) {
                return name.compareTo(((IconListItem) o).name);
            } else return -1;
        }


        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IconListItem that = (IconListItem) o;

            return name.equals(that.name);
        }

        public int hashCode() {
            return name.hashCode();
        }
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, false);
            HashMap<String, String> categoryMap = ExternalAdapterManagerUtil.getAvailableCategoryMap(sc, id);
            List<AbstractPluginCacheItem> imageNames = PluginCacheManager.getInstance().list(PluginType.ICON).get(PluginType.ICON);
            ArrayList<IconListItem> list = new ArrayList<IconListItem>();

            for (AbstractPluginCacheItem name : imageNames) {
                IconListItem li = new IconListItem(name.getName());
                list.add(li);
            }
            for (String categoryId : categoryMap.keySet()) {
                SecuredCategoryBean cat = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
                String icon = cat.getIcon();
                int h = list.indexOf(new IconListItem(icon));
                if (h > -1) list.get(h).getUses().add(cat.getName());
            }
            sc.setRequestAttribute(request, "icons", list);
            return mapping.findForward("iconViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
