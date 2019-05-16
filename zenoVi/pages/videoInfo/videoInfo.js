var videoUtils = require('../../utils/videoUtils.js')

const app = getApp()
Page({
  data: {
    cover: 'cover',
    videoId: '',
    src: '',
    videoInfo: {}
  },

  showSearch: function() {
    wx.navigateTo({
      url: '../searchInfo/searchInfo',
    })
  },

  videoCtx: {},

  onLoad: function(params) {
    var me = this;
    me.videoCtx = wx.createVideoContext("myvideo", me);

    //获取上个页面传参
    var videoInfo = JSON.parse(params.videoInfo);

    var height = videoInfo.videoHeight;
    var width = videoInfo.videoWidth;

    if(width > height) {
      cover = "";
    }

    me.setData({
      videoId: videoInfo.id,
      src: app.serverUrl + videoInfo.videoPath,
      videoInfo: videoInfo
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
  }
})