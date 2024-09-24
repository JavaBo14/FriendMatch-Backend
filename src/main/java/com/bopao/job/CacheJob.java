package com.bopao.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bopao.model.domain.User;
import com.bopao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Component
@Slf4j
public class CacheJob {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
//    private List<Long> mianUserListId = Arrays.asList(1L);
    private List<Long> mianUserListId = LongStream.rangeClosed(1, 100)
                                                   .boxed()
                                                   .collect(Collectors.toList());
    @Scheduled(cron = "0 0 0 * * *")
    public void doCacheRecommendUser() {

        RLock lock = redissonClient.getLock("bopao:cachejob:docache:lock");
        // 只有一个线程能获取到锁(看门狗机制这里必须设置-1)
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                for (Long mianUserId : mianUserListId) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> page = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("bopao:user:recommend:%s", mianUserId);
                    ValueOperations valueOperations = redisTemplate.opsForValue();
                    try {
                        // 设置1天的缓存过期时间
//                        valueOperations.set(redisKey, page, 1, TimeUnit.DAYS);
                        valueOperations.set(redisKey, page, 30, TimeUnit.SECONDS);
                        log.info("Cache updated successfully for userId: {}", mianUserId);
                    } catch (Exception e) {
                        log.error("redis set key error for userId: {}", mianUserId, e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}