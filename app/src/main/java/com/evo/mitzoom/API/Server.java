package com.evo.mitzoom.API;

public class Server {

    //public static final String BASE_URL_API = "http://103.140.91.46:6514/"; //old
//    public static final String BASE_URL_API = "http://103.140.91.46:6506/";
    public static final String BASE_URL_API = "https://dipsv2.grit.id:6006/gwnode/api/";
    public static final String BASE_URL_API2 = "https://dipsv2.grit.id:6006/gwnode/";
    //public static final String BASE_URL_API = "http://45.127.133.162:3000/"; //prod
    //public static final String BASE_URL_API = "https://next-backup.vercel.app/";
    //public static final String BASE_URL_API = "http://192.168.10.198:4000/";
    public static final String BASE_URL_RABBITMQ = "amqp://dips361:dips2022@103.140.90.42:6004";
//    public static final String BASE_URL_PRODUK = "http://103.140.90.122:4040/portal/";
    public static final String BASE_URL_ADVANCEAI = "https://api.advance.ai/";

    public static ApiService getAPIService() {
        return Client.getClientUnsafe(BASE_URL_API).create(ApiService.class);
    }

    public static ApiService getAPIService2() {
        return Client.getClientUnsafe(BASE_URL_API2).create(ApiService.class);
    }

    public static ApiService getAPIWAITING_PRODUCT() {
        return Client.getClientUnsafe(BASE_URL_API).create(ApiService.class);
    }

    public static ApiService getAPIServiceAdvanceAI() {
        return Client.getClientUnsafe(BASE_URL_ADVANCEAI).create(ApiService.class);
    }

    /* public static ApiService getAPIFirebase() {
        return Client.getClientUnsafe(BASE_URL_API_FIREBASE).create(ApiService.class);
    }*/

}
