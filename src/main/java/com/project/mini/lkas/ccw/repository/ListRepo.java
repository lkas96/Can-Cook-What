package com.project.mini.lkas.ccw.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.project.mini.lkas.ccw.constant.Constant;

@Repository
public class ListRepo {

    @Autowired
    @Qualifier(Constant.template01)
    RedisTemplate<String, String> redisTemplate;

    public void leftPush(String redisKey, String value) {
        redisTemplate.opsForList().leftPush(redisKey, value);
    }

    public void rightPush(String redisKey, String value) {
        redisTemplate.opsForList().rightPush(redisKey, value);
    }

    public void leftPop(String redisKey) {
        redisTemplate.opsForList().leftPop(redisKey);
    }

    public void rightPop(String redisKey) {
        redisTemplate.opsForList().rightPop(redisKey);
    }

    public Long size(String redisKey) {
        return redisTemplate.opsForList().size(redisKey);
    }

    public String get(String redisKey, Integer index) {
        return redisTemplate.opsForList().index(redisKey, index);
    }

    public List<String> getList(String redisKey) {
        return redisTemplate.opsForList().range(redisKey, 0, -1);
    }
}
