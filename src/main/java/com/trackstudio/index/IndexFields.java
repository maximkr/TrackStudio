package com.trackstudio.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public interface IndexFields {
    public static final Analyzer analyzer = new StandardAnalyzer();
    public static final Version VER = Version.LATEST;
    public static final String TASK_ID = "task_id";
    String TASK_NUMBER = "task_number";
    String TASK_UPDATE = "task_update";
    public static final String TASK_NAME = "task_name";
    public static final String TASK_DESC = "task_desc";
    public static final String TASK_MSGS = "task_msgs";

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_LOGIN = "user_login";
    public static final String USER_COMPANY = "user_company";

    public static final String ATTACH_ID = "attach_id";
    public static final String ATTACH_NAME = "attach_name";
    public static final String ATTACH_DESC = "attach_desc";
    public static final String ATTACH_ALL_FIELDS = "attach_all_fields";

    public static final String REF_BY_TASK_FOR_TASK = "ref_by_task_for_task";
    public static final String REF_BY_USER_FOR_TASK = "ref_by_user_for_task";
    public static final String REF_BY_TASK_FOR_USER = "ref_by_task_for_user";
    public static final String REF_BY_USER_FOR_USER = "ref_by_user_for_user";
    public static final String ALL_FIELDS = "udf_all_fields";
}
