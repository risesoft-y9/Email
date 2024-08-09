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
            </template>
        </y9Table>
    </div>
</template>

<script lang="ts" setup>
    import { onMounted, reactive, ref, toRefs } from 'vue';
    import router from '@/router';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { flagEmail, searchEmail } from '@/api/email/index';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo')||{};
    const { t } = useI18n();

    const settingStore = useSettingStore();
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
                    width: 120,
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
                {
                    title: '收件人',
                    key: 'toPersonNames',
                    width: 200,
                    render(row) {
                        return h(
                            'div',
                            {
                                style: row.read == true ? '' : 'font-weight: 600;'
                            },
                            row.toPersonNames
                        );
                    }
                },
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
                    key: 'createTime',
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
        read: '',
        attachment: ''
    });

    async function collectList() {
        tableConfig.value.loading = true;
        debugger;
        let collectList = await searchEmail({
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            flagged: tableConfig.value.flagged,
            subject: currFilters.value.name,
            read: tableConfig.value.read,
            attachment: tableConfig.value.attachment
        });

        console.info(collectList.limit);
        tableConfig.value.tableData = collectList.rows;
        tableConfig.value.pageConfig.total = collectList.total;

        tableConfig.value.loading = false;
        tableConfig.value.read = '';
        tableConfig.value.attachment = '';
        tableConfig.value.flagged = '';
    }

    //当前页改变时触发
    function handlerPageChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        collectList();
    }

    //每页条数改变时触发
    function handlerSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        collectList();
    }

    // 表格选中的数据
    let tableCurrSelectedVal = ref([]);
    let { loading, currFilters, tableConfig, filterConfig, y9FormRef } = toRefs(data);

    //打开邮件详情
    function openEmailDetails(row) {
        console.log('点击了列,获取到了邮件信息:');
        console.log(row);

        router.push({ path: '/emailDetailInfo', query: { folder: row.folder, uid: row.uid } });
        row.read = true;
    }

    function rowClick(row, column) {
        openEmailDetails(row);
    }

    //收藏,取消收藏
    async function onCollect(row) {
        console.log('点击了收藏按钮,获取到了邮件信息:');
        console.log(row);
        let folder = row.folder;
        let result;
        result = await flagEmail(row.uid, folder, !row.flagged);

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

    watch(
        () => currFilters.value,
        (newVal) => {
            if (currFilters.value.name != null) {
                collectList(); //获取发件箱列表
            }
        },
        {
            deep: true,
            immediate: true
        }
    );

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
        collectList(); //获取收藏夹列表
    }

    //刷新
    function refresh(e) {
        router.go(0);
    }

    onMounted(() => {
        // 收件列表
        collectList();
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
