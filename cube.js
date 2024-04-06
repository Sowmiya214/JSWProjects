var timerId = 0;
$(document).ready(function() {
    $('.select2').select2()
    comboLocationList();
    //cardsOperationCollapse("row_cards_option,coil_cards_option,coil_cards_option2");
})

function comboLocationList() {
    loadComboList("t1_plant_name", base_url+"/commonCtrl/getListPlant");
}

$("#t1_plant_name").change(function() {
    var plantId = $(this).val();
    $("#lable_plant_info").text($("#t1_plant_name option:selected").text());
    loadComboList("t1_yard_name", base_url+"/commonCtrl/getListYard?plant_id=" + plantId);
    emptyComboList("t1_yard_name,t1_bay_name,t1_row_no");
    emptytext("lable_yard_info,lable_bay_info");
    cardsOperationCollapse("row_cards_option,coil_cards_option,coil_cards_option2");
    clearRowAndGrid();
});
$("#t1_yard_name").change(function() {
    var yardId = $(this).val();
    $("#lable_yard_info").text($("#t1_yard_name option:selected").text());
    loadComboList("t1_bay_name", base_url+"/commonCtrl/getListBay?yard_id=" + yardId);
    emptyComboList("t1_bay_name,t1_row_no");
    cardsOperationCollapse("row_cards_option,coil_cards_option,coil_cards_option2");
    clearRowAndGrid();
});
$("#t1_bay_name").change(function() {
    var bay_id = $(this).val();
    $("#lable_bay_info").text($("#t1_bay_name option:selected").text());
    loadComboList("t1_row_no", base_url+"/commonCtrl/getListRowNo?bay_id=" + bay_id);
    cardsOperationExpand("row_cards_option");
    getRowList(bay_id);
    clearRowAndGrid();
});
// $("#shapes_name").change(function() {
//     selectedItem = $(this).children("option:selected").val();
//     cardsOperationExpand("row_cards_option");
//     batchListByYard(bay_id,selectedItem);
//     clearRowAndGrid();
// });
function clearRowAndGrid() {
    $('#coilGrid').html("");
    $('#coilGrid2').html("");
    $('#coil_row_list').html("");
    $('#lable_total_row_info').html("");
    $('#lable_filled_row_info').html("");
    $('#lable_empty_row_info').html("");
}
function getRowList(bay_id) {
    $.ajax({
        url: base_url+"/commonCtrl/getTotalRows?bay_id=" + bay_id,
        type: "GET",
        /*data: JSON.stringify(formData),
        //dataType: "JSON",*/

        success: function(data) {
            $('#coil_row_list').html(data[0]);
            $('#lable_total_row_info').html(data[1]);
            $('#lable_filled_row_info').html(data[2]);
            $('#lable_empty_row_info').html(data[3]);
        },
        error: function(data){
            if (data.status == 500) {
                toastr.error('Internal Server Error.');
            }
        }
    });
}
let selectedItem ="";
$("select#shapes_name").change(function() {
    selectedItem = $(this).children("option:selected").val();
    console.log(selectedItem);
    if (selectedItem == 'Round') {
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: base_url + "/CubeInfo/fetchBatchAllListByYardHTMLRound",
            type: "GET",
            success: function(data) {
                // Append the fetched HTML to the container
                $('#coilGrid').html(data.data);
            },
            error: function(data) {
                if (data.status == 500) {
                    toastr.error('Internal Server Error.');
                }
            }
        });
    }
    if(selectedItem == 'Rectangle'){
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: base_url + "/CubeInfo/getBatchAllListByYardHTML",
            type: "GET",
            success: function(data) {
                // Append the fetched HTML to the container
                $('#coilGrid').html(data.data);
            },
            error: function(data) {
                if (data.status == 500) {
                    toastr.error('Internal Server Error.');
                }
            }
        });
    }
    if(selectedItem == 'RoundRect'){
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: base_url + "/CubeInfo/fetchBatchAllListByYardHTMLRoundRect",
            type: "GET",
            success: function(data) {
                // Append the fetched HTML to the container
                $('#coilGrid').html(data.data);
            },
            error: function(data) {
                if (data.status == 500) {
                    toastr.error('Internal Server Error.');
                }
            }
        });
    }
});

