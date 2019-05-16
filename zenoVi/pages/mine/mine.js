var videoUtils = require('../../utils/videoUtils.js')

const app = getApp()

Page({
  data: {
    faceUrl: '../resource/images/noneface.png'
  },

  onLoad: function() {
    var me = this;
     
    //app.userInfo = res.data.data;
    var user = app.getGlobalUserInfo();

    wx.showLoading({
      title: '请等待',
    });
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/user/query?userId=' + user.id,
      method: 'POST',
      header: { 
        'content-type': 'application/json',
        'userId': user.id,
        'userToken': user.userToken
       },
      success: function (res) {
        wx.hideLoading();
        if(res.data.status == 200) {
          var userInfo = res.data.data;
          var faceUrl = '../resource/images/noneface.png';
          if(userInfo.faceImage != null && userInfo.faceImage != '' && 
          userInfo.faceImage != undefined) {
            var faceUrl = serverUrl + userInfo.faceImage;
          }

          me.setData({
            faceUrl: faceUrl,
            fansCounts: userInfo.fansCounts,
            followCounts: userInfo.followCounts,
            receiveLikeCounts: userInfo.receiveLikeCounts,
            nickname: userInfo.nickname
          });
        }
      }
    });    
  },

  logout: function() {
    var serverUrl = app.serverUrl;
    var user = app.getGlobalUserInfo();
    wx.request({
      url: serverUrl + '/logout?userId=' + user.id,
      method: 'POST',
      header: { 'content-type': 'application/json' },
      success: function (res) {
        wx.hideLoading();
        var status = res.data.status; 

        //跳转至登录并删除信息
        if(status == 200) {
          wx.showToast({
            title: '注销成功',
            duration: 1500
          }),
          wx.removeStorageSync("userInfo");
          wx.navigateTo({
            url: '../userLogin/login'
          })
        }
    }
  })
  },

  changeface: function() {
    var me = this;
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: function(res) {
        var tempFilePaths = res.tempFilePaths;

        wx.showLoading({
          title: '上传中',
        })
        var serverUrl = app.serverUrl;
        var userInfo = app.getGlobalUserInfo();
        wx.uploadFile({
          url: serverUrl + '/user/uploadFace?userId=' + userInfo.id,
          filePath: tempFilePaths[0],
          name: 'file',
          header: { 'content-type': 'application/json' },
          success: function(res) {
            var data = JSON.parse(res.data);
            console.log(data);
            wx.hideLoading();
            if(data.status == 200){
              wx.showToast({
                title: '上传成功',
              });

              var imgUrl = data.data;
              me.setData({
                faceUrl: serverUrl + imgUrl
              });
            } else if(data.status == 500) {
              wx.showToast({
                title: data.msg,
              })
            }
          }
        })
      }
    })
  },

  uploadVideo: function() {
    videoUtils.uploadVideo();
  },

    logout: function () {
    // var user = app.userInfo;
    var user = app.getGlobalUserInfo();

    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '请等待...',
    });
    // 调用后端
    wx.request({
      url: serverUrl + '/logout?userId=' + user.id,
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res.data);
        wx.hideLoading();
        if (res.data.status == 200) {
          // 登录成功跳转 
          wx.showToast({
            title: '注销成功',
            icon: 'success',
            duration: 2000
          });
          // app.userInfo = null;
          // 注销以后，清空缓存
          wx.removeStorageSync("userInfo")
          // 页面跳转
          wx.redirectTo({
            url: '../userLogin/login',
          })
        }
      }
    })
  },
})
