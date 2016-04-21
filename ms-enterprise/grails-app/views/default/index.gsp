<!doctype html>
<html>
<head>
	<asset:javascript src="dataferja.js"/>
	<title>Dataferja</title>
	<meta name="layout" content="dataferja">
</head>
<body>
	<header>
		<div class="app-title-container" style="text-align:center">
			<asset:image src="dataferja.png"/>
			<h1>dataferja</h1>
		</div>
	</header>


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

	<div id="datatable">

	</div>

</g:form>



</body>
</html>