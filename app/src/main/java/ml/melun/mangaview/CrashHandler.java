// File: app/src/main/java/ml/melun/mangaview/CrashHandler.java
package ml.melun.mangaview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private final Context context;
    private final String filePath;

    public CrashHandler(Context context) {
        this.context = context;
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        this.filePath = context.getFilesDir().getPath() + "/crash/crash-report_" + timestamp + ".txt";
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        saveCrashReport(throwable);
        showAlertDialog();
    }

    private void saveCrashReport(Throwable throwable) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(throwable.toString());
                for (StackTraceElement element : throwable.getStackTrace()) {
                    writer.write("\n\tat " + element.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog() {
        new Thread(() -> {
            Looper.prepare();
            new AlertDialog.Builder(context)
                    .setTitle("Application Error")
                    .setMessage("An unexpected error occurred. The application will close.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        ((Activity) context).finish();
                        System.exit(2);
                    })
                    .show();
            Looper.loop();
        }).start();
    }
}