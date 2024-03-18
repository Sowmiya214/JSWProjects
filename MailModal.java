package com.jsw.fe.Mail;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name="admin_mstr_user_mail")
@TableGenerator(name = "ADMIN_USER_MAIL", table = "SEQ_GENERATOR", pkColumnName = "COL_KEY", valueColumnName = "NEXT_VAL", pkColumnValue = "USER_SEQ_MAIL", allocationSize = 1)
@DynamicUpdate
@Data
public class MailModal implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ADMIN_USER_MAIL")
	@Column(name = "user_mail_id", unique = true, nullable = false)
	private Long user_mail_id;
	private Long fe_id;
	private String fe_ext_no;
	private String user_name;
	private String mobile_no;
	private String dept_name;
	private String comments;
	private int created_by;	
	private Date created_date_time;
	private int updated_by;
	private Date updated_date_time;
	private int record_status;

	private String fe_comments;
}


	


