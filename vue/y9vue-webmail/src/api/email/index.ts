/*
 * @Descripttion:
 * @version:
 * @Author: fanzhengyang
 * @Date: 2022-11-08 10:48:32
 * @LastEditors: Please set LastEditors
 * @LastEditTime: 2022-12-27 12:21:56
 */
import Request from '@/api/lib/emailRequest';
import qs from 'qs';

const emailRequest = Request();

/**
 * 批量删除各箱邮件
 * @param ids
 * @returns
 */
export const deleteEmail = async (folder, ids) => {
    const params = {
        folder: folder,
        uids: ids
    };
    const data = qs.stringify(params);
    return await emailRequest({
        url: '/api/standard/email/delete',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 批量彻底删除各箱邮件
 * @param ids
 * @returns
 */
export const deleteForeverEmail = async (folder, ids) => {
    const params = {
        uids: ids,
        folder: folder
    };
    const data = qs.stringify(params);
    return await emailRequest({
        url: '/api/standard/email/deletePermanently',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 批量标记为已读/未读
 * @param ids
 * @returns
 */
export const readEmail = async (ids, folder, isRead) => {
    const params = {
        uids: ids,
        folder: folder,
        isRead: isRead
    };
    const data = qs.stringify(params);
    return await emailRequest({
        url: '/api/standard/email/read',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 邮件星标/取消星标
 * @returns
 * @param id
 * @param folder
 * @param flagged
 */
export const flagEmail = async (id, folder, flagged) => {
    const params = {
        uids: id,
        folder: folder,
        flagged: flagged
    };
    const data = qs.stringify(params);
    return await emailRequest({
        url: '/api/standard/email/flag',
        method: 'POST',
        cType: false,
        params: params
    });
};

/**
 * 移动至
 * @param ids,folderId
 * @returns
 */

export const moveToEmail = async (ids, originFolder, toFolder) => {
    const params = {
        uids: ids,
        originFolder: originFolder,
        toFolder: toFolder
    };
    const data = qs.stringify(params);
    return await emailRequest({
        url: '/api/standard/email/move',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 邮件详情
 * @param params
 * @returns
 */

export const emailDetail = async (folder, uid) => {
    return await emailRequest({
        url: `/api/standard/email/${folder}/${uid}`,
        method: 'GET',
        cType: false
    });
};

/**
 * 发送
 * @param {*} emailId
 * @returns
 */
export const sendEmail = async (messageId) => {
    const data = new FormData();
    data.append('messageId', messageId);
    return await emailRequest({
        url: '/api/standard/email/send',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 存草稿
 * @param {*} emailId
 * @returns
 */
export const saveEmail = async (params) => {
    const data = qs.stringify(params, { indices: false });
    return await emailRequest({
        url: '/api/standard/email',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 回复
 */
export const replyEmail = async (folder, uid) => {
    return await emailRequest({
        url: `/api/standard/email/reply/${folder}/${uid}`,
        method: 'GET',
        cType: false
    });
};

/**
 * 全部回复
 */
export const replyAllEmail = async (folder, uid) => {
    return await emailRequest({
        url: `/api/standard/email/replyAll/${folder}/${uid}`,
        method: 'GET',
        cType: false
    });
};

/**
 * 快速回复
 */
export const quickReplyEmail = async (folder, uid, richText) => {
    const data = new FormData();
    data.append('richText', richText);
    return await emailRequest({
        url: `/api/standard/email/quickReply/${folder}/${uid}`,
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 撤回
 */
export const withDrawEmail = async (params) => {
    return await emailRequest({
        url: '/api/email/withDraw',
        method: 'GET',
        cType: false,
        params: params
    });
};

/**
 * 转发
 */
export const forwardPageEmail = async (folder, uid) => {
    return await emailRequest({
        url: `/api/standard/email/forward/${folder}/${uid}`,
        method: 'GET',
        cType: false
    });
};

/**
 * 最近联系人列表
 * /api/email/contact
 */
export const contactEmail = async () => {
    return await emailRequest({
        url: '/api/email/contact',
        method: 'GET',
        cType: false
    });
};

/**
 * 获取邮件列表
 * @param params
 * @returns
 */
export const emailList = async (params) => {
    const data = qs.stringify(params);
    return await emailRequest({
        url: '/api/standard/email/list',
        method: 'GET',
        cType: false,
        params: params
    });
};

/*
 * 带搜索条件的邮件列表
 */
export const searchEmail = async (params) => {
    const data = qs.stringify(params);
    return await emailRequest({
        url: '/api/standard/email/search',
        method: 'GET',
        cType: false,
        params: params
    });
};
