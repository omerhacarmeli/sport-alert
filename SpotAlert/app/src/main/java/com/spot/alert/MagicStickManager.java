package com.spot.alert;

import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.LocationDao;
import com.spot.alert.database.UserDao;
import com.spot.alert.database.UserTimeRangeDao;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.User;
import com.spot.alert.dataobjects.UserTimeRange;

public class MagicStickManager {
    public void run(MainActivity mainActivity) {

        AppDataBase dataBase = AppDataBase.getDatabase(mainActivity);

        insertUsers(dataBase.userDao());
        insertUsersTimeRange(dataBase.userTimeRangeDao());
        insertLocations(dataBase.locationDao());
    }

    private void insertLocations(LocationDao locationDao) {
        //Location(Long id, String name, String label, Integer radius, Double latitude, Double longitude, Integer level, Double zoom) {
        locationDao.insertLocation(new Location(1L, "מוקד אבטחה", "מוקד אבטחה", 25, 31.508419, 34.593228, 1, 15.0, null));
        locationDao.insertLocation(new Location(2L, "שער בית ספר", "שער בית ספר", 25, 31.506146, 34.592953, 1, 15.0, null));
        locationDao.insertLocation(new Location(3L, "שער גבים", "שער גבים", 25, 31.506974, 34.595017, 1, 15.0, null));
        locationDao.insertLocation(new Location(4L, "שער רכבים אחורי", "שער רכבים אחורי", 25, 31.511373, 34.598264, 1, 15.0, null));
    }

    private void insertUsersTimeRange(UserTimeRangeDao userTimeRangeDao) {
        //(int Long, Double fromTime, Double toTime, int dayWeek, Long userId)
        userTimeRangeDao.insertUserTimeRange(new UserTimeRange(1,9.30,14.15,6,1L));
        userTimeRangeDao.insertUserTimeRange(new UserTimeRange(2,16.00,18.45,6,1L));
        userTimeRangeDao.insertUserTimeRange(new UserTimeRange(3,11.30,18.15,6,2L));

    }

    private void insertUsers(UserDao userDao) {
        userDao.insertUser(new User(1L, "omerh@gmail.com", null, "Omer Hacarmeli", "1234", "0554455667"));
        userDao.insertUser(new User(2L, "magicjohnson@gmail.com", null, "Magic Johnson", "1234", "0554455661"));
        userDao.insertUser(new User(3L, "kevindurant@gmail.com", null, "Kevin Durant", "1234", "0554455667"));
        userDao.insertUser(new User(4L, "lionelmessi@gmail.com", null, "Lionel Messi", "1234", "0554455667"));
        userDao.insertUser(new User(5L, "lionelmessi@gmail.com", null, "Lionel Messi", "1234", "0554455667"));
        userDao.insertUser(new User(6L, "stephencurry@gmail.com", null, "Stephen Curry", "1234", "0554455667"));
        userDao.insertUser(new User(7L, "yonatanhh@gmail.com", null, "Yonatan Hacarmeli", "1234", "0554455667"));
        userDao.insertUser(new User(8L, "rotemh@gmail.com", null, "Rotem Hacarmeli", "1234", "0554455667"));
    }
}
