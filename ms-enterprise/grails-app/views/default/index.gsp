<!doctype html>
<html>
<head>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
	<script src="http://code.highcharts.com/highcharts.js"></script>
	<script src="http://github.highcharts.com/v3.0.2/modules/map.src.js"></script>
	<asset:javascript src="dataferja.js"/>
	<asset:javascript src="ferja-varmekart.js"/>
	%{--<asset:javascript src="ferja-temp.js"/>--}%
	<title>Dataferja</title>
	<meta name="layout" content="dataferja">
</head>
<body>
	<header>
		<div class="app-title-container" style="text-align:center">
			<asset:image src="dataferja.png"/>
			<h1>M/S Kommuneferjå</h1>
		</div>
	</header>
	<div class="menu">
		<ul class="navList">
			<li id="menu1" class="navLink selected">
				<a><g:message code="menu.dataset" />
				</a>
			</li>
			<li id="menu2" class="navLink">
				<a><g:message code="menu.heatmap" /></a>
			</li>
			<li id="menu3" class="navLink">
				<a><g:message code="menu.linechart" /></a>
			</li>
			<li id="menu4" class="navLink">
				<a><g:message code="menu.import" /></a>
			</li>
		</ul>
	</div>

<g:form controller="fetch" action="values" method="POST" name="municipality_search">
	<div class="container">
		<div id="datakiosk" class="ferry-container">
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

					<!--
					<button id="fetchBtn"
							class="blue"
							type="button">
							<span class="text"><g:message code="fetch.data" /></span>
							<i class="demo-icon icon-spin5 btn-spinner"></i>
					</button> -->
				</div>
				<div class="fclear"></div>
			</div>
			<div id="datatable"></div>
		</div>

		<div id="heatmap" class="ferry-container" style="display:none">
			<div id="heat-container" style="height: 300px; width:50%;"></div>
		</div>
		<div id="barchart" class="ferry-container" style="display:none">
			<h5>barchart goes here</h5>
		</div>
		<div id="machineroom" class="ferry-container" style="display:none">
			<h5>Maskinrom</h5>
		</div>


	</div>
</g:form>





</body>
</html>