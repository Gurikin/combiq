package ru.atott.combiq.service.site;

import ru.atott.combiq.service.bean.Options;

public interface OptionsService {

    Options getOptions();

    Options getCachedOptions();

    void saveOptions(Options options);
}