function batchListByYard(bay_id, r1) {
    $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },

        url: base_url+"/CubeInfo/getBatchAllListByYardHTML1?bay_id=" + bay_id + "&row_no=" + r1 + "&option=1",
        type: "GET",
        /*data: JSON.stringify(formData),
        //dataType: "JSON",*/

        success: function(data) {
            $('#coilGrid2').html(data.data);

        },
        error: function(data){
            if (data.status == 500) {
                toastr.error('Internal Server Error.');
            }
        }

    })
    // $.ajax({
    //     headers: {
    //         'Accept': 'application/json',
    //         'Content-Type': 'application/json'
    //     },
    //
    //     url: base_url+"/batchInfo/getBatchAllListByYardHTML?bay_id=" + bay_id + "&row_no=" + r2 + "&option=2",
    //     type: "GET",
    //     /*data: JSON.stringify(formData),
    //     //dataType: "JSON",*/
    //
    //     success: function(data) {
    //         $('#coilGrid2').html(data.data);
    //
    //     },
    //     error: function(data){
    //         if (data.status == 500) {
    //             toastr.error('Internal Server Error.');
    //         }
    //     }
    //
    // })
}

$(document).on("click", ".select-row", function(e) {
    e.preventDefault();
    var bay_id = $("#t1_bay_name").val();
    let thisRowNumber = $(this).attr('data-rowNumber');
    console.log(">>>>>>>: "+thisRowNumber);

    let r1 = 0, r2 = 0;
    if ((parseInt(thisRowNumber) % 2) == 0) {
        r1 = parseInt(thisRowNumber) - 1;
        r2 = parseInt(thisRowNumber);
    } else {
        r1 = parseInt(thisRowNumber);
        r2 = parseInt(thisRowNumber) + 1;
    }

    $("#selected_row_no").val(r1);
    $("#selected_row_no2").val(selectedItem);
    $("#lable_row_info").text(r1);
    $("#lable_row_info2").text(selectedItem);
    cardsOperationExpand("coil_cards_option,coil_cards_option2");
    cardsOperationCollapse("plant_card_operation");
    batchListByYard(bay_id, r1, r2);
});
function isSideViewLayer(layer) {
    // Check if the layer is odd
    return layer % 2 == 0; // If layer is odd, it belongs to side view
}
function isFrontViewLayer(layer) {
    // Check if the layer is odd
    return layer % 2 != 0; // If layer is odd, it belongs to side view
}



