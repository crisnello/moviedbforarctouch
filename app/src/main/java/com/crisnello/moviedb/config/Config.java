package com.crisnello.moviedb.config;

	public class Config {


            // global topic to receive app wide push notifications
            public static final String TOPIC_GLOBAL = "global";

            // broadcast receiver intent filters
            public static final String REGISTRATION_COMPLETE = "registrationComplete";
            public static final String PUSH_NOTIFICATION = "pushNotification";

            // id to handle the notification in the notification tray
            public static final int NOTIFICATION_ID = 100;
            public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

            public static final String SHARED_PREF = "ah_firebase";

        public static int WS_TIME_OUT = 15000;
        public final static String DB_NAME = "moviedb.sqlite";

        public final static String WS_SERVER_USADO = "http://IP_DO_SERVER";

        public final static String WS_BASE_URL = "http://themoviedb.org/";

        public final static String WS_URL_LOGIN = WS_SERVER_USADO+"/api/login.jsf";
        public final static String WS_URL_ADD_USER = WS_SERVER_USADO+"/api/usuarioAdd.jsf";

        public final static String WS_PATH_URL = "api/";
        public final static String WS_FILE_PATH="system/file/";

        public final static String WS_PROFILE_PICTURE_SIZE="cadastro/200x200/";


    }

