/*
 * @Author: fanzhengyang
 * @Date: 2021-12-22 15:41:55
 * @LastEditTime: 2026-09-21 16:05:06
 * @LastEditors: mengjuhua
 * @Description:  检查路由权限
 * @FilePath: \y9-vue\y9vue-webmail\src\router\checkRole.js
 */
import router, { asyncRoutes } from '@/router';
import { useRouterStore } from '@/store/modules/routerStore';
import { useFolderStore } from '@/store/modules/folderStore';

/**
 * 根据 meta.role 判断当前用户是否有权限
 * @param roles 用户的权限
 * @param route 路由
 */
function hasPermission(roles, route) {
    if (route.meta && route.meta.roles) {
        return roles.some((role) => route.meta.roles.includes(role));
    } else {
        return true;
    }
}

/**
 * 根据权限 - 递归过滤异步路由 - 深度优先遍历 - 留下有权限的路由
 * @param routes asyncRoutes
 * @param roles
 */
function filterAsyncRoutes(routes, roles) {
    const res = [];

    routes.forEach((route) => {
        const tmp = { ...route };
        if (hasPermission(roles, tmp)) {
            if (tmp.children) {
                tmp.children = filterAsyncRoutes(tmp.children, roles);
            }
            res.push(tmp);
        }
    });
    return res;
}

/**
 * 获取对应权限路由
 * @param routes asyncRoutes
 * @param roles
 */
export async function getPermissionRoutes(rolesArr = ['systemAdmin']) {
    const routerStore = useRouterStore();
    let folderStore = useFolderStore();
    // 获取动态路由后把动态路由添加进文件夹
    await folderStore.initAllFolders();
    let customFolders = folderStore.getCustomFolders;

    asyncRoutes.forEach((item) => {
        if (item.meta?.isDynamic) {
            // 后端返回的文件夹名称集合，用于清理已删除的文件夹
            const backendNames = new Set(customFolders.map((f) => f.name));

            // 清理后端已删除的动态路由
            item.children = item.children.filter((child) => {
                if (child.meta?.isDynamic && child.meta?.id != null && !backendNames.has(child.meta.id)) {
                    return false;
                }
                return true;
            });

            // 用 name 建立已有动态路由的映射，便于按 name 去重和同步更新
            const routeMap = new Map();
            item.children.forEach((child) => {
                if (child.meta?.isDynamic && child.meta?.id != null) {
                    routeMap.set(child.meta.id, child);
                }
            });

            customFolders.forEach((dynamicItem) => {
                const existing = routeMap.get(dynamicItem.name);
                if (existing) {
                    // 已存在则同步后端的最新名称
                    existing.name = dynamicItem.name;
                    existing.meta.title = dynamicItem.name;
                    existing.path = '/folder/' + encodeURIComponent(dynamicItem.name);
                    existing.props = { folder: dynamicItem.name };
                } else {
                    // 不存在则新增
                    item.children.unshift({
                        path: '/folder/' + encodeURIComponent(dynamicItem.name),
                        component: () => import('../views/dynamic/dynamic.vue'),
                        name: dynamicItem.name,
                        meta: { title: dynamicItem.name, icon: 'ri-folder-3-line', isDynamic: true, id: dynamicItem.name },
                        props: { folder: dynamicItem.name }
                    });
                }
            });
        }
    });

    const roles = rolesArr;
    const permissionRoutes = filterAsyncRoutes(asyncRoutes, roles);
    // 项目存储中心 pinia - routerStore模块 存储有权限的所有路由源数据
    routerStore.$patch({
        PermissionRoutes: permissionRoutes
    });
    return permissionRoutes;
}

// 查询路由权限
export async function checkRole(rolesArr = ['systemAdmin']) {
    // 获取权限路由
    const permissionRoutes = await getPermissionRoutes(rolesArr);
    if (permissionRoutes.length !== 0) {
        await permissionRoutes.map((route) => {
            router.addRoute(route);
        });
        return permissionRoutes;
    } else {
        console.log('没有权限');
        return false;
    }
}
