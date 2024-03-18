package com.jsw.fe.Mail;

import com.jsw.fe.master.Entity.LookupMstrModel;
import com.jsw.fe.master.Entity.PartsMappingMstrModel;
import com.jsw.fe.master.Repository.LookupRepository;
import com.jsw.fe.transaction.Entity.PartsStatusMstrModel;
import com.jsw.fe.transaction.Entity.ProdMapMstrModel;
import com.jsw.fe.transaction.Repository.PartsStatusRepository;
import com.jsw.fe.transaction.Repository.ProdMapRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MailServiceImple implements MailService {
	@Autowired
	private MailRepository mailRepository;

	@Autowired
	private ProdMapRepository mapRepository;

	@Autowired
	private LookupRepository lookupRepository;

	@Autowired
	private PartsStatusRepository partsStatusRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String sender;

	public String[] convertListToArray(List<String> lst) {
		String[] arr = new String[lst.size()];
		for (int i = 0; i < lst.size(); i++) {
			System.out.println("size= "+lst.size());
			System.out.println(lst.get(i));
			arr[i] = lst.get(i);
		}
		return arr;
	}
	@Override
	public MailModal sendMail(MailModal masterMailModal) {
		ProdMapMstrModel mail_obj = mapRepository.getDtlByFeId(masterMailModal.getFe_id());

		// mapRepository.findById(masterMailModal.getFe_id());
		LookupMstrModel lookup_obj = new LookupMstrModel();
		lookup_obj = lookupRepository.getDetailsFromLookup("MAIL", "REPORTER_ISSUE_CC").get();

		LookupMstrModel lookup_dtl = new LookupMstrModel();
		lookup_dtl = lookupRepository.getDetailsFromLookup("MAIL", "REPORTER_ISSUE_TO").get();

		System.out.println("lookup =" + lookup_obj.getLookup_value().toString());
		List<String> addr = new ArrayList<>();
		//addr.add(lookup_obj.getLookup_value().toString());
		addr.add(mail_obj.getLocationDetails().getDeptDetails().getDept_mail_id().toString());
		System.out.println("addr= "+addr.toString());

//		String a = lookup_obj.getLookup_value() + ","
//				+ mail_obj.getLocationDetails().getDeptDetails().getDept_mail_id();
		String a =mail_obj.getLocationDetails().getDeptDetails().getDept_mail_id();
		String[] emailAddress=a.split(",");
		System.out.println("mail= " + a.toString());
        // String[] val=a.split(",");

		List<String> addr_lst = new ArrayList<>();
		addr_lst.add("comments: " + masterMailModal.getComments());
		addr_lst.add("Fe_ext_no: " + masterMailModal.getFe_ext_no());
		addr_lst.add("username: " + masterMailModal.getUser_name());
		addr_lst.add("dept: " + masterMailModal.getDept_name());
		addr_lst.add("Location: " + mail_obj.getLocationDetails().getLoc_name());
		addr_lst.add("Product Name: " + mail_obj.getProdDetails().getProduct_name());
		System.out.println(addr_lst.toString());

		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

			List<PartsMappingMstrModel> parts_objs = mail_obj.getProdDetails().getProdTypeDetails()
					.getPartsMappingDetails();

			List<PartsStatusMstrModel> parts_sts = partsStatusRepository.getPartsStatusById(mail_obj.getFe_id());

			String html1 = "<table rules=\"rows\" cellspacing=\"2\" cellpadding=\"4\" style=\"width:auto; border: 1px solid black;border-collapse: collapse;text-align: center;\">";
			
			String html2 = "";
			//String html3 = "<table rules=\"rows\" cellspacing=\"2\" cellpadding=\"4\" style=\"width:auto; border: 1px solid black;border-collapse: collapse;text-align: center;\"><thead style=\"100%;color:#ffffff;background-color:#1f2240;font-weight:bold; border: 1px solid black;\"><tr><th colspan=\"6\" scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">PARTS - INFO</th></tr><th scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Parts-Name</th><th scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Parts-Status</th></tr></thead><tbody><tr>";
			
			html1 +="<thead style=\"100%;color:#ffffff;background-color:#1f2240;font-weight:bold; border: 1px solid black;\"><tr><th colspan=\"6\" scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">REPORTER ISSUE</th></tr><th colspan=\"2\" scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">UserName</th><th colspan=\"2\" scope=\"col\" style=\"background-color:#DBE9FA;border: 1px solid black;\">Department</th><th colspan=\"2\" scope=\"col\" style=\"background-color:#DBE9FA;\">Mobile No</th></thead><tbody>"
					+"<tr><td  colspan=\"2\" style=\" border: 1px solid black;border-collapse: collapse;font-weight:bold;\">"
				+ masterMailModal.getUser_name() + "</td><td  colspan=\"2\" style=\"border-collapse: collapse;\">"
					+ masterMailModal.getDept_name()+ "</td><td  colspan=\"2\" style=\"border-collapse: collapse;border: 1px solid black;\">"
				+ masterMailModal.getMobile_no()+ "</td></tr>"
				+ "<thead style=\"100%;color:#ffffff;background-color:#1f2240;font-weight:bold; border: 1px solid black;\"><tr><th colspan=\"1\" scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Comments</th><td colspan=\"6\" style=\"font-style: italic;font-weight:bold;text-align: left;\" >"
				+ masterMailModal.getComments() + "</td></thead></tbody>"
				+ "<thead style=\"100%;color:#ffffff;background-color:#1f2240;font-weight:bold; border: 1px solid black;\"><tr><th colspan=\"6\" scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">FE - INFO</th></tr><th scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Fe-ext-no</th><th scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Product</th><th scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Location</th><th scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Department</th><th scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Zone</th><th scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Plant</th></tr></thead><tbody>"
				+ "<tr><td style=\" border: 1px solid black;border-collapse: collapse;\">" + mail_obj.getFe_ext_no()
				+ "</td><td style=\" border: 1px solid black;border-collapse: collapse;\">"
				+ mail_obj.getProdDetails().getProduct_name()
				+ "</td><td style=\" border: 1px solid black;border-collapse: collapse;\">"
				+ mail_obj.getLocationDetails().getLoc_name()
				+ "</td><td style=\" border: 1px solid black;border-collapse: collapse;\">"
				+ mail_obj.getLocationDetails().getDeptDetails().getDept_name()
				+ "</td><td style=\" border: 1px solid black;border-collapse: collapse;\">"
				+ mail_obj.getLocationDetails().getDeptDetails().getZoneDetails().getZone_name()
				+ "</td><td style=\" border: 1px solid black;border-collapse: collapse;\">"
				+ mail_obj.getLocationDetails().getDeptDetails().getZoneDetails().getPlantDetails().getPlant_name()
				+ "</td></tr></thead></tbody>";

			html1+="<thead style=\"100%;color:#ffffff;background-color:#1f2240;font-weight:bold; border: 1px solid black;\"><tr><th colspan=\"6\" scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">PARTS - INFO</th></tr><th colspan=\"3\" scope=\"col\"  style=\"background-color:#DBE9FA; border: 1px solid black;\">Parts-Name</th><th colspan=\"3\"  scope=\"col\" style=\"background-color:#DBE9FA; border: 1px solid black;\">Parts-Status</th></tr></thead><tbody>";


			for (int i = 0; i < parts_objs.size(); i++) {
				String p_n = "";
				p_n += parts_objs.get(i).getPartsDetails().getParts_name();
				parts_objs.get(i).getPartsDetails().getParts_name();
				if( parts_sts.get(i).getParts_status().equalsIgnoreCase("false")){
					parts_sts.get(i).setParts_status("Healthy Condition");
				}else{
					parts_sts.get(i).setParts_status("Attention Required");
				}
				String listOfPartsSts = parts_sts.get(i).getParts_status().equals("Healthy Condition") ? "color: green;" : "color: red;";
				html1+="<tr><td colspan=\"3\" style=\" border: 1px solid black;border-collapse: collapse;\">" + p_n
						+"</td><td  colspan=\"4\" style=\" border: 1px solid black;border-collapse: collapse;\">"
						+ "<span style=\"" + listOfPartsSts + "\">" + parts_sts.get(i).getParts_status() + "</span>"
						+ "</td>"+"</tr>";
				System.out.println("parts details: "+parts_sts.get(i).getPartsMstrDetails().getParts_name()+","+parts_sts.get(i).getParts_status());
		}
			html1 += "</tbody>" + "</table>";


			//html3 += "</tbody>" + "</table>";

			message.setContent(html1 , "text/html; charset=utf-8");
			helper.setFrom(sender);
			helper.setTo(lookup_dtl.getLookup_value().toString());
			helper.setCc(emailAddress);
			helper.setSubject("\uD83E\uDDEF"+"Reporter Issue: " + masterMailModal.getFe_ext_no());
			System.out.println("message=" + message.toString());
		    mailSender.send(message);

		} catch (MessagingException e) {
			throw new MailParseException(e);
		} finally {
			String result = "success";
			if (result != "success") {
				result = "fail";
			}
		}
		return masterMailModal;
	}

	@Override
	public MailModal saveMailDetails(MailModal masterMailModal) {
		System.out.println("send mail..");
		// save details
		masterMailModal.setUser_mail_id(null);
		masterMailModal.setCreated_date_time(new Date());
		mailRepository.save(masterMailModal);
		System.out.println("save successfully..");
		return masterMailModal;
		// sending mail

	}

	@Override
	public MailModal getDtlByFeName(String fe_ext_no) {
		// TODO Auto-generated method stub
		return mailRepository.getDetailsByName(fe_ext_no);
	}
}
