const app = getApp()

Page({
  data: {
    //用于分页
    totalPage: 1,
    page:1,
    videoList: [],
    screenWidth: 350,
    serverUrl: ''
    
  },

  onLoad: function (params) {
    var me = this;
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    me.setData({
      screenWidth: screenWidth,
    });

    var page = me.data.page;
    me.getAllVideoList(page);
  },

  getAllVideoList: function(page){
    var me = this;
    var serverUrl = app.serverUrl;
    wx.showLoading({
      title: '请等待，加载中',
    });
    wx.request({
      url: serverUrl + '/video/showAll?page=' + page,
      method: 'POST',
      success: function (res) {
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        wx.stopPullDownRefresh();
        console.log(res.data);

        //判断当前页page是否是第一页
        if (page == 1) {
          me.setData({
            videoList: []
          });
        }

        var videoList = res.data.data.rows;
        var newVideoList = me.data.videoList;

        me.setData({
          videoList: newVideoList.concat(videoList),
          page: page,
          totalPage: res.data.data.total,
          serverUrl: serverUrl
        });
      }
    })
  },


  onPullDownRefresh: function () {
    wx.showNavigationBarLoading();
    this.getAllVideoList(1);
    
  },

  onReachBottom: function() {
    var me = this;
    var currentPage = me.data.page;
    var totalPage = me.data.totalPage;

    if (currentPage == totalPage) {
      wx.showToast({
        title: '已经没有视频了',
        icon: 'none'
      })
      return;
    }

    var page = currentPage + 1;
    me.getAllVideoList(page);
  }
})
