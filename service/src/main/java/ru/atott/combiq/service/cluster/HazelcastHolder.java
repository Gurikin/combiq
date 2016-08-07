package ru.atott.combiq.service.cluster;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.atott.combiq.service.util.EnvHolder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

@Component
public class HazelcastHolder {

    private HazelcastInstance hazelcast;

    @Autowired
    @Qualifier("serviceProperties")
    private Properties serviceProperties;

    @Autowired
    private EnvHolder envHolder;

    @PostConstruct
    public void init() throws Exception {
        Integer hazelcastPort = Integer.valueOf(serviceProperties.getProperty("service.cluster." + envHolder.getNode() + ".hazelcast.port", "5701"));

        Config config = new Config();
        NetworkConfig networkConfig = config.getNetworkConfig();

        networkConfig
                .setPort(hazelcastPort)
                .setReuseAddress(true)
                .setPortAutoIncrement(true)
                .setPortCount(20);

        JoinConfig join = networkConfig.getJoin();

        join
                .getMulticastConfig()
                .setEnabled(false);

        envHolder
                .getDeclaredClusterNodes()
                .forEach(clusterNode -> {
                    String host = serviceProperties.getProperty("service.cluster." + envHolder.getNode() + ".host");
                    join.getTcpIpConfig().addMember(host);
                });

        join.getTcpIpConfig().setEnabled(true);

        hazelcast = Hazelcast.newHazelcastInstance(config);
    }

    @PreDestroy
    public void shutdown() throws Exception {
        hazelcast.shutdown();
    }

    public HazelcastInstance getHazelcast() {
        return hazelcast;
    }
}
