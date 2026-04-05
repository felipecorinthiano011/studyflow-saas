package com.studyflow.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {

	@Test
	void contextLoads() {
		// Verifies that the Spring application context starts without errors.
		// No explicit assertions are needed: a misconfigured context causes the test to fail on startup.
	}

}