var focusInName = '';
$(document).on("click", ".cube", function(e) {
    e.preventDefault();
    $('#modal-batch-add').modal('show');
    $("#current_select").val("R1");
    let thisdata = $(this).attr('data-coil-info');

    console.log(">>>>>>>>>>>: "+thisdata);
    let arr = thisdata.split("/");

    let selected_row = $("#selected_row_no").val();
    let batch_name = arr[0];
    let row = arr[1];
    let layer = arr[2];
    let column = arr[3];
    console.log("batch_name: "+batch_name);
    console.log("row: "+row);
    console.log("layer: "+layer);
    console.log("column: "+column);
    let newyard = $('#t1_yard_name').val();
    let newplant = $('#t1_plant_name').val();
    let newbay = $('#t1_bay_name').val();
    let newrow = $('#t1_row_no').val();

    if (batch_name === 'NIL') {

        var formData = {
            "plant_id": newplant,
            "yard_id": newyard,
            "bay_id": newbay,
            "row_no": newrow,
            "layer_no": layer,
            "column_no": column
        };
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: base_url+"/batchInfo/getValidationForSave?plant_id=" + newplant + "&yard_id=" + newyard + "&bay_id=" + newbay + "&row_no=" + selected_row + "&layer_no=" + layer + "&column_no=" + column,
            type: "GET",
           // data: JSON.stringify(formData),
            //dataType: "JSON",

            success: function(data) {
                if (data.status == 1) {
                    $('#save_layer_no').val(layer);
                    $('#save_column_no').val(column);
                    //$('#save_batch_name').val('');
                    //$('#save_batch_name').focus();

                    var b = document.getElementById("save_batch_name");
                    b.value = "";
                    b.focus();

                    $('#modal-batch-add').on('shown.bs.modal', function() {
                        $('#save_batch_name').focus();
                    })

                }
                if(data.status == 3){
                    toastr.error('Internal Server Error.');
                }

            },
            error: function(data){
                if (data.status == 500) {
                    toastr.error('Batch Already Exist.');
                }
            }

        })

        } else {
        $('#modal-batch-dispatch').modal('show');

        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'

            },
            url: base_url+"/CubeInfo/getValidateForBatchPick?batch_name=" + batch_name,
            type: "GET",
            //data: JSON.stringify(formData),
            //dataType: "JSON",

            success: function(data) {
                if (data.status == 1) {

                    $('#coilMoveGrid').html('');
                    $('#dispatch_batch_name').val(batch_name);
                    $('#lable_dispatch_batch_name').text(batch_name);
                }
                // else if (data.status == 2) {
                //     var b_name = '';
                //     for (var i = 0; i < data.data.length; i++) {
                //         if (b_name == '') {
                //             b_name = data.data[i].batch_name;
                //         } else {
                //             b_name = b_name + ',' + data.data[i].batch_name;
                //         }
                //     }
                //     toastr.info(b_name + ' Move Mentioned Batch first..');
                //
                // }

            },
            error: function(data){
                if (data.status == 500) {
                    toastr.error('Internal Server Error.');
                }
            }

        })
    }
});


/*coilGrid2*/

var focusInName = '';
$(document).on("click", ".cube side-view editable", function(e) {
    $("#current_select").val("R2");
    e.preventDefault();

    let thisdata = $(this).attr('data-coil-info');

    let arr = thisdata.split("/");
    $('#modal-batch-add').modal('show');
    let selected_row = $("#selected_row_no2").val();
    let batch_name = arr[0];
    let row = arr[1];
    let layer = arr[2];
    let column = arr[3];
    let newyard = $('#t1_yard_name').val();
    let newplant = $('#t1_plant_name').val();
    let newbay = $('#t1_bay_name').val();
    let newrow = $('#t1_row_no').val();

    if (batch_name === 'NIL') {
        var formData = {
            "plant_id": newplant,
            "yard_id": newyard,
            "bay_id": newbay,
            "row_no": newrow,
            "layer_no": layer,
            "column_no": column
        };
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: base_url+"/CubeInfo/getValidationForSave?plant_id=" + newplant + "&yard_id=" + newyard + "&bay_id=" + newbay + "&row_no=" + selected_row + "&layer_no=" + layer + "&column_no=" + column,
            type: "GET",
            //data: JSON.stringify(formData),
            //dataType: "JSON",

            success: function(data) {
                if (data.status == 1) {
                    $('#modal-batch-add').modal('show');
                    $('#save_layer_no').val(layer);
                    $('#save_column_no').val(column);
                    //$('#save_batch_name').val('');
                    //$('#save_batch_name').focus();

                    var b = document.getElementById("save_batch_name");
                    b.value = "";
                    b.focus();

                    $('#modal-batch-add').on('shown.bs.modal', function () {
                        $('#save_batch_name').focus();
                    })
                }
                // } else if (data.status == 2) {
                //
                //     toastr.info('Fill Mentioned  Batch first..');
                // }
                //
                // else if (data.status == 3) {
                //
                //     toastr.info('Fill Mentioned  Batch first..');
                //
                // }

            },
            error: function(data){
                if (data.status == 500) {
                    toastr.error('Internal Server Error.');
                }
            }

        })
    } else {
        $('#modal-batch-dispatch').modal('show');
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'

            },
            url: base_url+"/CubeInfo/getValidateForBatchPick?batch_name=" + batch_name,
            type: "GET",
            //data: JSON.stringify(formData),
            //dataType: "JSON",

            success: function(data) {
                if (data.status == 1) {
                    $('#modal-batch-dispatch').modal('show');
                    $('#coilMoveGrid').html('');
                    $('#dispatch_batch_name').val(batch_name);
                    $('#lable_dispatch_batch_name').text(batch_name);

                } else if (data.status == 2) {
                    // var b_name = '';
                    // for (var i = 0; i < data.data.length; i++) {
                    //     if (b_name == '') {
                    //         b_name = data.data[i].batch_name;
                    //     } else {
                    //         b_name = b_name + ',' + data.data[i].batch_name;
                    //     }
                    // }
                    toastr.info(' Move Mentioned Batch first..');

                }

            },
            error: function(data){
                if (data.status == 500) {
                    toastr.error('Internal Server Error.');
                }
            }

        })
    }
});

