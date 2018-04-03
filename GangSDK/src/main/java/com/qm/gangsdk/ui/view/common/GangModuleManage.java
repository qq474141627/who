package com.qm.gangsdk.ui.view.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qm.gangsdk.ui.custom.ninegrid.ImageInfo;
import com.qm.gangsdk.ui.view.gangcenter.game.GangCenterGameActivity;
import com.qm.gangsdk.ui.view.gangcenter.message.GangCenterMessageActivity;
import com.qm.gangsdk.ui.view.gangcenter.user.GangCenterUserActivity;
import com.qm.gangsdk.ui.view.gangdynamic.dynamic.GangDynamicActivity;
import com.qm.gangsdk.ui.view.gangdynamic.dynamic.ImageBrowseActivity;
import com.qm.gangsdk.ui.view.gangdynamic.dynamic.MoreCommentsActivity;
import com.qm.gangsdk.ui.view.gangdynamic.dynamic.UserDynamicActivity;
import com.qm.gangsdk.ui.view.gangdynamic.publish.PublishDynamicActivity;
import com.qm.gangsdk.ui.view.gangin.InGangTabActivity;
import com.qm.gangsdk.ui.view.gangin.manage.ManageApplyActivity;
import com.qm.gangsdk.ui.view.gangin.manage.ManageRoleAccessActivity;
import com.qm.gangsdk.ui.view.gangin.members.MemberInfoActivity;
import com.qm.gangsdk.ui.view.gangin.members.MemberListActivity;
import com.qm.gangsdk.ui.view.gangout.OutGangTabActivity;
import com.qm.gangsdk.ui.view.gangout.create.GangCreateActivity;

import java.io.Serializable;
import java.util.List;

import static com.qm.gangsdk.ui.view.gangdynamic.dynamic.MoreCommentsFragment.COMMENT_SUCCESS;

/**
 * Created by shuzhou on 2017/8/3.
 */

public class GangModuleManage {

    //跳转到没有社群主界面
    public static void toOutGangTabActivity(Context context) {
        Intent intent = new Intent(context, OutGangTabActivity.class);
        context.startActivity(intent);
    }

    //跳转到创建社群主界面
    public static void toGangCreateActivity(Context context) {
        Intent intent = new Intent(context, GangCreateActivity.class);
        context.startActivity(intent);
    }

    //跳转到社群主界面
    public static void toInGangTabActivity(Context context) {
        Intent intent = new Intent(context, InGangTabActivity.class);
        context.startActivity(intent);
    }

    //跳转活跃榜
    public static void toMemberActiveActivity(Context context) {
        Intent intent = new Intent(context, MemberListActivity.class);
        intent.putExtra(MemberListActivity.TYPE, MemberListActivity.ACTIVIE);
        context.startActivity(intent);
    }

    //跳转贡献榜
    public static void toMemberContributeActivity(Context context) {
        Intent intent = new Intent(context, MemberListActivity.class);
        intent.putExtra(MemberListActivity.TYPE, MemberListActivity.CONTRUBUTE);
        context.startActivity(intent);
    }


    //跳转用户排行榜
    public static void toMemberSortActivity(Context context) {
        Intent intent = new Intent(context, MemberListActivity.class);
        intent.putExtra(MemberListActivity.TYPE, MemberListActivity.SORT);
        context.startActivity(intent);
    }

    //跳转禁言列表
    public static void toMemberNotalkActivity(Context context) {
        Intent intent = new Intent(context, MemberListActivity.class);
        intent.putExtra(MemberListActivity.TYPE, MemberListActivity.NOTALK);
        context.startActivity(intent);
    }

    //跳转玩家详情
    public static void toMemberInfoActivity(Context context, int memberid) {
        Intent intent = new Intent(context, MemberInfoActivity.class);
        intent.putExtra(MemberInfoActivity.MEMBER_ID, memberid);
        context.startActivity(intent);
    }

    //跳转申请列表
    public static void toGangApplyListActivity(Context context){
        Intent intent = new Intent(context, ManageApplyActivity.class);
        context.startActivity(intent);
    }

    //跳转职称管理
    public static void toManageProfessionActivity(Context context){
        Intent intent = new Intent(context, ManageRoleAccessActivity.class);
        context.startActivity(intent);
    }

    //跳转游戏中心
    public static void toGangCenterGameActivity(Context context){
        Intent intent = new Intent(context, GangCenterGameActivity.class);
        context.startActivity(intent);
    }

    //跳转消息中心
    public static void toGangCenterMessageActivity(Context context){
        Intent intent = new Intent(context, GangCenterMessageActivity.class);
        context.startActivity(intent);
    }

    //跳转个人中心
    public static void toGangCenterUserActivity(Context context){
        Intent intent = new Intent(context, GangCenterUserActivity.class);
        context.startActivity(intent);
    }

    //跳转社群动态
    public static void toGangDynamicActivity(Context context){
        Intent intent = new Intent(context, GangDynamicActivity.class);
        context.startActivity(intent);
    }

    //跳转个人动态
    public static void toUserDynamicActivity(Context context, int userid, String nickname){
        Intent intent = new Intent(context, UserDynamicActivity.class);
        intent.putExtra(UserDynamicActivity.USER_ID, userid);
        intent.putExtra(UserDynamicActivity.USER_NICKNAME, nickname);
        context.startActivity(intent);
    }

    //跳转动态更多评论
    public static void toMoreCommentsActivity(Context context, int dynamicid, int commentnum){
        Intent intent = new Intent(context, MoreCommentsActivity.class);
        intent.putExtra(MoreCommentsActivity.DYANMIC_ID, dynamicid);
        intent.putExtra(MoreCommentsActivity.COMMENT_NUM, commentnum);
        ((Activity)context).startActivityForResult(intent, COMMENT_SUCCESS);
    }

    //跳转图片查看
    public static void toImageBrowseActivity(Context context, List<ImageInfo> imageInfo, int index, boolean canDelete){
        Intent intent = new Intent(context, ImageBrowseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ImageBrowseActivity.CURRENT_ITEM, index);
        bundle.putBoolean(ImageBrowseActivity.CAN_DELETE, canDelete);
        bundle.putSerializable(ImageBrowseActivity.IMAGE_INFO, (Serializable) imageInfo);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //跳转发布动态
    public static void toPublishDynamicActivity(Context context){
        Intent intent = new Intent(context, PublishDynamicActivity.class);
        context.startActivity(intent);
    }
}
