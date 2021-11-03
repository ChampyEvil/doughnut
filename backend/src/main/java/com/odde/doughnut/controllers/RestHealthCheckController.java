
package com.odde.doughnut.controllers;

import com.odde.doughnut.entities.ReviewPoint;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
class RestHealthCheckController {
    @Autowired
    private Environment environment;

    @Autowired
    private ModelFactoryService modelFactoryService;

    @GetMapping("/healthcheck")
    public String ping() {
        return "OK. Active Profile: " + String.join(", ", environment.getActiveProfiles());
    }

    @GetMapping("/data_upgrade")
    @Transactional
    public List dataUpgrade() {
        modelFactoryService.entityManager.createNativeQuery("delete from review_point where note_id=1806").executeUpdate();
        modelFactoryService.entityManager.createNativeQuery("delete from review_point where note_id=null").executeUpdate();
        List resultList = modelFactoryService.entityManager.createNativeQuery("select user_id, note_id, count(1) as cnt from review_point rp group by note_id, user_id having cnt > 1").getResultList();
        return resultList;
    }

}
