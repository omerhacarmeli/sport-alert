package com.spot.alert;

import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.LocationDao;
import com.spot.alert.database.UserDao;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.User;

public class MagicStickManager {
    public void run(MainActivity mainActivity) {

        AppDataBase dataBase = AppDataBase.getDatabase(mainActivity);

        UserDao userDao = dataBase.userDao();

        userDao.insertUser(new User(1, "omerh@gmail.com", null, "Omer Hacarmeli", "1234", "0554455667"));
        userDao.insertUser(new User(2, "magicjohnson@gmail.com", null, "Magic Johnson", "1234", "0554455661"));
        userDao.insertUser(new User(3, "kevindurant@gmail.com", null, "Kevin Durant", "1234", "0554455667"));
        userDao.insertUser(new User(4, "lionelmessi@gmail.com", null, "Lionel Messi", "1234", "0554455667"));
        userDao.insertUser(new User(5, "lionelmessi@gmail.com", null, "Lionel Messi", "1234", "0554455667"));
        userDao.insertUser(new User(6, "stephencurry@gmail.com", null, "Stephen Curry", "1234", "0554455667"));
        userDao.insertUser(new User(7, "yonatanhh@gmail.com", null, "Yonatan Hacarmeli", "1234", "0554455667"));
        userDao.insertUser(new User(8, "rotemh@gmail.com", null, "Rotem Hacarmeli", "1234", "0554455667"));



        LocationDao locationDao = dataBase.locationDao();

        //Location(Long id, String name, String label, Integer radius, Double latitude, Double longitude, Integer level, Double zoom) {
        locationDao.insertLocation(new Location(1L, "מוקד אבטחה", "מוקד אבטחה", 25, 31.508419, 34.593228, 1, 15.0, null));
        locationDao.insertLocation(new Location(2L, "שער בית ספר", "שער בית ספר", 25, 31.506146, 34.592953, 1, 15.0, null));
        locationDao.insertLocation(new Location(3L, "שער גבים", "שער גבים", 25, 31.506974, 34.595017, 1, 15.0, null));
        locationDao.insertLocation(new Location(4L, "שער רכבים אחורי", "שער רכבים אחורי", 25, 31.511373, 34.598264, 1, 15.0, null));


    }
}
