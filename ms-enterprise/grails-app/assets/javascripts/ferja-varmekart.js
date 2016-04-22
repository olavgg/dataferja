
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

});



var range = function(start, end, step) {
	var range = [];
	var typeofStart = typeof start;
	var typeofEnd = typeof end;

	if (step === 0) {
		throw TypeError("Step cannot be zero.");
	}

	if (typeofStart == "undefined" || typeofEnd == "undefined") {
		throw TypeError("Must pass start and end arguments.");
	} else if (typeofStart != typeofEnd) {
		throw TypeError("Start and end arguments must be of same type.");
	}

	typeof step == "undefined" && (step = 1);

	if (end < start) {
		step = -step;
	}

	if (typeofStart == "number") {

		while (step > 0 ? end >= start : end <= start) {
			range.push(start);
			start += step;
		}

	} else if (typeofStart == "string") {

		if (start.length != 1 || end.length != 1) {
			throw TypeError("Only strings with one character are supported.");
		}

		start = start.charCodeAt(0);
		end = end.charCodeAt(0);

		while (step > 0 ? end >= start : end <= start) {
			range.push(String.fromCharCode(start));
			start += step;
		}

	} else {
		throw TypeError("Only string and number types are supported");
	}

	return range;

};