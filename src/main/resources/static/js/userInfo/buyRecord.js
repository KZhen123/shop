layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer','table'], function () {
    var table = layui.table;
    table.render({
        elem: '#sold'
        , url: basePath+'/order/selectBuyOrder'
        , page: {
            layout: ['limit', 'count', 'prev', 'page', 'next', 'skip']
            , groups: 3
            , limits: [10, 20, 50, 100]
            , limit: 10
        }, cols: [[
            {field: 'orderId', title: '订单号',width:180, align:'center'}
            , {field: 'commodityName', title: '商品名称', width: 280, align:'center'}
            , {field: 'commodityDesc', title: '商品描述', width: 280, align:'center'}
            , {field: 'price', title: '售价', width: 90, align:'center'}
            , {field: 'time', title: '成交时间', width: 160, sort: true, align:'center'}
            , {fixed: 'right', title: '操作', toolbar: '#barDemo', width:145, align:'center'}
        ]]
        ,height: 500
    });

    //监听行工具事件
    table.on('tool(test)', function (obj) {
        var data = obj.data;
        if (obj.event === 'xiangqing') {
            window.open(basePath+"/product-detail/"+data.commodityId)
        }
    });
});