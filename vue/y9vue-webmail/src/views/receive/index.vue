<!--
 * @Author:  shidaobang
 * @Date: 2022-08-02 10:51:50
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-08-19 15:57:44
 * @Description: 收件箱
-->
<template>
    <y9Table
        v-model:selectedVal="tableCurrSelectedVal"
        :config="tableConfig"
        :filter-config="filterConfig"
        @on-curr-page-change="handlerPageChange"
        @on-page-size-change="handlerSizeChange"
        @row-click="rowClick"
    >
        <template #addIcon>
            <el-dropdown>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                >
                    <i class="ri-drag-move-line"></i>{{ $t('移动到') }}
                </el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item
                            v-for="item in folderStore.getAllFolders"
                            :key="item.name"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            @click="move(item.name)"
                        >
                            {{ item.title }}
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
            <el-dropdown>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third margin-left-12"
                >
                    <i class="ri-pencil-ruler-2-line"></i>{{ $t('标记为') }}
                </el-button>

                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="flag('read')">
                            已读邮件
                        </el-dropdown-item>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="flag('unread')">
                            未读邮件
                        </el-dropdown-item>
                        <el-divider style="padding-bottom: 6px; margin: 0px; margin-top: 6px"></el-divider>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="flag('star')">
                            星标邮件
                        </el-dropdown-item>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="flag('unstar')">
                            取消星标
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>

            <el-dropdown>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third margin-left-12"
                >
                    <i class="ri-list-check"></i>{{ $t('查看') }}
                </el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="read('')">
                            全部
                        </el-dropdown-item>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="read('已读')">
                            已读
                        </el-dropdown-item>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="read('未读')">
                            未读
                        </el-dropdown-item>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="read('星标件')">
                            星标件
                        </el-dropdown-item>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="read('有附件')">
                            有附件
                        </el-dropdown-item>
                        <el-dropdown-item :style="{ fontSize: fontSizeObj.baseFontSize }" @click="read('无附件')">
                            无附件
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-third margin-left-12"
                @click="refresh"
            >
                <i class="ri-refresh-line"></i>
                <span>{{ $t('刷新') }}</span>
            </el-button>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-third margin-left-12"
                @click="completelyDelete"
            >
                <i class="ri-delete-bin-line"></i>
                <span>{{ $t('删除') }}</span>
            </el-button>
        </template>
    </y9Table>
</template>

