<!--
 * @Author: your name
 * @Date: 2022-01-11 18:38:31
 * @LastEditTime: 2026-09-21 16:36:30
 * @LastEditors: mengjuhua
 * @Description: 侧边栏菜单项组件
-->
<template>
    <template v-if="!item.hidden">
        <template
            v-if="
                item.children && Array.isArray(item.children) && (isDynamicParent ? true : hasChildRoute(item.children))
            "
        >
            <el-sub-menu :index="item.path" class="y9-el-sub-menu">
                <template #title>
                    <!-- 动态父菜单图标，点击新增文件夹 -->
                    <i
                        v-if="item.meta?.icon"
                        :class="['icon', item.meta.icon]"
                        style="color: var(--el-color-primary)"
                        :title="isDynamicParent ? '点击添加自定义文件夹' : ''"
                        @click.stop="isDynamicParent ? onEditDynamicRoute(true) : null"
                    />
                    <span>{{ $t(`${item.meta?.title}`) }}</span>
                </template>
                <!-- 新增文件夹输入项 -->
                <el-menu-item v-if="isEditRoute">
                    <i class="ri-folder-3-line"></i>
                    <el-input
                        ref="routeInputRef"
                        v-model="dynamicRouteTitle"
                        placeholder="请输入文件夹名称"
                        @blur="onSaveDynamicRoute(true)"
                        @keyup.esc="cancelEdit"
                        @keyup.enter="onSaveDynamicRoute(true)"
                        @click.stop
                    ></el-input>
                </el-menu-item>
                <sider-menu-item
                    v-for="item2 in item.children"
                    :key="item2.path"
                    :belong-top-menu="belongTopMenu"
                    :parent-route="item"
                    :route-item="item2"
                    :open-sub-menu="openSubMenu"
                >
                </sider-menu-item>
            </el-sub-menu>
        </template>

        <template v-else>
            <!-- 编辑状态下用 div 替代 a-link，避免点击输入框触发 router-link 导航 -->
            <div v-if="isEditRoute">
                <el-menu-item
                    :index="item.path"
                    @click="toggleCollapsedFunc"
                    @contextmenu.prevent="onMouseRightEvent(item, $event)"
                >
                    <i v-if="item.meta?.icon" :class="['icon', item.meta.icon]" />
                    <template #title>
                        <el-input
                            v-if="isEditRoute"
                            ref="routeInputRef"
                            v-model="dynamicRouteTitle"
                            placeholder="请输入文件夹名称"
                            @blur="onSaveDynamicRoute(false)"
                            @keyup.esc="cancelEdit"
                            @keyup.enter="onSaveDynamicRoute(false)"
                            @click.stop
                        ></el-input>
                    </template>
                </el-menu-item>
            </div>
            <a-link v-else :to="item.path">
                <el-menu-item
                    :index="item.path"
                    @click="toggleCollapsedFunc"
                    @contextmenu.prevent="onMouseRightEvent(item, $event)"
                >
                    <i v-if="item.meta?.icon" :class="['icon', item.meta.icon]" />
                    <template #title>
                        <span>{{ $t(`${item.meta?.title}`) }}</span>
                    </template>
                </el-menu-item>
            </a-link>
        </template>
    </template>
</template>

