package nl.overheid.koop.plooi.aanleveren.document.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {
    public static final String UUID = "efe9aa58-20f4-4d2c-aca5-d86e3dda92c3";
    public static final String UUID_SECOND = "efe9aa58-20f4-4d2c-aca5-d86e3dda92c2";

    public static final String SANDBOX_TOKEN_HEADER = "Aanlever-Token";
    public static final String SANDBOX_TOKEN_ID = "123";

    public static final String API_VERSION_HEADER = "Api-Version";
    public static final String API_VERSION_VALUE = "1.0.0";

    public static final String CACHE_CONTROL_HEADER = "Cache-Control";
    public static final String CACHE_CONTROL_VALUE = "public, max-age=86400";


    public static final String CONTENT_LANGUAGE_HEADER = "Content-Language";
    public static final String CONTENT_LANGUAGE_VALUE = "nl";

    public static final String XFRAME_OPTIONS_HEADER = "X-Frame-Options";
    public static final String XFRAME_OPTIONS_VALUE = "SAMEORIGIN";

    public static final String XCONTENT_TYPE_OPTIONS_HEADER = "X-Content-Type-Options";
    public static final String XCONTENT_TYPE_OPTIONS_VALUE = "nosniff";

    public static final String CONTENT_SECURITY_POLICY_HEADER = "Content-Security-Policy";
    public static final String CONTENT_SECURITY_POLICY_VALUE = "default-src self; frame-ancestors self;";

    public static final String REFERER_POLICY_HEADER = "Referrer-Policy";
    public static final String REFERER_POLICY_VALUE = "same-origin";
}
