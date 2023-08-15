package com.rendra.test.service.impl;

import com.rendra.test.entity.Dog;
import com.rendra.test.service.CrudService;
import com.rendra.test.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService{

    @Autowired
    private CrudService<Dog, Long> crudService;

    @Override
    @Scheduled(cron = "0 */1 * * * *")
    public void fetchDogEveryMinute() {
        log.info("execute fetchDogEveryMinute");
        crudService.getAll();
    }
}
