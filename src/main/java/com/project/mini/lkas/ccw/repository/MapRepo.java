package com.project.mini.lkas.ccw.repository;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.project.mini.lkas.ccw.constant.Constant;

@Repository
public class MapRepo {

    @Autowired
    @Qualifier(Constant.template02)
    RedisTemplate<String, String> redisTemplate;

    // day 15 - slide 36
    public void create(String key, String hashKey, String hashvalue) {
        redisTemplate.opsForHash().put(key, hashKey, hashvalue);
    }

    // day 15 - slide 37
    public String get(String key, String hashKey) {
        Object objValue = redisTemplate.opsForHash().get(key, hashKey);
        return objValue.toString();
    }

    // day 15 - slide 38
    public void deleteHashKey(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    // day 15 - slide 39
    public Boolean hashKeyExists(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    // day 15 - slide 40
    // List<Object> values = template.opsForHash().values("c01")l
    public List<Object> getValues(String key) {
        List<Object> values = redisTemplate.opsForHash().values(key);
        return values;
    }

    // day 15 - slide 41
    // long mapSize= template.opsForHash().size(“c01”);
    public Long size(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    public void expire(String redisKey, Long expireValue) {
        Duration expireDuration = Duration.ofSeconds(expireValue);
        redisTemplate.expire(redisKey, expireDuration);
    }

    public void update(String redisKey, String hashKey, String newHashValue) {
        redisTemplate.opsForHash().put(redisKey, hashKey, newHashValue);
    }


}
