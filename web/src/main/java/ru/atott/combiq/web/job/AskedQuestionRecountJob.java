package ru.atott.combiq.web.job;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.atott.combiq.service.cluster.HazelcastHolder;
import ru.atott.combiq.service.question.AskedQuestionService;
import ru.atott.combiq.web.utils.WebUtils;

@Component
public class AskedQuestionRecountJob {

    private static Logger logger = LoggerFactory.getLogger(AskedQuestionRecountJob.class);

    @Autowired
    private HazelcastHolder hazelcastHolder;

    @Autowired
    private AskedQuestionService askedQuestionService;

    @Scheduled(cron = "0 1 3 * * ?")
    public void recount() {
        try {
            logger.info("Job has started, node: {}", WebUtils.getNode());

            HazelcastInstance hazelcastInstance = hazelcastHolder.getHazelcast();
            ILock lock = hazelcastInstance.getLock(AskedQuestionRecountJob.class.getCanonicalName());

            if (lock.tryLock()) {
                try {
                    logger.info("Lock has been obtained, node: {}", WebUtils.getNode());

                    askedQuestionService.recountAskedCounts();
                } finally {
                    lock.unlock();

                    logger.info("Lock has been released, node: {}", WebUtils.getNode());
                }
            } else {
                logger.info("Failed ilock capture, node: {}", WebUtils.getNode());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            throw e;
        }
    }
}
