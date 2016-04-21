'use strict';
var um = {};
um.components = {};
um.globals = {};
um.findFirstParentWithClass = function (classname, element) {
	if (element.classList.contains(classname)) {
		return element;
	}
	return um.findFirstParentWithClass(classname, element.parentNode);
};
um.getForm = function (element) {
	return um.findFirstParentWithTagname('FORM', element);
};
um.findFirstParentWithTagname = function (tagName, element) {
	if (element.tagName === tagName) {
		return element;
	}
	return um.findFirstParentWithTagname(tagName, element.parentNode);
};
um.findFirstChildWithTagname = function (tagName, element) {
	var children = element.children;
	for (var i = 0; i < children.length; i++) {
		if (children[i].tagName === tagName) {
			return children[i];
		}
		var result = um.findFirstChildWithTagname(tagName, children[i]);
		if (result) {
			return result;
		}
	}
	return null;
};
um.hasClassName = function (target, className) {
	return new RegExp('(\\s|^)' + className + '(\\s|$)').test(target.className);
};
um.clean = function (node) {
	for (var n = 0; n < node.childNodes.length; n++) {
		var child = node.childNodes[n];
		if (child.nodeType === 8 ||
			(child.nodeType === 3 && !/\S/.test(child.nodeValue))) {
			node.removeChild(child);
			n--;
		}
		else if (child.nodeType === 1) {
			um.clean(child);
		}
	}
};
um.formSubmit = function (element) {
	um.showLoading(element);
	um.getForm(element).submit();
	return false;
};
um.showLoading = function (element) {
	if (element.tagName === "BUTTON") {
		element.setAttribute("data-loading", "");
		element.querySelector(".btn-spinner").classList.add("show");
		element.disabled = true;
	}
};
um.stopLoading = function (element) {
	if (element.tagName === "BUTTON") {
		element.removeAttribute("data-loading");
		element.querySelector(".btn-spinner").classList.remove("show");
		element.disabled = false;
	}
};
um.asyncFormSubmit = function (element, object) {
	um.showLoading(element);
	var form = um.getForm(element);
	var requestData = {};
	requestData.data = new FormData(form);
	requestData.url = object.url || form.action;
	requestData.method = object.method || form.method;
	requestData.sync = false;
	var completeFunction = object.complete || function (response) {
		};
	requestData.complete = function (response) {
		um.stopLoading(element);
		completeFunction(response);
	};
	um.ajax(requestData);
	return false;
};

um.loadComponent = function (obj) {
	var componentId = SipHash.hash_hex(
		SipHash.string16_to_key("0123456789ABCDEF"), obj.src);
	var s_element = document.getElementById(componentId);
	if (!s_element) {
		um.ajax({
			url: obj.src,
			complete: function (response) {
				var contentType = response.getResponseHeader('content-type');
				contentType = contentType.split(";")[0];
				if (contentType === "application/javascript") {
					s_element = document.createElement('script');
				}
				else if (contentType === "text/css") {
					s_element = document.createElement('style');
				}
				if (s_element) {
					s_element.id = componentId;
					s_element.type = contentType;
					s_element.textContent = response.responseText;
					document.head.appendChild(s_element);
				}
				if (obj.complete != null) {
					obj.complete();
				}
			}
		});
	} else {
		if (obj.complete != null) {
			obj.complete();
		}
	}
};
um.loadComponents = function (sources, cb, value) {
	if (value == undefined) {
		value = 0;
	}
	if (value == sources.length - 1) {
		um.loadComponent({src: sources[value], type: 'script', complete: cb});
	} else {
		um.loadComponent({
			src: sources[value],
			type: 'script',
			complete: function () {
				um.loadComponents(sources, cb, value + 1);
			}
		});
	}
};
um.ajax = function (object) {
	object.method = (object.method || 'GET').toUpperCase();
	object.data = object.data || new FormData();
	var request = new XMLHttpRequest();
	request.open(object.method, object.url, true);
	if (object.headers) {
		var objKeys = Object.keys(object.headers);
		for (var i = 0; i < objKeys.length; i++) {
			request.setRequestHeader(objKeys[i], object.headers[objKeys[i]]);
		}
	}
	request.onerror = function () {
		console.log('REQUEST ERROR:');
		console.log('HTTP CODE:' + request.status);
		console.log(request.responseText);
	};
	request.onload = function () {
		if (request.status >= 400) {
			request.onerror();
		}
		if (object.complete instanceof Function) {
			object.complete(request);
		}
	};
	request.send(object.data);
};

