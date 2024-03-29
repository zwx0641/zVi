var videoUtils = require('../../utils/videoUtils.js')

const app = getApp()
Page({
  data: {
    cover: 'cover',
    videoId: '',
    src: '',
    videoInfo: {},
    userLikeVideo: false
  },

  showSearch: function() {
    wx.navigateTo({
      url: '../searchInfo/searchInfo',
    })
  },

  videoCtx: {},

  onLoad: function(params) {
    var user = app.getGlobalUserInfo();
    var me = this;
    me.videoCtx = wx.createVideoContext("myvideo", me);

    //获取上个页面传参
    var videoInfo = JSON.parse(params.videoInfo);

    var height = videoInfo.videoHeight;
    var width = videoInfo.videoWidth;

    if(width > height) {
      me.setData({
        cover: ''
      })
    }
    
    me.setData({
      videoId: videoInfo.id,
      src: app.serverUrl + videoInfo.videoPath,
      videoInfo: videoInfo
    })

    var serverUrl = app.serverUrl;
    var loginUserId = "";

    if (user == null || user == '' || user == undefined) {
      loginUserId = user.id;
      
    }

    wx.request({
      url: serverUrl + '/user/queryPublisher?loginUserId=' + loginUserId + '&videoId=' + videoInfo.id + '&publisherId=' + videoInfo.userId,
      method: 'POST',
      success: function(res) {
        console.log(res.data);
        var publisher = res.data.data.publisher;
        var userLikeVideo = res.data.data.userLikeVideo;
        
        me.setData({
          serverUrl: serverUrl,
          publisher: publisher,
          userLikeVideo: userLikeVideo
        })
      }
    })
  },

  onShow: function () {
    var me = this;
    me.videoCtx.play();
  },

  onHide: function () {
    var me = this;
    me.videoCtx.pause();
  },

  showMine: function() {
    wx.navigateTo({
      url: '../mine/mine',
    })
  },

  showPublisher: function() {
    var user = app.getGlobalUserInfo();
    var me = this;
    var videoInfo = me.data.videoInfo;
    var realUrl = '../mine/mine#publisherId@' + videoInfo.userId;

    
    if (user == null || user == '' || user == undefined) {
      wx.navigateTo({
        url: '../userLogin/login?redirectUrl=' + realUrl,
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine?publisherId=' + videoInfo.userId,
      })
    }
  },

  upload: function() {
    var user = app.getGlobalUserInfo();
    var me = this;
    var videoInfo = JSON.stringify(me.data.videoInfo);
    var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;
  

    if (user == null || user == '' || user == undefined) {
      wx.navigateTo({
        url: '../userLogin/login?redirectUrl=' + realUrl,
      })
    } else {
      videoUtils.uploadVideo();
    }
  },

  showIndex: function() {
    var user = app.getGlobalUserInfo();

    if(user == null || user == '' || user == undefined) {
      wx.navigateTo({
        url: '../userLogin/login',
      })
    } else {
      wx.redirectTo({
        url: '../index/index',
      })
    }
  },

  shareMe: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    wx.showActionSheet({
      itemList: ['下载到本地', '举报用户', '分享到朋友圈', '分享到qq空间', '分享到微博'],
      success: function(res) {
        
        if (res.tapIndex == 0) {
          //下载
          wx.showLoading({
            title: '下载中',
          })
          wx.downloadFile({
            url: app.serverUrl + me.data.videoInfo.videoPath,
            success: function(res) {
              if(res.statusCode === 200) {
                
                wx.saveVideoToPhotosAlbum({
                  filePath: res.tempFilePath,
                  success: function(res) {
                    console.log(res.errMsg);
                    wx.hideLoading();
                  }
                })
              }
            }
          
          })
        } else if (res.tapIndex == 1) {
          //举报
          var videoInfo = JSON.stringify(me.data.videoInfo);
          var realUrl = '../videoInfo/videoInfo#videoInfo@' + videoInfo;

          if(user == null || user == undefined || user == '') {
            wx.navigateTo({
              url: '../userLogin/userLogin?redirectUrl=' + realUrl,
            })
          } else {
            var publisherUserId = me.data.videoInfo.userId;
            var videoId = me.data.videoInfo.id;
            var currentUserId = user.id;
            wx.navigateTo({
              url: '../report/report?videoId=' + videoId + '&publisherUserId=' + publisherUserId,
            })
          }
        } else {
          wx.showToast({
            title: '官方暂未开放',
          })
        }
      }
    })
  },

  likeVideoOrNot: function() {
    var me = this;
    var videoInfo = me.data.videoInfo;
    var user = app.getGlobalUserInfo();

    if (user == null || user == '' || user == undefined) {
      wx.navigateTo({
        url: '../userLogin/login',
      })
    } else {
      var userLikeVideo = me.data.userLikeVideo;
     
      var url = '/video/userLike?userId=' + user.id + '&videoId=' + videoInfo.id + '&videoCreatorId=' + videoInfo.userId;
      
      if(userLikeVideo) {
        var url = '/video/userUnlike?userId=' + user.id + '&videoId=' + videoInfo.id + '&videoCreatorId=' + videoInfo.userId;
      }

      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '',
      })
      wx.request({
        url: serverUrl + url,
        method: 'POST',
        header: {
          'content-type': 'application/json',
          'userId': user.id,
          'userToken': user.userToken
        },
        success: function(res) {
          console.log(res);
          wx.hideLoading();
          if(userLikeVideo == false) {
            me.setData({
              userLikeVideo: true
            })
          } else {
            me.setData({
              userLikeVideo: false
            })
          }
          
          console.log(me.data);
        }
      })
    }
  },

  onShareAppMessage: function(res) {
    var me = this;
    var videoInfo = me.data.videoInfo;
    
    if(res.from === 'button') {

    }
    return {
      title: '短视频内容分享',
      path: 'pages/videoInfo/videoInfo?videoInfo=' + JSON.stringify(videoInfo)
    }
  }
})