package org.springframework.context.annotation.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private CompanyService companyService;

	public String getCompanyName(){
		return companyService.getCompanyName();
	}

    public String welcome() {
        return "This is UserService.";
    }

}
