package com.serhat.taskFlow.interfaces;

import java.time.LocalDateTime;

public interface DateRangeParser {
    LocalDateTime parseStartDate(String startDate);

    LocalDateTime parseEndDate(String endDate);
}
