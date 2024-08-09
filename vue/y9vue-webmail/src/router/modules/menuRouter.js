/*
 * @Author: hongzhew
 * @Date: 2022-03-31 18:01:58
 * @LastEditors: Please set LastEditors
 * @LastEditTime: 2022-11-21 11:00:22
 * @Description: 菜单路由
 */

const menuRouter = [
    {
        path: '/writing',
        component: () => import('@/layouts/index.vue'),
        redirect: '/writing',
        name: 'writing',
        meta: {
            icon: 'ri-edit-line',
            title: '写信',
            roles: ['user']
        },
        children: [
            {
                path: '/writing',
                hidden: true,
                component: () => import('@/views/write/index.vue'),
                name: '/writing',
                meta: {
                    title: '写信',
                    icon: 'ri-edit-line',
                    roles: ['user']
                }
            },
            {
                path: '/writing/reply',
                hidden: true,
                component: () => import('@/views/write/index.vue'),
                name: '/writing/reply',
                meta: {
                    title: '写信',
                    icon: 'ri-edit-line',
                    roles: ['user']
                }
            },
            {
                path: '/writing/replyAll',
                hidden: true,
                component: () => import('@/views/write/index.vue'),
                name: '/writing/replyAll',
                meta: {
                    title: '写信',
                    icon: 'ri-edit-line',
                    roles: ['user']
                }
            },
            {
                path: '/writing/forward',
                hidden: true,
                component: () => import('@/views/write/index.vue'),
                name: '/writing/forward',
                meta: {
                    title: '写信',
                    icon: 'ri-edit-line',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/receive',
        component: () => import('@/layouts/index.vue'),
        redirect: '/receive',
        name: 'receive',
        meta: {
            title: '收件箱',
            roles: ['user']
        },
        children: [
            {
                path: '/receive',
                component: () => import('@/views/receive/index.vue'),
                name: 'receiveIndex',
                meta: {
                    title: '收件箱',
                    icon: 'ri-user-received-line',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/draft',
        component: () => import('@/layouts/index.vue'),
        redirect: '/draft',
        name: 'draft',
        meta: {
            title: '草稿箱',
            roles: ['user']
        },
        children: [
            {
                path: '/draft',
                component: () => import('@/views/draft/index.vue'),
                name: 'draftIndex',
                meta: {
                    title: '草稿箱',
                    icon: 'ri-draft-line',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/sent',
        component: () => import('@/layouts/index.vue'),
        redirect: '/sent',
        name: 'sent',
        meta: {
            title: '发件箱',
            roles: ['user']
        },
        children: [
            {
                path: '/sent',
                component: () => import('@/views/sent/index.vue'),
                name: 'sentIndex',
                meta: {
                    title: '发件箱',
                    icon: 'ri-send-plane-line',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/garbage',
        component: () => import('@/layouts/index.vue'),
        redirect: '/garbage',
        name: 'garbage',
        meta: {
            title: '垃圾箱',
            roles: ['user']
        },
        children: [
            {
                path: '/garbage',
                component: () => import('@/views/garbage/index.vue'),
                name: 'garbage',
                meta: {
                    title: '垃圾箱',
                    icon: 'ri-delete-bin-line',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/flagged',
        component: () => import('@/layouts/index.vue'),
        redirect: '/collect',
        name: 'collect',
        meta: {
            title: '星标邮件',
            roles: ['user']
        },
        children: [
            {
                path: '/flagged',
                component: () => import('@/views/collect/index.vue'),
                name: 'collect',
                meta: {
                    title: '星标邮件',
                    icon: 'ri-star-line',
                    roles: ['user']
                }
            }
        ]
    },
    {
        path: '/search',
        component: () => import('@/layouts/index.vue'),
        redirect: '/search',
        name: 'search',
        meta: {
            title: '邮件查询',
            roles: ['user']
        },
        children: [
            {
                path: '/search',
                component: () => import('@/views/search/index.vue'),
                name: 'search',
                meta: {
                    title: '邮件查询',
                    icon: 'ri-search-line',
                    roles: ['user']
                }
            }
        ]
    }
];

export default menuRouter;
