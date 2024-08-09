import { remove } from 'lodash';
import { defineStore } from 'pinia';
import { useCssModule } from 'vue';
import { $dataType } from '@/utils/object';

export const usePublicTreeStore = defineStore('publicTreeStore', {
    state: () => {
        return {
            publicTreeSetting: {},
            initPublicTreeSetting: {
                treeId: 'publicTree', // 树id
                itemInterface: {
                    // 二（三）级节点接口传到组件内
                    api: '',
                    params: { parentId: '' },
                    callback: async (resData) => {
                        for (let i = 0; i < resData.length; i++) {
                            const item = resData[i];
                            switch (item.orgType) {
                                case 'Organization': //组织
                                    item.title_icon = 'ri-stackshare-line';
                                    break;

                                case 'Department': //部门
                                    item.title_icon = 'ri-slack-line';
                                    item.hasChild = true;
                                    item.newName = item.name;
                                    const selectType =
                                        usePublicTreeStore().publicTreeSetting.itemInterface.params.treeType;
                                    // if (selectType == 'tree_type_person' || selectType === 'tree_type_org_person') {
                                    //     let res = await getAllPersonsCount(item.id, item.orgType);
                                    //     item.newName = item.name + "(" + res.data + ")";
                                    //     item.personCount = res.data
                                    // }

                                    //判断是否有权限删除
                                    const guidPathArr = item.guidPath.split(',');
                                    const parentId = JSON.parse(sessionStorage.getItem('ssoUserInfo')).parentId;
                                    const isGlobalManager = ref(
                                        JSON.parse(sessionStorage.getItem('ssoUserInfo')).globalManager
                                    );

                                    if (isGlobalManager.value) {
                                        item.delete_icon =
                                            usePublicTreeStore().publicTreeSetting.itemInfo.keys.delete_icon;
                                    } else if (!guidPathArr.includes(parentId) || item.id === parentId) {
                                        item.delete_icon = false;
                                    } else {
                                        item.delete_icon =
                                            usePublicTreeStore().publicTreeSetting.itemInfo.keys.delete_icon;
                                    }
                                    break;

                                case 'Group': //组
                                    item.title_icon = 'ri-shield-star-line';
                                    item.newName = item.name;
                                    break;

                                case 'Position': //岗位
                                    item.title_icon = 'ri-shield-user-line';
                                    item.newName = item.name;
                                    break;
                                case 'Manager': //子域三元
                                    item.title_icon = 'ri-women-line';
                                    item.newName = item.name;
                                    if (item.sex == 1) {
                                        item.title_icon = 'ri-men-line';
                                    }
                                    break;
                                case 'Person': //人员
                                    item.title_icon = 'ri-women-line';
                                    item.newName = item.name;
                                    if (item.sex == 1) {
                                        item.title_icon = 'ri-men-line';
                                    }
                                    if (item.disabled) {
                                        item.newName = item.name + '[禁用]';
                                    }
                                    if (!item.original) {
                                        if (item.sex == 1) {
                                            item.title_icon = 'ri-men-fill';
                                        } else {
                                            item.title_icon = 'ri-women-fill';
                                        }
                                    }
                                    break;
                            }

                            // 资源
                            switch (item.resourceType) {
                                case 0: //应用
                                    item.title_icon = 'ri-apps-line';
                                    item.hasChild = true;
                                    break;

                                case 1: //菜单
                                    item.title_icon = 'ri-menu-4-line';
                                    item.hasChild = true;
                                    break;

                                case 2: //按钮
                                    item.title_icon = 'ri-checkbox-multiple-blank-line';
                                    item.hasChild = true;
                                    break;
                            }

                            // 角色
                            switch (item.type) {
                                case 'role': //角色 人员
                                    item.title_icon = 'ri-contacts-line';
                                    item.hasChild = true;
                                    break;

                                case 'folder': // 文件夹
                                    item.title_icon = 'ri-folder-2-line';
                                    item.hasChild = true;
                                    break;
                            }
                        }

                        return resData; // 如果有回调，则需要返回data - [Array类型]
                    }
                },
                itemGroupPrefix: 'itemGroup-', // 有 children节点的 ID 前缀（ 完整示例：'itemGroup-' + item.id ）
                data: [], // 一级接口接口返回的源数据
                itemInfo: {
                    keys: {
                        // 数据字段映射
                        id: 'id', // id
                        parentId: 'parentId', // parentId
                        name: 'name', // name
                        children: 'children', // children
                        hasChild: 'hasChild', // haschild
                        title: 'name', // title 映射接口数据的 name 示例
                        subTitle: 'name', // subtitle 映射接口数据的 name 示例
                        // 以下字段 如果有，则显示映射的值，否则不显示
                        // checkbox: "checkbox",   // 复选框 实现每个 item 个性化显示复选框
                        // add_icon: 'add_icon', // 实现每个 item 个性化显示 增 事件
                        delete_icon: 'delete_icon', // 实现每个 item 个性化显示 删 事件
                        // edit_icon: 'edit_icon', // 实现每个 item 个性化显示 改 事件
                        // select_icon: "select_icon",  // 实现每个 item 个性化显示 查 事件
                        title_icon: 'title_icon' // 实现每个 item 个性化显示 icon
                    },
                    render: {
                        subTitle: {
                            // subTitle
                            show: false // true 渲染
                        },
                        checkbox: {
                            // 复选框
                            func: (data) => {
                                // 复选框事件
                                // console.log('点击了复选框', data, data.checked ? '勾选状态' : '取消勾选状态');
                            }
                        },
                        add_icon: {
                            // 增
                            text: '', // 设置 add_icon文本，空则不渲染
                            sort: 0,
                            icon: 'ri-add-line',
                            func: (data) => {
                                // console.log('点击了 add_icon', data);
                            }
                        },
                        delete_icon: {
                            // 删
                            text: '', // 设置 delete_icon文本，空则不渲染
                            sort: 4,
                            icon: 'ri-delete-bin-7-line',
                            func: (data) => {
                                // 设置 delete_icon 点击事件
                                // console.log('点击了 delete_icon', data);
                            }
                        },
                        edit_icon: {
                            // 改
                            text: '', // 设置 edit_icon文本，空则不渲染
                            sort: 2,
                            icon: 'ri-quill-pen-line',
                            func: (data) => {
                                // console.log('点击了 edit_icon', data);
                            }
                        },
                        select_icon: {
                            // 查
                            text: '', // 设置 select_icon文本，空则不渲染
                            sort: 3,
                            icon: 'ri-eye-line',
                            func: (data) => {
                                // console.log('点击了 select_icon', data);
                            }
                        },
                        mouse_over: {
                            // 鼠标悬停事件
                            func: (e) => {
                                if (e && e.originalTarget) {
                                    const li = e.originalTarget;
                                    if (li.className === 'treeItem') {
                                        li.style.backgroundColor = '#E7ECED';
                                    }
                                }
                            }
                        },
                        mouse_leave: {
                            // 鼠标离开事件
                            func: (e) => {
                                if (e && e.originalTarget) {
                                    const li = e.originalTarget;
                                    li.style.backgroundColor = '';
                                }
                            }
                        },
                        click: {
                            // 单击事件
                            func: (data) => {
                                // console.log('单击事件', data);
                            }
                        },
                        dbl_click: {
                            // 双击事件
                            func: (data) => {
                                // console.log('双击事件', data);
                            }
                        },
                        click_title_event: {
                            // 点击 title 事件
                            func: (data) => {
                                // 点击 title 事件
                                // console.log('点击了 title', data);
                            }
                        }
                    }
                },
                events: {
                    search: {
                        // 搜索功能
                        api: '',
                        params: {
                            // 搜索接口的参数对象
                            key: '' // 至少有这个属性，且必须为key
                        },
                        callback: (data) => {
                            /*
                                设置搜索结果的数据 注意保存原有数据
                            */
                            console.log('search - ', data);
                        }
                    },
                    dragEvent: {
                        draggable: false,
                        dragendCallback: (data) => {
                            /*
                                data.parentItemInfo: String（被拖拽的节点的父节点的信息，如果没有则为空，即被拖放到树一级节点，）
                                data.itemInfo: String（被拖拽的节点信息）
                                data.preItemInfo: String（被拖放的节点的前一个节点信息，如果没有则为空，即当前同级位置首节点）
                                data.nextItemInfo: String（被拖放的节点的后一个节点信息，如果没有则为空，即当前同级位置尾节点）
                            */
                            if (data.itemInfo) {
                                console.log('拖拽事件(itemInfo) >>>> ', JSON.parse(data.itemInfo));
                            }
                            if (data.parentItemInfo) {
                                console.log('拖拽事件(parentItemInfo) >>>> ', JSON.parse(data.parentItemInfo));
                            }
                            if (data.preItemInfo) {
                                console.log('拖拽事件(preItemInfo) >>>> ', JSON.parse(data.preItemInfo));
                            }
                            if (data.nextItemInfo) {
                                console.log('拖拽事件(nextItemInfo) >>>> ', JSON.parse(data.nextItemInfo));
                            }
                        }
                    }
                },
                style: {
                    li: {
                        // li activeClass
                        activeClassName: useCssModule('classes').active
                    },
                    animation: {
                        in: 'fadeInLeftBig',
                        out: 'fadeOutRight'
                    }
                }
            }
        };
    },
    getters: {
        getPublicTreeSetting: (state) => {
            return state.publicTreeSetting;
        }
    },
    actions: {
        // 树组件 - 一级节点接口数据
        // axios API 使用一级节点渲染树组件
        async getTree(interfacePromise) {
            try {
                let tree = [];

                if (interfacePromise && $dataType(interfacePromise == 'function')) {
                    tree = await interfacePromise();
                }

                const treeData = tree.data;

                for (let i = 0; i < treeData.length; i++) {
                    const item = treeData[i];
                    // 组织架构
                    switch (item.orgType) {
                        case 'Organization': //组织
                            item.title_icon = 'ri-stackshare-line';
                            item.hasChild = true;
                            item.newName = item.name;

                            const selectType = usePublicTreeStore().publicTreeSetting.itemInterface.params.treeType;
                            // if (selectType == 'tree_type_person' || selectType === 'tree_type_org_person') {
                            //     let res = await getAllPersonsCount(item.id, item.orgType);
                            //     item.newName = item.name + "(" + res.data + ")";
                            //     item.personCount = res.data
                            // }

                            const isGlobalManager = JSON.parse(sessionStorage.getItem('ssoUserInfo')).globalManager;
                            if (!isGlobalManager) {
                                item.delete_icon = false;
                            }

                            break;
                    }
                    // 系统
                    if (item.cnName) {
                        item.title_icon = 'ri-settings-line';
                    }

                    //公共角色管理
                    if (item.type && item.type === 'folder') {
                        item.delete_icon = false;
                        item.title_icon = 'ri-folder-2-line';
                        item.hasChild = true;
                    }

                    // 资源
                    switch (item.resourceType) {
                        case 0: //应用
                            item.title_icon = 'ri-apps-line';
                            item.delete_icon = false;
                            item.hasChild = true;
                            break;

                        case 1: //菜单
                            item.title_icon = 'ri-menu-4-line';
                            item.hasChild = true;
                            break;

                        case 2: //按钮
                            item.title_icon = 'ri-checkbox-multiple-blank-line';
                            break;
                    }
                }

                this.publicTreeSetting.data = treeData;
            } catch (error) {
                console.log('publicTreeStore-error', error);
            }
        },
        // 删除一个树节点，以 id 为例
        deleteItem(key, value) {
            const deepTraversal = (array, value) => {
                for (let index = 0; index < array.length; index++) {
                    const arrayItem = array[index];
                    if (arrayItem[key] === value) {
                        remove(array, (item) => item[key] === value);
                    }
                    if (arrayItem.children) {
                        deepTraversal(arrayItem.children, value);
                    }
                }
                return false;
            };
            deepTraversal(this.getPublicTreeSetting.data, value);
        },
        getChildById(parentId) {
            //重新获取子节点
            const deepTraversal = async (array, parentId) => {
                for (let index = 0; index < array.length; index++) {
                    const arrayItem = array[index];
                    if (arrayItem.id === parentId) {
                        if (arrayItem.children != undefined) {
                            const params = this.getPublicTreeSetting.itemInterface.params;
                            params.parentId = parentId;
                            const child = await this.getPublicTreeSetting.itemInterface.api(params);
                            child.data.forEach(async (item) => {
                                item.newName = item.name;
                                switch (item.orgType) {
                                    case 'Department': //部门
                                        item.title_icon = 'ri-slack-line';
                                        item.hasChild = true;
                                        const selectType =
                                            usePublicTreeStore().publicTreeSetting.itemInterface.params.treeType;
                                        // if (selectType == 'tree_type_person' || selectType === 'tree_type_org_person') {
                                        //     let res = await getAllPersonsCount(item.id, item.orgType);
                                        //     item.newName = item.name + "(" + res.data + ")";
                                        //     item.personCount = res.data
                                        // }
                                        break;
                                    case 'Group': //组
                                        item.title_icon = 'ri-shield-star-line';
                                        break;
                                    case 'Position': //岗位
                                        item.title_icon = 'ri-shield-user-line';
                                        break;
                                    case 'Manager': //子域三元
                                        item.title_icon = 'ri-women-line';
                                        if (item.sex == 1) {
                                            item.title_icon = 'ri-men-line';
                                        }
                                        break;
                                    case 'Person': //人员
                                        item.title_icon = 'ri-women-line';
                                        if (item.sex == 1) {
                                            item.title_icon = 'ri-men-line';
                                        }

                                        if (item.disabled) {
                                            item.newName = item.name + '[禁用]';
                                        }

                                        if (!item.original) {
                                            if (item.sex == 1) {
                                                item.title_icon = 'ri-men-fill';
                                            } else {
                                                item.title_icon = 'ri-women-fill';
                                            }
                                        }
                                        break;
                                }
                            });
                            arrayItem.children = child.data;
                            return true;
                        }
                        return false;
                    }
                    if (arrayItem.children) {
                        deepTraversal(arrayItem.children, parentId);
                    }
                }
                return false;
            };
            deepTraversal(this.getPublicTreeSetting.data, parentId);
        }
    }
});
