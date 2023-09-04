package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Don't forget to save the production and webseries Repo

        String name = webSeriesEntryDto.getSeriesName();
        if(webSeriesRepository.findBySeriesName(name) != null) {
            throw new Exception("Series is already present");
        }

        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(name);
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

        int productionHouseId = webSeriesEntryDto.getProductionHouseId();
        Optional<ProductionHouse> opProductionHouse = productionHouseRepository.findById(productionHouseId);
        if(!opProductionHouse.isPresent()) {
            throw new Exception("Production house is not found");
        }
        ProductionHouse productionHouse = opProductionHouse.get();
        webSeries.setProductionHouse(productionHouse);

        WebSeries savedWebSeries = webSeriesRepository.save(webSeries);
        productionHouse.getWebSeriesList().add(savedWebSeries);

        double rating = 0.0;
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        for(WebSeries ws : webSeriesList) {
            rating += ws.getRating();
        }

        rating = rating/webSeriesList.size();
        productionHouse.setRatings(rating);

        productionHouseRepository.save(productionHouse);
        WebSeries webSeries1 = webSeriesRepository.save(webSeries);

        return webSeries1.getId();
    }

}
