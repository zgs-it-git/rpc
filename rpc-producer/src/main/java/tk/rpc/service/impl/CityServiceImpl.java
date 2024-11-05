package tk.rpc.service.impl;

import com.alibaba.fastjson.JSONObject;
import tk.rpc.annotation.RpcService;
import tk.rpc.entity.City;
import tk.rpc.service.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author: Zhu Guangshun
 * @Date: 2024-11-04 20:59
 **/
@RpcService
public class CityServiceImpl implements CityService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    Map<String, City> cityMap = new HashMap<>();

    @Override
    public List<City> insert(City city) {
        logger.info("新增城市信息:{}", JSONObject.toJSONString(city));
        cityMap.put(city.getId(), city);
        return getCityList();
    }

    private List<City> getCityList() {
        List<City> cityList = new ArrayList<>();
        Iterator<Map.Entry<String, City>> iterator = cityMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, City> next = iterator.next();
            cityList.add(next.getValue());
        }
        logger.info("返回城市信息记录数：{}", cityList.size());
        return cityList;
    }

    @Override
    public City getById(String id) {
        City city = cityMap.get(id);
        logger.info("查询城市ID:{}", id);
        return city;
    }

    @Override
    public void deleteById(String id) {
        logger.info("删除城市信息:{}", JSONObject.toJSONString(cityMap.remove(id)));
    }

    @Override
    public String getNameById(String id) {
        logger.info("根据ID查询城市名称:{}", id);
        return cityMap.get(id).getName();
    }

    @Override
    public Map<String, City> getAllCity() {
        logger.info("查询所有城市信息:{}", cityMap.keySet().size());
        return cityMap;
    }
}
