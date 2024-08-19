<!--
 * @Author: your name
 * @Date: 2022-05-05 09:43:05
 * @LastEditTime: 2024-08-19 15:22:49
 * @LastEditors: mengjuhua
 * @Description: 邮件查询
-->
<template>
    <div class="userLog">
        <y9Card :showHeader="false">
            <y9Table
                ref="filterRef"
                :config="tableConfig"
                :filter-config="filterLogsConfig"
                border
                @on-curr-page-change="handlerPageChange"
                @on-page-size-change="handlerSizeChange"
                @row-click="rowClick"
                @window-height-change="windowHeightChange"
            >
                <template #slotDate>
                    <el-form>
                        <el-form-item :label="$t('起止时间')">
                            <el-date-picker
                                v-model="selectedDate"
                                :end-placeholder="$t('结束时间')"
                                :range-separator="$t('至')"
                                :shortcuts="shortcuts"
                                :start-placeholder="$t('开始时间')"
                                format="YYYY-MM-DD"
                                style="width: 100%"
                                type="datetimerange"
                                value-format="YYYY-MM-DD"
                                @change="selectdData()"
                            >
                            </el-date-picker>
                        </el-form-item>
                    </el-form>
                </template>
                <template #slotSearch>
                    <el-divider content-position="center">
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-main"
                            type="primary"
                            @click="search()"
                            >{{ $t('查询') }}
                        </el-button>
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="reset()"
                            >{{ $t('重置') }}
                        </el-button>
                    </el-divider>
                </template>
            </y9Table>
        </y9Card>
    </div>
</template>

