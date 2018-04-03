package com.qm.gangsdk.ui.view.gangdynamic.dynamic.helperclass;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.qm.gangsdk.core.outer.common.callback.DataCallBack;
import com.qm.gangsdk.core.outer.common.entity.XLDynamicCommentBean;
import com.qm.gangsdk.core.outer.common.utils.StringUtils;
import com.qm.gangsdk.ui.GangSDK;
import com.qm.gangsdk.ui.R;
import com.qm.gangsdk.ui.base.XLBaseDialogFragment;
import com.qm.gangsdk.ui.utils.XLKeyBoardUtil;
import com.qm.gangsdk.ui.utils.XLToastUtil;

/**
 * Created by lijiyuan on 2018/1/9.
 *
 * 评论输入弹框
 */

public class DialogDynamicCommentFragment extends XLBaseDialogFragment {

    private View dynamicsSpace;
    private Button btnDynamicComment;
    private EditText editCommentContent;
    private CommentResult commentResult;
    private int dynamicid = -1;
    private int refusedid = -1;
    private String refnickname;

    @Override
    protected int getContentView() {
        return R.layout.dialog_fragment_dynamic_comment;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View view) {
        dynamicsSpace = view.findViewById(R.id.dynamicsSpace);
        btnDynamicComment = (Button) view.findViewById(R.id.btnDynamicComment);
        editCommentContent = (EditText) view.findViewById(R.id.editCommentContent);

        if(StringUtils.isEmpty(refnickname)){
            editCommentContent.setHint("请输入评论内容");
        }else {
            editCommentContent.setHint("回复  " + refnickname);
        }
        XLKeyBoardUtil.showKeyBoard(aContext, editCommentContent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dynamicsSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        btnDynamicComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = editCommentContent.getText().toString().trim();
                if(StringUtils.isEmpty(content)){
                    XLToastUtil.showToastShort("内容不能为空");
                    return;
                }

                if(StringUtils.getChineseLength(content) > 300){
                    XLToastUtil.showToastShort("内容不能超过150个汉字");
                    return;
                }
                if(refusedid <= 0){
                    refusedid = 0;
                }
                loading.show();
                GangSDK.getInstance().dynamicManager().commentDynamic(dynamicid, content, refusedid, new DataCallBack<XLDynamicCommentBean>() {
                    @Override
                    public void onSuccess(int status, String message, XLDynamicCommentBean data) {
                        close();
                        loading.dismiss();
                        XLToastUtil.showToastShort(message);
                        if(commentResult != null){
                            commentResult.success(data);
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        loading.dismiss();
                        XLToastUtil.showToastShort(message);
                    }
                });

            }
        });
    }

    @Override
    public void onResume() {
        //去掉两边留白
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    /**
     * 动态ID
     * @param dynamicId         动态ID
     * @return
     */
    public DialogDynamicCommentFragment setDynamicId(int dynamicId){
        this.dynamicid = dynamicId;
        return this;
    }

    /**
     * 回复玩家ID
     * @param refusedid         玩家ID
     * @return                  玩家ID
     */
    public DialogDynamicCommentFragment setRefusedId(int refusedid){
        this.refusedid = refusedid;
        return this;
    }

    /**
     * 回复玩家昵称
     * @param refnickname       玩家昵称
     * @return                  玩家昵称
     */
    public DialogDynamicCommentFragment setRefNickname(String refnickname){
        this.refnickname = refnickname;
        return this;
    }

    /**
     * 评论成功回调结果
     * @param commentResult     回调接口
     * @return                  评论bean
     */
    public DialogDynamicCommentFragment setOnCommentResult(CommentResult commentResult){
        this.commentResult = commentResult;
        return this;
    }

    public interface CommentResult{
       void success(XLDynamicCommentBean commentBean);
    }
}
