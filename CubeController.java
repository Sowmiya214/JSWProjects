package com.jsw.ym.cube.Controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.jsw.ym.cube.Entity.CubeHdrModel;
import com.jsw.ym.cube.Service.CubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jsw.ym.utility.Constants;

@RestController
@RequestMapping("CubeInfo")
public class CubeController {
	@Autowired
	private CubeService cubeService;

	@PostMapping("/saveOrMoveBatch")
	public ResponseEntity<?> saveOrMoveBatch(@RequestBody CubeHdrModel cubeHdrModel) {
		Map<String, Object> map = new LinkedHashMap<>();
		try {
			// Check if the batch position indicates a side view layer
			if (isSideViewLayer(cubeHdrModel.getLayer_no())) {
				// Modify the batch position to save it in the front view instead of the side view
				cubeHdrModel.setLayer_no(modifyLayerNumber(cubeHdrModel.getLayer_no()));
			}
			// Proceed with saving or moving the batch
			Optional<CubeHdrModel> validateBatch = cubeService.getBatchName(cubeHdrModel.getBatch_name().replaceAll("\\s", ""));
			if (!validateBatch.isPresent()) {
				Optional<CubeHdrModel> existingBatch = cubeService.getVerifyPositionStatus(cubeHdrModel);
				if (!existingBatch.isPresent()) {
					cubeHdrModel.setBatch_name(cubeHdrModel.getBatch_name().replaceAll("\\s", ""));
					CubeHdrModel savedBatch = cubeService.saveBatch(cubeHdrModel);
					map.put("status", 1);
					map.put("data", savedBatch);
					map.put("message", Constants.BATCH_SAVE_SUCCESSFULLY);
					return new ResponseEntity<>(map, HttpStatus.OK);
				} else {
					CubeHdrModel updatedBatch = cubeService.updateBatch(cubeHdrModel);
					map.put("status", 2);
					map.put("data", updatedBatch);
					map.put("message", Constants.BATCH_UPDATE_SUCCESSFULLY);
					return new ResponseEntity<>(map, HttpStatus.OK);
				}
			}
		} catch (Exception ex) {
			System.out.println(ex);
			map.clear();
			map.put("status", 0);
			map.put("message", HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	// Method to check if the batch position indicates a side view layer
	private boolean isSideViewLayer(int layerNo) {
		System.out.println("");

		return layerNo % 2 == 0;
	}

	// Method to modify the layer number to save the batch in the front view instead of the side view
	private int modifyLayerNumber(int layerNo) {
		// Add your logic here to modify the layer number
		// For example, you might subtract 1 from the layer number to save it in the front view
		return layerNo - 1; // Example: Adjust layer number to save in the front view
	}



//	@PostMapping("/saveOrMoveBatch")
//	public ResponseEntity<?> saveOrMoveBatch(@RequestBody BatchHdrModel batchHdrModel) {
//		Map<String, Object> map = new LinkedHashMap<String, Object>();
//		try {
//			Optional<BatchHdrModel> validate_Batch = batchService.getBatchName(batchHdrModel.getBatch_name().replaceAll("\\s", ""));
//			if (!validate_Batch.isPresent()) {
//				Optional<BatchHdrModel> Loc_batch = batchService.getVerifyPositionStatus(batchHdrModel);
//				if (!Loc_batch.isPresent()) {
//					batchHdrModel.setBatch_name(batchHdrModel.getBatch_name().replaceAll("\\s", ""));
//					BatchHdrModel batch = batchService.saveBatch(batchHdrModel);
//					map.put("status", 1);
//					map.put("data", batch);
//					map.put("message", Constants.BATCH_SAVE_SUCCESSFULLY);
//					return new ResponseEntity<>(map, HttpStatus.OK);
//				} else {
//					map.put("status", 5);
//					map.put("data", Loc_batch);
//					map.put("message", Constants.BATCH_ALREADY_EXIST);
//					return new ResponseEntity<>(map, HttpStatus.OK);
//				}
//			} else {
//				if (validate_Batch.get().getBatch_status().equalsIgnoreCase(Constants.BATCH_STATUS_DISPATCH)) {
//					map.put("status", 3);
//					map.put("data", validate_Batch);
//					map.put("message", Constants.BATCH_ALREADY_DISPATCH);
//					return new ResponseEntity<>(map, HttpStatus.OK);
//				} else {
//					List<BatchHdrModel> batch_obj = batchService
//							.validateForBatchPick(validate_Batch.get().getBatch_name());
//
//					if (batch_obj.isEmpty()) {
//						List<BatchHdrModel> lst = batchService.getValidationForSave(batchHdrModel);
//						for (BatchHdrModel ob : lst) {
//							if (batchHdrModel.getBatch_name().equalsIgnoreCase(ob.getBatch_name())) {
//								map.put("status", 6);
//								map.put("data", ob);
//								map.put("message", Constants.BATCH_SAVE_VALIDATION_FAILD);
//								return new ResponseEntity<>(map, HttpStatus.OK);
//							}
//						}
//						BatchHdrModel batchUpdate = batchService.updateBatch(batchHdrModel);
//						map.put("status", 2);
//						map.put("data", batchUpdate);
//						map.put("message", Constants.BATCH_UPDATE_SUCCESSFULLY);
//						return new ResponseEntity<>(map, HttpStatus.OK);
//					} else {
//						map.put("status", 4);
//						map.put("data", batch_obj);
//						map.put("message", Constants.BATCH_NOT_READY_TO_PICK);
//						return new ResponseEntity<>(map, HttpStatus.OK);
//					}
//
//				}
//
//			}
//
//		} catch (Exception ex) {
//			System.out.println(ex);
//			map.clear();
//			map.put("status", 0);
//			map.put("message", HttpStatus.INTERNAL_SERVER_ERROR);
//			return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}



	@GetMapping("/getBatchAllListByYardHTML")
	public ResponseEntity<?> getBatchAllListByYard() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String batch_lst = "";

		batch_lst = cubeService.fetchBatchAllListByYardHTMLFst();

		if (!batch_lst.isEmpty()) {
			map.put("status", 1);
			map.put("data", batch_lst);
			map.put("message", Constants.BATCH_RETRIEVE_SUCCESSFULLY);
			return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			map.clear();
			map.put("status", 0);
			map.put("message", Constants.DATA_NOT_FOUND);
			return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
		}

	}
	@GetMapping("/fetchBatchAllListByYardHTMLRound")
	public ResponseEntity<?> fetchBatchAllListByYardHTMLRound() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String batch_lst = "";

		batch_lst = cubeService.fetchBatchAllListByYardHTMLRound();

		if (!batch_lst.isEmpty()) {
			map.put("status", 1);
			map.put("data", batch_lst);
			map.put("message", Constants.BATCH_RETRIEVE_SUCCESSFULLY);
			return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			map.clear();
			map.put("status", 0);
			map.put("message", Constants.DATA_NOT_FOUND);
			return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
		}

	}
	@GetMapping("/fetchBatchAllListByYardHTMLRoundRect")
	public ResponseEntity<?> fetchBatchAllListByYardHTMLRoundRect(@RequestParam("bay_id") Long bay_id,@RequestParam("row_no") int row_no, @RequestParam("shapes") String shapes) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String batch_lst = "";
		if (shapes == "RoundRect") {
			batch_lst = cubeService.fetchBatchAllListByYardHTMLRoundRect(bay_id,row_no,Constants.BATCH_STATUS_AVAILABLE);
		}

		if (!batch_lst.isEmpty()) {
			map.put("status", 1);
			map.put("data", batch_lst);
			map.put("message", Constants.BATCH_RETRIEVE_SUCCESSFULLY);
			return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			map.clear();
			map.put("status", 0);
			map.put("message", Constants.DATA_NOT_FOUND);
			return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
		}

	}
}