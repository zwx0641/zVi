const app = getApp()

Page({
  data: {
    faceUrl: '../resource/images/noneface.png'
  },

  onLoad: function() {
    var me = this;
    var user = app.userInfo;

    wx.showLoading({
      title: '请等待',
    });
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/user/query?userId=' + user.id,
      method: 'POST',
      header: { 'content-type': 'application/json' },
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
    var user = app.userInfo;
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
          app.userInfo = null;
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
        wx.uploadFile({
          url: serverUrl + '/user/uploadFace?userId=' + app.userInfo.id,
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
    var me = this;
    wx.chooseVideo({
      sourceType: ['album', 'camera'],
      maxDuration: 15,
      camera: 'back',
      success(res) {
        console.log(res);

        var duration = res.duration;
        var tmpHeight = res.height;
        var tmpWidth = res.width;
        var tmpVideoUrl = res.tempFilePath;
        var tmpCoverUrl = res.thumbTempFilePath;

        if(duration > 16) {
          wx.showToast({
            title: '视频长度不能超过15秒',
            icon: 'none',
            duration: 2000
          })
        } else if (duration < 1) {
          wx.showToast({
            title: '请上传超过1秒的视频',
            icon: 'none',
            duration: 2000
          })
        } else {
          //TODO bgm选择页面
          wx.navigateTo({
            url: '../chooseBgm/chooseBgm?duration=' + duration
              + "&tmpHeight=" + tmpHeight
              + "&tmpWidth=" + tmpWidth
              + "&tmpVideoUrl=" + tmpVideoUrl
              + "&tmpCoverUrl=" + tmpCoverUrl,
          })
        }
      }
    })
  }
})
