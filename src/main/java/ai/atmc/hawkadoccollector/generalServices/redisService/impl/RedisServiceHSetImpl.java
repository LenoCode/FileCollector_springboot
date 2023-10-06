package ai.atmc.hawkadoccollector.generalServices.redisService.impl;

import ai.atmc.hawkadoccollector.generalServices.redisService.RedisService;
import ai.atmc.hawkadoccollector.utilz.redis.RedisClient;
import ai.atmc.kvstore.utils.KeyValueStoreException;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


@Service
public class RedisServiceHSetImpl implements RedisService {


    @Value(value = "${hawkdoc.redis.host}")
    private String host;

    @Value(value = "${hawkdoc.redis.port}")
    private int port;

    private RedisClient redisClient;


    @PostConstruct
    public void init() {
        this.redisClient = new RedisClient(host, port);
    }

    /**
     * Add to set
     * @param a1
     * @param a2
     * @param value
     */
    public void addToSet(String a1, String a2, String value) {
        try {
            this.redisClient.getiKeyValueStore().hset(a1, a2, value);
        } catch (KeyValueStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if exist in set
     * @param a1
     * @param a2
     * @return
     */
    public boolean isExist(String a1, String a2) {
        try {
            return this.redisClient.getiKeyValueStore().hget(a1, a2) != null;
        } catch (KeyValueStoreException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @TODO Check if this function is needed, for now it is DEPRECATED
     * @param a1
     * @param a2
     * @param size
     * @return
     * @throws Exception
     */
    public boolean isPending(String a1, String a2, Long size) throws Exception {

        try {
            String redisData = this.redisClient.getiKeyValueStore().hget(a1, a2);
            if (redisData == null) {

                Map<String, Object> redisMap = new HashMap<>();
                redisMap.put("processed", "false");
                redisMap.put("size", size.toString());
                this.addToHset(a1, a2, redisMap);
                return true;
            }
            Map<String, Object> fileHashMap = new ObjectMapper().readValue(redisData, HashMap.class);


            Boolean processed = Boolean.parseBoolean((String) fileHashMap.get("processed"));

            if (processed.equals(Boolean.TRUE)) {
                return true;
            }

            Long storedSize = Long.valueOf((String) fileHashMap.get("size"));

            System.out.println(storedSize + " " + size);
            int comparison =Long.compare(size,storedSize);
            System.out.println(comparison);
            if (comparison > 0) {
                fileHashMap.replace("size", size.toString());
                this.addToHset(a1, a2, fileHashMap);
                return true;
            } else if (comparison == 0) {
                fileHashMap.replace("processed", "true");
                this.addToHset(a1, a2, fileHashMap);
                return false;
            } else {
                //TODO check what to do if this happens (stored size iz bigger than the one provided)
                throw new Exception("Oof. The stored file size seems to be bigger than the one provided.");
            }

        } catch (KeyValueStoreException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     *
     */
    public boolean removeHSet(String id) {
        try {
            this.redisClient.getiKeyValueStore().delete(id);
            return true;
        } catch (KeyValueStoreException e) {
            throw new RuntimeException(e);
        }
    }


    public void addToHset(String a1, String a2, Map<String, Object> redisMap) {

        JSONObject jsonObject = new JSONObject(redisMap);
        String jsonDataString = jsonObject.toString();

        try {
            this.redisClient.getiKeyValueStore().hset(a1, a2, jsonDataString);
        } catch (KeyValueStoreException e) {
            throw new RuntimeException(e);
        }

    }
}
