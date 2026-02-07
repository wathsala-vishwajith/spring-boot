package com.solid.isp.good;

/**
 * Robot only implements Workable - no forced unnecessary methods!
 */
public class Robot implements Workable {

    private String modelNumber;

    public Robot(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    @Override
    public void work() {
        System.out.println("Robot " + modelNumber + " is working efficiently");
    }

    // No need to implement eat(), sleep(), etc.
    // ISP is satisfied!
}
