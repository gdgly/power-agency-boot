$(function () {
    $("#jqGrid").jqGrid({
        url: '../userInfo/list',
        datatype: "json",
        postData:vm.q,
        colModel: [			
			{ label: '用户Id', name: 'id', index: 'id', width: 40, key: true },
			{ label: '昵称', name: 'nickname', index: 'nickname', width: 80 },
			{ label: 'openId', name: 'openId', index: 'openId', width: 160 },
			{ label: '头像', name: 'headimgurl', index: 'headimgurl', width: 50 ,formatter: function(value, options, row){
				var imgStr = "<img src='"+value+"' height='20'/>";
				return imgStr;
			}},
			{ label: '是否关注', name: 'status', index: 'status', width: 40 ,formatter: function(value, options, row){
				return value==1?"是":"否";
			}},
			{ label: '性别', name: 'sex', index: 'sex', width: 40,formatter: function(value, options, row){
				return value!=null?getDict(vm.sex)[value]:"";
			}}, 			
			{ label: '国家', name: 'country', index: 'country', width: 40 },
			{ label: '省份', name: 'province', index: 'province', width: 40 },		
			{ label: '城市', name: 'city', index: 'city', width: 60 },
			{ label: '公众号', name: 'unionId', index: 'unionId', width: 80 ,formatter: function(value, options, row){
				return value!=null?getDict(vm.platform)[value]:"";
			}}, 			
			{ label: '首次关注途径', name: 'channel', index: 'channel', width: 80,formatter: function(value, options, row){
				return value!=null?getDict(vm.channel)[value]:"";
			}}, 			
			{ label: '首次直接推荐人', name: 'direct_recommender', index: 'direct_recommender', width: 80}, 			
			{ label: '首次间接推荐人', name: 'indirect_recommender', index: 'indirect_recommender', width: 80 }, 			
			{ label: '首次关注时间', name: 'createTime', index: 'createTime', width: 100,formatter: function(value, options, row){
				return  value!=undefined?vm.parseDate(value):"";
			}},
			{ label: '最近关注时间', name: 'city', index: 'city', width:100 },
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
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
        beforeRequest:function(e){
        	if(vm.channel.length==0){
        		$.ajax({
        			type: "POST",
        			url: "../dict/CHANNEL",
        			async: false,
        			success: function(r){
        				if(r.code === 0){
        					vm.channel = r.data;
        				}else{alert(r.msg);}
        			}
        		});
        	}
        	if(vm.sex.length==0){
        		$.ajax({
        			type: "POST",
        			url: "../dict/SEX",
        			async: false,
        			success: function(r){
        				if(r.code === 0){
        					vm.sex = r.data;
        				}else{alert(r.msg);}
        			}
        		});
        	}
        	if(vm.platform.length==0){
        		$.ajax({
        			type: "POST",
        			url: "../dict/queryPlatform",
        			async: false,
        			success: function(r){
        				if(r.code === 0){
        					vm.platform = r.data;
        				}else{alert(r.msg);}
        			}
        		});
        	}
        	
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			userId:"",agencyId:"",openId:""
		},
		showList: true,
		title: null,
		status:null,
		channel:[],
		sex:[],
		platform:[]
	},
	methods: {
		query: function () {
			$("#jqGrid").jqGrid('setGridParam',{page:1});
			vm.reload();
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
				postData:vm.q,
                page:page
            }).trigger("reloadGrid");
		   },	
		parseDate: function(value){
			if(value==null){
				return "";
			}else{
				return new Date(parseInt(value)*1000).format("yyyy-MM-dd hh:mm:ss");
			}
		}
	}
});