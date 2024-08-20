<!--
 * @Author: your name
 * @Date: 2022-01-11 18:38:31
 * @LastEditTime: 2022-12-29 19:03:32
 * @LastEditors: Please set LastEditors
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-home/src/layouts/components/SiderMenuItem.vue
-->
<template>
    <template v-if="!item.hidden">
        <template
            v-if="
                item.children &&
                Array.isArray(item.children) &&
                (item.meta?.isDynamic ? true : hasChildRoute(item.children))
            "
        >
            <el-sub-menu :index="item.path" class="y9-el-sub-menu">
                <template #title>
                    <i
                        v-if="item.meta.icon"
                        :class="['icon', item.meta.icon]"
                        style="color: var(--el-color-primary)"
                        title="点击添加自定义文件夹"
                        @click.stop="onEditDynamicRoute(true)"
                    />
                    <span>{{ $t(`${item.meta.title}`) }}</span>
                </template>
                <el-menu-item v-if="isEditRoute">
                    <i class="ri-folder-3-line"></i>
                    <el-input
                        ref="routeInputRef"
                        v-model="dynamicRouteTitle"
                        @blur="onSaveDynamicRoute(true)"
                    ></el-input>
                </el-menu-item>
                <sider-menu-item
                    v-for="item2 in item.children"
                    :key="item2.path"
                    :belong-top-menu="belongTopMenu"
                    :parent-route="item"
                    :route-item="item2"
                >
                </sider-menu-item>
            </el-sub-menu>
        </template>
        <template v-else>
            <a-link :to="item.path">
                <el-menu-item
                    :index="item.path"
                    @click="toggleCollapsedFunc"
                    @contextmenu.prevent="onMouseRightEvent(item, $event)"
                >
                    <!-- <Icon v-if="item.meta.icon" :type="item.meta.icon" class="icon" /> -->
                    <i v-if="item.meta.icon" :class="['icon', item.meta.icon]" />
                    <template #title>
                        <el-input
                            v-if="isEditRoute"
                            ref="routeInputRef"
                            v-model="dynamicRouteTitle"
                            @blur="onSaveDynamicRoute(false)"
                        ></el-input>
                        <span v-else>{{ $t(`${item.meta.title}`) }}</span>
                    </template>
                </el-menu-item>
            </a-link>
        </template>
    </template>
</template>