$('#saveOrUpdateBatch').click(function() {
    let plant_id = $('#t1_plant_name').val();
    let yard_id = $('#t1_yard_name').val();
    let bay_id = $('#t1_bay_name').val();
    let selected_row = $("#current_select").val() === "R1" ? $("#selected_row_no").val() : $("#selected_row_no2").val();
    let row_no = selected_row;


    let thisdata = $(this).attr('data-coil-info');
    let arr = thisdata.split("/");
    let batch_name = arr[0];
    let row = arr[1];
    let layer = arr[2];
    let column = arr[3];

    let layer_no = $('#save_layer_no').val();
    let column_no = $('#save_column_no').val();
    let newbatch = $("#save_batch_name").val().trim();
    console.log("layer_no: "+layer);
    console.log("column_no: "+column);
    console.log("newbatch: "+batch_name);

    if (newbatch === "" || newbatch.length < 10) {
        toastr.error('Enter Batch Name...');
        return false;
    }

    var formData = {
        "batch_name": newbatch.toUpperCase(),
        "plant_id": plant_id,
        "yard_id": yard_id,
        "bay_id": bay_id,
        "row_no": row_no,
        "layer_no": layer_no,
        "column_no": column_no
    };

    console.log(JSON.stringify(formData));

    // Call your endpoint to save or move the batch
    $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        url: base_url + "/CubeInfo/saveOrMoveBatch",
        type: "POST",
        data: JSON.stringify(formData),
        dataType: "JSON",
        success: function(data) {
            // Handle success response
            if (data.status == 1) {
                toastr.success('Batch Save Successfully.');
                // You may perform additional actions here if needed
            } else if (data.status == 3) {
                toastr.error('Batch already exists.');
            } else {
                toastr.error('Failed to save the batch.');
            }
        },
        error: function(data) {
            // Handle error response
            if (data.status == 500) {
                toastr.error('Internal Server Error.');
            }
        }
    });
});



