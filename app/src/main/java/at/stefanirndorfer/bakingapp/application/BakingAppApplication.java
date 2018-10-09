package at.stefanirndorfer.bakingapp.application;

import android.app.Application;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import at.stefanirndorfer.bakingapp.BuildConfig;
import timber.log.Timber;

import static android.util.Log.INFO;
import static timber.log.Timber.DebugTree;

public class BakingAppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static final class CrashReportingTree extends Timber.Tree {
        @Override
        protected boolean isLoggable(@Nullable String tag, int priority) {
            return priority >= INFO;
        }


        @Override
        protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    Timber.e(t);
                } else if (priority == Log.WARN) {
                    Timber.w(t);
                }
            }
        }
    }
}
