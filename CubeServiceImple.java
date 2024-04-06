package com.jsw.ym.cube.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsw.ym.cube.Entity.CubeHdrModel;
import com.jsw.ym.cube.Repository.CubeRepository;
import com.jsw.ym.transaction.Entity.BatchHdrModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.jsw.ym.master.Entity.BayMstrModel;
import com.jsw.ym.master.Service.BayService;
import com.jsw.ym.transaction.Entity.INTFBatchDetails;
import com.jsw.ym.utility.Constants;

@Service
public class CubeServiceImple implements CubeService {
	@Autowired
	private CubeRepository cubeRepository;

	@Autowired
	private BayService bayService;

	private boolean isSideViewLayer(int layerNo) {
		// Implement your logic here to identify side view layers
		// For example, if side view layers are even-numbered, you can use:
		return layerNo % 2 == 0;
	}

	// Method to generate batch position for the side view
	private String generateBatchPositionForSideView(CubeHdrModel cubeHdrModel) {
		System.out.println("Side_View_" + cubeHdrModel.getRow_no() + "_" + cubeHdrModel.getLayer_no() + "_" + cubeHdrModel.getColumn_no());
		return "Side_View_" + cubeHdrModel.getRow_no() + "_" + cubeHdrModel.getLayer_no() + "_" + cubeHdrModel.getColumn_no();
	}

	// Method to generate batch position for the front view
	private String generateBatchPositionForFrontView(CubeHdrModel cubeHdrModel) {
		System.out.println("Front_View_" + cubeHdrModel.getRow_no() + "_" + cubeHdrModel.getLayer_no() + "_" + cubeHdrModel.getColumn_no());
		return "Front_View_" + cubeHdrModel.getRow_no() + "_" + cubeHdrModel.getLayer_no() + "_" + cubeHdrModel.getColumn_no();
	}

	public class CustomException extends RuntimeException {
		public CustomException(String message, HttpStatus internalServerError) {
			super(message);
		}
	}


