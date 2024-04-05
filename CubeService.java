package com.jsw.ym.cube.Service;

import java.util.List;
import java.util.Optional;

import com.jsw.ym.cube.Entity.CubeHdrModel;

public interface CubeService {

	CubeHdrModel saveBatch(CubeHdrModel CubeHdrModel);


	CubeHdrModel updateBatch(CubeHdrModel CubeHdrModel);

    List<CubeHdrModel> validateForBatchPick(String batch_name);


	List<CubeHdrModel> validateForBatchMove(CubeHdrModel CubeHdrModel);

	Optional<CubeHdrModel> getVerifyPositionStatus(CubeHdrModel cubeHdrModel);

	String fetchBatchAllListByYardHTMLRound();

	List<CubeHdrModel> getValidationForSave(CubeHdrModel cubeHdrModel);

	String getBatchListHTML(String shape, Long bay_id, int row_no, String batch_status);

	String fetchBatchAllListByYardHTMLFst();

	String fetchBatchAllListByYardHTMLRoundRect(Long bay_id, int row_no, String batch_status);

	Optional<CubeHdrModel> getBatchName(String replaceAll);
}
