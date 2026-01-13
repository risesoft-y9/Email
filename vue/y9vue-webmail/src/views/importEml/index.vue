<!--
 * @Author:  mengjuhua
 * @Date: 2022-08-02 10:51:50
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-23 17:16:29
 * @Description: 历史邮件
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
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="onActions('uploadEml', '上传邮件信息')"
            >
                <i class="ri-file-upload-line"></i>
                <span>{{ $t('导入') }}</span>
            </el-button>
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

    <!-- 弹窗 -->
    <y9Dialog v-model:config="dialogConfig">
        <uploadEml v-if="dialogConfig.type == 'uploadEml'" @update="reloaTable"></uploadEml>
    </y9Dialog>
</template>

<script lang="ts" setup>
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import router from '@/router';
    import { deleteEml, searchEml } from '@/api/importEml/index';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $deeploneObject } from '@/utils/object';
    import uploadEml from './dialogContent/uploadEml.vue';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo') || {};
    const { t } = useI18n();

    const settingStore = useSettingStore();

    const data = reactive({
        currFilters: {} as any, //当前选择的过滤数据
        loading: false, // 全局loading
        tableConfig: {
            //表格配置
            border: false,
            headerBackground: true,
            openAutoComputedHeight: false,
            columns: [
                {
                    type: 'selection',
                    width: 60
                },
                {
                    title: '发件人',
                    key: 'from',
                    width: 300,
                    align: 'left',
                    render(row) {
                        return h(
                            'div',
                            {
                                style: row.read == true ? 'cursor: pointer;' : 'font-weight: 600;cursor: pointer;'
                            },
                            row.from
                        );
                    }
                },
                {
                    title: '邮件标题',
                    key: 'subject',
                    headerAlign: 'left', //表头对齐方式， 若不设置该项，则使用表格的对齐方式	string	left / center / right
                    align: 'left', //对齐方式	string	left / center / right	默认值为center
                    render(row) {
                        let text =
                            row.textContent == null || row.textContent == ''
                                ? ''
                                : '--' + row.textContent.replace(new RegExp('&nbsp;', 'gm'), '').substr(0, 60) + '...';

                        return h(
                            'div',
                            {
                                style: 'vertical-align: middle;' + (row.read == true ? '' : 'font-weight: 600;') + ';'
                            },
                            [
                                row.subject == null ? '(无主题)' : row.subject,
                                h(
                                    'span',
                                    {
                                        style: 'font-size: 10px;vertical-align: middle;color: #0000006e;margin-left:5px;'
                                    },
                                    text
                                )
                            ]
                        );
                    }
                },
                {
                    title: '附件',
                    key: 'existAttchMent',
                    width: 60,
                    render(row) {
                        if (row.existAttchMent == '' || row.existAttchMent == null) {
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
                    key: 'dateTime',
                    width: 200,
                    render(row) {
                        return h(
                            'div',
                            {
                                style: row.read == true ? '' : 'font-weight: 600;'
                            },
                            row.dateTime
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
        read: '',
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            type: '',
            columns: [],
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let result = { success: false, msg: '' };
                    if (newConfig.value.type == 'sort') {
                    }
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        resolve();
                    } else {
                        reject();
                    }
                });
            }
        }
    });

    // 表格选中的数据
    let tableCurrSelectedVal = ref([]);

    const { loading, currFilters, tableConfig, filterConfig, y9FormRef, dialogConfig } = toRefs(data);

    //操作按钮
    async function onActions(type, title?) {
        if (type == 'edit') {
        } else if (type == 'sync' || type == 'extendAttr' || type == 'sort' || type == 'uploadEml') {
            //点击按钮显示弹窗
            // 深克隆对象
            let configInfo = $deeploneObject(dialogConfig.value);
            Object.assign(configInfo, {
                show: true,
                title: computed(() => t(`${title}`)),
                okText: type == 'extendAttr' || type == 'addPerson' ? false : computed(() => t('保存')),
                cancelText: type == 'sync' || type == 'extendAttr' ? false : computed(() => t('关闭')),
                width: type == 'move' || type == 'sync' || type == 'uploadEml' ? '30%' : '60%',
                type: type,
                showFooter: type == 'uploadEml' ? false : true
            });

            // 赋值
            dialogConfig.value = configInfo;
        }
    }

    function rowClick(row, column) {
        console.log(row, column);
        openEmailDetails(row);
    }

    async function getEmailList() {
        tableConfig.value.loading = true;

        let listResult = await searchEml({
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize
        });

        tableConfig.value.tableData = listResult.rows;
        tableConfig.value.pageConfig.total = listResult.total;
        tableConfig.value.loading = false;
    }

    async function searchEmailList() {
        tableConfig.value.loading = true;
        let searchResult = await searchEml({
            page: tableConfig.value.pageConfig.currentPage,
            size: tableConfig.value.pageConfig.pageSize,
            subject: currFilters.value.name,
            text: currFilters.value.name
        });

        console.info(searchResult.rows);
        tableConfig.value.tableData = searchResult.rows;
        tableConfig.value.pageConfig.total = searchResult.total;
        tableConfig.value.loading = false;
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
        router.push({ path: '/emlDetail', query: { eid: row.id } });
        row.read = true;
    }

    //刷新
    function refresh(e) {
        searchEmailList();
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
                let ids = [] as any;
                ids = tableCurrSelectedVal.value.map((item) => {
                    return item.id;
                });
                console.log(ids);
                //调用后台接口 let result = await removeOrgUnits(ids.join(','));
                let result = await deleteEml(ids.join(','));
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

    function reloaTable() {
        getEmailList();
        dialogConfig.value.show = false;
    }
</script>
<style lang="scss" scoped>
    .margin-left-12 {
        margin-left: 12px;
    }
</style>
