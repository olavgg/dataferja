//Dataferja custom JS

document.addEventListener("DOMContentLoaded", function(event) {


	new um.QueryField({
		ele: document.getElementById("search-municipalities"),
		queryUrl: "/search/municipality",
		cbFunction: function(result){
			for(var i = 0; i < result.ids.length; i++){
				um.getMunicipality(result.ids[i], function(obj){
					handleSelectedMunicipality(obj);
				});
			}

	new um.QueryField({
		ele: document.getElementById("search-attributes"),
		queryUrl: "/search/attributes",
		cbFunction: function (obj) {
			for(var i = 0; i < obj.ids.length; i++) {
				handleSelectedAttribute(obj.ids[i], obj.texts[i]);
			}
			um.asyncFormSubmit(obj.event.target, {
				complete: function(response){
					var result = JSON.parse(response.responseText);
					renderDataTable(result);
					renderHeatMap(result);
					//Remove messy legend
					//$('.highcharts-legend').hide();
					//$('.highcharts-axis').hide();
				}
			});
		}
	});

	um.domLoaded.escapeBtnEvent = function () {
		document.onkeydown = function (e) {
			if (e.keyCode === 27) {
				var elems = document.querySelectorAll(".sm-complex-search");

				[].forEach.call(elems, function(el) {
					el.classList.add("hidden");
				});
			}
		}
	};

	function handleSelectedMunicipality(obj){
		var chosenEle = document.getElementById("chosen-municipalities");
		var values = chosenEle.querySelectorAll("input[type='hidden'");
		var addMuniciaplity = true;
		for(var i = 0; i < values.length; i++){
			if(values[i].value == obj.id){
				addMuniciaplity = false;
				break;
			}
		}
		if(addMuniciaplity){
			var imgEle = document.createElement("IMG");
			imgEle.src = "/p/" + obj.image;
			imgEle.alt = obj.name;
			imgEle.title = obj.name;
			imgEle.className = "chosen-m";
			imgEle.addEventListener('click', removeChosenItem);
			imgEle.setAttribute('data-id', obj.id);

			var inputEle = document.createElement("INPUT");
			inputEle.name = "municipality_id";
			inputEle.value = obj.id;
			inputEle.type = "hidden";

			chosenEle.appendChild(imgEle);
			chosenEle.appendChild(inputEle);
		}
	}

	function removeChosenItem(e){
		var chosenEle = e.target.parentNode;
		var values = chosenEle.querySelectorAll("input[type='hidden'");

		for(var i = 0; i < values.length; i++){
			if(values[i].value == e.target.getAttribute("data-id")){
				chosenEle.removeChild(values[i]);
				chosenEle.removeChild(e.target);
				break;
			}
		}
	}

	function handleSelectedAttribute(objId, objText){
		var chosenEle = document.getElementById("chosen-attributes");
		var values = chosenEle.querySelectorAll("input[type='hidden'");
		var addAttr = true;
		for(var i = 0; i < values.length; i++){
			if(values[i].value == objId){
				addAttr = false;
				break;
			}
		}
		if(addAttr){
			var ele = document.createElement("SPAN");
			ele.className = "chosen-attr fleft";
			ele.textContent = objText;
			ele.addEventListener('click', removeChosenItem);
			ele.setAttribute('data-id', objId);

			var inputEle = document.createElement("INPUT");
			inputEle.name = "attr_id";
			inputEle.value = objId;
			inputEle.type = "hidden";

			chosenEle.appendChild(ele);
			chosenEle.appendChild(inputEle);
		}
	}

	function renderDataTable(obj){
		var tableEle = document.createElement('TABLE');
		var tHeadEle = tableEle.createTHead();
		var thRow = tHeadEle.insertRow();
		// Create Municipality Cell
		var cell = document.createElement('TH');
		cell.textContent = "Kommune";
		thRow.appendChild(cell);

		// Add Column Headers
		for(var i = 0; i < obj.headers.length; i++){
			cell = document.createElement('TH');
			cell.textContent = obj.headers[i].text;
			cell.setAttribute('data-label-id', obj.headers[i].id);
			thRow.appendChild(cell);
		}
		// Add empty header cell
		cell = document.createElement('TH');
		thRow.appendChild(cell);

		// Add rows
		var tBodyEle = tableEle.createTBody();
		var rows = [];
		for(var i = 0; i < obj.rows.length; i++){
			var row = tBodyEle.insertRow();
			rows.push(row);
			for(var y = 0; y < obj.rows[i].length; y++){
				cell = document.createElement('TD');
				cell.textContent = obj.rows[i][y].text;
				if(y == 0){
					row.setAttribute('data-muni-id', obj.rows[i][y].id);
				}
				row.appendChild(cell);
			}
			// Add empty cell
			cell = document.createElement('TD');
			row.appendChild(cell);
		}


		var container = document.getElementById('datatable');
		container.innerHTML = "";
		container.appendChild(tableEle);
	}

	var navLinks = $('.navLink');
	var contentElements = $('.ferry-container');

	navLinks.on('click', function (e) {

		//set menu
		navLinks.removeClass('selected');

		$(e.currentTarget).addClass('selected');
		var navId = $(e.currentTarget).attr('id');

		//set content
		contentElements.hide();
		switch (navId) {
			case "menu1":
				$('#datakiosk').show();
				break;
			case "menu2":
				$('#heatmap').show();
				break;
			case "menu3":
				$('#barchart').show();
				break;
			case "menu4":
				$('#machineroom').show();
				break;
		}
	});

	function renderHeatMap(obj){

		//#X-axis : Variables
		var headerTitles = [];
		for(var i = 0; i < obj.headers.length; i++){
			headerTitles.push(obj.headers[i].text);
		}
		console.log(headerTitles);

		//#X-axis : Variable Meta
		headerTitlesLength = obj.headers.length;
		if (headerTitlesLength == 1){
			headerTitlesLength=1;
			console.log(headerTitlesLength);
		}

		//#Y-axis : Municipalities
		var rowTitles = [];
		for(var i = 0; i < obj.rows.length; i++){
			rowTitles.push(obj.rows[i][0].text);
		}
		console.log(rowTitles);

		// Municipality meta:

		yAxisMax = obj.rows.length-0.500;
		yAxisMin = -0.500;

		// Data values:
		var tableValues = [];

		console.log(obj.rows);
		values_only = []

		for(var i = 0; i < obj.rows.length; i++) {
			for(var y = 1; y < obj.rows[i].length; y++) {
				console.log(y + " " + i + " " + obj.rows[i][y].text);
				tableValues.push({col: y-1, row: i, y: obj.rows[i][y].text });
				values_only.push(obj.rows[i][y].text);
				console.log(values_only);
			}
		}

		var minValue = 0;
		var maxValue = 0;
		var ranges;

		var minValue = Math.min.apply(Math, values_only);
		var maxValue = Math.max.apply(Math, values_only);

		console.log("min: "+minValue);
		console.log("max: "+maxValue);

		//Valueranges for color:

		ranges = range(minValue,maxValue,(maxValue/10.00));
		colors = ['white', '#f6fef5', '#ecfceb', '#e3fbe1',
			'#d9fad6', '#d0f8cc', '#c6f7c2', '#bcf5b8',
			'#b1f3ae', '#a7f2a4', '#9cf09a', '#90ee90'];

		var colormap = [];
		for(var i=0; i < ranges.length; i++) {
			if((ranges[i] < 0) || (ranges[i+1] < 0)){
				colormap.push({ from: ranges[i], to:ranges[i+1], color: 'red'})
			}
			else {
				colormap.push({ from: ranges[i], to:ranges[i+1], color: colors[i]})
			}
		}

		console.log(colormap);



		console.log(ranges);


		console.log(tableValues);

		var chart = new Highcharts.Chart({
			chart: {
				renderTo: 'heat-container',
				type: 'heatmap'
			},
			title: {
				text: ''

			},
			xAxis: {
				categories: headerTitles,
				min: 0,
				max: headerTitlesLength-1
			},
			yAxis: {
				categories: rowTitles,
				min: -0.50,
				max: yAxisMax, //Dynamic ferje-spesial to stack rows upwards
				minPadding: 0,
				maxPadding: 0,
				startOnTick: false,
				endOnTick: false
			},
			tooltip: {
				formatter: function () {
					return this.series.yAxis.categories[this.point.row] + ': <b>' +
						this.y + ' ' +
						this.series.xAxis.categories[this.point.col] + '</b>';
				}
			},
			legend: {
				valueDecimals: 0
			},
			series: [{
				borderWidth: 0,
				data: tableValues,
				// Color ranges for the legend


				valueRanges: colormap

			}]
		});





	}





});



//[{
//	to: ranges[0],
//	color: colors[0]
//}, {
//	from: ranges[0],
//	to: ranges[1],
//	color: colors[1]
//}, {
//	from: ranges[1],
//	to: ranges[2],
//	color: colors[2]
//}, {
//	from: ranges[2],
//	to: ranges[3],
//	color: colors[3]
//}, {
//	from: ranges[3],
//	to: ranges[4],
//	color: colors[4]
//}, {
//	from: ranges[4],
//	to: ranges[5],
//	color: colors[5]
//}, {
//	from: ranges[5],
//	to: ranges[6],
//	color: colors[6]
//}, {
//	from: ranges[6],
//	to: ranges[7],
//	color: colors[7]
//}, {
//	from: ranges[7],
//	to: ranges[8],
//	color: colors[8]
//}, {
//	from: ranges[8],
//	to: ranges[9],
//	color: colors[9]
//}, {
//	from: ranges[9],
//	to: ranges[10],
//	color: colors[10]
//},  {
//	from: ranges[11],
//	color: colors[11]
//}]