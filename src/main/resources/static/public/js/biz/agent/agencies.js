$(function () {
    $("#jqGrid").jqGrid({
        url: '../../agencies/list',
        datatype: "json",
        colModel: [			
			{ label: 'ID', name: 'id', index: 'id', width: 50, key: true },
			{ label: '用户名', name: 'username', index: 'username', width: 120 }, 			
			{ label: '邮箱', name: 'email', index: 'email', width: 120 },
			{ label: '合同开始时间', name: 'contractStartdt', index: 'contract_startDt', width: 100 }, 			
			{ label: '合同结束时间', name: 'contractEnddt', index: 'contract_endDt', width: 100 },
			//{ label: '地域', name: 'region', index: 'region', width: 80 }, 			
			{ label: '区域', name: 'address', index: 'address', width: 150 }, 			
			{ label: '类型', name: 'type', index: 'type', width: 80 },
			{ label: '角色', name: 'agencyrole', index: 'agencyrole', width: 80 },
			{ label: '性质', name: 'type', index: 'type', width: 80 },
			{ label: '企业名称', name: 'name', index: 'name', width: 80 }, 			
			{ label: '最后登录ip', name: 'sysLogList', index: 'sysLogList' ,formatter: function(value, options, row){
				return value.length>0?value[0].ip:"";
			}},
			{ label: '最后登录时间', name: 'sysLogList', index: 'sysLogList' ,formatter: function(value, options, row){
				return value.length>0?value[0].create_date:"";
			}},
			{ label: '访问次数', name: 'sysLogList', index: 'sysLogList' ,formatter: function(value, options, row){
				return value.length>0?value.length:0;
			}},
			{ label: '父级ID', name: 'parent', index: 'parent', width: 80 }, 			
			{ label: '创建时间', name: 'createDt', index: 'create_dt', width: 80 }, 			
			{ label: '修改时间', name: 'updateDt', index: 'update_dt', width: 80 }, 			
			{ label: '创建人', name: 'createBy', index: 'create_by', width: 80 }, 			
			{ label: '修改人', name: 'updateBy', index: 'update_by', width: 80 }, 			
			{ label: '代理商类型', name: 'agencytype', index: 'agencytype', width: 80 }, 			
			{ label: '营业执照注册号', name: 'businessNo', index: 'business_no', width: 80 }, 			
			{ label: '营业执照副本', name: 'businessImg', index: 'business_img', width: 80 }, 			
			{ label: '开户银行', name: 'openBank', index: 'open_bank', width: 80 }, 			
			{ label: '开户名称', name: 'bankname', index: 'bankname', width: 80 }, 			
			{ label: '银行卡号', name: 'bankCardno', index: 'bank_cardno', width: 80 }, 			
			{ label: '法人姓名', name: 'legaluserName', index: 'legaluser_name', width: 80 }, 			
			{ label: '法人身份证号', name: 'legaluserNo', index: 'legaluser_no', width: 80 }, 			
			{ label: '法人身份证扫描件', name: 'legaluserImg', index: 'legaluser_img', width: 80 }, 			
			{ label: '推广二维码', name: 'qrcode', index: 'qrcode', width: 80 },
			{ label: '状态', name: 'status', index: 'status', width: 80 }, 
			{ label: '通过审核时间', name: 'examineDt', index: 'examine_dt', width: 80 }			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        shrinkToFit:false, 
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	//$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		status:null,
		agencies: {}
	},
	mounted: function(){
		var bodywidth = $(document.body).width();
		$(".grid-btn").css("width",bodywidth);
	},
	methods: {
		query: function () {
			$("#jqGrid").jqGrid('setGridParam',{page:1});
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.status = "add";
			vm.agencies = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            vm.status = "edit";
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
			var url = vm.agencies.id == null ? "../../agencies/save" : "../../agencies/update";
			$.ajax({
				type: "POST",
			    url: url,
			    contentType: "application/json",
			    data: JSON.stringify(vm.agencies),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: "../../agencies/delete",
				    contentType: "application/json",
				    data: JSON.stringify(ids),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		getInfo: function(id){
			$.get("../../agencies/info/"+id, function(r){
                vm.agencies = r.agencies;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});