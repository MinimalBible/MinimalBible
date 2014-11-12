package org.bspeice.minimalbible;

/**
 * Define a simple interface for classes to receive an object to provide their dependencies.
 * This way, the dependencies can be provided by an activity, or a test module, etc.
 */
public interface Injector {
    public void inject(Object o);
}
