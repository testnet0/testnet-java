package testnet.common.service;

/**
 * redis操作Service
 */
public interface IRedisStreamService {

    String addObject(String key, Object o);

    Long del(String key, String... recordIds);

    void initKeyAndGroup(String channelName, String groupName);

    Long ack(String key, String group, String... recordIds);
}