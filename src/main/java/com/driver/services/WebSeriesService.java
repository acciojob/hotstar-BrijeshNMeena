package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        ProductionHouse productionHouse = productionHouseRepository.findById(productionHouseId).get();
        webSeries.setProductionHouse(productionHouse);

        WebSeries  savedWebSeries = webSeriesRepository.save(webSeries);
        productionHouse.getWebSeriesList().add(savedWebSeries);

        double rating = 0;
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        for(WebSeries ws : webSeriesList) {
            rating += ws.getRating();
        }

        rating /= webSeriesList.size();
        productionHouse.setRatings(rating);

        productionHouseRepository.save(productionHouse);

        return 0;
    }

}
