package com.trackstudio.sman;

public class AppServer {

    public static String FILE_DELIMITER = System.getProperty("file.separator");
    public static String PATH_DELIMITER = System.getProperty("path.separator");

    private static AppServer instance;

    public AppServer() {

    }

    public static AppServer getInstance() {
        if (instance == null) instance = new AppServer();
        return instance;
    }

}