um.showFileValue = function (ele) {
	var fileValueElement;
	for (var i = 0; i < ele.parentNode.children.length; i++) {
		if (ele.parentNode.children[i].classList.contains('fileValue')) {
			fileValueElement = ele.parentNode.children[i];
		}
	}
	if (!fileValueElement) {
		fileValueElement = document.createElement("DIV");
		fileValueElement.className = "fileValue";

		if (ele.files && ele.files[0]) {
			var reader = new FileReader();
			reader.onload = function (e) {
				var imgEle = document.createElement("IMG");
				imgEle.src = e.target.result;
				imgEle.style.width = "100%";
				fileValueElement.appendChild(imgEle);
			};
			reader.readAsDataURL(ele.files[0]);
		}

		ele.parentNode.appendChild(fileValueElement);
		ele.parentNode.style.display = "block";
	}
	fileValueElement.textContent = ele.value;
};

um.clearFlashMessages = function () {
	var flashMessages = document.querySelectorAll("div.flash");
	for (var i = 0; i < flashMessages.length; i++) {
		flashMessages[i].parentNode.removeChild(flashMessages[i]);
	}
};
um.renderFlashMessage = function (type, message) {
	um.clearFlashMessages();
	var flashMessage = document.createElement('DIV');
	flashMessage.className = "flash " + type;
	var icon = document.createElement('I');
	switch (type) {
		case 'notice':
			icon.className = "demo-icon icon-info-circled";
			break;
		case 'warning':
			icon.className = 'demo-icon icon-attention';
			break;
		case 'error':
			icon.className = 'demo-icon icon-block';
			break;
		case 'success':
			icon.className = 'demo-icon icon-ok';
			break;
	}
	flashMessage.appendChild(icon);
	var messageElement = document.createElement('SPAN');
	messageElement.textContent = message;
	flashMessage.appendChild(messageElement);
	var closeIcon = document.createElement('I');
	closeIcon.className = "demo-icon icon-cancel";
	closeIcon.onclick = function () {
		um.clearFlashMessages();
	};
	flashMessage.appendChild(closeIcon);
	var containerEle = document.getElementById("pagebody");
	containerEle.insertBefore(flashMessage, containerEle.firstChild);
};
um.renderFlashErrorMessages = function (response) {
	um.clearFlashMessages();
	var result = JSON.parse(response.responseText);
	var flashMessage = document.createElement('DIV');
	flashMessage.className = "flash error";
	var icon = document.createElement('I');
	icon.className = 'fa fa-ban';
	flashMessage.appendChild(icon);
	var messageList = document.createElement('UL');
	var errorHeader = document.createElement('LI');
	errorHeader.textContent = result.message;
	messageList.appendChild(errorHeader);
	if (result.fields) {
		var fields = Object.keys(result.fields);
		for (var i = 0; i < fields.length; i++) {
			var messageItem = document.createElement('LI');
			messageItem.textContent = result.fields[fields[i]];
			messageList.appendChild(messageItem);
			var inputElement = document.getElementById(fields[y]);
			if (inputElement) {
				inputElement.parentNode.classList.add("error");
			}
		}
	}
	flashMessage.appendChild(messageList);
	var closeIcon = document.createElement('I');
	closeIcon.className = "fa fa-times";
	closeIcon.onclick = function () {
		um.clearFlashMessages();
	};
	flashMessage.appendChild(closeIcon);
	var containerEle = document.getElementById('layout-container');
	containerEle.insertBefore(flashMessage, containerEle.firstChild);
};
um.hideElements = function (elements) {
	for (var y = 0; y < elements.length; y++) {
		if (!elements[y].classList.contains('hidden')) {
			elements[y].classList.add('hidden');
		}
	}
};

um.setSelectAmongSiblings = function (ele, className) {
	var siblings = ele.parentNode.children;
	for (var i = 0; i < siblings.length; i++) {
		if (siblings[i].classList.contains(className) && siblings[i] !== ele) {
			siblings[i].classList.remove(className);
		} else if (siblings[i] === ele && !ele.classList.contains(className)) {
			siblings[i].classList.add(className);
		}
	}
};
um.getMunicipality = function (id, cb) {
	var formData = new FormData();
	formData.append("id", id);
	um.ajax({
		method: 'POST',
		data: formData,
		url: "/municipality/show",
		headers: {
			"Accept": "application/json"
		},
		complete: function (req) {
			cb(JSON.parse(req.responseText));
		}
	});
};

