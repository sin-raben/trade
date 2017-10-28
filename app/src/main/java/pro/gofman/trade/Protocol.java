package pro.gofman.trade;

/**
 * Created by roman on 11.08.16.
 */




public class Protocol {

    /*
           Придумать класс для автоматической синхронизации данными
           таблицы и поля можно брать из структуры базы данных

     */

    public static final String HEAD = "head";
    public static final String BODY = "body";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String DEBUG = "debug";
    public static final String FINISH = "finish";
    public static final String NOW = "now";



    protected static final String CONNECTION_BEGIN = "BeginConnection";

    public static final String USER_DATA = "userData";
    public static final String NEW_KEY = "newKey";
    public static final String COMMAND_SYNC = "commandSync";
    public static final String CUSTOM_SYNC = "customSync";
    // Команды синхронизации
    protected static final String MESSAGE_SYNC = "messageSync";
    protected static final String FULL_SYNC = "fullsync";
    protected static final String NORMAL_SYNC = "normalSync";
    protected static final String SUPPORT_SYNC = "supportSync";

    // Служебные атрибуты
    public static final String SYNC_ID = "sync";
    public static final String TOKEN = "token";
    public static final String DATA = "data";
    public static final String RESULT = "result";
    public static final String FCM_TOKEN = "fcmToken";

    // Объекты синхронизации
    protected static final String AUTH_USER = "f010002s";
    protected static final String ITEMS = "items";
    protected static final String PRICE = "price";
    protected static final String PRICELISTS = "pricelist";
    protected static final String LINK_PRICELISTS = "pricelistLink";
    protected static final String STORES = "stores";
    protected static final String LINK_STORES = "storeLink";
    protected static final String STOCKS = "stocks";

    // Функции синхронизации
    public static final String SYNC_NEWS = "f020018g";
    public static final String SYNC_ITEMS = "f020006g";
    public static final String SYNC_ITEMGROUPTYPES = "f020007g";
    public static final String SYNC_ITEMGROUPS = "f020008g";
    public static final String SYNC_LINKITEMGROUP = "f020009g";
    public static final String SYNC_ITEMUNITS = "f020010g";
    public static final String SYNC_LINKITEMUNIT = "f020011g";
    public static final String SYNC_ITEMSEARCH = "f020012g";

    public static final String SYNC_COUNTERAGENTS = "f020013g";
    public static final String SYNC_DELIVERYPOINTS = "f020014g";
    public static final String SYNC_LINKCOUNTERAGENTPOINT = "f020015g";
    public static final String SYNC_COUNTERAGENTADDRESS = "f020016g";
    public static final String SYNC_POINTSEARCH = "f020017g";


    public static final String SYNC_CALLS = "f020005s";
    public static final String SYNC_COORDS = "f020004s";

    public static final String SYNC_FCMTOKEN = "f010003s";
    public static final String RESULT_SYNC = "f010004s";



    // Комманды пришедшие с сервера
    protected static final String COMMAND_SERVER = "cmd";

    protected static final String CMD_SYNC = "1000";
    protected static final String CMD_SYNC_NEWS = "1000";
    protected static final String CMD_FULLSYNC = "1000";

    protected static final String CMD_OPENITEMS = "2000";

    protected static final String CMD_COORD = "3000";

    // Описание уведомления
    public static final String NOTIFICATION_OBJECT = "n";
    public static final String NOTIFICATION_DBDATA = "b";
    public static final String NOTIFICATION_HEAD = "h";
    public static final String NOTIFICATION_DATA = "d";
    public static final String NOTIFICATION_TITLE = "t";
    public static final String NOTIFICATION_BODY = "b";
    public static final String NOTIFICATION_TICKER = "tr";
    public static final String NOTIFICATION_SMALLICON = "si";
    public static final String NOTIFICATION_LARGEICON = "li";
    public static final String NOTIFICATION_COLOR = "c";
    public static final String NOTIFICATION_SOUND = "s";
    public static final String NOTIFICATION_VIBRATE = "v";
    public static final String NOTIFICATION_LIGHT = "l";


    public static final String EVENT = "event";
    // События регистрируемые в приложении
    public static final int EVENT_UNKNOW = 0;         //
    public static final int EVENT_MONITORING = 1;     // Постоянный мониторинг
    public static final int EVENT_QUERY = 2;          // По запросу
    public static final int EVENT_ANSWER_ACTION = 3;  // В момент ответа на Действия (новости)



    public static final String LOCATION_REQUEST = "lr";
    public static final String LR_UPDATE_INTERVAL = "lr_ui";
    public static final String LR_FASTEST_INTERVAL = "lr_fui";
    public static final String LR_MAX_WAIT_TIME = "lr_mwt";
    public static final String LR_PRIORITY_HIGH_ACCURACY  = "lr_pha";

    // LocationRequest значения по умолчанию
    public static final long LOCATION_UPDATE_INTERVAL = 10000;
    public static final long LOCATION_FASTEST_UPDATE_INTERVAL = 5000;
    public static final long LOCATION_MAX_WAIT_TIME = LOCATION_UPDATE_INTERVAL * 5;


    // Таблицы, надо придумать что более красивое
    public static final String DB_NEWS = "news";


    // Подгонка полей серверной базы под локальную
    public static final String[][] FIELDS_NEWS = {
        {"n_id", "n_id", "int"},
        {"n_date", "n_date", "text"},
        {"n_title", "n_title", "text"},
        {"n_text", "n_text", "text"},
        {"n_data", "n_data", "text"},
        {"n_type", "n_type", "int"}
    };




}
