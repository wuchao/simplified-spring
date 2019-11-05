package org.springframework.context.annotation;

import org.junit.Test;
import org.springframework.context.annotation.beans.UserService;

public class AnnotationConfigApplicationContextTests {

	@Test
	public void testAnnotationConfigApplicationContext() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("org.springframework.context.annotation.beans");
		context.refresh();

		Object object = context.getBean(UserService.class);
		if (object instanceof UserService) {
			System.out.println(((UserService) object).welcome());
			System.out.println("companyName: " +
					((UserService) object).getCompanyName());
		}
	}

}
