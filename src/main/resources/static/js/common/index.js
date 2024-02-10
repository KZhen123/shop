let name = "null";//商品名称
layui.use(['form', 'slider'], function () {
    var form = layui.form
        , layer = layui.layer;
    var $ = layui.$

    // 监听搜索操作
    form.on('submit(data-search-btn)', function (data) {
        var formData = data.field;
        if (formData.name!==''){
            name = formData.name;
        }
        getPage();
        return false;
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
                url: basePath + "/list-number/"+"/"+name,
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
                url: basePath + "/product-listing/"+page+"/"+name,
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