/**
 * 
 */
function selectAll() {
	$("input[name='checkbox']").prop("checked", true);
}

function selectNone() {
	$("input[name='checkbox']").prop("checked", false);
}

function getSelectedIds() {
	var obj = document.getElementsByName('checkbox');
	var ids = '';
	for (var i = 0; i < obj.length; i++) {
		if (obj[i].checked) {
			ids += obj[i].id + ',';
		}
	}
	if (ids.Length > 0) {
		ids = ids.Substring(0, ids.Length - 1);
	}
	return ids;
}

function getSelectedNum() {
	var obj = document.getElementsByName('checkbox');
	var num = 0;
	for (var i = 0; i < obj.length; i++) {
		if (obj[i].checked) {
			num += 1;
		}
	}
	return num;
}

//删除所选
function deleteSelected() {
	var num = getSelectedNum();
	if (num > 0) {
		var ids = getSelectedIds();
		if (confirm('确定要删除选中的' + getSelectedNum() + '条纪录吗?\r\n注意：此操作不可恢复！')) {
			window.location.replace("/manage?delete=" + ids);
		}
	} else {
		alert("请勾选要删除的记录！");
	}
}

//倒计时跳转
function countDown(secs, surl) {
	var jumpTo = document.getElementById('jumpTo');
	jumpTo.innerHTML = secs;
	if (--secs > 0) {
		setTimeout("countDown(" + secs + ",'" + surl + "')", 1000);
	} else {
		location.href = surl;
	}
}


//文件上传校验
function checkFile(){
    var val = document.getElementById("file_select").value;
    if(val == ""){
        alert("请先选择要导入的文件！");
        return false;
    }
    else{
    	return confirm('确定要导入【' + val + '】的内容吗？');
    }
}

//清空文件选择
function clearFile(){
	document.getElementById("file_select").value="";
}

function cleanCache(){
	if (confirm('确定要清空缓存吗?')) {
		window.location.replace("/manage?clean=true");
	}
}