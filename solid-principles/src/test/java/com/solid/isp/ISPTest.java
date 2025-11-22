package com.solid.isp;

import com.solid.isp.good.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Interface Segregation Principle
 */
class ISPTest {

    @Test
    void testFullTimeEmployeeImplementsAllInterfaces() {
        FullTimeEmployee employee = new FullTimeEmployee("John Doe");

        // Can be used as any of its interfaces
        Workable workable = employee;
        Eatable eatable = employee;
        Sleepable sleepable = employee;
        MeetingAttendable meetingAttendable = employee;
        TimesheetSubmittable timesheetSubmittable = employee;
        VacationEligible vacationEligible = employee;

        assertDoesNotThrow(workable::work);
        assertDoesNotThrow(eatable::eat);
        assertDoesNotThrow(sleepable::sleep);
        assertDoesNotThrow(meetingAttendable::attendMeeting);
        assertDoesNotThrow(timesheetSubmittable::submitTimesheet);
        assertDoesNotThrow(vacationEligible::getPaidVacation);
    }

    @Test
    void testRobotOnlyImplementsWorkable() {
        Robot robot = new Robot("R2D2");

        // Robot only implements Workable
        Workable workable = robot;
        assertDoesNotThrow(workable::work);

        // Robot is NOT Eatable, Sleepable, etc.
        // This is compile-time safe - we can't cast Robot to these interfaces
        assertFalse(robot instanceof Eatable);
        assertFalse(robot instanceof Sleepable);
        assertFalse(robot instanceof VacationEligible);
    }

    @Test
    void testContractorImplementsRelevantInterfaces() {
        Contractor contractor = new Contractor("Jane Smith");

        // Contractor implements some interfaces but not all
        Workable workable = contractor;
        Eatable eatable = contractor;
        Sleepable sleepable = contractor;
        MeetingAttendable meetingAttendable = contractor;

        assertDoesNotThrow(workable::work);
        assertDoesNotThrow(eatable::eat);
        assertDoesNotThrow(sleepable::sleep);
        assertDoesNotThrow(meetingAttendable::attendMeeting);

        // Contractor is NOT VacationEligible or TimesheetSubmittable
        assertFalse(contractor instanceof VacationEligible);
        assertFalse(contractor instanceof TimesheetSubmittable);
    }

    @Test
    void testWorkManagerAssignsWork() {
        WorkManager manager = new WorkManager();

        List<Workable> workers = Arrays.asList(
            new FullTimeEmployee("Alice"),
            new Robot("C3PO"),
            new Contractor("Bob")
        );

        // All workers can work, regardless of other capabilities
        assertDoesNotThrow(() -> manager.assignWork(workers));
    }

    @Test
    void testWorkManagerSchedulesLunch() {
        WorkManager manager = new WorkManager();

        List<Eatable> workers = Arrays.asList(
            new FullTimeEmployee("Alice"),
            new Contractor("Bob")
            // Note: Robot is NOT included - it doesn't eat
        );

        assertDoesNotThrow(() -> manager.scheduleLunch(workers));
    }

    @Test
    void testWorkManagerSchedulesVacations() {
        WorkManager manager = new WorkManager();

        List<VacationEligible> employees = Arrays.asList(
            new FullTimeEmployee("Alice")
            // Note: Only full-time employees get vacation
            // Contractors and Robots are NOT included
        );

        assertDoesNotThrow(() -> manager.scheduleVacations(employees));
    }

    @Test
    void testWorkManagerCollectsTimesheets() {
        WorkManager manager = new WorkManager();

        List<TimesheetSubmittable> employees = Arrays.asList(
            new FullTimeEmployee("Alice")
            // Note: Only full-time employees submit timesheets
        );

        assertDoesNotThrow(() -> manager.collectTimesheets(employees));
    }

    @Test
    void testBadExampleViolation() {
        // The bad example forces Robot to implement methods it doesn't need
        com.solid.isp.bad.Robot badRobot = new com.solid.isp.bad.Robot("BadBot");

        assertDoesNotThrow(badRobot::work);

        // These throw UnsupportedOperationException - violates ISP
        assertThrows(UnsupportedOperationException.class, badRobot::eat);
        assertThrows(UnsupportedOperationException.class, badRobot::sleep);
        assertThrows(UnsupportedOperationException.class, badRobot::getPaidVacation);
    }

    @Test
    void testInterfaceSegregationBenefit() {
        // With segregated interfaces, we can work with just the capability we need
        Workable worker1 = new FullTimeEmployee("Alice");
        Workable worker2 = new Robot("R2D2");
        Workable worker3 = new Contractor("Bob");

        // All are Workable, but have different other capabilities
        // This is the benefit of ISP - clients depend only on what they need
        assertDoesNotThrow(worker1::work);
        assertDoesNotThrow(worker2::work);
        assertDoesNotThrow(worker3::work);
    }
}
