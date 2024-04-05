package com.jsw.ym.cube.Repository;

import java.util.List;
import java.util.Optional;

import com.jsw.ym.cube.Entity.CubeHdrModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CubeRepository extends JpaRepository<CubeHdrModel, Long> {

	@Query(value="select b from CubeHdrModel b where b.yard_id=:y_id and b.bay_id=:b_id and b.row_no=:r_no and  b.layer_no=:l_no and b.column_no in(:col) and b.batch_status=:b_status")
	List<CubeHdrModel> getBatchList(@Param("y_id")Long yard_id, @Param("b_id")Long bay_id, @Param("r_no")int row_no, @Param("l_no") int top_layer, @Param("col") List<Integer> col, @Param("b_status")String batch_status);
		
	@Query(value="select b from CubeHdrModel b where b.yard_id=:y_id and b.row_no=:r_no and b.layer_no=:l_no and b.column_no in(:newCol)")
	List<CubeHdrModel> getNewBatchList(@Param("y_id")Long yard_id, @Param("r_no")int row_no, @Param("l_no") int btm_layer, @Param("newCol") List<Integer> newCol);

	@Query(value="select b from CubeHdrModel b where b.plant_id=:plant_id and b.yard_id=:y_id and b.layer_no=:l_no and b.column_no in(:newCol) and b.batch_status=:b_status")
	List<CubeHdrModel> getAvailableBatchList(@Param("plant_id")Long plant_id, @Param("y_id")Long yard_id, @Param("l_no") int btm_layer, @Param("newCol") List<Integer> newCol, @Param("b_status")String batch_status);
	
	@Query(value="select b from CubeHdrModel b where b.yard_id=:y_id and b.bay_id=:b_id and b.row_no=:r_no and b.layer_no=:l_no and b.column_no =:newCol and b.batch_status=:b_status")
	Optional<CubeHdrModel> getVerifyPositionStatus(@Param("y_id")Long yard_id, @Param("b_id")Long bay_id, @Param("r_no")int row_no, @Param("l_no") int btm_layer, @Param("newCol") int newCol, @Param("b_status")String batch_status);

	@Query(value = "SELECT b.plant_id FROM CubeHdrModel b where b.record_status=:rec_status")
	Optional<CubeHdrModel> getLocationData(@Param("rec_status")int record_status);
			
//	@Query(value="select count(*) b from BatchHdrModel b where b.yard_id =:id")
//	Optional<BatchHdrModel> getYardList(@Param("id") Long yard_id);
		
//    @Query( value="select b from BatchHdrModel b where b.plant_id=:plant_id and  b.yard_id=:y_id and b.bay_id=:bay_id and b.row_no=:row_no and b.layer_no=:lay_no and b.column_no in(:avlCol) and b.batch_status=:b_status")
//	List<BatchHdrModel> getValidationForYard(@Param("plant_id") Long plant_id , @Param("y_id") Long yard_id ,@Param("bay_id") Long bay_id,@Param("row_no") int row_no, @Param("lay_no") int layer_no ,@Param("avlCol") List<Integer> avlCol,@Param("b_status") String batch_status);
    @Query( value="select b from CubeHdrModel b where b.plant_id=:plant_id and  b.yard_id=:y_id and b.bay_id=:bay_id and b.row_no=:row_no and b.layer_no=:lay_no")
	List<CubeHdrModel> getValidationForYard(@Param("plant_id") Long plant_id , @Param("y_id") Long yard_id , @Param("bay_id") Long bay_id, @Param("row_no") int row_no, @Param("lay_no") int layer_no);

  //  @Query(value="select b,d from BatchHdrModel b , BatchdetailsModel d where d.plant_id=:plant_id and d.yard_id=:y_id")
   // List<BatchdetailsModel> getBatchDetailsList(@Param("plant_id")long plant_id,@Param("y_id")long yard_id);
    
    @Query(value="select b from CubeHdrModel b where b.batch_name=:b_name")
    Optional<CubeHdrModel> getBatchNameList(@Param("b_name") String batch_name);
  
    @Query(value="select bay.bay_name from BayMstrModel bay INNER JOIN  CubeHdrModel b ON b.bay_id=bay.bay_id where b.batch_name=:b_name")
    Optional<CubeHdrModel> getBayNameByBatchName(@Param("b_name") String batch_name);
    
    @Query(value="select p.plant_name from PlantMstrModel p INNER JOIN  CubeHdrModel b ON b.plant_id=p.plant_id where b.batch_name=:b_name")
    Optional<CubeHdrModel> getPlantNameByBatchName(@Param("b_name") String batch_name);
    
    @Query(value="select y.yard_name from YardMstrModel y INNER JOIN  CubeHdrModel b ON b.yard_id=y.yard_id where b.batch_name=:b_name")
    Optional<CubeHdrModel> getYardNameByBatchName(@Param("b_name") String batch_name);

    @Query(value="select b from CubeHdrModel b where b.plant_id=:plant_id and b.yard_id=:y_id and b.layer_no=:lay_no and b.batch_status=:b_status" )
	List<CubeHdrModel> getAvailableLayerByYard(@Param("plant_id") Long plant_id, @Param("y_id") Long yard_id, @Param("lay_no") int l_no, @Param("b_status") String batch_status);
     
    @Query(value="select b from CubeHdrModel b where b.plant_id=:plant_id and b.yard_id=:y_id and b.layer_no=:lay_no and b.batch_status=:b_status" )
	List<CubeHdrModel> getAvailableColumnByLayer(@Param("plant_id") Long plant_id, @Param("y_id") Long yard_id, @Param("lay_no") Integer l_no, @Param("b_status") String batch_status);
        
    @Query(value="select b from CubeHdrModel b where b.yard_id=:y_id and b.batch_status=:b_status")
	List<CubeHdrModel> fetchBatchListByYard(@Param("y_id") Long yard_id, @Param("b_status") String batch_status);
    
    @Query(value="select b from CubeHdrModel b where b.batch_name=:batch_name and b.batch_status=:b_status" )
    Optional<CubeHdrModel> getBatchByName(@Param("batch_name") String batch_name, @Param("b_status") String batch_status);
    
    @Query( value="select b from CubeHdrModel b where b.plant_id=:plant_id and  b.yard_id=:y_id and b.bay_id=:bay_id and b.batch_status=:b_status")
	List<CubeHdrModel> getBatchListByBay(@Param("plant_id") Long plant_id, @Param("y_id") Long yard_id, @Param("bay_id") Long bay_id, @Param("b_status") String b_status);
    	}

