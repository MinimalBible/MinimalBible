package org.bspeice.minimalbible;

/**
 * List modules to be used during testing
 * Also the entry point for setting whether or not we are using testing mode
 */
public class Modules {

    private Modules() {
    }

    public static Object[] list(MinimalBible app) {
        return new Object[]{
                new MinimalBibleModules(app),
        };
    }
}
