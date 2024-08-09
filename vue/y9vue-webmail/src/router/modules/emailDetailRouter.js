/*
 * @Author: fanzhengyang
 */
const emailDetailRouter = {
    path: '/emailDetail',
    component: () => import('@/layouts/index.vue'),
    redirect: '/emailDetailInfo',
    name: 'emailDetail',
    hidden: true,
    meta: {
        title: '邮件详情',
        roles: ['user']
    },
    children: [
        {
            path: '/emailDetailInfo',
            component: () => import('@/views/emailDetail/index.vue'),
            name: 'emailDetailInfo',
            hidden: true,
            meta: {
                title: '邮件详情',
                roles: ['user']
            },
            // props: true
            props: (route) => ({ folder: route.query.folder, uid: route.query.uid })
        }
    ]
};

export default emailDetailRouter;
