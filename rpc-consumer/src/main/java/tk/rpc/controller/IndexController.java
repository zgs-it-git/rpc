package tk.rpc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import tk.rpc.entity.City;
import tk.rpc.service.CityService;
import tk.rpc.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author: Zhu Guangshun
 * @Date: 2024-11-05 10:32
 **/
@Controller
public class IndexController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CityService cityService;

    @RequestMapping("getById")
    public City getById(String id){
        logger.info("根据ID查询城市信息:{}", id);
        return cityService.getById(id);
    }

    @RequestMapping("insert")
    @ResponseBody
    public List<City> insert() throws InterruptedException {
        long start = System.currentTimeMillis();
        int thread_cnt = 100;
        CountDownLatch countDownLatch = new CountDownLatch(thread_cnt);
        for (int i = 0; i < thread_cnt; i++) {
            new Thread(() -> {
                City infoUser = new City(IdUtil.getId(), "Beijing", "CHN");
                List<City> users = cityService.insert(infoUser);
                logger.info("返回城市记录:{}", JSON.toJSONString(users));
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        logger.info("线程数:{},执行时间:{}" ,thread_cnt, (end-start));
        return null;
    }

    @RequestMapping("getAllCity")
    @ResponseBody
    public Map<String,City> getAllCity() throws InterruptedException {

        long start = System.currentTimeMillis();
        int thread_count = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(thread_count);
        for (int i=0;i<thread_count;i++){
            new Thread(() -> {
                Map<String, City> allCity = cityService.getAllCity();
                logger.info("查询所有城市信息：{}", JSONObject.toJSONString(allCity));
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        logger.info("线程数：{},执行时间:{}",thread_count,(end-start));

        return null;
    }
}
