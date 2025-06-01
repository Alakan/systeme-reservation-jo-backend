package com.example.systeme_reservation_jo;

import org.junit.jupiter.api.Test;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;


@ImportAutoConfiguration(exclude = { SwaggerConfig.class })
@SpringBootTest(properties = {
		"springdoc.api-docs.enabled=false",
		"springdoc.swagger-ui.enabled=false",
		"springdoc.enabled=false"
})
class SystemeReservationJoApplicationTests {

	@Test
	void contextLoads() {
	}

}