<script lang="ts" setup>
    import { computed, inject, nextTick, onBeforeUnmount, onMounted, ref, toRefs } from 'vue';
    import { hasChildRoute, RoutesDataItem } from '@/utils/routes';
    import { useSettingStore } from '@/store/modules/settingStore';
    import ALink from '@/layouts/components/ALink/index.vue';
    import { ElMessage, ElNotification, ElMessageBox, ElInput } from 'element-plus';
    import router from '@/router';
    import { deleteFolder, saveFolder } from '@/api/folder/index';

    defineOptions({ name: 'SiderMenuItem' });

    interface Props {
        routeItem: RoutesDataItem;
        belongTopMenu?: string;
        openSubMenu?: (index: string) => void;
        parentRoute?: RoutesDataItem;
    }

    const props = withDefaults(defineProps<Props>(), {
        belongTopMenu: ''
    });

    const { routeItem: item } = toRefs(props);

    const fontSizeObj = inject<{ largeFontSize: string; baseFontSize: string }>('sizeObjInfo', {
        largeFontSize: '16px',
        baseFontSize: '14px'
    });

    const settingStore = useSettingStore();
    const { toggleCollapsed } = settingStore;
    const toggleCollapsedFunc = (e?: Event) => {
        // 编辑状态下阻止事件冒泡，避免触发 router-link 导航
        if (isEditRoute.value) {
            e?.stopPropagation();
            e?.preventDefault();
            return;
        }
        if (settingStore.getDevice === 'mobile') {
            toggleCollapsed();
        }
    };

    // 是否为动态文件夹
    const isDynamicFolder = computed(() => {
        const path = item.value.path || '';
        const metaFlag = item.value.meta?.isDynamic;
        return metaFlag || path.startsWith('/folder/');
    });

    // 是否为动态父菜单
    const isDynamicParent = computed(() => {
        if (item.value.meta?.isDynamic) return true;
        if (item.value.children?.length) {
            return item.value.children.some(
                (child) => child.meta?.isDynamic || (child.path && child.path.startsWith('/folder/'))
            );
        }
        return false;
    });

    // 动态路由状态
    const dynamicRouteTitle = ref('');
    const isEditRoute = ref(false);
    // 保存锁，防止 @keyup.enter 和 @blur 同时触发导致重复保存
    const isSaving = ref(false);
    const routeInputRef = ref<InstanceType<typeof ElInput> | null>(null);

    // 全局点击关闭右键菜单
    const globalClickHandler = (e: MouseEvent) => {
        const contextMenu = document.getElementById('globalContextMenu');
        if (contextMenu && !contextMenu.contains(e.target as HTMLElement)) {
            contextMenu.remove();
        }
    };

    // 取消编辑
    const cancelEdit = () => {
        isEditRoute.value = false;
        dynamicRouteTitle.value = '';
    };

    // 进入新增/编辑状态
    const onEditDynamicRoute = (isAdd: boolean) => {
        if (!isDynamicFolder.value && !isDynamicParent.value) return;

        if (isAdd) {
            props.openSubMenu?.(item.value.path);
        }

        dynamicRouteTitle.value = isAdd ? '' : item.value.meta?.title || '';
        isEditRoute.value = true;
        nextTick(() => {
            routeInputRef.value?.focus();
            if (!isAdd && dynamicRouteTitle.value) {
                routeInputRef.value?.select();
            }
        });
    };

    // 递归判断是否存在同名路由
    const isSameName = (data: any[], targetName: string): boolean => {
        const trimmedName = targetName.trim();
        for (const route of data) {
            if (route.name === trimmedName) return true;
            if (route.children?.length) {
                if (isSameName(route.children, trimmedName)) return true;
            }
        }
        return false;
    };

    // 保存新增/重命名的文件夹
    const onSaveDynamicRoute = async (isAdd: boolean) => {
        if (isSaving.value) return;
        isSaving.value = true;
        try {
            const trimmedTitle = dynamicRouteTitle.value.trim();
            if (!trimmedTitle) {
                cancelEdit();
                return;
            }
            dynamicRouteTitle.value = trimmedTitle;

            if (isAdd) {
                if (isSameName(router.getRoutes(), trimmedTitle)) {
                    ElNotification.error({
                        title: '新增失败',
                        message: '文件夹名称重复，请重试',
                        offset: 100
                    });
                    nextTick(() => routeInputRef.value?.focus());
                    return;
                }

                const res = await saveFolder({ newFolderName: trimmedTitle });
                if (res.code === 0 && res.success) {
                    const newRoute = {
                        path: '/folder/' + encodeURIComponent(trimmedTitle),
                        component: () => import('@/views/dynamic/dynamic.vue'),
                        name: trimmedTitle,
                        meta: {
                            title: trimmedTitle,
                            icon: 'ri-folder-3-line',
                            isDynamic: true,
                            id: trimmedTitle
                        },
                        props: { folder: trimmedTitle }
                    };
                    item.value.children?.unshift(newRoute);
                    if (item.value.name) {
                        router.addRoute(item.value.name as string, newRoute);
                    }
                    router.push({ path: newRoute.path });
                }
            } else {
                const oldTitle = item.value.meta?.title;
                // 标题未改则保持编辑状态
                if (oldTitle === trimmedTitle) {
                    return;
                }

                if (isSameName(router.getRoutes(), trimmedTitle)) {
                    ElNotification.error({
                        title: '修改失败',
                        message: '文件夹名称重复，请重试',
                        offset: 100
                    });
                    nextTick(() => routeInputRef.value?.focus());
                    return;
                }

                const res = await saveFolder({
                    originFolderName: oldTitle,
                    newFolderName: trimmedTitle
                });
                if (res.code === 0 && res.success && item.value.meta) {
                    const oldPath = item.value.path;
                    const newPath = `/folder/${encodeURIComponent(trimmedTitle)}`;
                    // webmail 路径基于文件夹名，重命名后路径变化，需要先移除旧路由再注册新路由
                    if (oldTitle) {
                        router.removeRoute(oldTitle);
                    }
                    item.value.path = newPath;
                    item.value.name = trimmedTitle;
                    item.value.meta.title = trimmedTitle;
                    item.value.meta.path = newPath;
                    item.value.meta.id = trimmedTitle;
                    if (item.value.props && typeof item.value.props === 'object') {
                        item.value.props.folder = trimmedTitle;
                    }
                    // 重新注册为父路由的子路由
                    if (props.parentRoute?.name) {
                        router.addRoute(props.parentRoute.name, item.value);
                    }

                    if (router.currentRoute.value.path === oldPath) {
                        router.replace(newPath);
                    }
                }
            }
            isEditRoute.value = false;
        } finally {
            isSaving.value = false;
        }
    };

    // 右键菜单：重命名、删除
    const onMouseRightEvent = (route: RoutesDataItem, event: MouseEvent) => {
        const isDynamic = route.meta?.isDynamic || (route.path && route.path.startsWith('/folder/'));
        if (!isDynamic) return;

        const globalContextMenu = document.getElementById('globalContextMenu');
        globalContextMenu?.remove();

        // 视口边界适配
        const menuWidth = 120;
        const menuHeight = 80;
        const x = Math.min(event.clientX, window.innerWidth - menuWidth);
        const y = Math.min(event.clientY, window.innerHeight - menuHeight);

        const menuDom = document.createElement('div');
        menuDom.className = 'global-context-menu';
        menuDom.style.position = 'fixed';
        menuDom.style.top = `${y}px`;
        menuDom.style.left = `${x}px`;
        menuDom.style.zIndex = '2000';
        menuDom.setAttribute('id', 'globalContextMenu');

        const menuList = ['重命名', '删除'];
        menuList.forEach((menuText) => {
            const menuItemDom = document.createElement('div');
            menuItemDom.innerHTML = menuText;
            menuItemDom.className = 'global-context-menu-item';
            menuItemDom.addEventListener('click', async (e) => {
                e.preventDefault();
                e.stopPropagation();
                if (menuText === '重命名') {
                    onEditDynamicRoute(false);
                } else if (menuText === '删除') {
                    ElMessageBox.confirm(`是否删除文件夹"${route.meta?.title}"？`, {
                        confirmButtonText: '删除',
                        cancelButtonText: '取消',
                        type: 'warning'
                    })
                        .then(async () => {
                            const delRes = await deleteFolder({ folder: route.meta?.title });
                            if (delRes.code === 0 && delRes.success) {
                                ElMessage.success(delRes.msg || '删除成功');
                                if (route.name) {
                                    router.removeRoute(route.name as string);
                                }
                                // 从父级路由的 children 中移除当前项
                                if (props.parentRoute?.children) {
                                    const targetIndex = props.parentRoute.children.indexOf(route);
                                    if (targetIndex > -1) {
                                        props.parentRoute.children.splice(targetIndex, 1);
                                    }
                                }
                                // 跳转到第一个子项，没有则跳转到收件箱
                                const parentChildren = props.parentRoute?.children || [];
                                if (parentChildren.length > 0) {
                                    router.replace(parentChildren[0].path);
                                } else {
                                    router.replace('/receive');
                                }
                            }
                        })
                        .catch(() => {});
                }
                document.getElementById('globalContextMenu')?.remove();
            });
            menuDom.appendChild(menuItemDom);
        });

        // 挂载到 body 避免被 overflow:hidden 截断
        document.body.appendChild(menuDom);
    };

    onMounted(() => {
        document.addEventListener('click', globalClickHandler);
    });

    onBeforeUnmount(() => {
        document.removeEventListener('click', globalClickHandler);
        document.getElementById('globalContextMenu')?.remove();
    });
