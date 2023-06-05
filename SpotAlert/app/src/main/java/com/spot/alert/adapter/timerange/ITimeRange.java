package com.spot.alert.adapter.timerange;

public interface ITimeRange {
    Long getId();

    void setId(Long id);

    Double getFromTime();

    void setFromTime(Double fromTime);

    Double getToTime();

    void setToTime(Double toTime);

    int getDayWeek();

    void setDayWeek(int dayWeek);
}
