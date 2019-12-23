package me.pointofreview.persistence;

import me.pointofreview.core.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class MongoUserDataStore implements UserDataStore {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public boolean existsUsername(String username){
        return mongoTemplate.exists(Query.query(Criteria.where("username").is(username)), User.class);
    }

    @Override
    public User getUserById(String userId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), User.class);
    }

    @Override
    public User getUserByUsername(String username) {
        return mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), User.class);
    }

    @Override
    public boolean checkUsernameAndPassword(String username, String password) {
        var user = mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), User.class);
        return user != null && user.getPassword().equals(password);
    }

    @Override
    public boolean createUser(User user) {
        mongoTemplate.insert(user);
        return true;
    }

    @Override
    public boolean updateUser(User user) {
        return false; // TODO: do we need this?
    }
}