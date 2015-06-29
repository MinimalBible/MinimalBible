package org.bspeice.minimalbible;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Create a MinimalBible application that we can extend from the main release
 * Currently it's not doing much, but would allow for shenanigans during testing in the future
 */
/*
@ReportsCrashes(formKey = "",
        mailTo = "bspeice.nc@gmail.com",
        mode = ReportingInteractionMode.SILENT
)
*/
public class MinimalBibleDebug extends MinimalBible {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("MinimalBible")
                .setLogLevel(LogLevel.FULL);
        Logger.d("Beginning application run...");
//        ACRA.init(this);
    }
}