/**
 * 
 */
//启动
function wakeupAll() {
	location.href="?wakeup=true";
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