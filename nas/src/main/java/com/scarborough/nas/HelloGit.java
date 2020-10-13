package com.scarborough.nas;


//team->add index	添加到暂存区
//team->commit	提交
//git->添加忽略.gitigore
//team->show history	返回历史版本	命令行才能到新的
//team->switch to	分支		合并分支-->merge
//分支场景：版本跟新
//每次提交之前都要pull下载更新，再进行push
//import->git->uri->适配
public class HelloGit {
	public static void main(String[] args) {
		System.out.println("Hello Git!");
		System.out.println("更新1");
		System.out.println("创建分支");
		System.out.println("主干添加");
		System.out.println("分支添加");
		System.out.println("第二次提交之前跟新");
	}
}
