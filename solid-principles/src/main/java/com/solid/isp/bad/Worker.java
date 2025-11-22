package com.solid.isp.bad;

/**
 * BAD EXAMPLE: Violates Interface Segregation Principle
 * This "fat interface" forces all implementations to provide methods they may not need
 */
public interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void submitTimesheet();
    void getPaidVacation();
}
