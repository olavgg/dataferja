<!doctype html>
<html>
<head>
	<title><g:message code="app.title"/></title>
	<meta name="layout" content="dataferja">
</head>
<body>

<g:form controller="fetch" action="values" method="POST" name="municipality_search">

	<div class="box-dialog">
		<div class="w50 fleft">
			<ferja:querySelector
					btnId="search-municipalities"
					chosenId="chosen-municipalities"
					labelText="${message(code: 'municipalities')}"
					selectBtnText="${message(code: 'choose.municipality')}"
					searchPlaceHolderText="${message(code: 'search.placeholder')}"
					searchBtnText="${message(code: 'search.btn')}"/>

			<ferja:querySelector
					btnId="search-attributes"
					chosenId="chosen-attributes"
					labelText="${message(code: 'attributes')}"
					selectBtnText="${message(code: 'choose.attribute')}"
					searchPlaceHolderText="${message(code: 'search.placeholder')}"
					searchBtnText="${message(code: 'search.btn')}"/>

			<button id="fetchBtn"
					class="blue"
					type="button">
					<span class="text"><g:message code="fetch.data" /></span>
					<i class="demo-icon icon-spin5 btn-spinner"></i>
			</button>
		</div>
		<div class="fclear"></div>
	</div>

	<div id="datatable">

	</div>

</g:form>
<script type="application/javascript">

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
			cbFunction: handleSelectedAttribute
		});

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
			for(var i = 0; i < obj.headers.length; i++){
				cell = document.createElement('TH');
				cell.textContent = obj.headers[i].text;
				cell.setAttribute('data-label-id', obj.headers[i].id);
				thRow.appendChild(cell);
			}

			var tBodyEle = tableEle.createTBody();
			var rows = [];
			for(var i = 0; i < obj.rows.length; i++){
				var row = tBodyEle.insertRow();
				rows.push(row);
				cell = document.createElement('TD');
				cell.textContent = obj.rows[i].text;
				row.setAttribute('data-muni-id', obj.rows[i].id);
				row.appendChild(cell);
			}

			for(var i = 0; i < obj.values.length; i++){
				console.log(obj.values);
			}

			document.getElementById('datatable').appendChild(tableEle);
		}

		var fetchBtn = document.getElementById("fetchBtn");
		fetchBtn.addEventListener('click', function(e){
			um.asyncFormSubmit(e.target, {
				complete: function(response){
					var result = JSON.parse(response.responseText);
					renderDataTable(result);
				}
			});
		})
	});
</script>
</body>
</html>