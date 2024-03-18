package com.jsw.fe.Mail;

public interface MailService {

	MailModal sendMail(MailModal mailModal);
	
	MailModal saveMailDetails(MailModal masterMailModal);
	
	MailModal getDtlByFeName(String fe_ext_no);
	
	//Optional<LookupMstrModel> getLookupValue(String lookup_type,String lookup_code);
}
