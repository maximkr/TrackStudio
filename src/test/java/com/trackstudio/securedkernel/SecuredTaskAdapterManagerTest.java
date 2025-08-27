package com.trackstudio.securedkernel;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class SecuredTaskAdapterManagerTest {

    @Test
    public void archives() throws GranException {
        SecuredTaskAdapterManager manager = AdapterManager.getInstance().getSecuredTaskAdapterManager();
        manager.archiveByNumber(null, "1");
    }
}