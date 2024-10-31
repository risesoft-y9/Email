/*
 * @Author: your name
 * @Date: 2021-08-31 14:32:57
 * @LastEditTime: 2023-02-12 20:11:54
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-v9.5.x-vue\y9vue-info\src\api\tpl\upload.js
 */
import Request from '@/api/lib/emailRequest';

const emailRequest = Request();

export const uploadFile = async (file) => {
    const data = new FormData();
    data.append('file', file);
    return await emailRequest({
        url: '/api/standard/emailAttachment/uploadFile',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 上传
 */
export const addAttachment = async (folder, messageId, file) => {
    const data = new FormData();
    data.append('folder', folder);
    data.append('messageId', messageId);
    data.append('file', file);

    return await emailRequest({
        url: '/api/standard/emailAttachment',
        method: 'POST',
        cType: false,
        data: data
    });
};

export const deleteAttachment = async (folder, messageId, fileName) => {
    const data = new FormData();
    data.append('folder', folder);
    data.append('messageId', messageId);
    data.append('fileName', fileName);

    return await emailRequest({
        url: '/api/standard/emailAttachment/delete',
        method: 'POST',
        cType: false,
        data: data
    });
};
