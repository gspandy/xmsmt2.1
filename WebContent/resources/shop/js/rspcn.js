var rspcn = function() {

	this.signIn = function(username, password, verify, online) {
		return window.external.signIn(username, password, verify, online);
	};

	this.signOut = function() {
		window.external.signOut();
	};

	this.getVerify = function() {
		return window.external.getVerify();
	};

	this.getToken = function(appId) {
		return window.external.getToken(appId);
	};

	this.getUserInfo = function() {
		return window.external.getUserInfo();
	};

	this.connect = function() {
		return window.external.connect();
	};

	this.disconnect = function() {
		window.external.disconnet();
	};

	this.getLastError = function() {
		return window.external.getLastError();
	};

	this.setLocalJson = function(doMain, json) {
		window.external.setLocalJson(doMain, json);
	};

	this.getLocalJson = function(doMain) {
		return window.external.getLocalJson(doMain);
	};

	this.fechTopResults = function(sql, cnt, orderby) {
		var type = factor.iDbType();
		if (type == 0) {
			sql = "select top " + cnt + " * from (" + sql + ") j order by "
					+ orderby;
		} else if (type == 1) {
			sql = "select * from ( select * from (" + sql + ") j order by "
					+ orderby + ") where ROWNUM<=" + cnt
		} else if (type == 4) {
			sql = "select * from ( select * from (" + sql + ") j order by "
					+ orderby + ") tp fetch first " + cnt + "  rows only";
		} else if (type == 5) {
			sql = "select * from (" + sql + ") j order by " + orderby
					+ " limit " + cnt;
		} else {
			sql = "select * from (" + sql + ") j order by " + orderby;
		}
		return sql;
	};

	this.parseSQL = function(sql) {
		return window.external.parseSQL(sql);
	};
	
	this.getIniInfo = function(FileName,Section,ParamName,Default) {
		return window.external.getIniInfo(FileName,Section,ParamName,Default);
	};

};

var dataset = function() {

	var ds = undefined;

	this.getDataSet = function() {
		return ds;
	};

	this.createDataSet = function() {
		ds = window.external.createDataSet();
	};

	this.eraseDataSet = function() {
		window.external.eraseDataSet(ds);
		ds = undefined;
	};

	this.setSQL = function(SQL) {
		window.external.setSQL(ds, SQL);
	};

	this.append = function() {
		window.external.append(ds);
	};

	this.edit = function() {
		window.external.edit(ds);
	};

	this.post = function() {
		window.external.post(ds);
	};

	this.getAsString = function(fieldname) {
		return window.external.getAsString(ds, fieldname);
	};

	this.getAsInteger = function(fieldname) {
		return window.external.getAsInteger(ds, fieldname);
	};

	this.getAsValue = function(fieldname) {
		return window.external.getAsValue(ds, fieldname);
	};

	this.getAsDouble = function(fieldname) {
		return window.external.getAsDouble(ds, fieldname);
	};

	this.setAsString = function(fieldname, val) {
		window.external.setAsString(ds, fieldname, val);
	};

	this.setAsInteger = function(fieldname, val) {
		window.external.setAsInteger(ds, fieldname, val);
	};

	this.setAsValue = function(fieldname, val) {
		window.external.setAsValue(ds, fieldname, val);
	};

	this.setAsDouble = function(fieldname, val) {
		window.external.setAsDouble(ds, fieldname, val);
	};

	this.first = function() {
		window.external.first(ds);
	};

	this.last = function() {
		window.external.last(ds);
	};

	this.next = function() {
		window.external.next(ds);
	};

	this.prior = function() {
		window.external.prior(ds);
	};

	this.eof = function() {
		return window.external.eof(ds);
	};

	this.locate = function(fieldname, value) {
		return window.external.locate(ds, fieldname, value);
	};

	this.getLastError = function() {
		return window.external.getLastError();
	};

};

var dbfactor = function() {

	this.open = function(ds, ns, params) {
		window.external.open(ds.getDataSet(), ns, params);
	};

	this.beginBatch = function() {
		window.external.beginbatch();
	};

	this.addBatch = function(ds, ns, params) {
		window.external.addbatch(ds.getDataSet(), ns, params);
	};

	this.openBatch = function() {
		window.external.openBatch();
	};

	this.commitBatch = function() {
		window.external.commitBatch();
	};

	this.cancelBatch = function() {
		window.external.cancelBatch();
	};

	this.updateBatch = function(ds, ns, params) {
		window.external.updatebatch(ds.getDataSet(), ns, params);
	};

	this.execSQL = function(sql) {
		return window.external.execSQL(sql);
	};

	this.iDbType = function() {
		return window.external.iDbType();
	};

	this.moveToRemote = function() {
		window.external.moveToRemote();
	};

	this.moveToSqlite = function() {
		window.external.moveToSqlite();
	};

	this.getLastError = function() {
		return window.external.getLastError();
	};

};

var rsp = new rspcn();
var factor = new dbfactor();