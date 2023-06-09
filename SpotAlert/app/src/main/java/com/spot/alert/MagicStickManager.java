package com.spot.alert;

import com.spot.alert.database.AppDataBase;
import com.spot.alert.database.CalendarManagementDao;
import com.spot.alert.database.LocationDao;
import com.spot.alert.database.LocationTimeRangeDao;
import com.spot.alert.database.UserDao;
import com.spot.alert.database.UserTimeRangeDao;
import com.spot.alert.dataobjects.CalendarManagement;
import com.spot.alert.dataobjects.Location;
import com.spot.alert.dataobjects.LocationTimeRange;
import com.spot.alert.dataobjects.User;
import com.spot.alert.dataobjects.UserTimeRange;
import com.spot.alert.utils.CalendarUtils;

import java.time.LocalDate;

public class MagicStickManager {
    CalendarManagementDao calendarManagementDao;
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
        calendarManagementDao = dataBase.calendarManagementDao();

        insertUsers();
        insertUsersTimeRange();
        insertLocations();
        insertLocationsTimeRange();
    }

    private void insertCalendarManagement(long locationId, long locationTimeRangeId) {
        String date = CalendarUtils.formattedDate(LocalDate.now());
        for (int i = 8; i < 22; i++) {

            try {
                calendarManagementDao.insertCalendarManagement(new CalendarManagement(null, date, i + ":00", locationId, 1L, locationTimeRangeId));
            } catch (Exception e) {

            }
        }
    }

    private void insertLocationsTimeRange() {

        int seq = 0;

        for (long locationId = 2; locationId < 5; locationId++) {
            for (int day = 1; day < 8; day++) {
                seq++;
                locationTimeRangeDao.insertLocationTimeRange(new LocationTimeRange(seq, 8.00, 22.00, day, locationId));

                insertCalendarManagement(locationId, seq);
            }
        }
    }

    private void insertLocations() {
        //Location(Long id, String name, String label, Integer radius, Double latitude, Double longitude, Integer level, Double zoom) {
        locationDao.insertLocation(new Location(1L, "מוקד אבטחה", "מוקד אבטחה", 25, 31.508419, 34.593228, 1, 15.0, null));
        locationDao.insertLocation(new Location(2L, "שער בית ספר", "שער בית ספר", 25, 31.506146, 34.592953, 1, 15.0, null));
        locationDao.insertLocation(new Location(3L, "שער גבים", "שער גבים", 25, 31.506974, 34.595017, 1, 15.0, null));
        locationDao.insertLocation(new Location(4L, "שער רכבים אחורי", "שער רכבים אחורי", 25, 31.511373, 34.598264, 1, 15.0, null));
    }

    private void insertUsersTimeRange() {
        //(int Long, Double fromTime, Double toTime, int dayWeek, Long userId)

        int seq = 0;

        for (long userId = 1; userId < 9; userId++) {
            for (int day = 1; day < 8; day++) {
                seq++;
                userTimeRangeDao.insertUserTimeRange(new UserTimeRange(seq, 6.00, 17.00, day, userId));
                seq++;
                userTimeRangeDao.insertUserTimeRange(new UserTimeRange(seq, 18.15, 20.45, day, userId));
            }
        }
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
