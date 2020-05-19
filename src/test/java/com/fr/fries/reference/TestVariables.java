package com.fr.fries.reference;

public class TestVariables {

    // common
    public static String url_starts = "http";
    public static String url_contentType = "content-type";
    public static String url_contentLength = "content-length";

    // health
    public static String health_up = "UP";
    public static String health_down = "DOWN";

    // createUploadUrl, getDownloadUrl
    public static String userId_normal = "userId_nor"; // length(1-36)
    public static String userId_normal_hex = "785c02b30d916d5374dc80479e37f6ba6ceaf5c68b01f97c6a17d45e54740016";
    public static String userId_blank = " ";
    public static String userId_invalid = "userId_invalid_abcdefghijklmnopqrstuv"; // length(37)

    public static String appId_iot = "app_normal"; // length(10)

    public static String cid_normal = "cid_normal";
    public static String cid_invalid = "cid_invalid"; // length(11)
    public static String cid_notInPolicy = "cid_not_in";
    public static String cid_s3_down = "cid_s3down";

    public static String objectId_blank = " ";
    public static String objectId_invalid_35 = "5acf02d7-77b6-11e9-9ffe-31f3e54743a"; // length(35)
    public static String objectId_invalid_37 = "5acf02d7-77b6-11e9-9ffe-31f3e54743a5-"; // length(37)

    public static String body_data = "upload data";

    public static String contentType_textPlain = "text/plain";
    public static String contentType_wav = "media/wav";
    public static String contentType_wav_urlEncode = "media%2Fwav";

    public static Long contentLength_zero = 0L;
    public static Long contentLength_exceeds_max = 1073741825L;

    public static String resFilename_rename = "rename.txt";
}
