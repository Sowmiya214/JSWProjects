package com.jsw.fe.transaction.Service;

import com.jsw.fe.Inspection.InspectionMstrModal;
import com.jsw.fe.Inspection.InspectionRepository;
import com.jsw.fe.master.Entity.LocationMstrModel;
import com.jsw.fe.master.Entity.PartsMappingMstrModel;
import com.jsw.fe.master.Entity.ProductMstrModel;
import com.jsw.fe.master.Repository.LocationRepository;
import com.jsw.fe.master.Repository.PartsMappingRepository;
import com.jsw.fe.master.Repository.ProductRepository;
import com.jsw.fe.master.Repository.ViewProductMappingRepository;
import com.jsw.fe.master.businessmodel.InspectionListModel;
import com.jsw.fe.master.businessmodel.ViewProductMapping;
import com.jsw.fe.transaction.Entity.PartsStatusMstrModel;
import com.jsw.fe.transaction.Entity.ProdMapMstrModel;
import com.jsw.fe.transaction.Repository.OverAllHistoryRepository;
import com.jsw.fe.transaction.Repository.PartsStatusRepository;
import com.jsw.fe.transaction.Repository.ProdMapRepository;
import com.jsw.fe.transaction.Repository.ProdReportMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProdMapServiceImple implements ProdMapService {
	@Autowired
	private ProdMapRepository prodMapRepository;

	@Autowired
	private OverAllHistoryRepository overAllHistoryRepository;

	@Autowired
	private ViewProductMappingRepository viewProductMappingRepository;

	@Autowired
	private ProdReportMapRepository prodReportMapRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PartsStatusRepository partsStatusRepository;

	@Autowired
	private InspectionRepository inspectionRepository;

	@Autowired
	private PartsMappingRepository partsMappingRepository;

	@Autowired
	private LocationRepository locationRepository;
	@Override
	public ProdMapMstrModel saveprod(ProdMapMstrModel prodMap, Long userId) {
	    Optional<ProductMstrModel> prods_map = productRepository.findByProdId(prodMap.getProdDetails().getProduct_id());

		prodMap.setFe_id(null);
		prodMap.setCreated_date_time(LocalDate.now());
		//prodMap.setCreated_by(userId.intValue());
		prodMap.setRecord_status(1);
		prodMap.setParts_status("false");
		prodMap.setProdDetails(prods_map.get());

		prodMap = prodMapRepository.save(prodMap);
		List<PartsMappingMstrModel> parts_details = prodMap.getProdDetails().getProdTypeDetails().getPartsMappingDetails();

		for(PartsMappingMstrModel p: parts_details){
					PartsStatusMstrModel partsStatus_dtls = new PartsStatusMstrModel();
					partsStatus_dtls.setFe_id(prodMap.getFe_id());
					partsStatus_dtls.setProduct_id(prodMap.getProdDetails().getProduct_id());
					partsStatus_dtls.setProd_type_id(prodMap.getProdDetails().getProd_type_id());
					partsStatus_dtls.setParts_status("false");
					partsStatus_dtls.setComments("");
					var new_parts_map_id = p.getParts_map_id();
					var new_parts_id = p.getParts_id();
					partsStatus_dtls.setParts_id(new_parts_id);
					partsStatus_dtls.setParts_map_id(new_parts_map_id);
					partsStatus_dtls.setCreated_date_time(new Date());

					prodMap.setParts_status(partsStatus_dtls.getParts_status());
					partsStatusRepository.save(partsStatus_dtls);
		}

		InspectionMstrModal inspec = new InspectionMstrModal();
		inspec.setFe_id(prodMap.getFe_id());
		int insp_sch = prodMap.getProdDetails().getProdTypeDetails().getInspection_schd();
		inspec.setInspec_sys_schd_date(prodMap.getCreated_date_time().plusMonths(insp_sch));
		inspec.setTask_status("Pending");
		inspec.setRecord_status(1);
		inspec.setCreated_date_time(new Date());
		inspectionRepository.save(inspec);
		return prodMap;
	}
	@Override
	public ProdMapMstrModel updateMap(ProdMapMstrModel prodMap) {
		System.out.println("update prodMap: "+prodMap.toString());
		Optional<ProdMapMstrModel> prod_obj = prodMapRepository.findById(prodMap.getFe_id());
		System.out.println(prod_obj.get().getFe_ext_no());
		try {
			Optional<ProductMstrModel> prods_map = productRepository.findByProdId(prodMap.getProdDetails().getProduct_id());
			//Optional<LocationMstrModel> loc_map = locationRepository.getLocById(prodMap.getLocationDetails().getLoc_id());
			if(prod_obj.isPresent()) {
				System.out.println(prod_obj.get().getProdDetails().getProduct_id());
				prod_obj.get().setProdDetails(prods_map.get());
				System.out.println("8888888888888" +prod_obj.get().getProdDetails().getProduct_name());
				prod_obj.get().setLoc_id(prodMap.getLoc_id());

				List<PartsMappingMstrModel> parts_dtls = prods_map.get().getProdTypeDetails().getPartsMappingDetails();
				for (var i = 0; i < parts_dtls.size(); i++) {
					PartsStatusMstrModel partsStatus_dtls = new PartsStatusMstrModel();
					partsStatus_dtls.setFe_id(prodMap.getFe_id());
					partsStatus_dtls.setProduct_id(prods_map.get().getProduct_id());
					partsStatus_dtls.setProd_type_id(prods_map.get().getProdTypeDetails().getProd_type_id());
					partsStatus_dtls.setParts_status("false");
					partsStatus_dtls.setComments("");
					var new_parts_map_id = parts_dtls.get(i).getParts_map_id();
					var new_parts_id = parts_dtls.get(i).getParts_id();
					partsStatus_dtls.setParts_id(new_parts_id);
					partsStatus_dtls.setParts_map_id(new_parts_map_id);
					partsStatus_dtls.setCreated_date_time(new Date());
					prod_obj.get().setParts_status(partsStatus_dtls.getParts_status());
					partsStatusRepository.save(partsStatus_dtls);
				}
				prodMapRepository.save(prod_obj.get());

			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		System.out.println("after: "+prodMap);
		return prod_obj.get();
	}

	@Override
	public long getTotalRecords() {
		return prodMapRepository.count();
	}

	@Override
	public List<ProdMapMstrModel> findAllWithoutPagination() {
		return prodMapRepository.findAll();
	}
	@Override
	public List<ProdMapMstrModel> fetchProdMappingList() {
		return prodMapRepository.findAll();
	}

	@Override
	public List<ViewProductMapping> fetchViewList() {
		return viewProductMappingRepository.getDataByView();
	}

	@Override
	public List<ViewProductMapping> getHealthyCondition() {
		return viewProductMappingRepository.getHealthyCondition();
	}
	@Override
	public List<ViewProductMapping> getAttentionRequired() {
		return viewProductMappingRepository.getAttentionRequired();
	}
	@Override
	public Page<ProdMapMstrModel> findAll(Pageable pageable) {
		return prodMapRepository.findAll(pageable);
	}

	@Override
	public Page<ProdMapMstrModel> findAllBySort(Pageable pageable) {
		return prodReportMapRepository.findAll(pageable);
	}

	@Override
	public Page<ProdMapMstrModel> findAllByList(Integer pageNo, Integer pageSize, String sortBy) {
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("fe_id"));

		Page<ProdMapMstrModel> pagedResult = prodMapRepository.findAll(paging);
		return pagedResult;
	}

	@Override
	public Page<ProdMapMstrModel> fetchProdMappingList(int start, int length, String searchValue, String orderColumnName, String orderDir) {
		return null;
	}


	@Override
	public Page<ProdMapMstrModel> fetchProdMappingList(int page, int pageSize, String orderColumnName, String orderDir) {
		return null;
	}

	@Override
	public List<ProdMapMstrModel> getProdByExtNo(String fe_ext_no) {
		// TODO Auto-generated method stub
		return prodMapRepository.getListByExt(fe_ext_no);
	}

	@Override
	public ProdMapMstrModel getProdByFeName(String fe_ext_no) {
		// TODO Auto-generated method stub
		return prodMapRepository.getfeDetailsByName(fe_ext_no);
	}
	@Override
	public Optional<ProdMapMstrModel> getFEById(Long fe_id) {
		// TODO Auto-generated method stub
		return prodMapRepository.findById(fe_id);
	}

	@Override
	public Optional<ProdMapMstrModel> getDetailsByLoc(Long loc_id) {
		// TODO Auto-generated method stub
		return prodMapRepository.findById(loc_id);
	}

	@Override
	public ProductMstrModel getPartsByProdId(Long product_id) {
		// TODO Auto-generated method stub
		return prodMapRepository.getPartsDetails(product_id);
	}

	@Override
	public List<PartsStatusMstrModel> getpartsListById(Long fe_id) {
		// TODO Auto-generated method stub
		return prodMapRepository.getPartsStatusById(fe_id);
	}

	@Override
	public ProdMapMstrModel saveParts(ProdMapMstrModel mstrModel) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ProdMapMstrModel updateParts(ProdMapMstrModel mstrParts) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ProdMapMstrModel updateProd(ProdMapMstrModel prodMap, Long userId) {
		// TODO Auto-generated method stub
		return null;
	}
	public Page<ProdMapMstrModel> fetchProdMappingList(int page, int pageSize) {
		PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.Direction.ASC, "fe_id");
		return prodMapRepository.findAll(pageRequest);
	}
	@Override
	public ProdMapMstrModel deleteExtNameById(ProdMapMstrModel deleteProd,Long userId) {
		try {
			Optional<ProdMapMstrModel> delete_prod = prodMapRepository.findById(deleteProd.getFe_id());
			if(delete_prod.isPresent()){
				delete_prod.get().setRecord_status(0);
				delete_prod.get().setLoc_id(delete_prod.get().getLoc_id());
				delete_prod.get().setProdDetails(delete_prod.get().getProdDetails());
				delete_prod.get().setParts_status(delete_prod.get().getParts_status());
				//delete_prod.get().setUpdated_by(userId.intValue());
				delete_prod.get().setUpdated_date_time(new java.sql.Date(new Date().getTime()));
			}
			//prodMapRepository.save(deleteProd);
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public ProdMapMstrModel getPartsByFeName(String fe_ext_no) {
		// TODO Auto-generated method stub
		return prodMapRepository.getfeDetailsByName(fe_ext_no);
	}
	@Override
	public int getTotalCount() {
		// TODO Auto-generated method stub
		return prodMapRepository.getFeTotalCount();
	}

	@Override
	public int getFeHlthycondition() {
		// TODO Auto-generated method stub
		return prodMapRepository.getFeHlthycondition();
	}

	@Override
	public int getFeAttenReqCondition() {
		// TODO Auto-generated method stub
		return prodMapRepository.getFeAttenReqCondition();
	}

	@Override
	public List<ProdMapMstrModel> getPlanListById(Long fe_id) {
		// TODO Auto-generated method stub
		return prodMapRepository.getPlanListById(fe_id);
	}

	@Override
	public List<ProdMapMstrModel> getFeActualListByMonthAndYear(String parts_sts) {
		// TODO Auto-generated method stub
		return prodMapRepository.getFeActualListByMonthAndYear(parts_sts);
	}

	private class YourCustomException extends Throwable {
		public YourCustomException(String error_fetching_data, Exception e) {
		}
	}
}

