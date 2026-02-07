package com.solid.isp.bad;

/**
 * Problem: Contractor doesn't get paid vacation or submit timesheets
 * But is forced to implement these methods
 */
public class Contractor implements Worker {

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

    @Override
    public void submitTimesheet() {
        // Contractors invoice differently
        throw new UnsupportedOperationException("Contractors don't submit timesheets, they send invoices");
    }

    @Override
    public void getPaidVacation() {
        // Contractors don't get paid vacation!
        throw new UnsupportedOperationException("Contractors don't get paid vacation");
    }
}