<script lang="ts" setup>
    import { h, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import router from '@/router';
    import { deleteEmail, emailList, flagEmail, moveToEmail, readEmail, searchEmail } from '@/api/email/index';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useFolderStore } from '@/store/modules/folderStore';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const { t } = useI18n();

    const settingStore = useSettingStore();
    const folderStore = useFolderStore();
    const folder = ref('INBOX');

    const data = reactive({
        currFilters: {}, //当前选择的过滤数据
        loading: false, // 全局loading
        tableConfig: {
            //表格配置
            border: false,
            headerBackground: true,
            columns: [
                {
                    type: 'selection',
                    width: 60
                },

                {
                    title: '状态',
                    key: 'read',
                    width: 70,
                    render(row) {
                        return h('i', {
                            class: row.read == true ? 'ri-mail-open-line' : 'ri-mail-line',
                            style: 'font-size: 16px;vertical-align: middle;'
                        });
                    }
                },
                {
                    title: '星标',
                    key: 'star',
                    width: 70,
                    render(row) {
                        return h('i', {
                            class: row.flagged == true ? 'ri-star-fill' : 'ri-star-line',
                            style: 'font-size: 16px;vertical-align: middle;',
                            onClick: (e) => {
                                e.stopPropagation();
                                onCollect(row);
                            }
                        });
                    }
                },
                {
                    title: '发件人',
                    key: 'fromPersonName',
                    className: 'y9div-pointer',
                    width: 200,
                    render(row) {
                        return h(
                            'div',
                            {
                                style: row.read == true ? '' : 'font-weight: 600;'
                            },
                            row.fromPersonName
                        );
                    }
                },
                // {
                //     title: '收件人',
                //     key: 'toPersonNames',
                //     width: 200,
                //     render(row) {
                //         return h(
                //             'div',
                //             {
                //                 style: row.read == true ? '' : 'font-weight: 600;'
                //             },
                //             row.toPersonNames
                //         );
                //     }
                // },
                {
                    title: '邮件标题',
                    key: 'subject',
                    className: 'y9div-pointer',
                    headerAlign: 'left', //表头对齐方式， 若不设置该项，则使用表格的对齐方式	string	left / center / right
                    align: 'left', //对齐方式	string	left / center / right	默认值为center
                    render(row) {
                        return h(
                            'div',
                            {
                                style: 'vertical-align: middle;' + (row.read == true ? '' : 'font-weight: 600;') + ';'
                            },
                            row.subject == null ? '(无主题)' : row.subject
                            // [
                            //     h('span',
                            //         {
                            //             style:"font-size: 10px;vertical-align: middle;color: #0000006e;"
                            //         },
                            //         "--" + (row.text == null ? '(无正文)' : row.text),
                            //     ),
                            // ]
                        );
                    }
                },
                {
                    title: '附件',
                    key: 'attachment',
                    className: 'y9div-pointer',
                    width: 70,
                    render(row) {
                        if (row.attachment == '' || row.attachment == null) {
                            return h(
                                'div',
                                {
                                    style: row.read == true ? '' : 'font-weight: 600;'
                                },
                                '-'
                            );
                        } else {
                            return h('i', {
                                class: 'ri-attachment-2',
                                style: 'font-size: 16px;vertical-align: middle;'
                            });
                        }
                    }
                },
                {
                    title: '时间',
                    key: 'sendTime',
                    width: 200,
                    render(row) {
                        return h(
                            'div',
                            {
                                style: row.read == true ? '' : 'font-weight: 600;'
                            },
                            row.createTime
                        );
                    }
                },
                {
                    title: '大小',
                    key: 'attachmentSize',
                    width: 100,
                    render(row) {
                        return h(
                            'div',
                            {
                                style: row.read == true ? '' : 'font-weight: 600;'
                            },
                            row.attachment == false ? '-' : row.attachmentSize
                        );
                    }
                }
            ],
            tableData: [],
            loading: false,
            pageConfig: {
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 15, //每页显示条目个数，支持 v-model 双向绑定
                total: 0,
                pageSizeOpts: [5, 10, 15, 20, 30, 40, 1000]
            }
        },
        filterConfig: {
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: settingStore.device === 'mobile' ? 8 : 18,
                    slotName: 'addIcon'
                },
                {
                    type: 'input',
                    key: 'name',
                    span: settingStore.device === 'mobile' ? 16 : 6
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            }
        },
        y9FormRef: '',
        searchForm: {
            // attachment: null, //是否有附件
            // read: null, //是否已读
            // flagged: null //是否星标
        } as any
    });

    // 表格选中的数据
    let tableCurrSelectedVal = ref([]);

    const { loading, currFilters, tableConfig, filterConfig, searchForm } = toRefs(data);

    function rowClick(row, column) {
        console.log(row, column);
        openEmailDetails(row);
    }

    async function getEmailList() {
        tableConfig.value.loading = true;

        let listResult = await emailList({
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            folder: 'INBOX',
            subject: currFilters.value.name
        });

        console.info(listResult.rows);
        tableConfig.value.tableData = listResult.rows;
        tableConfig.value.pageConfig.total = listResult.total;

        tableConfig.value.loading = false;
        searchForm.value.read = '';
    }

    async function searchEmailList() {
        tableConfig.value.loading = true;
        let searchResult = await searchEmail({
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            flagged: searchForm.value.flagged,
            folder: 'INBOX',
            subject: currFilters.value.name,
            read: searchForm.value.read,
            attachment: searchForm.value.attachment
        });

        console.info(searchResult.rows);
        tableConfig.value.tableData = searchResult.rows;
        tableConfig.value.pageConfig.total = searchResult.total;

        tableConfig.value.loading = false;
        searchForm.value.read = '';
        searchForm.value.attachment = '';
        searchForm.value.flagged = '';
    }

    //当前页改变时触发
    function handlerPageChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        getEmailList();
    }

    //每页条数改变时触发
    function handlerSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        getEmailList();
    }

    watch(
        () => currFilters.value,
        (newVal) => {
            if (currFilters.value.name != null) {
                searchEmailList(); //获取发件箱列表
            }
        },
        {
            deep: true,
            immediate: true
        }
    );

    //打开邮件详情
    async function openEmailDetails(row) {
        console.log('点击了列,获取到了邮件信息:');
        console.log(row);

        //  row.folder = row.folder == "收件箱" ? "INBOX" : row.folder == "发件箱" ? "Sent" :  row.folder == "草稿箱" ? "Drafts" : "collect";
        router.push({ path: '/emailDetailInfo', query: { folder: row.folder, uid: row.uid } });

        row.read = true;
    }

    //收藏,取消收藏
    async function onCollect(row) {
        console.log('点击了收藏按钮,获取到了邮件信息:');
        console.log(row);
        let result;
        result = await flagEmail(row.uid, folder.value, !row.flagged);

        // if(row.flagged){
        //     result = await unCollectEmail(row.id);
        // }else{
        //     result = await collectEmail(row.id);
        // }
        //处理完成后
        if (result.success) {
            row.flagged = !row.flagged;
            ElNotification({
                title: t('成功'), //result.success ? t('成功') : t('失败'),
                message: 'ok', //result.msg,
                type: 'success', //result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
        }
    }

    //标记为
    function flag(type) {
        if (!tableCurrSelectedVal.value.length) {
            ElNotification({
                title: t('标记失败'),
                message: t('请选择邮件'),
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        ElMessageBox.confirm(
            `${t(
                '是否将选中的邮件' +
                    (type == 'star' ? '标记为星标' : type == 'unstar' ? '取消星标' : '') +
                    (type == 'read' ? '标记为已读' : type == 'unread' ? '标记为未读' : '')
            )}?`,
            t('提示'),
            {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
            }
        )
            .then(async () => {
                loading.value = true;
                let ids: any = [];
                ids = tableCurrSelectedVal.value.map((item) => {
                    return item.uid;
                });
                console.log(ids);
                //分情况调用后台接口 let result = await removeOrgUnits(ids.join(','));
                let result;
                switch (type) {
                    case 'read':
                        result = await readEmail(ids.join(','), folder.value, true);
                        break;
                    case 'unread':
                        result = await readEmail(ids.join(','), folder.value, false);
                        break;
                    case 'star':
                        result = await flagEmail(ids.join(','), folder.value, true);
                        break;
                    case 'unstar':
                        result = await flagEmail(ids.join(','), folder.value, false);
                        break;
                }

                loading.value = false;
                if (result.success) {
                    ElNotification({
                        title: t('成功'), //result.success ? t('成功') : t('失败'),
                        message: result.msg, //result.msg,
                        type: 'success', //result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    // 初始化数据
                    getEmailList();
                    ids = [];
                    tableCurrSelectedVal.value = [];
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消标记'),
                    offset: 65
                });
            });
    }

    //移动到
    async function move(toFolder) {
        if (!tableCurrSelectedVal.value.length) {
            ElNotification({
                title: t('移动失败'),
                message: t('请选择邮件'),
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }
        let result;
        let ids: any = [];
        ids = tableCurrSelectedVal.value.map((item) => {
            return item.uid;
        });
        result = await moveToEmail(ids.join(','), folder.value, toFolder);
        console.log(result);
        getEmailList();
    }

    //查看
    function read(type) {
        if (type == '已读') {
            searchForm.value.read = true;
        }
        if (type == '未读') {
            searchForm.value.read = false;
        }
        if (type == '星标件') {
            searchForm.value.flagged = true;
        }
        if (type == '有附件') {
            searchForm.value.attachment = true;
        }
        if (type == '无附件') {
            searchForm.value.attachment = false;
        }
        searchEmailList(); //获取发件箱列表
    }

    //刷新
    function refresh(e) {
        router.go(0);
    }

    //删除
    function completelyDelete(e) {
        if (!tableCurrSelectedVal.value.length) {
            return;
        }
        ElMessageBox.confirm(`${t('是否删除选中的邮件')}?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                let ids: any = [];
                ids = tableCurrSelectedVal.value.map((item) => {
                    return item.uid;
                });
                console.log(ids);
                //调用后台接口 let result = await removeOrgUnits(ids.join(','));
                let result = await deleteEmail(folder.value, ids.join(','));
                loading.value = false;
                if (result.success) {
                    ElNotification({
                        title: t('成功'), //result.success ? t('成功') : t('失败'),
                        message: result.msg, //result.msg,
                        type: 'success', //result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    // 初始化数据
                    getEmailList();
                    ids = [];
                    tableCurrSelectedVal.value = [];
                }
            })
            .catch(() => {
                ElMessage({
                    type: 'info',
                    message: t('已取消删除'),
                    offset: 65
                });
            });
    }

    onMounted(() => {
        // 收件列表
        getEmailList();
    });
</script>
<style lang="scss" scoped>
    .margin-left-12 {
        margin-left: 12px;
    }

    :deep(.el-table__cell) {
        cursor: default;
    }

    // 分页的文字样式
    .table-style {
        :deep(.y9-pagination) {
            width: 94%;
            position: absolute;
            bottom: 14px;
        }
    }
</style>
