package org.bspeice.minimalbible.activity.search;

import org.bspeice.minimalbible.common.injection.InvalidBookModule;
import org.bspeice.minimalbible.common.injection.MainBookModule;

import dagger.Component;

@Component(modules = {SearchModules.class, MainBookModule.class, InvalidBookModule.class})
public interface BasicSearchComponent {

    BasicSearch injectBasicSearch(BasicSearch basicSearch);
}
