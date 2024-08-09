/*
 * @Descripttion:
 * @version:
 * @Author: fanzhengyang
 * @Date: 2022-11-08 10:48:32
 * @LastEditors: Please set LastEditors
 * @LastEditTime: 2022-12-27 18:52:38
 */
import Request from '@/api/lib/emailRequest';
import qs from 'qs';

const folderRequest = Request();

/**
 * 获取文件夹列表
 * @param params
 * @returns
 */
export const getFolderList = async (params) => {
    return await folderRequest({
        url: '/api/standard/emailFolder/customList',
        method: 'GET',
        cType: false,
        params: params
    });
};

/**
 * 获取所有文件夹列表
 * @returns
 */
export const getAllFolderList = async () => {
    return await folderRequest({
        url: '/api/standard/emailFolder/allList',
        method: 'GET',
        cType: false
    });
};

/**
 * 保存文件夹
 * @param params
 * @returns
 */
export const saveFolder = async (params) => {
    const data = qs.stringify(params);
    return await folderRequest({
        url: '/api/standard/emailFolder',
        method: 'POST',
        cType: false,
        data: data
    });
};
/**
 * 删除文件夹
 * @param params
 * @returns
 */
export const deleteFolder = async (params) => {
    const data = qs.stringify(params);
    return await folderRequest({
        url: '/api/standard/emailFolder',
        method: 'DELETE',
        cType: false,
        data: data
    });
};
