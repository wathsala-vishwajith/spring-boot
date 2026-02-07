package com.solid.isp.bad;

/**
 * Problem: Robot is forced to implement methods that don't make sense
 * Violates ISP - clients shouldn't be forced to depend on methods they don't use
 */
public class Robot implements Worker {

    private String modelNumber;

    public Robot(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    @Override
    public void work() {
        System.out.println("Robot " + modelNumber + " is working");
    }

    @Override
    public void eat() {
        // Robots don't eat!
        throw new UnsupportedOperationException("Robots don't eat");
    }

    @Override
    public void sleep() {
        // Robots don't sleep!
        throw new UnsupportedOperationException("Robots don't sleep");
    }

    @Override
    public void attendMeeting() {
        // Robots don't attend meetings!
        throw new UnsupportedOperationException("Robots don't attend meetings");
    }

    @Override
    public void submitTimesheet() {
        // Robots don't submit timesheets!
        throw new UnsupportedOperationException("Robots don't submit timesheets");
    }

    @Override
    public void getPaidVacation() {
        // Robots don't get vacations!
        throw new UnsupportedOperationException("Robots don't get paid vacation");
    }
}
