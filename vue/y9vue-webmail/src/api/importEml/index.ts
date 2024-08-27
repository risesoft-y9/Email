/*
 * @Descripttion:
 * @version:
 * @Author: fanzhengyang
 * @Date: 2022-11-08 10:48:32
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-08-12 17:01:04
 */
import Request from '@/api/lib/emailRequest';
import qs from 'qs';

const emailRequest = Request();

/*
 * 带搜索条件的邮件列表
 */
export const searchEml = async (params) => {
    const data = qs.stringify(params);
    return await emailRequest({
        url: '/api/rest/importEml/page',
        method: 'GET',
        cType: false,
        params: params
    });
};

/**
 * 上传
 */
export const importEml = async (file) => {
    const data = new FormData();
    data.append('file', file);

    return await emailRequest({
        url: '/api/rest/importEml/importEml',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 邮件详情
 * @param id
 * @returns
 */

export const emlDetail = async (id) => {
    return await emailRequest({
        url: '/api/rest/importEml/getById',
        method: 'GET',
        params: { id: id }
    });
};

/**
 * 批量删除各箱邮件
 * @param ids
 * @returns
 */
export const deleteEml = async (ids) => {
    const params = {
        ids: ids
    };
    const data = qs.stringify(params);
    return await emailRequest({
        url: '/api/rest/importEml/deleteEml',
        method: 'POST',
        cType: false,
        data: data
    });
};

export const getAttById = async (importEmlId) => {
    return await emailRequest({
        url: '/api/rest/importEml/getAttById',
        method: 'GET',
        cType: false,
        params: { importEmlId: importEmlId }
    });
};
