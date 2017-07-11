package pro.gofman.trade;

/**
 * Created by roman on 11.08.16.
 */




public class Protocol {

    /*
           Придумать класс для автоматической синхронизации данными
           таблицы и поля можно брать из структуры базы данных

     */

    protected static final String HEAD = "head";
    protected static final String BODY = "body";
    protected static final String NAME = "name";
    protected static final String ID = "id";

    protected static final String CONNECTION_BEGIN = "BeginConnection";

    public static final String USER_DATA = "userData";
    public static final String COMMAND_SYNC = "commandSync";
    public static final String CUSTOM_SYNC = "customSync";
    // Команды синхронизации
    protected static final String MESSAGE_SYNC = "messageSync";
    protected static final String FULL_SYNC = "fullsync";
    protected static final String NORMAL_SYNC = "normalSync";
    protected static final String SUPPORT_SYNC = "supportSync";

    // Служебные атрибуты
    public static final String SYNC_ID = "sync";
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
    protected static final String SYNC_NEWS = "f020018g";
    protected static final String SYNC_ITEMS = "f020006g";
    protected static final String SYNC_ITEMGROUPTYPES = "f020007g";
    protected static final String SYNC_ITEMGROUPS = "f020008g";
    protected static final String SYNC_LINKITEMGROUP = "f020009g";
    protected static final String SYNC_ITEMUNITS = "f020010g";
    protected static final String SYNC_LINKITEMUNIT = "f020011g";
    protected static final String SYNC_ITEMSEARCH = "f020012g";

    protected static final String SYNC_COUNTERAGENTS = "f020013g";
    protected static final String SYNC_DELIVERYPOINTS = "f020014g";
    protected static final String SYNC_LINKCOUNTERAGENTPOINT = "f020015g";
    protected static final String SYNC_COUNTERAGENTADDRESS = "f020016g";
    protected static final String SYNC_POINTSEARCH = "f020017g";


    protected static final String SYNC_CALLS = "f020005s";

    protected static final String RESULT_SYNC = "f010004s";



    // Комманды пришедшие с сервера
    protected static final String COMMAND_SERVER = "cmd";

    protected static final String CMD_SYNC = "1000";
    protected static final String CMD_SYNC_NEWS = "1000";
    protected static final String CMD_FULLSYNC = "1000";

    protected static final String CMD_OPENITEMS = "2000";

    protected static final String CMD_COORD = "3000";




}
