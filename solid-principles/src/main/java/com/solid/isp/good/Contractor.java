package com.solid.isp.good;

/**
 * Contractor implements only the interfaces that make sense
 * No paid vacation or timesheet submission
 */
public class Contractor implements Workable, Eatable, Sleepable, MeetingAttendable {

    private String name;

    public Contractor(String name) {
        this.name = name;
    }

    @Override
    public void work() {
        System.out.println(name + " (contractor) is working on project");
    }

    @Override
    public void eat() {
        System.out.println(name + " is eating lunch");
    }

    @Override
    public void sleep() {
        System.out.println(name + " is resting");
    }

    @Override
    public void attendMeeting() {
        System.out.println(name + " is attending client meeting");
    }

    // No need to implement submitTimesheet() or getPaidVacation()
    // Contractor can have its own invoicing method elsewhere
}
