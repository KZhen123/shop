window.onload = function() {

    let indexList = new Vue({
        el: '#indexssss',
        data() {
            return {
                indexData: [],
                categoryList: ['全部', '3C数码', '书籍', '生活用品', '服饰', '美妆', '出行','其他'],
                clilckCategory: '全部',
            }
        },
        mounted: function() {
            this.getCategoryList(this.clilckCategory);
        },
        methods: {
            getCategoryList: function(choiced) {
                let that = this;
                $.ajax({
                    url: basePath + '/index/product/' + choiced,
                    data: "",
                    type: 'GET',
                    contentType: "application/json;charset=UTF-8",
                    dataType: 'json',
                    success: function(msg) {
                        if(msg.status === 200) {
                            that.indexData = msg.data;
                        }
                    },error:function () {
                        layer.msg("系统错误", {
                            time: 1000,
                            icon: 5,
                            offset: '100px'
                        });
                    }
                })
            },

            // 点击商品的分类：把选中的分类传到后台，返回数据重新渲染数据
            sendCategoryLabel(choiceName) {
                this.clilckCategory = choiceName;
                this.getCategoryList(this.clilckCategory);
            }
        }
    })
}