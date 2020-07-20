package com.yuan;

import redis.clients.jedis.Jedis;

/**
 * @author yuan
 * @date 2020/7/20 2:23 下午
 * 使用redis HyperLogLog 做网站uv统计,因为uv需要去重,如果对每个页面都用set的话数据量太大,
 * 所以可以用HyperLogLog来做这种大数据量的不精确统计
 */
public class StatisticsService {
    private Jedis jedis = new Jedis();

    /**
     * 添加访问量
     * @param userId 用户id
     * @param page 页面
     * @return
     */
    public boolean addCount(int userId, String page) {
        return jedis.pfadd(getKey(page), userId + "") == 1;
    }

    /**
     * 获取页面uv
     * @param page 页面
     * @return
     */
    public long getCount(String page) {
        return jedis.pfcount(getKey(page));
    }

    public String getKey(String page) {
        return String.format("uv:%s", page);
    }
}
