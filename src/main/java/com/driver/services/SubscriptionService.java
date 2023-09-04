package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    //@Autowired
    SubscriptionRepository subscriptionRepository = new SubscriptionRepository() {
        @Override
        public List<Subscription> findAll() {
            return null;
        }

        @Override
        public List<Subscription> findAll(Sort sort) {
            return null;
        }

        @Override
        public List<Subscription> findAllById(Iterable<Integer> iterable) {
            return null;
        }

        @Override
        public <S extends Subscription> List<S> saveAll(Iterable<S> iterable) {
            return null;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends Subscription> S saveAndFlush(S s) {
            return null;
        }

        @Override
        public void deleteInBatch(Iterable<Subscription> iterable) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Subscription getOne(Integer integer) {
            return null;
        }

        @Override
        public <S extends Subscription> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends Subscription> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public Page<Subscription> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Subscription> S save(S s) {
            return null;
        }

        @Override
        public Optional<Subscription> findById(Integer integer) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(Integer integer) {
            return false;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Integer integer) {

        }

        @Override
        public void delete(Subscription subscription) {

        }

        @Override
        public void deleteAll(Iterable<? extends Subscription> iterable) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public <S extends Subscription> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Subscription> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Subscription> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Subscription> boolean exists(Example<S> example) {
            return false;
        }
    };

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        int screens = subscriptionEntryDto.getNoOfScreensRequired();
        Date date = new Date();
        int userId = subscriptionEntryDto.getUserId();

        int amount = 0;
        if(subscriptionType.equals(SubscriptionType.BASIC))
            amount = 500 + 200 * screens;
        else if(subscriptionType.equals(SubscriptionType.PRO))
            amount = 800 + 250 * screens;
        else
            amount = 1000 + 350 * screens;

        Subscription subscription = new Subscription(subscriptionType, screens, date, amount);
        User user = userRepository.findById(userId).get();
        subscription.setUser(user);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        user.setSubscription(savedSubscription);
        userRepository.save(user);

        return savedSubscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        SubscriptionType subs = subscription.getSubscriptionType();

        int extraAmount = 0;

        if(subs.equals(SubscriptionType.BASIC)){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            extraAmount = 300 + 50 * subscription.getNoOfScreensSubscribed();
            subscription.setTotalAmountPaid(subscription.getTotalAmountPaid() + extraAmount);

        } else if (subs.equals(SubscriptionType.PRO)) {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            extraAmount = 200 + 100 * subscription.getNoOfScreensSubscribed();
            subscription.setTotalAmountPaid(subscription.getTotalAmountPaid() + extraAmount);

        } else {
            throw new Exception("Already the best Subscription");

        }

        user.setSubscription(subscription);
        userRepository.save(user);
        return extraAmount;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int revenue = 0;


        if(subscriptionList.size() > 0){
            for (Subscription subscription : subscriptionList) {
                revenue += subscription.getTotalAmountPaid();
            }
        }

        return revenue;
    }

}
