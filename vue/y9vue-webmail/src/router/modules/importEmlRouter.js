/*
 * @Author: hongzhew
 * @Date: 2022-03-31 18:01:58
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-08-14 09:50:59
 * @Description: 菜单路由
 */

const importEmlRouter = [
    {
        path: '/importEml',
        component: () => import('@/layouts/index.vue'),
        redirect: '/importEml',
        name: 'importEml',
        meta: {
            title: '历史邮件',
            roles: ['user']
        },
        children: [
            {
                path: '/importEml',
                component: () => import('@/views/importEml/index.vue'),
                name: 'importEmlIndex',
                meta: {
                    title: '历史邮件',
                    icon: 'ri-mail-lock-line',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/emlDetail',
        component: () => import('@/layouts/index.vue'),
        redirect: '/emlDetail',
        name: 'emlDetail',
        meta: {
            title: '历史邮件详情',
            roles: ['user']
        },
        hidden: true,
        children: [
            {
                path: '/emlDetail',
                component: () => import('@/views/importEml/emailDetail/index.vue'),
                name: 'emlDetailInfo',
                meta: {
                    title: '历史邮件详情',
                    icon: 'ri-search-line',
                    roles: ['user']
                },
                hidden: true,
                props: (route) => ({ id: route.query.id })
            }
        ]
    }
];

export default importEmlRouter;
