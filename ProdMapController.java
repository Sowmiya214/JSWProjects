package com.jsw.fe.transaction.Controller;

import com.jsw.fe.Utility.Constants;
import com.jsw.fe.Utility.UtilClass;
import com.jsw.fe.master.Entity.PartsList;
import com.jsw.fe.master.Entity.ProductMstrModel;
import com.jsw.fe.master.Service.PartsService;
import com.jsw.fe.master.businessmodel.InspectionListModel;
import com.jsw.fe.master.businessmodel.ViewProductMapping;
import com.jsw.fe.transaction.Entity.PartsStatusMstrModel;
import com.jsw.fe.transaction.Entity.ProdMapMstrModel;
import com.jsw.fe.transaction.Service.PartsStatusService;
import com.jsw.fe.transaction.Service.ProdMapService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.*;
@Slf4j
@RestController
@RequestMapping("MappingInfo")
public class ProdMapController {

	@Autowired
	private ProdMapService prodMapService;

	@Autowired
	private PartsService partsService;

	@Autowired
	private PartsStatusService partsStatusService;

	@Autowired
	private UtilClass utilClass;
	@PostMapping("/SaveOrUpdateProdMap")
	public ResponseEntity<?> saveProdMap(@RequestBody ProdMapMstrModel prodMap, HttpSession session) {
		log.info("SaveOrUpdateProdMap");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		try {
			if (prodMap.getFe_id() != null) {
				map.put("status", 2);
				map.put("data", prodMapService.updateProd(prodMap,utilClass.getUserId(session)));
				map.put("message", Constants.DATA_UPDATE_SUCCESSFULLY);
				log.info("Data Updated Successfully");
				return new ResponseEntity<>(map, HttpStatus.OK);
			} else {
				map.put("status", 1);
				map.put("data", prodMapService.saveprod(prodMap,utilClass.getUserId(session)));
				map.put("message",Constants.DATA_SAVE_SUCCESSFULLY);
				log.info("Data Save Successfully");
				return new ResponseEntity<>(map, HttpStatus.OK);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@GetMapping("/getPlanById")
	public ResponseEntity<?> getPlanById(@RequestParam("fe_id") Long fe_id) {
		log.info("fe_id: "+fe_id);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		List<ProdMapMstrModel> prod_lst = prodMapService.getPlanListById(fe_id);
		if (prod_lst.isEmpty()) {
			map.put("status", 1);
			map.put("data", prod_lst);
			map.put("message", Constants.GET_DATA_AVAILABLE);
			log.info("Data is Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			map.put("status", 1);
			map.put("data", prod_lst);
			map.put("message", Constants.DATA_NOT_FOUND);
			log.warn("Data is not Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
	}
	@PostMapping("/SaveMail")
	public ResponseEntity<?> SaveMail(@RequestParam("user_id") String user_id, @RequestParam("user_cmt") String user_cmt) {
		return null;
	}

	@PostMapping("/UpdateMap")
	public ResponseEntity<?> UpdateMap(@RequestBody ProdMapMstrModel prodMap) {
		log.info("UpdateMap");
		System.out.println("prodMap: "+prodMap.toString());
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (prodMap.getFe_id() != null) {
			map.put("status", 2);
			map.put("data", prodMapService.updateMap(prodMap));
			map.put("message", Constants.DATA_UPDATE_SUCCESSFULLY);
			log.info("Data Updated Successfully");
			return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			map.put("status", 0);
			map.put("message", HttpStatus.INTERNAL_SERVER_ERROR);
			log.error("Internal Server Error");
			return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getFeById")
	public Optional<ProdMapMstrModel> getFeById(@RequestParam("fe_id")Long fe_id) {
		log.info("getFeById "+fe_id);
		return prodMapService.getFEById(fe_id);
	}

	@GetMapping("/getDtlsByLoc")
	public Optional<ProdMapMstrModel> getDetailsByLoc(@RequestParam("loc_id")Long loc_id) {
		log.info("getDtlsByLoc "+loc_id);
		return prodMapService.getDetailsByLoc(loc_id);
	}
	@PostMapping("/deleteExtNameById")
	public ResponseEntity<?> deleteExtNameById(@RequestBody ProdMapMstrModel deleteProd,Long userId){
			log.info("deleteExtNameById");
		Map<String, Object> map=new LinkedHashMap<String,Object>();
		System.out.println("deleteProd: "+deleteProd.toString());
		try {
			if(deleteProd.getFe_id()!=null) {
				map.put("status", 1);
				map.put("data",prodMapService.deleteExtNameById(deleteProd,userId));
				map.put("message",Constants.DATA_DELETE_SUCCESSFULLY);
				log.info("Data Deleted Successfully");
			}else {
				map.put("status", 2);
				map.put("data",prodMapService.deleteExtNameById(deleteProd,userId));
				map.put("message",Constants.DATA_NOT_FOUND);
				log.warn("Data not found");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
	@GetMapping("/getViewList")
	public ResponseEntity<?> fetchProdMapList(){
		log.info("fetchProdMapList");
		Map<String, Object> map=new LinkedHashMap<String,Object>();
		List<ViewProductMapping> get_map_dtls=prodMapService.fetchViewList();
		List<InspectionListModel> ext = ModelToViewForm(get_map_dtls);
		if(!get_map_dtls.isEmpty()) {
			for(ViewProductMapping lst_obj:get_map_dtls){
				if(lst_obj.getParts_status().equalsIgnoreCase("false")){
					lst_obj.setParts_status("HealthyCondition");
				}else{
					lst_obj.setParts_status("AttentionRequired");
				}
			}
			map.put("status", 1);
			map.put("data",ext);
			map.put("message",Constants.GET_DATA_AVAILABLE);
			log.info("Data is Available");
		}else {
			map.put("status", 2);
			map.put("data",ext);
			map.put("message",Constants.DATA_NOT_FOUND);
			log.error("Data is not Available");
		}
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
	private List<InspectionListModel> ModelToViewForm(List<ViewProductMapping> get_map_dtls) {
		PartsList p = new PartsList();
		List<InspectionListModel> Inspectionlist = new ArrayList<>();
		for (ViewProductMapping prod : get_map_dtls) {
			String qrcode = "";
			String checkbox = "";
			InspectionListModel insp = new InspectionListModel();
			insp.setFe_id(prod.getFe_id());
			insp.setFe_ext_number(prod.getFe_ext_no());
			insp.setDepartment(prod.getDept_name());
			insp.setLocation(prod.getLoc_name());
			insp.setZone(prod.getZone_name());
			insp.setPlant(prod.getPlant_name());
			insp.setProduct(prod.getProduct_name());
			insp.setType(prod.getProd_type_name());
			insp.setProd_id(prod.getProduct_id());
			insp.setLstmodifydate(prod.getActual_cmpl_date());
			insp.setNxt_ins_date(prod.getNext_schd_date());
			qrcode += "<button type=\"button\" data-fe-ext=\"" + prod.getFe_ext_no() + "\" data-toggle=\"modal\" data-target=\"#modal-default\" class=\"btn btn-default QRCode_generator\">QRCode Visible</button>";
			checkbox += "<input class=\"switch-input \" data-fe-ext=\"" + prod.getFe_ext_no() + "\"  data-location=\""+prod.getLoc_name()+ "\" type=\"checkbox\" required />";
			insp.setQrcode(qrcode);
			insp.setCheckbox(checkbox);
			if (prod.getParts_status().equalsIgnoreCase("true")) {
				insp.setStatus("AttentionRequired");
			} else {
				insp.setStatus("HealthyCondition");
			}
			Inspectionlist.add(insp);
		}
		return Inspectionlist;
	}

	@GetMapping("/getList")
	public ResponseEntity<?> fetchProdMapList(
			@RequestParam(defaultValue = "0", name = "draw", required = false) int draw,
			@RequestParam(defaultValue = "0", name = "start", required = false) int start,
			@RequestParam(defaultValue = "10", name = "length", required = false) int length,
			@RequestParam(name = "search[value]", required = false) String searchValue
	) {
		Map<String, Object> map = new LinkedHashMap<>();
		System.out.println("start= " + start);
		System.out.println("length= " + length);

		// Calculate the page number based on the start index and length
		int pageNumber = start / length;

		Pageable paging = PageRequest.of(pageNumber, length);
		Page<ProdMapMstrModel> get_map_dtls = prodMapService.findAll(paging);
		System.out.println("get_map_dtls= " + get_map_dtls.toString());
		System.out.println("Size=" + get_map_dtls.getNumberOfElements());

		// Get the total number of records in your dataset
		long totalRecords = prodMapService.getTotalRecords();
		List<InspectionListModel> ext = ModelToForm(get_map_dtls);

		if (searchValue != null && !searchValue.isEmpty()) {
			List<InspectionListModel> filteredData = new ArrayList<>();
			for (InspectionListModel item : ext) {
				if (item.getFe_ext_number().contains(searchValue) ||
						item.getProduct().contains(searchValue) ||
						item.getLocation().contains(searchValue) ||
						item.getDepartment().contains(searchValue) ||
						item.getZone().contains(searchValue) ||
						item.getPlant().contains(searchValue) ||
						item.getStatus().contains(searchValue)) {
					filteredData.add(item);
				}
			}
			map.put("data", filteredData);

			map.put("recordsFiltered", filteredData.size());
		} else {
			map.put("data", ext);
			map.put("recordsFiltered", ext.size());
		}
		map.put("draw", draw); // Include the 'draw' parameter from DataTables in the response
		map.put("recordsTotal", totalRecords); // Total records in the dataset
		map.put("recordsFiltered", totalRecords); // Total records after filtering

		if (!get_map_dtls.isEmpty()) {
			map.put("status", 1);
			map.put("message", Constants.GET_DATA_AVAILABLE);
		} else {
			map.put("status", 2);
			map.put("message", Constants.DATA_NOT_FOUND);
		}
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	private List<InspectionListModel> ModelToForm(Page<ProdMapMstrModel> get_map_dtls) {
		PartsList p = new PartsList();
		List<InspectionListModel> Inspectionlist = new ArrayList<>();
		for (ProdMapMstrModel prod : get_map_dtls) {
			String qrcode = "";
			InspectionListModel insp = new InspectionListModel();
			insp.setFe_id(prod.getFe_id());
			insp.setDept_id(prod.getLocationDetails().getDeptDetails().getDept_id());
			insp.setFe_ext_number(prod.getFe_ext_no());
			insp.setDepartment(prod.getLocationDetails().getDeptDetails().getDept_name());
			insp.setLocation(prod.getLocationDetails().getLoc_name());
			insp.setZone(prod.getLocationDetails().getDeptDetails().getZoneDetails().getZone_name());
			insp.setPlant(prod.getLocationDetails().getDeptDetails().getZoneDetails().getPlantDetails().getPlant_name());
			insp.setProduct(prod.getProdDetails().getProduct_name());
			insp.setLstmodifydate(prod.getInspectionDetails().getActual_cmpl_date());
			insp.setNxt_ins_date(prod.getInspectionDetails().getNext_schd_date());
			qrcode += "<button type=\"button\" data-fe-ext=\"" + prod.getFe_ext_no() + "\" data-toggle=\"modal\" data-target=\"#modal-default\" class=\"btn btn-default QRCode_generator\">QRCode Visible</button>";
			insp.setQrcode(qrcode);
			if (prod.getParts_status().equalsIgnoreCase("true")) {
				insp.setStatus("AttentionRequired");
			} else {
				insp.setStatus("HealthyCondition");
			}
			Inspectionlist.add(insp);
		}
		return Inspectionlist;
	}

	@GetMapping("/getFeDetailById")
	public ResponseEntity<?> getProdMapByFe(@RequestParam (name="fe_ext_no",required = false) @Pattern(regexp = "^FE-[0-9]+[A-Z]*$") String fe_ext_no) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if(fe_ext_no==null){
			map.put("status", 2);
			map.put("data",null);
			map.put("message",Constants.DATA_NOT_FOUND);
			log.warn("Data is not Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
		if (fe_ext_no == null || !fe_ext_no.matches("^FE-[0-9]+[A-Z]*$")) {
			map.put("status", 4);
			map.put("message", Constants.STATUS);
			log.warn("Invalid fe_ext_no format");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}

			ProdMapMstrModel Fe_details = prodMapService.getProdByFeName(fe_ext_no);
			log.info("getFeDetailById: " + fe_ext_no);
				if (Fe_details != null) {
					List<PartsStatusMstrModel> part_status_lst = partsStatusService.getpartsListById(Fe_details.getFe_id());
					InspectionListModel ext = ModelToForm(Fe_details, part_status_lst);
//				 if (fe_ext_no != null && fe_ext_no.length() <= 8) {
//					System.out.println("length: "+fe_ext_no.length());
//					map.put("status", 4);
//					map.put("data", ext);
//					map.put("message", "Invalid fe_ext_no size. It must not be exactly 8 characters.");
//					log.warn("Invalid fe_ext_no size");
//					return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
//				}

					if (!Fe_details.getFe_ext_no().isEmpty()) {
						map.put("status", 1);
						map.put("data", ext);
						map.put("message", Constants.GET_DATA_AVAILABLE);
						log.info("Data is Available");
						return new ResponseEntity<>(map, HttpStatus.OK);
					} else {
						map.put("status", 2);
						map.put("data", ext);
						map.put("message", Constants.DATA_NOT_FOUND);
						log.warn("Data is not Available");
						return new ResponseEntity<>(map, HttpStatus.OK);
					}
				} else {
					map.put("status", 3);
					map.put("message", HttpStatus.INTERNAL_SERVER_ERROR);
					log.error("Internal Server Error");
					return new ResponseEntity<>(map, HttpStatus.OK);
				}
	}

	@GetMapping("/parts")
	public ProdMapMstrModel getparts(@RequestParam("fe_ext_no") String fe_ext_no){
		return prodMapService.getPartsByFeName(fe_ext_no);
	}

	@GetMapping("/getpartsDetailsByFe")
	public ResponseEntity<?> getPartsByFe(@RequestParam("fe_ext_no") @Pattern(regexp ="^FE-[0-9]+[A-Z]*$\"") String fe_ext_no, HttpServletRequest request) {
		String t= request.getHeader("userId");
		System.out.println("t: "+t);
		log.info("getpartsDetailsByFe: " + fe_ext_no);
		Map<String, Object> map = new LinkedHashMap<>();

		// Retrieve a list of ProdMapMstrModel records based on fe_ext_no
		ProdMapMstrModel partsList = prodMapService.getPartsByFeName(fe_ext_no);

		if (partsList.getFe_ext_no()== null) {
			map.put("status", 3);
			map.put("message", Constants.STATUS);
			log.warn("Invalid fe_ext_no format");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
			List<PartsStatusMstrModel> part_details_lst = partsStatusService.getpartsDetailsByFe(partsList.getFe_id());
			InspectionListModel ext_obj = ModelToForm1(partsList, part_details_lst);

		if (!partsList.getFe_ext_no().isEmpty()) {
			map.put("status", 1);
			map.put("data", ext_obj);
			map.put("message", Constants.GET_DATA_AVAILABLE);
			log.info("Data is Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}else{
			map.put("status", 2);
			map.put("message", Constants.DATA_NOT_FOUND);
			log.warn("Data is not Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
	}

	//	@GetMapping("/getpartsDetailsByFe")
//	public ResponseEntity<?> getPartsByFe(@RequestParam("fe_ext_no") @Pattern(regexp ="^FE-[0-9]+[A-Z]*$\"" )  String fe_ext_no) {
//		log.info("getpartsDetailsByFe: "+fe_ext_no);
//		Map<String, Object> map=new LinkedHashMap<String,Object>();
//		List<ProdMapMstrModel> Part_By_fe=prodMapService.getPartsByFeName(fe_ext_no);
//			try {
//			if(Part_By_fe==null){
//				map.put("status", 3);
//				map.put("message", Constants.STATUS);
//				log.warn("Invalid fe_ext_no format");
//				return new ResponseEntity<>(map, HttpStatus.OK);
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//			List<PartsStatusMstrModel> part_details_lst=partsStatusService.getpartsDetailsByFe();
//			List<InspectionListModel> inspectionList = new ArrayList<>();
//			InspectionListModel ext_obj=ModelToForm1(Part_By_fe,part_details_lst);
//		for (ProdMapMstrModel part : Part_By_fe) {
//			try {
//				if (Part_By_fe != null) {
//					map.put("status", 1);
//					map.put("data", ext_obj);
//					map.put("message", Constants.GET_DATA_AVAILABLE);
//					log.info("Data is Available");
//					return new ResponseEntity<>(map, HttpStatus.OK);
//				} else {
//					map.put("status", 2);
//					map.put("data", ext_obj);
//					map.put("message", Constants.DATA_NOT_FOUND);
//					log.warn("Data is not Available");
//					return new ResponseEntity<>(map, HttpStatus.OK);
//				}
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}
//		return new ResponseEntity<>(map, HttpStatus.OK);
//
//	}
	private InspectionListModel ModelToForm(ProdMapMstrModel fe_details,List<PartsStatusMstrModel> part_status_lst) {
		try {
			InspectionListModel insp=new InspectionListModel();
			insp.setFe_id(fe_details.getFe_id());
			insp.setDept_id(fe_details.getLocationDetails().getDeptDetails().getDept_id());
			insp.setFe_ext_number(fe_details.getProdDetails().getProdMapDetails().getFe_ext_no());
			insp.setProd_id(fe_details.getProdDetails().getProduct_id());
			insp.setProduct(fe_details.getProdDetails().getProduct_name());
			insp.setType(fe_details.getProdDetails().getProdTypeDetails().getProd_type_name());
			insp.setLocation(fe_details.getLocationDetails().getLoc_name());
			insp.setDepartment(fe_details.getLocationDetails().getDeptDetails().getDept_name());
			insp.setZone(fe_details.getLocationDetails().getDeptDetails().getZoneDetails().getZone_name());
			insp.setPlant(fe_details.getLocationDetails().getDeptDetails().getZoneDetails().getPlantDetails().getPlant_name());
			insp.setLstmodifydate(fe_details.getInspectionDetails().getActual_cmpl_date());
			insp.setNxt_ins_date(fe_details.getInspectionDetails().getNext_schd_date());
			List<PartsList> lst=new ArrayList<>();
			for(PartsStatusMstrModel sta : part_status_lst) {
				String html="",input="";
				PartsList p=new PartsList();
				p.setFe_id(sta.getFe_id());
				p.setPart_sts_id(sta.getParts_sts_id());
				p.setPart_id(sta.getParts_id());
				p.setParts_name(sta.getPartsMstrDetails().getParts_name());

				if (sta.getInspecSchDetails() != null) {
					if (sta.getInspecSchDetails().getActual_cmpl_date() != null) {
						p.setModifydate(sta.getInspecSchDetails().getActual_cmpl_date());
					} else {
						p.setModifydate(null);
					}
					if (sta.getInspecSchDetails().getNext_schd_date() != null) {
						p.setNxt_inspec_date(sta.getInspecSchDetails().getNext_schd_date());
					} else {
						p.setNxt_inspec_date(null);
					}
				} else {
					p.setModifydate(null);
					p.setNxt_inspec_date(null);
				}

				if(!sta.getParts_status().equalsIgnoreCase("false")) {
					html+="<label class=\"switch\"><input class=\"switch-input\"  data-sts-val=\""+sta.getParts_sts_id()+"\"  data-pt-val=\""+sta.getParts_id()+"\"  data-fe-val=\""+sta.getFe_id()+"\" data-fe_parts=\""+sta.getPartsMstrDetails().getParts_name()+"\" checked data-tgl-val=\""+sta.getParts_status()+"\" type=\"checkbox\" required /><span style=\"width: auto;\" class=\"switch-label\"  data-on=\"Yes\" data-off=\"No\"></span><span class=\"switch-handle\"></span></label>";
				}else {
					html+="<label class=\"switch\"><input class=\"switch-input\"  data-sts-val=\""+sta.getParts_sts_id()+"\"  data-pt-val=\""+sta.getParts_id()+"\"  data-fe-val=\""+sta.getFe_id()+"\" data-fe_parts=\""+sta.getPartsMstrDetails().getParts_name()+"\"  data-tgl-val=\""+sta.getParts_status()+"\" type=\"checkbox\"  required /><span style=\"width: auto;\" class=\"switch-label\"  data-on=\"Yes\" data-off=\"No\"></span><span class=\"switch-handle\"></span></label>";
				}
				p.setStatus(html);
				input += "<div class=\"col-md-4\"><div class=\"form-group\"><input style=\"background: transparent;width:auto;\" value=\"" + sta.getComments() + "\" id=\"list_cmd_name\" class=\"form-control\" data-initial-comment=\"" + sta.getComments() + "\"></input></div></div>";
				p.setComments(input);
				lst.add(p);
				//System.out.println("partsList= "+sta.getFe_id());
			}

			insp.setLst(lst);
			return insp;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private InspectionListModel ModelToForm1(ProdMapMstrModel Part_By_fe,List<PartsStatusMstrModel> part_details_lst) {
		InspectionListModel inspec=new InspectionListModel();
		inspec.setDept_id(Part_By_fe.getLocationDetails().getDeptDetails().getDept_id());
		inspec.setFe_id(Part_By_fe.getFe_id());
		inspec.setFe_ext_number(Part_By_fe.getProdDetails().getProdMapDetails().getFe_ext_no());
		inspec.setProduct(Part_By_fe.getProdDetails().getProduct_name());
		inspec.setType(Part_By_fe.getProdDetails().getProdTypeDetails().getProd_type_name());
		inspec.setLocation(Part_By_fe.getLocationDetails().getLoc_name());
		inspec.setDepartment(Part_By_fe.getLocationDetails().getDeptDetails().getDept_name());
		inspec.setZone(Part_By_fe.getLocationDetails().getDeptDetails().getZoneDetails().getZone_name());
		inspec.setPlant(Part_By_fe.getLocationDetails().getDeptDetails().getZoneDetails().getPlantDetails().getPlant_name());
		inspec.setLstmodifydate(Part_By_fe.getInspectionDetails().getActual_cmpl_date());
		inspec.setNxt_ins_date(Part_By_fe.getInspectionDetails().getNext_schd_date());
		List<PartsList> lst=new ArrayList<>();
		for(PartsStatusMstrModel sts : part_details_lst) {
			String html="",input="";
			PartsList pt=new PartsList();
			pt.setFe_id(sts.getFe_id());
			pt.setPart_sts_id(sts.getParts_sts_id());
			pt.setPart_id(sts.getParts_id());
			pt.setParts_name(sts.getPartsMstrDetails().getParts_name());

			if(sts.getInspecSchDetails()!=null){
				if(sts.getInspecSchDetails().getActual_cmpl_date()!=null){
					pt.setModifydate(sts.getInspecSchDetails().getActual_cmpl_date());
				}else{
					pt.setModifydate(null);
				}
				if(sts.getInspecSchDetails().getNext_schd_date()!=null){
					pt.setNxt_inspec_date(sts.getInspecSchDetails().getNext_schd_date());
				}else{
					pt.setNxt_inspec_date(null);
				}
			}else{
				pt.setModifydate(null);
				pt.setNxt_inspec_date(null);
			}

			if(!sts.getParts_status().equalsIgnoreCase("false")) {
				html+="<label class=\"switch\"><input class=\"switch-input\" data-sts-val=\""+sts.getParts_sts_id()+"\"  data-pt-val=\""+sts.getParts_id()+"\"  data-fe-val=\""+sts.getFe_id()+"\" checked  data-tgl-val=\""+sts.getParts_status()+"\" type=\"checkbox\" required /><span style=\"width: auto;\" class=\"switch-label\"  data-on=\"Yes\" data-off=\"No\"></span><span class=\"switch-handle\"></span></label>";	
			}else {
				html+="<label class=\"switch\"><input class=\"switch-input\" data-sts-val=\""+sts.getParts_sts_id()+"\"  data-pt-val=\""+sts.getParts_id()+"\"  data-fe-val=\""+sts.getFe_id()+"\"   data-tgl-val=\""+sts.getParts_status()+"\" type=\"checkbox\"  required /><span style=\"width: auto;\" class=\"switch-label\"  data-on=\"Yes\" data-off=\"No\"></span><span class=\"switch-handle\"></span></label>";
			}
			pt.setStatus(html);
			input += "<div class=\"col-md-4\"><div class=\"form-group\"><input style=\"background: transparent;width:auto;\" value=\"" + sts.getComments() + "\" id=\"list_cmd_name\" class=\"form-control\" data-initial-comment=\"" + sts.getComments() + "\"></input></div></div>";
			pt.setComments(input);
			lst.add(pt);
		}
		inspec.setLst(lst);
		return inspec;
	}
	
	@GetMapping("getPartsByProdId")
	public ResponseEntity<?> getPartsByProdId(@RequestParam("product_id")Long product_id){
		log.info("getPartsByProdId: "+product_id);
		Map<String, Object> map=new LinkedHashMap<String,Object>();
		ProductMstrModel get_parts_List=prodMapService.getPartsByProdId(product_id);
		if(get_parts_List.getProduct_id()!=null) {
			map.put("status", 1);
			map.put("data",get_parts_List);
			map.put("message",Constants.GET_DATA_AVAILABLE);
			log.info("Data is Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}else {
			map.put("status", 1);
			map.put("data",get_parts_List);
			map.put("message",Constants.DATA_NOT_FOUND);
			log.warn("Data is not Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
			}

	@GetMapping("/getListByExtNo")
	public ResponseEntity<?> getProdMapByExtNo(@RequestParam("fe_ext_no") String fe_ext_no) {
		log.info("getListByExtNo: "+fe_ext_no);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		List<ProdMapMstrModel> prod_Extno_List = prodMapService.getProdByExtNo(fe_ext_no);
		if (!prod_Extno_List.isEmpty()) {
			map.put("status", 1);
			map.put("data", prod_Extno_List);
			map.put("message", Constants.GET_DATA_AVAILABLE);
			log.info("Data is Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			map.put("status", 2);
			map.put("data", prod_Extno_List);
			map.put("message", Constants.DATA_NOT_FOUND);
			log.warn("Data is not Available");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}
	}
	@GetMapping("/getFeTotalCount")
	public int getFeTotalCount() {
		log.info("getFeTotalCount ");
		return prodMapService.getTotalCount();
	}
	@GetMapping("/getFeHlthycondition")
    public int getFeHlthycondition() {
		log.info("getFeHlthycondition");
		return prodMapService.getFeHlthycondition();
	}
	@GetMapping("/getFeAttenReqCondition")
	public int getFeAttenReqCondition() {
		log.info("getFeAttenReqCondition");
		return prodMapService.getFeAttenReqCondition();
		
	}

	@GetMapping("/getAllList")
	public List<ProdMapMstrModel> getAllList(){
		return prodMapService.findAllWithoutPagination();
	}
}
