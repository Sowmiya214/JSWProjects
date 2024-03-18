package com.jsw.fe.Admin.Controller;

import com.jsw.fe.Admin.Entity.LoginHistoryDetailsModel;
import com.jsw.fe.Admin.Entity.UserDetailsModel;
import com.jsw.fe.Admin.Entity.UserLoginHistoryModel;
import com.jsw.fe.Admin.Service.LoginService;
import com.jsw.fe.Config.AES256;
import com.jsw.fe.Utility.Constants;
import com.jsw.fe.master.Entity.LookupMstrModel;
import com.jsw.fe.master.Service.LookupService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@Configuration
@RequestMapping("loginInfo")
public class LoginController {

	private static final String String = null;

	@Autowired
	private LoginService loginService;
	@Autowired
	private LookupService lookupService;

	@Value("${aes.secret}")
	private  String secretKey;
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@PostMapping("/saveLogin")
	public  ResponseEntity<?>  saveloginhistoryDetails(@RequestBody UserDetailsModel userDetails) {
		log.info("saveLogin");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if(userDetails.getUser_id()!=null) {
			map.put("status", 1);
			map.put("data",loginService.saveloginhistoryDetails(userDetails));
			map.put("message", Constants.DATA_SAVE_SUCCESSFULLY);
			log.info("Data save successfully");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}else {
			map.clear();
			map.put("status", 0);
			map.put("message", HttpStatus.INTERNAL_SERVER_ERROR);
			log.error("Internal server error");
			return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getLogin")
	public ResponseEntity<?> fetchLoginHistoryDetailsList() {
		log.info("getLogin");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		 List<LoginHistoryDetailsModel> Get_Login =loginService.fetchLoginHistoryDetailsList();
		 if(!Get_Login.isEmpty()){
				map.put("status", 1);
				map.put("data", Get_Login);
				map.put("message", Constants.GET_DATA_AVAILABLE);
				log.info("Data is Available");
				return new ResponseEntity<>(map, HttpStatus.OK);
		 }else {
			 map.put("status", 0);
				map.put("message", Constants.DATA_NOT_FOUND);
				log.warn("Data not found");
				return new ResponseEntity<>(map, HttpStatus.OK);
		 }
	}
	@PostMapping("/updateLogin")
	public ResponseEntity<?> updateLoginHistoryDetails(UserDetailsModel userDetails) {
		log.info("updateLogin");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		LoginHistoryDetailsModel new_obj=loginService.updateLoginHistoryDetails(userDetails);
		System.out.println("up= "+new_obj.toString());
		if(new_obj.getUser_id()!=null) {
			map.put("status", 1);
			map.put("data", userDetails);
			map.put("message", Constants.GET_DATA_AVAILABLE);
			log.info("Data is Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}else {
			 map.put("status", 0);
				map.put("message", Constants.DATA_NOT_FOUND);
				log.warn("Data not found");
				return new ResponseEntity<>(map, HttpStatus.OK);
		}
	}
	@PostMapping("/saveUser")
	public ResponseEntity<?> saveUserDetails(@RequestBody UserDetailsModel userDetails) throws Exception {
		log.info("saveUser");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if(userDetails.getUser_id()!=null) {
			map.put("status", 2);
			map.put("data", loginService.updateUserDetails(userDetails, userDetails.getUser_id()));
			map.put("message", Constants.DATA_UPDATE_SUCCESSFULLY);
			log.info("Data updated successfully");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}else {
			map.put("status", 1);
			map.put("data", loginService.saveUserDetails(userDetails));
			map.put("message", Constants.DATA_SAVE_SUCCESSFULLY);
			log.info("Data save successfully");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
	}
	@GetMapping("/getUser")
	public ResponseEntity<?>fetchUserDetailsList() {
		log.info("getUser");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		List<UserDetailsModel>  Get_User = loginService.fetchUserDetailsList();
        if(!Get_User.isEmpty()) {
        	map.put("status", 1);
			map.put("data", Get_User);
			map.put("message", Constants.GET_DATA_AVAILABLE);
			log.info("Data is Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
        }else {
        	map.put("status", 0);
			map.put("message", Constants.DATA_NOT_FOUND);
			log.warn("Data not found");
			return new ResponseEntity<>(map, HttpStatus.OK);
        }
	}
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserDetailsModel userDetails, HttpSession session) throws Exception {
		log.info("login");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			UserDetailsModel validate_login = loginService.getlogin(userDetails.getEmp_no());
			System.out.println("validate_login: "+validate_login.toString());

			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String newHashedPassword = passwordEncoder.encode(userDetails.getLogin_pwd());

			LookupMstrModel lk_obj=new LookupMstrModel();
			lk_obj=lookupService.getLookupValueByTypeWithCode("USER_TYPE", "ADMIN").get();
			if(validate_login!=null){
				if(validate_login.getRecord_status()!=0) {
					if (validate_login.getAcc_sts() != 0) {
						if (passwordEncoder.matches(userDetails.getLogin_pwd(),validate_login.getLogin_pwd())) {
							System.out.println("pwd"+userDetails.getLogin_pwd());
							//decrypt the string
							LocalDateTime myDateObj = LocalDateTime.now();
							DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
							String formattedDate = myDateObj.format(myFormatObj);

							String userData = validate_login.getUsername() +","+ validate_login.getUser_id()+","+ formattedDate; // Convert UserDetailsModel to JSON
							String encryptedString = AES256.encrypt(userData);
							if (encryptedString != null) {
								System.out.println(" Login Encrypted: " + encryptedString);
							} else {
								System.err.println("Login Encryption failed.");
							}
							encryptedString="fmssalem "+encryptedString;

							UserDetailsModel tocken_obj = loginService.generateTokenAndUpdate(validate_login,myDateObj);

							String html ="";
							String pages = validate_login.getLookupDetails().getView_pages();
							String page_description = validate_login.getLookupDetails().getDescription();
							String[] v_page = pages.split(",");
							String[] v_description = page_description.split(",");
							for (int i = 0; i < v_page.length; i++) {
								html += "<li class=\"nav-item\"><a href=" + v_page[i] + " class=\"nav-link menu-link\"><i class=\"far fa-circle nav-icon\"></i><p style=\"color: #c2c7d0;\">" + v_description[i] + "</p></a></li>";
							}
							map.put("status", 3);
							map.put("menuList", html);
							map.put("tocken", encryptedString);
							map.put("userinfo", validate_login);
							session.setAttribute("userId",validate_login.getUser_id());
							map.put("message", Constants.ADMIN_LOGIN_SUCCESSFULLY);
							log.info("Login successfully");
							loginService.saveHistory(userDetails, "Success");
						} else {
							map.put("status", 4);
							map.put("data", validate_login);
							String msg=loginService.saveHistory(userDetails, "Failed");
							log.info("Invalid Credential, Try Again...");
							map.put("message", Constants.INVALID_CREDENTIAL+"<br><br>"+msg);
						}
					} else {
						map.put("status", 2);
						map.put("message", Constants.ACCOUNT_LOCKED);
						log.warn("your account has been locked.");
					}
					return new ResponseEntity<>(map, HttpStatus.OK);
				}
					else{
						map.put("status", 1);
						map.put("data", validate_login);
						map.put("message", Constants.ACCOUNT_INACTIVE);
						log.warn("your account is Inactive");
					}
			}
			else {
				map.put("status", 5);
				map.put("data", validate_login);
				map.put("message", Constants.ADMIN_LOGIN_FAILED);
				log.error("Login failed");
			}
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (Exception e) {
			map.clear();
			map.put("status", 0);
			map.put("message", HttpStatus.INTERNAL_SERVER_ERROR);
			log.error("Internal server error");
			return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@GetMapping("getUserRole")
		public ResponseEntity<?> getUserDtl(@RequestParam("emp_no")String emp_no){
		log.info("getUserRole "+emp_no);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		UserDetailsModel getRole=loginService.getUserDtl(emp_no);
		if(getRole!=null){
			LookupMstrModel lk_obj=new LookupMstrModel();
			lk_obj=lookupService.getLookupValueByTypeWithCode("USER_TYPE", "ADMIN").get();
			String html="";
			String pages=getRole.getLookupDetails().getView_pages();
			String page_description=getRole.getLookupDetails().getDescription();
			String[] v_page = pages.split(",");
			String[] v_description = page_description.split(",");
			for (int i = 0; i < v_page.length; i++) {
				html += "<li class=\"nav-item\"><a href=" + v_page[i] + " class=\"nav-link\"><i class=\"far fa-circle nav-icon\"></i><p style=\"color: #c2c7d0;\">" + v_description[i] + "</p></a></li>";
			}
			map.put("status", 1);
			map.put("data", html);
			map.put("message", Constants.ADMIN_LOGIN_SUCCESSFULLY);
			log.info("Login successfully");
		}else{
			map.put("status", 2);
			map.put("data", getRole);
			map.put("message", Constants.INVALID_CREDENTIAL);
			log.info("Invalid credential");
		}
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@GetMapping("/getUserBySts")
	public List<UserLoginHistoryModel> getAttemptCount(String login_sts){
		log.info("getUserBySts "+login_sts);
		return loginService.getAttemptCount(login_sts);
	}

	@GetMapping("getUserDetailsFromLookup")
	public LookupMstrModel getUserDetailsFromLookup(){
		log.info("getUserDetailsFromLookup");
		LookupMstrModel lk_obj=new LookupMstrModel();
		lk_obj=lookupService.getLookupValueByTypeWithCode("USER_TYPE", "USER_ROLE").get();
		lk_obj.getLookup_code();
		return lk_obj;
	}
}
