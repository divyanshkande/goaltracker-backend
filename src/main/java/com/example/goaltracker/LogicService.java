package com.example.goaltracker;

import org.springframework.stereotype.Service;

@Service
public class LogicService {



	public boolean validateUser(String username, String password) {
		 return "admin".equals(username) && "1234".equals(password);
    }


}
