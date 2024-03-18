package com.jsw.fe.transaction.Service;

import com.jsw.fe.master.Entity.ProductMstrModel;
import com.jsw.fe.master.businessmodel.InspectionListModel;
import com.jsw.fe.master.businessmodel.ViewProductMapping;
import com.jsw.fe.transaction.Entity.PartsStatusMstrModel;
import com.jsw.fe.transaction.Entity.ProdMapMstrModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProdMapService {

	ProdMapMstrModel saveprod(ProdMapMstrModel prodMap, Long userId);
	
	ProdMapMstrModel saveParts(ProdMapMstrModel mstrModel);
	
	ProdMapMstrModel updateParts(ProdMapMstrModel mstrParts);
	
	Optional<ProdMapMstrModel> getFEById(Long fe_id);
	
	List<ProdMapMstrModel>  getPlanListById(Long fe_id);
	
	List<ProdMapMstrModel>  getFeActualListByMonthAndYear(String parts_sts);
	
	ProdMapMstrModel updateProd(ProdMapMstrModel prodMap, Long userId);

	List<ProdMapMstrModel> fetchProdMappingList();

	List<ViewProductMapping> fetchViewList();

	List<ViewProductMapping> getHealthyCondition();

	List<ViewProductMapping> getAttentionRequired();


	Page<ProdMapMstrModel> findAll(Pageable pageable);

	Page<ProdMapMstrModel> findAllBySort(Pageable pageable);

	Page<ProdMapMstrModel> findAllByList(Integer pageNo, Integer pageSize, String sortBy);


	Page<ProdMapMstrModel> fetchProdMappingList(int start, int length, String searchValue, String orderColumnName, String orderDir);
	Page<ProdMapMstrModel> fetchProdMappingList(int page, int pageSize, String orderColumnName, String orderDir);
//	Page<ProdMapMstrModel> fetchProdMappingList(int start, int length, String searchValue, String orderColumnName, String orderDir);

	//List<ProdMapMstrModel> getPlanListByMonthAndYear(ProdMapMstrModel mstrModel);
	
	ProductMstrModel getPartsByProdId(Long product_id);

	ProdMapMstrModel getProdByFeName(String fe_ext_no);
	ProdMapMstrModel getPartsByFeName(String fe_ext_no);
	Optional<ProdMapMstrModel> getDetailsByLoc(Long loc_id);
	
	//List<ProdMapMstrModel> getProdById(Long plant_id,Long zone_id,Long dept_id,Long loc_id);
	
	List<ProdMapMstrModel> getProdByExtNo(String fe_ext_no);
	
	List<PartsStatusMstrModel> getpartsListById(Long fe_id);

	ProdMapMstrModel updateMap(ProdMapMstrModel prodMap);

	ProdMapMstrModel deleteExtNameById(ProdMapMstrModel deleteProd,Long userId);
	
	int getTotalCount();
	
	int getFeHlthycondition();
	
	int getFeAttenReqCondition();

	long getTotalRecords();

	List<ProdMapMstrModel> findAllWithoutPagination();

}