um.QueryField = function (object) {
	this.mainBtn = object.ele || null;
	this.queryUrl = object.queryUrl || '';
	this.minLength = object.minQueryLength || 2;
	this.cbFunc = object.cbFunction || function () {
		};
	this.queryElement = this.mainBtn.parentNode.querySelector(".sm-complex-search");
	this.inputElement = this.queryElement.querySelector('input');
	this.timer(this);
	this.mainBtn.addEventListener('click', this.showQueryField.bind(this));
	this.queryElement.addEventListener('keydown', this.handleKeyDown.bind(this));
};

um.QueryField.prototype.showQueryField = function (e) {
	var searchElements = document.querySelectorAll(".sm-complex-search");
	for (var i = 0; i < searchElements.length; i++) {
		if (!searchElements[i].classList.contains('hidden')) {
			searchElements[i].classList.add('hidden');
		}
	}
	this.queryElement.classList.remove('hidden');
	this.inputElement.focus();
};

um.QueryField.prototype.timer = function (self) {
	var typingTimer = null;
	var doneTyping = 300;
	self.inputElement.addEventListener('input', function (e) {
		clearTimeout(typingTimer);
		typingTimer = setTimeout(self.executeQuery.bind(self, e), doneTyping);
	});
};

um.QueryField.prototype.handleKeyDown = function (e) {
	var selectableItems = this.getSelectableItems();
	var selectedIndex = -1;

	for (var i = 0; i < selectableItems.length; i++) {
		if (selectableItems[i].classList.contains("selected")) {
			selectedIndex = i;
			break;
		}
	}

	if (e.keyCode == 38) { // key arrow up
		if (selectedIndex > 0) {
			selectableItems[selectedIndex].classList.remove("selected");
			selectableItems[selectedIndex - 1].classList.add("selected");
			selectableItems[selectedIndex].parentNode.scrollTop =
				(selectableItems[selectedIndex - 1].scrollHeight * (selectedIndex - 1));
		}
	} else if (e.keyCode == 40) { // key arrow down
		if (selectedIndex < selectableItems.length - 1) {
			selectableItems[selectedIndex].classList.remove("selected");
			selectableItems[selectedIndex + 1].classList.add("selected");
			selectableItems[selectedIndex].parentNode.scrollTop =
				(selectableItems[selectedIndex + 1].scrollHeight * selectedIndex);
		}
	} else if (e.keyCode == 13) { // key return
		for (var i = 0; i < selectableItems.length; i++) {
			if (selectableItems[i].classList.contains("selected")) {
				this.queryElement.classList.add('hidden');
				this.cbFunc({
					id: selectableItems[i].getAttribute('data-id'),
					text: selectableItems[i].textContent,
					event: e
				});
			}
		}
		e.preventDefault();
	}
};

um.QueryField.prototype.getSelectableItems = function () {
	var ul = this.queryElement.querySelector("ul.results");
	if (!ul) {
		return [];
	}
	return ul.querySelectorAll('.selectable-result');
};

um.QueryField.prototype.handleMouse = function () {
	var selectableItems = this.getSelectableItems();
	for (var i = 0; i < selectableItems.length; i++) {
		selectableItems[i].addEventListener('mouseover', function (e) {
			for (var y = 0; y < selectableItems.length; y++) {
				if (selectableItems[y] !== this &&
					selectableItems[y].classList.contains('selected')) {
					selectableItems[y].classList.remove('selected');
				}
			}
			this.classList.add('selected');
		});
		selectableItems[i].addEventListener('click', function (e) {
			this.queryElement.classList.add('hidden');
			this.cbFunc({
				id: e.target.getAttribute('data-id'),
				text: e.target.textContent,
				event: e
			});
		}.bind(this));
	}
};

um.QueryField.prototype.executeQuery = function (e) {
	if (this.inputElement.value.length < this.minLength) {
		this.clearResults();
		return;
	}
	var formData = new FormData();
	formData.append("query", this.inputElement.value);
	var self = this;
	um.ajax({
		method: 'POST',
		data: formData,
		url: this.queryUrl,
		headers: {
			"Accept": "application/json"
		},
		complete: function (request) {
			self.renderQueryResults(request);
		}
	});
};
um.QueryField.prototype.clearResults = function () {
	var listEle = this.queryElement.querySelector("ul.results");
	if (listEle) {
		listEle.parentNode.removeChild(listEle);
	}
};

