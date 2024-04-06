package com.jsw.ym.cube.Service;

import java.util.List;
import java.util.Optional;

import com.jsw.ym.cube.Entity.CubeHdrModel;
import com.jsw.ym.master.Entity.BayMstrModel;

public interface CubeService {

	CubeHdrModel saveBatch(CubeHdrModel CubeHdrModel);

	Optional<CubeHdrModel> getBayById(Long bay_id);

	CubeHdrModel updateBatch(CubeHdrModel CubeHdrModel);
	int[] getFilledCoilCount(Long bay_id, int i);
    List<CubeHdrModel> validateForBatchPick(String batch_name);


	List<CubeHdrModel> validateForBatchMove(CubeHdrModel CubeHdrModel);

	Optional<CubeHdrModel> getVerifyPositionStatus(CubeHdrModel cubeHdrModel);

	String fetchBatchAllListByYardHTMLRound();

	List<CubeHdrModel> getValidationForSave(CubeHdrModel cubeHdrModel);

	String getBatchListHTML(String shape, Long bay_id, int row_no, String batch_status);

	String fetchBatchAllListByYardHTMLFst();
	String fetchBatchAllListByYardHTMLFst(Long bay_id,int row_no, String batch_status);

	String fetchBatchAllListByYardHTMLScnd(Long bay_id,int row_no, String batch_status);

	String fetchBatchAllListByYardHTMLRoundRect();

	Optional<CubeHdrModel> getBatchName(String replaceAll);
}