</script>

<style lang="scss" scoped>
    .y9-el-sub-menu {
        & > div {
            text-decoration: none;
            i {
                font-size: v-bind('fontSizeObj.largeFontSize');
                margin-right: 15px;
            }
            span {
                font-size: v-bind('fontSizeObj.baseFontSize');
            }
        }
    }

    :deep(.el-menu-item) {
        font-size: v-bind('fontSizeObj.baseFontSize');
        .el-icon {
            font-size: v-bind('fontSizeObj.baseFontSize');
            color: inherit;
            margin-left: -3px;
            padding: 0;
            margin-right: 12px !important;
        }
        .el-input {
            width: 100%;
        }
    }

    .el-teleport,
    .el-popper {
        ul.el-menu {
            & > a {
                text-decoration: none;
            }
            li.el-menu-item {
                & > i {
                    font-size: v-bind('fontSizeObj.largeFontSize');
                    margin-right: 15px;
                }
            }
        }
    }

    .el-menu {
        background-color: transparent;
        li.el-menu-item {
            & > i {
                font-size: v-bind('fontSizeObj.largeFontSize');
                margin-right: 15px;
            }
        }
    }
</style>

<style>
    .y9-el-sub-menu.el-sub-menu .el-menu {
        background: transparent;
    }
    .global-context-menu {
        min-width: 100px;
        background: #ffffff;
        border-radius: 6px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
        padding: 4px 0;
        user-select: none;
    }
    .global-context-menu-item {
        padding: 8px 16px;
        font-size: 14px;
        color: #303133;
        cursor: pointer;
        transition: background 0.2s;
    }
    .global-context-menu-item:hover {
        background-color: #f5f7fa;
        color: #409eff;
    }
</style>
