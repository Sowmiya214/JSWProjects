package com.jsw.ym.cube.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.jsw.ym.master.Entity.BayMstrModel;
import com.jsw.ym.master.Entity.PlantMstrModel;
import com.jsw.ym.master.Entity.YardMstrModel;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import lombok.Data;

@Entity
@Table(name = "trns_batch_hdr")
@TableGenerator(name = "TRNS_BATCH_ID", table = "SEQ_GENERATOR", pkColumnName = "COL_KEY", valueColumnName = "NEXT_VAL", pkColumnValue = "BATCH_SEQ_ID", allocationSize = 1)
@DynamicUpdate
@Data
public class CubeHdrModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TRNS_BATCH_ID")
	@Column(name = "batch_id", unique = true, nullable = false)

	private Long batch_id;

	private String batch_position;

	private String batch_name;

	private String batch_status;

	private Long plant_id;

	private Long yard_id;

	private Long bay_id;

	private int row_no;

	private int layer_no;

	private int column_no;

	private int created_by;

	private int front_layer;

	private int side_layer;

	@CreatedDate
	private LocalDateTime created_date_time = LocalDateTime.now();

	private int updated_by;

	@LastModifiedDate
	private LocalDateTime updated_date_time = LocalDateTime.now();

	private int record_status;

	@JoinColumn(name = "plant_id", insertable = false, updatable = false)
	@ManyToOne(targetEntity = PlantMstrModel.class, fetch = FetchType.EAGER)
	private PlantMstrModel plantDetails;

	@JoinColumn(name = "yard_id", insertable = false, updatable = false)
	@ManyToOne(targetEntity = YardMstrModel.class, fetch = FetchType.EAGER)
	private YardMstrModel YardDetails;

	@JoinColumn(name = "bay_id", insertable = false, updatable = false)
	@ManyToOne(targetEntity = BayMstrModel.class, fetch = FetchType.EAGER)
	private BayMstrModel BayDetails;

	public CubeHdrModel() {
		super();
	}

}
