webpackJsonp([26],{"5iXv":function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var l=a("HzJ8"),i=a.n(l),s=a("aA9S"),n=a.n(s),o=a("JfVH"),r={data:function(){return{tableList:[],listLoading:!0,isShowEditVisible:!1,deleteVisible:!1,isShowAddVisible:!1,temp:{uid:"",cname:"",date:"",status:""},farmName:"",total:0,page:1,pageSize:10,status:[{statusId:1,label:"启用"},{statusId:0,label:"禁用"}],value:"",startYear:"",endYear:"",searchName:"",filterTableDataEnd:[],nowYear:(new Date).getFullYear(),years:[]}},created:function(){for(var t=0;t<=20;t++)this.years.push(this.nowYear-t);this.fetchData()},filters:{statusFilter:function(t){return{1:"success",2:"danger"}[t]}},methods:{updateData1:function(t){var e=this;this.listLoading=!0,Object(o.a)({name:t}).then(function(t){e.fetchData()})},getInFieldList:function(t){this.GLOBAL.shot=t,this.$router.push("/shot")},fetchData:function(){var t=this;this.listLoading=!0,Object(o.j)({farmlandId:this.GLOBAL.farmland.farmlandId}).then(function(e){var a=e.data.rows.filter(function(e,a){return a<10*t.page&&a>=10*(t.page-1)});t.total=e.data.total,t.tableList=a,t.listLoading=!1})},doFilter:function(){var t=this;""!==this.searchName?(console.log(this.searchName),this.filterTableDataEnd=[],this.tableList.forEach(function(e,a){e.cname&&e.cname.indexOf(t.searchName)>=0&&(t.filterTableDataEnd.push(e),console.log(t.filterTableDataEnd))}),this.page=1,this.total=this.filterTableDataEnd.length,this.currentChangePage(this.filterTableDataEnd)):this.fetchData()},clickfun:function(t){console.log(t.target.innerText)},handleUpdate:function(t){this.isShowEditVisible=!0,this.temp=n()({},t),console.log(t)},handle1Update:function(){this.isShowAddVisible=!0,console.log()},deleteUpdate:function(t){console.log(t),this.deleteVisible=!0,this.temp=n()({},t)},submitDelete:function(){var t=n()({},this.temp);console.log(t),console.log(this.tableList);var e=!0,a=!1,l=void 0;try{for(var s,o=i()(this.tableList);!(e=(s=o.next()).done);e=!0){var r=s.value;if(r.uid===this.temp.uid){this.tableList.indexOf(r);this.tableList.splice(fieldList,1),this.fetchData(),console.log(this.tableList);break}}}catch(t){a=!0,l=t}finally{try{!e&&o.return&&o.return()}finally{if(a)throw l}}this.deleteVisible=!1,this.$notify({title:"成功",message:"删除成功",type:"success",duration:2e3})},handleModifyStatus:function(t,e){this.$message({message:"操作成功",type:"success"}),console.log(t),t.status=e},updateData:function(){var t=this,e=n()({},this.temp);console.log(e),updateArticle(e).then(function(){var e=!0,a=!1,l=void 0;try{for(var s,n=i()(t.tableList);!(e=(s=n.next()).done);e=!0){var o=s.value;if(o.uid===t.temp.uid){t.tableList.indexOf(o);t.tableList.splice(fieldList,1,t.temp);break}}}catch(t){a=!0,l=t}finally{try{!e&&n.return&&n.return()}finally{if(a)throw l}}t.isShowEditVisible=!1,t.$notify({title:"成功",message:"更新成功",type:"success",duration:2e3})})},handleSizeChange:function(t){this.page=t,console.log(this.page),this.fetchData()},handleCurrentChange:function(t){this.page=t,console.log(this.page),this.fetchData()},currentChangePage:function(t){var e=(this.page-1)*this.pageSize,a=this.page*this.pageSize;for(this.tableList=[];e<a;e++)t[e]&&this.tableList.push(t[e])}}},c={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"app-container"},[a("el-col",{staticClass:"toolbar",staticStyle:{"padding-bottom":"0px"},attrs:{span:24}},[a("el-form",{attrs:{inline:!0}},[a("el-form-item",{staticStyle:{"max-width":"110px"}},[a("el-select",{attrs:{clearable:"",placeholder:"起始年份"},model:{value:t.startYear,callback:function(e){t.startYear=e},expression:"startYear"}},t._l(t.years,function(t){return a("el-option",{key:t,attrs:{label:t,value:t}})}))],1),t._v(" "),a("el-form-item",{staticStyle:{"max-width":"110px"}},[a("el-select",{attrs:{clearable:"",placeholder:"结束年份"},model:{value:t.endYear,callback:function(e){t.endYear=e},expression:"endYear"}},t._l(t.years,function(t){return a("el-option",{key:t,attrs:{label:t,value:t}})}))],1),t._v(" "),a("el-form-item",[a("el-input",{attrs:{placeholder:"姓名"},model:{value:t.searchName,callback:function(e){t.searchName=e},expression:"searchName"}})],1),t._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:function(e){t.doFilter()}}},[a("i",{staticClass:"el-icon-search"}),t._v("搜索")])],1),t._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:function(e){t.handle1Update()}}},[t._v("新增")])],1)],1)],1),t._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.listLoading,expression:"listLoading"}],staticStyle:{width:"100%"},attrs:{data:t.tableList,border:"","element-loading-text":"拼命加载中"}},[a("el-table-column",{attrs:{prop:"shotId",label:"航拍序号"}}),t._v(" "),a("el-table-column",{attrs:{prop:"farmlandId",label:"大田序号"}}),t._v(" "),a("el-table-column",{attrs:{prop:"year",label:"年份五十六个民资"}}),t._v(" "),a("el-table-column",{attrs:{prop:"imgUrl",label:"图片地址"},scopedSlots:t._u([{key:"default",fn:function(t){return[a("img",{staticStyle:{width:"50px",height:"50px"},attrs:{src:t.row.imgUrl,alt:""}})]}}])}),t._v(" "),a("el-table-column",{attrs:{prop:"wwww",label:"年BCDE"}}),t._v(" "),a("el-table-column",{attrs:{prop:"operation",label:"操作 "},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-button",{attrs:{size:"small",type:"primary"},on:{click:function(a){t.handleUpdate(e.row)}}},[t._v("编辑")]),t._v(" "),"2"!=e.row.status?a("el-button",{attrs:{size:"mini",type:"success"},on:{click:function(a){t.getInFieldList(e.row)}}},[t._v("\n          进入\n        ")]):t._e(),t._v(" "),"1"!=e.row.status?a("el-button",{attrs:{size:"mini"},on:{click:function(a){t.handleModifyStatus(e.row,"1")}}},[t._v("禁用\n        ")]):t._e(),t._v(" "),a("el-button",{attrs:{size:"small",type:"danger"},on:{click:function(a){t.deleteUpdate(e.row)}}},[t._v("删除")])]}}])})],1),t._v(" "),a("el-pagination",{staticStyle:{"text-align":"center"},attrs:{layout:"total, prev, pager, next",background:"","page-size":10,total:t.total},on:{"size-change":t.handleSizeChange,"current-change":t.handleCurrentChange}}),t._v(" "),a("el-dialog",{attrs:{title:"Edit",visible:t.isShowEditVisible},on:{"update:visible":function(e){t.isShowEditVisible=e}}},[a("el-form",{ref:"dataForm",attrs:{"label-width":"80px",model:t.temp}},[a("el-form-item",{attrs:{label:"姓名",prop:"cname"}},[a("el-input",{model:{value:t.temp.cname,callback:function(e){t.$set(t.temp,"cname",e)},expression:"temp.cname"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"时间",prop:"date"}},[a("el-input",{model:{value:t.temp.date,callback:function(e){t.$set(t.temp,"date",e)},expression:"temp.date"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"状态"},model:{value:t.temp.status,callback:function(e){t.$set(t.temp,"status",e)},expression:"temp.status"}},[a("el-select",{attrs:{placeholder:"启用状态"},model:{value:t.temp.status,callback:function(e){t.$set(t.temp,"status",e)},expression:"temp.status"}},t._l(t.status,function(t){return a("el-option",{key:t.statusId,attrs:{label:t.label,value:t.statusId}})}))],1)],1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.isShowEditVisible=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{staticClass:"title1",attrs:{type:"primary",loading:t.listLoading},on:{click:t.updateData}},[t._v("确定")])],1)],1),t._v(" "),a("el-dialog",{attrs:{title:"Edit1",visible:t.isShowAddVisible},on:{"update:visible":function(e){t.isShowAddVisible=e}}},[a("el-form",{ref:"dataForm",attrs:{"label-width":"80px",model:t.farmName}},[a("el-form-item",{attrs:{label:"大田名字",prop:"fname"}},[a("el-input",{model:{value:t.farmName,callback:function(e){t.farmName=e},expression:"farmName"}})],1)],1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.isShowAddVisible=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{staticClass:"title1",attrs:{type:"primary",loading:t.listLoading},on:{click:function(e){t.updateData1(t.farmName)}}},[t._v("确定")])],1)],1),t._v(" "),a("el-dialog",{attrs:{title:"删除",visible:t.deleteVisible,width:"30%"},on:{"update:visible":function(e){t.deleteVisible=e}}},[a("span",[t._v("确认删除吗")]),t._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.deleteVisible=!1}}},[t._v("取 消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:t.submitDelete}},[t._v("确 定")])],1)]),t._v(" "),a("el-dialog",{attrs:{title:"新增",visible:t.deleteVisible,width:"30%"},on:{"update:visible":function(e){t.deleteVisible=e}}},[a("span",[t._v("确认添加吗")]),t._v(" "),a("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.deleteVisible=!1}}},[t._v("取 消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:t.submitDelete}},[t._v("确 定")])],1)])],1)},staticRenderFns:[]},d=a("/Xao")(r,c,!1,null,null,null);e.default=d.exports}});