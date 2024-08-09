/*
 * @Author: hongzhew
 * @Date: 2022-03-31 18:01:58
 * @LastEditors: Please set LastEditors
 * @LastEditTime: 2022-11-23 16:47:18
 * @Description: 首页路由
 */

const homeRouter = {
    path: '/home',
    component: () => import('@/layouts/index.vue'),
    redirect: '/home',
    name: 'home',
    meta: {
        title: '首页',
        roles: ['user']
    },
    children: [
        {
            path: '/home',
            component: () => import('@/views/home/index.vue'),
            name: 'homeIndex',
            meta: {
                title: '首页',
                icon: 'ri-home-2-line',
                roles: ['user']
            }
        }
    ]
};

export default homeRouter;
