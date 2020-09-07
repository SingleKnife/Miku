# Miku
在安卓上加载，渲染MikuMikuDance pmd格式模型和vmd格式动画  
- 基于Opengles 2.0实现模型加载及渲染，使用FrameBuffer实现阴影显示
- 关键帧插值实现动画播放，使用CCD(循环坐标降解法)实现骨骼动画
- 使用bullet物理引擎实现头发，衣服物理模拟

![image](https://github.com/SingleKnife/Miku/blob/master/images/sample.gif)

参考项目： https://github.com/benikabocha/saba
基于桌面端的OpenGL项目,支持mmd的各种格式  

相关资料：
1. 文件格式解析  
mmd: https://mikumikudance.fandom.com/wiki/MMD:Polygon_Model_Data
vmd: https://mikumikudance.fandom.com/wiki/VMD_file_format
https://harigane.at.webry.info/201103/article_1.html
2. vmd文件下载  
https://bowlroll.net/file/keyword/%E3%83%A2%E3%83%BC%E3%82%B7%E3%83%A7%E3%83%B3?sort=download&order=down&date=none&auth=none
3. ccd算法  
http://www.ryanjuckett.com/programming/cyclic-coordinate-descent-in-2d/
https://blog.dreamana.com/2014/01/11/ccd-algorithm-in-as3/
4. 骨骼运动算法  
http://www.cnblogs.com/neoragex2002/archive/2007/09/13/Bone_Animation.html

5. 贝塞尔曲线  
https://pomax.github.io/bezierinfo/zh-CN/?utm_source=androidweekly.io&utm_medium=website

6. bullet引擎  
https://www.cnblogs.com/zhaolizhe/p/6937998.html
http://www.dwenzhao.cn/profession/netbuild/ammoegine.html  
bullet中文接口说明
