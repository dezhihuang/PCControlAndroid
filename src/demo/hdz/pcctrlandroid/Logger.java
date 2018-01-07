package demo.hdz.pcctrlandroid;

/**
 * Created by hdz on 2017/12/23.
 */
public class Logger {

    private static final String TAG = "PCCtrlAndroid";

    public static void d(String tag, String log) {
        try {
            StackTraceElement[] a = new Throwable().getStackTrace();
            StackTraceElement ste = a[0];
            for (StackTraceElement anA : a) {
                if (!anA.getMethodName().equals("d")) {
                    ste = anA;
                    break;
                }
            }
            String  sClassName = ste.getClassName();
            String sMethodName = ste.getMethodName();
            int          iLine = ste.getLineNumber();
            String   sFileName = ste.getFileName();

            String sTmpClassName = "";
            int iPos = sClassName.lastIndexOf(".");
            if(iPos <=0 ) {
                sTmpClassName = sClassName;
            } else {
                sTmpClassName = sClassName.substring(iPos+1);
            }
            System.out.println("[" + sFileName + ":" + iLine + " " + sTmpClassName + "::" + sMethodName + "] " + log);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(log);
        }
    }

    public static void d(String tag, int log) {
        d(tag, "" + log);
    }
    public static void d(String tag, String log, boolean out) {if (out)d(tag, log);}
    public static void d(String tag, int    log, boolean out) {if (out)d(tag, log);}
    public static void d(String log, boolean out) {if (out) d(log);}
    public static void d(int    log, boolean out) {if (out)d(log);}
    public static void d(String log) {d(TAG, log);}
    public static void d(int log) {d(TAG, ""+log);}
}