<script lang="ts" setup>
    import { h, inject, onMounted, ref } from 'vue';
    import router from '@/router';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useI18n } from 'vue-i18n';
    import { flagEmail, searchEmail } from '@/api/email';
    import { useFolderStore } from '@/store/modules/folderStore';
    import { ElNotification } from 'element-plus';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const { t } = useI18n();
    const settingStore = useSettingStore();
    const filterRef = ref('');

    const selectedDate = ref([]);
    const shortcuts = [
        {
            text: t('最近一周'),
            value: () => {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                return [start, end];
            }
        },
        {
            text: t('最近一个月'),
            value: () => {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                return [start, end];
            }
        },
        {
            text: t('最近三个月'),
            value: () => {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                return [start, end];
            }
        }
    ];
    const selectdData = () => {
        currFilters.value.startTime = selectedDate.value[0];
        currFilters.value.endTime = selectedDate.value[1];
    };

    const currFilters = ref({
        subject: '', //主题
        textBody: '', //正文
        state: '', //状态
        accessory: '', //附件名
        folder: '', //文件夹
        addresser: '', //发件人
        recipients: '', //收件人
        startTime: '',
        endTime: ''
    });

    const filterLogsConfig = ref({
        filtersValueCallBack: (filter) => {
            currFilters.value = filter;
        },
        itemList: [
            {
                type: 'input',
                value: '',
                key: 'subject',
                label: '邮件主题',
                labelWidth: '72px',
                props: {
                    placeholder: '邮件主题'
                },
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'input',
                value: '',
                key: 'textBody',
                labelWidth: '72px',
                label: '邮件正文',
                props: {
                    placeholder: '邮件正文'
                },
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'select',
                value: '',
                key: 'state',
                label: '状态',
                labelWidth: '72px',
                props: {
                    options: [
                        //选项列表
                        {
                            label: '全部',
                            value: ''
                        },
                        {
                            label: '已读',
                            value: true
                        },
                        {
                            label: '未读',
                            value: false
                        }
                    ],
                    placeholder: '请选择'
                },
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'select',
                value: '',
                key: 'accessory',
                label: '附件',
                labelWidth: '72px',
                props: {
                    options: [
                        //选项列表
                        {
                            label: '全部',
                            value: ''
                        },
                        {
                            label: '有附件',
                            value: true
                        },
                        {
                            label: '无附件',
                            value: false
                        }
                    ],
                    placeholder: '请选择'
                },
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'select',
                value: '',
                key: 'folder',
                label: '文件夹',
                labelWidth: '72px',
                props: {
                    options: [],
                    placeholder: '请选择'
                },
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'input',
                value: '',
                key: 'addresser',
                label: '发件人',
                labelWidth: '72px',
                props: {
                    placeholder: '发件人'
                },
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'input',
                value: '',
                key: 'recipients',
                label: '收件人',
                labelWidth: '72px',
                props: {
                    placeholder: '收件人'
                },
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'slot',
                slotName: 'slotDate',
                span: settingStore.device === 'mobile' ? 24 : 6
            },
            {
                type: 'slot',
                slotName: 'slotSearch',
                span: 24
            }
        ],
        showBorder: true
    });

    function rowClick(row) {
        openEmailDetails(row);
    }

    //打开邮件详情
    function openEmailDetails(row) {
        console.log('点击了列,获取到了邮件信息:');
        console.log(row);

        router.push({ path: '/emailDetailInfo', query: { folder: row.folder, uid: row.uid } });
    }

    //收藏,取消收藏
    async function onCollect(row) {
        console.log('点击了收藏按钮,获取到了邮件信息:');
        console.log(row);
        let result;
        let folder = row.folder;
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

    window.onresize = () => {
        return (() => {})();
    };

    //窗口变动时触发，获取表格的高度
    function windowHeightChange(tableHeight) {
        tableConfig.value.height = tableHeight - 35 - 35 - 2; //35 35 是y9-card-content样式中上padding、下padding的值
        tableConfig.value.maxHeight = tableHeight - 35 - 35 - 2; //35 35 是y9-card-content样式中上padding、下padding的值
    }

    // 表格 配置
    let tableConfig = ref({
        loading: false,
        headerBackground: false,
        columns: [
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
                className: 'y9div-pointer',
                width: 200,
                render(row) {
                    return h(
                        'div',
                        {
                            style: row.read == true ? '' : 'font-weight: 600;'
                        },
                        row.toPersonNames == '' ? '<无收件人>' : row.toPersonNames
                    );
                }
            },
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
                width: 180,
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
            },
            {
                title: '所属文件夹',
                key: 'folderStr',
                width: 120,
                render(row) {
                    return h(
                        'div',
                        {
                            style: row.read == true ? '' : 'font-weight: 600;'
                        },
                        row.folderStr
                    );
                }
            }
        ],
        tableData: [],
        pageConfig: {
            currentPage: 1, //当前页数，支持 v-model 双向绑定
            pageSize: 20, //每页显示条目个数，支持 v-model 双向绑定
            total: 0 //总条目数
        },
        border: false
    });

    const refreshTable = () => {
        tableConfig.value.tableData = [];
        tableConfig.value.pageConfig.total = 0;
        tableConfig.value.pageConfig.currentPage = 1;
        tableConfig.value.pageConfig.pageSize = 20;
    };

    const pageInfo = ref({
        folder: '',
        subject: '',
        sendOrgUnitName: '',
        receiveOrgUnitName: '',
        text: '',
        read: '',
        attachmentName: '',
        startDate: '',
        endDate: ''
    });

    const search = () => {
        pageInfo.value.folder = currFilters.value.folder;
        pageInfo.value.subject = currFilters.value.subject;
        pageInfo.value.sendOrgUnitName = currFilters.value.addresser;
        pageInfo.value.receiveOrgUnitName = currFilters.value.recipients;
        pageInfo.value.text = currFilters.value.textBody;
        pageInfo.value.read = currFilters.value.state;
        pageInfo.value.attachmentName = currFilters.value.accessory;
        pageInfo.value.startDate = currFilters.value.startTime;
        pageInfo.value.endDate = currFilters.value.endTime;
        tableConfig.value.pageConfig.currentPage = 1;

        getListEmail();
    };

    const reset = () => {
        pageInfo.value.folder = '';
        pageInfo.value.subject = '';
        pageInfo.value.sendOrgUnitName = '';
        pageInfo.value.receiveOrgUnitName = '';
        pageInfo.value.text = '';
        pageInfo.value.read = '';
        pageInfo.value.attachmentName = '';
        pageInfo.value.startDate = '';
        pageInfo.value.endDate = '';
        currFilters.value.startTime = '';
        currFilters.value.endTime = '';
        filterRef.value.y9FilterRef.onReset();
        selectedDate.value = [];
        refreshTable();
        getListEmail();
    };

    //当前页改变时触发
    function handlerPageChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        getListEmail();
    }

    //每页条数改变时触发
    function handlerSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        getListEmail();
    }

    //分页查询邮件
    async function getListEmail() {
        tableConfig.value.loading = true;
        let getListEmail = await searchEmail({
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            folder: pageInfo.value.folder,
            subject: pageInfo.value.subject,
            text: pageInfo.value.text,
            read: pageInfo.value.read,
            startDate: pageInfo.value.startDate,
            endDate: pageInfo.value.endDate,
            attachment: pageInfo.value.attachmentName,
            from: pageInfo.value.sendOrgUnitName,
            to: pageInfo.value.receiveOrgUnitName
        });
        tableConfig.value.tableData = getListEmail.rows;
        tableConfig.value.pageConfig.total = getListEmail.total;
        tableConfig.value.loading = false;
    }

    onMounted(async () => {
        let index = filterLogsConfig.value.itemList.findIndex((item) => item.key == 'folder' && item.type == 'select');

        let folders = [{ label: '全部', value: '' }];
        let allFolders = useFolderStore().getAllFolders;
        allFolders.map((item) => {
            folders.push({ label: item.title, value: item.name });
        });
        filterLogsConfig.value.itemList[index].props.options = folders;
        // for (let i = 0; i < customFolder.length; i++) {
        //     filterLogsConfig.value.itemList[index].props.options.push(customFolder[i])
        // }

        // 收件列表
        getListEmail();
    });
