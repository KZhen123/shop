var app = new Vue({
    el: '#goodsform',
    data() {
        return {
            listimages: [],
            mainimg: mainimgs,
        }
    },
    mounted: function () {
        //将vue中的函数设置成全局的
        window.showlistimages = this.showlistimages;
        window.setmainimgurl = this.setmainimgurl;

        window.getlistimages = this.getlistimages;
        window.getmainimgurl = this.getmainimgurl;
    },
    methods: {
        showlistimages: function (imgurl) {
            var that = this;
            if (that.listimages.length !== 3) {
                var object = new Object();
                object["imgsrc"] = imgurl;
                that.listimages.push(object);//向vue数组中添加图片
            }
        },getlistimages: function () {
            var that = this;
            return that.listimages;
        },setmainimgurl: function (imgurl) {
            var that = this;
            that.mainimg = imgurl;
        },getmainimgurl: function () {
            var that = this;
            return that.mainimg;
        },delimage: function (ids) {
            var that = this;
            that.listimages.splice(ids, 1);//从vue数组中删除此图
        },mouseOver: function (id) {
            $("#del" + id).show();
        }, mouseLeave: function (id) {
            $("#del" + id).hide();
        },openimg: function (imgsrc) {
            var img = '<img src="' + imgsrc + '" style="width:100%;height:100%">';
            layer.open({
                type: 1,
                title: false,
                shade: 0.6,
                closeBtn: 0, //不显示关闭按钮
                anim: 2,
                shadeClose: true, //开启遮罩关闭
                content: img
            });
        }
    }
});

for (var i=0;i<otherimages.length;i++){
    showlistimages(otherimages[i]);
}

layui.use(['form', 'upload', 'element'], function () {
    var upload = layui.upload;
    var form=layui.form;
    upload.render({
        elem: '#test3'
        , url: basePath + '/relgoods/images'
        , accept: 'images' //图片
        , size: 1024 * 20
        , exts: 'png|jpg'
        , multiple: true
        , number: 3
        , choose: function (obj) {
            //上传前判断已经上传了多少张图片
            var imgs = document.getElementById("otherimages").getElementsByTagName("img");
            if (imgs.length === 6) {
                layer.msg('最多上传三张图片');
                layer.close(obj);//报错让其停止上传
            }
        }
        , progress: function (n) {
            var percent = n + '%';
            layer.msg(percent, {
                icon: 16
                , shade: 0.01
            });
        }
        , done: function (res) {
            //如果上传失败
            if (res.code > 0) {
                return layer.msg('上传失败');
            } else {
                layer.closeAll('loading');
                layer.msg('上传成功', {
                    time: 1000,
                    icon: 1,
                    offset: '150px'
                });
                showlistimages(res.data.src[0]);
                $("#otherimages").show();
            }
        }
        , error: function () {
            layer.closeAll('loading');
            layer.msg('上传失败', {
                time: 1000,
                icon: 2,
                offset: '150px'
            });
        }
    });
    upload.render({
        elem: '#test2'
        , url: basePath + '/relgoods/pic'
        , accept: 'images' //图片
        , size: 1024 * 20
        , exts: 'png|jpg'
        , progress: function (n) {
            var percent = n + '%'; //获取进度百分比
            layer.msg(percent, {
                icon: 16
                , shade: 0.01
            });
        }
        , done: function (res) {
            //如果上传失败
            if (res.code > 0) {
                return layer.msg('上传失败');
            } else {
                layer.closeAll('loading');
                layer.msg('上传成功', {
                    time: 1000,
                    icon: 1,
                    offset: '150px'
                });
                setmainimgurl(res.data.src);
                $("#mainimage").show();
            }
        }
        , error: function () {
            layer.closeAll('loading');
            layer.msg('上传失败', {
                time: 1000,
                icon: 2,
                offset: '150px'
            });
        }
    });


    form.on('submit(demo1)', function (data) {
        var vuemainimg=getmainimgurl();
        var vuelistimages=getlistimages();
        if(vuemainimg.length===0){
            layer.msg('请上传商品的主图', {
                time: 1000,
                icon: 2,
                offset: '150px'
            });
            return false;
        }
        if(vuelistimages.length===0){
            layer.msg('请上传商品的其他图', {
                time: 1000,
                icon: 2,
                offset: '150px'
            });
            return false;
        }
        $("#demo1").addClass("layui-btn-disabled");
        $("#demo1").attr("disabled", true);
        var rellistimgs=new Array();
        for (var i=0;i<vuelistimages.length;i++){
            rellistimgs[i]=vuelistimages[i].imgsrc;
        }
        if(data.field.commname.length>200){
            layer.msg('商品名字过长', {
                time: 1000,
                icon: 2,
                offset: '150px'
            });
            return false;
        }
        var object = new Object();
        object["commid"] = commid;
        object["commname"] = data.field.commname;
        object["commdesc"] = data.field.commdesc;
        object["money"] = data.field.money;
        object["category"] = data.field.category;
        object["common"] = data.field.common;
        object["common2"] = data.field.common2;
        object["image"] = vuemainimg;
        object["otherimg"] = rellistimgs;
        var jsonData = JSON.stringify(object);
        $.ajax({
            url: basePath + "/changegoods/rel",
            data: jsonData,
            contentType: "application/json;charset=UTF-8",
            type: "post",
            dataType: "json",
            beforeSend: function () {
                layer.load(1, {
                    content: '请稍等...',
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
                layer.msg("修改成功！", {
                    time: 1000,
                    icon: 1,
                    offset: '100px'
                }, function () {
                    var mylay = parent.layer.getFrameIndex(window.name);
                    parent.layer.close(mylay);
                });
            },error:function () {
                layer.msg('发布失败', {
                    time: 1000,
                    icon: 2,
                    offset: '150px'
                });
            }
        });
        return false;
    });
    // 渲染下拉框
    $.ajax({
        type: 'get',
        url: basePath + '/category/all',
        dataType: 'json',
        success: function (data) {
            $("#category").empty();
            $.each(data, function (i, cate) {
                var option = "<option value='" + cate.id + "'";
                if (cate.id === categoryId) {
                    option += " selected";
                }
                option += ">" + cate.name + "</option>";

                $("#category").append(option);
            })
            // 更新渲染下拉框
            form.render();
        }
    });
});