package com.spot.alert;

import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.LocationDao;
import com.spot.alert.database.LocationTimeRangeDao;
import com.spot.alert.database.UserDao;
import com.spot.alert.database.UserTimeRangeDao;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.User;

public class MagicStickManager {
    LocationTimeRangeDao locationTimeRangeDao;
    LocationDao locationDao;
    UserDao userDao;
    UserTimeRangeDao userTimeRangeDao;

    public void run(MainActivity mainActivity) {

        AppDataBase dataBase = AppDataBase.getDatabase(mainActivity);
        userDao = dataBase.userDao();
        userTimeRangeDao = dataBase.userTimeRangeDao();
        locationDao = dataBase.locationDao();
        locationTimeRangeDao = dataBase.locationTimeRangeDao();

        insertUsers();
        insertLocations();
    }

    private void insertLocations() {
        //Location(Long id, String name, String label, Integer radius, Double latitude, Double longitude, Integer level, Double zoom) {
        locationDao.insertLocation(new Location(1L, "מוקד אבטחה", "מוקד אבטחה", 25, 31.508419, 34.593228, 1, 15.0, null));
        locationDao.insertLocation(new Location(2L, "שער בית ספר", "שער בית ספר", 25, 31.506146, 34.592953, 1, 15.0, null));
        locationDao.insertLocation(new Location(3L, "שער גבים", "שער גבים", 25, 31.506974, 34.595017, 1, 15.0, null));
        locationDao.insertLocation(new Location(4L, "שער רכבים אחורי", "שער רכבים אחורי", 25, 31.511373, 34.598264, 1, 15.0, null));
    }

    private void insertUsers() {
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
