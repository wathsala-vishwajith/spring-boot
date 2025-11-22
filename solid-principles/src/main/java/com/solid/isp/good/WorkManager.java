package com.solid.isp.good;

import java.util.List;

/**
 * Manager can work with specific capabilities independently
 */
public class WorkManager {

    public void assignWork(List<Workable> workers) {
        for (Workable worker : workers) {
            worker.work();
        }
    }

    public void scheduleLunch(List<Eatable> workers) {
        for (Eatable worker : workers) {
            worker.eat();
        }
    }

    public void scheduleVacations(List<VacationEligible> employees) {
        for (VacationEligible employee : employees) {
            employee.getPaidVacation();
        }
    }

    public void collectTimesheets(List<TimesheetSubmittable> employees) {
        for (TimesheetSubmittable employee : employees) {
            employee.submitTimesheet();
        }
    }
}
