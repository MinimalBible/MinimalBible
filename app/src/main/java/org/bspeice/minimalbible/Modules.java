package org.bspeice.minimalbible;

/**
 * List modules used by default MinimalBible configuration
 */
public class Modules {
    private Modules() {
    }

    public static Object[] list(MinimalBible app) {
        return new Object[]{
                new MinimalBibleModules(app)
        };
    }
}