um.QueryField.prototype.renderQueryResults = function (req) {
	var result = JSON.parse(req.responseText);
	if (result.total === 0) {
		return;
	}
	var parentGroups = [];
	this.clearResults();
	var listEle = document.createElement('UL');
	listEle.className = "results";
	for (var i = 0; i < result.hits.length; i++) {
		if (parentGroups.indexOf(result.hits[i].parent_id) == -1) {
			parentGroups.push(result.hits[i].parent_id);
			var parentLabel = document.createElement('LI');
			parentLabel.className = "parent-label";
			parentLabel.textContent = result.hits[i].parent_text;
			listEle.appendChild(parentLabel);
		}
		var listItem = document.createElement('LI');
		listItem.className = "selectable-result";
		listItem.textContent = result.hits[i].text;
		listItem.setAttribute('data-id', result.hits[i].id);
		if (i === 0) {
			listItem.classList.add("selected");
		}
		listEle.appendChild(listItem);
	}
	this.queryElement.appendChild(listEle);
	this.handleMouse();
};

um.domLoaded = {};
um.domLoaded.escapeBtnEvent = function () {
	document.onkeydown = function (e) {
		if (e.keyCode === 27) {
			console.log("user pressed esc button");
		}
	}
};
um.domLoaded.addLoaderEventForButtons = function () {
	var buttons = document.querySelectorAll("button.submit_button");
	for (var i = 0; i < buttons.length; i++) {
		if (buttons[i].type !== "button" && buttons[i].onclick == null) {
			buttons[i].onclick = function (e) {
				var self = this;
				this.setAttribute("data-loading", "");
				setTimeout(function () {
					self.removeAttribute('data-loading');
					document.querySelector(".btn-spinner").style.opacity = 0;
					self.disabled = false;
				}, 10000);
				document.querySelector(".btn-spinner").style.opacity = 1;
				this.disabled = true;
				if (!self.classList.contains("no-form-submit")) {
					um.getForm(this).submit();
				}
			}
		}
	}
};
um.domLoaded.addCloseFunctionForFlashMessages = function () {
	var buttons = document.querySelectorAll(".flash i.fa-times-circle");
	for (var i = 0; i < buttons.length; i++) {
		buttons[i].onclick = function (e) {
			this.parentNode.parentNode.removeChild(this.parentNode);
		}
	}
};

um.domLoaded.simpleFormFocusEvent = function () {
	var inputSelector = ".simple-form input, " +
		".simple-form select, .simple-form textarea";
	var elements = document.querySelectorAll(inputSelector);
	for (var i = 0; i < elements.length; i++) {
		elements[i].onfocus = function (event) {
			this.parentNode.classList.add("focused");
		};
		elements[i].onblur = function () {
			this.parentNode.classList.remove("focused");
		};
	}
};


um.domLoaded.defaultInit = function () {
	um.globals.fileElements = document.querySelectorAll("input[type=file]");
	document.addEventListener("dragover", function (event) {
		// prevent default to allow drop
		event.preventDefault();
		for (var i = 0; i < um.globals.fileElements.length; i++) {
			um.globals.fileElements[i].parentNode.classList.add("highlight")
		}
	}, false);

	document.addEventListener("drop", function (event) {
		for (var i = 0; i < um.globals.fileElements.length; i++) {
			um.globals.fileElements[i].parentNode.classList.remove("highlight");
		}
	}, false);

	document.addEventListener("dragleave", function (event) {
		for (var i = 0; i < um.globals.fileElements.length; i++) {
			um.globals.fileElements[i].parentNode.classList.remove("highlight");
		}
	}, false);
};
document.addEventListener("DOMContentLoaded", function (event) {
	var properties = Object.keys(um.domLoaded);
	for (var i = 0; i < properties.length; i++) {
		um.domLoaded[properties[i]]();
	}
	// Remove unneccesary whitespaces caused by indented html code
	um.clean(document);
});

