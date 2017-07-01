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
    protected static final String EMPTY = "{}";


    protected static final String CONNECTION_BEGIN = "BeginConnection";

    protected static final String USER_DATA = "userData";
    protected static final String COMMAND_SYNC = "commandSync";
    // Команды синхронизации
    protected static final String MESSAGE_SYNC = "messageSync";
    protected static final String FULL_SYNC = "fullsync";
    protected static final String NORMAL_SYNC = "normalSync";
    protected static final String SUPPORT_SYNC = "supportSync";

    // Служебные атрибуты
    protected static final String SYNC_ID = "sync";
    protected static final String DATA = "data";
    protected static final String RESULT = "result";
    protected static final String FCM_TOKEN = "fcmToken";

    // Объекты синхронизации
    protected static final String AUTH_USER = "f010002s";
    protected static final String ITEMS = "items";
    protected static final String ITEM_GROUP_TYPES = "itemGroupTypes";
    protected static final String ITEM_GROUPS = "itemGroups";
    protected static final String LINK_ITEM_GROUPS = "linkItemGroups";
    protected static final String ITEMS_SEARCH = "itemsSearch";
    protected static final String ITEM_UNIT_TYPES = "itemUnitTypes";
    protected static final String ITEM_UNITS = "itemUnits";
    protected static final String COUNTERAGENTS = "countragents";
    protected static final String POINTS_DELIVERY = "deliveryPoints";
    protected static final String LINK_POINTS_DELIVERY = "linksCountragentDeliveryPoint";
    protected static final String COUNTRAGENT_SEARCH = "CountragentsSearch";
    protected static final String COUNTRAGENT_ADDRESSES = "address";
    protected static final String DELIVERY_POINT_SEARCH = "CountragentsSearch";
    protected static final String PRICE = "price";
    protected static final String PRICELISTS = "pricelist";
    protected static final String LINK_PRICELISTS = "pricelistLink";
    protected static final String STORES = "stores";
    protected static final String LINK_STORES = "storeLink";
    protected static final String STOCKS = "stocks";
    protected static final String SYNC_NEWS = "f020018g";
    protected static final String RESULT_SYNC = "f010004s";



    // Комманды пришедшие с сервера
    protected static final String COMMAND_SERVER = "cmd";

    protected static final String CMD_SYNC = "1000";
    protected static final String CMD_SYNC_NEWS = "1000";
    protected static final String CMD_FULLSYNC = "1000";

    protected static final String CMD_OPENITEMS = "2000";

    protected static final String CMD_COORD = "3000";




}
