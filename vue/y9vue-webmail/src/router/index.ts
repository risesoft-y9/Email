/*
 * @Author: your name
 * @Date: 2021-05-14 09:26:23
 * @LastEditTime: 2024-08-09 15:27:21
 * @LastEditors: mengjuhua
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-v9.5.x-vue\y9vue-info\src\router\index.js
 */

import { routerBeforeEach } from '@/router/checkRouter';
import NProgress from 'nprogress';
import { createRouter, createWebHistory } from 'vue-router';
import authRouter from './modules/authRouter';
import menuRouter from './modules/menuRouter';
import emailDetailRouter from './modules/emailDetailRouter';
import dynamicRouter from './modules/dynamicRouter';

import importEmlRouter from './modules/importEmlRouter';


//constantRoutes为不需要动态判断权限的路由，如登录、404、500等
export const constantRoutes: Array<any> = [
    {
        path: '/',
        name: 'index',
        hidden: true,
        redirect: '/auth'
    },
    {
        path: '/401',
        hidden: true,
        meta: {
            title: 'Not Permission'
        },
        component: () => import('@/views/401/index.vue')
    },
    {
        path: '/404',
        hidden: true,
        meta: {
            title: 'Not Found'
        },
        component: () => import('@/views/404/index.vue')
    }
];

//asyncRoutes需求动态判断权限并动态添加的页面  这里的路由模块顺序也是菜单显示的顺序（位置：src->router->modules）
export const asyncRoutes = [
    ...authRouter,
    // homeRouter,
    // 引入其他模块路由
    ...menuRouter,
    emailDetailRouter,
    ...importEmlRouter,
    dynamicRouter
];

//创建路由模式，采用history模式没有“#”
const router = createRouter({
    history: createWebHistory(import.meta.env.VUE_APP_PUBLIC_PATH),
    routes: constantRoutes
});

//在用户点击前，进入routerBeforeEach去判断用户是否有权限
//全部判断逻辑请查看checkRouter.js
router.beforeEach(routerBeforeEach);
router.afterEach(() => {
    NProgress.done();
});
export default router;