	@Override
	public CubeHdrModel saveBatch(CubeHdrModel cubeHdrModel) {
		Optional<BayMstrModel> mstr_bay = bayService.getBayById(cubeHdrModel.getBay_id());
		CubeHdrModel Loc_batch = new CubeHdrModel();

		if (mstr_bay.isPresent() && cubeHdrModel.getRow_no() <= mstr_bay.get().getRow_no()
				&& cubeHdrModel.getLayer_no() <= mstr_bay.get().getLayer_no()
				&& cubeHdrModel.getColumn_no() <= mstr_bay.get().getColumn_no()) {
			cubeHdrModel.setBatch_id(null);
			cubeHdrModel.setRecord_status(1);
			cubeHdrModel.setUpdated_date_time(null);
			cubeHdrModel.setBatch_status(Constants.BATCH_STATUS_AVAILABLE);

			// Determine whether to generate batch position for front view or side view
			if (isSideViewLayer(cubeHdrModel.getLayer_no())) {
				// Generate batch position for side view
				cubeHdrModel.setBatch_position(generateBatchPositionForSideView(cubeHdrModel));
			} else {
				// Generate batch position for front view
				cubeHdrModel.setBatch_position(generateBatchPositionForFrontView(cubeHdrModel));
			}

			try {
				Loc_batch = cubeRepository.save(cubeHdrModel);
				// Add code here to save batch details if needed
			} catch (Exception e) {
				// Handle database save error
				e.printStackTrace(); // Log the exception for debugging
				throw new CustomException("Failed to save batch", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			// Handle invalid batch parameters or missing bay information
			throw new CustomException("Invalid batch parameters or bay information", HttpStatus.BAD_REQUEST);
		}

		return Loc_batch;
	}



	//	@Override
//	public CubeHdrModel saveBatch(CubeHdrModel cubeHdrModel) {
//		Optional<BayMstrModel> mstr_bay = bayService.getBayById(cubeHdrModel.getBay_id());
//		CubeHdrModel Loc_batch = new CubeHdrModel();
//
//		if (cubeHdrModel.getRow_no() <= mstr_bay.get().getRow_no()
//				&& cubeHdrModel.getLayer_no() <= mstr_bay.get().getLayer_no()
//				&& cubeHdrModel.getColumn_no() <= mstr_bay.get().getColumn_no()) {
//			cubeHdrModel.setBatch_id(null);
//			cubeHdrModel.setRecord_status(1);
//			cubeHdrModel.setUpdated_date_time(null);
//			cubeHdrModel.setBatch_status(Constants.BATCH_STATUS_AVAILABLE);
//			cubeHdrModel.setBatch_position(generateBatchPosition(cubeHdrModel));
//
//			Loc_batch = cubeRepository.save(cubeHdrModel);
//
//			BatchdetailsModel Loc_batchdetails = new BatchdetailsModel();
//			Loc_batchdetails.setBatchdetails_id(null);
//			Loc_batchdetails.setBatch_id(Loc_batch.getBatch_id());
//			Loc_batchdetails.setBatch_name(Loc_batch.getBatch_name());
//			Loc_batchdetails.setBatch_status(Loc_batch.getBatch_status());
//			Loc_batchdetails.setPlant_id(Loc_batch.getPlant_id());
//			Loc_batchdetails.setYard_id(Loc_batch.getYard_id());
//			Loc_batchdetails.setBay_id(Loc_batch.getBay_id());
//			Loc_batchdetails.setRow_no(Loc_batch.getRow_no());
//			Loc_batchdetails.setLayer_no(Loc_batch.getLayer_no());
//			Loc_batchdetails.setColumn_no(Loc_batch.getColumn_no());
//			Loc_batchdetails.setCreated_by(Loc_batch.getCreated_by());
//			Loc_batchdetails.setUpdated_by(Loc_batch.getUpdated_by());
//			Loc_batchdetails.setCreated_date_time(Loc_batch.getCreated_date_time());
//			Loc_batchdetails.setUpdated_date_time(null);
//			Loc_batchdetails.setRecord_status(1);
//
//			intfSave(cubeHdrModel);
//		}
//		return Loc_batch;
//	}
	public String generateBatchPosition(CubeHdrModel cubeHdrModel) {
		String position = "";
		try {
			Optional<BayMstrModel> mstr_bay = bayService.getBayById(cubeHdrModel.getBay_id());
			String bay = mstr_bay.get().getBay_name().substring(0, 2).toUpperCase();
			position += bay;
			if (mstr_bay.get().getPosition_status() == 1) {
				int columnNumber = bayService.getColumnByBayWithRow(cubeHdrModel.getBay_id(),
						cubeHdrModel.getRow_no());
				if (columnNumber != 0) {
					if (columnNumber < 10) {
						position += "0" + columnNumber;
					} else {
						position += columnNumber;
					}
				}

			} else {
				position += "00";
			}

			// ROW NUMBER FORMAT

			int row = cubeHdrModel.getRow_no();
			if (row < 10) {
				position += "R00" + row;
			} else if (row > 9 && row < 100) {
				position += "R0" + row;
			} else {
				position += "R" + row;
			}

			// ROW NUMBER FORMAT

			position += "L" + cubeHdrModel.getLayer_no();
			int col = cubeHdrModel.getColumn_no();
			if (col < 10) {
				position += "0" + col;
			} else {
				position += col;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return position;
	}

	public INTFBatchDetails intfSave(CubeHdrModel cubeHdrModel) {
		INTFBatchDetails intf = new INTFBatchDetails();
		intf.setIntf_id(null);
		intf.setBatch_name(cubeHdrModel.getBatch_name());
		intf.setBatch_status(Constants.BATCH_STATUS_AVAILABLE);
		intf.setBatch_position(generateBatchPosition(cubeHdrModel));
		intf.setPlant_id(cubeHdrModel.getPlant_id());
		intf.setYard_id(cubeHdrModel.getYard_id());
		intf.setBay_id(cubeHdrModel.getBay_id());
		intf.setRow_no(cubeHdrModel.getRow_no());
		intf.setColumn_no(cubeHdrModel.getColumn_no());
		intf.setLayer_no(cubeHdrModel.getLayer_no());
		intf.setRead_status(0);
		intf.setCreated_date_time(intf.getCreated_date_time());
		return intf;
	}
	public class BatchNotFoundException extends RuntimeException {
		public BatchNotFoundException(String message) {
			super(message);
		}
	}
	@Override
	public CubeHdrModel updateBatch(CubeHdrModel cubeHdrModel) {
		try {
			CubeHdrModel batchToUpdate = new CubeHdrModel();
			batchToUpdate.setBatch_status(Constants.BATCH_STATUS_AVAILABLE);
			batchToUpdate.setBatch_position(generateBatchPosition(cubeHdrModel));
			batchToUpdate.setPlant_id(cubeHdrModel.getPlant_id());
			batchToUpdate.setYard_id(cubeHdrModel.getYard_id());
			batchToUpdate.setBay_id(cubeHdrModel.getBay_id());
			batchToUpdate.setRow_no(cubeHdrModel.getRow_no());
			batchToUpdate.setColumn_no(cubeHdrModel.getColumn_no());
			batchToUpdate.setLayer_no(cubeHdrModel.getLayer_no());
			batchToUpdate.setUpdated_by(cubeHdrModel.getUpdated_by());
			batchToUpdate.setUpdated_date_time(cubeHdrModel.getUpdated_date_time());
			batchToUpdate.setCreated_date_time(batchToUpdate.getCreated_date_time()); // Set current timestamp for created_date_time

			// Save the new batch to the repository
			return cubeRepository.save(batchToUpdate);
		} catch (Exception ex) {
			ex.printStackTrace(); // Log the exception for debugging
			// Return an appropriate response indicating that the batch couldn't be saved
			return null; // Or handle it based on your application's requirements
		}
	}


//	@Override
//	public CubeHdrModel updateBatch(CubeHdrModel cubeHdrModel) {
//
//		Optional<CubeHdrModel> b_obj = cubeRepository.getBatchByName(cubeHdrModel.getBatch_name(),
//				Constants.BATCH_STATUS_AVAILABLE);
//		if (b_obj.isPresent()) {
//			//b_obj.get().setBatch_name(batchHdrModel.getBatch_name());
//			b_obj.get().setBatch_status(Constants.BATCH_STATUS_AVAILABLE);
//			b_obj.get().setBatch_position(generateBatchPosition(cubeHdrModel));
//			b_obj.get().setPlant_id(cubeHdrModel.getPlant_id());
//			b_obj.get().setYard_id(cubeHdrModel.getYard_id());
//			b_obj.get().setBay_id(cubeHdrModel.getBay_id());
//			b_obj.get().setRow_no(cubeHdrModel.getRow_no());
//			b_obj.get().setColumn_no(cubeHdrModel.getColumn_no());
//			b_obj.get().setLayer_no(cubeHdrModel.getLayer_no());
//			b_obj.get().setUpdated_by(cubeHdrModel.getUpdated_by());
//			b_obj.get().setUpdated_date_time(cubeHdrModel.getUpdated_date_time());
//			b_obj.get().setCreated_date_time(b_obj.get().getCreated_date_time());
//			cubeRepository.save(b_obj.get());
//
//		}
//
//		return b_obj.get();
//	}



	@Override
	public List<CubeHdrModel> validateForBatchPick(String batch_name) {

		List<CubeHdrModel> temporary_batch = new ArrayList<>();

		Optional<CubeHdrModel> batch_obj = cubeRepository.getBatchByName(batch_name,
				Constants.BATCH_STATUS_AVAILABLE);

		CubeHdrModel cubeHdrModel = null;
		if (batch_obj.isPresent()) {
			cubeHdrModel = batch_obj.get();

			Optional<BayMstrModel> mstr_bay = bayService.getBayById(cubeHdrModel.getBay_id());

			int max_layer_no = mstr_bay.get().getLayer_no();
			int max_column_no = mstr_bay.get().getColumn_no();
			int row_no = cubeHdrModel.getRow_no();
			Long bay_id = cubeHdrModel.getBay_id();
			Long yard_id = cubeHdrModel.getYard_id();

			int loc_layer_no = cubeHdrModel.getLayer_no();
			int loc_column_no = cubeHdrModel.getColumn_no();
			int top_layer = 0, below_layer = 0;
			int pre_cols = 0, qry_pre_cols = 0;
			int after_cols = 0, qry_after_cols = 0;
			System.out.println("loc_layer_no: "+loc_layer_no);
			System.out.println("loc_column_no: "+loc_column_no);
			System.out.println("top_layer: "+top_layer);
			System.out.println("below_layer: "+below_layer);
			System.out.println("after_cols: "+after_cols);
			System.out.println("qry_after_cols: "+qry_after_cols);
			if (loc_layer_no < max_layer_no) {
				top_layer = loc_layer_no + 1;
				pre_cols = loc_column_no - 1;
				if (pre_cols > 0) {
					qry_pre_cols = pre_cols;
				}
				if (loc_column_no <= (max_column_no - 1)) {
					qry_after_cols = loc_column_no;
				}
				List<Integer> col = new ArrayList<>();
				if (qry_pre_cols > 0) {
					col.add(qry_pre_cols);
				}
				if (qry_after_cols > 0) {
					col.add(qry_after_cols);
				}

				temporary_batch = cubeRepository.getBatchList(yard_id, bay_id, row_no, top_layer, col,
						Constants.BATCH_STATUS_AVAILABLE);
			} else {

				System.out.println("Pick Batch Validation Pass..");
			}
		}

		return temporary_batch;
	}

	@Override
	public List<CubeHdrModel> validateForBatchMove(CubeHdrModel cubeHdrModel) {

		Optional<BayMstrModel> mstr_bay = bayService.getBayById(cubeHdrModel.getBay_id());
		List<CubeHdrModel> newBatch = new ArrayList<>();

		int max_layer_no = mstr_bay.get().getLayer_no();
		int max_column_no = mstr_bay.get().getColumn_no();
		int row_no = cubeHdrModel.getRow_no();
		Long bay_id = cubeHdrModel.getBay_id();
		Long yard_id = cubeHdrModel.getYard_id();

		int loc_layer_no = cubeHdrModel.getLayer_no();
		int loc_column_no = cubeHdrModel.getColumn_no();
		int top_layer = 0;
		int pre_cols = 0, qry_pre_cols = 0;
		int after_cols = 0, qry_after_cols = 0;
		int btm_layer = 0;
		int pre_col = 0, yard_pre_col = 0;
		int aft_col = 0, yard_aft_col = 0;

		btm_layer = loc_layer_no - 1;

		if (loc_layer_no > 0) {
			aft_col = loc_column_no + 1;
			if (aft_col > 0 && aft_col <= max_column_no) {
				yard_aft_col = aft_col;
			}
			if (loc_column_no > 0) {
				yard_pre_col = loc_column_no;
			}

			List<Integer> newCol = new ArrayList<>();
			if (yard_pre_col > 0) {
				newCol.add(yard_pre_col);
			}

			if (yard_aft_col > 0) {
				newCol.add(yard_aft_col);
			}

			newBatch = cubeRepository.getBatchList(yard_id, bay_id, row_no, top_layer, newCol,
					Constants.BATCH_STATUS_AVAILABLE);
		} else {
			System.out.println("Move Batch Validation Pass..");
		}

		return newBatch;
	}

	@Override
	public Optional<CubeHdrModel> getVerifyPositionStatus(CubeHdrModel cubeHdrModel) {

		Optional<CubeHdrModel> temp_batch = cubeRepository.getVerifyPositionStatus(cubeHdrModel.getYard_id(),
				cubeHdrModel.getBay_id(), cubeHdrModel.getRow_no(), cubeHdrModel.getLayer_no(),
				cubeHdrModel.getColumn_no(), Constants.BATCH_STATUS_AVAILABLE);

		return (temp_batch.isPresent()) ? temp_batch : (Optional) temp_batch.empty();
	}
	@Override
	public List<CubeHdrModel> getValidationForSave(CubeHdrModel cubeHdrModel) {
		Optional<BayMstrModel> mstr_Bay = bayService.getBayById(cubeHdrModel.getBay_id());
		System.out.println("cubeHdrModel: " + cubeHdrModel.getBay_id());
		List<CubeHdrModel> newYard = new ArrayList<>();

		// Ensure mstr_Bay is present before proceeding
		if (mstr_Bay.isPresent()) {
			int max_layer_no = mstr_Bay.get().getLayer_no();
			int loc_layer_no = cubeHdrModel.getLayer_no();

			// Assuming loc_layer_no is 1-based, decrement it to access the 0-based index
			loc_layer_no--;

			// Initialize sideViewState array if it's null
			if (sideViewState == null) {
				sideViewState = new boolean[max_layer_no];
				Arrays.fill(sideViewState, true); // Initialize all layers in side view as editable
			}

			// Mark the corresponding front view or side view layer as used based on the conditions
			if (cubeHdrModel.getLayer_no() % 2 == 0) {
				// If the layer number is even, it's a side view layer
				sideViewState[loc_layer_no] = false; // Mark side view layer as used
				frontViewState[loc_layer_no] = false; // Mark corresponding front view layer as disabled
				System.out.println("Side view layer " + (loc_layer_no + 1) + " is used. Front view layer " + (loc_layer_no + 1) + " is disabled.");
			} else {
				// If the layer number is odd, it's a front view layer
				frontViewState[loc_layer_no] = false; // Mark front view layer as used
				sideViewState[loc_layer_no] = false; // Mark corresponding side view layer as disabled
				System.out.println("Front view layer " + (loc_layer_no + 1) + " is used. Side view layer " + (loc_layer_no + 1) + " is disabled.");
			}

			// Additional existing code logic for batch validation and processing goes here
			// newYard = performBatchValidation(cubeHdrModel); // Example function call
		} else {
			// Handle the case when BayMstrModel is not found for the given bay_id
			System.out.println("Error: BayMstrModel not found for bay_id " + cubeHdrModel.getBay_id());
			// You may throw an exception or handle it as per your application's error handling strategy
		}

		return newYard;
	}


	//@Override
	public List<CubeHdrModel> getValidationForSave1(CubeHdrModel cubeHdrModel) {
		Optional<BayMstrModel> mstr_Bay = bayService.getBayById(cubeHdrModel.getBay_id());
		System.out.println("cubeHdrModel: "+cubeHdrModel.getBay_id());
		List<CubeHdrModel> newYard = new ArrayList<>();

		// Ensure mstr_Bay is present before proceeding
		if (mstr_Bay.isPresent()) {
			int max_layer_no = mstr_Bay.get().getLayer_no();
			int loc_layer_no = cubeHdrModel.getLayer_no();

			// Assuming loc_layer_no is 1-based, decrement it to access the 0-based index
			loc_layer_no--;

			// Initialize sideViewState array if it's null
			if (sideViewState == null) {
				sideViewState = new boolean[max_layer_no];
				Arrays.fill(sideViewState, true); // Initialize all layers in side view as editable
			}

			// If batch is saved in the front view, mark the corresponding side view layer as not editable
			if (loc_layer_no >= 0 && loc_layer_no < max_layer_no) {
				frontViewState[loc_layer_no] = false; // Mark front view layer as used
				sideViewState[loc_layer_no] = false; // Mark corresponding side view layer as not editable
				System.out.println("Front view layer " + (loc_layer_no + 1) + " is used. Side view layer " + (loc_layer_no + 1) + " is disabled.");
			}

			// Additional existing code logic for batch validation and processing goes here
			// newYard = performBatchValidation(cubeHdrModel); // Example function call
		} else {
			// Handle the case when BayMstrModel is not found for the given bay_id
			System.out.println("Error: BayMstrModel not found for bay_id " + cubeHdrModel.getBay_id());
			// You may throw an exception or handle it as per your application's error handling strategy
		}

		return newYard;
	}
	boolean[] frontViewState = null; // Tracks available layers for front view
	boolean[] sideViewState = null; // Tracks available layers for side view

	ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public String getBatchListHTML(String shape, Long bay_id, int row_no, String batch_status) {
		StringBuilder htmlContent = new StringBuilder();

		if ("round".equals(shape)) {
			htmlContent.append(fetchBatchAllListByYardHTMLRound());
		} else if ("rectangle".equals(shape)) {
			htmlContent.append(fetchBatchAllListByYardHTMLFst());
		}

		return htmlContent.toString();
	}

	public String fetchBatchAllListByYardHTMLFst(Long bay_id, int row_no, String batch_status) {
		Optional<BayMstrModel> mstr_bay = bayService.getBayById(bay_id);
		int max_layer = mstr_bay.get().getLayer_no();
		int max_cols = mstr_bay.get().getColumn_no();
		Long yard_id = mstr_bay.get().getYard_id();

		// Initialize view states based on max layers
		frontViewState = new boolean[max_layer];
		sideViewState = new boolean[max_layer];
		Arrays.fill(frontViewState, true);
		Arrays.fill(sideViewState, true);

		String sideViewStateJson = null;
		try {
			sideViewStateJson = objectMapper.writeValueAsString(sideViewState);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		StringBuilder html_str = new StringBuilder();
		html_str.append("<input type='hidden' id='sideViewState' value='" + sideViewStateJson + "' />");		html_str.append("<h4>Front View</h4>");
		html_str.append("<div style=\"display: flex;\">");
		html_str.append("<div id=\"front-container\" class=\"cube-container\" style=\"overflow-y: scroll;\">");
		for (int i = max_layer; i > 0; i--) { // Loop in reverse order starting from max_layer
			html_str.append("<div class=\"cube-row\">");
			for (int j = 1; j <= max_cols; j++) {
				Optional<CubeHdrModel> coil = cubeRepository.getVerifyPositionStatus(yard_id, bay_id, row_no, i, j, batch_status);
				String coilInfo = "";
				if (coil.isPresent()) {
					String save_coil = coil.get().getBatch_name() + "<br>" + coil.get().getBatch_position();
					coilInfo = coil.get().getBatch_name() + "/" + row_no + "/" + i + "/" + j; // Customize coil info as needed
					html_str.append("<div class=\"cube\" style=\"background-color:green;box-shadow:inset 0 0 1.5px 1.5px #444444\"  data-coil-info=\"" + coilInfo + "\">" + save_coil + "</div>");
				} else {
					String title = "Front";
					coilInfo = "NIL/" + row_no + "/" + i + "/" + j;
					html_str.append("<div class=\"cube\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\" data-coil-info=\"" + coilInfo + "\">" + title + "</div>");
				}
			}
			html_str.append("</div>");
		}
		html_str.append("</div>");

		// Generate side view HTML
		StringBuilder side_html_str = new StringBuilder();
		side_html_str.append("<h4 style=\"margin-top:-9mm;width:0mm;\">Side View</h4>");
		side_html_str.append("<div style=\"display: flex;\">");
		side_html_str.append("<div id=\"side-container\" class=\"cube-container\" style=\"overflow-y: scroll;\">"); // Apply scroll to side view container
		for (int i = max_layer - 1; i >= 0; i--) { // Loop in reverse order for side view starting from max_layer - 1 to 0
			side_html_str.append("<div class=\"cube-row\">");
			for (int j = 1; j <= max_cols; j++) {
				String title = "side";
				String coilInfo = "NIL/" + row_no + "/" + (i + 1) + "/" + j; // Correct layer indexing for coil info
				// Check if the layer is editable and add onclick event accordingly
				if (sideViewState[i]) {
					side_html_str.append("<div class=\"cube side-view editable\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\" data-coil-info=\"" + coilInfo + "\" onclick=\"handleSideViewClick('" + title + "', " + i + ")\">" + title + "</div>");
				} else {
					side_html_str.append("<div class=\"cube side-view disabled\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\" data-coil-info=\"" + coilInfo + "\">" + title + "</div>");
				}
			}
			side_html_str.append("</div>");
		}
		side_html_str.append("</div>");

		// JavaScript function to handle side view click
		html_str.append("<script>");
		html_str.append("function handleSideViewClick(layer, index) {");
		html_str.append("var sideViewStateStr = document.getElementById('sideViewState').value;");
		html_str.append("console.log('sideViewStateStr:', sideViewStateStr);"); // Log sideViewStateStr for debugging
		html_str.append("var sideViewState = JSON.parse(sideViewStateStr);");
		html_str.append("if (sideViewState[index]) {");
		html_str.append("alert('Can create new batches in side view layer ' + (index + 1));");
		html_str.append("} else {");
		html_str.append("alert('Cannot create new batches in front view layer ' + (index + 1));");
		html_str.append("}");
		html_str.append("}");
		html_str.append("</script>");
		return html_str.toString() + side_html_str.toString();
	}

	@Override
	public String fetchBatchAllListByYardHTMLScnd(Long bay_id, int row_no, String batch_status) {
		return null;
	}

	@Override
	public String fetchBatchAllListByYardHTMLFst() {
		int max_layer = 5;
		int max_cols = 10;

		// Initialize view states based on max layers
		frontViewState = new boolean[max_layer];
		sideViewState = new boolean[max_layer];
		Arrays.fill(frontViewState, true);
		Arrays.fill(sideViewState, true);

		// Convert side view state to JSON
		String sideViewStateJson = null;
		try {
			sideViewStateJson = objectMapper.writeValueAsString(sideViewState);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// Build HTML for front view
		StringBuilder html_str = new StringBuilder();
		html_str.append("<input type='hidden' id='sideViewState' value='" + sideViewStateJson + "' />");
		html_str.append("<h4>Front View</h4>");
		html_str.append("<div style=\"display: flex;\">");
		html_str.append("<div id=\"front-container\" class=\"cube-container\" style=\"overflow-y: scroll;\">");
		for (int i = max_layer; i > 0; i--) {
			html_str.append("<div class=\"cube-row\">");
			for (int j = 1; j <= max_cols; j++) {
				// Here, you can add your logic for different shapes
				// For example, if you want to display Round shape for all cells, you can use this:
				String title = "Front";
				html_str.append("<div class=\"cube\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\"></div>");
			}
			html_str.append("</div>");
		}
		html_str.append("</div>");

		// Build HTML for side view
		StringBuilder side_html_str = new StringBuilder();
		side_html_str.append("<h4 style=\"margin-top:-9mm;width:0mm;\">Side View</h4>");
		side_html_str.append("<div style=\"display: flex;\">");
		side_html_str.append("<div id=\"side-container\" class=\"cube-container\" style=\"overflow-y: scroll;\">");
		for (int i = max_layer - 1; i >= 0; i--) {
			side_html_str.append("<div class=\"cube-row\">");
			for (int j = 1; j <= max_cols; j++) {
				String title = "side";
				// Here, you can add your logic for side view cells similar to the front view
				if (sideViewState[i]) {
					side_html_str.append("<div class=\"cube side-view editable\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\"  onclick=\"handleSideViewClick('" + title + "', " + i + ")\">" + title + "</div>");
				} else {
					side_html_str.append("<div class=\"cube side-view disabled\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\" " + title + "</div>");
				}
			}
			side_html_str.append("</div>");
		}
		side_html_str.append("</div>");

		// JavaScript function to handle side view click
		StringBuilder script = new StringBuilder();
		script.append("<script>");
		script.append("function handleSideViewClick(layer, index) {");
		script.append("var sideViewStateStr = document.getElementById('sideViewState').value;");
		script.append("console.log('sideViewStateStr:', sideViewStateStr);");
		script.append("var sideViewState = JSON.parse(sideViewStateStr);");
		script.append("if (sideViewState[index]) {");
		script.append("alert('Can create new batches in side view layer ' + (index + 1));");
		script.append("} else {");
		script.append("alert('Cannot create new batches in front view layer ' + (index + 1));");
		script.append("}");
		script.append("}");
		script.append("</script>");

		// Concatenate HTML for front view, side view, and script
		StringBuilder finalHtml = new StringBuilder();
		finalHtml.append(html_str.toString());
		finalHtml.append(side_html_str.toString());
		finalHtml.append(script.toString());

		return finalHtml.toString();
	}

	// New method for saving batch name in the side view
	public void saveBatchName(String batchName, int frontLayerIndex, int sideLayerIndex) {
		// Assuming sideViewState and frontViewState are accessible instance variables

		// Check if the corresponding front layer is filled
		if (!frontViewState[frontLayerIndex]) {
			// Front layer is already filled, so disallow saving batch name in the side view
			System.out.println("Cannot save batch name in side view layer: " + (sideLayerIndex + 1) + ", Front layer already filled");
		}

		// Check if the side view layer is editable
		if (sideViewState[sideLayerIndex]) {
			// Save batch name in side view
			sideViewState[sideLayerIndex] = false; // Update state to indicate the layer is now filled
			// Add your logic to save batch name here
			System.out.println("Batch name saved in side view layer: " + (sideLayerIndex + 1) + ", Batch Name: " + batchName);
		} else {
			// Cannot save in side view, show error or handle accordingly
			System.out.println("Cannot save batch name in side view layer: " + (sideLayerIndex + 1) + ", Layer already filled");
		}
	}

	@Override
	public String fetchBatchAllListByYardHTMLRound() {
		int max_layer = 5;
		int max_cols = 10;

		// Initialize view states based on max layers
		frontViewState = new boolean[max_layer];
		sideViewState = new boolean[max_layer];
		Arrays.fill(frontViewState, true);
		Arrays.fill(sideViewState, true);

		String sideViewStateJson = null;
		try {
			sideViewStateJson = objectMapper.writeValueAsString(sideViewState);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println("sideViewStateJson: " + sideViewStateJson);
		StringBuilder html_str = new StringBuilder();
		html_str.append("<input type='hidden' id='sideViewState' value='" + sideViewStateJson + "' />");		html_str.append("<h4>Front View</h4>");
		html_str.append("<div style=\"display: flex;\">");
		html_str.append("<div id=\"front-container\" class=\"round-container\" style=\"overflow-y: scroll;\">");
		for (int i = max_layer; i > 0; i--) { // Loop in reverse order starting from max_layer
			html_str.append("<div class=\"round-row\">");
			for (int j = 1; j <= max_cols; j++) {
				String title = "Front";
				html_str.append("<div class=\"round\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\"  title=\"" + title + "\"></div>");
				}
			html_str.append("</div>");
		}
		html_str.append("</div>");

		// Generate side view HTML
		StringBuilder side_html_str = new StringBuilder();
		side_html_str.append("<h4 style=\"margin-top:-9mm;width:0mm;\">Side View</h4>");
		side_html_str.append("<div style=\"display: flex;\">");
		side_html_str.append("<div id=\"side-container\" class=\"round-container\" style=\"overflow-y: scroll;\">"); // Apply scroll to side view container
		for (int i = max_layer - 1; i >= 0; i--) { // Loop in reverse order for side view starting from max_layer - 1 to 0
			side_html_str.append("<div class=\"round-row\">");
			for (int j = 1; j <= max_cols; j++) {
				String title = "side";
				// Check if the layer is editable and add onclick event accordingly
				if (sideViewState[i]) {
					side_html_str.append("<div class=\"round side-view editable\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\"  onclick=\"handleSideViewClick('" + title + "', " + i + ")\">" + title + "</div>");
				} else {
					side_html_str.append("<div class=\"round side-view disabled\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\" " + title + "</div>");
				}

			}
			side_html_str.append("</div>");
		}
		side_html_str.append("</div>");

		// JavaScript function to handle side view click
		StringBuilder script = new StringBuilder();
		script.append("<script>");
		script.append("function handleSideViewClick(layer, index) {");
		script.append("var sideViewStateStr = document.getElementById('sideViewState').value;");
		script.append("console.log('sideViewStateStr:', sideViewStateStr);"); // Log sideViewStateStr for debugging
		script.append("var sideViewState = JSON.parse(sideViewStateStr);");
		script.append("if (sideViewState[index]) {");
		script.append("alert('Can create new batches in side view layer ' + (index + 1));");
		script.append("} else {");
		script.append("alert('Cannot create new batches in front view layer ' + (index + 1));");
		script.append("}");
		script.append("}");
		script.append("</script>");

		StringBuilder finalHtml = new StringBuilder();
		finalHtml.append(html_str.toString());
		finalHtml.append(side_html_str.toString());
		finalHtml.append(script.toString());

		return finalHtml.toString();

	}

	@Override
	public String fetchBatchAllListByYardHTMLRoundRect() {
		int max_layer = 5;
		int max_cols = 10;

		// Initialize view states based on max layers
		frontViewState = new boolean[max_layer];
		sideViewState = new boolean[max_layer];
		Arrays.fill(frontViewState, true);
		Arrays.fill(sideViewState, true);

		String sideViewStateJson = null;
		try {
			sideViewStateJson = objectMapper.writeValueAsString(sideViewState);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println("sideViewStateJson: " + sideViewStateJson);
		StringBuilder html_str = new StringBuilder();
		html_str.append("<input type='hidden' id='sideViewState' value='" + sideViewStateJson + "' />");		html_str.append("<h4>Front View</h4>");
		html_str.append("<div style=\"display: flex;\">");
		html_str.append("<div id=\"front-container\" class=\"roundRect-container\" style=\"overflow-y: scroll;\">");
		for (int i = max_layer; i > 0; i--) { // Loop in reverse order starting from max_layer
			html_str.append("<div class=\"roundRect-row\">");
			for (int j = 1; j <= max_cols; j++) {
				String title = "Front";
				html_str.append("<div class=\"roundRect\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\"  title=\"" + title + "\"></div>");
			}
			html_str.append("</div>");
		}
		html_str.append("</div>");

		// Generate side view HTML
		StringBuilder side_html_str = new StringBuilder();
		side_html_str.append("<h4 style=\"margin-top:-9mm;width:0mm;\">Side View</h4>");
		side_html_str.append("<div style=\"display: flex;\">");
		side_html_str.append("<div id=\"side-container\" class=\"roundRect-container\" style=\"overflow-y: scroll;\">"); // Apply scroll to side view container
		for (int i = max_layer - 1; i >= 0; i--) { // Loop in reverse order for side view starting from max_layer - 1 to 0
			side_html_str.append("<div class=\"roundRect-row\">");
			for (int j = 1; j <= max_cols; j++) {
				String title = "side";
				// Check if the layer is editable and add onclick event accordingly
				if (sideViewState[i]) {
					side_html_str.append("<div class=\"roundRect side-view editable\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\"  onclick=\"handleSideViewClick('" + title + "', " + i + ")\">" + title + "</div>");
				} else {
					side_html_str.append("<div class=\"roundRect side-view disabled\" style=\"background-color:#d5cfcf8f;box-shadow:inset 0 0 1.5px 1.5px #444444\" title=\"" + title + "\" " + title + "</div>");
				}

			}
			side_html_str.append("</div>");
		}
		side_html_str.append("</div>");

		// JavaScript function to handle side view click
		StringBuilder script = new StringBuilder();
		script.append("<script>");
		script.append("function handleSideViewClick(layer, index) {");
		script.append("var sideViewStateStr = document.getElementById('sideViewState').value;");
		script.append("console.log('sideViewStateStr:', sideViewStateStr);"); // Log sideViewStateStr for debugging
		script.append("var sideViewState = JSON.parse(sideViewStateStr);");
		script.append("if (sideViewState[index]) {");
		script.append("alert('Can create new batches in side view layer ' + (index + 1));");
		script.append("} else {");
		script.append("alert('Cannot create new batches in front view layer ' + (index + 1));");
		script.append("}");
		script.append("}");
		script.append("</script>");

		StringBuilder finalHtml = new StringBuilder();
		finalHtml.append(html_str.toString());
		finalHtml.append(side_html_str.toString());
		finalHtml.append(script.toString());

		return finalHtml.toString();


	}

	@Override
	public Optional<CubeHdrModel> getBatchName(String batch_name) {
		Optional<CubeHdrModel> ob = cubeRepository.getBatchNameList(batch_name);
		return ob;
	}
	@Override
	public Optional<CubeHdrModel> getBayById(Long bay_id) {
		return cubeRepository.findById(bay_id);
	}
	@Override
	public int[] getFilledCoilCount(Long bay_id, int row_no) {
		Optional<BayMstrModel> mstr_bay = bayService.getBayById(bay_id);
		int max_layer = mstr_bay.get().getLayer_no();
		int max_cols = mstr_bay.get().getColumn_no();
		Long yard_id = mstr_bay.get().getYard_id();
		int max_row_no = mstr_bay.get().getRow_no();
		int req_row_no = row_no;
		String html_str = "";
		int totalcount = 0;
		int filled = 0, empty = 0;
		int colsCount = 0;
		for (int i = max_layer; i > 0; i--) {
			if (i == 1) {
				colsCount = max_cols;
			} else {
				colsCount = (max_cols - (i - 1));
			}
			String coil_val = "";
			String coil_info = "";

			for (int j = 1; j <= colsCount; j++) {

				Optional<CubeHdrModel> coil = cubeRepository.getVerifyPositionStatus(yard_id, bay_id, req_row_no, i,
						j, Constants.BATCH_STATUS_AVAILABLE);
				//	System.out.println(yard_id+"/"+ bay_id+"/"+ req_row_no+"/"+ i+"/"+j+"/"+Constants.BATCH_STATUS_AVAILABLE);
				if (coil.isPresent()) {
					filled = filled + 1;
				} else {
					empty = empty + 1;
				}
			}
		}
		int columnNumber = bayService.getColumnByBayWithRow(bay_id, req_row_no);

		int[] val = { (filled + empty), filled, empty, columnNumber };
		return val;
	}
}
