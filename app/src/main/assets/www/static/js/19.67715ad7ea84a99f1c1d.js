webpackJsonp([19],{QPlP:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=a("HzJ8"),s=a.n(i),l=a("aA9S"),n=a.n(l),o=a("JfVH"),r={data:function(){return{tableList:[],listLoading:!0,isShowEditVisible:!1,isShowAddVisible:!1,deleteVisible:!1,temp:{uid:"",cname:"",date:"",status:""},total:0,page:1,pageSize:10,farmlandId:"",status:[{statusId:1,label:"启用"},{statusId:0,label:"禁用"}],value:"",searchName:"",filterTableDataEnd:[]}},created:function(){this.fetchData()},filters:{statusFilter:function(t){return{1:"success",2:"danger"}[t]}},methods:{updateData1:function(t){var e=this;this.listLoading=!0,Object(o.a)({name:t}).then(function(t){e.fetchData()}),this.isShowAddVisible=!1,this.$notify({title:"成功",message:"添加成功",type:"success",duration:2e3})},getInShotList:function(t){this.GLOBAL.farmland=t,console.log(t),this.$router.push("/farmland")},fetchData:function(){var t=this;this.listLoading=!0,Object(o.f)(this.listQuery).then(function(e){var a=e.data.rows.filter(function(e,a){return a<10*t.page&&a>=10*(t.page-1)});t.total=e.data.total,t.tableList=a,t.listLoading=!1})},doFilter:function(){var t=this;if(""===this.searchName)return this.fetchData(),void this.$message.warning("查询条件不能为空！");console.log(this.searchName),this.listLoading=!0,Object(o.f)({name:this.searchName}).then(function(e){var a=e.data.rows.filter(function(e,a){return a<10*t.page&&a>=10*(t.page-1)});t.total=e.data.total,t.tableList=a,t.listLoading=!1})},clickfun:function(t){console.log(t.target.innerText)},handleUpdate:function(t){this.isShowEditVisible=!0,this.temp=n()({},t),console.log(t)},handleUpdate1:function(){this.isShowAddVisible=!0,console.log()},deleteUpdate:function(t){console.log(t),this.deleteVisible=!0,this.temp=n()({},t),this.farmlandId=t.farmlandId},submitDelete:function(){var t=this,e=n()({},this.temp);console.log(e),console.log(this.tableList);var a=!0,i=!1,l=void 0;try{for(var r,c=s()(this.tableList);!(a=(r=c.next()).done);a=!0){var d=r.value;if(d.uid===this.temp.uid){var u=this.tableList.indexOf(d);this.tableList.splice(u,1),this.fetchData(),console.log(this.tableList);break}}}catch(t){i=!0,l=t}finally{try{!a&&c.return&&c.return()}finally{if(i)throw l}}Object(o.c)({farmlandId:this.farmlandId}).then(function(e){t.fetchData()}),this.deleteVisible=!1,this.$notify({title:"成功",message:"删除成功",type:"success",duration:2e3})},handleModifyStatus:function(t,e){this.$message({message:"操作成功",type:"success"}),console.log(t),t.status=e},updateData:function(){var t=this,e=n()({},this.temp);console.log(e),updateArticle(e).then(function(){var e=!0,a=!1,i=void 0;try{for(var l,n=s()(t.tableList);!(e=(l=n.next()).done);e=!0){var o=l.value;if(o.uid===t.temp.uid){var r=t.tableList.indexOf(o);t.tableList.splice(r,1,t.temp);break}}}catch(t){a=!0,i=t}finally{try{!e&&n.return&&n.return()}finally{if(a)throw i}}t.isShowEditVisible=!1,t.$notify({title:"成功",message:"更新成功",type:"success",duration:2e3})})},handleSizeChange:function(t){this.page=t,console.log(this.page),this.fetchData()},handleCurrentChange:function(t){this.page=t,console.log(this.page),this.fetchData()},currentChangePage:function(t){var e=(this.page-1)*this.pageSize,a=this.page*this.pageSize;for(this.tableList=[];e<a;e++)t[e]&&this.tableList.push(t[e])}}},c={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"app-container"},[a("el-col",{staticClass:"toolbar",staticStyle:{"padding-bottom":"0px"},attrs:{span:24}},[a("el-form",{attrs:{inline:!0}},[a("el-form-item",[a("el-input",{attrs:{placeholder:"大田名称"},model:{value:t.searchName,callback:function(e){t.searchName=e},expression:"searchName"}})],1),t._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:function(e){t.doFilter()}}},[a("i",{staticClass:"el-icon-search"}),t._v("搜索")])],1),t._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:function(e){t.handleUpdate1()}}},[t._v("新增")])],1)],1)],1),t._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.listLoading,expression:"listLoading"}],staticStyle:{width:"100%"},attrs:{data:t.tableList,"max-height":"550",border:"","element-loading-text":"拼命加载中"}},[a("el-table-column",{attrs:{prop:"farmlandId",label:"大田序号"}}),t._v(" "),a("el-table-column",{attrs:{prop:"name",label:"大田名称"}}),t._v(" "),a("el-table-column",{attrs:{prop:"operation",label:"操作 "},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-button",{attrs:{size:"mini",type:"success"},on:{click:function(a){t.getInShotList(e.row)}}},[t._v("\n          进入\n        ")]),t._v(" "),a("el-button",{attrs:{size:"small",type:"danger"},on:{click:function(a){t.deleteUpdate(e.row)}}},[t._v("删除")])]}}])})],1),t._v(" "),a("el-pagination",{staticStyle:{"text-align":"center"},attrs:{layout:"total, prev, pager, next",background:"","page-size":10,total:t.total},on:{"size-change":t.handleSizeChange,"current-change":t.handleCurrentChange}}),t._v(" "),a("el-dialog",{attrs:{title:"Edit",visible:t.isShowEditVisible},on:{"update:visible":function(e){t.isShowEditVisible=e}}},[a("el-form",{ref:"dataForm",attrs:{"label-width":"80px",model:t.temp}},[a("el-form-item",{attrs:{label:"姓名",prop:"cname"}},[a("el-input",{model:{value:t.temp.cname,callback:function(e){t.$set(t.temp,"cname",e)},expression:"temp.cname"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"时间",prop:"date"}},[a("el-input",{model:{value:t.temp.date,callback:function(e){t.$set(t.temp,"date",e)},expression:"temp.date"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"状态"},model:{value:t.temp.status,callback:function(e){t.$set(t.temp,"status",e)},expression:"temp.status"}},[a("el-select",{attrs:{placeholder:"启用状态"},model:{value:t.temp.status,callback:function(e){t.$set(t.temp,"status",e)},expression:"temp.status"}},t._l(t.status,function(t){return a("el-option",{key:t.statusId,attrs:{label:t.label,value:t.statusId}})}))],1)],1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.isShowEditVisible=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{staticClass:"title1",attrs:{type:"primary",loading:t.listLoading},on:{click:t.updateData}},[t._v("确定")])],1)],1),t._v(" "),a("el-dialog",{attrs:{title:"Edit1",visible:t.isShowAddVisible},on:{"update:visible":function(e){t.isShowAddVisible=e}}},[a("el-form",{ref:"dataForm",attrs:{"label-width":"80px",model:t.farmName}},[a("el-form-item",{attrs:{label:"大田名字",prop:"fname"}},[a("el-input",{model:{value:t.farmName,callback:function(e){t.farmName=e},expression:"farmName"}})],1)],1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.isShowAddVisible=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{staticClass:"title1",attrs:{type:"primary",loading:t.listLoading},on:{click:function(e){t.updateData1(t.farmName)}}},[t._v("确定")])],1)],1),t._v(" "),a("el-dialog",{attrs:{title:"删除",visible:t.deleteVisible,width:"30%"},on:{"update:visible":function(e){t.deleteVisible=e}}},[a("span",[t._v("确认删除吗")]),t._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.deleteVisible=!1}}},[t._v("取 消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:t.submitDelete}},[t._v("确 定")])],1)])],1)},staticRenderFns:[]},d=a("/Xao")(r,c,!1,null,null,null);e.default=d.exports}});