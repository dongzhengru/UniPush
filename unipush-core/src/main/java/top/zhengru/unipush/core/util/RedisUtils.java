package top.zhengru.unipush.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类（Core模块专用）
 *
 * @author zhengru
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取RedisTemplate（用于复杂操作）
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 缓存基本的对象
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，带过期时间
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void set(String key, T value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        Boolean result = redisTemplate.expire(key, timeout, unit);
        return result != null && result;
    }

    /**
     * 获取缓存的基本对象
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key Redis键
     */
    public boolean delete(String key) {
        Boolean result = redisTemplate.delete(key);
        return result != null && result;
    }

    /**
     * 删除集合对象
     *
     * @param keys 多个Redis键
     */
    public long delete(Collection<String> keys) {
        Long result = redisTemplate.delete(keys);
        return result != null ? result : 0;
    }

    /**
     * 判断key是否存在
     *
     * @param key Redis键
     * @return true=存在；false=不存在
     */
    public boolean hasKey(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return result != null && result;
    }

    /**
     * 获取过期时间
     *
     * @param key Redis键
     * @return 过期时间（秒）
     */
    public long getExpire(String key) {
        Long expire = redisTemplate.getExpire(key);
        return expire != null ? expire : -1;
    }

    /**
     * 哈希添加
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void hSet(String key, String hKey, T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 哈希获取数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T hGet(String key, String hKey) {
        return (T) redisTemplate.opsForHash().get(key, hKey);
    }

    /**
     * 哈希获取所有数据
     *
     * @param key Redis键
     * @return Hash中的对象
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 哈希删除数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     */
    public long hDelete(String key, Object... hKey) {
        Long result = redisTemplate.opsForHash().delete(key, hKey);
        return result != null ? result : 0;
    }

    /**
     * 哈希判断是否存在
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return true=存在；false=不存在
     */
    public boolean hHasKey(String key, String hKey) {
        Boolean result = redisTemplate.opsForHash().hasKey(key, hKey);
        return result != null && result;
    }

    /**
     * 递增
     *
     * @param key Redis键
     * @return 递增后的值
     */
    public long increment(String key) {
        Long result = redisTemplate.opsForValue().increment(key);
        return result != null ? result : 0;
    }

    /**
     * 递增
     *
     * @param key       Redis键
     * @param increment 递增步长
     * @return 递增后的值
     */
    public long increment(String key, long increment) {
        Long result = redisTemplate.opsForValue().increment(key, increment);
        return result != null ? result : 0;
    }

    /**
     * 递减
     *
     * @param key Redis键
     * @return 递减后的值
     */
    public long decrement(String key) {
        Long result = redisTemplate.opsForValue().decrement(key);
        return result != null ? result : 0;
    }

    /**
     * 递减
     *
     * @param key       Redis键
     * @param decrement 递减步长
     * @return 递减后的值
     */
    public long decrement(String key, long decrement) {
        Long result = redisTemplate.opsForValue().decrement(key, decrement);
        return result != null ? result : 0;
    }

    // ============================= 专用方法：推送消息相关 =============================

    /**
     * 缓存推送消息
     *
     * @param messageId     消息ID
     * @param pushMessage   消息实体
     * @param expireSeconds 过期时间（秒）
     */
    public void setPushMessage(String messageId, Object pushMessage, long expireSeconds) {
        String key = top.zhengru.unipush.common.constant.RedisConstants.PUSH_MESSAGE_KEY + messageId;
        redisTemplate.opsForValue().set(key, pushMessage, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 获取推送消息
     *
     * @param messageId 消息ID
     * @return 推送消息实体
     */
    public <T> T getPushMessage(String messageId) {
        String key = top.zhengru.unipush.common.constant.RedisConstants.PUSH_MESSAGE_KEY + messageId;
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 添加消息到持久化队列（Sorted Set）
     *
     * @param messageId 消息ID
     * @param score     时间戳（用于定时任务排序）
     */
    public void addToPersistQueue(String messageId, long score) {
        String key = top.zhengru.unipush.common.constant.RedisConstants.PUSH_MESSAGE_BATCH_KEY;
        redisTemplate.opsForZSet().add(key, messageId, score);
    }

    /**
     * 从持久化队列中移除
     *
     * @param messageId 消息ID
     */
    public void removeFromPersistQueue(String messageId) {
        String key = top.zhengru.unipush.common.constant.RedisConstants.PUSH_MESSAGE_BATCH_KEY;
        redisTemplate.opsForZSet().remove(key, messageId);
    }

    /**
     * 批量从持久化队列中移除
     *
     * @param messageIds 消息ID列表
     */
    public long removeFromPersistQueueBatch(Object... messageIds) {
        String key = top.zhengru.unipush.common.constant.RedisConstants.PUSH_MESSAGE_BATCH_KEY;
        Long result = redisTemplate.opsForZSet().remove(key, messageIds);
        return result != null ? result : 0;
    }

    /**
     * 获取持久化队列中所有消息ID（用于定时任务）
     *
     * @param min 最小score
     * @param max 最大score
     * @param offset 偏移量
     * @param count 数量
     * @return 消息ID集合
     */
    public Set<String> getPersistQueueMessages(long min, long max, long offset, long count) {
        String key = top.zhengru.unipush.common.constant.RedisConstants.PUSH_MESSAGE_BATCH_KEY;
        Set<Object> objects = redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);

        // 转换 Set<Object> 为 Set<String>
        if (objects == null || objects.isEmpty()) {
            return new java.util.HashSet<>();
        }

        Set<String> result = new java.util.HashSet<>();
        for (Object obj : objects) {
            if (obj != null) {
                result.add(obj.toString());
            }
        }
        return result;
    }

    /**
     * 获取持久化队列的大小
     *
     * @return 队列大小
     */
    public long getPersistQueueSize() {
        String key = top.zhengru.unipush.common.constant.RedisConstants.PUSH_MESSAGE_BATCH_KEY;
        Long size = redisTemplate.opsForZSet().size(key);
        return size != null ? size : 0;
    }
}
