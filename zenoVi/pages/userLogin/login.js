// pages/userLogin/login.js
const app = getApp()
Page({
  data: {

  },
  goRegistPage: function(){
    wx.navigateTo({
      url: '../userRegist/regist',
    })
  },

  doLogin: function(e) {
    var formObject = e.detail.value;
    var username = formObject.username;
    var password = formObject.password;

    //Simple verification
    if (username.length == 0 || password.length == 0) {
      wx.showToast({
        title: '用户名或密码为空',
        icon: 'none',
        duration: 3000
      })
    } else {
      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '请稍等',
      }),
      wx.request({
        url: serverUrl + '/login',
        method: 'POST',
        data: {
          username: username,
          password: password
        },
        header: {'content-type': 'application/json'},
        success: function(res) {
          wx.hideLoading();
          var status = res.data.status;
          if(status == 200) {
            wx.showToast({
              title: '登录成功',
              duration: 2000
            }),
            app.userInfo = res.data.data;
            //跳转至mine
            wx.navigateTo({
              url: '../mine/mine',
            })
          } else if (status == 500) {
            wx.showToast({
              title: res.data.msg,
              icon: 'none',
              duration: 3000
            })
          }
        }
      })
    }
  }
})