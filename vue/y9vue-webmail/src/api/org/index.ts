/*
 * @Author: your name
 * @Date: 2021-04-09 18:53:30
 * @LastEditTime: 2023-02-05 16:42:50
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel-dcat-style\src\api\org\index.ts
 */
import Request from '@/api/lib/emailRequest';

// const baseURL = process.env.VUE_APP_HOST_Y9HOME
const platformRequest = Request();

// 树组件 - 一级接口数据
export const treeInterface = async (params) => {
    return await platformRequest({
        //url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/org/list',
        url: '/api/standard/org/getOrganization',
        method: 'get',
        cType: false,
        params: {}
    });
};

// 树组件 - 二（三）级接口数据
export const getTreeItemById = async (params) => {
    return await platformRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/org/getTree',
        url: '/api/standard/org/getOrgTree',
        method: 'get',
        cType: false,
        params: {
            id: params.parentId,
            treeType: 'tree_type_person',
            disabled: params.disabled
        }
    });
};

// 树组件 - 搜索接口
export const searchByName = async (params) => {
    // console.log(params, "0000");
    return await platformRequest({
        url: '/api/standard/org/getOrgTree',
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/org/treeSearch',      // apifox - mock
        method: 'GET',
        cType: false,
        params: { name: params.key, treeType: params.treeType }
    });
};

/**
 * 获取人员数
 * @param {*} ID
 * @param {*} orgType
 * @returns
 */
export const getAllPersonsCount = async (id, orgType) => {
    return await platformRequest({
        url: '/api/org/getAllPersonsCount',
        method: 'GET',
        cType: false,
        params: { id: id, orgType: orgType }
    });
};
