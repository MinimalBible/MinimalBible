package org.bspeice.minimalbible.common.injection;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class InvalidBookModule {
    /**
     * Provide a list of book names that are known bad. This can be because they trigger NPE,
     * or are just missing lots of content, etc.
     *
     * @return the list of books (by name) to ignore
     */
    @Provides
    List<String> invalidBooks() {
        List<String> list = new ArrayList<>();
        list.add("ABU"); // Missing content
        list.add("ERen_no"); // Thinks its installed, when it isn't. Triggers NPE
        list.add("ot1nt2"); // Thinks its installed, when it isn't. Triggers NPE

        return list;
    }
}
