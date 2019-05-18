var videoUtils = require('../../utils/videoUtils.js')

const app = getApp()

Page({
  data: {
    faceUrl: '../resource/images/noneface.png',
    isMe: true,
    isFollow:false,

    videoSelClass: 'video-info',
    isSelectedWork: 'video-info-selected',
    isSelectedLike: '',
    isSelectedFollow: '',

    myVideoList: [],
    myVideoPage: 1,
    myVideoTotal: 1,

    likeVideoList: [],
    likeVideoPage: 1,
    likeVideoTotal: 1,

    followVideoList: [],
    followVideoPage: 1,
    followVideoTotal: 1,

    myWorkFlag: false,
    myLikesFlag: true,
    myFollowFlage: true
  },

  doSelectWork: function () {
    var me = this;
    me.setData({
      isSelectedLike: '',
      isSelectedWork: 'video-info-selected',
      isSelectedFollow: '',

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1,

      myWorkFlag: false,
      myLikesFlag: true,
      myFollowFlage: true
    });
    this.getMyVideoList(1);
  },

  doSelectLike: function() {
    var me = this;
    me.setData({
      isSelectedLike: 'video-info-selected',
      isSelectedWork: '',
      isSelectedFollow: '',

      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1,

      myWorkFlag: true,
      myLikesFlag: false,
      myFollowFlage: true

    });
    this.getMyLikesList(1);
  },

  doSelectFollow: function () {
    var me = this;
    me.setData({
      isSelectedLike: '',
      isSelectedWork: '',
      isSelectedFollow: 'video-info-selected',
      
      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,

      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,

      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1,

      myWorkFlag: true,
      myLikesFlag: true,
      myFollowFlage: false
    });
    this.getMyFollowList(1);
  },

  getMyVideoList: function (page) {
    var me = this;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showAll/?page=' + page + '&pageSize=6',
      method: "POST",
      data: {
        userId: me.data.userId
      },
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res.data);
        var myVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.myVideoList;
        me.setData({
          myVideoPage: page,
          myVideoList: newVideoList.concat(myVideoList),
          myVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  getMyLikesList: function (page) {
    var me = this;
    var userId = me.data.userId;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showMyLike/?userId=' + userId + '&page=' + page + '&pageSize=6',
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res.data);
        var likeVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.likeVideoList;
        me.setData({
          likeVideoPage: page,
          likeVideoList: newVideoList.concat(likeVideoList),
          likeVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },

  getMyFollowList: function (page) {
    var me = this;
    var userId = me.data.userId;

    // 查询视频信息
    wx.showLoading();
    // 调用后端
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showMyFollow/?userId=' + userId + '&page=' + page + '&pageSize=6',
      method: "POST",
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res.data);
        var followVideoList = res.data.data.rows;
        wx.hideLoading();

        var newVideoList = me.data.followVideoList;
        me.setData({
          followVideoPage: page,
          followVideoList: newVideoList.concat(followVideoList),
          followVideoTotal: res.data.data.total,
          serverUrl: app.serverUrl
        });
      }
    })
  },  

  // 点击跳转到视频详情页面
  showVideo: function (e) {

    console.log(e);

    var myWorkFlag = this.data.myWorkFlag;
    var myLikesFlag = this.data.myLikesFlag;
    var myFollowFlag = this.data.myFollowFlag;

    if (!myWorkFlag) {
      var videoList = this.data.myVideoList;
    } else if (!myLikesFlag) {
      var videoList = this.data.likeVideoList;
    } else if (!myFollowFlag) {
      var videoList = this.data.followVideoList;
    }

    var arrindex = e.target.dataset.arrindex;
    var videoInfo = JSON.stringify(videoList[arrindex]);

    wx.redirectTo({
      url: '../videoInfo/videoInfo?videoInfo=' + videoInfo
    })

  },

  onLoad: function(params) {
    var me = this;
     
    //app.userInfo = res.data.data;
    var user = app.getGlobalUserInfo();

    var userId = user.id;
    
    var publisherId = params.publisherId;
    if (publisherId != null && publisherId != '' && publisherId != undefined) {
      userId = publisherId;
      me.setData({
        isMe: false,
        publisherId: publisherId,
      })
    }
    
    
    wx.showLoading({
      title: '请等待',
    });
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/user/query?userId=' + userId + '&fanId=' + user.id,
      method: 'POST',
      header: { 
        'content-type': 'application/json',
        'userId': user.id,
        'userToken': user.userToken
       },
      success: function (res) {
        console.log(res.data);
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
            nickname: userInfo.nickname,
            isFollow: userInfo.follow
          });
        } else if(res.data.status == 502) {
          wx.showToast({
            title: res.data.msg,
            duration: 3000,
            icon: 'none',
            success: function() {
              wx.redirectTo({
                url: '../userLogin/login',
              })
            }
          })
        }
      }
    });    
  },

  followMe: function(e) {
    var me = this;

    var user = app.getGlobalUserInfo();
    var userId = user.id;
    var publisherId = me.data.publisherId;
    
    var followType = e.currentTarget.dataset.followtype;
    
    var url = '';
    //1:关注， 0:取消关注
    if(followType == '1') {
      url = '/user/beyourfans?userId=' + publisherId + '&fanId=' + userId;
    } else {
      url = '/user/dontbeyourfans?userId=' + publisherId + '&fanId=' + userId;
    }

    wx.showLoading();
    wx.request({
      url: app.serverUrl + url,
      method: 'POST',
      header: {
        'content-type': 'application/json',
        'userId': user.id,
        'userToken': user.userToken
      },
      success: function() {
        wx.hideLoading();
        if(followType == '1') {
          me.setData({
            isFollow: true,
            fansCounts: ++me.data.fansCounts
          })
        } else {
          me.setData({
            isFollow: false,
            fansCounts: --me.data.fansCounts
          })
        }
      }
    })
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

  onReachBottom: function () {
    var myWorkFlag = this.data.myWorkFlag;
    var myLikesFlag = this.data.myLikesFlag;
    var myFollowFlag = this.data.myFollowFlag;

    if (!myWorkFlag) {
      var currentPage = this.data.myVideoPage;
      var totalPage = this.data.myVideoTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyVideoList(page);
    } else if (!myLikesFlag) {
      var currentPage = this.data.likeVideoPage;
      var totalPage = this.data.myLikesTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyLikesList(page);
    } else if (!myFollowFlag) {
      var currentPage = this.data.followVideoPage;
      var totalPage = this.data.followVideoTotal;
      // 获取总页数进行判断，如果当前页数和总页数相等，则不分页
      if (currentPage === totalPage) {
        wx.showToast({
          title: '已经没有视频啦...',
          icon: "none"
        });
        return;
      }
      var page = currentPage + 1;
      this.getMyFollowList(page);
    }

  }
})