var SipHash = function () {
	function t(h, r) {
		var n = h.l + r.l, t = {
			h: h.h + r.h + (n / 2 >>> 31) >>> 0,
			l: n >>> 0
		};
		h.h = t.h, h.l = t.l
	}

	function h(h, r) {
		h.h ^= r.h, h.h >>>= 0, h.l ^= r.l, h.l >>>= 0
	}

	function l(h, r) {
		var n = {h: h.h << r | h.l >>> 32 - r, l: h.l << r | h.h >>> 32 - r};
		h.h = n.h, h.l = n.l
	}

	function u(h) {
		var r = h.l;
		h.l = h.h, h.h = r
	}

	function r(o, r, e, n) {
		t(o, r), t(e, n), l(r, 13), l(n, 16), h(r, o), h(n, e), u(o), t(e, r), t(o, n), l(r, 17), l(n, 21), h(r, e), h(n, o), u(e)
	}

	function n(h, r) {
		return h.charCodeAt(r + 3) << 24 | h.charCodeAt(r + 2) << 16 | h.charCodeAt(r + 1) << 8 | h.charCodeAt(r)
	}

	function o(d, s) {
		var i, f, c, v = {h: d[1] >>> 0, l: d[0] >>> 0}, A = {
			h: d[3] >>> 0,
			l: d[2] >>> 0
		}, t = {h: v.h, l: v.l}, u = v, e = {
			h: A.h,
			l: A.l
		}, l = A, a = 0, C = s.length, g = C - 7, o = new Uint8Array(new ArrayBuffer(8));
		for (h(t, {h: 1936682341, l: 1886610805}), h(e, {
			h: 1685025377,
			l: 1852075885
		}), h(u, {h: 1819895653, l: 1852142177}), h(l, {
			h: 1952801890,
			l: 2037671283
		}); g > a;)i = {
			h: n(s, a + 4),
			l: n(s, a)
		}, h(l, i), r(t, e, u, l), r(t, e, u, l), h(t, i), a += 8;
		for (o[7] = C, f = 0; C > a;)o[f++] = s.charCodeAt(a++);
		for (; 7 > f;)o[f++] = 0;
		return i = {
			h: o[7] << 24 | o[6] << 16 | o[5] << 8 | o[4],
			l: o[3] << 24 | o[2] << 16 | o[1] << 8 | o[0]
		}, h(l, i), r(t, e, u, l), r(t, e, u, l), h(t, i), h(u, {
			h: 0,
			l: 255
		}), r(t, e, u, l), r(t, e, u, l), r(t, e, u, l), r(t, e, u, l), c = t, h(c, e), h(c, u), h(c, l), c
	}

	function e(h) {
		return [n(h, 0), n(h, 4), n(h, 8), n(h, 12)]
	}

	function a(r, n) {
		var h = o(r, n);
		return ("0000000" + h.h.toString(16)).substr(-8) + ("0000000" + h.l.toString(16)).substr(-8)
	}

	function i(r, n) {
		var h = o(r, n);
		return 4294967296 * (2097151 & h.h) + h.l
	}

	return {string16_to_key: e, hash: o, hash_hex: a, hash_uint: i}
}(), module = module || {}, exports = module.exports = SipHash;

// Proper decimal rounding from Mozilla
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/round

(function () {
	/**
	 * Decimal adjustment of a number.
	 *
	 * @param {String}  type  The type of adjustment.
	 * @param {Number}  value The number.
	 * @param {Integer} exp   The exponent (the 10 logarithm of the adjustment base).
	 * @returns {Number} The adjusted value.
	 */
	function decimalAdjust(type, value, exp) {
		// If the exp is undefined or zero...
		if (typeof exp === 'undefined' || +exp === 0) {
			return Math[type](value);
		}
		value = +value;
		exp = +exp;
		// If the value is not a number or the exp is not an integer...
		if (isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0)) {
			return NaN;
		}
		// Shift
		value = value.toString().split('e');
		value = Math[type](+(value[0] + 'e' + (value[1] ? (+value[1] - exp) : -exp)));
		// Shift back
		value = value.toString().split('e');
		return +(value[0] + 'e' + (value[1] ? (+value[1] + exp) : exp));
	}

	// Decimal round
	if (!Math.round10) {
		Math.round10 = function (value, exp) {
			return decimalAdjust('round', value, exp);
		};
	}
	// Decimal floor
	if (!Math.floor10) {
		Math.floor10 = function (value, exp) {
			return decimalAdjust('floor', value, exp);
		};
	}
	// Decimal ceil
	if (!Math.ceil10) {
		Math.ceil10 = function (value, exp) {
			return decimalAdjust('ceil', value, exp);
		};
	}
})();

function nFormatter(num) {
	var isNegative = num < 0;
	num = Math.abs(num);
	if (num >= 1000000000) {

		var formattedNumber =
			(num / 1000000000).toFixed(1).replace(/\.0$/, '') + 'G';

	} else if (num >= 1000000) {

		formattedNumber =
			(num / 1000000).toFixed(1).replace(/\.0$/, '') + 'M';

	} else if (num >= 1000) {

		formattedNumber = (num / 1000).toFixed(1).replace(/\.0$/, '') + 'k';

	} else {
		formattedNumber = num;
	}
	if (isNegative) {
		formattedNumber = '-' + formattedNumber
	}
	return formattedNumber;
}