$('#saveOrUpdateBatch1').click(function() {
    let plant_id = $('#t1_plant_name').val();
    let yard_id = $('#t1_yard_name').val();
    let bay_id = $('#t1_bay_name').val();
    let selected_row = $("#selected_row_no").val();
    let first_row = $("#selected_row_no").val();;
    let second_row = $("#selected_row_no2").val();
    if ($("#current_select").val() === "R1") {
        selected_row = $("#selected_row_no").val();

    } else {
        selected_row = $("#selected_row_no2").val();

    }

    let row_no = selected_row;//$("#t1_row_no").val();
    let layer_no = $('#save_layer_no').val();
    let column_no = $('#save_column_no').val();
    let newbatch = $("#save_batch_name").val().trim();
    if (newbatch === "" || newbatch.length < 10) {
        toastr.error('Enter Batch Name...');
        return false;
    }
    var formData = {
        "batch_name": newbatch.toUpperCase(),
        "plant_id": plant_id,
        "yard_id": yard_id,
        "bay_id": bay_id,
        "row_no": row_no,
        "layer_no": layer_no,
        "column_no": column_no
    };
    console.log(JSON.stringify(formData));
    $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'

        },
        url: base_url+"/CubeInfo/saveOrMoveBatch",
        type: "POST",
        data: JSON.stringify(formData),
        dataType: "JSON",

        success: function(data) {
            if (data.status == 1) {
                toastr.success('Batch Save Successfully.');
                getRowList(bay_id);
                batchListByYard(bay_id, first_row, second_row);

            }
            if (data.status == 2) {
                toastr.success('Batch Move Successfully.');
                getRowList(bay_id);
                batchListByYard(bay_id, first_row, second_row);
            }
            if (data.status == 3) {
                toastr.error(data.message);
            }
            if (data.status == 4) {
                var b_name = '';
                for (var i = 0; i < data.data.length; i++) {
                    if (b_name == '') {
                        b_name = data.data[i].batch_name;
                    } else {
                        b_name = b_name + ',' + data.data[i].batch_name;
                    }
                }
                toastr.error(b_name + ' Move mentioned Batches from Source Location..');
            }
            if (data.status == 6) {
                toastr.error('This Batch Not Able to move the above Position...');
            }

        },
        error: function(data){
            if (data.status == 500) {
                toastr.error('Internal Server Error.');
            }
        }
    })
});

$('#batch_dispatch').click(function() {
    var bay_id = $("#t1_bay_name").val();
    let selected_row = $("#selected_row_no").val();
    let first_row = $("#selected_row_no").val();;
    let second_row = $("#selected_row_no2").val();
    if ($("#current_select").val() === "R1") {
        selected_row = $("#selected_row_no").val();

    } else {
        selected_row = $("#selected_row_no2").val();

    }

    let row_no = selected_row;//$("#t1_row_no").val();
    let dispatch_batch = $('#dispatch_batch_name').val().trim();
    $.ajax({
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'

        },

        url: base_url+"/CubeInfo/batchDispatch?batch_name=" + dispatch_batch,
        type: "GET",
        //data: JSON.stringify(formData),
        //dataType: "JSON",

        success: function(data) {
            if (data.status == 1) {
                toastr.success(data.data);
                $('#modal-batch-dispatch').modal('hide');
                batchListByYard(bay_id, first_row, second_row);
                getRowList(bay_id);

            } else if (data.status == 2) {
                toastr.error(data.data);
            }

        },
        error: function(data){
            if (data.status == 500) {
                toastr.error('Internal Server Error.');
            }
        }

    })
});
/* End Batch Move Operation */


