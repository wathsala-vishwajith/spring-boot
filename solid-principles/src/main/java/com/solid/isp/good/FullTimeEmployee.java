package com.solid.isp.good;

/**
 * Full-time employee implements all relevant interfaces
 */
public class FullTimeEmployee implements Workable, Eatable, Sleepable,
        MeetingAttendable, TimesheetSubmittable, VacationEligible {

    private String name;

    public FullTimeEmployee(String name) {
        this.name = name;
    }

    @Override
    public void work() {
        System.out.println(name + " is working on tasks");
    }

    @Override
    public void eat() {
        System.out.println(name + " is eating lunch");
    }

    @Override
    public void sleep() {
        System.out.println(name + " is taking a power nap");
    }

    @Override
    public void attendMeeting() {
        System.out.println(name + " is attending a meeting");
    }

    @Override
    public void submitTimesheet() {
        System.out.println(name + " is submitting timesheet");
    }

    @Override
    public void getPaidVacation() {
        System.out.println(name + " is going on paid vacation");
    }
}
