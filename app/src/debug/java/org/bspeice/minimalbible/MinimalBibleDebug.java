package org.bspeice.minimalbible;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Create a MinimalBible application that we can extend from the main release
 * Currently it's not doing much, but would allow for shenanigans during testing in the future
 */
@ReportsCrashes(formKey = "",
        mailTo = "bspeice.nc@gmail.com",
        mode = ReportingInteractionMode.SILENT
)
public class MinimalBibleDebug extends MinimalBible implements Injector {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}