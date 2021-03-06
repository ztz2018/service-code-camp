package me.pointofreview.persistence;

import me.pointofreview.core.objects.Impression;
import me.pointofreview.core.objects.Notification;
import me.pointofreview.core.objects.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserDataStore implements UserDataStore {

    private Map<String, User> users;

    public InMemoryUserDataStore() {
        this.users = new HashMap<>();
    }

    @Override
    public boolean existsUsername(String username) { return false; }

    @Override
    public User getUserById(String userId) {
        return users.get(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return null;
    }

    @Override
    public boolean checkUsernameAndPassword(String username, String password) {
        return false;
    }


    @Override
    public boolean createUser(User user) {
        if (users.containsKey(user.getId()))
            return false;

        users.put(user.getId(), user);
        return true;
    }

    @Override
    public boolean addReport(User user, String reportType) {
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        if (!users.containsKey(user.getId()))
            return false;

        users.put(user.getId(), user);
        return true;
    }

    @Override
    public void resetDatabase() {

    }

    @Override
    public boolean updateUserReputation(User user,String voterId ,String sourceId,Impression impression) {
        return true;
    }

    @Override
    public List<Notification> getNotificationsByUsername(String username) {
        return null;
    }

    @Override
    public boolean addNotification(String username, Notification notification){
        return true;
    }
}
