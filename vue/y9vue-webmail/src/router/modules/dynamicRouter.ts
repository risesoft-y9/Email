/*
 * @Author: fanzhengyang
 */
/*
 * @Author: fanzhengyang
 */
/*
 * @Author: fanzhengyang
 */
const dynamicRouter = {
    path: '/dynamicIndex',
    component: () => import('@/layouts/index.vue'),
    redirect: '/archived',
    name: 'dynamicIndex',
    meta: {
        title: '文件夹',
        isDynamic: true,
        icon: 'ri-folder-add-line'
    },
    children: [
        // {
        // 	path: "/archived",
        // 	component: () => import("@/views/dynamic/archived.vue"),
        // 	name: "archived",
        // 	meta: { title: "办件邮件", icon: "ri-archive-drawer-line" }
        // },
        // {
        // 	path: "/spam",
        // 	component: () => import("@/views/dynamic//spam.vue"),
        // 	name: "/spam",
        // 	meta: { title: "提醒邮件", icon: "ri-indeterminate-circle-line" }
        // }
        // ,
        // {
        // 	path: "/deleted",
        // 	component: () => import("@/views/dynamic/deleted.vue"),
        // 	name: "deleted",
        // 	meta: { title: "待办邮件", icon: "ri-delete-bin-line" }
        // }
    ]
};

export default dynamicRouter;
