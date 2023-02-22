package ru.baz.aisa.rest.request;

public enum Periods {
    DAY(1),
    WEEK(7),
    MONTH(30);

    private Integer days;

    Periods(Integer days) {
        this.days = days;
    }

    public Integer getDays() {
        return days;
    }
}
