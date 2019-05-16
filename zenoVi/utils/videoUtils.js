function uploadVideo() {
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

      if (duration > 16) {
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
          url: '../chooseBgm/chooseBgm?duration=' + duration +
            "&tmpHeight=" + tmpHeight +
            "&tmpWidth=" + tmpWidth +
            "&tmpVideoUrl=" + tmpVideoUrl +
            "&tmpCoverUrl=" + tmpCoverUrl,
        })
      }
    }
  })
}

module.exports = {
  uploadVideo: uploadVideo
}