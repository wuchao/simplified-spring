package org.springframework.context.annotation.beans;

import org.springframework.stereotype.Service;

@Service
public class CompanyService {

	public String getCompanyName() {
		return "kshx";
	}

	public String toString() {
		return "This is CompanyService.";
	}
}
