/*
  Created by Terry Brady, Georgetown University Library
  This code is an optional add-on for DSpace.
  https://github.com/DSpace-Labs/DSpace-REST-Reports
*/
var SchemaReport = function() {
	Report.call(this);
	var self = this;

	this.init = function() {
	    this.baseInit();	
	}
	
	this.initMetadataFields = function() {
		this.myMetadataFields = new MyMetadataFields(self);
	    this.myMetadataFields.load();		
	}
}
SchemaReport.prototype = Object.create(Report.prototype);

$(document).ready(function(){
	var myReport=new SchemaReport();
	myReport.init();
});

var MyMetadataFields = function(report) {
	MetadataFields.call(this, report);
    var self = this;
	
	this.initFields = function(data, report) {
		self.metadataSchemas = data;
		
		var md = $("#metadatadiv");
		md.empty().append($("<h2>Schema Registry</h2>"));
		var table = $("<table/>");
		md.append(table);
		var tr = report.myHtmlUtil.addTr(table);
		report.myHtmlUtil.addTh(tr,"Shema Namespace").addClass("reg");
		report.myHtmlUtil.addTh(tr,"Shema Prefix");
		$.each(data, function(i, ischema){
			var tr = report.myHtmlUtil.addTr(table);
			report.myHtmlUtil.addTd(tr,ischema.namespace);
			report.myHtmlUtil.addTd(tr,ischema.prefix);			
		});
		md.append($("<h2>Metadata Registry</h2>"));
		table = $("<table/>");
		md.append(table);
		var tr = report.myHtmlUtil.addTr(table);
		report.myHtmlUtil.addTh(tr,"Field Name").addClass("reg");
		report.myHtmlUtil.addTh(tr,"Field Description").addClass("reg");
		$.each(data, function(i, ischema){
			$.each(ischema.fields, function(j, field){
				var tr = report.myHtmlUtil.addTr(table);
				report.myHtmlUtil.addTd(tr,field.name);
				report.myHtmlUtil.addTd(tr,field.description);							
			});
		});
		
		$("button.download").on("click", function(){
			var encodedUri = encodeURI("data:application/json;charset=utf-8,"+JSON.stringify(self.metadataSchemas));
	        window.open(encodedUri);		
		})
		
	    report.spinner.stop();
	}
	
}
MyMetadataFields.prototype = Object.create(MetadataFields.prototype);