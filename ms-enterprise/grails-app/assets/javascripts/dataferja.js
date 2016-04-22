//Dataferja custom JS

document.addEventListener("DOMContentLoaded", function(event) {



	new um.QueryField({
		ele: document.getElementById("search-municipalities"),
		queryUrl: "/search/municipality",
		cbFunction: function(result){
			um.getMunicipality(result.id, function(obj){
				handleSelectedMunicipality(obj);
			});
		}
	});

	new um.QueryField({
		ele: document.getElementById("search-attributes"),
		queryUrl: "/search/attributes",
		cbFunction: function (obj) {
			handleSelectedAttribute(obj);
			um.asyncFormSubmit(obj.event.target, {
				complete: function(response){
					var result = JSON.parse(response.responseText);
					renderDataTable(result);
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

	function handleSelectedAttribute(obj){
		var chosenEle = document.getElementById("chosen-attributes");
		var values = chosenEle.querySelectorAll("input[type='hidden'");
		var addAttr = true;
		for(var i = 0; i < values.length; i++){
			if(values[i].value == obj.id){
				addAttr = false;
				break;
			}
		}
		if(addAttr){
			var ele = document.createElement("SPAN");
			ele.className = "chosen-attr fleft";
			ele.textContent = obj.text;
			ele.addEventListener('click', removeChosenItem);
			ele.setAttribute('data-id', obj.id);

			var inputEle = document.createElement("INPUT");
			inputEle.name = "attr_id";
			inputEle.value = obj.id;
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





});