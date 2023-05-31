package com.spot.alert.adapter.timerange;

public interface ITimeRange {
    public Long getId();

    public void setId(Long id);

    public Double getFromTime() ;

    public void setFromTime(Double fromTime);

    public Double getToTime() ;

    public void setToTime(Double toTime);

    public int getDayWeek() ;

    public void setDayWeek(int dayWeek);
}