</script>
<style lang="scss" scoped>
    .el-card {
        display: flex;
        width: 100%;
        // margin-top: 30px;
    }

    :deep(.el-form-item__label) {
        text-align: justify !important;
    }

    :deep(.el-divider__text) {
        display: flex;
        background-color: transparent;
    }

    :deep(.el-form-item__label) {
        width: 82px;
        text-align: left;
        margin-right: 10px;
    }

    :deep(.el-date-range-picker__time-header) {
        display: none;
    }

    :deep(.el-form-item__content) {
        align-items: stretch;
    }

    :deep(.el-table__header-wrapper) {
        border-top: 1px solid #eee;
    }

    :deep(.y9-filter .y9-filter-item .label) {
        padding: 0 0 0 15px;
    }

    :deep(.el-input__inner) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    :deep(.el-date-editor .el-range-input) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }

    .userLog {
        width: auto;
        margin: 0 auto;

        .pagination-style {
            display: flex;
            justify-content: v-bind("settingStore.device === 'mobile'? 'flex-start':'flex-end'");
            margin-top: 20px;
            overflow: auto;
        }

        :deep(.y9-card-content) {
            padding: 35px 20px;

            .el-divider--horizontal {
                margin: 20px 0 30px 0;
            }
        }
    }

    :deep(.el-divider__text) {
        display: flex;
    }

    :deep(.el-form-item__label) {
        width: 90px;
        display: flex;
        justify-content: flex-start;
        font-size: v-bind('fontSizeObj.baseFontSize');
        align-items: center;
        line-height: 18px;
    }

    :deep(.el-date-range-picker__time-header) {
        display: none;
    }

    :deep(.el-form-item__content) {
        align-items: stretch;
    }

    :deep(.el-form-item) {
        margin: 0;
    }

    :deep(.el-date-editor) {
        i,
        input {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }

    :deep(.el-select__wrapper) {
        border-radius: 30px;
    }
</style>
