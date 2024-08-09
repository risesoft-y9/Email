<template>
    <div class="table-style">
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
                    <el-button class="global-btn-main" type="primary" :size="fontSizeObj.buttonSize"
                               :style="{ fontSize: fontSizeObj.baseFontSize }">
                        <i class="ri-drag-move-line"></i>{{ $t('移动到') }}
                    </el-button>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item
                                v-for="item in folderStore.getAllFolders"
                                :key="item.name"
                                @click="move(item.name)"
                            >
                                {{ item.title }}
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
                <el-dropdown>
                    <el-button class="global-btn-third margin-left-12" :size="fontSizeObj.buttonSize"
                               :style="{ fontSize: fontSizeObj.baseFontSize }">
                        <i class="ri-pencil-ruler-2-line"></i>{{ $t('标记为') }}
                    </el-button>

                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="flag('read')">已读</el-dropdown-item>
                            <el-dropdown-item @click="flag('unread')">未读</el-dropdown-item>
                            <el-dropdown-item @click="flag('star')">星标</el-dropdown-item>
                            <el-dropdown-item @click="flag('unstar')">取消星标</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>

                <el-dropdown>
                    <el-button class="global-btn-third margin-left-12" :size="fontSizeObj.buttonSize"
                               :style="{ fontSize: fontSizeObj.baseFontSize }">
                        <i class="ri-list-check"></i>{{ $t('查看') }}
                    </el-button>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="read('')">全部</el-dropdown-item>
                            <el-dropdown-item @click="read('已读')">已读</el-dropdown-item>
                            <el-dropdown-item @click="read('未读')">未读</el-dropdown-item>
                            <el-dropdown-item @click="read('星标件')">星标件</el-dropdown-item>
                            <el-dropdown-item @click="read('有附件')">有附件</el-dropdown-item>
                            <el-dropdown-item @click="read('无附件')">无附件</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
                <el-button class="global-btn-third margin-left-12" @click="refresh" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-refresh-line"></i>
                    <span>{{ $t('刷新') }}</span>
                </el-button>
                <el-button class="global-btn-third margin-left-12" @click="completelyDelete" :size="fontSizeObj.buttonSize"
                           :style="{ fontSize: fontSizeObj.baseFontSize }">
                    <i class="ri-delete-bin-line"></i>
                    <span>{{ $t('删除') }}</span>
                </el-button>
            </template>
        </y9Table>
    </div>
</template>

<script lang="ts" setup>
    import { onMounted, reactive, ref, toRefs, watch } from 'vue';
    import router from '@/router';
    import { deleteEmail, emailList, flagEmail, moveToEmail, readEmail, searchEmail } from '@/api/email/index';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useFolderStore } from '@/store/modules/folderStore';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo')||{};
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
                    width: 60,
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
                    width: 60,
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
                    headerAlign: 'center', //表头对齐方式， 若不设置该项，则使用表格的对齐方式	string	left / center / right
                    align: 'center', //对齐方式	string	left / center / right	默认值为center
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
                    width: 60,
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
            pageConfig: {
                currentPage: 1, //当前页数，支持 v-model 双向绑定
                pageSize: 15, //每页显示条目个数，支持 v-model 双向绑定
                total: 0,
                pageSizeOpts: [5,10,15,20,30,40,1000]
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
        read: ''
    });

    // 表格选中的数据
    let tableCurrSelectedVal = ref([]);

    const { loading, currFilters, tableConfig, filterConfig, y9FormRef } = toRefs(data);

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
        tableConfig.value.read = '';
    }

    async function searchEmailList() {
        tableConfig.value.loading = true;
        let searchResult = await searchEmail({
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            flagged: tableConfig.value.flagged,
            folder: 'INBOX',
            subject: currFilters.value.name,
            read: tableConfig.value.read,
            attachment: tableConfig.value.attachment
        });

        console.info(searchResult.rows);
        tableConfig.value.tableData = searchResult.rows;
        tableConfig.value.pageConfig.total = searchResult.total;

        tableConfig.value.loading = false;
        tableConfig.value.read = '';
        tableConfig.value.attachment = '';
        tableConfig.value.flagged = '';
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
            return;
        }
        ElMessageBox.confirm(
            `${t('是否将选中的邮件' + (type == 'star' ? '标记为星标' : type == 'unstar' ? '取消星标' : '') + (type == 'read' ? '标记为已读' : type == 'unread' ? '标记为未读' : ''))}?`,
            t('提示'),
            {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
            }
        )
            .then(async () => {
                loading.value = true;
                let ids = [];
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
        let result;
        let ids = [];
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
            tableConfig.value.read = true;
        }
        if (type == '未读') {
            tableConfig.value.read = false;
        }
        if (type == '星标件') {
            tableConfig.value.flagged = true;
        }
        if (type == '有附件') {
            tableConfig.value.attachment = true;
        }
        if (type == '无附件') {
            tableConfig.value.attachment = false;
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
                let ids = [];
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
<style scoped lang="scss">
    :deep(.custom-picture-card) {
        .el-upload-list__item {
            width: 360px;
            height: 260px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    }

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
    :deep(.y9-table-header){
      font-size: v-bind('fontSizeObj.baseFontSize');
    }
    :deep(.el-table__row){
      font-size: v-bind('fontSizeObj.baseFontSize');
    }
    :deep(.el-input__inner){
      font-size: v-bind('fontSizeObj.baseFontSize');
    }
    :deep(.el-date-editor .el-range-input){
      font-size: v-bind('fontSizeObj.baseFontSize');
    }
    :deep(.el-table__empty-text){
      font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
<style>
    .el-message-box__status.el-message-box-icon--info {
        --el-messagebox-color: var(--el-color-primary);
        color: var(--el-messagebox-color);
    }

    .el-message .el-message-icon--info {
        color: var(--el-color-primary);
    }

    .el-message--info {
        --el-message-bg-color: var(--el-fill-color-blank);
        --el-message-text-color: var(--el-color-primary);
        --el-message-border-color: var(--el-fill-color-blank);
    }

    .el-message-box__headerbtn .el-message-box__close {
        color: var(--el-color-primary);
    }
</style>
