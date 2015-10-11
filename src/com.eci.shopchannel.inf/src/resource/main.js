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
	return ids==''?'':ids.substring(0,ids.length-1);;
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
			location.href="/manage?delete=" + ids;
		}
	} else {
		alert('请勾选要删除的记录！');
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
    if(val == ''){
        alert('请先选择要导入的文件！');
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
		location.href="/manage?cleanCache=true";
	}
}

function setStatus(i) {
	var num = getSelectedNum();
	var str = i == 1 ? '启用' : '停用';
	if (num > 0) {
		var ids = getSelectedIds();
		if (confirm('确定要' + str + '选中的' + num + '条纪录吗?')) {
			location.href="/manage?status=" + (i==1?1:0) + "&ids=" + ids;
		}
	} else {
		alert("请勾选要" + str + "的记录！");
	}
}

function editItem(){
	var num = getSelectedNum();
	if(num<1){
		alert("请勾选要修改的记录！");
	}else if(num>1){
		alert("只能修改1条记录，目前勾选了"+num+"条！");
	}else{
		location.href="/manage?edit=true&id="+getSelectedIds();
	}
}

function setSaveType(value){
	document.getElementById("saveType").value=value;
}

function checkItem(){

	
}