package top.zhengru.unipush.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author zhengru
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
     * 缓存List数据
     *
     * @param key  缓存的键值
     * @param list 缓存的值
     * @return 缓存的对象
     */
    public <T> long setList(String key, List<T> list) {
        Long count = redisTemplate.opsForList().rightPushAll(key, list);
        return count != null ? count : 0;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getList(String key) {
        return (List<T>) redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> long setSet(String key, Set<T> dataSet) {
        Long count = redisTemplate.opsForSet().add(key, dataSet.toArray());
        return count != null ? count : 0;
    }

    /**
     * 获得缓存的set
     *
     * @param key 缓存的key
     * @return set集合
     */
    public <T> Set<T> getSet(String key) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
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
}
