package com.project.mini.lkas.ccw.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.project.mini.lkas.ccw.constant.Constant;

@Repository
public class ValueRepo {
    
    @Autowired
    @Qualifier(Constant.template01)
    RedisTemplate<String, String> redisTemplate;

    public void setValue(String redisKey, String value) {
        redisTemplate.opsForValue().set(redisKey, value);
    }

    public String getValue(String redisKey) {
        return redisTemplate.opsForValue().get(redisKey);
    }

    public void incrementValue(String redisKey) {
        redisTemplate.opsForValue().increment(redisKey);
    }

    public void decrementValue(String redisKey) {
        redisTemplate.opsForValue().decrement(redisKey);
    }

    public void incrementByValue(String redisKey, Integer valueToIncrement) {
        redisTemplate.opsForValue().increment(redisKey, valueToIncrement);
    }

    public void decrementByValue(String redisKey, Integer valueToDecrement) {
        redisTemplate.opsForValue().decrement(redisKey, valueToDecrement);
    }

    public void deleteKey(String redisKey) {
        redisTemplate.delete(redisKey);
    }

    public Boolean checkKeyExists(String redisKey) {
        return redisTemplate.hasKey(redisKey);
    }
}
