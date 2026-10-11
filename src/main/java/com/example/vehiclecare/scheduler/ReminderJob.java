package com.example.vehiclecare.scheduler;

import com.example.vehiclecare.dto.ReminderResponse;
import com.example.vehiclecare.service.NotificationService;
import com.example.vehiclecare.service.ReminderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReminderJob {

    private final ReminderService reminders;
    private final NotificationService notifier;

    public ReminderJob(ReminderService reminders, NotificationService notifier) {
        this.reminders = reminders;
        this.notifier = notifier;
    }

    /** Daily digest of everything DUE_SOON or OVERDUE. Cron is configurable via app.reminder.cron. */
    @Scheduled(cron = "${app.reminder.cron:0 0 8 * * *}")
    public void run() {
        List<ReminderResponse> due = reminders.list(null, true);
        if (due.isEmpty()) {
            return;
        }
        String body = due.stream()
                .map(r -> "[" + r.status() + "] " + r.summary())
                .collect(Collectors.joining("\n"));
        notifier.send("Vehicle care: " + due.size() + " item(s) need attention", body);
    }
}
