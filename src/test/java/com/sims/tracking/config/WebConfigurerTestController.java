package com.sims.tracking.config;

import org.junit.Ignore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Ignore
@RestController
public class WebConfigurerTestController {

    @GetMapping("/api/test-cors")
    public void testCorsOnApiPath() {
    }

    @GetMapping("/test/test-cors")
    public void testCorsOnOtherPath() {
    }
}
