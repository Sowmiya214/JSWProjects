package com.jsw.fe.Mail;

import com.jsw.fe.Utility.Constants;
import com.jsw.fe.master.Entity.LookupMstrModel;
import com.jsw.fe.master.Service.LookupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("MstrMailInfo")
public class MailController {
	@Autowired
	private MailService mailService;
	
	@Autowired
	private LookupService lookupService;
	
	@PostMapping("/saveMailDetails")
	public ResponseEntity<?> saveMailDetails(@RequestBody MailModal masterMailModal) {
		log.info("saveMailDetails");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Optional<LookupMstrModel> lookup_obj= lookupService.getLookupValue("MAIL", "REPORTER_ISSUE_MAIL");
		System.out.println(lookup_obj.toString());
		System.out.println("lookup");
		MailModal mail_obj=mailService.saveMailDetails(masterMailModal);
		System.out.println("save mail");
		if(lookup_obj.get().getLookup_value().equalsIgnoreCase(Constants.STATUS_NO)) {
			map.put("status", 2);
			map.put("data",mail_obj);
			map.put("message", Constants.DATA_SAVE_SUCCESSFULLY);
			log.info("Data save successfully");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}		
		if (mail_obj.getUser_mail_id() != null) {
			mailService.sendMail(masterMailModal);
			map.put("status", 1);
			map.put("data", mail_obj);
			map.put("message", Constants.SEND_MAIL_SUCCESSFULLY);
			log.info("Mail send successfully");
			return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			map.clear();
			map.put("status", 0);
			map.put("message", HttpStatus.INTERNAL_SERVER_ERROR);
			log.error("Internal server error");
			return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
