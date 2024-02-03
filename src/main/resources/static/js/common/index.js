let sortId = 0;//排序编号
let category = 0;//商品类别
let name = "null";//商品名称
let minmoney = 0;//商品最低价
let maxmoney = 5000;//商品最高价
layui.use(['form', 'slider'], function () {
    var form = layui.form
        , layer = layui.layer;
    var $ = layui.$
        , slider = layui.slider;
    //开启范围选择
    slider.render({
        elem: '#slideTest9'
        , value: [0, 5000] //初始值
        , range: true //范围选择
        , min: 0 //最小值
        , max: 30000 //最大值
        , change: function (value) {
            minmoney=value[0];
            maxmoney=value[1];
            getPage();
        }
    });
    form.on('select(sort)', function (data) {
        var indexGID = data.elem.selectedIndex;
        sortId = data.elem[indexGID].title;
        getPage();
    });
    form.on('select(category)', function (data) {
        var indexGID = data.elem.selectedIndex;
        category = data.elem[indexGID].title;
        getPage();
    });
    // 监听搜索操作
    form.on('submit(data-search-btn)', function (data) {
        var formData = data.field;
        if (formData.name!==''){
            name = formData.name;
        }
        getPage();
        return false;
    });

    // 渲染下拉框
    $.ajax({
        type: 'get',
        url: basePath+'/category/all',
        dataType: 'json',
        success: function(data) {
              $("#category").empty();
              $.each(data, function(i, cate) {
                    $("#category").append("<option selected th:title='"+cate.id+"'>"+cate.name+"</option>");
                    console.log(cate.name);
              })
              console.log("渲染开始");
              //更新渲染下拉框
              form.render();
              console.log("渲染结束");
        }
    });
});

var productList = new Vue({
    el: '#productlist',
    data() {
        return {
            productlistData: []
        }
    },
    mounted: function () {
        this.getPage();
        window.getPage = this.getPage;
        window.lookproductlistData = this.lookproductlistData;
    },
    methods: {
        //创建layui的分页
        getPage:function () {
            $.ajax({
                url: basePath + "/list-number/"+category+"/"+minmoney+"/"+maxmoney+"/"+name,
                data: "",
                contentType: "application/json;charset=UTF-8", //发送数据的格式
                type: "get",
                dataType: "json", //回调
                success: function (data) {
                    layui.use(['laypage', 'layer'], function () {
                        var laypage = layui.laypage
                            , layer = layui.layer;

                        laypage.render({
                            elem: 'layuipage'
                            , count: data.dataNumber
                            ,limit: 16
                            ,limits: [16, 32, 48]
                            , layout: ['count', 'prev', 'page', 'next']
                            , jump: function (obj) {
                                lookproductlistData(obj.curr);
                            }
                        });
                    });
                },error:function () {
                    layer.msg("系统错误", {
                        time: 1000,
                        icon: 2,
                        offset: '100px'
                    });
                }
            });
        }
        //分页查询
        , lookproductlistData:function (page) {
            var that=this;
            $.ajax({
                url: basePath + "/product-listing/"+category+"/"+page+"/"+minmoney+"/"+maxmoney+"/"+sortId+"/"+name,
                data: "",
                contentType: "application/json;charset=UTF-8", //发送数据的格式
                type: "get",
                dataType: "json", //回调
                beforeSend: function () {
                    layer.load(1, { //icon支持传入0-2
                        content: '查询中...',
                        success: function (layero) {
                            layero.find('.layui-layer-content').css({
                                'padding-top': '39px',
                                'width': '60px'
                            });
                        }
                    });
                },
                complete: function () {
                    layer.closeAll('loading');
                },
                success: function (data) {
                    that.productlistData=data.data;
                },error:function () {
                    layer.msg("系统错误", {
                        time: 1000,
                        icon: 2,
                        offset: '100px'
                    });
                }
            });
        }
    }
})