//重置密码部分
var re = /^1\d{10}$/; //正则表达式验证手机号

var app = new Vue({
    el: '#forget',
    data() {
        return {
            listimages: [],
            mainimg: "",
            videourl: ""
        }
    },
    mounted: function () {
        //将vue中的函数设置成全局的
        window.jiantingphone = this.jiantingphone;
    },
    methods: {
        jiantingphone: function () {
            var phone=$("#mobilephone").val();
            if(phone.length==0){
                layer.tips("请输入手机号", '#mobilephone', {
                    tips: [1, "#0FA6D8"],
                    tipsMore: !1,
                    time: 1300
                });
                $("#mobilephone").focus();
                return 0;
            }else if(!re.test(phone)){
                layer.tips("请输入合法的手机号", '#mobilephone', {
                    tips: [1, "#FF5722"],
                    tipsMore: !1,
                    time: 1300
                });
                $("#mobilephone").focus();
                return 0;
            }
            return 1;
        }
    }
});

function submitforget() {
    var mobilephone=$("#mobilephone").val();
    var password=$("#password").val();
    var t=jiantingphone();
    if(t==0){
        return;
    }
    if (password.length == 0) {
        layer.tips("请输入密码", '#password', {
            tips: [1, "#0FA6D8"],
            tipsMore: !1,
            time: 1300
        });
        $("#password").focus();
        return;
    }
    if (password.length > 20 || password.length < 5) {
        layer.tips("密码长度为：5-20", '#password', {
            tips: [1, "#FF5722"],
            tipsMore: !1,
            time: 1500
        });
        $("#password").focus();
        return;
    }

    $("#submitrg").addClass("layui-btn-disabled");
    $("#submitrg").attr("disabled", true);
    var object = new Object(); //创建一个存放数据的对象
    object["mobilephone"] = mobilephone;
    object["password"] = password;
    var jsonData = JSON.stringify(object); //根据数据生成json数据
    $.ajax({
        url: basePath + "/user/resetpwd",
        data: jsonData,
        contentType: "application/json;charset=UTF-8", //发送数据的格式
        type: "post",
        dataType: "json", //回调
        beforeSend: function () {
            layer.load(1, { //icon支持传入0-2
                content: '重置中...',
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
            if (data.status == 200) {
                layer.msg(data.message, {
                    time: 1000,
                    icon: 1,
                    offset: '100px'
                }, function () {
                    location.href=basePath+"/";
                });
            }else {
                layer.msg(data.message, {
                    time: 1000,
                    icon: 2,
                    offset: '100px'
                }, function () {
                    location.reload();
                });
            }
            $("#submitrg").removeClass("layui-btn-disabled");
            $("#submitrg").attr("disabled", false);
        },error:function () {
            layer.msg("系统错误", {
                time: 1000,
                icon: 5,
                offset: '100px'
            });
        }
    });
}
