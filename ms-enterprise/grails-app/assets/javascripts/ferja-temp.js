
document.addEventListener("DOMContentLoaded", function(event) {
	(function (Highcharts) {
		var seriesTypes = Highcharts.seriesTypes,
			each = Highcharts.each;
		seriesTypes.heatmap = Highcharts.extendClass(seriesTypes.map, {
			translate: function () {
				var series = this,
					options = series.options,
					dataMin = Number.MAX_VALUE,
					dataMax = Number.MIN_VALUE,
					opacity,
					minOpacity = options.minOpacity,
					path,
					color;
				series.generatePoints();
				each(series.data, function (point) {
					point.path = [
						'M', point.col - 0.5, point.row - 0.5,
						'L', point.col + 0.5, point.row - 0.5,
						'L', point.col + 0.5, point.row + 0.5,
						'L', point.col - 0.5, point.row + 0.5,
						'Z'
					];
					point.shapeType = 'path';
					point.shapeArgs = {
						d: series.translatePath(point.path)
					};
					if (typeof point.y === 'number') {
						if (point.y > dataMax) {
							dataMax = point.y;
						} else if (point.y < dataMin) {
							dataMin = point.y;
						}
					}
				});
				series.translateColors(dataMin, dataMax);
			},
			getBox: function () {
			}
		});
	}(Highcharts));


	var chart = new Highcharts.Chart({
		chart: {
			renderTo: 'heat-container2',
			type: 'heatmap'
		},
		title: {
			text: 'Variabelbasert varmekart'
		},
		xAxis: {
			categories: ['Apples', 'Pears', 'Oranges'],
			min: 0,
			max: 2
		},
		yAxis: {
			categories: ['Gert', 'Torstein', 'Anne Jorunn', 'Grethe', 'Hilde', 'Guro'],
			min: -0.50,
			max: 5.5,
			minPadding: 0,
			maxPadding: 0,
			startOnTick: false,
			endOnTick: false
		},
		tooltip: {
			formatter: function () {
				return this.series.yAxis.categories[this.point.row] + ': <b>' +
					this.y + ' ' +
					this.series.xAxis.categories[this.point.col].toLowerCase() + '</b>';
			}
		},
		legend: {
			valueDecimals: 0
		},
		series: [{
			borderWidth: 0,
			data: [
				// Gert
				{row: 0, col: 0, y: 112},
				{row: 0, col: 1, y: 234},
				{row: 0, col: 2, y: 190},
				// Torstein
				{row: 1, col: 0, y: 232},
				{row: 1, col: 1, y: 234},
				{row: 1, col: 2, y: 164},
				// Anne Jorunn
				{row: 2, col: 0, y: 345},
				{row: 2, col: 1, y: 136},
				{row: 2, col: 2, y: 323},
				// Grethe
				{row: 3, col: 0, y: 13},
				{row: 3, col: 1, y: 16},
				{row: 3, col: 2, y: 2},
				// Hilde
				{row: 4, col: 0, y: 244},
				{row: 4, col: 1, y: 223},
				{row: 4, col: 2, y: 154},
				// Guro
				{row: 5, col: 0, y: 323},
				{row: 5, col: 1, y: 19},
				{row: 5, col: 2, y: 33}
			],
			// Color ranges for the legend
			valueRanges: [{
				to: 99,
				color: 'green'
			}, {
				from: 100,
				to: 199,
				color: 'red'
			}, {
				from: 180,
				to: 200,
				color: 'purple'
			},	{
				from: 200,
				color: 'yellow'
			}]
		}]
	});
});