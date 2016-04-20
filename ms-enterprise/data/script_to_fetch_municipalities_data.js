// url:  https://no.wikipedia.org/w/index.php?title=Norges_kommuner&veaction=edit&vesection=3
var table = document.getElementById("mwSQ");
for(var i = 1; i < table.rows.length; i++){
	var mapImg = table.rows[i].cells[6].querySelector("a.image img").src;
	var logo = table.rows[i].cells[7].querySelector("a.image img").src;

	var fylke = table.rows[i].cells[3].textContent;
	var spanEle = table.rows[i].cells[4].querySelector("span");
	if(spanEle){
		table.rows[i].cells[4].removeChild(spanEle);
	}
	var innbyggerere = Number(table.rows[i].cells[4].textContent.replace(/\s/g, ""));
	var areal = (Number(table.rows[i].cells[5].textContent.replace(",",".")) * 1000).toFixed(0);
	console.log(
		table.rows[i].cells[0].textContent + "," +
		table.rows[i].cells[1].textContent + "," +
		table.rows[i].cells[2].textContent + "," +
		fylke + "," +
		innbyggerere + "," +
		areal + "," +

		mapImg + "," +
		logo + "," +

		table.rows[i].cells[8].textContent + "," +
		table.rows[i].cells[9].textContent + "," +
		table.rows[i].cells[10].textContent
	);
}