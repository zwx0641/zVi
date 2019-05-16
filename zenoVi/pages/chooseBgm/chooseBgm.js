const app = getApp()

Page({
    data: {
      serverUrl: '',
      bgmList: [],
      videoParams: {}
    },

    onLoad: function(params) {
      var me = this;
      var user = app.userInfo;
      console.log(params);
      me.setData({
        videoParams: params
      });

      wx.showLoading({
        title: '请等待',
      });
      var serverUrl = app.serverUrl;
      wx.request({
        url: serverUrl + '/bgm/list',
        method: 'POST',
        header: { 'content-type': 'application/json' },
        success: function (res) {
          console.log(res.data);
          wx.hideLoading();
          if (res.data.status == 200) {
            var bgmList = res.data.data;
            me.setData({
              bgmList: bgmList,
              serverUrl: serverUrl
            })
          }
        }
      }); 
    },

    upload: function(e) {
      var me = this;
      var bgmId = e.detail.value.bgmId;
      var desc = e.detail.value.desc;

      console.log("bgmId:" + bgmId);
      console.log("desc:" + desc);

      console.log(me.data.videoParams);
      var duration = me.data.videoParams.duration;
      var tmpHeight = me.data.videoParams.tmpHeight;
      var tmpWidth = me.data.videoParams.tmpWidth;
      var tmpVideoUrl = me.data.videoParams.tmpVideoUrl;
      var tmpCoverUrl = me.data.videoParams.tmpCoverUrl;

      //上传短视频
      wx.showLoading({
        title: '上传中',
      })

      var serverUrl = app.serverUrl;
      var userInfo = app.getGlobalUserInfo();
      wx.uploadFile({
        url: serverUrl + '/video/upload',
        formData: {
          userId: userInfo.id,
          bgmId: bgmId,
          desc: desc,
          videoSeconds: duration,
          videoHeight: tmpHeight,
          videoWidth: tmpWidth
        },
        filePath: tmpVideoUrl,
        name: 'file',
        header: { 'content-type': 'application/json' },
        success: function (res) {
          var data = JSON.parse(res.data);
          wx.hideLoading();
          if (data.status == 200) {
            wx.showToast({
              title: '上传成功',
              duration: 1000
            });
            wx.navigateTo({
              url: '../mine/mine',
            })
          } 
        }
      })
    }
})

