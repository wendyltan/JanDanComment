# JanDanComment

## 注意：

*现在煎蛋已经使用js动态onload图片，增加了img-hash，明文图片链接不再存在。
尚未解决此问题，图片区暂不可用！*

## 项目简介：
本app是仿造**煎蛋**的一款软件，实现其基本的功能。
煎蛋官网：[煎蛋][1]

## 开发环境：
目前： 
- Android Studio 3.0

## 版本：
### v1.0.1（17/10/29）

## 主要参考项目：
- [Glide][2]
- [XRefreshView][3]
- [PinchImageView][4]
- 库：[Jsoup][5]
## 主要面板：
1. 段子区
2. 无聊图
3. 新鲜事首页 ~~（不是首页就不算新鲜）~~ 
4. 妹子图

### 段子区介绍：

![段子区界面][6]
#### 主要ui功能：
- 显示当前页数。这个页数是对应网页页数的。也就是网页版内容当前段子的而最新页号
- 跳转页数。输入页数可以直接跳转。
- 刷新：
	+ **上拉**：跳到上一页
	+ **下拉**：跳到下一页
- 吐槽：点击之后可以查看相应段子的评论内容
- menu：右下角三个点，用于分享段子。
### 无聊图区：


![无聊图界面][7]



*基本界面和段子如出一辙*
- 使用Glide显示的图片，可以加载动图
- 还有进度条会在图片加载时显示
- 上下页的跳页方式 ~~（其实可以弄成刷新但是因为懒所以。。。）~~

### 妹子图区：


![妹子图界面][8]

#### 加载原理和无聊图完全一样


> 图片区点击之后可以查看图片详情页，但是目前只能保存静态的图片（即无法保存gif）

### 设置界面：

- 开启或者关闭妹子图（隐藏或者创建）
- 简介

![设置][9]

### 新鲜事区：

#### 唯一不同的是这个区用的仍然是listview显示，我并没有改用recyclerview是因为这个界面并不需要更多的操作

- 点击新鲜事可以看新鲜事的详情，右上角查看评论


![新鲜事详情][10]


  [1]: http://jandan.net/
  [2]: https://github.com/bumptech/glide
  [3]: https://github.com/huxq17/XRefreshView
  [4]: https://github.com/boycy815/PinchImageView
  [5]: https://jsoup.org/
  [6]: /screenshot/1.jpg 
  [7]: /screenshot/2.jpg 
  [8]: /screenshot/4.jpg 
  [9]: /screenshot/7.png 
  [10]: /screenshot/9.jpg