<script lang="ts">
    import { computed, ComputedRef, defineComponent, PropType, Ref, toRefs } from 'vue';
    import { getRouteBelongTopMenu, hasChildRoute, RoutesDataItem } from '@/utils/routes';
    import { useSettingStore } from '@/store/modules/settingStore';
    import ALink from '@/layouts/components/ALink/index.vue';
    import Icon from './Icon.vue';
    import { ElMessage } from 'element-plus';
    import router from '@/router';
    import { deleteFolder, saveFolder } from '@/api/folder/index';
    import { useFolderStore } from '@/store/modules/folderStore';

    interface SiderMenuItemSetupData {
        item: Ref;
        topMenuPath: ComputedRef<string>;
        hasChildRoute: (children: RoutesDataItem[]) => boolean;
        toggleCollapsedFunc: () => void;
        fontSizeObj: Object;
    }

    export default defineComponent({
        name: 'SiderMenuItem',
        props: {
            routeItem: {
                type: Object as PropType<RoutesDataItem>,
                required: true
            },
            belongTopMenu: {
                type: String,
                default: ''
            },
            openSubMenu: {
                //展开指定的 sub-menu,参数index: 需要打开的 sub-menu 的 index
                type: Function
            },
            parentRoute: {
                //父级路由对象
                type: Object as PropType<RoutesDataItem>
            }
        },
        components: {
            ALink,
            Icon
        },
        setup(props): SiderMenuItemSetupData {
            const { routeItem } = toRefs(props);
            // 注入 字体变量
            const fontSizeObj: any = inject('sizeObjInfo');
            const topMenuPath = computed<string>(() => getRouteBelongTopMenu(routeItem.value as RoutesDataItem));

            const settingStore = useSettingStore();
            const { toggleCollapsed } = settingStore;
            const toggleCollapsedFunc = () => {
                if (settingStore.getDevice === 'mobile') {
                    toggleCollapsed();
                }
            };

            //动态路由名称
            let dynamicRouteTitle = ref('');
            //是否为编辑路由状态
            let isEditRoute = ref(false);
            //路由编辑input实例
            let routeInputRef = ref();

            //添加时编辑动态菜单
            const onEditDynamicRoute = (isAdd) => {
                if (isAdd) {
                    props.openSubMenu && props.openSubMenu(routeItem.value.path); //展开菜单
                }

                if (routeItem.value.meta.isDynamic) {
                    dynamicRouteTitle.value = isAdd ? '' : routeItem.value.meta.title;
                }
                isEditRoute.value = true; //设为编辑状态
                nextTick(() => {
                    routeInputRef.value?.focus(); //聚焦编辑状态下的input
                });
            };

            //判断是否存在同名路由
            const isSameName = (data, targetName) => {
                let flag = false;
                for (let i = 0; i < data.length; i++) {
                    const route = data[i];
                    if (route.name === targetName) {
                        flag = true;
                        break;
                    } else if (route.children && route.children.length > 0) {
                        isSameName(route.children, targetName);
                    }
                }
                return flag;
            };

            //input失去焦点时，保存动态路由
            const onSaveDynamicRoute = async (isAdd) => {
                if (dynamicRouteTitle.value) {
                    //判断输入框是否有值
                    if (isAdd) {
                        //新增
                        if (isSameName(router.getRoutes(), dynamicRouteTitle.value)) {
                            //判断是否存在同名路由
                            ElNotification.error({
                                title: '新增失败',
                                message: '文件夹名称重复，请重试',
                                offset: 100
                            });
                        } else {
                            const res = await saveFolder({
                                //保存新增文件夹
                                newFolderName: dynamicRouteTitle.value
                            });

                            if (res.code === 0 && res.success) {
                                //保存成功前端才做处理
                                await useFolderStore().initAllFolders();

                                let newRoute = {
                                    path: '/folder/' + encodeURIComponent(dynamicRouteTitle.value),
                                    //	path: "/folder" + res.data.id,
                                    component: () => import('@/views/dynamic/dynamic.vue'),
                                    name: dynamicRouteTitle.value,
                                    meta: {
                                        title: dynamicRouteTitle.value,
                                        icon: 'ri-folder-3-line',
                                        isDynamic: true,
                                        id: dynamicRouteTitle.value
                                    },
                                    props: { folder: dynamicRouteTitle.value }
                                };
                                routeItem.value.children.unshift(newRoute);
                                router.addRoute(routeItem.value.name, newRoute);
                                // router.push({path: newRoute.path});
                            }
                        }
                    } else {
                        //编辑
                        if (routeItem.value.meta.title !== dynamicRouteTitle.value) {
                            //判断路由是否有改变
                            if (isSameName(router.getRoutes(), dynamicRouteTitle.value)) {
                                //判断是否存在同名路由
                                ElNotification.error({
                                    title: '修改失败',
                                    message: '文件夹名称重复，请重试',
                                    offset: 100
                                });
                            } else {
                                const res = await saveFolder({
                                    originFolderName: routeItem.value.meta.title,
                                    newFolderName: dynamicRouteTitle.value
                                });
                                if (res.code === 0 && res.success) {
                                    await useFolderStore().initAllFolders();

                                    // routeItem.value.path = '/'+ dynamicRouteTitle.value;
                                    routeItem.value.path = '/folder/' + encodeURIComponent(routeItem.value.meta.title);
                                    routeItem.value.name = dynamicRouteTitle.value;
                                    routeItem.value.meta.title = dynamicRouteTitle.value;
                                    routeItem.value.meta.path = '/folder' + routeItem.value.meta.title;
                                    routeItem.value.props.folder = dynamicRouteTitle.value;
                                    router.replace('/folder/' + encodeURIComponent(routeItem.value.meta.title));
                                }
                            }
                        }
                    }
                }

                isEditRoute.value = false; //取消编辑状态
            };

            //鼠标右键点击事件
            const onMouseRightEvent = (route, event) => {
                if (route.meta.isDynamic) {
                    //动态路由才出现右键菜单

                    //判断是否存在右键菜单，存在先移除
                    const globalContextMenu = document.getElementById('globalContextMenu');
                    if (globalContextMenu) {
                        globalContextMenu.remove();
                    }

                    //创建右键菜单dom节点
                    const menuDom = document.createElement('div');
                    menuDom.className = 'global-context-menu';
                    menuDom.style.position = 'fixed';
                    menuDom.style.top = event.y + 'px';
                    menuDom.style.left = event.x + 'px';
                    menuDom.style.zIndex = '2000';
                    menuDom.setAttribute('id', 'globalContextMenu');

                    let list = ['重命名', '删除'];
                    list.forEach((item) => {
                        const menuItemDom = document.createElement('div');
                        menuItemDom.innerHTML = item;
                        menuItemDom.className = 'global-context-menu-item';
                        menuItemDom.addEventListener('click', (e) => {
                            e.preventDefault(); //阻止捕获
                            e.stopPropagation();

                            if (e.target.innerHTML === '重命名') {
                                onEditDynamicRoute(false);
                            } else if (e.target.innerHTML === '删除') {
                                ElMessageBox.confirm(`是否删除文件夹“${route.meta.title}”？`, {
                                    confirmButtonText: '删除',
                                    cancelButtonText: '取消',
                                    center: true
                                })
                                    .then(() => {
                                        //1.请求删除路由的接口
                                        //2.请求成功之后进行以下操作：
                                        deleteFolder({ folder: route.meta.title }).then((res) => {
                                            if (res.success) {
                                                ElMessage({
                                                    type: 'success',
                                                    message: res.msg
                                                });
                                            }
                                        });
                                        //删除路由
                                        router.removeRoute(route.name);
                                        if (props.parentRoute) {
                                            for (let i = 0; i < props.parentRoute.children.length; i++) {
                                                const route2 = props.parentRoute.children[i];
                                                if (route2.name === route.name) {
                                                    props.parentRoute.children.splice(i, 1);
                                                    break;
                                                }
                                            }
                                        }

                                        //如果还有动态路由就跳转到第一个动态路由，没有则跳转到/receive
                                        if (props.parentRoute.children.length > 0) {
                                            router.replace(props.parentRoute.children[0].path);
                                        } else {
                                            router.replace('/receive');
                                        }
                                    })
                                    .catch((err) => {});
                            }
                            document.getElementById('globalContextMenu').remove();
                        });
                        menuDom.appendChild(menuItemDom);
                    });

                    event.target.appendChild(menuDom);
                }
            };

            return {
                item: routeItem,
                topMenuPath: topMenuPath,
                hasChildRoute,
                toggleCollapsedFunc,
                isEditRoute,
                dynamicRouteTitle,
                isSameName,
                routeInputRef,
                onEditDynamicRoute,
                onSaveDynamicRoute,
                onMouseRightEvent,
                fontSizeObj
            };
        },

        mounted() {
            //id='globalContextMenu' 以外的dom元素添加点击事件，让id='globalContextMenu'气泡框消失
            document.addEventListener('click', (e) => {
                let div = document.getElementById('globalContextMenu');
                if (div && !div.contains(e.target)) {
                    div.remove();
                }
            });
        }
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
        background-color: none;

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
</style>
