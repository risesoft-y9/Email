import { remove } from 'lodash';
import { defineStore } from 'pinia';
import { useCssModule } from 'vue';

import { $dataType, $deeploneObject } from '@/utils/object.ts';

export const useSelectTreeStore = defineStore('selectTreeStore', {
    state: () => {
        return {
            selectTreeSetting: {},
            initSelectTreeSetting: {
                treeId: 'selectTree', // 树id
                itemInterface: {
                    // 二（三）级节点接口传到组件内
                    api: '',
                    params: { parentId: '' },
                    callback: (resData) => {
                        // 二（三）级节点接口 数据的回调函数
                        resData.forEach((item) => {
                            const treeType = useSelectTreeStore().getSelectTreeSetting.itemInterface.params.treeType;
                            console.log(treeType, 'type');
                            // undefined 就default
                            switch (treeType) {
                                case 'tree_type_org_person':
                                    const filterDataS = (resData) => {
                                        return resData.filter((item) =>
                                            item.children
                                                ? filterDataS(item.children)
                                                : item.orgType === 'Department' || item.orgType === 'Person'
                                        );
                                    };
                                    resData = filterDataS(resData);
                                    break;
                                case 'tree_type_position':
                                    const filterData = (resData) => {
                                        return resData.filter((item) =>
                                            item.children
                                                ? filterData(item.children)
                                                : item.orgType === 'Department' || item.orgType === 'Position'
                                        );
                                    };
                                    resData = filterData(resData);

                                    break;
                                default:
                                    break;
                            }
                            switch (item.orgType) {
                                case 'Department': //部门
                                    item.title_icon = 'ri-slack-line';
                                    item.hasChild = true;
                                    break;

                                case 'Group': //组
                                    item.title_icon = 'ri-shield-star-line';
                                    break;

                                case 'Position': //岗位
                                    item.title_icon = 'ri-shield-user-line';
                                    break;

                                case 'Person': //人员
                                    item.title_icon = 'ri-women-line';
                                    if (item.sex == 1) {
                                        item.title_icon = 'ri-men-line';
                                    }
                                    if (item.disabled) {
                                        item.name = item.name + '[禁用]';
                                    }
                                    break;

                                default:
                                    item.title_icon = '';
                            }
                            // 资源
                            switch (item.resourceType) {
                                case 0: //应用
                                    item.title_icon = 'ri-apps-line';
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
                                    item.title_icon = 'ri-women-line';
                                    item.hasChild = true;
                                    break;

                                case 'folder': // 文件夹
                                    item.title_icon = 'ri-folder-2-line';
                                    item.hasChild = true;
                                    break;
                            }
                        });

                        resData = useSelectTreeStore().selectTreeSetting.itemInterfaceCallBack(resData);

                        return resData; // 如果有回调，则需要返回data - [Array类型]
                    }
                },
                itemInterfaceCallBack: (data) => {
                    //二级接口数据回调函数

                    return data;
                },
                topLevelInterfaceCallBack: (data) => {
                    //一级级接口数据回调函数

                    return data;
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
                        checkbox: 'checkbox', // 复选框 实现每个 item 个性化显示复选框
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
                                console.log('点击了复选框', data, data.checked ? '勾选状态' : '取消勾选状态');
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
        getSelectTreeSetting: (state) => {
            return state.selectTreeSetting;
        }
    },
    actions: {
        // 树组件 - 一级节点接口数据
        // axios API 使用一级节点渲染树组件
        async getTree(interfacePromise) {
            try {
                let tree = {};

                if (interfacePromise && $dataType(interfacePromise == 'function')) {
                    tree = await interfacePromise();
                }

                let treeData = tree.data;

                treeData.forEach((item) => {
                    item.name = item.name;
                    item.title_icon = 'ri-stackshare-line';
                    item.hasChild = true;
                });

                treeData = useSelectTreeStore().selectTreeSetting.topLevelInterfaceCallBack(treeData); //一级接口数据回调

                this.selectTreeSetting.data = treeData;
            } catch (error) {
                console.log('selectTreeStore-error', error);
            }
        }
    }
});
