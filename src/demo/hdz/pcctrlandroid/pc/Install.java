package demo.hdz.pcctrlandroid.pc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by wanjian on 2017/4/5.
 */

public class Install {

    public static void main(String[] args) {

        install();
    }

    public static void install() {
        adbCommand("forward tcp:8888 localabstract:puppet-ver1");

        adbCommand("push ./bin/Main.dex /data/local/tmp/Main.dex");

        String path = "export CLASSPATH=/data/local/tmp/Main.dex";
        String app  = "exec app_process /data/local/tmp demo.hdz.pcctrlandroid.android.Main";

        shellCommand(new String[]{path, app});
    }

    private static void adbCommand(String com) {

        command("cmd", "adb " + com);
    }

    private static void shellCommand(String[] com) {
        try {
            Process process = Runtime.getRuntime().exec("adb shell "); // adb
            // shell
            final BufferedWriter outputStream = new BufferedWriter( new OutputStreamWriter(process.getOutputStream()));

            for (String s : com) {
                outputStream.write(s);
                outputStream.write("\n");
            }

            outputStream.flush();

            readError(process.getErrorStream());

            readResult(process.getInputStream());

            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readError(final InputStream errorStream) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                readResult(errorStream);
            }
        }.start();
    }

    private static void command(String c, String com) {
        try {
            Process process = Runtime .getRuntime() .exec(c); // adb
            final BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            outputStream.write(com);
            outputStream.write("\n");
            outputStream.write("exit\n");
            outputStream.flush();

            int i = process.waitFor();
            readResult(process.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readResult(final InputStream stream) {
        try {
            String line;
            final BufferedReader reader = new BufferedReader( new InputStreamReader(stream));

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                stream.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
