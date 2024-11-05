package tk.rpc.service;

import tk.rpc.entity.City;

import java.util.List;
import java.util.Map;

public interface CityService {
    List<City> insert(City infoUser);

    City getById(String id);

    void deleteById(String id);

    String getNameById(String id);

    Map<String,City> getAllCity();
}
