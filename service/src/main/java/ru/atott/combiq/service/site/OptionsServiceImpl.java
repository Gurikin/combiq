package ru.atott.combiq.service.site;

import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.atott.combiq.dao.entity.OptionsEntity;
import ru.atott.combiq.dao.repository.OptionsRepository;
import ru.atott.combiq.service.bean.Options;

@Service
public class OptionsServiceImpl implements OptionsService {

    @Autowired
    private OptionsRepository optionsRepository;

    private Options cachedOptions;

    @Override
    public Options getOptions() {
        OptionsEntity entity = optionsRepository.findOne("1");

        if (entity == null) {
            entity = new OptionsEntity();
            entity.setId("1");
            optionsRepository.save(entity);
        }

        Options options = new Options();
        options.setPostsInProduction(entity.isPostsInProduction());

        return options;
    }

    @Override
    public Options getCachedOptions() {
        if (cachedOptions == null) {
            cachedOptions = getOptions();
        }

        return cachedOptions;
    }

    @Override
    public void saveOptions(Options options) {
        Validate.notNull(options);

        OptionsEntity entity = optionsRepository.findOne("1");

        if (entity == null) {
            entity = new OptionsEntity();
            entity.setId("1");
        }

        entity.setPostsInProduction(options.isPostsInProduction());
        cachedOptions = null;

        optionsRepository.save(entity);
    }
}