// $('document').ready(function() {
// 	var lastScrollTop = 0;
// 	$(window).scroll(function trucenscroll(event) {
// 		var st = $(this).scrollTop();
// 		var sl = $(this).scrollLeft();
// 		if (st > lastScrollTop) {
//
// 			//Le cube tourne
// 			var p1,angle,i,tmp;
//
// 			p1	= {'x': sl - p0.x,	'y': st - p0.y },
// 				angle	= {'x': -p1.y * unit,		'y': p1.x * unit};
//
// 			for(i = 0; i < faces.length; i++)
// 			{
// 				tmp = 'rotateX(' + angle.x + 'deg)' + ' rotateY(' + angle.y + 'deg)' + styles[i];
// 				faces[i].style.transform = p + tmp;
// 				faces[i].style['-webkit-transform'] = p + tmp;
// 			}
// 		}
// 		else if(st == lastScrollTop) {
// 			//do nothing
// 			//In IE this is an important condition because there seems to be some instances where the last scrollTop is equal to the new one
// 		}
// 		else {
// 			var p1,angle,i,tmp;
// 			p1	= {'x': sl - p0.x,	'y': st - p0.y },
// 				angle	= {'x': -p1.y * unit,		'y': p1.x * unit};
//
// 			for(i = 0; i < faces.length; i++)
// 			{
// 				tmp = 'rotateX(' + angle.x + 'deg)' + ' rotateY(' + angle.y + 'deg)' + styles[i];
// 				faces[i].style.transform = p + tmp;
// 				faces[i].style['-webkit-transform'] = p + tmp;
// 			}
// 		}
// 		lastScrollTop = st;
// 	});
// });
//
//
// // END OF UNSURE PART
//
//
//
//
//
//
//
//
// init();
// //===========================================================
// //			onMouseMove
// //===========================================================
// function onMouseMove(e)
// {
// 	var p1,angle,i,tmp;
//
// 	if (! dragging) return;
//
// 	p1	= {'x': e.clientX - p0.x,	'y': e.clientY - p0.y },
// 		angle	= {'x': -p1.y * unit,		'y': p1.x * unit};
//
// 	for(i = 0; i < faces.length; i++)
// 	{
// 		tmp = 'rotateX(' + angle.x + 'deg)' + ' rotateY(' + angle.y + 'deg)' + styles[i];
// 		faces[i].style.transform = p + tmp;
// 		faces[i].style['-webkit-transform'] = p + tmp;
// 	}
// }
// //===========================================================
// //			onMouseDown
// //===========================================================
// function onMouseDown(e)
// {
// 	var element;
//
// 	onMouseUp();	// disable if dragging
//
// 	element = e.target;
// 	//if (! element.classList.contains('face')) return false;
//
// 	e.preventDefault();
// 	window.p0	= { 'x': e.clientX, 'y': e.clientY };
// 	dragging	= true;
// 	return false;
// }
// //===========================================================
// //			onMouseUp
// //===========================================================
// function onMouseUp(e)
// {
// 	var i,tmp,style;
//
// 	if (! dragging) return;
// 	dragging = false;
//
// 	for ( i = 0; i < faces.length; i++)
// 	{
// 		style = faces[i].style;
// 		tmp = style.transform || style['-webkit-transform'];
// 		styles[i] = tmp.replace('perspective(32em) ', '');
// 	}
//
// }
// //=====================================================================
// //			initializeCube
// //=====================================================================
// function initializeCube()
// {
// 	var i,tmp;
//
// 	for (i = 0; i < faces.length; i++)
// 	{
// 		if (i  < 4) tmp = 'rotateY(' + i*90 + 'deg)';
// 		if (i >= 4) tmp = 'rotateX(' + Math.pow(-1, i) * 90 + 'deg)';
// 		tmp += ' translateZ(' + side/2 + 'px)';
//
// 		faces[i].style.transform = p + tmp;
// 		faces[i].style['-webkit-transform'] = p + tmp;
// 		styles.push(tmp);
// 	}
// }
// //=====================================================================
// //			init
// //=====================================================================
// function init()
// {
// 	window.addEventListener('mousedown', onMouseDown, false);
// 	window.addEventListener('mouseup',   onMouseUp,   false);
// 	window.addEventListener('mousemove', onMouseMove, false);
//
// 	window.faces 		= document.querySelectorAll('.face');
// 	window.styles 		= new Array();
// 	window.style 		= getComputedStyle(faces[0]);
// 	window.factor 		= 3;
// 	window.side 		= parseInt(style.width.split('px')[0], 10);
// 	window.max_amount 	= factor * side;
// 	window.unit 		= 360 / max_amount;
// 	window.dragging 	= false;
// 	window.scrolling 	= false;
// 	window.p 		= 'perspective(32em)';
//
// 	initializeCube();
// }
