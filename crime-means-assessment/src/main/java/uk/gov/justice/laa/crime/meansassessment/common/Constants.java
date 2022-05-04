package uk.gov.justice.laa.crime.meansassessment.common;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Constants class");
    }
    public static final String URIVAR_USERNAME = "username";
    public static final String URIVAR_REP_ID = "repId";
    public static final String URIVAR_RESERVATION_ID = "reservationId";
    public static final String URIVAR_SESSION_ID = "sessionId";
    public static final String URIVAR_NWOR_CODE = "nworCode";
    public static final String URIVAR_ACTION = "action";
    public static final String ACTION_CREATE_ASSESSMENT = "CREATE_ASSESSMENT";
    public static final String LAA_TRANSACTION_ID = "Laa-Transaction-Id";
}
