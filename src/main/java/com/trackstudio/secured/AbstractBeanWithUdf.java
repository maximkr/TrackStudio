package com.trackstudio.secured;

import java.util.List;
import java.util.Map;

import com.trackstudio.exception.GranException;

import net.jcip.annotations.Immutable;

/**
 * Bean with custom fields
 */
@Immutable
public abstract class AbstractBeanWithUdf extends Secured {

    public abstract List<SecuredUDFValueBean> getUDFValuesList() throws GranException;

    public abstract Map<String, SecuredUDFValueBean> getUDFValues() throws GranException;

    public abstract List<SecuredUDFValueBean> getFilteredUDFValues() throws GranException;

    @Override
    public void setId(String id) {
    }
}
