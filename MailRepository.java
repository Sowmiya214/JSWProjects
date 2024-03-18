package com.jsw.fe.Mail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MailRepository extends JpaRepository<MailModal, Long>{

	@Query(value="select mm from MailModal mm where mm.fe_id=:fe_id")
	List<MailModal> getDetailsFromMstrMail(@Param("fe_id")Long fe_id);
	
	@Query(value="select mm from MailModal mm where mm.fe_ext_no=:fe_ext_no")
	MailModal getDetailsByName(@Param("fe_ext_no") String fe_ext_no);
	
}
