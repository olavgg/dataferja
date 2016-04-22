<div class="row">
	<label class="w25 fleft">${labelText}</label>
	<div id="${chosenId}" class="select-wrapper w75 fleft"></div>
</div>

<div class="row">
	<div class="w25 fleft"></div>
	<div class="select-wrapper w75 fleft">
		<button id="${btnId}"
				type="button" class="dialog-btn w100 sm-pad">
			<span>
				<i class="demo-icon icon-search"></i>
				${selectBtnText}
			</span>
			<span class="caret"></span>
		</button>

		<div class="sm-complex-search box-dialog mwrapper hidden">
			<div class="input-group w100">
				<input class="sm-pad" placeholder="${searchPlaceHolderText}"
					   type="search" value="">
			</div>
		</div>
	</div>
</div>