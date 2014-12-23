package org.bspeice.minimalbible;

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
public class MinimalBibleDebug extends MinimalBible implements Injector {

    @Override
    public void onCreate() {
        super.onCreate();
//        ACRA.init(this);
    }
}