package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        User savedUser = userRepository.save(user);

        //Jut simply add the user to the Db and return the userId returned by the repository
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).get();
        Integer age = user.getAge();
        SubscriptionType subs = user.getSubscription().getSubscriptionType();

        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        Integer count = 0;

        if(subs.equals(SubscriptionType.BASIC)){
            for(WebSeries w : webSeriesList) {
                if(w.getSubscriptionType().equals(SubscriptionType.BASIC) && w.getAgeLimit() <= age) {
                    count++;
                }
            }

        } else if (subs.equals(SubscriptionType.PRO)) {
            for(WebSeries w : webSeriesList) {
                if(!w.getSubscriptionType().equals(SubscriptionType.ELITE) && w.getAgeLimit() <= age) {
                    count++;
                }
            }

        } else {
            for(WebSeries w : webSeriesList) {
                if(w.getAgeLimit() <= age) {
                    count++;
                }
            }

        }

        return count;
    }


}
