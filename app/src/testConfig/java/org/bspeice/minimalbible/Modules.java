package org.bspeice.minimalbible;

/**
 * List modules to be used during testing
 */
public class Modules {
    private Modules() {}

    public static Object[] list(MinimalBible app) {
        return new Object[] {
                new MinimalBibleModules(app),
                new TestModules()
        };
    }
}
